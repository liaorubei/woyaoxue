package com.newclass.woyaoxue.fragment;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.newclass.woyaoxue.activity.PlayActivity;
import com.newclass.woyaoxue.base.BaseAdapter;
import com.newclass.woyaoxue.bean.Document;
import com.newclass.woyaoxue.util.NetworkUtil;
import com.voc.woyaoxue.R;

public class DocsListFragment extends Fragment
{
	private BaseAdapter<Document> adapter;

	private List<Document> documents;

	@ViewInject(R.id.listView)
	private ListView listView;
	@ViewInject(R.id.tv_none_data)
	private TextView tv_none_data;

	private String mFullPath;

	public DocsListFragment(String fullPath)
	{
		this.mFullPath = fullPath;
	}

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		if (documents == null)
		{
			documents = new ArrayList<Document>();
		}
		if (adapter == null)
		{
			adapter = new MyAdatper(getActivity(), R.layout.listitem_docslist, documents);
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		View inflate = inflater.inflate(R.layout.fragment_docslist, container, false);
		ViewUtils.inject(this, inflate);
		fillData();
		listView.setAdapter(adapter);
		listView.setOnItemClickListener(new OnItemClickListener()
		{
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id)
			{
				Log.i("logi", "onItemClick");
				Intent intent = new Intent(getActivity(), PlayActivity.class);
				intent.putExtra("Id", documents.get(position).Id);
				startActivity(intent);
			}
		});
		return inflate;
	}

	public void fillData()
	{
		Log.i("logi", "fillData" + this.mFullPath);
		new HttpUtils().send(HttpMethod.GET, this.mFullPath, new RequestCallBack<String>()
		{
			@Override
			public void onFailure(HttpException error, String msg)
			{
				Log.i("logi", "msg=" + msg);
			}

			@Override
			public void onSuccess(ResponseInfo<String> responseInfo)
			{
				List<Document> fromJson = new Gson().fromJson(responseInfo.result, new TypeToken<List<Document>>()
				{}.getType());
				if (fromJson != null)
				{
					documents.clear();
					documents.addAll(fromJson);
					adapter.notifyDataSetChanged();
					tv_none_data.setVisibility(View.GONE);
				}
			}
		});

	}

	public void fillData(String fullPath)
	{
		this.mFullPath = fullPath;
		fillData();
	}

	private class MyAdatper extends BaseAdapter<Document>
	{

		public MyAdatper(Context context, int resource, List<Document> objects)
		{
			super(context, resource, objects);
			// TODO Auto-generated constructor stub
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent)
		{
			final Document document = getItem(position);
			if (convertView == null)
			{
				convertView = View.inflate(getActivity(), R.layout.listitem_docslist, null);
				ViewHolder holder = new ViewHolder();
				holder.tv_title = (TextView) convertView.findViewById(R.id.tv_title);
				holder.tv_duration = (TextView) convertView.findViewById(R.id.tv_duration);
				holder.tv_size = (TextView) convertView.findViewById(R.id.tv_size);
				holder.ib_download = convertView.findViewById(R.id.ib_download);
				convertView.setTag(holder);
			}

			ViewHolder tag = (ViewHolder) convertView.getTag();
			tag.tv_title.setText(document.Title);
			tag.tv_duration.setText(document.Duration+"");
			tag.tv_size.setText(document.Size+"");
			tag.ib_download.setOnClickListener(new OnClickListener()
			{
				@Override
				public void onClick(View v)
				{
document.NeedDownLoad=true;

				}
			});
			
			tag.pb_download.setMax(100);
			tag.pb_download.setProgress(0);
			
			return convertView;
		}
	}

	private class ViewHolder
	{

		public View ib_download;
		public ProgressBar pb_download;
		public TextView tv_size;
		public TextView tv_duration;
		public TextView tv_title;
	}

}
