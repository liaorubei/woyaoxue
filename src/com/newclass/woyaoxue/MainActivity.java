package com.newclass.woyaoxue;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.google.gson.Gson;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.newclass.woyaoxue.bean.Level;
import com.newclass.woyaoxue.bean.UpgradePatch;
import com.newclass.woyaoxue.fragment.CategoryFragment;
import com.newclass.woyaoxue.util.NetworkUtil;
import com.viewpagerindicator.TabPageIndicator;
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
	private TabPageIndicator indicator;
	@ViewInject(R.id.viewPager)
	private ViewPager viewPager;

	private FragmentPagerAdapter mPagerAdapter;
	List<CategoryFragment> fragments;
	private PackageManager packageManager;
	private AlertDialog alertDialog;

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

		new HttpUtils().send(HttpMethod.GET, "http://www.baidu.com", new RequestCallBack<String>()
		{

			@Override
			public void onSuccess(ResponseInfo<String> responseInfo)
			{
				Log.i("logi", "onSuccess");
				try
				{

					// 解析
					UpgradePatch upgradePatch = new UpgradePatch();// new Gson().fromJson(responseInfo.result, UpgradePatch.class);
					packageManager = getPackageManager();
					PackageInfo packageInfo = packageManager.getPackageInfo(getPackageName(), PackageManager.GET_CONFIGURATIONS);
					Log.i("logi", " versionCode=" + packageInfo.versionCode + " versionName=" + packageInfo.versionName + " packageName=" + packageInfo.packageName);

					if (!packageInfo.versionName.equals(upgradePatch.VersionName))
					{
						Builder builder = new AlertDialog.Builder(MainActivity.this);
						builder.setTitle(R.string.upgrade_tips);

						builder.setMessage("发现了新的版本\r\n文件大小:4.5MB\r\n你需要更新吗?\r\n更新日志");
						builder.setNegativeButton(R.string.negative_text, new OnClickListener()
						{

							@Override
							public void onClick(DialogInterface dialog, int which)
							{

							}
						});
						builder.setPositiveButton(R.string.positive_text, new OnClickListener()
						{

							@Override
							public void onClick(DialogInterface dialog, int which)
							{
								// 开始下载

							}
						});

						alertDialog = builder.show();

					}
				}
				catch (Exception e)
				{

					e.printStackTrace();
				}

			}

			@Override
			public void onFailure(HttpException error, String msg)
			{
				Log.i("logi", "连网失败");
			}
		});
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
