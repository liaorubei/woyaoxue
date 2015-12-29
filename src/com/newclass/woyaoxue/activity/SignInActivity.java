package com.newclass.woyaoxue.activity;

import java.io.UnsupportedEncodingException;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.Observer;
import com.netease.nimlib.sdk.RequestCallback;
import com.netease.nimlib.sdk.StatusCode;
import com.netease.nimlib.sdk.auth.AuthService;
import com.netease.nimlib.sdk.auth.AuthServiceObserver;
import com.netease.nimlib.sdk.auth.LoginInfo;
import com.netease.nimlib.sdk.avchat.AVChatManager;
import com.netease.nimlib.sdk.avchat.model.AVChatData;
import com.netease.nimlib.sdk.avchat.model.AVChatRingerConfig;
import com.netease.nimlib.sdk.friend.FriendService;
import com.netease.nimlib.sdk.friend.model.AddFriendNotify;
import com.netease.nimlib.sdk.msg.MsgServiceObserve;
import com.netease.nimlib.sdk.msg.SystemMessageObserver;
import com.netease.nimlib.sdk.msg.constant.SystemMessageType;
import com.netease.nimlib.sdk.msg.model.IMMessage;
import com.netease.nimlib.sdk.msg.model.SystemMessage;
import com.netease.nimlib.sdk.rts.RTSCallback;
import com.netease.nimlib.sdk.rts.RTSChannelStateObserver;
import com.netease.nimlib.sdk.rts.RTSManager;
import com.netease.nimlib.sdk.rts.constant.RTSEventType;
import com.netease.nimlib.sdk.rts.constant.RTSTimeOutEvent;
import com.netease.nimlib.sdk.rts.constant.RTSTunType;
import com.netease.nimlib.sdk.rts.model.RTSCalleeAckEvent;
import com.netease.nimlib.sdk.rts.model.RTSCommonEvent;
import com.netease.nimlib.sdk.rts.model.RTSControlEvent;
import com.netease.nimlib.sdk.rts.model.RTSData;
import com.netease.nimlib.sdk.rts.model.RTSOptions;
import com.netease.nimlib.sdk.rts.model.RTSTunData;
import com.newclass.woyaoxue.MainActivity;
import com.newclass.woyaoxue.MyApplication;
import com.newclass.woyaoxue.bean.Answer;
import com.newclass.woyaoxue.bean.Response;
import com.newclass.woyaoxue.bean.User;
import com.newclass.woyaoxue.util.CommonUtil;
import com.newclass.woyaoxue.util.Log;
import com.newclass.woyaoxue.util.NetworkUtil;
import com.voc.woyaoxue.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 用户登录界面
 * @author liaorubei
 *
 */
public class SignInActivity extends Activity implements OnClickListener
{
	public static final int SignUp = 0;
	protected static final String TAG = "SignInActivity";
	private Button bt_login;
	private EditText et_username, et_password;
	private TextView tv_signup;

	private void initView()
	{
		et_username = (EditText) findViewById(R.id.et_username);
		et_password = (EditText) findViewById(R.id.et_password);
		bt_login = (Button) findViewById(R.id.bt_login);
		tv_signup = (TextView) findViewById(R.id.tv_signup);

		bt_login.setOnClickListener(this);
		tv_signup.setOnClickListener(this);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == SignUp && resultCode == SignUpActivity.SignUp && data != null)
		{
			String username = data.getStringExtra("username");
			String password = data.getStringExtra("password");
			signIn(username, password);
		}
	}

	@Override
	public void onClick(View v)
	{
		switch (v.getId())
		{
		case R.id.bt_login:
			bt_login.setEnabled(false);

			String account = et_username.getText().toString().trim();
			String password = et_password.getText().toString().trim();

			if (TextUtils.isEmpty(account) || TextUtils.isEmpty(password))
			{
				Toast.makeText(SignInActivity.this, "帐号或密码不能为空", Toast.LENGTH_SHORT).show();
				return;
			}

			// 开始登录
			signIn(account, password);

			break;

		case R.id.tv_signup:
			startActivityForResult(new Intent(SignInActivity.this, SignUpActivity.class), SignUp);
			break;
		default:
			break;
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_signin);

		// 取出保存的用户数据,如果存在就直接登录
		SharedPreferences sp = getSharedPreferences("user", MODE_PRIVATE);
		String username = sp.getString("username", "");
		String password = sp.getString("password", "");
		if (!(TextUtils.isEmpty(username) || TextUtils.isEmpty(password)))
		{
			// signIn(username, password);
		}

		initView();

		NIMClient.getService(AuthService.class).logout();

	}

	public void signIn(final String username, final String password)
	{
		RequestParams params = new RequestParams();
		params.addBodyParameter("username", username);
		params.addBodyParameter("password", password);

		new HttpUtils().send(HttpMethod.POST, NetworkUtil.userSignIn, params, new RequestCallBack<String>()
		{
			@Override
			public void onFailure(HttpException error, String msg)
			{
				bt_login.setEnabled(true);
				Toast.makeText(MyApplication.getContext(), "网络异常,请稍后重试", Toast.LENGTH_SHORT).show();
			}

			@Override
			public void onSuccess(ResponseInfo<String> responseInfo)
			{
				bt_login.setEnabled(true);
				Response<User> response = new Gson().fromJson(responseInfo.result, new TypeToken<Response<User>>()
				{}.getType());
				if (response.code == 200)
				{
					// 登录云信
					signInNim(response.info.Accid, response.info.Token);
					// 保护登录信息
					Editor editor = SignInActivity.this.getSharedPreferences("user", MODE_PRIVATE).edit();
					editor.putInt("id", response.info.Id);
					editor.putString("accid", response.info.Accid);
					editor.putString("token", response.info.Token);
					editor.putString("username", username);
					editor.putString("password", password);
					editor.commit();
				}
				else
				{
					Toast.makeText(MyApplication.getContext(), "登录失败,帐号密码不匹配", Toast.LENGTH_SHORT).show();
				}
			}
		});
	}

	@SuppressWarnings("unchecked")
	private void signInNim(String accid, String token)
	{
		NIMClient.getService(AuthService.class).login(new LoginInfo(accid, token)).setCallback(new RequestCallback<LoginInfo>()
		{
			@Override
			public void onException(Throwable arg0)
			{
				Log.i("logi", "云集登录异常" + arg0);
				CommonUtil.toast("网络异常,登录失败");
			}

			@Override
			public void onFailed(int arg0)
			{
				Log.i("logi", "云集登录失败" + arg0);
				CommonUtil.toast("网络异常,登录失败");
			}

			@Override
			public void onSuccess(LoginInfo info)
			{
				Log.i("logi", "云信登录成功:" + info.getAccount() + " token=" + info.getToken());
				// 保护登录信息
				Editor editor = SignInActivity.this.getSharedPreferences("user", MODE_PRIVATE).edit();
				editor.putString("accid", info.getAccount());
				editor.putString("token", info.getToken());
				editor.commit();

				initAVChatManager();

				finish();
			}
		});

		// 监听用户在线状态
		NIMClient.getService(AuthServiceObserver.class).observeOnlineStatus(new Observer<StatusCode>()
		{
			private static final long serialVersionUID = 1L;

			@Override
			public void onEvent(StatusCode code)
			{
				Log.i("logi", "StatusCode;" + code);
				if (code == StatusCode.KICK_BY_OTHER_CLIENT)
				{

				}
				else if (code == StatusCode.KICKOUT)
				{

				}
				else if (code == StatusCode.LOGINED)
				{

				}
				else if (code == StatusCode.UNLOGIN)
				{

				}
			}
		}, true);
	}

	protected void initAVChatManager()
	{
		AVChatRingerConfig config = new AVChatRingerConfig();
		config.res_connecting = R.raw.avchat_connecting;
		config.res_no_response = R.raw.avchat_no_response;
		config.res_peer_busy = R.raw.avchat_peer_busy;
		config.res_peer_reject = R.raw.avchat_peer_reject;
		config.res_ring = R.raw.avchat_ring;
		AVChatManager.getInstance().setRingerConfig(config); // 铃声配置

		// 消息监听注册
		NIMClient.getService(MsgServiceObserve.class).observeReceiveMessage(new Observer<List<IMMessage>>()
		{
			private static final long serialVersionUID = 1L;

			@Override
			public void onEvent(List<IMMessage> list)
			{
				for (IMMessage imMessage : list)
				{
					Log.i("logi", "教师消息类型为:" + imMessage.getMsgType());
				}
			}
		}, true);

		// 监听网络来电
		AVChatManager.getInstance().observeIncomingCall(new Observer<AVChatData>()
		{
			private static final long serialVersionUID = 1L;

			@Override
			public void onEvent(AVChatData avChatData)
			{
				Log.i(TAG, "observeIncomingCall");
				Intent intent = new Intent(getApplication(), TakeActivity.class);
				intent.putExtra(TakeActivity.KEY_TARGET, avChatData.getAccount());
				startActivity(intent);
			}
		}, true);

		// 实时会话
		RTSManager.getInstance().observeIncomingSession(new Observer<RTSData>()
		{

			private static final long serialVersionUID = 1L;

			@Override
			public void onEvent(RTSData rtsData)
			{
				// 启动会话界面

				// 会话类型
				List<RTSTunType> tunTypes = rtsData.getTunTypes();

			}
		}, true);

		RTSOptions options = new RTSOptions().setRecordAudioTun(true).setRecordTCPTun(true);
		String sessionId = "";
		// 接受会话（被叫方）
		RTSManager.getInstance().accept("", options, new RTSCallback<Boolean>()
		{

			@Override
			public void onException(Throwable arg0)
			{

			}

			@Override
			public void onFailed(int arg0)
			{

			}

			@Override
			public void onSuccess(Boolean arg0)
			{

			}
		});

		// 拒绝会话（被叫方）
		RTSManager.getInstance().close("", new RTSCallback<Void>()
		{

			@Override
			public void onException(Throwable arg0)
			{

			}

			@Override
			public void onFailed(int arg0)
			{

			}

			@Override
			public void onSuccess(Void arg0)
			{

			}
		});

		// 监听被叫方回应（主叫方）
		RTSManager.getInstance().observeCalleeAckNotification(sessionId, new Observer<RTSCalleeAckEvent>()
		{

			@Override
			public void onEvent(RTSCalleeAckEvent rtsCalleeAckEvent)
			{

				if (rtsCalleeAckEvent.getEvent() == RTSEventType.CALLEE_ACK_AGREE)
				{
					// 判断SDK自动开启通道是否成功
					if (!rtsCalleeAckEvent.isTunReady())
					{
						return;
					}
					// 进入会话界面
				}
				else if (rtsCalleeAckEvent.getEvent() == RTSEventType.CALLEE_ACK_REJECT)
				{
					// 被拒绝，结束会话
				}

			}
		}, true);

		// 监听对方结束会话（主叫方、被叫方）
		// 当被叫方收到会话请求时需要监听主叫方结束会话的通知；当双方会话建立之后，需要监听对方结束会话的通知。
		RTSManager.getInstance().observeHangUpNotification(sessionId, new Observer<RTSCommonEvent>()
		{

			private static final long serialVersionUID = 1L;

			@Override
			public void onEvent(RTSCommonEvent arg0)
			{

			}
		}, true);

		// 发送控制信息
		// 双方会话建立之后，就可以相互发送控制信息了。
		RTSManager.getInstance().sendControlCommand(sessionId, "content", new RTSCallback<Void>()
		{

			@Override
			public void onException(Throwable arg0)
			{

			}

			@Override
			public void onFailed(int arg0)
			{

			}

			@Override
			public void onSuccess(Void arg0)
			{

			}
		});

		// 监听会话控制通知
		// 双方会话建立之后，需要监听会话控制通知。
		RTSManager.getInstance().observeControlNotification(sessionId, new Observer<RTSControlEvent>()
		{

			@Override
			public void onEvent(RTSControlEvent arg0)
			{

			}
		}, true);

		// 监听（发起）创建新通道或接受新通道超时通知
		// 主叫方在（发起会话）创建通道时，超过 40 秒被叫方还未接受，则自动挂断。被叫方超过 40 秒接受会话，也会自动挂断。
		RTSManager.getInstance().observeTimeoutNotification(sessionId, new Observer<RTSTimeOutEvent>()
		{

			@Override
			public void onEvent(RTSTimeOutEvent arg0)
			{

			}
		}, true);

		// 监听数据通道的状态
		// 发起会话（对方接受后），或者接受了会话请求后，需要立即注册对数据通道状态的监听。
		RTSManager.getInstance().observeChannelState(sessionId, new RTSChannelStateObserver()
		{

			@Override
			public void onRecordInfo(RTSTunType arg0, String arg1, String arg2)
			{

			}

			@Override
			public void onNetworkStatusChange(RTSTunType arg0, int arg1)
			{

			}

			@Override
			public void onError(RTSTunType arg0, int arg1)
			{

			}

			@Override
			public void onDisconnectServer(RTSTunType arg0)
			{

			}

			@Override
			public void onConnectResult(RTSTunType arg0, int arg1)
			{

			}

			@Override
			public void onChannelEstablished(RTSTunType arg0)
			{

			}
		}, true);

		// 接收数据
		// 数据通道建立完之后，就可以监听对方发送来的数据。
		RTSManager.getInstance().observeReceiveData(sessionId, new Observer<RTSTunData>()
		{
			@Override
			public void onEvent(RTSTunData rtsTunData)
			{
				String data = "[parse bytes error]";
				try
				{
					data = new String(rtsTunData.getData(), 0, rtsTunData.getLength(), "UTF-8");
				}
				catch (UnsupportedEncodingException e)
				{
					e.printStackTrace();
				}

			}
		}, true);

		// ===================================好友======================================================
		NIMClient.getService(SystemMessageObserver.class).observeReceiveSystemMsg(new Observer<SystemMessage>()
		{
			private static final long serialVersionUID = 1L;

			@Override
			public void onEvent(SystemMessage sysMsg)
			{
				switch (sysMsg.getType())
				{
				case AddFriend:// 添加好友
					AddFriendNotify attachData = (AddFriendNotify) sysMsg.getAttachObject();
					if (attachData != null)
					{
						// 针对不同的事件做处理
						if (attachData.getEvent() == AddFriendNotify.Event.RECV_ADD_FRIEND_DIRECT)
						{
							CommonUtil.toast("有人添加你为他/她/它的好友");
							// 对方直接添加你为好友
						}
						else if (attachData.getEvent() == AddFriendNotify.Event.RECV_AGREE_ADD_FRIEND)
						{
							CommonUtil.toast("对方通过了你的好友请求");
							// 对方通过了你的好友验证请求
						}
						else if (attachData.getEvent() == AddFriendNotify.Event.RECV_REJECT_ADD_FRIEND)
						{
							CommonUtil.toast("对方拒绝了你的好友请求");
							// 对方拒绝了你的好友验证请求
						}
						else if (attachData.getEvent() == AddFriendNotify.Event.RECV_ADD_FRIEND_VERIFY_REQUEST)
						{
							// 对方请求添加好友，一般场景会让用户选择同意或拒绝对方的好友请求。
							// 通过message.getContent()获取好友验证请求的附言
							Intent intent = new Intent(getApplication(), FriendAddActivity.class);
							intent.putExtra("account", sysMsg.getFromAccount());
							intent.putExtra("content", sysMsg.getContent());
							startActivity(intent);

						}
					}
					break;
				case ApplyJoinTeam:

					break;
				case DeclineTeamInvite:

					break;
				case RejectTeamApply:

					break;
				case TeamInvite:

					break;
				case undefined:

					break;
				default:
					break;
				}

			}
		}, true);

	}
}
