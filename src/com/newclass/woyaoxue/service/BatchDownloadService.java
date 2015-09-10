package com.newclass.woyaoxue.service;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.os.SystemClock;
import android.view.View;
import android.widget.ProgressBar;

import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.newclass.woyaoxue.bean.Document;
import com.newclass.woyaoxue.util.Log;
import com.newclass.woyaoxue.util.NetworkUtil;

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
						BatchDownloadCallBacke batchDownloadCallBacke = new BatchDownloadCallBacke(document);
						new HttpUtils().download(NetworkUtil.getFullPath(document.SoundPath), new File(getFilesDir(), document.SoundPath).getAbsolutePath(), batchDownloadCallBacke);
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

	public class BatchDownloadCallBacke extends RequestCallBack<File>
	{
		private ProgressBar progressBar;
		private Document document;

		public BatchDownloadCallBacke(Document document2)
		{
			this.document = document2;
			// this.progressBar=doc
		}

		@Override
		public void onStart()
		{
			Log.i("logi", "文件:" + document.SoundPath + "开始下载");
			isRunning = true;
		}

		@Override
		public void onLoading(long total, long current, boolean isUploading)
		{
			if (progressBar.getTag().equals(document.SoundPath))
			{
				progressBar.setMax((int) total);
				progressBar.setProgress((int) current);
			}
		}

		@Override
		public void onSuccess(ResponseInfo<File> responseInfo)
		{
			Log.i("logi", "文件下载成功,保存在:" + responseInfo.result.getAbsolutePath());
			document.SoundFileExists = true;
			isRunning = false;

		}

		@Override
		public void onFailure(HttpException error, String msg)
		{
			Log.i("logi", "文件下载失败,出错信息为:" + msg);
			document.SoundFileExists = false;
			isRunning = false;
		}
	}

}
