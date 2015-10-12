package com.newclass.woyaoxue.service;

import java.io.File;
import java.util.LinkedList;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.os.SystemClock;

import com.lidroid.xutils.HttpUtils;
import com.newclass.woyaoxue.fragment.DocsListFragment.DownloadHelper;
import com.newclass.woyaoxue.util.FolderUtil;
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
	public static boolean isDownloading = false;
	public static int downloadCount = 5;
	private LinkedList<DownloadHelper> downloadQueue;
	private BatchDownloadBinder batchDownloadBinder;
	protected boolean isRunning = false;

	protected File root;

	@Override
	public void onCreate()
	{
		root = FolderUtil.rootDir(getApplication());

		Log.i("logi", "BatchDownloadService--" + "onCreate");
		downloadQueue = new LinkedList<DownloadHelper>();
		new Thread(new Runnable()
		{
			public void run()
			{
				while (true)
				{

					if (downloadQueue.size() > 0 && !isDownloading && downloadCount > 0)
					{
						DownloadHelper first = downloadQueue.pollFirst();
						new HttpUtils().download(NetworkUtil.getFullPath(first.getDoc().SoundPath), new File(root, first.getDoc().SoundPath).getAbsolutePath(), first);
						downloadCount--;
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

		public boolean isInDownloadQueue(DownloadHelper callBack)
		{
			return downloadQueue.contains(callBack);
		}

		public void addToDownloadQueue(DownloadHelper callBack)
		{
			downloadQueue.add(callBack);
		}

	}
}
