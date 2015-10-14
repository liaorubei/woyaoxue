package com.newclass.woyaoxue.service;

import java.io.File;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.os.SystemClock;

import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.entity.FileUploadEntity;
import com.newclass.woyaoxue.bean.Document;
import com.newclass.woyaoxue.bean.DownloadInfo;
import com.newclass.woyaoxue.util.FolderUtil;
import com.newclass.woyaoxue.util.Log;
import com.newclass.woyaoxue.util.NetworkUtil;

public class DownloadService extends Service
{

	private int downloadCount = 5;// 最多下载数
	private MyBinder myBinder;
	private DownloadManager manager;

	public DownloadService()
	{
		myBinder = new MyBinder();
		manager = new DownloadManager();

	}

	@Override
	public IBinder onBind(Intent intent)
	{
		Log.i("DownloadService--onBind");
		return myBinder;
	}

	@Override
	public void onCreate()
	{
		Log.i("DownloadService--onCreate");

		new Thread(new Runnable()
		{
			public void run()
			{
				while (true)
				{
					if (manager.toDownloadList.size() > 0)
					{
						
							DownloadInfo remove = manager.toDownloadList.remove();
							manager.downloadingMap.put(remove.Url, remove);
					

						new HttpUtils().download(remove.Url, remove.Target.getAbsolutePath(), new RequestCallBack<File>()
						{
							@Override
							public void onFailure(HttpException error, String msg)
							{}

							public void onLoading(long total, long current, boolean isUploading)
							{
								manager.change(this.getRequestUrl(), current, total);
								manager.notifyObservers();
							}

							@Override
							public void onSuccess(ResponseInfo<File> responseInfo)
							{
								Log.i("onSuccess=" + this.getRequestUrl());

								// 从正在下载列表中移除
								manager.downloadingMap.remove(this.getRequestUrl());
								// 写入数据库说明下载完成,因为在"我的下载"模块还要乃用到这些数据
							}
						});
					}
					SystemClock.sleep(2000);
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

	}

}
