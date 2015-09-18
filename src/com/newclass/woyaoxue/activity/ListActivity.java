package com.newclass.woyaoxue.activity;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.newclass.woyaoxue.bean.Level;
import com.newclass.woyaoxue.fragment.DocsListFragment;
import com.newclass.woyaoxue.util.NetworkUtil;
import com.viewpagerindicator.TabPageIndicator;
import com.voc.woyaoxue.R;

public class ListActivity extends FragmentActivity
{
	@ViewInject(R.id.indicator)
	private TabPageIndicator indicator;
	private List<Level> levels;
	private FragmentPagerAdapter pagerAdapter;
	@ViewInject(R.id.viewpager)
	private ViewPager viewpager;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_list);
		ViewUtils.inject(this);

		sInitData();

		pagerAdapter = new MyPagerAdapter(getSupportFragmentManager());

		viewpager.setAdapter(pagerAdapter);
		indicator.setViewPager(viewpager);

		getActionBar().setDisplayShowHomeEnabled(true);

		// 如果没有长度,平分
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
				// TODO Auto-generated method stub

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
					indicator.notifyDataSetChanged();
				}
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
			Fragment fragment = new DocsListFragment(NetworkUtil.getDocsByLevelId(levels.get(position).Id));
			return fragment;
		}

		@Override
		public CharSequence getPageTitle(int position)
		{
			return levels.get(position).LevelName;
		}
	}

}
