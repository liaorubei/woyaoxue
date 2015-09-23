package com.newclass.woyaoxue.activity;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;
import com.newclass.woyaoxue.base.BaseFragment;
import com.newclass.woyaoxue.bean.Document;
import com.newclass.woyaoxue.bean.Level;
import com.newclass.woyaoxue.fragment.DocsListFragment;
import com.newclass.woyaoxue.service.AutoUpdateService;
import com.newclass.woyaoxue.util.NetworkUtil;
import com.newclass.woyaoxue.view.ContentView;
import com.newclass.woyaoxue.view.ContentView.ViewState;
import com.newclass.woyaoxue.view.ViewPagerIndicator;
import com.voc.woyaoxue.R;

public class ListActivity extends FragmentActivity
{

	private ViewPagerIndicator indicator;
	private List<Level> levels;
	private FragmentPagerAdapter pagerAdapter;

	private ViewPager viewpager;
	protected PackageManager packageManager;
	private ContentView contentView;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		contentView = new ContentView(this)
		{

			@Override
			public View onCreateSuccessView()
			{
				View view = View.inflate(ListActivity.this, R.layout.activity_list, null);
				indicator = (ViewPagerIndicator) view.findViewById(R.id.indicator);
				viewpager = (ViewPager) view.findViewById(R.id.viewpager);
				return view;
			}
		};
		setContentView(contentView);
		sInitData();

		pagerAdapter = new MyPagerAdapter(getSupportFragmentManager());
		viewpager.setAdapter(pagerAdapter);
		indicator.setViewPager(viewpager, 0);

		// ActionBar
		getActionBar().setDisplayShowHomeEnabled(true);

		// 自动升级服务
		Intent service = new Intent(this, AutoUpdateService.class);
		startService(service);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		getMenuInflater().inflate(R.menu.list_activity, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		switch (item.getItemId())
		{
		case R.id.menu_download:
			Intent intent = new Intent(this, DownActivity.class);
			startActivity(intent);
			break;
		case R.id.menu_setting:
			Toast.makeText(this, "你点击了一个菜单", Toast.LENGTH_LONG).show();
			break;
		default:
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	private void sInitData()
	{
		levels = new ArrayList<Level>();

		new HttpUtils().send(HttpMethod.GET, NetworkUtil.getLevels(), new RequestCallBack<String>()
		{

			@Override
			public void onFailure(HttpException error, String msg)
			{
				contentView.showView(ViewState.FAILURE);
			}

			@Override
			public void onSuccess(ResponseInfo<String> responseInfo)
			{
				List<Level> fromJson = new Gson().fromJson(responseInfo.result, new TypeToken<List<Level>>()
				{}.getType());
				if (fromJson != null)
				{
					levels.addAll(fromJson);
					pagerAdapter.notifyDataSetChanged();
					indicator.refreshTitle();
				}
				contentView.showView(ViewState.SUCCESS);
			}
		});

	}

	private class MyPagerAdapter extends FragmentPagerAdapter
	{
		public MyPagerAdapter(FragmentManager fm)
		{
			super(fm);
		}

		@Override
		public int getCount()
		{

			return levels.size();
		}

		@Override
		public Fragment getItem(int position)
		{
			final BaseFragment<List<Document>> fragment = new DocsListFragment(NetworkUtil.getDocsByLevelId(levels.get(position).Id));
			new HttpUtils().send(HttpMethod.GET, NetworkUtil.getDocsByLevelId(levels.get(position).Id), new RequestCallBack<String>()
			{

				@Override
				public void onSuccess(ResponseInfo<String> responseInfo)
				{
					List<Document> json = new Gson().fromJson(responseInfo.result, new TypeToken<List<Document>>()
					{}.getType());

					fragment.onSuccess(json);
				}

				@Override
				public void onFailure(HttpException error, String msg)
				{
					fragment.onFailure();
				}
			});
			Log.i("logi", "getItem=" + position);
			return fragment;
		}

		@Override
		public CharSequence getPageTitle(int position)
		{
			return levels.get(position).LevelName;
		}
	}

}
