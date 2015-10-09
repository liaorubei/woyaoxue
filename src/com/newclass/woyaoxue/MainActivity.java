package com.newclass.woyaoxue;

import java.util.List;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.lidroid.xutils.view.annotation.ViewInject;
import com.newclass.woyaoxue.activity.DownActivity;
import com.newclass.woyaoxue.activity.FolderActivity;
import com.newclass.woyaoxue.activity.TestActivity;
import com.newclass.woyaoxue.bean.Level;
import com.newclass.woyaoxue.fragment.DocsListFragment;
import com.voc.woyaoxue.R;

public class MainActivity extends FragmentActivity implements android.view.View.OnClickListener
{
	// #3498db #95a5a6

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
		Intent intent = new Intent(this, FolderActivity.class);
		//Intent intent = new Intent(this, TestActivity.class);
		startActivity(intent);
		this.finish();
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
	public boolean onCreateOptionsMenu(Menu menu)
	{
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.main_activity, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		switch (item.getItemId())
		{
		case R.id.menu_mydownload:
			Intent intent = new Intent(this, DownActivity.class);
			startActivity(intent);
			return true;

		default:
			break;
		}
		return super.onOptionsItemSelected(item);
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
				textView.setText("我的下载");
				textView.setOnClickListener(new View.OnClickListener()
				{

					@Override
					public void onClick(View v)
					{
						window.dismiss();
						Intent intent = new Intent(MainActivity.this, DownActivity.class);
						startActivity(intent);
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
