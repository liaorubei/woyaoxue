package com.newclass.woyaoxue.service;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.newclass.woyaoxue.bean.Document;
import com.newclass.woyaoxue.util.NetworkUtil;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;

/**
 * 批量音频下载服务
 * 
 * @author liaorubei
 *
 */
public class BatchDownloadService extends Service
{
	private List<Document> downloadLists;// 待下载的
	private BatchDownloadBinder batchDownloadBinder;
	protected boolean isRunning = false;

	@Override
	public void onCreate()
	{
		Log.i("logi", "BatchDownloadService--" + "onCreate");
		downloadLists = new ArrayList<Document>();
		new Thread(new Runnable()
		{
			public void run()
			{
				while (true)
				{
					if (downloadLists.size() > 0 && !isRunning)
					{
						Document document = downloadLists.remove(0);
						new HttpUtils().download(NetworkUtil.getFullPath(document.SoundPath), document.SoundPath, new RequestCallBack<File>()
						{
							public void onStart()
							{
								isRunning = true;
							};

							public void onLoading(long total, long current, boolean isUploading)
							{
								Log.i("logi", "total=" + total + " current=" + current + " isUploading=" + isUploading);
							};

							@Override
							public void onSuccess(ResponseInfo<File> responseInfo)
							{
								isRunning = false;

							}

							@Override
							public void onFailure(HttpException error, String msg)
							{
								isRunning = false;

							}
						});
					}

					SystemClock.sleep(1000);// 休眠一秒
				}

			}
		}).start();
		super.onCreate();
	}

	@Override
	public IBinder onBind(Intent intent)
	{
		if (batchDownloadBinder == null)
		{
			synchronized (this)
			{
				if (batchDownloadBinder == null)
				{
					batchDownloadBinder = new BatchDownloadBinder();
				}
			}
		}
		return batchDownloadBinder;
	}

	public class BatchDownloadBinder extends Binder
	{

		public void addToDownloadQueue(Document document, View v)
		{
			downloadLists.add(document);
		}

		public boolean isInDownloadQueue(Document document)
		{
			return downloadLists.contains(document);
		}

	}

}
