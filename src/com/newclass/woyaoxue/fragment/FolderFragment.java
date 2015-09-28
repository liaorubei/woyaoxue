package com.newclass.woyaoxue.fragment;

import java.util.List;

import android.view.View;
import android.widget.ListView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;
import com.newclass.woyaoxue.base.BaseFragment;
import com.newclass.woyaoxue.bean.Folder;
import com.newclass.woyaoxue.util.NetworkUtil;
import com.newclass.woyaoxue.view.XListView;
import com.newclass.woyaoxue.view.ContentView.ViewState;
import com.voc.woyaoxue.R;

public class FolderFragment extends BaseFragment<List<Folder>>
{
	private int mLevelId;

	public FolderFragment(int levelId)
	{
		this.mLevelId = levelId;
	}

	private XListView xListView;

	@Override
	protected View initView()
	{
		View view = View.inflate(getContext(), R.layout.fragment_folder, null);
		xListView = (XListView) view.findViewById(R.id.xListView);
		return view;
	}

	@Override
	public void showData(List<Folder> data)
	{
		if (data != null && data.size() > 0)
		{

		}
		else
		{

			new HttpUtils().send(HttpMethod.GET,NetworkUtil.getFolders(this.mLevelId), new RequestCallBack<String>()
			{

				@Override
				public void onSuccess(ResponseInfo<String> responseInfo)
				{
					List<Folder> fromJson = new Gson().fromJson(responseInfo.result, new TypeToken<List<Folder>>()
					{}.getType());
					if (fromJson.size() > 0)
					{

						success();
					}
					else
					{
						vacancy();
					}
				}

				@Override
				public void onFailure(HttpException error, String msg)
				{
					failure();

				}
			});

		}

	}

	protected void success()
	{
		this.contentView.showView(ViewState.SUCCESS);

	}

	protected void vacancy()
	{
		this.contentView.showView(ViewState.EMPTY);

	}

	protected void failure()
	{
		this.contentView.showView(ViewState.FAILURE);

	}

}
