package com.newclass.woyaoxue.fragment;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
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
import com.netease.nimlib.sdk.rts.RTSCallback;
import com.netease.nimlib.sdk.rts.RTSManager;
import com.netease.nimlib.sdk.rts.constant.RTSTunType;
import com.netease.nimlib.sdk.rts.model.RTSData;
import com.netease.nimlib.sdk.rts.model.RTSOptions;
import com.newclass.woyaoxue.activity.CallActivity;
import com.newclass.woyaoxue.activity.LiveChatActivity;
import com.newclass.woyaoxue.bean.Answer;
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

public class RandomFragment extends Fragment implements OnClickListener
{
	private Button bt_call, bt_text, bt_board;

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
		bt_board = (Button) view.findViewById(R.id.bt_broad);

		bt_call.setOnClickListener(this);
		bt_text.setOnClickListener(this);
		bt_board.setOnClickListener(this);
	}

	@Override
	public void onClick(View v)
	{

		switch (v.getId())
		{
		case R.id.bt_call:
		{
			// 请求服务器
			Parameters parameters = new Parameters();
			parameters.add("id", getActivity().getSharedPreferences("user", Context.MODE_PRIVATE).getInt("id", 0) + "");
			HttpUtil.post(NetworkUtil.ObtainTeacher, parameters, new RequestCallBack<String>()
			{

				@Override
				public void onSuccess(ResponseInfo<String> responseInfo)
				{
					Response<User> resp = new Gson().fromJson(responseInfo.result, new TypeToken<Response<User>>()
					{}.getType());
					if (resp.code == 200)
					{

						Intent intent = new Intent(getActivity().getApplication(), CallActivity.class);
						intent.putExtra(CallActivity.KEY_TARGET, resp.info.Accid);
						intent.putExtra(CallActivity.KEY_NICKNAME, resp.info.NickName);
						intent.putExtra(CallActivity.CALL_TYPE_KEY, CallActivity.CALL_TYPE_AUDIO);
						startActivity(intent);
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
		}
			break;

		case R.id.bt_text:

			IMMessage message = MessageBuilder.createTextMessage("bf09f7dd02e549f4a16af0cf8e9a5701", SessionTypeEnum.P2P, "来自学生的消息");
			NIMClient.getService(MsgService.class).sendMessage(message, false);
			break;
		case R.id.bt_broad:
			List<RTSTunType> types = new ArrayList<RTSTunType>(1);
			types.add(RTSTunType.AUDIO);
			types.add(RTSTunType.TCP);
			String account = "";
			String pushContent = account + "发起一个会话";
			String extra = "extra_data";
			RTSOptions options = new RTSOptions().setPushContent(pushContent).setExtra(extra).setRecordAudioTun(true).setRecordTCPTun(true);

			String sessionId = RTSManager.getInstance().start(account, types, options, new RTSCallback<RTSData>()
			{

				@Override
				public void onException(Throwable arg0)
				{
					

				}

				@Override
				public void onFailed(int arg0)
				{
					

				}

				@Override
				public void onSuccess(RTSData arg0)
				{
					

				}
			});

			break;
		default:
			break;
		}
	}

}
