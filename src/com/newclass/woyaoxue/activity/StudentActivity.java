package com.newclass.woyaoxue.activity;

import com.newclass.woyaoxue.fragment.ChooseFragment;
import com.newclass.woyaoxue.fragment.FriendFragment;
import com.newclass.woyaoxue.fragment.GroupsFragment;
import com.newclass.woyaoxue.fragment.RandomFragment;
import com.newclass.woyaoxue.view.LazyViewPager;
import com.newclass.woyaoxue.view.LazyViewPager.OnPageChangeListener;
import com.voc.woyaoxue.R;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;

public class StudentActivity extends FragmentActivity implements OnClickListener
{
	private LazyViewPager viewpager;
	private LinearLayout ll_ctrl;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_student);

		initView();

		PagerAdapter myPagerAdapter = new MyPagerAdapter(getSupportFragmentManager());
		viewpager.setAdapter(myPagerAdapter);
	}

	private void initView()
	{
		viewpager = (LazyViewPager) findViewById(R.id.viewpager);
		viewpager.setOnPageChangeListener(new OnPageChangeListener()
		{

			@Override
			public void onPageSelected(int position)
			{
				int childCount = ll_ctrl.getChildCount();
				for (int i = 0; i < childCount; i++)
				{
					TextView childAt = (TextView) ll_ctrl.getChildAt(i);
					childAt.setTextColor(i == position ? Color.BLUE : Color.BLACK);
				}
			}

			@Override
			public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels)
			{
				// TODO Auto-generated method stub

			}

			@Override
			public void onPageScrollStateChanged(int state)
			{
				// TODO Auto-generated method stub

			}
		});

		ll_ctrl = (LinearLayout) findViewById(R.id.ll_ctrl);
		int childCount = ll_ctrl.getChildCount();
		for (int i = 0; i < childCount; i++)
		{
			final int position = i;
			TextView childAt = (TextView) ll_ctrl.getChildAt(i);
			childAt.setTextColor(i == 0 ? Color.BLUE : Color.BLACK);
			childAt.setOnClickListener(new OnClickListener()
			{

				@Override
				public void onClick(View v)
				{
					viewpager.setCurrentItem(position);
				}
			});
		}
	}

	@Override
	public void onClick(View v)
	{
		switch (v.getId())
		{
		case R.id.viewpager:

			break;

		default:
			break;
		}

	}

	public class MyPagerAdapter extends FragmentPagerAdapter
	{

		public MyPagerAdapter(FragmentManager fm)
		{
			super(fm);
		}

		@Override
		public Fragment getItem(int position)
		{
			switch (position)
			{
			case 0:
				return new RandomFragment();
			// break;
			case 1:
				return new ChooseFragment();
			// break;
			case 2:
				return new GroupsFragment();
			// break;
			case 3:
				return new FriendFragment();
			// break;
			default:
				break;
			}

			return new RandomFragment();
		}

		@Override
		public int getCount()
		{

			return 4;
		}
	}

}
