package com.newclass.woyaoxue.activity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
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
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

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
import com.newclass.woyaoxue.bean.database.UrlCache;
import com.newclass.woyaoxue.fragment.FolderFragment;
import com.newclass.woyaoxue.service.AutoUpdateService;
import com.newclass.woyaoxue.util.ConstantsUtil;
import com.newclass.woyaoxue.util.Log;
import com.newclass.woyaoxue.util.NetworkUtil;
import com.voc.woyaoxue.R;

public class HomeActivity extends FragmentActivity
{
	private MyFragmentPagerAdapter adapter;

	private List<BaseFragment> fragments;
	private LinearLayout ll_levels;

	private ViewPager vp_folder;

	protected Database database;

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
			Intent intent = new Intent(this, DownFolderActivity.class);
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

	@Override
	protected void onDestroy()
	{

		super.onDestroy();
		database.closeConnection();
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

			}

			@Override
			public void onPageScrollStateChanged(int arg0)
			{

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
		database = new Database(HomeActivity.this);
		loadData();

		// ActionBar
		getActionBar().setDisplayShowHomeEnabled(true);

		// 自动升级服务
		Intent service = new Intent(this, AutoUpdateService.class);
		startService(service);
	}

	private void loadData()
	{
		String requestUrl = NetworkUtil.getLevels();
		UrlCache cache = database.cacheSelectByUrl(requestUrl);
		if (cache == null || (System.currentTimeMillis() - cache.UpdateAt > 600000))
		{
			Log.i("使用网络数据");
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
						fillData(fromJson);

						// 保存等级信息
						for (Level level : fromJson)
						{
							if (!database.levelExists(level.Id))
							{
								database.levelInsert(level);
							}
						}
					}

					// 把数据缓存到数据库里面
					UrlCache urlCache = new UrlCache();
					urlCache.Url = this.getRequestUrl();
					urlCache.Json = responseInfo.result;
					urlCache.UpdateAt = System.currentTimeMillis();
					database.cacheInsertOrUpdate(urlCache);
				}

			});
		}
		else
		{
			Log.i("使用缓存数据");

			List<Level> json = new Gson().fromJson(cache.Json, new TypeToken<List<Level>>()
			{}.getType());

			if (json.size() > 0)
			{
				fillData(json);
			}
		}

	}

	/**
	 * @param json
	 */
	private void fillData(List<Level> json)
	{
		// 排序
		Collections.sort(json, new Comparator<Level>()
		{

			@Override
			public int compare(Level lhs, Level rhs)
			{
				return Integer.valueOf(rhs.Sort).compareTo(lhs.Sort);
			}
		});

		// ViewPager数据源
		fragments.clear();
		for (Level level : json)
		{
			fragments.add(new FolderFragment(level.Id));
		}
		adapter.notifyDataSetChanged();

		// tabs数据源
		ll_levels.removeAllViews();
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, 1);
		for (int i = 0; i < json.size(); i++)
		{
			final int item = i;
			TextView child = new TextView(HomeActivity.this);
			child.setGravity(Gravity.CENTER);
			child.setText(json.get(i).Name);
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
			baseFragment.initData(HomeActivity.this);
			return baseFragment;
		}
	}
}
