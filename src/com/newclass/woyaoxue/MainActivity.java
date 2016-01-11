package com.newclass.woyaoxue;

import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.auth.AuthService;
import com.newclass.woyaoxue.fragment.ListenFragment;
import com.newclass.woyaoxue.fragment.MyFragment;
import com.newclass.woyaoxue.fragment.RandomFragment;
import com.newclass.woyaoxue.service.AutoUpdateService;
import com.newclass.woyaoxue.service.DownloadService;
import com.voc.woyaoxue.R;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentPagerAdapter;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;

public class MainActivity extends FragmentActivity
{
	// Monkey测试代码
	// adb shell monkey -p com.voc.woyaoxue -s 500 --ignore-crashes --ignore-timeouts --monitor-native-crashes -v -v 10000 > E:\log.txt

	protected static final String TAG = "MainActivity";
	private RadioGroup ll_ctrl;

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

		NIMClient.getService(AuthService.class).logout();// 登出帐号
	}

	private Fragment randomFragment, listenFragment, myFragment;
	private FragmentPagerAdapter kk;

	private void initView()
	{
		ll_ctrl = (RadioGroup) findViewById(R.id.ll_ctrl);
		ll_ctrl.setOnCheckedChangeListener(new OnCheckedChangeListener()
		{

			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId)
			{

				switch (checkedId)
				{
				case R.id.rb_random:
				{
					if (randomFragment == null)
					{
						randomFragment = new RandomFragment();
					}
					getSupportFragmentManager().beginTransaction().replace(R.id.fl_content, randomFragment).commit();
				}
					break;
				case R.id.rb_listen:
				{
					if (listenFragment == null)
					{
						listenFragment = new ListenFragment();
					}
					getSupportFragmentManager().beginTransaction().replace(R.id.fl_content, listenFragment).commit();
				}
					break;
				case R.id.rb_my:
					getSupportFragmentManager().beginTransaction().replace(R.id.fl_content, new MyFragment()).commit();
					break;
				default:
					break;
				}
			}
		});

	}
}
