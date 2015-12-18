package com.newclass.woyaoxue.activity;

import com.newclass.woyaoxue.fragment.ChooseFragment;
import com.newclass.woyaoxue.fragment.FriendFragment;
import com.newclass.woyaoxue.fragment.GroupsFragment;
import com.newclass.woyaoxue.fragment.RandomFragment;
import com.newclass.woyaoxue.view.LazyViewPager;
import com.voc.woyaoxue.R;

import android.content.Intent;
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
	private TextView tv_random, tv_choose, tv_groups, tv_friend;

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
		tv_random = (TextView) findViewById(R.id.tv_random);
		tv_choose = (TextView) findViewById(R.id.tv_choose);
		tv_groups = (TextView) findViewById(R.id.tv_groups);
		tv_friend = (TextView) findViewById(R.id.tv_friend);

		tv_choose.setOnClickListener(this);
		tv_groups.setOnClickListener(this);
		tv_friend.setOnClickListener(this);
	}

	@Override
	public void onClick(View v)
	{
		switch (v.getId())
		{
		case R.id.viewpager:

			break;
		case R.id.tv_choose:

			break;

		case R.id.tv_groups:
			Intent intent = new Intent(StudentActivity.this, GroupActivity.class);
			intent.putExtra(GroupActivity.ENTER_TYPE, GroupActivity.ENTER_STUDENT);
			startActivity(intent);

			break;
		case R.id.tv_friend:

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

			return 1;
		}
	}

}
