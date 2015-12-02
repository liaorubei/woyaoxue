package com.newclass.woyaoxue.activity;

import com.google.gson.Gson;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;
import com.newclass.woyaoxue.bean.Answer;
import com.newclass.woyaoxue.util.Log;
import com.newclass.woyaoxue.util.NetworkUtil;
import com.voc.woyaoxue.R;

import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

/**
 * 帐号注册界面
 * @author liaorubei
 *
 */
public class SignUpActivity extends Activity
{
	private EditText et_username, et_password, et_repassword;
	private Button bt_signup;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_signup);
		initView();
	}

	private void initView()
	{
		et_username = (EditText) findViewById(R.id.et_username);
		et_password = (EditText) findViewById(R.id.et_password);
		et_repassword = (EditText) findViewById(R.id.et_repassword);
		bt_signup = (Button) findViewById(R.id.bt_signup);
		bt_signup.setOnClickListener(new OnClickListener()
		{

			@Override
			public void onClick(View v)
			{
				String account = et_username.getText().toString().trim();
				String password = et_password.getText().toString().trim();
				String repassword = et_repassword.getText().toString().trim();

				if (TextUtils.isEmpty(account) || TextUtils.isEmpty(password) || TextUtils.isEmpty(repassword))
				{
					Toast.makeText(SignUpActivity.this, "帐号或密码不能为空", Toast.LENGTH_SHORT).show();
					return;
				}

				if (!password.equals(repassword))
				{
					Toast.makeText(SignUpActivity.this, "两次输入的密码不一致", Toast.LENGTH_SHORT).show();
					return;
				}

				HttpUtils httpUtils = new HttpUtils();
				RequestParams params = new RequestParams();
				params.addBodyParameter("account", account);
				params.addBodyParameter("password", password);
				params.addBodyParameter("NickName", account);

				bt_signup.setEnabled(false);
				httpUtils.send(HttpMethod.POST, NetworkUtil.userCreate, params, new RequestCallBack<String>()
				{

					@Override
					public void onSuccess(ResponseInfo<String> responseInfo)
					{
						Answer json = new Gson().fromJson(responseInfo.result, Answer.class);

						Log.i("logi", "创建成功:" + json.toString());
						if (200 == json.code)
						{
							Toast.makeText(SignUpActivity.this, "注册成功", Toast.LENGTH_SHORT).show();

							// 登录
							SignInActivity.signIn(json.info.username, json.info.password);

							finish();
						}
						else
						{
							Toast.makeText(SignUpActivity.this, json.desc, Toast.LENGTH_SHORT).show();
						}

					}

					@Override
					public void onFailure(HttpException error, String msg)
					{
						Toast.makeText(SignUpActivity.this, "网络异常,请重试", Toast.LENGTH_SHORT).show();
						bt_signup.setEnabled(true);
					}
				});

			}
		});
	}
}
