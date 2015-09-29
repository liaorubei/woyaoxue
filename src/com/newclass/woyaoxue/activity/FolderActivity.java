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
import com.newclass.woyaoxue.bean.Document;
import com.newclass.woyaoxue.bean.Level;
import com.newclass.woyaoxue.fragment.DocsListFragment;
import com.newclass.woyaoxue.fragment.FolderFragment;
import com.newclass.woyaoxue.service.AutoUpdateService;
import com.newclass.woyaoxue.util.ConstantsUtil;
import com.newclass.woyaoxue.util.NetworkUtil;
import com.newclass.woyaoxue.view.ContentView;
import com.newclass.woyaoxue.view.ContentView.ViewState;
import com.voc.woyaoxue.R;

public class FolderActivity extends FragmentActivity
{
	// #3498db #95a5a6
	private LinearLayout tabLayout;
	private List<Level> showLevels;
	private List<Level> hideLevels;

	private FragmentPagerAdapter pagerAdapter;

	private ViewPager viewpager;
	private ContentView contentView;

	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		contentView = new ContentView(this)
		{

			@Override
			public View onCreateSuccessView()
			{
				View view = View.inflate(FolderActivity.this, R.layout.activity_folder, null);
				tabLayout = (LinearLayout) view.findViewById(R.id.ll_tablayout);
				viewpager = (ViewPager) view.findViewById(R.id.viewpager);
				return view;
			}

			@Override
			public void initData()
			{
				// TODO Auto-generated method stub

			}
		};
		setContentView(contentView);
		sInitData();

		pagerAdapter = new MyPagerAdapter(getSupportFragmentManager());
		viewpager.setAdapter(pagerAdapter);

		viewpager.setOnPageChangeListener(new OnPageChangeListener()
		{

			@Override
			public void onPageSelected(int position)
			{
				for (int i = 0; i < tabLayout.getChildCount(); i++)
				{
					TextView view = (TextView) tabLayout.getChildAt(i);
					view.setTextColor(i == position ? ConstantsUtil.ColorOne : ConstantsUtil.ColorTwo);
				}
			}

			@Override
			public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels)
			{

			}

			@Override
			public void onPageScrollStateChanged(int state)
			{

			}
		});

		// ActionBar
		getActionBar().setDisplayShowHomeEnabled(true);

		// 自动升级服务
		Intent service = new Intent(this, AutoUpdateService.class);
		startService(service);

		// PopupWindow popupWindow=new PopupWindow();

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
		showLevels = new ArrayList<Level>();

		hideLevels = new ArrayList<Level>();

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
				if (fromJson.size() > 0)
				{
					// 要求按Level的权重(排序sort)顺序显示,权重越大,越前面
					Comparator<Level> comparator = new Comparator<Level>()
					{

						@Override
						public int compare(Level lhs, Level rhs)
						{
							return Integer.valueOf(rhs.Sort).compareTo(lhs.Sort);
						}
					};
					Collections.sort(fromJson, comparator);
					// 全部的Level目前有两种状态,一种是不要显示在界面的(即在MORE按钮下),别一种是要求在界面显示
					for (Level level : fromJson)
					{
						if (level.Show == 1)
						{
							showLevels.add(level);
						}
						else if (level.Show == 0)
						{
							hideLevels.add(level);
						}
					}
					pagerAdapter.notifyDataSetChanged();

					// 刷新或者初始化TabTitle
					refreshTab();

				}
				contentView.showView(ViewState.SUCCESS);
			}
		});

	}

	/**
	 * 通过网络获取的Level列表来填充TabTitle和MORE按钮的数据
	 */
	protected void refreshTab()
	{

		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, 1);
		// TabTitle
		for (int i = 0; i < showLevels.size(); i++)
		{
			final int item = i;
			TextView tabsView = new TextView(this);
			tabsView.setGravity(Gravity.CENTER);
			tabsView.setText(showLevels.get(i).LevelName);
			tabsView.setBackgroundResource(R.drawable.selector_levels);
			tabsView.setTextColor(i == 0 ? ConstantsUtil.ColorOne : ConstantsUtil.ColorTwo);
			tabsView.setOnClickListener(new OnClickListener()
			{
				@Override
				public void onClick(View v)
				{
					viewpager.setCurrentItem(item);
				}
			});

			tabLayout.addView(tabsView, params);
		}

		// More按钮
		TextView moreView = new TextView(this);
		moreView.setText("MORE");
		moreView.setGravity(Gravity.CENTER);
		moreView.setTextColor(ConstantsUtil.ColorTwo);
		moreView.setBackgroundResource(R.drawable.selector_levels);
		moreView.setOnClickListener(new OnClickListener()
		{

			@Override
			public void onClick(View v)
			{
				Intent intent = new Intent(FolderActivity.this, MoreActivity.class);
				String json = new Gson().toJson(hideLevels);
				intent.putExtra("hideLevels", json);
				startActivity(intent);
			}
		});
		tabLayout.addView(moreView, params);
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
			return showLevels.size();
		}

		@Override
		public Fragment getItem(int position)
		{
			BaseFragment fragment = new FolderFragment(showLevels.get(position).Id);
			//fragment = new DocsListFragment(NetworkUtil.getDocsByLevelId(showLevels.get(position).Id));
			fragment.initData();
			return fragment;
		}
	}

}
