package com.newclass.woyaoxue.activity;

import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.newclass.woyaoxue.base.BaseAdapter;
import com.newclass.woyaoxue.bean.Document;
import com.voc.woyaoxue.R;

/**
 * 我的下载界面,显示所有已经下载的文件,方面再次打开和管理
 * 
 * @author liaorubei
 *
 */
public class MyDownloadActivity extends Activity
{
	@ViewInject(R.id.listview)
	private ListView listview;
	private ListAdapter adapter;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_mydownload);
		ViewUtils.inject(this);
		// 返回首页按钮
		getActionBar().setDisplayHomeAsUpEnabled(true);

		listview.setAdapter(adapter);
		listview.setOnItemClickListener(new OnItemClickListener()
		{

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id)
			{
				Toast.makeText(MyDownloadActivity.this, "点击了", Toast.LENGTH_LONG).show();

			}
		});

	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		switch (item.getItemId())
		{
		case android.R.id.home:

			this.finish();
			return true;

		default:
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	private class MyAdapter extends BaseAdapter<Document>
	{

		public MyAdapter(List<Document> list)
		{
			super(list);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent)
		{
			Document document = getItem(position);
			if (convertView == null)
			{
				convertView = View.inflate(MyDownloadActivity.this, R.layout.listitem_docslist, null);
				ViewHolder holder = new ViewHolder();
				convertView.setTag(holder);
			}
			ViewHolder holder = (ViewHolder) convertView.getTag();

			return convertView;
		}
	}

	private class ViewHolder
	{

	}
}
