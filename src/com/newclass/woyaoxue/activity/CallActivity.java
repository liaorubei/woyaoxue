package com.newclass.woyaoxue.activity;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.client.methods.HttpPost;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
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
import com.newclass.woyaoxue.bean.CallLog;
import com.newclass.woyaoxue.bean.NimSysNotice;
import com.newclass.woyaoxue.bean.Response;
import com.newclass.woyaoxue.bean.Theme;
import com.newclass.woyaoxue.bean.User;
import com.newclass.woyaoxue.util.CommonUtil;
import com.newclass.woyaoxue.util.HttpUtil;
import com.newclass.woyaoxue.util.HttpUtil.Parameters;
import com.newclass.woyaoxue.util.Log;
import com.newclass.woyaoxue.util.NetworkUtil;
import com.voc.woyaoxue.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager.LayoutParams;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * 电话拨打界面
 * @author liaorubei
 *
 */
public class CallActivity extends Activity implements OnClickListener
{
	public static final int CALL_TYPE_AUDIO = 1;
	public static final String CALL_TYPE_KEY = "CALL_TYPE_KEY";
	public static final int CALL_TYPE_VIDEO = 2;
	public static final String KEY_TARGET_NICKNAME = "KEY_TARGET_NICKNAME";
	public static final String KEY_TARGET_ACCID = "KEY_TARGET_ACCID";
	public static final String KEY_TARGET_ID = "KEY_TARGET_ID";
	public static final String TAG = "CallActivity";

	private AVChatCallback<Void> callback_hangup;
	private AVChatCallback<AVChatData> callback_call;
	private Button bt_hangup, bt_mute, bt_free, bt_face, bt_text, bt_card, bt_more;

	private MyAdapter cardAdapter;
	private AlertDialog cardDialog;
	private List<Theme> cardList;
	private Chronometer cm_time;
	private Gson gson = new Gson();
	private ImageView iv_icon;

	private TextView tv_nickname;

	private String callId;
	private User source;
	private User target;

	// 监听网络通话被叫方的响应（接听、拒绝、忙）
	private Observer<AVChatCalleeAckEvent> observerCallAck = new Observer<AVChatCalleeAckEvent>()
	{
		private static final long serialVersionUID = 1L;

		@Override
		public void onEvent(AVChatCalleeAckEvent event)
		{
			switch (event.getEvent())
			{
			case CALLEE_ACK_AGREE:// 被叫方同意接听
				if (event.isDeviceReady())
				{
					CommonUtil.toast("设备正常,开始通话");
					cm_time.setBase(SystemClock.elapsedRealtime());

					Log.i(TAG, "被叫方同意接听: ackEvent.getChatId():" + event.getChatId());

					Parameters parameters = new Parameters();
					parameters.add("chatId", event.getChatId() + "");
					parameters.add("chatType", event.getChatType().getValue() + "");
					parameters.add("target", target.Id + "");
					parameters.add("source", source.Id + "");

					HttpUtil.post(NetworkUtil.callstart, parameters, new RequestCallBack<String>()
					{

						@Override
						public void onSuccess(ResponseInfo<String> responseInfo)
						{
							Response<CallLog> resp = gson.fromJson(responseInfo.result, new TypeToken<Response<CallLog>>()
							{}.getType());

							if (resp.code == 200)
							{
								callId = resp.info.Id;
							}
							Log.i(TAG, "记录Id:" + resp.info.Id);
						}

						@Override
						public void onFailure(HttpException error, String msg)
						{
							Log.i(TAG, "添加记录失败:" + msg);
						}
					});
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
	};

	private Observer<AVChatCommonEvent> observerHangUp = new Observer<AVChatCommonEvent>()
	{
		private static final long serialVersionUID = 1L;

		@Override
		public void onEvent(AVChatCommonEvent event)
		{
			Log.i("logi", "对方已挂断 event.getChatId:" + event.getChatId());
			Parameters parameters = new Parameters();
			parameters.add("id", callId + "");
			parameters.add("chatId", event.getChatId() + "");

			HttpUtil.post(NetworkUtil.callFinish, parameters, null);
			finish();
		}
	};

	private Observer<AVChatTimeOutEvent> observerTimeOut = new Observer<AVChatTimeOutEvent>()
	{
		private static final long serialVersionUID = 1L;

		@Override
		public void onEvent(AVChatTimeOutEvent timeOutEvent)
		{
			CommonUtil.toast("超时");
			finish();
		}
	};

	protected RequestCallback<Void> sendcallBack = new RequestCallback<Void>()
	{

		@Override
		public void onException(Throwable arg0)
		{
			Log.i(TAG, "sendcallBack" + "onException:" + arg0.getMessage());
		}

		@Override
		public void onFailed(int arg0)
		{
			Log.i(TAG, "sendcallBack" + "onFailed" + arg0);
		}

		@Override
		public void onSuccess(Void arg0)
		{
			Log.i(TAG, "sendcallBack" + "onSuccess");
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_call_teacher);
		initView();
		initData();
	}

	private void createCardDialog()
	{
		DisplayMetrics outMetrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(outMetrics);

		Builder builder = new AlertDialog.Builder(CallActivity.this);
		View dialogView = View.inflate(getApplication(), R.layout.dialog_card, null);
		GridView gv_card = (GridView) dialogView.findViewById(R.id.gv_card);
		cardList = new ArrayList<Theme>();
		cardAdapter = new MyAdapter(cardList);
		gv_card.setAdapter(cardAdapter);
		builder.setView(dialogView);
		cardDialog = builder.create();

		cardDialog.setCanceledOnTouchOutside(false);
		LayoutParams attributes2 = getWindow().getAttributes();
		attributes2.height = outMetrics.heightPixels / 2;
		// cardDialog.getWindow().setAttributes(attributes2);

	}

	private void nimCall()
	{
		if (callback_call == null)
		{
			callback_call = new AVChatCallback<AVChatData>()
			{

				@Override
				public void onException(Throwable arg0)
				{
					CommonUtil.toast("拨打异常");
				}

				@Override
				public void onFailed(int arg0)
				{
					CommonUtil.toast("拨打错误");
				}

				@Override
				public void onSuccess(AVChatData avChatData)
				{
					cm_time.start();
					CommonUtil.toast("拨打成功");
				}
			};
		}
		// 呼叫拨出
		AVChatManager.getInstance().call(target.Accid, AVChatType.AUDIO, null, callback_call);
	}

	private void nimHangup()
	{
		if (callback_hangup == null)
		{
			callback_hangup = new AVChatCallback<Void>()
			{

				@Override
				public void onException(Throwable arg0)
				{
					Log.i("logi", "callactivity hangUp onException:" + arg0.getMessage());
					finish();
				}

				@Override
				public void onFailed(int arg0)
				{
					Log.i("logi", "callactivity hangUp onFailed:" + arg0);
					finish();
				}

				@Override
				public void onSuccess(Void arg0)
				{
					Log.i("logi", "callactivity hangUp onSuccess");
					finish();
				}
			};
		}
		AVChatManager.getInstance().hangUp(callback_hangup);
	}

	private void initData()
	{
		Intent intent = getIntent();
		String nickname = intent.getStringExtra(KEY_TARGET_NICKNAME);
		tv_nickname.setText(nickname);
		SharedPreferences sp = getSharedPreferences("user", MODE_PRIVATE);

		source = new User();
		source.Id = sp.getInt("id", 0);
		source.Accid = sp.getString("accid", "");
		source.NickName = sp.getString("", "");

		target = new User();
		target.Id = intent.getIntExtra(KEY_TARGET_ID, 0);
		target.Accid = intent.getStringExtra(KEY_TARGET_ACCID);
		target.NickName = intent.getStringExtra(KEY_TARGET_NICKNAME);

		nimCall();

		registerObserver();
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
		// cm_time.stop();

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
			nimHangup();
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

			Parameters parameters = new Parameters();
			parameters.add("skip", 0 + "");
			parameters.add("take", 15 + "");
			HttpUtil.post(NetworkUtil.ThemeSelect, parameters, new RequestCallBack<String>()
			{

				@Override
				public void onFailure(HttpException error, String msg)
				{
					CommonUtil.toast("网络异常");
				}

				@Override
				public void onSuccess(ResponseInfo<String> responseInfo)
				{
					Response<List<Theme>> resp = gson.fromJson(responseInfo.result, new TypeToken<Response<List<Theme>>>()
					{}.getType());

					if (resp.code == 200)
					{
						cardList.clear();
						List<Theme> themes = resp.info;
						for (Theme theme : themes)
						{
							cardList.add(theme);
						}
						cardAdapter.notifyDataSetChanged();
					}

					cardDialog.show();
				}
			});

			break;
		default:
			break;
		}
	}

	@Override
	protected void onDestroy()
	{
		super.onDestroy();
		AVChatManager.getInstance().observeCalleeAckNotification(observerCallAck, false);
		AVChatManager.getInstance().observeHangUpNotification(observerHangUp, false);
		AVChatManager.getInstance().observeTimeoutNotification(observerTimeOut, false);
	}

	private void registerObserver()
	{

		// 监听网络通话被叫方的响应（接听、拒绝、忙）
		AVChatManager.getInstance().observeCalleeAckNotification(observerCallAck, true);

		// 监听网络通话对方挂断的通知,即在正常通话时,结束通话
		AVChatManager.getInstance().observeHangUpNotification(observerHangUp, true);

		// 监听呼叫或接听超时通知
		// 主叫方在拨打网络通话时，超过 45 秒被叫方还未接听来电，则自动挂断。
		// 被叫方超过 45 秒未接听来听，也会自动挂断
		// 在通话过程中网络超时 30 秒自动挂断。
		AVChatManager.getInstance().observeTimeoutNotification(observerTimeOut, true);
	}

	private class MyAdapter extends BaseAdapter<Theme>
	{

		public MyAdapter(List<Theme> list)
		{
			super(list);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent)
		{
			final Theme item = getItem(position);
			View inflate = View.inflate(getApplication(), R.layout.griditem_card, null);
			inflate.findViewById(R.id.iv_card).setVisibility(View.VISIBLE);
			TextView tv_theme = (TextView) inflate.findViewById(R.id.tv_theme);
			TextView tv_name = (TextView) inflate.findViewById(R.id.tv_name);
			tv_theme.setText(item.Name);
			tv_name.setText(item.Name);
			inflate.setOnClickListener(new OnClickListener()
			{

				@Override
				public void onClick(View v)
				{
					v.findViewById(R.id.iv_card).setVisibility(View.INVISIBLE);

					// 构造自定义通知，指定接收者
					NimSysNotice<Theme> notice = new NimSysNotice<Theme>();
					notice.NoticeType = NimSysNotice.NoticeType_Card;					
					notice.info =item;

					CustomNotification notification = new CustomNotification();
					notification.setFromAccount(source.Accid);
					notification.setSessionId(target.Accid);
					notification.setSessionType(SessionTypeEnum.P2P);
					notification.setSendToOnlineUserOnly(true);
					notification.setContent(gson.toJson(notice));

					// 发送自定义通知
					NIMClient.getService(MsgService.class).sendCustomNotification(notification).setCallback(sendcallBack);
					if (cardDialog != null)
					{
						cardDialog.dismiss();
					}
				}
			});

			return inflate;
		}
	}

}
