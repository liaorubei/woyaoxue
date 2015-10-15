package com.newclass.woyaoxue.activity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;
import com.newclass.woyaoxue.base.BaseFragment;
import com.newclass.woyaoxue.bean.Level;
import com.newclass.woyaoxue.bean.database.Database;
import com.newclass.woyaoxue.fragment.FolderFragment;
import com.newclass.woyaoxue.service.AutoUpdateService;
import com.newclass.woyaoxue.util.ConstantsUtil;
import com.newclass.woyaoxue.util.NetworkUtil;
import com.newclass.woyaoxue.view.ContentView;
import com.newclass.woyaoxue.view.ContentView.ViewState;
import com.voc.woyaoxue.R;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Telephony.Sms.Conversations;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class HomeActivity extends FragmentActivity
{
	private MyFragmentPagerAdapter adapter;

	private List<BaseFragment> fragments;
	private LinearLayout ll_levels;

	private ViewPager vp_folder;

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
			Intent intent = new Intent(this, FolderDownActivity.class);
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

	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home);
		ll_levels = (LinearLayout) findViewById(R.id.ll_levels);
		vp_folder = (ViewPager) findViewById(R.id.vp_folder);

		fragments = new ArrayList<BaseFragment>();
		adapter = new MyFragmentPagerAdapter(getSupportFragmentManager());

		vp_folder.setAdapter(adapter);
		vp_folder.setOnPageChangeListener(new OnPageChangeListener()
		{

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2)
			{
				// TODO Auto-generated method stub

			}

			@Override
			public void onPageScrollStateChanged(int arg0)
			{
				// TODO Auto-generated method stub

			}

			@Override
			public void onPageSelected(int arg0)
			{
				for (int i = 0; i < ll_levels.getChildCount(); i++)
				{
					TextView childAt = (TextView) ll_levels.getChildAt(i);
					childAt.setTextColor(i == arg0 ? ConstantsUtil.ColorOne : ConstantsUtil.ColorTwo);
				}
			}
		});

		new HttpUtils().send(HttpMethod.GET, NetworkUtil.getLevels(), new RequestCallBack<String>()
		{

			@Override
			public void onFailure(HttpException error, String msg)
			{}

			@Override
			public void onSuccess(ResponseInfo<String> responseInfo)
			{
				List<Level> fromJson = new Gson().fromJson(responseInfo.result, new TypeToken<List<Level>>()
				{}.getType());
				if (fromJson.size() > 0)
				{
					// 排序
					Collections.sort(fromJson, new Comparator<Level>()
					{

						@Override
						public int compare(Level lhs, Level rhs)
						{
							return Integer.valueOf(rhs.Sort).compareTo(lhs.Sort);
						}
					});

					// ViewPager数据源
					for (Level level : fromJson)
					{
						fragments.add(new FolderFragment(level.Id));
					}
					adapter.notifyDataSetChanged();

					// tabs数据源
					LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, 1);
					for (int i = 0; i < fromJson.size(); i++)
					{
						final int item = i;
						TextView child = new TextView(HomeActivity.this);
						child.setGravity(Gravity.CENTER);
						child.setText(fromJson.get(i).Name);
						child.setBackgroundResource(R.drawable.selector_levels);
						child.setTextColor(i == 0 ? ConstantsUtil.ColorOne : ConstantsUtil.ColorTwo);

						child.setOnClickListener(new OnClickListener()
						{

							@Override
							public void onClick(View v)
							{
								vp_folder.setCurrentItem(item);
							}
						});

						ll_levels.addView(child, params);
					}

					// 保存等级数据在数据库
					Database database = new Database(HomeActivity.this);
					for (Level level : fromJson)
					{
						if (!database.levelExists(level.Id))
						{
							database.levelInsert(level);
						}
					}

				}
			}
		});

		// ActionBar
		getActionBar().setDisplayShowHomeEnabled(true);

		// 自动升级服务
		Intent service = new Intent(this, AutoUpdateService.class);
		startService(service);
	}

	private class MyFragmentPagerAdapter extends FragmentPagerAdapter
	{

		public MyFragmentPagerAdapter(FragmentManager fm)
		{
			super(fm);
		}

		@Override
		public int getCount()
		{
			return fragments.size();
		}

		@Override
		public Fragment getItem(int arg0)
		{
			BaseFragment baseFragment = fragments.get(arg0);
			baseFragment.initData();
			return baseFragment;
		}
	}
}
