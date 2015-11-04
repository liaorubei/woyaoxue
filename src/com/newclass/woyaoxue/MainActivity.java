package com.newclass.woyaoxue;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.newclass.woyaoxue.activity.FolderActivity;
import com.newclass.woyaoxue.service.DownloadService;
import com.voc.woyaoxue.R;

public class MainActivity extends Activity
{
	// Monkey测试代码
	// adb shell monkey -p com.voc.woyaoxue -s 500 --ignore-crashes --ignore-timeouts --monitor-native-crashes -v -v 10000 > E:\log.txt
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
