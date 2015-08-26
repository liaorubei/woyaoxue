package com.newclass.woyaoxue;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.newclass.woyaoxue.bean.Level;
import com.newclass.woyaoxue.fragment.CategoryFragment;
import com.newclass.woyaoxue.util.NetworkUtil;
import com.viewpagerindicator.TitlePageIndicator;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;

public class MainActivity extends FragmentActivity
{
	@ViewInject(R.id.indicator)
	private TitlePageIndicator indicator;
	@ViewInject(R.id.viewPager)
	private ViewPager viewPager;

	private FragmentPagerAdapter mPagerAdapter;
	List<CategoryFragment> fragments;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		ViewUtils.inject(this);

		fragments = new ArrayList<CategoryFragment>();
		fragments.add(new CategoryFragment());
		fragments.add(new CategoryFragment());
		fragments.add(new CategoryFragment());
		
		mPagerAdapter = new MyPagerAdatper(getSupportFragmentManager());
	
		viewPager.setAdapter(mPagerAdapter);	
		indicator.setViewPager(viewPager);
	
		initData();
	}

	private void initData()
	{

		HttpUtils httpUtils = new HttpUtils();
		httpUtils.send(HttpMethod.GET, NetworkUtil.getLevels(), new RequestCallBack<String>()
		{

			@Override
			public void onSuccess(ResponseInfo<String> responseInfo)
			{
				try
				{
					JSONArray jsonArray = new JSONArray(responseInfo.result);
					List<Level> list = new ArrayList<Level>();
					for (int i = 0; i < jsonArray.length(); i++)
					{
						JSONObject jsonObject = jsonArray.getJSONObject(i);
						Level level = new Level(jsonObject.getInt("Id"), jsonObject.getString("LevelName"), jsonObject.getInt("DocCount"));
						list.add(level);
					}

					for (int i = 0; i < mPagerAdapter.getCount(); i++)
					{
						CategoryFragment cate = (CategoryFragment) mPagerAdapter.getItem(i);
						cate.fillData(list);
					}
					// mPagerAdapter.notifyDataSetChanged();
				}
				catch (Exception e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}

			@Override
			public void onFailure(HttpException error, String msg)
			{

			}
		});

	}
	
	

	private class MyPagerAdatper extends FragmentPagerAdapter
	{

		public MyPagerAdatper(FragmentManager manager)
		{
			super(manager);
		}

		@Override
		public Fragment getItem(int position)
		{

			return fragments.get(position);
		}

		@Override
		public int getCount()
		{

			return fragments.size();
		}

		@Override
		public CharSequence getPageTitle(int position)
		{

			return "分类" + position;
		}
	}

}
