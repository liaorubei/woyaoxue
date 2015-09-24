package com.newclass.woyaoxue.activity;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
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
import com.newclass.woyaoxue.bean.Level;
import com.newclass.woyaoxue.download.DownloadManager;
import com.newclass.woyaoxue.fragment.DocsListFragment;
import com.newclass.woyaoxue.util.NetworkUtil;
import com.newclass.woyaoxue.view.ViewPagerIndicator;
import com.voc.woyaoxue.R;

public class TestActivity extends FragmentActivity
{

	private ListView listview;
	private FragmentPagerAdapter mAdapter;
	private ViewPager mViewPager;

	private ArrayList<Level> levels;
	protected PagerAdapter pagerAdapter;
	protected ViewPagerIndicator indicator;
	private List<Document> list;
	private MyAdapter adapter;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.vp_indicator);

		listview = (ListView) findViewById(R.id.listview);
		list = new ArrayList<Document>();
		adapter = new MyAdapter(list);
		listview.setAdapter(adapter);

		new HttpUtils().send(HttpMethod.GET, NetworkUtil.getDocsByLevelId(1), new RequestCallBack<String>()
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

		public MyAdapter(List<Document> list)
		{
			super(list);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent)
		{
			final Document item = getItem(position);
			if (convertView == null)
			{
				convertView = View.inflate(TestActivity.this, R.layout.listitem_downloadable, null);
			}

			ImageView iv_download = (ImageView) convertView.findViewById(R.id.iv_download);
			iv_download.getBackground().setLevel(8);
			iv_download.setOnClickListener(new OnClickListener()
			{
				
				@Override
				public void onClick(View v)
				{
				if(	DownloadManager.con(item.SoundPath)){
					Toast.makeText(TestActivity.this, "已经在队列中", Toast.LENGTH_SHORT).show();
				}
					v.setTag(1);
					
					
				}
			});

			return convertView;
		}

		@Override
		public void update(Observable observable, Object data)
		{
			notifyDataSetChanged();
		}
	}
}
