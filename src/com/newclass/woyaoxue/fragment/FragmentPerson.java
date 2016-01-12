package com.newclass.woyaoxue.fragment;

import com.newclass.woyaoxue.view.ContentView;
import com.newclass.woyaoxue.view.ContentViewPerson;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class FragmentPerson extends Fragment
{
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		ContentView textView = new ContentViewPerson(getActivity());
		return textView;
	}

}
