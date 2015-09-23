package com.newclass.woyaoxue.activity;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;
import com.newclass.woyaoxue.bean.Level;
import com.newclass.woyaoxue.fragment.DocsListFragment;
import com.newclass.woyaoxue.util.NetworkUtil;
import com.newclass.woyaoxue.view.ViewPagerIndicator;
import com.voc.woyaoxue.R;

public class TestActivity extends FragmentActivity
{

	private FragmentPagerAdapter mAdapter;
	private ViewPager mViewPager;
	
	private ArrayList<Level> levels;
	protected PagerAdapter pagerAdapter;
	protected ViewPagerIndicator indicator;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.vp_indicator);

		initView();
		initData();

		// 设置Tab上的标题
		mViewPager.setAdapter(mAdapter);
		// 设置关联的ViewPager
		// mIndicator.setViewPager(mViewPager);

	}

	private void initData()
	{
		levels = new ArrayList<Level>();

		mAdapter = new FragmentPagerAdapter(getSupportFragmentManager())
		{
			@Override
			public int getCount()
			{
				return levels.size();
			}

			@Override
			public Fragment getItem(int position)
			{
				return new DocsListFragment("");
			}

			@Override
			public CharSequence getPageTitle(int position)
			{
				return levels.get(position).LevelName;
			}
		};

		new HttpUtils().send(HttpMethod.GET, NetworkUtil.getLevels(), new RequestCallBack<String>()
		{

			@Override
			public void onSuccess(ResponseInfo<String> responseInfo)
			{
				List<Level> fromJson = new Gson().fromJson(responseInfo.result, new TypeToken<List<Level>>()
				{}.getType());
				if (fromJson != null)
				{
					levels.clear();
					levels.addAll(fromJson);
					mAdapter.notifyDataSetChanged();
				
				}
			}

			@Override
			public void onFailure(HttpException error, String msg)
			{
				// TODO Auto-generated method stub

			}
		});

	}

	private void initView()
	{
		mViewPager = (ViewPager) findViewById(R.id.id_vp);
	}
}
