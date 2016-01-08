package com.newclass.woyaoxue.activity;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.StatusCode;
import com.netease.nimlib.sdk.auth.AuthService;
import com.newclass.woyaoxue.base.BaseAdapter;
import com.newclass.woyaoxue.bean.Rank;
import com.newclass.woyaoxue.bean.Response;
import com.newclass.woyaoxue.bean.User;
import com.newclass.woyaoxue.util.HttpUtil;
import com.newclass.woyaoxue.util.HttpUtil.Parameters;
import com.newclass.woyaoxue.util.NetworkUtil;
import com.voc.woyaoxue.R;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 教师排队界面
 * @author liaorubei
 *
 */
public class TeacherQueueActivity extends Activity
{
	protected static final String TAG = "TeacherQueueActivity";
	private List<User> list;
	private MyAdapter adapter;
	private ListView listview;
	private Gson gson = new Gson();

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_teacherqueue);

		// 为了便于测试,没有上次帐号的干扰,每次进入之前都登出上次的帐号
		NIMClient.getService(AuthService.class).logout();

		initView();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		getMenuInflater().inflate(R.menu.activity_teacherqueue, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		switch (item.getItemId())
		{
		case R.id.menu_refresh_teacher:
			refresh();
			break;
		case R.id.menu_teacher_queue:
			enqueue();
			break;
		default:
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	private void enqueue()
	{
		StatusCode status = NIMClient.getStatus();
		if (status != StatusCode.LOGINED)
		{
			startActivity(new Intent(this, SignInActivity.class));
			return;
		}

		Parameters parameters = new Parameters();
		parameters.add("id", getSharedPreferences("user", MODE_PRIVATE).getInt("id", 0) + "");
		HttpUtil.post(NetworkUtil.teacherEnqueue, parameters, new RequestCallBack<String>()
		{

			@Override
			public void onSuccess(ResponseInfo<String> responseInfo)
			{
				refreshView(responseInfo.result);
			}

			@Override
			public void onFailure(HttpException error, String msg)
			{
				Toast.makeText(getApplication(), "排除失败", Toast.LENGTH_SHORT).show();
			}
		});
	}

	private void refresh()
	{
		StatusCode status = NIMClient.getStatus();
		if (status != StatusCode.LOGINED)
		{
			startActivity(new Intent(this, SignInActivity.class));
			return;
		}

		Parameters parameters = new Parameters();
		parameters.add("id", getSharedPreferences("user", MODE_PRIVATE).getInt("id", 0) + "");
		HttpUtil.post(NetworkUtil.teacherRefresh, parameters, new RequestCallBack<String>()
		{
			@Override
			public void onSuccess(ResponseInfo<String> responseInfo)
			{
				refreshView(responseInfo.result);
			}

			@Override
			public void onFailure(HttpException error, String msg)
			{}
		});
	}

	private void refreshView(String json)
	{
		Response<Rank> resp = gson.fromJson(json, new TypeToken<Response<Rank>>()
		{}.getType());

		if (resp.code == 200 && resp.info != null)
		{
			List<User> users = resp.info.Data;
			list.clear();
			for (User user : users)
			{
				list.add(user);
			}
			adapter.notifyDataSetChanged();
		}
	}

	private void initView()
	{
		listview = (ListView) findViewById(R.id.listview);
		list = new ArrayList<User>();
		adapter = new MyAdapter(list);
		listview.setAdapter(adapter);
		//listview.smoothScrollToPosition(position);
	}

	private class MyAdapter extends BaseAdapter<User>
	{

		public MyAdapter(List<User> list)
		{
			super(list);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent)
		{
			User user = list.get(position);
			String accid = getSharedPreferences("user", MODE_PRIVATE).getString("accid", "");
			View inflate = View.inflate(TeacherQueueActivity.this, R.layout.listitem_teacherqueue, null);
			TextView tv_nickname = (TextView) inflate.findViewById(R.id.tv_nickname);
			TextView tv_username = (TextView) inflate.findViewById(R.id.tv_username);
			TextView tv_category = (TextView) inflate.findViewById(R.id.tv_category);

			tv_nickname.setText(user.Name + (accid.equals(user.Accid) ? "(本人)" : ""));
			tv_username.setText(user.Username);
			tv_category.setText(user.Category == 1 ? "教师" : "学生");

			tv_username.setTextColor(accid.equals(user.Accid) ? Color.RED : Color.BLACK);
			return inflate;
		}
	}
}
