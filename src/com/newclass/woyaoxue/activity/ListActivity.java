package com.newclass.woyaoxue.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.newclass.woyaoxue.base.BaseFragment;
import com.newclass.woyaoxue.fragment.DocsListFragment;
import com.voc.woyaoxue.R;

public class ListActivity extends FragmentActivity
{
	private int levelid;
	private int folderId;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		Intent intent = getIntent();
		levelid = intent.getIntExtra("LevelId", 0);
		folderId = intent.getIntExtra("FolderId", 0);

		setContentView(R.layout.activity_list);

		ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);
		viewPager.setAdapter(new MyPagerAdapter(getSupportFragmentManager()));

		getActionBar().setDisplayHomeAsUpEnabled(true);
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
		case android.R.id.home:
			this.finish();
			break;
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

	private class MyPagerAdapter extends FragmentPagerAdapter
	{

		public MyPagerAdapter(FragmentManager fm)
		{
			super(fm);
		}

		@Override
		public Fragment getItem(int position)
		{
			BaseFragment fragment = new DocsListFragment(folderId,levelid);
			fragment.initData();
			return fragment;
		}

		@Override
		public int getCount()
		{
			return 1;
		}
	}

}
