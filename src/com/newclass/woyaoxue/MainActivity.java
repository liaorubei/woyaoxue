package com.newclass.woyaoxue;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.newclass.woyaoxue.activity.FolderActivity;
import com.newclass.woyaoxue.service.DownloadService;
import com.voc.woyaoxue.R;

public class MainActivity extends Activity
{
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		Intent sIntent = new Intent(this, DownloadService.class);
		startService(sIntent);

		Intent intent = new Intent(this, FolderActivity.class);
		// Intent intent = new Intent(this, TestActivity.class);
		startActivity(intent);
		this.finish();		
	}
}
