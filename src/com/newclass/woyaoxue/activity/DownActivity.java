package com.newclass.woyaoxue.activity;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.format.Formatter;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.newclass.woyaoxue.base.BaseAdapter;
import com.newclass.woyaoxue.bean.database.Document;
import com.newclass.woyaoxue.util.DaoUtil;
import com.voc.woyaoxue.R;

/**
 * 我的下载界面,显示所有已经下载的文件,方面再次打开和管理
 * 
 * @author liaorubei
 *
 */
public class DownActivity extends Activity
{
	@ViewInject(R.id.listview)
	private ListView listview;
	private ListAdapter adapter;
	private List<Document> documents;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_down);
		ViewUtils.inject(this);
		// 返回首页按钮
		getActionBar().setDisplayHomeAsUpEnabled(true);

		sInitData();
		sInitView();

	}

	private void sInitView()
	{
		adapter = new MyAdapter(documents);
		listview.setAdapter(adapter);

		// Item点击事件
		listview.setOnItemClickListener(new OnItemClickListener()
		{

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id)
			{
				Intent intent = new Intent(DownActivity.this, PlayActivity.class);
				intent.putExtra("Id", documents.get(position).DocId);
				startActivity(intent);
			}
		});

	}

	private void sInitData()
	{
		List<Document> allDocs = DaoUtil.getAllDocs(this, true);
		documents = allDocs == null ? new ArrayList<Document>() : new ArrayList<Document>(allDocs);
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
				convertView = View.inflate(DownActivity.this, R.layout.listitem_down, null);
				ViewHolder holder = new ViewHolder();
				holder.tv_title_one = (TextView) convertView.findViewById(R.id.tv_title_one);
				holder.tv_title_two = (TextView) convertView.findViewById(R.id.tv_title_two);
				holder.tv_date = (TextView) convertView.findViewById(R.id.tv_date);
				holder.tv_size = (TextView) convertView.findViewById(R.id.tv_size);
				holder.tv_time = (TextView) convertView.findViewById(R.id.tv_time);

				convertView.setTag(holder);
			}

			ViewHolder holder = (ViewHolder) convertView.getTag();
			holder.tv_title_one.setText(document.TitleOne);
			holder.tv_title_two.setText(document.TitleTwo);
			holder.tv_date.setText(document.Date);
			holder.tv_size.setText(Formatter.formatFileSize(DownActivity.this, document.Length));
			holder.tv_time.setText(document.Time);

			return convertView;
		}
	}

	private class ViewHolder
	{
		public TextView tv_time;
		public TextView tv_size;
		public TextView tv_date;
		public TextView tv_title_two;
		public TextView tv_title_one;
	}
}
