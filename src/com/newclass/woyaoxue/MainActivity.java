package com.newclass.woyaoxue;

import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.auth.AuthService;
import com.newclass.woyaoxue.fragment.FragmentPerson;
import com.newclass.woyaoxue.fragment.ListenFragment;
import com.newclass.woyaoxue.fragment.RandomFragment;
import com.newclass.woyaoxue.service.AutoUpdateService;
import com.newclass.woyaoxue.service.DownloadService;
import com.voc.woyaoxue.R;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.RadioButton;
import android.widget.RadioGroup;

public class MainActivity extends FragmentActivity implements OnClickListener
{
	// Monkey测试代码
	// adb shell monkey -p com.voc.woyaoxue -s 500 --ignore-crashes --ignore-timeouts --monitor-native-crashes -v -v 10000 > E:\log.txt

	protected static final String TAG = "MainActivity";
	private RadioGroup ll_ctrl;
	private RadioButton rb_random, rb_listen, rb_person;

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

	private Fragment randomFragment, listenFragment, fragmentPerson;
	private FragmentPagerAdapter kk;

	private void initView()
	{
		ll_ctrl = (RadioGroup) findViewById(R.id.ll_ctrl);
		rb_random = (RadioButton) findViewById(R.id.rb_random);
		rb_listen = (RadioButton) findViewById(R.id.rb_listen);
		rb_person = (RadioButton) findViewById(R.id.rb_person);

		rb_random.setOnClickListener(this);
		rb_listen.setOnClickListener(this);
		rb_person.setOnClickListener(this);

		rb_listen.performClick();
	}

	@Override
	public void onClick(View v)
	{
		switch (v.getId())
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
				listenFragment = new ListenFragment(getSupportFragmentManager());
			}
			getSupportFragmentManager().beginTransaction().replace(R.id.fl_content, listenFragment).commit();
		}
			break;
		case R.id.rb_person:
			if (fragmentPerson == null)
			{
				fragmentPerson = new FragmentPerson();
			}
			getSupportFragmentManager().beginTransaction().replace(R.id.fl_content, fragmentPerson).commit();
			break;
		default:
			break;
		}
	}
}
