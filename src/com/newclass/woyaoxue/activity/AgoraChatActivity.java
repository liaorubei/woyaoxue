package com.newclass.woyaoxue.activity;

import com.newclass.woyaoxue.util.Log;
import com.voc.woyaoxue.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.SurfaceView;
import android.widget.FrameLayout;
import android.widget.TextView;
import io.agora.rtc.IRtcEngineEventHandler;
import io.agora.rtc.RtcEngine;
import io.agora.rtc.video.VideoCanvas;

//声网专用视频聊天界面
public class AgoraChatActivity extends Activity
{
	public static final String vendorKey = "a94963353d4d47d6be2bdecbaaa060a9";// "6D7A26A1D3554A54A9F43BE6797FE3E2";
	protected static final int JOIN_USER = 0;
	private FrameLayout fl_native;
	private FrameLayout fl_remote;
	private RtcEngine rtcEngine;

	private Handler handler = new Handler()
	{
		public void handleMessage(android.os.Message msg)
		{
			switch (msg.what)
			{
			case JOIN_USER:
				joinUser(msg.obj);
				break;

			default:
				break;
			}
		};
	};

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		Intent intent = getIntent();
		String channel = intent.getStringExtra("channel");
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_agorachat);

		initView();

		SurfaceView nativeSurfaceView = RtcEngine.CreateRendererView(this);
		SurfaceView remoteSurfaceView = RtcEngine.CreateRendererView(this);
		FrameLayout.LayoutParams p = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);
		fl_native.addView(nativeSurfaceView, p);
		fl_remote.addView(remoteSurfaceView, p);

		rtcEngine = RtcEngine.create(this, vendorKey, new IRtcEngineEventHandler()
		{

			@Override
			public void onJoinChannelSuccess(String channel, int uid, int elapsed)
			{
				Log.i("logi", "onJoinChannelSuccess channel:" + channel + " uid:" + uid + " elapsed:" + elapsed);
			}

			@Override
			public void onUserJoined(int uid, int elapsed)
			{
				Log.i("logi", "onUserJoined" + " uid:" + uid + " elapsed:" + elapsed);

				Message msg = handler.obtainMessage();
				msg.what = JOIN_USER;
				msg.obj = uid;
				handler.sendMessage(msg);
			}

			@Override
			public void onUserOffline(int uid)
			{
				Log.i("logi", "onUserOffline uid:" + uid);
			}

			@Override
			public void onLeaveChannel(SessionStats stats)
			{
				Log.i("logi", "onLeaveChannel stats.totalDuration:" + stats.totalDuration);
			}

		});
		// rtcEngine.enableVideo();
		rtcEngine.setupLocalVideo(new VideoCanvas(nativeSurfaceView));
		rtcEngine.setupRemoteVideo(new VideoCanvas(remoteSurfaceView));
		rtcEngine.setVideoResolution(640, 360);
		rtcEngine.joinChannel(vendorKey, channel, "", 0);
	}

	private void joinUser(Object obj)
	{
		TextView child = new TextView(AgoraChatActivity.this);
		child.setText("uid:" + obj);
		fl_remote.addView(child);
	}

	private void initView()
	{
		fl_native = (FrameLayout) findViewById(R.id.fl_native);
		fl_remote = (FrameLayout) findViewById(R.id.fl_remote);
	}

	@Override
	protected void onDestroy()
	{
		rtcEngine.leaveChannel();
		super.onDestroy();
	}
}
