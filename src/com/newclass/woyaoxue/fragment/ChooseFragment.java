package com.newclass.woyaoxue.fragment;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.msg.MessageBuilder;
import com.netease.nimlib.sdk.msg.MsgService;
import com.netease.nimlib.sdk.msg.constant.SessionTypeEnum;
import com.netease.nimlib.sdk.msg.model.IMMessage;
import com.newclass.woyaoxue.activity.CallActivity;
import com.newclass.woyaoxue.bean.Response;
import com.newclass.woyaoxue.bean.User;
import com.newclass.woyaoxue.util.CommonUtil;
import com.newclass.woyaoxue.util.HttpUtil;
import com.newclass.woyaoxue.util.HttpUtil.Parameters;
import com.newclass.woyaoxue.util.Log;
import com.newclass.woyaoxue.util.NetworkUtil;
import com.voc.woyaoxue.R;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

public class ChooseFragment extends Fragment implements OnClickListener
{
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		Log.i("logi", "ChooseFragment onCreate");
	}

	private Button bt_call, bt_text;
	private SurfaceView sv_video;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		View inflate = inflater.inflate(R.layout.fragment_random, null);
		initView(inflate);

		TextView textView = new TextView(getActivity());
		textView.setText("自选");
		return textView;
	}

	private void initView(View view)
	{
		sv_video = (SurfaceView) view.findViewById(R.id.sv_video);
		bt_call = (Button) view.findViewById(R.id.bt_call);
		bt_text = (Button) view.findViewById(R.id.bt_text);

		bt_call.setOnClickListener(this);
		bt_text.setOnClickListener(this);
	}

	@Override
	public void onClick(View v)
	{
		switch (v.getId())
		{
		case R.id.bt_call:



			break;

		case R.id.bt_text:
			IMMessage message = MessageBuilder.createTextMessage("bf09f7dd02e549f4a16af0cf8e9a5701", SessionTypeEnum.P2P, "来自学生的消息");
			NIMClient.getService(MsgService.class).sendMessage(message, false);
			break;

		default:
			break;
		}
	}

}
