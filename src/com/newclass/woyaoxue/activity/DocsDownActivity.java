package com.newclass.woyaoxue.activity;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import android.R.integer;
import android.app.Activity;
import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.AsyncTask;
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

/**
 * 我的下载--文档列表显示界面
 * 
 * @author liaorubei
 *
 */
public class DocsDownActivity extends Activity
{
	private List<Document> list;
	private int folderId;
	private ContentView contentView;
	private MyAdapter adapter;
	private ServiceConnection conn;
	private MyBinder myBinder;
	private Database database;
	private XListView listview;


	protected int pageSize = 15;
	private int levelId;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		Log.i("DocsDownActivity onCreate");
		super.onCreate(savedInstanceState);
		contentView = new ContentView(this)
		{

			@Override
			public View onCreateSuccessView()
			{
				View view = View.inflate(DocsDownActivity.this, R.layout.activity_docs, null);
				view.findViewById(R.id.rl_download).setVisibility(view.GONE);
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

		if (myBinder.getDownloadManager().size() > 0)
		{
			Toast.makeText(this, "后台下载中", Toast.LENGTH_SHORT).show();
		}

		// 移除观察者,让下载服务后台自行下载
		myBinder.getDownloadManager().deleteObserver(adapter);

		// 移除服务绑定,避免内存泄漏
		unbindService(conn);
	}

	private void loadMore()
	{
		/*
		 * new HttpUtils().send(HttpMethod.GET, NetworkUtil.getDocs(folderId + "", list.size() + "", pageSize + ""), new RequestCallBack<String>() {
		 * 
		 * @Override public void onSuccess(ResponseInfo<String> responseInfo) { Log.i("加载成功=" + this.getRequestUrl()); List<Document> json = new Gson().fromJson(responseInfo.result, new TypeToken<List<Document>>() {}.getType()); if (json.size() > 0) { list.addAll(json); adapter.notifyDataSetChanged(); contentView.showView(ViewState.SUCCESS); } else { contentView.showView(list.size() > 0 ? ViewState.SUCCESS : ViewState.EMPTY); }
		 * 
		 * listview.stopLoadMore(json.size() < pageSize ? XListViewFooter.STATE_NOMORE : XListViewFooter.STATE_NORMAL); }
		 * 
		 * @Override public void onFailure(HttpException error, String msg) { contentView.showView(ViewState.FAILURE); } });
		 */

		new AsyncTask<Integer, Integer, Integer>()
		{

			@Override
			protected Integer doInBackground(Integer... params)
			{
				List<Document> docs = database.docsSelectListByFolderId(params[0]);
				list.addAll(docs);
				return docs.size();
			}

			@Override
			protected void onPostExecute(Integer result)
			{
				adapter.notifyDataSetChanged();
				contentView.showView(result > 0 ? ViewState.SUCCESS : ViewState.EMPTY);
			}

		}.execute(folderId);

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
				convertView = View.inflate(DocsDownActivity.this, R.layout.listitem_docs, null);
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

			if (database.docsExists(item.Id))
			{
				holder.cpb.setMax(100);
				holder.cpb.setProgress(100);
				holder.cpb.setBackgroundResource(R.drawable.download_finish);
			}
			else
			{
				holder.cpb.setMax(100);
				holder.cpb.setProgress(0);
				holder.cpb.setBackgroundResource(R.drawable.download_notyet);
			}

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
