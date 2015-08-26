package com.newclass.woyaoxue.base;

import java.util.List;

import com.lidroid.xutils.ViewUtils;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

public class BaseAdapter<T> extends ArrayAdapter
{

	private int mResource;
	private List<T> data;

	public BaseAdapter(Context context, int resource, List<T> objects)
	{
		super(context, resource, objects);
		this.mResource = resource;
		this.data = objects;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent)
	{
		T t = this.data.get(position);
		View view = View.inflate(getContext(), this.mResource, null);
		ViewUtils.inject(view);

		return view;
	}

}
