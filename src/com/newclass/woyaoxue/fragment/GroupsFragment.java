package com.newclass.woyaoxue.fragment;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
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
import com.newclass.woyaoxue.activity.AgoraChatActivity;
import com.newclass.woyaoxue.activity.GroupActivity;
import com.newclass.woyaoxue.base.BaseAdapter;
import com.newclass.woyaoxue.bean.Group;
import com.newclass.woyaoxue.bean.Response;
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
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class GroupsFragment extends Fragment implements OnClickListener
{

	private ListView listview;
	private MyAdapter adapter;
	private List<Group> list;

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		Log.i("logi", "GroupsFragment onCreate");
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		View inflate = inflater.inflate(R.layout.fragment_groups, null);
		initView(inflate);
		initData();
		return inflate;
	}

	private void initData()
	{
		HttpUtil.post(NetworkUtil.groupSelect, null, new RequestCallBack<String>()
		{

			@Override
			public void onSuccess(ResponseInfo<String> responseInfo)
			{
				Log.i("logi", "" + responseInfo.result);
				Response<List<Group>> fromJson = new Gson().fromJson(responseInfo.result, new TypeToken<Response<List<Group>>>()
				{
				}.getType());

				if (fromJson.code == 200 && fromJson.info.size() > 0)
				{
					for (Group i : fromJson.info)
					{
						list.add(i);
					}
					adapter.notifyDataSetChanged();
				}

			}

			@Override
			public void onFailure(HttpException error, String msg)
			{
			}
		});

	}

	private void initView(View view)
	{
		listview = (ListView) view.findViewById(R.id.listview);
		list = new ArrayList<Group>();
		adapter = new MyAdapter(list);
		listview.setAdapter(adapter);
		listview.setOnItemClickListener(new OnItemClickListener()
		{

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id)
			{
				Group group = list.get(position);
				Intent intent = new Intent(getActivity(), AgoraChatActivity.class);
				intent.putExtra("channel", group.Id);
				startActivity(intent);

			}
		});
	}

	@Override
	public void onClick(View v)
	{
		switch (v.getId())
		{
		case R.id.bt_call:
			// 请求服务器

			String teacherAccid = "bf09f7dd02e549f4a16af0cf8e9a5701";
			// 发起网络通话,并监听是否拨通的通知

			VideoChatParam param = null;// new VideoChatParam(sv_video, 0);
			AVChatManager.getInstance().call(teacherAccid, AVChatType.AUDIO, param, new AVChatCallback<AVChatData>()
			{
				@Override
				public void onSuccess(AVChatData avChatData)
				{
					CommonUtil.toast("拨打成功");
				}

				@Override
				public void onFailed(int arg0)
				{
					CommonUtil.toast("网络失败,请重试");
				}

				@Override
				public void onException(Throwable arg0)
				{
					CommonUtil.toast("网络异常,请重试");
				}
			});

			break;

		case R.id.bt_text:
			IMMessage message = MessageBuilder.createTextMessage("bf09f7dd02e549f4a16af0cf8e9a5701", SessionTypeEnum.P2P, "来自学生的消息");
			NIMClient.getService(MsgService.class).sendMessage(message, false);
			break;

		default:
			break;
		}
	}

	private class MyAdapter extends BaseAdapter<Group>
	{

		public MyAdapter(List<Group> list)
		{
			super(list);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent)
		{
			final Group item = getItem(position);
			View inflate = View.inflate(getActivity(), R.layout.listitem_group, null);
			TextView tv_time = (TextView) inflate.findViewById(R.id.tv_time);
			TextView tv_host = (TextView) inflate.findViewById(R.id.tv_host);
			TextView tv_theme = (TextView) inflate.findViewById(R.id.tv_theme);

			tv_time.setText("");
			tv_host.setText(item.HostName);
			tv_theme.setText(item.Theme);

			inflate.findViewById(R.id.bt_accede).setOnClickListener(new OnClickListener()
			{

				@Override
				public void onClick(View v)
				{
					Intent intent = new Intent(getActivity(), AgoraChatActivity.class);
					intent.putExtra("channel", item.Id);
					startActivity(intent);
				}
			});

			return inflate;
		}
	}

}
