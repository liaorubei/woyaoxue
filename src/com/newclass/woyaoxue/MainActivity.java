package com.newclass.woyaoxue;

import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

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
import com.newclass.woyaoxue.fragment.DocsListFragment;
import com.newclass.woyaoxue.service.DownLoadService;
import com.newclass.woyaoxue.util.NetworkUtil;
import com.voc.woyaoxue.R;

public class MainActivity extends FragmentActivity implements android.view.View.OnClickListener
{
	protected static final int INIT_LEVEL = 0;

	@ViewInject(R.id.fl_content)
	private FrameLayout fl_content;
	@ViewInject(R.id.rg_levels)
	private RadioGroup rg_levels;
	@ViewInject(R.id.bt_menu)
	private View bt_menu;

	List<DocsListFragment> fragments;
	private PackageManager packageManager;
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

	private PopupWindow window;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		ViewUtils.inject(this);

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
					RadioGroup.LayoutParams params = new RadioGroup.LayoutParams(0, RadioGroup.LayoutParams.MATCH_PARENT, 1);
					rg_levels.removeAllViews();
					for (int i = 0; i < 3; i++)
					{
						RadioButton button = new RadioButton(MainActivity.this);
						button.setGravity(Gravity.CENTER);
						button.setLayoutParams(params);
						button.setBackgroundResource(R.drawable.selector_levels);
						button.setButtonDrawable(android.R.color.transparent);
						button.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 12);

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
						rg_levels.addView(button);
					}
					rg_levels.getChildAt(0).performClick();
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

						builder.show();

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

		bt_menu.setOnClickListener(this);
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

	@Override
	public void onClick(View v)
	{
		switch (v.getId())
		{
		case R.id.bt_menu:
			if (window == null)
			{
				window = new PopupWindow(this);
				// window.dismiss();
				window.setFocusable(true);// 要求可以取得焦点,当失去焦点时可以自动dismiss
				TextView textView = new TextView(this);
				textView.setText("弹出窗口");
				textView.setPadding(50, 50, 50, 50);
				textView.setOnClickListener(new View.OnClickListener()
				{

					@Override
					public void onClick(View v)
					{
						Toast.makeText(MainActivity.this, "弹出吐司", Toast.LENGTH_LONG).show();
						window.dismiss();
					}

				});
				window.setContentView(textView);
				window.setWindowLayoutMode(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
			}

			window.showAtLocation(bt_menu, Gravity.BOTTOM | Gravity.END, 0, 0);

			break;

		default:
			break;
		}

	}
}
