package com.newclass.woyaoxue.activity;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
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
import com.newclass.woyaoxue.bean.Level;
import com.newclass.woyaoxue.fragment.DocsListFragment;
import com.newclass.woyaoxue.util.NetworkUtil;
import com.viewpagerindicator.TabPageIndicator;
import com.voc.woyaoxue.R;

public class ListActivity extends FragmentActivity
{
	private static final String[] TITLE = new String[] { "分类1", "分类2", "分类3", "分类4" };
	@ViewInject(R.id.viewpager)
	private ViewPager viewpager;
	@ViewInject(R.id.indicator)
	private TabPageIndicator indicator;
	private List<Level> levels;
	private FragmentPagerAdapter pagerAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_list);
		ViewUtils.inject(this);

		initData();

		pagerAdapter = new MyPagerAdapter(getSupportFragmentManager());

		viewpager.setAdapter(pagerAdapter);
		indicator.setViewPager(viewpager);
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
					pagerAdapter.notifyDataSetChanged();
					indicator.notifyDataSetChanged();
				}
			}

			@Override
			public void onFailure(HttpException error, String msg)
			{
				// TODO Auto-generated method stub

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

		@Override
		public int getCount()
		{

			return levels.size();
		}
	}

}
