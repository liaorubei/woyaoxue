package com.newclass.woyaoxue.activity;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.newclass.woyaoxue.base.BaseAdapter;
import com.newclass.woyaoxue.bean.Response;
import com.newclass.woyaoxue.bean.User;
import com.newclass.woyaoxue.util.HttpUtil;
import com.newclass.woyaoxue.util.HttpUtil.Parameters;
import com.newclass.woyaoxue.util.NetworkUtil;
import com.voc.woyaoxue.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

public class FriendActivity extends Activity
{
	private List<User> list;
	private MyAdapter adapter;
	private ListView listview;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_friend);

		initView();
		// initData();
	}

	private void initData()
	{
		Parameters parameters = new Parameters();
		HttpUtil.post(NetworkUtil.teacherInQueue, parameters, new RequestCallBack<String>()
		{

			@Override
			public void onSuccess(ResponseInfo<String> responseInfo)
			{
				Response<List<User>> response = new Gson().fromJson(responseInfo.result, new TypeToken<Response<List<User>>>()
				{
				}.getType());
				if (response.code == 200 && response.info != null)
				{
					List<User> users = response.info;
					for (User user : users)
					{
						list.add(user);
					}
					adapter.notifyDataSetChanged();
				}

			}

			@Override
			public void onFailure(HttpException error, String msg)
			{
				// TODO Auto-generated method stub

			}
		});
	}

	private void initView()
	{
		listview = (ListView) findViewById(R.id.listview);
		list = new ArrayList<User>();
		list.add(new User());
		list.add(new User());
		list.add(new User());
		list.add(new User());
		list.add(new User());
		adapter = new MyAdapter(list);
		listview.setAdapter(adapter);

	}

	private class MyAdapter extends BaseAdapter<User>
	{

		public MyAdapter(List<User> list)
		{
			super(list);
			// TODO Auto-generated constructor stub
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent)
		{
			final User user = list.get(position);
			View inflate = View.inflate(FriendActivity.this, R.layout.listitem_friend, null);
			TextView tv_nickname = (TextView) inflate.findViewById(R.id.tv_nickname);
			tv_nickname.setText(user.NickName);

			Button bt_call = (Button) inflate.findViewById(R.id.bt_call);
			bt_call.setOnClickListener(new OnClickListener()
			{

				@Override
				public void onClick(View v)
				{
					Intent intent = new Intent(FriendActivity.this, LiveChatActivity.class);
					intent.putExtra("target", user.Accid);
					intent.putExtra(LiveChatActivity.CHATSTATE_KEY, LiveChatActivity.CHATSTATE_CALL);
					startActivity(intent);

				}
			});

			return inflate;
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		getMenuInflater().inflate(R.menu.friend_activity, menu);
		return true;
	}
}
