package com.newclass.woyaoxue.activity;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.newclass.woyaoxue.base.BaseAdapter;
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
import android.graphics.Color;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.Button;
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
	private List<User> list;
	private MyAdapter adapter;
	private ListView listview;
	private String accid;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_teacherqueue);

		initView();
		// initData();

		SharedPreferences sp = getSharedPreferences("user", MODE_PRIVATE);
		accid = sp.getString("accid", "");
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		getMenuInflater().inflate(R.menu.activity_teacherqueue, menu);
		return true;
	}

	@Override
	protected void onResume()
	{
		super.onResume();
		initData();
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		switch (item.getItemId())
		{
		case R.id.menu_refresh_teacher:
			initData();
			break;
		case R.id.menu_teacher_queue:
			queue();
			break;
		default:
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	private void queue()
	{
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
					Toast.makeText(getApplication(), "排队成功,当前名次为:" + resp.info.Rank, Toast.LENGTH_SHORT).show();
				}
			}

			@Override
			public void onFailure(HttpException error, String msg)
			{
				Toast.makeText(getApplication(), "排除失败", Toast.LENGTH_SHORT).show();
			}
		});
	}

	private void initData()
	{
		Parameters parameters = new Parameters();
		parameters.add("skip", 0 + "");
		parameters.add("take", 5 + "");
		HttpUtil.post(NetworkUtil.teacherInQueue, parameters, new RequestCallBack<String>()
		{

			@Override
			public void onSuccess(ResponseInfo<String> responseInfo)
			{
				Response<List<User>> response = new Gson().fromJson(responseInfo.result, new TypeToken<Response<List<User>>>()
				{}.getType());

				if (response.code == 200 && response.info != null)
				{
					List<User> users = response.info;
					list.clear();
					for (User user : users)
					{
						list.add(user);
					}
					adapter.notifyDataSetChanged();
				}
				Log.i("logi", "数据更新");
			}

			@Override
			public void onFailure(HttpException error, String msg)
			{}
		});
	}

	private void initView()
	{
		listview = (ListView) findViewById(R.id.listview);
		list = new ArrayList<User>();
		adapter = new MyAdapter(list);
		listview.setAdapter(adapter);
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
			final User user = list.get(position);
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
