package com.newclass.woyaoxue.base;

import com.newclass.woyaoxue.view.ContentView;
import com.newclass.woyaoxue.view.ContentView.ViewState;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public abstract class BaseFragment<T> extends Fragment
{

	private ContentView contentView;

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

			};
		}

		return contentView;
	}

	public void onFailure()
	{
		contentView.showView(ViewState.FAILURE);
	}

	public void onSuccess(T data)
	{
		if (data != null)
		{
			showData(data);
			contentView.showView(ViewState.SUCCESS);
		}
		else
		{
			contentView.showView(ViewState.EMPTY);
		}

	}

	public abstract void showData(T data);

}
