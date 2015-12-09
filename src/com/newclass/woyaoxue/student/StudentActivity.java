package com.newclass.woyaoxue.student;

import com.newclass.woyaoxue.fragment.RandomFragment;
import com.newclass.woyaoxue.util.Log;
import com.voc.woyaoxue.R;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.View.OnClickListener;

public class StudentActivity extends FragmentActivity implements OnClickListener
{
	private ViewPager viewpager;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_student);

		initView();

		MyPagerAdapter myPagerAdapter = new MyPagerAdapter(getSupportFragmentManager());
		viewpager.setAdapter(myPagerAdapter);
	}

	private void initView()
	{
		viewpager = (ViewPager) findViewById(R.id.viewpager);

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
		public Fragment getItem(int arg0)
		{
			return new RandomFragment();
		}

		@Override
		public int getCount()
		{

			return 1;
		}
	}

}
