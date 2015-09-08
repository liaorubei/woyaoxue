package com.newclass.woyaoxue;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.newclass.woyaoxue.bean.Level;
import com.newclass.woyaoxue.bean.UpgradePatch;
import com.newclass.woyaoxue.fragment.CategoryFragment;
import com.newclass.woyaoxue.fragment.DocsListFragment;
import com.newclass.woyaoxue.service.DownLoadService;
import com.newclass.woyaoxue.util.NetworkUtil;
import com.voc.woyaoxue.R;

public class MainActivity extends FragmentActivity
{
	protected static final int INIT_LEVEL = 0;

	@ViewInject(R.id.fl_content)
	private FrameLayout fl_content;

	List<DocsListFragment> fragments;
	private PackageManager packageManager;
	private AlertDialog alertDialog;

	@ViewInject(R.id.ll_ctrl)
	private LinearLayout ll_ctrl;

	private static Handler handler = new Handler()
	{
		public void handleMessage(android.os.Message msg)
		{
			switch (msg.what)
			{
			case INIT_LEVEL:
				init_level();
				break;

			default:
				break;
			}

		};
	};
	private List<Level> levels;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		ViewUtils.inject(this);

		Log.i("logi", "FilesDir=" + getFilesDir());
		
		
		

		// 常规数据请求
		new HttpUtils().send(HttpMethod.GET, NetworkUtil.getLevels(), new RequestCallBack<String>()
		{

			@Override
			public void onSuccess(ResponseInfo<String> responseInfo)
			{
				levels = new Gson().fromJson(responseInfo.result, new TypeToken<List<Level>>()
				{}.getType());

				if (levels.size() > 4)
				{
					fragments = new ArrayList<DocsListFragment>();
					for (int i = 0; i < 3; i++)
					{
						Button button = (Button) ll_ctrl.getChildAt(i);
						Level level = levels.get(i);
						button.setText(level.LevelName);
						button.setTag(i);
						button.setOnClickListener(new View.OnClickListener()
						{
							@Override
							public void onClick(View v)
							{
								DocsListFragment docsListFragment = fragments.get((Integer) v.getTag());
								getSupportFragmentManager().beginTransaction().replace(R.id.fl_content, docsListFragment).commit();
							}
						});

						DocsListFragment fragment = new DocsListFragment(NetworkUtil.getDocsByLevelId(level.Id));
						fragments.add(fragment);
					}
					ll_ctrl.getChildAt(0).performClick();
				}

			}

			@Override
			public void onFailure(HttpException error, String msg)
			{
				// TODO Auto-generated method stub

			}
		});

		// 升级数据请求,建议放到spash界面
		new HttpUtils().send(HttpMethod.GET, NetworkUtil.getLatest(), new RequestCallBack<String>()
		{

			@Override
			public void onSuccess(ResponseInfo<String> responseInfo)
			{
				Log.i("logi", "onSuccess");
				try
				{

					// 解析
					final UpgradePatch upgradePatch = new Gson().fromJson(responseInfo.result, UpgradePatch.class);
					packageManager = getPackageManager();
					PackageInfo packageInfo = packageManager.getPackageInfo(getPackageName(), PackageManager.GET_CONFIGURATIONS);
					// Log.i("logi", "versionCode=" + packageInfo.versionCode + " versionName=" + packageInfo.versionName + " packageName=" + packageInfo.packageName);

					if (!packageInfo.versionName.equals(upgradePatch.VersionName))
					{
						Builder builder = new AlertDialog.Builder(MainActivity.this);
						builder.setTitle(R.string.upgrade_tips);

						builder.setMessage(upgradePatch.UpgradeInfo);
						builder.setNegativeButton(R.string.negative_text, new OnClickListener()
						{

							@Override
							public void onClick(DialogInterface dialog, int which)
							{

							}
						});
						builder.setPositiveButton(R.string.positive_text, new OnClickListener()
						{

							@Override
							public void onClick(DialogInterface dialog, int which)
							{

								Intent service = new Intent(MainActivity.this, DownLoadService.class);
								service.putExtra("versionName", upgradePatch.VersionName);
								service.putExtra("path", upgradePatch.PackagePath);
								startService(service);

							}
						});

						alertDialog = builder.show();

					}
				}
				catch (Exception e)
				{

					e.printStackTrace();
				}

			}

			@Override
			public void onFailure(HttpException error, String msg)
			{
				Log.i("logi", "连网失败");
			}
		});
	}

	protected static void init_level()
	{

	}

	@Override
	protected void onDestroy()
	{
		super.onDestroy();
		handler.removeCallbacksAndMessages(null);
	}
}
