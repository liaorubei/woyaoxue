package com.newclass.woyaoxue.base;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.newclass.woyaoxue.view.ContentView;
import com.newclass.woyaoxue.view.ContentView.ViewState;

public abstract class BaseFragment extends Fragment
{

	private ContentView contentView;

	public abstract void initData();

	protected abstract View initView();

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		if (contentView == null)
		{
			contentView = new ContentView(getActivity())
			{
				@Override
				public View onCreateSuccessView()
				{
					return BaseFragment.this.initView();
				}

				@Override
				public void initData()
				{
					BaseFragment.this.initData();
				}
			};
		}

		return contentView;
	}

	protected void success()
	{
		contentView.showView(ViewState.SUCCESS);
	}

	protected void failure()
	{
		contentView.showView(ViewState.FAILURE);
	}

	protected void vacancy()
	{
		contentView.showView(ViewState.EMPTY);
	}

}
