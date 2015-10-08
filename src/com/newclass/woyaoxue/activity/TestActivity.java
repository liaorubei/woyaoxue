package com.newclass.woyaoxue.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ExpandableListView.OnGroupClickListener;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;
import com.newclass.woyaoxue.base.BaseAdapter;
import com.newclass.woyaoxue.bean.Document;
import com.newclass.woyaoxue.bean.Level;
import com.newclass.woyaoxue.download.DownloadManager;
import com.newclass.woyaoxue.fragment.DocsListFragment;
import com.newclass.woyaoxue.util.NetworkUtil;
import com.newclass.woyaoxue.view.ViewPagerIndicator;
import com.voc.woyaoxue.R;

public class TestActivity extends FragmentActivity
{

	private List<Group> list;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_test);
		ExpandableListView elv = (ExpandableListView) findViewById(R.id.elv);

		// 设置当组被点击时的监听
		elv.setOnGroupClickListener(new OnGroupClickListener()
		{

			@Override
			public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id)
			{
				Toast.makeText(TestActivity.this, list.get(groupPosition).Name, Toast.LENGTH_SHORT).show();
				return false;
			}
		});

		// 设置当子项目被点击时的监听
		elv.setOnChildClickListener(new OnChildClickListener()
		{

			@Override
			public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id)
			{
				Toast.makeText(TestActivity.this, list.get(groupPosition).Childs.get(childPosition).Name, Toast.LENGTH_SHORT).show();
				return false;
			}
		});

		initdata();

		MyAdapter adapter = new MyAdapter();
		elv.setAdapter(adapter);

	}

	private void initdata()
	{
		list = new ArrayList<Group>();
		for (int i = 0; i < 20; i++)
		{
			Group group = new Group();
			group.Name = "Group" + i;
			group.Childs = new ArrayList<TestActivity.Child>();

			for (int j = 0; j < 5; j++)
			{
				Child child = new Child();
				child.Name = "Group" + i + " Child" + j;
				group.Childs.add(child);
			}
			list.add(group);
		}
	}

	private class Group
	{
		String Name;
		List<Child> Childs;
	}

	private class Child
	{
		String Name;
	}

	private class MyAdapter extends BaseExpandableListAdapter
	{

		@Override
		public int getGroupCount()
		{
			return list.size();
		}

		@Override
		public int getChildrenCount(int groupPosition)
		{
			return list.get(groupPosition).Childs.size();
		}

		@Override
		public Object getGroup(int groupPosition)
		{
			return list.get(groupPosition);
		}

		@Override
		public Object getChild(int groupPosition, int childPosition)
		{
			return list.get(groupPosition).Childs.get(childPosition);
		}

		@Override
		public long getGroupId(int groupPosition)
		{
			return groupPosition;
		}

		@Override
		public long getChildId(int groupPosition, int childPosition)
		{
			return childPosition;
		}

		@Override
		public boolean hasStableIds()
		{
			return false;
		}

		@Override
		public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent)
		{
			TextView textView = new TextView(TestActivity.this);
			textView.setPadding(30, 0, 0, 0);//缩进设置
			textView.setText(list.get(groupPosition).Name);
			return textView;
		}

		@Override
		public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent)
		{
			TextView textView = new TextView(TestActivity.this);
			textView.setPadding(60, 0, 0, 0);//缩进设置
			textView.setText(list.get(groupPosition).Childs.get(childPosition).Name);
			return textView;
		}

		@Override
		public boolean isChildSelectable(int groupPosition, int childPosition)
		{
			return true;
		}
	}
}
