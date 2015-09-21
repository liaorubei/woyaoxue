package com.newclass.woyaoxue.base;

import com.newclass.woyaoxue.view.ContentView;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public abstract class BaseFragment extends Fragment
{

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		ContentView contentView = new ContentView(getActivity())
		{
			@Override
			public View onCreateSuccessView()
			{
				return BaseFragment.this.onCreateSuccessView();
			}
		};

		return contentView;
	}

	protected abstract View onCreateSuccessView();
}
