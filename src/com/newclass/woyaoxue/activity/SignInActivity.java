package com.newclass.woyaoxue.activity;

import com.google.gson.Gson;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.RequestCallback;
import com.netease.nimlib.sdk.auth.AuthService;
import com.netease.nimlib.sdk.auth.LoginInfo;
import com.newclass.woyaoxue.MyApplication;
import com.newclass.woyaoxue.bean.Answer;
import com.newclass.woyaoxue.util.Log;
import com.newclass.woyaoxue.util.NetworkUtil;
import com.voc.woyaoxue.R;

import android.app.Activity;
import android.content.Intent;
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
	protected void onCreate(Bundle savedInstanceState)
	{
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_signin);

		initView();
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
			startActivity(new Intent(SignInActivity.this, SignUpActivity.class));
			break;
		default:
			break;
		}
	}

	public static void signIn(String username, String password)
	{
		RequestParams params = new RequestParams();
		params.addBodyParameter("username", username);
		params.addBodyParameter("password", password);
		new HttpUtils().send(HttpMethod.POST, NetworkUtil.userLogin, params, new RequestCallBack<String>()
		{
			@Override
			public void onSuccess(ResponseInfo<String> responseInfo)
			{
				Answer answer = new Gson().fromJson(responseInfo.result, Answer.class);
				Log.i("logi", "" + answer.toString());
				if (answer.code == 200)
				{
					// 登录云信
					LoginInfo loginInfo = new LoginInfo(answer.info.accid, answer.info.token);
					RequestCallback<LoginInfo> callBack = new RequestCallback<LoginInfo>()
					{

						@Override
						public void onSuccess(LoginInfo info)
						{
							Log.i("logi", "云信登录成功");
							// 保护登录信息
							Editor editor = MyApplication.getContext().getSharedPreferences("user", MODE_PRIVATE).edit();
							editor.putString("accid", info.getAccount());
							editor.putString("token", info.getToken());
							editor.commit();
						}

						@Override
						public void onFailed(int arg0)
						{
							Log.i("logi", "云集登录失败" + arg0);
							Toast.makeText(MyApplication.getContext(), "网络异常,即时聊天功能暂时不可用", Toast.LENGTH_SHORT).show();
						}

						@Override
						public void onException(Throwable arg0)
						{
							Toast.makeText(MyApplication.getContext(), "网络异常,即时聊天功能暂时不可用", Toast.LENGTH_SHORT).show();
						}
					};
					NIMClient.getService(AuthService.class).login(loginInfo).setCallback(callBack);

					// 保护登录信息
					Editor editor = MyApplication.getContext().getSharedPreferences("user", MODE_PRIVATE).edit();
					editor.putString("username", answer.info.username);
					editor.putString("password", answer.info.password);
					editor.commit();
				}
				else
				{
					Toast.makeText(MyApplication.getContext(), "登录失败,帐号密码不匹配", Toast.LENGTH_SHORT).show();
				}

			}

			@Override
			public void onFailure(HttpException error, String msg)
			{
				Toast.makeText(MyApplication.getContext(), "网络异常,请稍后重试", Toast.LENGTH_SHORT).show();
			}
		});
	}
}
