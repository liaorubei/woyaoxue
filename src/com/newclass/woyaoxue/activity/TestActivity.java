package com.newclass.woyaoxue.activity;

import java.util.ArrayList;
import java.util.Arrays;
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
	private List<String> mDatas = Arrays.asList("短信1", "短信2", "短信3", "短信4", "短信5", "短信6", "短信7", "短信8", "短信9");
	// private List<String> mDatas = Arrays.asList("短信", "收藏", "推荐");

	private ViewPagerIndicator mIndicator;
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
		initDatas();
		// 设置Tab上的标题
		mIndicator.setTabItemTitles(mDatas);
		mViewPager.setAdapter(mAdapter);
		// 设置关联的ViewPager
		mIndicator.setViewPager(mViewPager, 0);

	}

	private void initData()
	{
		levels = new ArrayList<Level>();

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
					List<String> datas = new ArrayList<String>();
					for (Level level : fromJson)
					{
						datas.add(level.LevelName);
					}
					mIndicator.setTabItemTitles(datas);
				}
			}

			@Override
			public void onFailure(HttpException error, String msg)
			{
				// TODO Auto-generated method stub

			}
		});

	}

	private void initDatas()
	{

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
				return new DocsListFragment(NetworkUtil.getDocsByLevelId(levels.get(position).Id));
			}
		};
	}

	private void initView()
	{
		mViewPager = (ViewPager) findViewById(R.id.id_vp);
		mIndicator = (ViewPagerIndicator) findViewById(R.id.id_indicator);
	}
}
