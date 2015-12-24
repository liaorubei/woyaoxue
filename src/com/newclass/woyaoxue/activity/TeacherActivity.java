package com.newclass.woyaoxue.activity;

import org.json.JSONException;
import org.json.JSONObject;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.auth.AuthService;
import com.newclass.woyaoxue.bean.Rank;
import com.newclass.woyaoxue.bean.Response;
import com.newclass.woyaoxue.bean.User;
import com.newclass.woyaoxue.util.HttpUtil;
import com.newclass.woyaoxue.util.HttpUtil.Parameters;
import com.newclass.woyaoxue.util.Log;
import com.newclass.woyaoxue.util.NetworkUtil;
import com.voc.woyaoxue.R;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

public class TeacherActivity extends Activity implements OnClickListener
{
	private Button bt_queue, bt_group;
	private String accid;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_queue);
		initView();
		NIMClient.getService(AuthService.class).logout();
		startActivity(new Intent(this, SignInActivity.class));

	}

	private void initView()
	{
		bt_queue = (Button) findViewById(R.id.bt_queue);
		bt_group = (Button) findViewById(R.id.bt_group);
		bt_queue.setOnClickListener(this);
		bt_group.setOnClickListener(this);
	}

	@Override
	public void onClick(View v)
	{
		switch (v.getId())
		{
		case R.id.bt_queue:
			SharedPreferences sp = getSharedPreferences("user", MODE_PRIVATE);
			accid = sp.getString("accid", "");

			bt_queue.setEnabled(false);

			Parameters parameters = new Parameters();
			parameters.add("id", getSharedPreferences("user", MODE_PRIVATE).getInt("id", 0) + "");
			HttpUtil.post(NetworkUtil.teacherEnqueue, parameters, new RequestCallBack<String>()
			{

				@Override
				public void onSuccess(ResponseInfo<String> responseInfo)
				{
					Response<Rank> resp = new Gson().fromJson(responseInfo.result, new TypeToken<Response<Rank>>()
					{}.getType());
					if (resp.code == 200)
					{
						Toast.makeText(TeacherActivity.this, "排队成功,当前名次为:" + resp.info.Rank, Toast.LENGTH_SHORT).show();
					}
					bt_queue.setEnabled(true);
				}

				@Override
				public void onFailure(HttpException error, String msg)
				{
					bt_queue.setEnabled(true);
					Toast.makeText(TeacherActivity.this, "排除失败", Toast.LENGTH_SHORT).show();
				}
			});
			break;

		case R.id.bt_group:
			Intent intent = new Intent(this, GroupActivity.class);
			intent.putExtra(GroupActivity.ENTER_TYPE, GroupActivity.ENTER_TEACHER);
			startActivity(intent);

			break;

		default:
			break;
		}
	}
}
