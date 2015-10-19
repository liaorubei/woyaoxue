package com.newclass.woyaoxue.fragment;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.CheckBox;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;
import com.newclass.woyaoxue.activity.DocsActivity;
import com.newclass.woyaoxue.base.BaseAdapter;
import com.newclass.woyaoxue.base.BaseFragment;
import com.newclass.woyaoxue.bean.Folder;
import com.newclass.woyaoxue.bean.database.Database;
import com.newclass.woyaoxue.bean.database.UrlCache;
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
	private Database database;

	public FolderFragment(int levelid)
	{
		this.mLevelId = levelid;
		list = new ArrayList<Folder>();
		myAdapter = new MyAdapter(list);
	}

	@Override
	public void initData(Context context)
	{
		String url = NetworkUtil.getFolders(mLevelId);
		database = new Database(context);
		UrlCache cache =null;

		if (cache == null || (System.currentTimeMillis() - cache.UpdateAt > 600000))
		{
			Log.i("请求网络=" + url);
			new HttpUtils().send(HttpMethod.GET, url, new RequestCallBack<String>()
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

						// 添加数据到数据库
						for (Folder folder : fromJson)
						{
							if (!database.folderExists(folder.Id))
							{
								database.folderInsert(folder);
							}
						}
					}
					else
					{
						vacancy();
					}

					// 缓存数据
					UrlCache urlCache = new UrlCache();
					urlCache.Url = this.getRequestUrl();
					urlCache.Json = responseInfo.result;
					urlCache.UpdateAt = System.currentTimeMillis();
					database.cacheInsertOrUpdate(urlCache);
				}

				@Override
				public void onFailure(HttpException error, String msg)
				{
					failure();
				}
			});
		}
		else
		{
			Log.i("加载缓存=" + url);
			List<Folder> fromJson = new Gson().fromJson(cache.Json, new TypeToken<List<Folder>>()
			{}.getType());
			if (fromJson.size() > 0)
			{
				list.clear();
				list.addAll(fromJson);
				myAdapter.notifyDataSetChanged();
				success();
			}
		}

	}

	@Override
	protected View initView()
	{
		View inflate = View.inflate(getActivity(), R.layout.fragment_folder, null);
		xListView = (XListView) inflate.findViewById(R.id.xListView);
		xListView.setAdapter(myAdapter);
		xListView.setPullDownEnable(false);
		xListView.setPullupEnable(false);

		xListView.setOnItemClickListener(new OnItemClickListener()
		{

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id)
			{
				Intent intent = new Intent(getActivity(), DocsActivity.class);
				intent.putExtra("LevelId", FolderFragment.this.mLevelId);
				intent.putExtra("FolderId", list.get(position - 1).Id);
				intent.putExtra("FolderName", list.get(position - 1).Name);
				startActivity(intent);
			}
		});
		return inflate;
	}

	@Override
	public void onDestroy()
	{
		super.onDestroy();
		database.closeConnection();
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
				holder.tv_folder = (TextView) convertView.findViewById(R.id.tv_folder);
				holder.tv_counts = (TextView) convertView.findViewById(R.id.tv_counts);
				convertView.setTag(holder);
			}
			ViewHolder holder = (ViewHolder) convertView.getTag();
			holder.tv_folder.setText(folder.Name);
			holder.tv_counts.setText("课程:" + folder.DocsCount);
			return convertView;
		}
	}

	private class ViewHolder
	{
		public CheckBox cb_delete;
		public TextView tv_folder;
		public TextView tv_counts;
	}

}
