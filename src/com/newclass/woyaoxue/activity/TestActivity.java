package com.newclass.woyaoxue.activity;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ExpandableListView.OnGroupClickListener;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;
import com.newclass.woyaoxue.base.BaseAdapter;
import com.newclass.woyaoxue.bean.Document;
import com.newclass.woyaoxue.util.Log;
import com.newclass.woyaoxue.view.CircularProgressBar;
import com.newclass.woyaoxue.view.FylxListView;
import com.voc.woyaoxue.R;

public class TestActivity extends FragmentActivity
{

	private List<Document> list;
	private MyAdapter adapter;
	private FylxListView listview;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_test);
		// listView.getFirstVisiblePosition();

		listview = (FylxListView) findViewById(R.id.listview);
		list = new ArrayList<Document>();
		adapter = new MyAdapter(list);
		listview.setAdapter(adapter);

		new HttpUtils().send(HttpMethod.GET, "http://voc2015.azurewebsites.net/NewClass/GetDocs?skip=0&take=5", new RequestCallBack<String>()
		{

			@Override
			public void onSuccess(ResponseInfo<String> responseInfo)
			{
				List<Document> fromJson = new Gson().fromJson(responseInfo.result, new TypeToken<List<Document>>()
				{}.getType());

				if (fromJson.size() > 0)
				{
					list.addAll(fromJson);
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

	private class MyAdapter extends BaseAdapter<Document> implements Observer
	{
		private Map<String, Document> downloadingList;

		public MyAdapter(List<Document> list)
		{
			super(list);
			// TODO Auto-generated constructor stub
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent)
		{
			Document document = list.get(position);
			if (convertView == null)
			{
				convertView = View.inflate(TestActivity.this, R.layout.listitem_docslist, null);
				ViewHolder holder = new ViewHolder();
				holder.tv_title_one = (TextView) convertView.findViewById(R.id.tv_title_one);

				convertView.setTag(holder);
			}

			ViewHolder holder = (ViewHolder) convertView.getTag();
			holder.tv_title_one.setText(document.Title);

			convertView.setOnClickListener(new OnClickListener()
			{

				@Override
				public void onClick(View v)
				{
					// TODO Auto-generated method stub

				}
			});

			return convertView;
		}

		@Override
		public void update(Observable observable, Object data)
		{
			downloadingList = (Map<String, Document>) data;
			notifyDataSetChanged();
		}
	}

	private class ViewHolder
	{

		public TextView tv_title_one;

	}
}
