package com.newclass.woyaoxue;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.newclass.woyaoxue.activity.CallActivity;
import com.newclass.woyaoxue.activity.FolderActivity;
import com.newclass.woyaoxue.activity.RandomActivity;
import com.newclass.woyaoxue.activity.SignInActivity;
import com.newclass.woyaoxue.activity.StudentActivity;
import com.newclass.woyaoxue.activity.TakeActivity;
import com.newclass.woyaoxue.activity.TestActivity;
import com.newclass.woyaoxue.service.AutoUpdateService;
import com.newclass.woyaoxue.service.DownloadService;
import com.newclass.woyaoxue.util.CommonUtil;
import com.voc.woyaoxue.R;

public class MainActivity extends Activity implements OnClickListener
{
	// Monkey测试代码
	// adb shell monkey -p com.voc.woyaoxue -s 500 --ignore-crashes --ignore-timeouts --monitor-native-crashes -v -v 10000 > E:\log.txt
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		initView();

		// 下载任务服务
		Intent sIntent = new Intent(this, DownloadService.class);
		startService(sIntent);
		// 自动升级服务
		Intent service = new Intent(this, AutoUpdateService.class);
		startService(service);
	}

	private Button bt_chat, bt_listen;
	private int back = 0;

	private void initView()
	{
		bt_chat = (Button) findViewById(R.id.bt_chat);
		bt_listen = (Button) findViewById(R.id.bt_listen);

		bt_chat.setOnClickListener(this);
		bt_listen.setOnClickListener(this);
	}

	@Override
	public void onBackPressed()
	{
		// TODO Auto-generated method stub
		super.onBackPressed();
		// back = 1;
		// CommonUtil.toast("再次点击退出");
	}

	@Override
	public void onClick(View v)
	{
		switch (v.getId())
		{
		case R.id.bt_chat:
		{
			// Intent intent = new Intent(this, StudentActivity.class);
			// Intent intent = new Intent(this, TakeActivity.class);
			Intent intent = new Intent(this, RandomActivity.class);
			startActivity(intent);
			// this.finish();

		}
			break;
		case R.id.bt_listen:
		{
			Intent intent = new Intent(this, FolderActivity.class);
			// Intent intent = new Intent(this, TestActivity.class);
			startActivity(intent);
			// this.finish();
		}
			break;
		default:
			break;
		}
	}
}
