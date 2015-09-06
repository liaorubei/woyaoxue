package com.newclass.woyaoxue.activity;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.HttpHandler;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.voc.woyaoxue.R;
import com.newclass.woyaoxue.base.BaseAdapter;
import com.newclass.woyaoxue.bean.Document;
import com.newclass.woyaoxue.util.NetworkUtil;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class ListActivity extends Activity implements OnItemClickListener
{
	@ViewInject(R.id.listView)
	private ListView listView;

	private List<Document> objects;
	private MyAdapter adapter;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_list);
		int levelId = getIntent().getIntExtra("levelId", 0);

		ViewUtils.inject(this);

		objects = new ArrayList<Document>();
		adapter = new MyAdapter(ListActivity.this, R.layout.listitem_listactivity, objects);

		listView.setAdapter(adapter);

		listView.setOnItemClickListener(this);

		String url = NetworkUtil.getDocsByLevelId(levelId);
		Log.i("logi", "url=" + url);
		new HttpUtils().send(HttpMethod.GET, url, new RequestCallBack<String>()
		{

			@Override
			public void onSuccess(ResponseInfo<String> responseInfo)
			{
				List<Document> list = new Gson().fromJson(responseInfo.result, new TypeToken<List<Document>>()
				{}.getType());

				for (Document document : list)
				{
					objects.add(document);
				}
				adapter.notifyDataSetChanged();
			}

			@Override
			public void onFailure(HttpException error, String msg)
			{
				// TODO Auto-generated method stub

			}
		});

	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id)
	{
		Document document = objects.get(position);

		Intent intent = new Intent(ListActivity.this, PlayActivity.class);
		intent.putExtra("Id", document.Id);
		intent.putExtra("Title", document.Title);
		intent.putExtra("SoundPath", document.SoundPath);
		startActivity(intent);
	}

	private class MyAdapter extends ArrayAdapter<Document>
	{

		private int mResource;

		public MyAdapter(Context context, int resource, List<Document> objects)
		{
			super(context, resource, objects);
			this.mResource = resource;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent)
		{
			Document item = getItem(position);
			if (convertView == null)
			{
				convertView = View.inflate(getApplicationContext(), mResource, null);
			}
			((TextView) convertView).setText(item.Title);
			return convertView;
		}

	}

}
