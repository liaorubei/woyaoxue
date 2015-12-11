package com.newclass.woyaoxue.student;

import java.util.List;

import org.apache.http.NameValuePair;

import com.lidroid.xutils.http.RequestParams;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.auth.AuthService;
import com.newclass.woyaoxue.fragment.ChooseFragment;
import com.newclass.woyaoxue.fragment.FriendFragment;
import com.newclass.woyaoxue.fragment.GroupsFragment;
import com.newclass.woyaoxue.fragment.RandomFragment;
import com.newclass.woyaoxue.util.Log;
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

public class StudentActivity extends FragmentActivity implements OnClickListener
{
	private LazyViewPager viewpager;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_student);

		initView();

		PagerAdapter myPagerAdapter = new MyPagerAdapter(getSupportFragmentManager());
		viewpager.setAdapter(myPagerAdapter);

		NIMClient.getService(AuthService.class).logout();
		startActivity(new Intent(this, SignInActivity.class));
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
