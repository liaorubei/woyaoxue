package com.newclass.woyaoxue.service;

import java.io.File;

import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.newclass.woyaoxue.util.NetworkUtil;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import android.view.WindowManager;

public class DownLoadService extends Service
{

	@Override
	public IBinder onBind(Intent intent)
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId)
	{
		String path = intent.getStringExtra("path");
		String versionName = intent.getStringExtra("versionName");

		RequestCallBack<File> callback = new RequestCallBack<File>()
		{

			@Override
			public void onStart()
			{
				Log.i("logi", "下载已经开始");
			}

			@Override
			public void onLoading(long total, long current, boolean isUploading)
			{
				Log.i("logi", current + "/" + total);
			}

			@Override
			public void onSuccess(ResponseInfo<File> responseInfo)
			{
				Log.i("logi", "Path=" + responseInfo.result.getAbsolutePath());

				Builder builder = new AlertDialog.Builder(getApplicationContext());
				builder.setTitle("下载完毕");
				AlertDialog create = builder.create();
				create.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
				create.show();

			}

			@Override
			public void onFailure(HttpException error, String msg)
			{
				Log.i("logi", "onFailure=" + msg);

			}
		};

		new HttpUtils().download(NetworkUtil.getFullPath(path), "/sdcard/com.ddd" + versionName + ".apk", true, true, callback);
		return super.onStartCommand(intent, flags, startId);
	}

}
