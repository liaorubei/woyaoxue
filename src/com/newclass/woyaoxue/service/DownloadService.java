package com.newclass.woyaoxue.service;

import java.io.File;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Observable;

import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.SystemClock;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationCompat.Builder;

import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.newclass.woyaoxue.bean.DownloadInfo;
import com.newclass.woyaoxue.bean.database.Database;
import com.newclass.woyaoxue.util.Log;
import com.voc.woyaoxue.R;

public class DownloadService extends Service
{

	protected static final int NOTIFY = 0;
	private MyBinder binder;
	private Database database;
	private int downloadCount = 5;// 最多下载数
	private DownloadManager manager;

	private NotificationManager notificationManager;
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

		// 把数据库里面还没有下载完毕的任务取出来重新下载
		database.docsSelectUnfinishedDownload();

		// 通知管理器
		notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

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
						new HttpUtils().download(info.Url, info.Target.getAbsolutePath(), new MyRequestCallBack());
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

	@Override
	public void onDestroy()
	{
		database.closeConnection();
	}

	public class DownloadManager extends Observable
	{
		private Map<String, DownloadInfo> downloadingMap;// 正在下载列表
		private LinkedList<DownloadInfo> toDownloadList;// 等待下载列表

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

		public boolean contains(DownloadInfo path)
		{
			return toDownloadList.contains(path) || downloadingMap.containsKey(path.Url);
		}

		public void enqueue(DownloadInfo path)
		{
			toDownloadList.add(path);
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

	public class MyBinder extends Binder
	{

		public DownloadManager getDownloadManager()
		{
			return manager;
		}

	}

	private class MyRequestCallBack extends RequestCallBack<File>
	{
		private Builder builder;
		private DownloadInfo info;

		@Override
		public void onFailure(HttpException error, String msg)
		{
			// 下载失败了,日志通知下载失败并移除数据库里的数据
			Log.i(manager.downloadingMap.get(this.getRequestUrl()).Title + " 下载失败");

			builder.setContentText("下载失败");
			notificationManager.notify(info.Id, builder.build());
			database.docsDeleteByDownloadPath(this.getRequestUrl());
		}

		public void onLoading(long total, long current, boolean isUploading)
		{
			builder.setProgress((int) total, (int) current, false);
			builder.setContentText("正在下载");
			notificationManager.notify(info.Id, builder.build());
			manager.change(this.getRequestUrl(), current, total);
		}

		public void onStart()
		{
			info = manager.downloadingMap.get(this.getRequestUrl());
			builder = new NotificationCompat.Builder(DownloadService.this);

			builder.setContentTitle(info.Title);
			builder.setContentText("开始下载");
			builder.setSmallIcon(R.drawable.ic_launcher);
			builder.setProgress(100, 0, false);

			notificationManager.notify(info.Id, builder.build());
		}

		@Override
		public void onSuccess(ResponseInfo<File> responseInfo)
		{
			// 下载成功之后要先移除下载列表里面的任务,并更新数据库之后再通知更新

			builder.setContentText("下载完成");
			builder.setProgress(100, 100, false);
			notificationManager.notify(info.Id, builder.build());

			// 移除并更新数据库
			manager.downloadingMap.remove(this.getRequestUrl());
			database.docsUpdateSuccessByDownloadPath(this.getRequestUrl());

			// 通知更新
			manager.notifyObservers();
		}
	}

}
