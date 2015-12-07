package com.newclass.woyaoxue.activity;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;
import com.newclass.woyaoxue.base.BaseAdapter;
import com.newclass.woyaoxue.bean.Answer;
import com.newclass.woyaoxue.bean.Answer.People;
import com.newclass.woyaoxue.util.Log;
import com.newclass.woyaoxue.util.NetworkUtil;
import com.voc.woyaoxue.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;

public class ContactActivity extends Activity
{
	private ListView listview;
	private List<People> list;
	private MyAdapter adapter;
	private String accid;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		accid = getIntent().getStringExtra("accid");
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_contact);

		listview = (ListView) findViewById(R.id.listview);
		list = new ArrayList<People>();
		adapter = new MyAdapter(list);
		listview.setAdapter(adapter);
		listview.setOnItemClickListener(new OnItemClickListener()
		{

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id)
			{
				list.get(position);
				Intent intent = new Intent(ContactActivity.this, MessageActivity.class);
				intent.putExtra("target", list.get(position).AccId);
				startActivity(intent);
			}
		});

		new HttpUtils().send(HttpMethod.POST, NetworkUtil.userSearch, new RequestCallBack<String>()
		{

			@Override
			public void onSuccess(ResponseInfo<String> responseInfo)
			{
				Answer answer = new Gson().fromJson(responseInfo.result, Answer.class);

				if (200 == answer.code)
				{
					List<People> others = answer.info.others;
					for (People people : others)
					{
						if (!people.AccId.equals(accid))
						{
							list.add(people);
						}
						else
						{
							Log.i("logi", "accid=" + accid);
						}
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

	private class MyAdapter extends BaseAdapter<People>
	{

		public MyAdapter(List<People> list)
		{
			super(list);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent)
		{
			People item = getItem(position);
			TextView textView = (TextView) View.inflate(ContactActivity.this, R.layout.listitem_listactivity, null);
			textView.setText(item.NickName);
			return textView;
		}
	}
}
