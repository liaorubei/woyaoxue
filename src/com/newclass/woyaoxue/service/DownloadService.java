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
	private LinkedList<Document> toDownloadList;// 等待下载列表
	private Map<String, Document> downloadingMap;// 正在下载列表
	private int downloadCount = 5;// 最多下载数

	private MyBinder myBinder;

	private File root;
	private DownloadManager manager;
	boolean observable = false;

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
		root = FolderUtil.rootDir(this);
		Log.i("DownloadService--onCreate");
		
		toDownloadList = new LinkedList<Document>();
		downloadingMap = new HashMap<String, Document>();
	

		new Thread(new Runnable()
		{
			public void run()
			{
				while (true)
				{
					if (toDownloadList.size() > 0)
					{
						Document remove = toDownloadList.remove();

						String url = NetworkUtil.getFullPath(remove.SoundPath);

						downloadingMap.put(url, remove);
						Log.i("正在下载:" + remove.Title);

						new HttpUtils().download(url, new File(root, remove.SoundPath).getAbsolutePath(), new RequestCallBack<File>()
						{
							@Override
							public void onFailure(HttpException error, String msg)
							{}

							public void onLoading(long total, long current, boolean isUploading)
							{
								if (observable)
								{
									manager.Chang(this.getRequestUrl(), current, total);
									manager.notifyObservers();
								}

							}

							@Override
							public void onSuccess(ResponseInfo<File> responseInfo)
							{
								Log.i("onSuccess=" + this.getRequestUrl());
								Document document = downloadingMap.remove(this.getRequestUrl());
							}
						});
					}

					observable = true;
					SystemClock.sleep(2000);
					observable = false;
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

		public void add(Document document)
		{
			toDownloadList.add(document);

		}

		public boolean contains(Document document)
		{
			return toDownloadList.contains(document) || downloadingMap.containsValue(document);

		}

	}

	public class DownloadManager extends Observable
	{
		private Map<String, DownloadInfo> infos;

		public void Chang(String requestUrl, long current, long total)
		{
			DownloadInfo downloadInfo = infos.get(requestUrl);
			downloadInfo.Current = current;
			downloadInfo.Total = total;
			setChanged();
		}

	}

}
