package com.newclass.woyaoxue.activity;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
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
import com.newclass.woyaoxue.service.DownloadService;
import com.newclass.woyaoxue.service.DownloadService.DownloadManager;
import com.newclass.woyaoxue.service.DownloadService.MyBinder;
import com.newclass.woyaoxue.util.Log;
import com.newclass.woyaoxue.view.FylxListView;
import com.voc.woyaoxue.R;

public class TestActivity extends FragmentActivity
{

	private MyAdapter adapter;
	private ServiceConnection conn;
	private List<Document> list;
	private FylxListView listview;
	private DownloadService.MyBinder myBinder;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_test);

		conn = new MyServiceConnection();
		bindService(new Intent(this, DownloadService.class), conn, Service.BIND_AUTO_CREATE);

		listview = (FylxListView) findViewById(R.id.listview);
		list = new ArrayList<Document>();
		adapter = new MyAdapter(list);
		listview.setAdapter(adapter);

		new HttpUtils().send(HttpMethod.GET, "http://voc2015.azurewebsites.net/NewClass/GetDocs?skip=0&take=5", new RequestCallBack<String>()
		{

			@Override
			public void onFailure(HttpException error, String msg)
			{

			}

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
		});

	}

	@Override
	protected void onDestroy()
	{

		super.onDestroy();
		unbindService(conn);
	}

	private class MyAdapter extends BaseAdapter<Document> implements Observer
	{
		public MyAdapter(List<Document> list)
		{
			super(list);

		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent)
		{
			final Document document = list.get(position);
			if (convertView == null)
			{
				convertView = View.inflate(TestActivity.this, R.layout.listitem_docslist, null);
				ViewHolder holder = new ViewHolder();
				holder.tv_title_one = (TextView) convertView.findViewById(R.id.tv_title_one);

				convertView.setTag(holder);
			}

			ViewHolder holder = (ViewHolder) convertView.getTag();
			holder.tv_title_one.setText(document.Title);

			return convertView;
		}

		@Override
		public void update(Observable observable, Object data)
		{
			notifyDataSetChanged();
		}
	}

	private class MyServiceConnection implements ServiceConnection
	{

		@Override
		public void onServiceConnected(ComponentName name, IBinder service)
		{
			Log.i("onServiceConnected");
			myBinder = (MyBinder) service;
		}

		@Override
		public void onServiceDisconnected(ComponentName name)
		{

		}
	}

	private class ViewHolder
	{

		public TextView tv_title_one;

	}

}
