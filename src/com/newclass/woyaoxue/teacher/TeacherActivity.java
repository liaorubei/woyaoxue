package com.newclass.woyaoxue.teacher;

import java.util.List;

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
import com.netease.nimlib.sdk.auth.AuthService;
import com.netease.nimlib.sdk.auth.LoginInfo;
import com.netease.nimlib.sdk.avchat.AVChatManager;
import com.netease.nimlib.sdk.avchat.model.AVChatCommonEvent;
import com.netease.nimlib.sdk.avchat.model.AVChatData;
import com.netease.nimlib.sdk.avchat.model.AVChatRingerConfig;
import com.netease.nimlib.sdk.msg.MsgServiceObserve;
import com.netease.nimlib.sdk.msg.model.IMMessage;
import com.newclass.woyaoxue.MyApplication;
import com.newclass.woyaoxue.activity.LiveChatActivity;
import com.newclass.woyaoxue.bean.Answer;
import com.newclass.woyaoxue.util.Log;
import com.newclass.woyaoxue.util.NetworkUtil;
import com.voc.woyaoxue.R;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class TeacherActivity extends Activity implements OnClickListener
{
	private EditText et_username, et_password;
	private Button bt_login;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_teacher);

		initView();

		// 每次进入之前都要登出
		NIMClient.getService(AuthService.class).logout();
	}

	private void initView()
	{
		et_username = (EditText) findViewById(R.id.et_username);
		et_password = (EditText) findViewById(R.id.et_password);
		bt_login = (Button) findViewById(R.id.bt_login);
		bt_login.setOnClickListener(this);
	}

	@Override
	public void onClick(View v)
	{
		switch (v.getId())
		{
		case R.id.bt_login:
			bt_login.setEnabled(false);

			String username = et_username.getText().toString().trim();
			String password = et_password.getText().toString().trim();

			if (TextUtils.isEmpty(username) || TextUtils.isEmpty(password))
			{
				Toast.makeText(this, "不能为空", Toast.LENGTH_SHORT).show();
				return;
			}

			RequestParams params = new RequestParams();
			params.addBodyParameter("username", username);
			params.addBodyParameter("password", password);
			new HttpUtils().send(HttpMethod.POST, NetworkUtil.userSignIn, params, new RequestCallBack<String>(this)
			{

				@Override
				public void onSuccess(ResponseInfo<String> responseInfo)
				{
					Answer answer = new Gson().fromJson(responseInfo.result, Answer.class);

					if (200 == answer.code)
					{
						signInNim(answer.info.accid, answer.info.token);
					}
					bt_login.setEnabled(true);
				}

				@Override
				public void onFailure(HttpException error, String msg)
				{
					Toast.makeText((Context) this.userTag, "网络异常,登录失败", Toast.LENGTH_SHORT).show();
					bt_login.setEnabled(true);
				}
			});

			break;

		default:
			break;
		}

	}

	@SuppressWarnings("unchecked")
	protected void signInNim(String accid, String token)
	{
		LoginInfo arg0 = new LoginInfo(accid, token);
		NIMClient.getService(AuthService.class).login(arg0).setCallback(new RequestCallback<LoginInfo>()
		{

			@Override
			public void onException(Throwable arg0)
			{
				Toast.makeText(MyApplication.getContext(), "网络异常,登录失败", Toast.LENGTH_SHORT).show();
			}

			@Override
			public void onFailed(int arg0)
			{
				Toast.makeText(MyApplication.getContext(), "网络异常,登录失败", Toast.LENGTH_SHORT).show();
			}

			@Override
			public void onSuccess(LoginInfo info)
			{
				Toast.makeText(MyApplication.getContext(), "教师登录成功", Toast.LENGTH_SHORT).show();
				Log.i("logi", "教师登录成功:accid=" + info.getAccount() + " token=" + info.getToken());

				// 铃声配置
				AVChatRingerConfig config = new AVChatRingerConfig();
				config.res_connecting = R.raw.avchat_connecting;
				config.res_no_response = R.raw.avchat_no_response;
				config.res_peer_busy = R.raw.avchat_peer_busy;
				config.res_peer_reject = R.raw.avchat_peer_reject;
				config.res_ring = R.raw.avchat_ring;
				AVChatManager.getInstance().setRingerConfig(config);

				// 消息监听注册
				NIMClient.getService(MsgServiceObserve.class).observeReceiveMessage(new Observer<List<IMMessage>>()
				{

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
						Intent intent = new Intent(TeacherActivity.this, LiveChatActivity.class);
						intent.putExtra("chatData", avChatData);
						startActivity(intent);
					}
				}, true);

				// 监听网络通话对方挂断的通知
				AVChatManager.getInstance().observeHangUpNotification(new Observer<AVChatCommonEvent>()
				{
					private static final long serialVersionUID = 1L;

					@Override
					public void onEvent(AVChatCommonEvent event)
					{
						Log.i("logi", "对方已挂断");
					}
				}, true);

				Intent intent = new Intent(TeacherActivity.this, QueueActivity.class);
				intent.putExtra("accid", info.getAccount());
				startActivity(intent);
			}
		});

	}
}
