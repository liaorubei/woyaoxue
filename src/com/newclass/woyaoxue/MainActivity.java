package com.newclass.woyaoxue;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.newclass.woyaoxue.activity.HomeActivity;
import com.newclass.woyaoxue.service.BatchDownloadService;
import com.newclass.woyaoxue.service.DownloadService;
import com.voc.woyaoxue.R;

public class MainActivity extends Activity
{
	// #3498db #95a5a6

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		// 启动批量下载服务
		Intent service = new Intent(this, BatchDownloadService.class);
		startService(service);

		Intent sIntent = new Intent(this, DownloadService.class);
		startService(sIntent);

		Intent intent = new Intent(this, HomeActivity.class);
		// Intent intent = new Intent(this, TestActivity.class);
		startActivity(intent);
		this.finish();
	}

}
