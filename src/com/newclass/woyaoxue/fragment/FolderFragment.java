package com.newclass.woyaoxue.fragment;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;
import com.newclass.woyaoxue.activity.ListActivity;
import com.newclass.woyaoxue.base.BaseAdapter;
import com.newclass.woyaoxue.base.BaseFragment;
import com.newclass.woyaoxue.bean.Folder;
import com.newclass.woyaoxue.util.Log;
import com.newclass.woyaoxue.util.NetworkUtil;
import com.newclass.woyaoxue.view.XListView;
import com.voc.woyaoxue.R;

public class FolderFragment extends BaseFragment
{

	private XListView xListView;
	private MyAdapter myAdapter;
	private List<Folder> list;
	private int mLevelId;

	public FolderFragment(int levelid)
	{
		this.mLevelId = levelid;
		list = new ArrayList<Folder>();
		myAdapter = new MyAdapter(list);
	}

	@Override
	public void initData()
	{
		new HttpUtils().send(HttpMethod.GET, NetworkUtil.getFolders(mLevelId), new RequestCallBack<String>()
		{

			@Override
			public void onSuccess(ResponseInfo<String> responseInfo)
			{
				List<Folder> fromJson = new Gson().fromJson(responseInfo.result, new TypeToken<List<Folder>>()
				{}.getType());

				if (fromJson.size() > 0)
				{
					list.clear();
					list.addAll(fromJson);
					myAdapter.notifyDataSetChanged();
					success();
				}
				else
				{
					vacancy();
				}

			}

			@Override
			public void onFailure(HttpException error, String msg)
			{
				failure();
			}
		});

	}

	@Override
	protected View initView()
	{
		View inflate = View.inflate(getActivity(), R.layout.fragment_folder, null);
		xListView = (XListView) inflate.findViewById(R.id.xListView);
		xListView.setAdapter(myAdapter);
		xListView.set下拉刷新Enable(false);
		xListView.set下拉刷新Enable(false);

		xListView.setOnItemClickListener(new OnItemClickListener()
		{

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id)
			{
				Intent intent = new Intent(getActivity(), ListActivity.class);
				intent.putExtra("LevelId", FolderFragment.this.mLevelId);
				intent.putExtra("FolderId", list.get(position-1).Id);
				startActivity(intent);
			}
		});
		return inflate;
	}

	private class MyAdapter extends BaseAdapter<Folder>
	{

		public MyAdapter(List<Folder> list)
		{
			super(list);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent)
		{
			Folder folder = list.get(position);
			if (convertView == null)
			{
				convertView = View.inflate(getActivity(), R.layout.listitem_folder, null);
				ViewHolder holder = new ViewHolder();
				holder.tv_folder_name = (TextView) convertView.findViewById(R.id.tv_folder_name);
				holder.tv_document_count = (TextView) convertView.findViewById(R.id.tv_document_count);
				convertView.setTag(holder);
			}
			ViewHolder holder = (ViewHolder) convertView.getTag();
			holder.tv_folder_name.setText(folder.Name);
			holder.tv_document_count.setText("课程:" + folder.DocsCount);
			return convertView;
		}
	}

	private class ViewHolder
	{
		public TextView tv_folder_name;
		public TextView tv_document_count;
	}

}
