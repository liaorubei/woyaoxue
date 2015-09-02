package com.newclass.woyaoxue.fragment;

import java.util.ArrayList;
import java.util.List;
import com.voc.woyaoxue.R;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.newclass.woyaoxue.activity.ListActivity;
import com.newclass.woyaoxue.activity.PlayActivity;
import com.newclass.woyaoxue.bean.Level;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.TextView;

public class CategoryFragment extends Fragment
{
	@ViewInject(R.id.listView)
	private ListView listView;

	private MyAdapter adapter;
	private List<Level> objs;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		View inflate = inflater.inflate(R.layout.fragment_category, container, false);
		ViewUtils.inject(this, inflate);

		objs = new ArrayList<Level>();
		adapter = new MyAdapter(getActivity(), R.layout.listitem_category, objs);

		listView.setAdapter(adapter);
		listView.setOnItemClickListener(new OnItemClickListener()
		{

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id)
			{
				Intent intent = new Intent(getActivity(), ListActivity.class);
				intent.putExtra("levelId", objs.get(position).Id);
				startActivity(intent);

			}
		});
		return inflate;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState)
	{

		super.onActivityCreated(savedInstanceState);
	}

	public void fillData(List<Level> list)
	{
		if (objs == null)
		{
			objs = new ArrayList<Level>();
		}
		objs.clear();
		for (Level level : list)
		{
			objs.add(level);
		}
		if (adapter == null)
		{
			Log.i("logi", getActivity() + "");
			adapter = new MyAdapter(getActivity(), R.layout.listitem_category, objs);
		}
		adapter.notifyDataSetChanged();
	}

	private class MyAdapter extends ArrayAdapter<Level>
	{
		private int mResource;
		private List<Level> objs;

		private TextView tv_title;

		private TextView tv_count;

		public MyAdapter(Context context, int resource, List<Level> objects)
		{
			super(context, resource, objects);
			this.mResource = resource;
			this.objs = objects;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent)
		{
			Level level = this.objs.get(position);

			View view = View.inflate(getActivity(), this.mResource, null);

			tv_title = (TextView) view.findViewById(R.id.tv_title);
			tv_count = (TextView) view.findViewById(R.id.tv_count);

			tv_title.setText("分类:" + level.LevelName);
			tv_count.setText("文章:" + level.DocCount + "编");

			return view;
		}

	}

}
