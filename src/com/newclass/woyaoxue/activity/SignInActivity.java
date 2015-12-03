package com.newclass.woyaoxue.activity;

import com.google.gson.Gson;
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
import com.netease.nimlib.sdk.avchat.constant.AVChatEventType;
import com.netease.nimlib.sdk.avchat.model.AVChatCalleeAckEvent;
import com.netease.nimlib.sdk.avchat.model.AVChatCommonEvent;
import com.netease.nimlib.sdk.avchat.model.AVChatData;
import com.netease.nimlib.sdk.avchat.model.AVChatRingerConfig;
import com.newclass.woyaoxue.MyApplication;
import com.newclass.woyaoxue.bean.Answer;
import com.newclass.woyaoxue.util.Log;
import com.newclass.woyaoxue.util.NetworkUtil;
import com.voc.woyaoxue.R;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
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

	public void signIn(final String username, final String password)
	{
		RequestParams params = new RequestParams();
		params.addBodyParameter("username", username);
		params.addBodyParameter("password", password);

		new HttpUtils().send(HttpMethod.POST, NetworkUtil.userLogin, params, new RequestCallBack<String>()
		{
			@Override
			public void onFailure(HttpException error, String msg)
			{
				Toast.makeText(MyApplication.getContext(), "网络异常,请稍后重试", Toast.LENGTH_SHORT).show();
			}

			@Override
			public void onSuccess(ResponseInfo<String> responseInfo)
			{
				Answer answer = new Gson().fromJson(responseInfo.result, Answer.class);
				Log.i("logi", "应用登录成功:" + answer.toString());
				if (answer.code == 200)
				{
					// 登录云信
					signInNim(answer.info.accid, answer.info.token);
					// 保护登录信息
					Editor editor = SignInActivity.this.getSharedPreferences("user", MODE_PRIVATE).edit();
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

	private void signInNim(String accid, String token)
	{
		LoginInfo loginInfo = new LoginInfo(accid, token);
		RequestCallback<LoginInfo> callBack = new RequestCallback<LoginInfo>()
		{
			@Override
			public void onException(Throwable arg0)
			{
				Toast.makeText(MyApplication.getContext(), "网络异常,即时聊天功能暂时不可用", Toast.LENGTH_SHORT).show();
			}

			@Override
			public void onFailed(int arg0)
			{
				Log.i("logi", "云集登录失败" + arg0);
				Toast.makeText(MyApplication.getContext(), "网络异常,即时聊天功能暂时不可用", Toast.LENGTH_SHORT).show();
			}

			@Override
			public void onSuccess(LoginInfo info)
			{
				Log.i("logi", "云信登录成功");
				// 保护登录信息
				Editor editor = SignInActivity.this.getSharedPreferences("user", MODE_PRIVATE).edit();
				editor.putString("accid", info.getAccount());
				editor.putString("token", info.getToken());
				editor.commit();

				AVChatRingerConfig config = new AVChatRingerConfig();
				config.res_connecting = R.raw.avchat_connecting;
				config.res_no_response = R.raw.avchat_no_response;
				config.res_peer_busy = R.raw.avchat_peer_busy;
				config.res_peer_reject = R.raw.avchat_peer_reject;
				config.res_ring = R.raw.avchat_ring;
				AVChatManager.getInstance().setRingerConfig(config); // 铃声配置

				// 监听来电
				AVChatManager.getInstance().observeIncomingCall(new Observer<AVChatData>()
				{

					@Override
					public void onEvent(AVChatData chatData)
					{
						Log.i("来电了:" + chatData.getAccount());
						Intent intent = new Intent(SignInActivity.this, LiveChatActivity.class);
						startActivity(intent);
					}
				}, true);

				// 监听挂断
				AVChatManager.getInstance().observeHangUpNotification(new Observer<AVChatCommonEvent>()
				{

					@Override
					public void onEvent(AVChatCommonEvent arg0)
					{
						Log.i("对方挂断了:" + arg0.getAccount());

					}
				}, true);

				// 监听回应
				AVChatManager.getInstance().observeCalleeAckNotification(new Observer<AVChatCalleeAckEvent>()
				{

					@Override
					public void onEvent(AVChatCalleeAckEvent ackInfo)
					{
						if (ackInfo.getEvent() == AVChatEventType.CALLEE_ACK_BUSY)
						{
							// 对方正在忙
						}
						else if (ackInfo.getEvent() == AVChatEventType.CALLEE_ACK_REJECT)
						{
							// 对方拒绝接听
						}
						else if (ackInfo.getEvent() == AVChatEventType.CALLEE_ACK_AGREE)
						{
							// 对方同意接听
							if (ackInfo.isDeviceReady())
							{
								// 设备初始化成功，开始通话
							}
							else
							{
								// 设备初始化失败，无法进行通话
							}
						}

					}
				}, true);

				startActivity(new Intent(SignInActivity.this, MessageActivity.class));
				finish();
			}
		};
		NIMClient.getService(AuthService.class).login(loginInfo).setCallback(callBack);

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
		}, true);//
	}

	private EditText et_username, et_password;

	private Button bt_login;

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
	public void onClick(View v)
	{
		switch (v.getId())
		{
		case R.id.bt_login:

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
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == SignUp && resultCode == SignUpActivity.SignUp)
		{
			String username = data.getStringExtra("username");
			String password = data.getStringExtra("password");
			signIn(username, password);
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_signin);

		SharedPreferences sp = getSharedPreferences("user", MODE_PRIVATE);
		String username = sp.getString("username", "");
		String password = sp.getString("password", "");

		Log.i("logi", "username=" + username + " password=" + password);

		if (!(TextUtils.isEmpty(username) || TextUtils.isEmpty(password)))
		{
			signIn(username, password);
		}

		initView();
	}
}
