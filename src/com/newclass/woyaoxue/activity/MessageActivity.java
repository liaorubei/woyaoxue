package com.newclass.woyaoxue.activity;

import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.msg.MessageBuilder;
import com.netease.nimlib.sdk.msg.MsgService;
import com.netease.nimlib.sdk.msg.constant.SessionTypeEnum;
import com.netease.nimlib.sdk.msg.model.IMMessage;
import com.voc.woyaoxue.R;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class MessageActivity extends Activity implements OnClickListener
{
	private Button bt_text, bt_video;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_docs);

		initView();

	}

	private void initView()
	{
		bt_text = (Button) findViewById(R.id.bt_text);
		bt_video = (Button) findViewById(R.id.bt_video);

		bt_text.setOnClickListener(this);
		bt_video.setOnClickListener(this);
	}

	@Override
	public void onClick(View v)
	{
		switch (v.getId())
		{
		case R.id.bt_text:
			IMMessage message=MessageBuilder.createTextMessage("accid", SessionTypeEnum.P2P	, "文本内容") ;
			NIMClient.getService(MsgService.class).sendMessage(message, false);

			break;

		default:
			break;
		}

	}
}
