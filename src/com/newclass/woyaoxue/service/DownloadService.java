package com.newclass.woyaoxue.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class DownloadService extends Service
{
	@Override
	public void onCreate()
	{
		super.onCreate();

		new Thread()
		{
			public void run()
			{

				
				
				
			}
		}.start();

	}

	@Override
	public IBinder onBind(Intent intent)
	{

		return null;
	}

}
