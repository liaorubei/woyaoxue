package com.newclass.woyaoxue.activity;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;

import com.google.gson.Gson;
import com.netease.media.a;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.Observer;
import com.netease.nimlib.sdk.RequestCallback;
import com.netease.nimlib.sdk.avchat.AVChatCallback;
import com.netease.nimlib.sdk.avchat.AVChatManager;
import com.netease.nimlib.sdk.avchat.constant.AVChatTimeOutEvent;
import com.netease.nimlib.sdk.avchat.constant.AVChatType;
import com.netease.nimlib.sdk.avchat.model.AVChatCalleeAckEvent;
import com.netease.nimlib.sdk.avchat.model.AVChatCommonEvent;
import com.netease.nimlib.sdk.avchat.model.AVChatData;
import com.netease.nimlib.sdk.msg.MsgService;
import com.netease.nimlib.sdk.msg.constant.SessionTypeEnum;
import com.netease.nimlib.sdk.msg.model.CustomNotification;
import com.newclass.woyaoxue.base.BaseAdapter;
import com.newclass.woyaoxue.bean.NimSysNotice;
import com.newclass.woyaoxue.util.CommonUtil;
import com.newclass.woyaoxue.util.Log;
import com.newclass.woyaoxue.util.Rotate3dAnimation;
import com.voc.woyaoxue.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager.LayoutParams;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * 电话拨打界面
 * @author liaorubei
 *
 */
public class CallActivity extends Activity implements OnClickListener
{
	public static final String KEY_TARGET = "TARGET";
	public static final String CALL_TYPE_KEY = "CALL_TYPE_KEY";
	public static final int CALL_TYPE_AUDIO = 1;
	public static final int CALL_TYPE_VIDEO = 2;
	public static final String KEY_NICKNAME = "KEY_NICKNAME";
	protected static final String TAG = "CallActivity";
	private String accid;
	private Button bt_hangup, bt_mute, bt_free, bt_face, bt_text, bt_card, bt_more;
	private ImageView iv_icon;
	private TextView tv_nickname;
	private Chronometer cm_time;
	private AVChatCallback<Void> avChatCallback;
	private AlertDialog cardDialog;
	private String target;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_call_teacher);

		accid = getSharedPreferences("user", MODE_PRIVATE).getString("accid", "");
		initView();
		initData();
	}

	private void initData()
	{
		Intent intent = getIntent();
		target = intent.getStringExtra(KEY_TARGET);
		String nickname = intent.getStringExtra(KEY_NICKNAME);

		tv_nickname.setText(nickname);

		// 呼叫拨出
		AVChatManager.getInstance().call(target, AVChatType.AUDIO, null, new AVChatCallback<AVChatData>()
		{

			@Override
			public void onSuccess(AVChatData avChatData)
			{
				cm_time.start();
			}

			@Override
			public void onFailed(int arg0)
			{}

			@Override
			public void onException(Throwable arg0)
			{}
		});

		registerObserver();
	}

	private void registerObserver()
	{
		// 监听网络通话被叫方的响应（接听、拒绝、忙）
		AVChatManager.getInstance().observeCalleeAckNotification(new Observer<AVChatCalleeAckEvent>()
		{
			private static final long serialVersionUID = 1L;

			@Override
			public void onEvent(AVChatCalleeAckEvent ackEvent)
			{
				switch (ackEvent.getEvent())
				{
				case CALLEE_ACK_AGREE:// 被叫方同意接听
					if (ackEvent.isDeviceReady())
					{
						CommonUtil.toast("设备正常,开始通话");
						cm_time.setBase(SystemClock.elapsedRealtime());
					}
					else
					{
						CommonUtil.toast("设备异常,无法通话");
						finish();
					}
					break;
				case CALLEE_ACK_REJECT:
					CommonUtil.toast("对方拒绝接听");
					finish();
					break;

				case CALLEE_ACK_BUSY:
					CommonUtil.toast("对方忙");
					finish();
					break;
				default:
					break;
				}
			}
		}, true);

		// 监听网络通话对方挂断的通知,即在正常通话时,结束通话
		AVChatManager.getInstance().observeHangUpNotification(new Observer<AVChatCommonEvent>()
		{
			private static final long serialVersionUID = 1L;

			@Override
			public void onEvent(AVChatCommonEvent event)
			{
				Log.i("logi", "对方已挂断");
				finish();
			}
		}, true);

		// 监听呼叫或接听超时通知
		// 主叫方在拨打网络通话时，超过 45 秒被叫方还未接听来电，则自动挂断。
		// 被叫方超过 45 秒未接听来听，也会自动挂断
		// 在通话过程中网络超时 30 秒自动挂断。
		AVChatManager.getInstance().observeTimeoutNotification(new Observer<AVChatTimeOutEvent>()
		{
			private static final long serialVersionUID = 1L;

			@Override
			public void onEvent(AVChatTimeOutEvent timeOutEvent)
			{
				CommonUtil.toast("超时");
				finish();
			}
		}, true);
	}

	private void initView()
	{
		bt_hangup = (Button) findViewById(R.id.bt_hangup);
		bt_mute = (Button) findViewById(R.id.bt_mute);
		bt_free = (Button) findViewById(R.id.bt_free);
		bt_face = (Button) findViewById(R.id.bt_face);
		bt_text = (Button) findViewById(R.id.bt_text);
		bt_card = (Button) findViewById(R.id.bt_card);
		bt_more = (Button) findViewById(R.id.bt_more);

		tv_nickname = (TextView) findViewById(R.id.tv_nickname);
		cm_time = (Chronometer) findViewById(R.id.cm_time);
		cm_time.stop();

		bt_hangup.setOnClickListener(this);
		bt_mute.setOnClickListener(this);
		bt_free.setOnClickListener(this);
		bt_face.setOnClickListener(this);
		bt_text.setOnClickListener(this);
		bt_card.setOnClickListener(this);
		bt_more.setOnClickListener(this);

	}

	@Override
	public void onClick(View v)
	{
		switch (v.getId())
		{
		case R.id.bt_hangup:
			hangup();
			break;

		case R.id.bt_mute:
		{
			if (AVChatManager.getInstance().isMute())
			{
				// isMute是否处于静音状态
				// 关闭音频
				AVChatManager.getInstance().setMute(false);
			}
			else
			{
				// 打开音频
				AVChatManager.getInstance().setMute(true);
			}
			bt_mute.setText(AVChatManager.getInstance().isMute() ? "目前静音" : "目前不静音");
		}
			break;
		case R.id.bt_free:
		{
			// 设置扬声器是否开启
			AVChatManager.getInstance().setSpeaker(!AVChatManager.getInstance().speakerEnabled());
			bt_free.setText(AVChatManager.getInstance().speakerEnabled() ? "目前外放" : "目前耳机");
		}
			break;

		case R.id.bt_card:
			if (cardDialog == null)
			{
				createCardDialog();
			}
			cardDialog.show();

			break;
		default:
			break;
		}
	}

	private void createCardDialog()
	{
		DisplayMetrics outMetrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(outMetrics);

		Builder builder = new AlertDialog.Builder(CallActivity.this);
		View dialogView = View.inflate(getApplication(), R.layout.dialog_card, null);
		GridView gv_card = (GridView) dialogView.findViewById(R.id.gv_card);
		List<String> list = new ArrayList<String>();
		for (int i = 0; i < 5; i++)
		{
			list.add(i + "号主题");
		}
		MyAdapter adapter = new MyAdapter(list);
		gv_card.setAdapter(adapter);
		builder.setView(dialogView);
		cardDialog = builder.create();

		cardDialog.setCanceledOnTouchOutside(false);
		LayoutParams attributes2 = getWindow().getAttributes();
		attributes2.height = outMetrics.heightPixels / 2;
		// cardDialog.getWindow().setAttributes(attributes2);

	}

	private void hangup()
	{
		if (avChatCallback == null)
		{
			avChatCallback = new AVChatCallback<Void>()
			{

				@Override
				public void onSuccess(Void arg0)
				{
					Log.i("logi", "callactivity hangUp onSuccess");
					finish();
				}

				@Override
				public void onFailed(int arg0)
				{
					Log.i("logi", "callactivity hangUp onFailed:" + arg0);
					finish();
				}

				@Override
				public void onException(Throwable arg0)
				{
					Log.i("logi", "callactivity hangUp onException:" + arg0.getMessage());
					finish();
				}
			};
		}
		AVChatManager.getInstance().hangUp(avChatCallback);
	}

	protected RequestCallback<Void> sendcallBack = new RequestCallback<Void>()
	{

		@Override
		public void onSuccess(Void arg0)
		{
			Log.i(TAG, "sendcallBack" + "onSuccess");
		}

		@Override
		public void onFailed(int arg0)
		{
			Log.i(TAG, "sendcallBack" + "onFailed" + arg0);
		}

		@Override
		public void onException(Throwable arg0)
		{
			Log.i(TAG, "sendcallBack" + "onException:" + arg0.getMessage());
		}
	};

	private class MyAdapter extends BaseAdapter<String>
	{

		public MyAdapter(List<String> list)
		{
			super(list);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent)
		{
			final String item = getItem(position);
			View inflate = View.inflate(getApplication(), R.layout.griditem_card, null);
			inflate.findViewById(R.id.iv_card).setVisibility(View.VISIBLE);
			TextView tv_theme = (TextView) inflate.findViewById(R.id.tv_theme);
			tv_theme.setText(item);
			inflate.setOnClickListener(new OnClickListener()
			{

				@Override
				public void onClick(View v)
				{
					v.findViewById(R.id.iv_card).setVisibility(View.INVISIBLE);

					// 构造自定义通知，指定接收者
					NimSysNotice<String> notice = new NimSysNotice<String>();
					notice.NoticeType = NimSysNotice.NoticeType_Card;
					notice.info = item;

					CustomNotification notification = new CustomNotification();
					notification.setFromAccount(accid);
					notification.setSessionId(target);
					notification.setSessionType(SessionTypeEnum.P2P);
					notification.setSendToOnlineUserOnly(true);
					notification.setContent(new Gson().toJson(notice));

					// 发送自定义通知
					NIMClient.getService(MsgService.class).sendCustomNotification(notification).setCallback(sendcallBack);;
				}
			});

			return inflate;
		}
	}

}
