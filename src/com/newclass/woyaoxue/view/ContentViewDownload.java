package com.newclass.woyaoxue.view;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

public class ContentViewDownload extends ContentView
{

	public ContentViewDownload(Context context)
	{
		super(context);
		initData();
	}

	private void initData()
	{
		showView(ViewState.SUCCESS);
	}

	@Override
	public View onCreateSuccessView()
	{
		TextView textView = new TextView(getContext());
		textView.setText("ContentViewDownload");
		return textView;
	}

}
