package com.newclass.woyaoxue.activity;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import android.app.Activity;
import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.text.format.Formatter;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;
import com.newclass.woyaoxue.base.BaseAdapter;
import com.newclass.woyaoxue.bean.Document;
import com.newclass.woyaoxue.bean.DownloadInfo;
import com.newclass.woyaoxue.bean.database.Database;
import com.newclass.woyaoxue.bean.database.MySQLiteOpenHelper;
import com.newclass.woyaoxue.service.DownloadService;
import com.newclass.woyaoxue.service.DownloadService.MyBinder;
import com.newclass.woyaoxue.util.FolderUtil;
import com.newclass.woyaoxue.util.Log;
import com.newclass.woyaoxue.util.NetworkUtil;
import com.newclass.woyaoxue.view.CircularProgressBar;
import com.newclass.woyaoxue.view.ContentView;
import com.newclass.woyaoxue.view.ContentView.ViewState;
import com.newclass.woyaoxue.view.XListView;
import com.newclass.woyaoxue.view.XListView.IXListViewListener;
import com.newclass.woyaoxue.view.XListViewFooter;
import com.voc.woyaoxue.R;

public class DocsActivity extends Activity
{
	private List<Document> list;
	private int folderId;
	private ContentView contentView;
	private MyAdapter adapter;
	private ServiceConnection conn;
	private MyBinder myBinder;
	private Database database;
	private XListView listview;
	private View cpb_download;
	private TextView tv_folder;
	protected int pageSize = 15;
	private int levelId;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		contentView = new ContentView(this)
		{

			@Override
			public View onCreateSuccessView()
			{
				View view = View.inflate(DocsActivity.this, R.layout.activity_docs, null);
				tv_folder = (TextView) view.findViewById(R.id.tv_folder);
				cpb_download = view.findViewById(R.id.cpb_download);
				listview = (XListView) view.findViewById(R.id.listview);
				return view;
			}
		};
		setContentView(contentView);

		database = new Database(this);

		new MySQLiteOpenHelper(this);

		// 取得传递过来的数据
		Intent intent = getIntent();
		levelId = intent.getIntExtra("LevelId", 0);
		folderId = intent.getIntExtra("FolderId", 0);
		String folderName = intent.getStringExtra("FolderName");
		tv_folder.setText(folderName);

		list = new ArrayList<Document>();
		adapter = new MyAdapter(list);

		conn = new ServiceConnection()
		{

			@Override
			public void onServiceDisconnected(ComponentName name)
			{

			}

			@Override
			public void onServiceConnected(ComponentName name, IBinder service)
			{
				myBinder = (MyBinder) service;
				myBinder.getDownloadManager().addObserver(adapter);
			}
		};
		bindService(new Intent(this, DownloadService.class), conn, Service.BIND_AUTO_CREATE);

		listview.setAdapter(adapter);
		// 其他设置
		listview.setPullDownEnable(false);
		listview.setPullupEnable(true);
		listview.setOnItemClickListener(new OnItemClickListener()
		{

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id)
			{
				// TODO Auto-generated method stub

			}
		});
		listview.setXListViewListener(new IXListViewListener()
		{

			@Override
			public void onRefresh()
			{}

			@Override
			public void onLoadMore()
			{
				loadMore();
			}
		});

		cpb_download.setOnClickListener(new OnClickListener()
		{

			@Override
			public void onClick(View v)
			{
				// 添加到下载列表中,添加到数据库中,提示数据适配器更新
				for (Document i : list)
				{
					if (!database.docsExists(i.Id))
					{
						DownloadInfo info = new DownloadInfo();
						info.Title = i.Title;
						info.Url = NetworkUtil.getFullPath(i.SoundPath);
						info.Target = new File(FolderUtil.rootDir(DocsActivity.this), i.SoundPath);
						info.Total = 100L;
						info.Current = 0L;
						myBinder.getDownloadManager().enqueue(info);
						database.docsInsert(i);
					}
					adapter.notifyDataSetChanged();
				}

			}
		});

		getActionBar().setDisplayHomeAsUpEnabled(true);

		loadMore();
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		switch (item.getItemId())
		{
		case android.R.id.home:
			this.finish();
			break;

		default:
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	protected void onDestroy()
	{
		super.onDestroy();

		database.closeConnection();

		// 移除观察者,让下载服务后台自行下载
		myBinder.getDownloadManager().deleteObserver(adapter);

		// 移除服务绑定,避免内存泄漏
		unbindService(conn);
	}

	private void loadMore()
	{
		new HttpUtils().send(HttpMethod.GET, NetworkUtil.getDocs(folderId + "", list.size() + "", pageSize + ""), new RequestCallBack<String>()
		{

			@Override
			public void onSuccess(ResponseInfo<String> responseInfo)
			{
				Log.i("加载成功=" + this.getRequestUrl());
				List<Document> json = new Gson().fromJson(responseInfo.result, new TypeToken<List<Document>>()
				{}.getType());
				if (json.size() > 0)
				{
					list.addAll(json);
					adapter.notifyDataSetChanged();
					contentView.showView(ViewState.SUCCESS);
				}
				else
				{
					contentView.showView(list.size() > 0 ? ViewState.SUCCESS : ViewState.EMPTY);
				}

				listview.stopLoadMore(json.size() < pageSize ? XListViewFooter.STATE_NOMORE : XListViewFooter.STATE_NORMAL);
			}

			@Override
			public void onFailure(HttpException error, String msg)
			{
				contentView.showView(ViewState.FAILURE);
			}
		});

	}

	private class MyAdapter extends BaseAdapter<Document> implements Observer
	{

		public MyAdapter(List<Document> list)
		{
			super(list);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent)
		{
			final Document item = getItem(position);
			if (convertView == null)
			{
				convertView = View.inflate(DocsActivity.this, R.layout.listitem_docs, null);
				ViewHolder holder = new ViewHolder();
				holder.tv_title_one = (TextView) convertView.findViewById(R.id.tv_title_one);
				holder.tv_title_two = (TextView) convertView.findViewById(R.id.tv_title_two);
				holder.tv_date = (TextView) convertView.findViewById(R.id.tv_date);
				holder.tv_size = (TextView) convertView.findViewById(R.id.tv_size);
				holder.tv_time = (TextView) convertView.findViewById(R.id.tv_time);
				holder.cpb = (CircularProgressBar) convertView.findViewById(R.id.cpb);
				convertView.setTag(holder);
			}
			ViewHolder holder = (ViewHolder) convertView.getTag();
			holder.tv_title_one.setText(item.Title);
			holder.tv_title_two.setText(item.TitleTwo);
			holder.tv_date.setText(item.DateString);
			holder.tv_size.setText(Formatter.formatFileSize(DocsActivity.this, item.Length));
			holder.tv_time.setText(item.LengthString);

			final DownloadInfo info = new DownloadInfo();
			info.Url = NetworkUtil.getFullPath(item.SoundPath);
			info.Target = new File(FolderUtil.rootDir(DocsActivity.this), item.SoundPath);
			info.Total = 100L;
			info.Current = 0L;
			info.Title = item.Title;
			if (database.docsExists(item.Id))
			{
				DownloadInfo downloadInfo = myBinder.getDownloadManager().get(info.Url);
				if (downloadInfo != null)
				{
					holder.cpb.setMax((int) downloadInfo.Total);
					holder.cpb.setProgress((int) downloadInfo.Current);
					holder.cpb.setBackgroundResource(R.drawable.download_begin);
				}
				else
				{
					holder.cpb.setMax(100);
					holder.cpb.setProgress(100);
					holder.cpb.setBackgroundResource(R.drawable.download_finish);
				}
			}
			else
			{
				holder.cpb.setMax(100);
				holder.cpb.setProgress(0);
				holder.cpb.setBackgroundResource(R.drawable.download_notyet);
			}

			holder.cpb.setOnClickListener(new OnClickListener()
			{
				@Override
				public void onClick(View v)
				{
					if (database.docsExists(item.Id))
					{
						Toast.makeText(DocsActivity.this, "请不要重复下载", Toast.LENGTH_SHORT).show();
					}
					else
					{
						// 加入待下载队列中
						myBinder.getDownloadManager().enqueue(info);
						// 保存数据到数据库
						item.LevelId = levelId;
						item.FolderId = folderId;
						database.docsInsert(item);
						v.setBackgroundResource(R.drawable.download_begin);
					}
				}
			});

			return convertView;
		}

		@Override
		public void update(Observable observable, Object data)
		{
			Log.i("update");
			notifyDataSetChanged();
		}
	}

	private class ViewHolder
	{
		public TextView tv_title_one;
		public TextView tv_title_two;
		public TextView tv_date;
		public TextView tv_size;
		public TextView tv_time;
		public CircularProgressBar cpb;
	}

}
