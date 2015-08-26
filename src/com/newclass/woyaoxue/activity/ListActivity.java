package com.newclass.woyaoxue.activity;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.HttpHandler;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.newclass.woyaoxue.R;
import com.newclass.woyaoxue.bean.Document;
import com.newclass.woyaoxue.util.NetworkUtil;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class ListActivity extends Activity
{
	@ViewInject(R.id.listView)
	private ListView listView;

	private List<String> objects;
	private ArrayAdapter<String> adapter;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_list);
		int levelId = getIntent().getIntExtra("levelId", 0);
		Log.i("logi", "levelId===" + levelId);
		ViewUtils.inject(this);

		objects = new ArrayList<String>();
		adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, objects);
		listView.setAdapter(adapter);

		String url = NetworkUtil.getDocsById(levelId);
		Log.i("logi", url);
		HttpHandler<String> send = new HttpUtils().send(HttpMethod.GET, url, new RequestCallBack<String>()
		{

			@Override
			public void onSuccess(ResponseInfo<String> responseInfo)
			{
				try
				{
					JSONArray array = new JSONArray(responseInfo.result);
					for (int i = 0; i < array.length(); i++)
					{
						JSONObject object = array.getJSONObject(i);
						objects.add(object.getString("Title"));
						Log.i("logi", "" + object.getString("SoundPath"));
					}
					adapter.notifyDataSetChanged();
				}
				catch (Exception e)
				{
					// TODO: handle exception
				}
			}

			@Override
			public void onFailure(HttpException error, String msg)
			{
				// TODO Auto-generated method stub

			}
		});

	}

}
