package com.newclass.woyaoxue.activity;

import java.io.Serializable;

import com.netease.nimlib.sdk.avchat.AVChatCallback;
import com.netease.nimlib.sdk.avchat.AVChatManager;
import com.netease.nimlib.sdk.avchat.model.AVChatData;
import com.netease.nimlib.sdk.avchat.model.VideoChatParam;
import com.newclass.woyaoxue.util.Log;
import com.voc.woyaoxue.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class LiveChatActivity extends Activity implements OnClickListener
{
	private Button bt_accept, bt_hangup;
	private SurfaceView sv_video;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		AVChatData chatData = (AVChatData) getIntent().getSerializableExtra("chatData");
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_livechat);

		initView();
	}

	private void initView()
	{
		bt_accept = (Button) findViewById(R.id.bt_accept);
		bt_hangup = (Button) findViewById(R.id.bt_hangup);
		sv_video = (SurfaceView) findViewById(R.id.sv_video);

		bt_accept.setOnClickListener(this);
		bt_hangup.setOnClickListener(this);
	}

	@Override
	public void onClick(View v)
	{
		switch (v.getId())
		{
		case R.id.bt_accept:
			VideoChatParam videoParam = new VideoChatParam(sv_video, 0);
			AVChatManager.getInstance().toggleLocalVideo(true, null);
			AVChatManager.getInstance().accept(videoParam, new AVChatCallback<Void>()
			{

				@Override
				public void onException(Throwable arg0)
				{
					Log.i("");
				}

				@Override
				public void onFailed(int arg0)
				{
					Log.i("");
				}

				@Override
				public void onSuccess(Void arg0)
				{
					Log.i("");
				}
			});
			break;
		case R.id.bt_hangup:
			AVChatManager.getInstance().hangUp(new AVChatCallback<Void>()
			{

				@Override
				public void onSuccess(Void arg0)
				{
					Log.i("");

				}

				@Override
				public void onFailed(int arg0)
				{
					Log.i("");

				}

				@Override
				public void onException(Throwable arg0)
				{
					Log.i("");

				}
			});
			break;
		default:
			break;
		}

	}
}
