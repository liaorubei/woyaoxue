package com.newclass.woyaoxue.activity;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.newclass.woyaoxue.base.BaseAdapter;
import com.newclass.woyaoxue.bean.Group;
import com.newclass.woyaoxue.bean.Response;
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
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

public class GroupActivity extends Activity implements OnClickListener
{
	private Button bt_create;
	private ListView listview;
	private List<Group> list;
	private MyAdapter adapter;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_group);

		initView();
		initData();
	}

	private void initData()
	{
		Parameters parameters = new Parameters();
		SharedPreferences sp = getSharedPreferences("user", MODE_PRIVATE);
		String accid = sp.getString("accid", "");
		parameters.add("accid", accid);
		HttpUtil.post(NetworkUtil.groupSelect, parameters, new RequestCallBack<String>()
		{

			@Override
			public void onSuccess(ResponseInfo<String> responseInfo)
			{
				Log.i("logi", "" + responseInfo.result);
				Response<List<Group>> fromJson = new Gson().fromJson(responseInfo.result, new TypeToken<Response<List<Group>>>()
				{
				}.getType());
				list.clear();
				if (fromJson.code == 200 && fromJson.info.size() > 0)
				{
					for (Group i : fromJson.info)
					{
						list.add(i);
					}
					adapter.notifyDataSetChanged();
				}
			}

			@Override
			public void onFailure(HttpException error, String msg)
			{

			}
		});

	}

	private void initView()
	{
		bt_create = (Button) findViewById(R.id.bt_create);
		bt_create.setOnClickListener(this);

		listview = (ListView) findViewById(R.id.listview);
		list = new ArrayList<Group>();
		adapter = new MyAdapter(list);

		listview.setAdapter(adapter);
		listview.setOnItemClickListener(new OnItemClickListener()
		{

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id)
			{
				Group group = list.get(position);
				Intent intent = new Intent(GroupActivity.this, AgoraChatActivity.class);
				intent.putExtra("channel", group.Id);
				startActivity(intent);
			}
		});
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		if (requestCode == GroupCreate.REQUEST_CODE && resultCode == GroupCreate.RESULT_CODE)
		{
			initData();
		}

		super.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	public void onClick(View v)
	{
		switch (v.getId())
		{
		case R.id.bt_create:
			startActivityForResult(new Intent(this, GroupCreate.class), GroupCreate.REQUEST_CODE);
			break;

		default:
			break;
		}

	}

	private class MyAdapter extends BaseAdapter<Group>
	{

		public MyAdapter(List<Group> list)
		{
			super(list);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent)
		{
			final Group group = list.get(position);

			View inflate = View.inflate(GroupActivity.this, R.layout.listitem_group, null);

			TextView tv_time = (TextView) inflate.findViewById(R.id.tv_time);
			TextView tv_host = (TextView) inflate.findViewById(R.id.tv_host);
			TextView tv_theme = (TextView) inflate.findViewById(R.id.tv_theme);

			tv_time.setText("");
			tv_host.setText(group.HostName);
			tv_theme.setText(group.Theme);

			inflate.findViewById(R.id.bt_accede).setOnClickListener(new OnClickListener()
			{

				@Override
				public void onClick(View v)
				{
					Intent intent = new Intent(GroupActivity.this, AgoraChatActivity.class);
					intent.putExtra("channel", group.Id);
					startActivity(intent);
				}
			});
			return inflate;
		}
	}
}
