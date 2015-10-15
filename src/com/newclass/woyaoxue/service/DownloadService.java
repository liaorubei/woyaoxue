package com.newclass.woyaoxue.service;

import java.io.File;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Observable;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.SystemClock;

import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.newclass.woyaoxue.bean.DownloadInfo;
import com.newclass.woyaoxue.bean.database.Database;
import com.newclass.woyaoxue.util.Log;

public class DownloadService extends Service
{

	protected static final int NOTIFY = 0;
	private int downloadCount = 5;// 最多下载数
	private MyBinder binder;
	private DownloadManager manager;
	private Database database;

	Handler handler = new Handler()
	{

		@Override
		public void handleMessage(Message msg)
		{
			switch (msg.what)
			{

			case NOTIFY:
				manager.notifyObservers();
				break;

			default:
				break;
			}
		}
	};

	@Override
	public IBinder onBind(Intent intent)
	{
		Log.i("DownloadService--onBind");
		return binder;
	}

	@Override
	public void onCreate()
	{
		Log.i("DownloadService--onCreate");
		binder = new MyBinder();
		manager = new DownloadManager();
		database = new Database(this);
		
		//把数据库里面还没有下载完毕的任务取出来重新下载
		database.docsSelectUnfinishedDownload();

		new Thread(new Runnable()
		{
			public void run()
			{
				while (true)
				{
					if (manager.toDownloadList.size() > 0)
					{
						DownloadInfo info = manager.toDownloadList.remove();
						manager.downloadingMap.put(info.Url, info);
						new HttpUtils().download(info.Url, info.Target.getAbsolutePath(), new RequestCallBack<File>()
						{
							public void onLoading(long total, long current, boolean isUploading)
							{
								manager.change(this.getRequestUrl(), current, total);
							}

							@Override
							public void onSuccess(ResponseInfo<File> responseInfo)
							{
								Log.i(manager.downloadingMap.get(this.getRequestUrl()).Title + " 下载成功");
								// 下载成功之后要先移除下载列表里面的任务,并更新数据库之后再通知更新

								// 移除并更新数据库
								manager.downloadingMap.remove(this.getRequestUrl());
								database.docsUpdateSuccessByDownloadPath(this.getRequestUrl());

								// 通知更新
								manager.notifyObservers();
							}

							@Override
							public void onFailure(HttpException error, String msg)
							{
								// 下载失败了,日志通知下载失败并移除数据库里的数据
								Log.i(manager.downloadingMap.get(this.getRequestUrl()).Title + " 下载失败");
								database.docsDeleteByDownloadPath(this.getRequestUrl());
							}
						});
					}

					if (manager.toDownloadList.size() + manager.downloadingMap.size() > 0)
					{
						handler.sendEmptyMessage(NOTIFY);
					}
					SystemClock.sleep(1000);
				}
			}
		}).start();

	}

	public class MyBinder extends Binder
	{

		public DownloadManager getDownloadManager()
		{
			return manager;
		}

	}

	public class DownloadManager extends Observable
	{
		private LinkedList<DownloadInfo> toDownloadList;// 等待下载列表
		private Map<String, DownloadInfo> downloadingMap;// 正在下载列表

		DownloadManager()
		{
			toDownloadList = new LinkedList<DownloadInfo>();
			downloadingMap = new HashMap<String, DownloadInfo>();
		}

		public void change(String key, long current, long total)
		{
			DownloadInfo down = this.downloadingMap.get(key);
			down.Current = current;
			down.Total = total;

			// 标识数据已经发生了改变
			setChanged();
		}

		public void enqueue(DownloadInfo path)
		{
			toDownloadList.add(path);
		}

		public boolean contains(DownloadInfo path)
		{
			return toDownloadList.contains(path) || downloadingMap.containsKey(path.Url);
		}

		public DownloadInfo get(String key)
		{
			DownloadInfo downloadInfo = this.downloadingMap.get(key);
			if (downloadInfo == null)
			{
				for (DownloadInfo i : toDownloadList)
				{
					if (i.Url.equals(key))
					{
						downloadInfo = i;
					}
				}
			}
			return downloadInfo;
		}

		public int size()
		{
			return this.toDownloadList.size() + downloadingMap.size();
		}

	}

}
