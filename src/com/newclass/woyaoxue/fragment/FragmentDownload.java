package com.newclass.woyaoxue.fragment;

import com.newclass.woyaoxue.view.ContentViewDownload;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class FragmentDownload extends Fragment
{

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		return new ContentViewDownload(getActivity());
	}
}
