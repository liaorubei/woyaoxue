package com.newclass.woyaoxue.activity;

import java.nio.charset.CharsetDecoder;

import com.netease.nimlib.sdk.avchat.AVChatCallback;
import com.netease.nimlib.sdk.avchat.AVChatManager;
import com.netease.nimlib.sdk.avchat.model.AVChatData;
import com.netease.nimlib.sdk.avchat.model.VideoChatParam;
import com.newclass.woyaoxue.util.Log;
import com.voc.woyaoxue.R;

import android.app.Activity;
import android.os.Bundle;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class LiveChatActivity extends Activity implements OnClickListener
{
	private Button bt_accept, bt_hangup;
	private SurfaceView sv_video;
	private TextView tv_type;
	private AVChatData chatData;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		chatData = (AVChatData) getIntent().getSerializableExtra("chatData");
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_livechat);

		initView();

		tv_type.setText("你有一个" + chatData.getChatType() + "类型的来电");
	}

	private void initView()
	{
		bt_accept = (Button) findViewById(R.id.bt_accept);
		bt_hangup = (Button) findViewById(R.id.bt_hangup);
		sv_video = (SurfaceView) findViewById(R.id.sv_video);
		tv_type = (TextView) findViewById(R.id.tv_type);

		bt_accept.setOnClickListener(this);
		bt_hangup.setOnClickListener(this);
	}

	@Override
	public void onClick(View v)
	{
		switch (v.getId())
		{
		case R.id.bt_accept:
			switch (chatData.getChatType())
			{
			case AUDIO:

				break;
			case VIDEO:

				break;
			default:
				break;
			}
			VideoChatParam videoParam = null;// new VideoChatParam(sv_video, 0);
			AVChatManager.getInstance().toggleLocalVideo(true, null);
			AVChatManager.getInstance().accept(videoParam, new AVChatCallback<Void>()
			{

				@Override
				public void onException(Throwable arg0)
				{
					Log.i("接受异常");
				}

				@Override
				public void onFailed(int arg0)
				{
					Log.i("接受失败");
				}

				@Override
				public void onSuccess(Void arg0)
				{
					bt_accept.setEnabled(false);
				}
			});
			break;
		case R.id.bt_hangup:
			// 拒绝来电,或者在挂断通话
			AVChatManager.getInstance().hangUp(new AVChatCallback<Void>()
			{

				@Override
				public void onSuccess(Void arg0)
				{
					finish();
				}

				@Override
				public void onFailed(int arg0)
				{
					Log.i("挂断失败");
				}

				@Override
				public void onException(Throwable arg0)
				{
					Log.i("挂断异常");
				}
			});
			break;
		default:
			break;
		}

	}
}
