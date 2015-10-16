package com.newclass.woyaoxue.activity;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;

import com.newclass.woyaoxue.base.BaseAdapter;
import com.newclass.woyaoxue.bean.Folder;
import com.newclass.woyaoxue.bean.database.Database;
import com.newclass.woyaoxue.util.Log;
import com.newclass.woyaoxue.view.ContentView;
import com.newclass.woyaoxue.view.ContentView.ViewState;
import com.voc.woyaoxue.R;

/**
 * 我的下载界面,显示所有已经下载的文件,方面再次打开和管理
 * 
 * @author liaorubei
 *
 */
public class DownFolderActivity extends Activity
{

	private MyAdapter adapter;
	private ContentView contentView;
	private List<Folder> list;
	private ListView listview;

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		switch (item.getItemId())
		{
		case android.R.id.home:
			this.finish();
			return true;

		case R.id.menu_refresh:

			break;

		default:
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		getMenuInflater().inflate(R.menu.menu_downfolder, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		Log.i("FolderDownActivity.onCreate");
		super.onCreate(savedInstanceState);

		contentView = new ContentView(this)
		{

			@Override
			public View onCreateSuccessView()
			{
				View view = View.inflate(DownFolderActivity.this, R.layout.activity_down, null);
				listview = (ListView) view.findViewById(R.id.listview);
				return view;
			}
		};

		setContentView(contentView);

		Database database = new Database(this);
		List<Folder> folders = database.folderSelectListWithDocsCount();

		list = new ArrayList<Folder>();
		for (Folder folder : folders)
		{
			if (folder.DocsCount > 0)
			{
				list.add(folder);
			}
		}

		contentView.showView(ViewState.SUCCESS);
		database.closeConnection();

		adapter = new MyAdapter(list);
		listview.setAdapter(adapter);
		listview.setOnItemClickListener(new OnItemClickListener()
		{

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id)
			{
				Log.i("点击了");
				Intent intent = new Intent(DownFolderActivity.this, DownDocsActivity.class);
				intent.putExtra("FolderId", list.get(position).Id);
				startActivity(intent);

			}
		});

		// 返回首页按钮
		getActionBar().setDisplayHomeAsUpEnabled(true);

	}

	private class MyAdapter extends BaseAdapter<Folder>
	{

		public MyAdapter(List<Folder> list)
		{
			super(list);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent)
		{
			Folder folder = getItem(position);
			if (convertView == null)
			{
				convertView = View.inflate(DownFolderActivity.this, R.layout.listitem_folder, null);
				ViewHolder holder = new ViewHolder();
				holder.tv_folder_name = (TextView) convertView.findViewById(R.id.tv_folder_name);
				holder.tv_document_count = (TextView) convertView.findViewById(R.id.tv_document_count);
				convertView.setTag(holder);
			}
			ViewHolder holder = (ViewHolder) convertView.getTag();
			holder.tv_folder_name.setText(folder.Name);
			holder.tv_document_count.setText("课程:" + folder.DocsCount);
			return convertView;
		}
	}

	private class ViewHolder
	{
		public TextView tv_document_count;
		public TextView tv_folder_name;
	}

}
