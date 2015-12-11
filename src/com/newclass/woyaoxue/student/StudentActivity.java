package com.newclass.woyaoxue.student;

import com.newclass.woyaoxue.fragment.ChooseFragment;
import com.newclass.woyaoxue.fragment.FriendFragment;
import com.newclass.woyaoxue.fragment.GroupsFragment;
import com.newclass.woyaoxue.fragment.RandomFragment;
import com.newclass.woyaoxue.util.Log;
import com.newclass.woyaoxue.view.LazyViewPager;
import com.voc.woyaoxue.R;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.view.View.OnClickListener;

public class StudentActivity extends FragmentActivity implements OnClickListener
{
	private LazyViewPager viewpager;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_student);

		initView();

		PagerAdapter myPagerAdapter = new MMyPagerAdapter();
		viewpager.setAdapter(myPagerAdapter);
	}

	private void initView()
	{
		viewpager = (LazyViewPager) findViewById(R.id.viewpager);

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

	public class MMyPagerAdapter extends PagerAdapter
	{

		@Override
		public int getCount()
		{
			return 4;
		}

		@Override
		public boolean isViewFromObject(View arg0, Object arg1)
		{
			return arg0 == arg1;
		}

		@Override
		public Object instantiateItem(ViewGroup container, int position)
		{
			Log.i("logi", "instantiateItem:" + position);

			TextView textView = new TextView(StudentActivity.this);
			textView.setText("textview:" + position);
			container.addView(textView);
			return textView;
		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object object)
		{
			container.removeView((View) object);
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
