package com.newclass.woyaoxue.view;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.newclass.woyaoxue.bean.Level;
import com.newclass.woyaoxue.bean.Response;
import com.newclass.woyaoxue.fragment.FolderFragment;
import com.newclass.woyaoxue.fragment.FragmentDownload;
import com.newclass.woyaoxue.util.HttpUtil;
import com.newclass.woyaoxue.util.NetworkUtil;
import com.newclass.woyaoxue.view.LazyViewPager.OnPageChangeListener;
import com.voc.woyaoxue.R;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;

public class ContentViewListen extends ContentView
{
	private MyAdapter adapter;
	private List<ContentView> contentViews = new ArrayList<ContentView>();
	private List<Fragment> fragments = new ArrayList<Fragment>();
	private Gson gson = new Gson();
	private List<Level> levels = new ArrayList<Level>();
	private RadioGroup ll_levels;

	private LazyViewPager viewpager;
	private FragmentManager fragmentManager;

	public ContentViewListen(Context context, FragmentManager fm)
	{
		super(context);
		this.fragmentManager = fm;
		initData();
	}

	public void initData()
	{
		// 由于FragmentPagerAdapter要求使用兼容包的FragmentManager,所以相关设置的代码不放onCreateSuccessView
		adapter = new MyAdapter(this.fragmentManager);
		viewpager.setAdapter(adapter);
		viewpager.setOnPageChangeListener(new OnPageChangeListener()
		{

			@Override
			public void onPageSelected(int position)
			{
				RadioButton childAt = (RadioButton) ll_levels.getChildAt(position);
				childAt.setChecked(true);

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

		HttpUtil.post(NetworkUtil.levelSelect, null, new RequestCallBack<String>()
		{

			@Override
			public void onFailure(HttpException error, String msg)
			{
				showView(ViewState.FAILURE);
			}

			@Override
			public void onSuccess(ResponseInfo<String> responseInfo)
			{
				Response<List<Level>> resp = gson.fromJson(responseInfo.result, new TypeToken<Response<List<Level>>>()
				{}.getType());
				if (resp.code == 200)
				{
					RadioGroup.LayoutParams params = new RadioGroup.LayoutParams(0, RadioGroup.LayoutParams.MATCH_PARENT, 1);
					levels = resp.info;
					Collections.sort(levels, new Comparator<Level>()
					{

						@Override
						public int compare(Level lhs, Level rhs)
						{
							return Integer.valueOf(rhs.Sort).compareTo(lhs.Sort);
						}
					});

					int[][] states = new int[2][];
					states[0] = new int[] { android.R.attr.state_pressed, android.R.attr.state_checked };
					states[0] = new int[] { android.R.attr.state_checked };
					states[1] = new int[] {};
					int[] colorss = new int[] { Color.parseColor("#3498db"), Color.parseColor("#95a5a6"), Color.parseColor("#95a5a6") };
					ColorStateList colors = new ColorStateList(states, colorss);
					for (int i = 0; i < levels.size(); i++)
					{
						Level level = levels.get(i);

						RadioButton child = new RadioButton(getContext());
						child.setButtonDrawable(null);
						child.setGravity(Gravity.CENTER);
						child.setText(level.Name);
						child.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
						child.setTextColor(colors);
						ll_levels.addView(child, i, params);

						contentViews.add(new ContentViewFolder(getContext(), level.Id));
						fragments.add(new FolderFragment(level.Id));
					}
					fragments.add(new FragmentDownload());
					contentViews.add(new ContentViewDownload(getContext()));
					adapter.notifyDataSetChanged();

					int childCount = ll_levels.getChildCount();
					for (int i = 0; i < childCount; i++)
					{
						final int position = i;
						RadioButton childAt = (RadioButton) ll_levels.getChildAt(i);
						childAt.setOnClickListener(new OnClickListener()
						{
							@Override
							public void onClick(View v)
							{
								viewpager.setCurrentItem(position);
							}
						});
					}

					ll_levels.getChildAt(0).performClick();
				}

				showView(ViewState.SUCCESS);
			}
		});

	}

	@Override
	public View onCreateSuccessView()
	{
		View inflate = View.inflate(getContext(), R.layout.contentview_listen, null);
		viewpager = (LazyViewPager) inflate.findViewById(R.id.viewpager);
		ll_levels = (RadioGroup) inflate.findViewById(R.id.ll_levels);
		return inflate;
	}

	private class MyAdapter extends FragmentPagerAdapter
	{

		public MyAdapter(FragmentManager fm)
		{
			super(fm);
		}

		@Override
		public int getCount()
		{
			return fragments.size();
		}

		@Override
		public Fragment getItem(int position)
		{
			return fragments.get(position);
		}
	}

}
