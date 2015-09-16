package com.newclass.woyaoxue.activity;

import com.voc.woyaoxue.R;

import android.app.ActionBar;
import android.app.Activity;
import android.os.Bundle;
import android.view.MenuItem;

/**
 * 我的下载界面,显示所有已经下载的文件,方面再次打开和管理
 * @author liaorubei
 *
 */
public class MyDownloadActivity extends Activity
{
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_mydownload);

		ActionBar actionBar = getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		switch (item.getItemId())
		{
		case android.R.id.home:

			this.finish();
			return true;

		default:
			break;
		}
		return super.onOptionsItemSelected(item);
	}
}
