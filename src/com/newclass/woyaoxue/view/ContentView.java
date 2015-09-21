package com.newclass.woyaoxue.view;

import android.content.Context;
import android.view.View;
import android.widget.FrameLayout;

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
		initView();
	}

	private void initView()
	{
		loadingView = onCreateLoadingView();
		if (loadingView != null)
		{
			this.addView(loadingView);
		}

		failureView = onCreateFailureView();
		if (failureView != null)
		{
			this.addView(failureView);
		}

		emptyView = onCreateEmptyView();
		if (emptyView != null)
		{
			this.addView(emptyView);
		}

		showView();

	}

	private void showView()
	{
		// 要保证操作是在主线程进行

		showViewOnMainThread();

	}

	private void showViewOnMainThread()
	{
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

	enum ViewState
	{
		LOADING, SUCCESS, FAILURE, EMPTY
	}

}
