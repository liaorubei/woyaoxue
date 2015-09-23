package com.newclass.woyaoxue.view;

import android.content.Context;
import android.view.View;
import android.widget.FrameLayout;

import com.lidroid.xutils.http.callback.RequestCallBack;
import com.newclass.woyaoxue.util.Log;
import com.voc.woyaoxue.R;

public abstract class ContentView extends FrameLayout
{
	private ViewState CURRENTSTATE = ViewState.LOADING;
	private View successView;
	private View failureView;
	private View emptyView;
	private View loadingView;
	private View contentView;

	public ContentView(Context context)
	{
		super(context);
		contentView = View.inflate(context, R.layout.contentview, null);
		this.addView(contentView);
		initView();
	}

	private void initView()
	{
		loadingView = onCreateLoadingView();
		failureView = onCreateFailureView();
		emptyView = onCreateEmptyView();
		successView = onCreateSuccessView();
		if (successView!=null)
		{
			this.addView(successView);
		}
		showView(ViewState.LOADING);
	}

	public void showView(ViewState viewState)
	{
		this.CURRENTSTATE = viewState;
		if (loadingView != null)
		{
			loadingView.setVisibility(this.CURRENTSTATE == ViewState.LOADING ? View.VISIBLE : View.INVISIBLE);
		}

		if (failureView != null)
		{
			failureView.setVisibility(this.CURRENTSTATE == ViewState.FAILURE ? View.VISIBLE : View.INVISIBLE);
		}

		if (emptyView != null)
		{
			emptyView.setVisibility(this.CURRENTSTATE == ViewState.EMPTY ? View.VISIBLE : View.INVISIBLE);
		}

		if (successView != null)
		{
			successView.setVisibility(this.CURRENTSTATE == ViewState.SUCCESS ? View.VISIBLE : View.INVISIBLE);
		}
	}

	private View onCreateEmptyView()
	{
		return contentView.findViewById(R.id.rl_empty);
	}

	private View onCreateLoadingView()
	{
		return contentView.findViewById(R.id.rl_loading);
	}

	private View onCreateFailureView()
	{
		return contentView.findViewById(R.id.rl_failure);
	}

	public abstract View onCreateSuccessView();

	public static enum ViewState
	{
		LOADING, SUCCESS, FAILURE, EMPTY
	}

}
