package com.newclass.woyaoxue.base;

import java.util.List;

import com.lidroid.xutils.ViewUtils;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

public class BaseAdapter<T> extends android.widget.BaseAdapter
{
	private List<T> data;
	private Context mContext;
	private int mResource;

	public BaseAdapter(Context context, List<T> objects)
	{
		this.mContext = context;
		this.data = objects;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent)
	{
		T t = this.data.get(position);
		if (convertView == null)
		{
			convertView = View.inflate(this.mContext, this.mResource, null);
		}
		else
		{}

		return convertView;
	}

	@Override
	public int getCount()
	{
		return this.data.size();
	}

	@Override
	public T getItem(int position)
	{
		return this.data.get(position);
	}

	@Override
	public long getItemId(int position)
	{
		return position;
	}

}
