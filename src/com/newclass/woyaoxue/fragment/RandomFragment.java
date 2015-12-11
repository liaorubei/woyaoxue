package com.newclass.woyaoxue.fragment;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import com.google.gson.Gson;
import com.lidroid.xutils.db.table.KeyValue;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.avchat.AVChatCallback;
import com.netease.nimlib.sdk.avchat.AVChatManager;
import com.netease.nimlib.sdk.avchat.constant.AVChatType;
import com.netease.nimlib.sdk.avchat.model.AVChatData;
import com.netease.nimlib.sdk.avchat.model.VideoChatParam;
import com.netease.nimlib.sdk.msg.MessageBuilder;
import com.netease.nimlib.sdk.msg.MsgService;
import com.netease.nimlib.sdk.msg.constant.SessionTypeEnum;
import com.netease.nimlib.sdk.msg.model.IMMessage;
import com.newclass.woyaoxue.activity.LiveChatActivity;
import com.newclass.woyaoxue.bean.Answer;
import com.newclass.woyaoxue.util.CommonUtil;
import com.newclass.woyaoxue.util.HttpUtil;
import com.newclass.woyaoxue.util.HttpUtil.Parameters;
import com.newclass.woyaoxue.util.Log;
import com.newclass.woyaoxue.util.NetworkUtil;
import com.voc.woyaoxue.R;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;

public class RandomFragment extends Fragment implements OnClickListener
{
	private Button bt_call, bt_text;

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		Log.i("logi", "RandomFragment onCreate");
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		View inflate = inflater.inflate(R.layout.fragment_random, null);
		initView(inflate);
		return inflate;
	}

	private void initView(View view)
	{
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
			Parameters parameters = new Parameters();
			parameters.add("", "");

			HttpUtil.post(NetworkUtil.studentCall, parameters, new RequestCallBack<String>()
			{

				@Override
				public void onSuccess(ResponseInfo<String> responseInfo)
				{
					Log.i("logi", "result" + responseInfo.result);

					JSONObject object;
					try
					{
						object = new JSONObject(responseInfo.result);
						int code = object.getInt("code");
						String accid = object.getString("accid");

						if (code == 200)
						{
							Intent intent = new Intent(getActivity(), LiveChatActivity.class);
							intent.putExtra("target", accid);
							intent.putExtra(LiveChatActivity.CHATSTATE_KEY, LiveChatActivity.CHATSTATE_CALL);
							startActivity(intent);
						}
						else
						{
							CommonUtil.toast(object.getString("desc"));
						}
					}
					catch (Exception e)
					{
						e.printStackTrace();
					}
					bt_call.setEnabled(true);
				}

				@Override
				public void onFailure(HttpException error, String msg)
				{
					CommonUtil.toast("网络异常");
					bt_call.setEnabled(true);
				}
			});

			bt_call.setEnabled(false);
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
