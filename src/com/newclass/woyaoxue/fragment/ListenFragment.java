package com.newclass.woyaoxue.fragment;

import com.newclass.woyaoxue.view.ContentView;
import com.newclass.woyaoxue.view.ContentViewListen;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class ListenFragment extends Fragment
{
	private static final String TAG = "ListenFragment";
	private ContentView contentView;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		if (contentView == null)
		{
			contentView = new ContentViewListen(getActivity());
		}
		return contentView;
	}
}
