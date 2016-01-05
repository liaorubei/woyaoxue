package com.newclass.woyaoxue.activity;

import java.util.ArrayList;
import java.util.List;

import com.newclass.woyaoxue.base.BaseAdapter;
import com.newclass.woyaoxue.bean.Theme;
import com.voc.woyaoxue.R;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

public class QuestionActivity extends Activity
{
	private ListView listview;
	private TextView tv_theme;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_question);
		initView();
		initData();

		getActionBar().setDisplayHomeAsUpEnabled(true);
	}

	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item)
	{
		switch (item.getItemId())
		{
		case android.R.id.home:
			this.finish();
			break;

		default:
			break;
		}

		return true;
	}

	private void initView()
	{
		tv_theme = (TextView) findViewById(R.id.tv_theme);
		listview = (ListView) findViewById(R.id.listview);

		List<Theme> list = new ArrayList<Theme>();
		list.add(new Theme());
		list.add(new Theme());
		list.add(new Theme());
		list.add(new Theme());
		list.add(new Theme());

		for (Theme theme : list)
		{
			theme.Name = "问题1:" + "你早上是坐什么交通工具上班的啊";
		}

		BaseAdapter<Theme> adapter = new MyAdapter(list);
		listview.setAdapter(adapter);

	}

	private void initData()
	{
		// TODO Auto-generated method stub

	}

	private class MyAdapter extends BaseAdapter<Theme>
	{

		public MyAdapter(List<Theme> list)
		{
			super(list);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent)
		{
			Theme item = getItem(position);

			TextView textView = new TextView(getApplication());
			textView.setText(item.Name);
			textView.setTextColor(Color.BLACK);
			return textView;
		}
	}
}
