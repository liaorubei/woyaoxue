package com.newclass.woyaoxue.service;

import java.util.ArrayList;
import java.util.List;

import com.newclass.woyaoxue.bean.Document;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

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

	@Override
	public void onCreate()
	{
		downloadLists = new ArrayList<Document>();
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

		public void addToDownloadLists(Document document)
		{
			downloadLists.add(document);

		}

		public boolean isInDownloadQueue(Document document)
		{
			return downloadLists.contains(document);
		}

	}

}
