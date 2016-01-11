package com.newclass.woyaoxue.view;

import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.newclass.woyaoxue.util.HttpUtil;
import com.newclass.woyaoxue.util.NetworkUtil;
import com.newclass.woyaoxue.view.ContentView.ViewState;
import com.voc.woyaoxue.R;

import android.content.Context;
import android.view.View;

public class ContentViewFolder extends ContentView
{
	private int mLevelId;

	public ContentViewFolder(Context context, int levelId)
	{
		super(context);
		this.mLevelId = levelId;
		initData();
	}

	private void initData()
	{
		HttpUtil.post(NetworkUtil.getFolders(mLevelId), null, new RequestCallBack<String>()
		{

			@Override
			public void onSuccess(ResponseInfo<String> responseInfo)
			{
				// TODO Auto-generated method stub

				showView(ViewState.SUCCESS);
			}

			@Override
			public void onFailure(HttpException error, String msg)
			{
				// TODO Auto-generated method stub
				showView(ViewState.FAILURE);
			}
		});
	}

	@Override
	public View onCreateSuccessView()
	{
		View inflate = View.inflate(getContext(), R.layout.fragment_folder, null);
		return inflate;
	}

}
