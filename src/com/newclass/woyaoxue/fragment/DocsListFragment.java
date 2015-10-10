package com.newclass.woyaoxue.fragment;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.os.Bundle;
import android.os.IBinder;
import android.text.format.DateFormat;
import android.text.format.Formatter;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;
import com.newclass.woyaoxue.activity.PlayActivity;
import com.newclass.woyaoxue.base.BaseAdapter;
import com.newclass.woyaoxue.base.BaseFragment;
import com.newclass.woyaoxue.bean.Document;
import com.newclass.woyaoxue.service.BatchDownloadService;
import com.newclass.woyaoxue.service.BatchDownloadService.BatchDownloadBinder;
import com.newclass.woyaoxue.util.DaoUtil;
import com.newclass.woyaoxue.util.FolderUtil;
import com.newclass.woyaoxue.util.Log;
import com.newclass.woyaoxue.util.NetworkUtil;
import com.newclass.woyaoxue.view.CircularProgressBar;
import com.newclass.woyaoxue.view.XListView;
import com.newclass.woyaoxue.view.XListView.IXListViewListener;
import com.newclass.woyaoxue.view.XListViewFooter;
import com.voc.woyaoxue.R;

public class DocsListFragment extends BaseFragment
{
	private BatchDownloadBinder batchDownloadBinder;
	private String mPath;
	private MyAdapter myAdapter;
	private MyServiceConnection myServiceConnection;
	private List<DownloadHelper> objects;
	protected int pageSize = 20;
	private XListView xListView;
	private int mFolderId;


	public DocsListFragment(int folderId, int levelid)
	{
		this.mFolderId = folderId;

		objects = new ArrayList<DocsListFragment.DownloadHelper>();
		myAdapter = new MyAdapter(objects);
		this.mPath = NetworkUtil.getDocs(mFolderId == 0 ? "" : mFolderId + "",  objects.size() + "", pageSize + "");
	}

	@Override
	protected View initView()
	{
		Log.i("logi", this.mPath);
		View view = View.inflate(getActivity(), R.layout.fragment_docslist, null);

		xListView = (XListView) view.findViewById(R.id.xListView);
		xListView.set下拉刷新Enable(false);
		xListView.set上拉加载Enable(true);
		xListView.setAdapter(myAdapter);

		// xListView.setOnItemClickListener();//不直接在ListView上设置监听,容易出现Item错位问题

		xListView.setXListViewListener(new IXListViewListener()
		{

			@Override
			public void onLoadMore()
			{
				loadMore();
			}

			@Override
			public void onRefresh()
			{
				refresh();
			}

		});
		return view;
	}

	private void loadMore()
	{
		this.mPath = NetworkUtil.getDocs(mFolderId == 0 ? "" : mFolderId + "", objects.size() + "", pageSize + "");
		new HttpUtils().send(HttpMethod.GET, DocsListFragment.this.mPath, new RequestCallBack<String>()
		{

			@Override
			public void onFailure(HttpException error, String msg)
			{
				xListView.stopRefresh();
				xListView.stopLoadMore(XListViewFooter.STATE_ERRORS);
				xListView.setRefreshTime(DateFormat.format("HH:mm:ss", new Date()) + " 刷新失败");
			}

			@Override
			public void onSuccess(ResponseInfo<String> responseInfo)
			{
				List<Document> fromJson = new Gson().fromJson(responseInfo.result, new TypeToken<List<Document>>()
				{}.getType());

				if (fromJson.size() > 0)
				{
					for (Document d : fromJson)
					{
						objects.add(new DownloadHelper(d));
					}
					myAdapter.notifyDataSetChanged();
				}

				xListView.stopLoadMore(fromJson.size() < pageSize ? XListViewFooter.STATE_NOMORE : XListViewFooter.STATE_NORMAL);
				xListView.setRefreshTime(DateFormat.format("HH:mm:ss", new Date()).toString());

			}
		});
	}

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		Intent service = new Intent(getActivity(), BatchDownloadService.class);
		myServiceConnection = new MyServiceConnection();
		getActivity().bindService(service, myServiceConnection, Context.BIND_AUTO_CREATE);
	}

	@Override
	public void onDestroy()
	{
		super.onDestroy();
		getActivity().unbindService(myServiceConnection);
	}

	private void refresh()
	{
		this.mPath = NetworkUtil.getDocs(mFolderId == 0 ? "" : mFolderId + "",  objects.size() + "", pageSize + "");
		new HttpUtils().send(HttpMethod.GET, DocsListFragment.this.mPath, new RequestCallBack<String>()
		{

			@Override
			public void onFailure(HttpException error, String msg)
			{
				xListView.stopRefresh();
				xListView.setRefreshTime(DateFormat.format("HH:mm:ss", new Date()) + " 刷新失败");
				failure();
			}

			@Override
			public void onSuccess(ResponseInfo<String> responseInfo)
			{
				List<Document> fromJson = new Gson().fromJson(responseInfo.result, new TypeToken<List<Document>>()
				{}.getType());

				if (fromJson.size() > 0)
				{
					objects.clear();
					for (Document document : fromJson)
					{
						objects.add(new DownloadHelper(document));
					}
					myAdapter.notifyDataSetChanged();
					success();
				}
				else
				{
					vacancy();
				}

				xListView.stopRefresh();
				xListView.stopLoadMore(fromJson.size() < pageSize ? XListViewFooter.STATE_NOMORE : XListViewFooter.STATE_NORMAL);
				xListView.setRefreshTime(DateFormat.format("HH:mm:ss", new Date()).toString());
			}
		});
	}

	@Override
	public void initData()
	{
		refresh();
	}

	/**
	 * 扩展自RequestCallBack的下载辅助类,可以进行基本的回调,文件是否存在判断等功能
	 * 
	 * @author liaorubei
	 *
	 */
	public class DownloadHelper extends RequestCallBack<File>
	{
		private Document doc;
		private CircularProgressBar mCpb;

		public DownloadHelper(Document document)
		{
			this.doc = document;
		}

		public boolean exists()
		{
			File file = new File(FolderUtil.rootDir(getActivity()), this.doc.SoundPath);
			if (file.exists())
			{
				DaoUtil.documentSaveorUpdate(this.doc, getActivity());
			}
			return file.exists();
		}

		public Document getDoc()
		{
			return doc;
		}

		@Override
		public void onFailure(HttpException error, String msg)
		{
			BatchDownloadService.isDownloading = false;
			BatchDownloadService.downloadCount++;
		}

		@Override
		public void onLoading(long total, long current, boolean isUploading)
		{
			if (this.mCpb != null && this.mCpb.getTag().equals(doc.SoundPath))
			{
				this.mCpb.setMax((int) total);
				this.mCpb.setProgress((int) current);

				this.mCpb.setCricleColor(Color.parseColor("#95a5a6"));
				this.mCpb.setCricleProgressColor(Color.parseColor("#5AB400"));
				this.mCpb.setBackgroundResource(R.drawable.download_begin);
			}
		}

		@Override
		public void onStart()
		{
			BatchDownloadService.isDownloading = true;
		}

		@Override
		public void onSuccess(ResponseInfo<File> responseInfo)
		{
			BatchDownloadService.isDownloading = false;
			BatchDownloadService.downloadCount++;

			Log.i("logi", "onSuccess=" + this.doc.Id);

			// 每次下载成功一个,就添加一条记录到数据库
			DaoUtil.documentSaveorUpdate(this.doc, getActivity());

			if (this.mCpb != null && this.mCpb.getTag().equals(doc.SoundPath))
			{
				this.mCpb.setBackgroundResource(R.drawable.download_finish);
			}

		}

		public void setProgressBar(CircularProgressBar cpb)
		{
			this.mCpb = cpb;
		}
	}

	private class MyAdapter extends BaseAdapter<DownloadHelper>
	{

		public MyAdapter(List<DownloadHelper> objects)
		{
			super(objects);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent)
		{
			final DownloadHelper helper = getItem(position);
			Document document = helper.getDoc();
			if (convertView == null)
			{
				convertView = View.inflate(getActivity(), R.layout.listitem_docslist, null);
				ViewHolder holder = new ViewHolder();
				holder.tv_title_one = (TextView) convertView.findViewById(R.id.tv_title_one);
				holder.tv_title_two = (TextView) convertView.findViewById(R.id.tv_title_two);
				holder.tv_date = (TextView) convertView.findViewById(R.id.tv_date);
				holder.tv_time = (TextView) convertView.findViewById(R.id.tv_time);
				holder.tv_size = (TextView) convertView.findViewById(R.id.tv_size);
				holder.fl_icon = convertView.findViewById(R.id.iv_download);
				holder.cpb = (CircularProgressBar) convertView.findViewById(R.id.cpb);

				convertView.setTag(holder);
			}

			ViewHolder tag = (ViewHolder) convertView.getTag();
			tag.tv_title_one.setText(document.Title);
			tag.tv_title_two.setText(document.TitleTwo);
			tag.tv_date.setText(document.DateString);
			tag.tv_time.setText(document.LengthString);
			tag.tv_size.setText(Formatter.formatFileSize(getActivity(), document.Length));
			tag.cpb.setTag(document.SoundPath);
			tag.cpb.setMax(100);
			tag.cpb.setProgress(0);
			tag.cpb.setCricleColor(Color.parseColor("#5AB400"));
			tag.cpb.setCricleProgressColor(Color.parseColor("#95a5a6"));
			tag.cpb.setBackgroundResource(helper.exists() ? R.drawable.download_finish : R.drawable.download_begin);

			helper.setProgressBar(tag.cpb);// 把进度条添加到回调管理中

			tag.cpb.setOnClickListener(new OnClickListener()
			{

				@Override
				public void onClick(View v)
				{
					Log.i("" + "点击事件");
					// 如果音频文件已经存在,或者音频文件已经在下载队列中,那么就让点击跳过代码
					if (helper.exists())
					{
						Toast.makeText(getActivity(), "已经下载", Toast.LENGTH_SHORT).show();
					}
					else if (batchDownloadBinder.isInDownloadQueue(helper))
					{
						Toast.makeText(getActivity(), "正在下载", Toast.LENGTH_SHORT).show();
					}
					else
					{
						batchDownloadBinder.addToDownloadQueue(helper);
						CircularProgressBar bar = (CircularProgressBar) v;
						bar.setMax(100);
						bar.setProgress(0);
						bar.setCricleColor(Color.parseColor("#95a5a6"));
						bar.setCricleProgressColor(Color.parseColor("#5AB400"));
					}
				}
			});

			// 在这里设置点击事件监听,而不在ListView上设置是因为当添加上拉加载时,Item错位的问题
			convertView.setOnClickListener(new OnClickListener()
			{

				@Override
				public void onClick(View v)
				{

					Intent intent = new Intent(getActivity(), PlayActivity.class);
					intent.putExtra("Id", helper.getDoc().Id);
					startActivity(intent);
				}
			});

			return convertView;
		}
	}

	private class MyServiceConnection implements ServiceConnection
	{

		@Override
		public void onServiceConnected(ComponentName name, IBinder service)
		{
			batchDownloadBinder = (BatchDownloadBinder) service;
		}

		@Override
		public void onServiceDisconnected(ComponentName name)
		{

		}
	}

	private class ViewHolder
	{
		public View fl_icon;

		public CircularProgressBar cpb;

		public TextView tv_date;
		public TextView tv_size;
		public TextView tv_time;
		public TextView tv_title_one;
		public TextView tv_title_two;
	}

}
