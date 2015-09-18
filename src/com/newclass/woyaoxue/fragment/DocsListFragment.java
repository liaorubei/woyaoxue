package com.newclass.woyaoxue.fragment;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.Fragment;
import android.text.format.Formatter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.newclass.woyaoxue.activity.PlayActivity;
import com.newclass.woyaoxue.base.BaseAdapter;
import com.newclass.woyaoxue.bean.Document;
import com.newclass.woyaoxue.service.BatchDownloadService;
import com.newclass.woyaoxue.service.BatchDownloadService.BatchDownloadBinder;
import com.newclass.woyaoxue.util.DaoUtil;
import com.newclass.woyaoxue.util.DocDbUtil;
import com.newclass.woyaoxue.util.FolderUtil;
import com.voc.woyaoxue.R;

public class DocsListFragment extends Fragment
{
	private BaseAdapter<DownloadHelper> adapter;

	private BatchDownloadBinder batchDownloadBinder;
	private List<DownloadHelper> helpers;
	@ViewInject(R.id.listView)
	private ListView listView;

	private String mFullPath;

	@ViewInject(R.id.tv_none_data)
	private TextView tv_none_data;

	public DocsListFragment(String fullPath)
	{
		this.mFullPath = fullPath;
		initData();
	}

	public void fillData()
	{
		initData();
		new HttpUtils().send(HttpMethod.GET, this.mFullPath, new RequestCallBack<String>()
		{
			@Override
			public void onFailure(HttpException error, String msg)
			{}

			@Override
			public void onSuccess(ResponseInfo<String> responseInfo)
			{
				List<Document> fromJson = new Gson().fromJson(responseInfo.result, new TypeToken<List<Document>>()
				{}.getType());
				if (fromJson != null)
				{
					helpers.clear();
					for (Document document : fromJson)
					{
						helpers.add(new DownloadHelper(document));
					}
					adapter.notifyDataSetChanged();
					tv_none_data.setVisibility(View.GONE);
				}
			}
		});

	}

	public void fillData(String fullPath)
	{
		this.mFullPath = fullPath;
		fillData();
	}

	private void initData()
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		if (helpers == null)
		{
			helpers = new ArrayList<DownloadHelper>();
		}

		if (adapter == null)
		{
			adapter = new MyAdatper(helpers);
		}

		Intent service = new Intent(getActivity(), BatchDownloadService.class);
		ServiceConnection conn = new MyServiceConnection();
		// 开启批量下载服务
		getActivity().bindService(service, conn, Context.BIND_AUTO_CREATE);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		View inflate = inflater.inflate(R.layout.fragment_docslist, container, false);
		ViewUtils.inject(this, inflate);
		fillData();
		listView.setAdapter(adapter);
		listView.setOnItemClickListener(new OnItemClickListener()
		{
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id)
			{
				Intent intent = new Intent(getActivity(), PlayActivity.class);
				intent.putExtra("Id", helpers.get(position).getDoc().Id);
				startActivity(intent);
			}
		});
		Log.i("logi", "onCreateView");
		return inflate;
	}

	/**
	 * 扩展自RequestCallBack的下载辅助类,可以进行基本的回调,文件是否存在判断等功能
	 * 
	 * @author liaorubei
	 *
	 */
	public class DownloadHelper extends RequestCallBack<File>
	{
		public ProgressBar bar;
		private Document doc;
		private long mCurrent;
		private long mTotal;

		public DownloadHelper(Document document)
		{
			this.doc = document;
		}

		public boolean exists()
		{
			File file = new File(FolderUtil.rootDir(getActivity()), this.doc.SoundPath);
			return file.exists();
		}

		public Document getDoc()
		{
			return doc;
		}

		@Override
		public void onFailure(HttpException error, String msg)
		{
			BatchDownloadService.isDownloading = false;
			BatchDownloadService.downloadCount++;
		}

		@Override
		public void onLoading(long total, long current, boolean isUploading)
		{
			if (bar != null && bar.getTag().equals(doc.SoundPath))
			{
				this.bar.setMax((int) total);
				this.bar.setProgress((int) current);
			}
			this.mTotal = total;
			this.mCurrent = current;
		}

		@Override
		public void onStart()
		{
			BatchDownloadService.isDownloading = true;
		}

		@Override
		public void onSuccess(ResponseInfo<File> responseInfo)
		{
			BatchDownloadService.isDownloading = false;
			BatchDownloadService.downloadCount++;

			Log.i("logi", "onSuccess=" + this.doc.Id);

			// 每次下载成功一个,就添加一条记录到数据库
			DaoUtil.addDocument(this.doc, getActivity());

		}

		public void setProgressBar(ProgressBar progressBar)
		{
			this.bar = progressBar;
			this.bar.setMax((int) mTotal);
			this.bar.setProgress((int) mCurrent);
			this.bar.setTag(doc.SoundPath);
		}
	}

	private class MyAdatper extends BaseAdapter<DownloadHelper>
	{

		public MyAdatper(List<DownloadHelper> objects)
		{
			super(objects);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent)
		{
			final DownloadHelper helper = getItem(position);
			Document document = helper.getDoc();
			if (convertView == null)
			{
				convertView = View.inflate(getActivity(), R.layout.listitem_docslist, null);
				ViewHolder holder = new ViewHolder();
				holder.tv_title_one = (TextView) convertView.findViewById(R.id.tv_title_one);
				holder.tv_title_two = (TextView) convertView.findViewById(R.id.tv_title_two);
				holder.tv_date = (TextView) convertView.findViewById(R.id.tv_date);
				holder.tv_time = (TextView) convertView.findViewById(R.id.tv_time);
				holder.tv_size = (TextView) convertView.findViewById(R.id.tv_size);
				holder.fl_icon = convertView.findViewById(R.id.fl_icon);
				holder.pb_download = (ProgressBar) convertView.findViewById(R.id.pb_download);
				convertView.setTag(holder);
			}

			ViewHolder tag = (ViewHolder) convertView.getTag();
			tag.tv_title_one.setText(document.Title);
			tag.tv_title_two.setText(document.TitleTwo);
			tag.tv_date.setText(document.DateString);
			tag.tv_time.setText(document.LengthString);
			tag.tv_size.setText(Formatter.formatFileSize(getActivity(), document.Length));

			helper.setProgressBar(tag.pb_download);// 把进度条添加到回调管理中

			// 如果音频文件已经存在,或者音频文件已经在下载队列中,那么就让下载按钮的背景变灰色
			tag.fl_icon.setBackgroundResource((helper.exists() || batchDownloadBinder.isInDownloadQueue(helper)) ? R.drawable.file_download_disable : R.drawable.file_download_enbale);

			tag.fl_icon.setOnClickListener(new OnClickListener()
			{

				@Override
				public void onClick(View v)
				{
					// 如果音频文件已经存在,或者音频文件已经在下载队列中,那么就让点击跳过代码
					if (helper.exists())
					{
						Toast.makeText(getActivity(), "已经下载", Toast.LENGTH_SHORT).show();
					}
					else if (batchDownloadBinder.isInDownloadQueue(helper))
					{
						Toast.makeText(getActivity(), "正在下载", Toast.LENGTH_SHORT).show();
					}
					else
					{
						v.setBackgroundResource(R.drawable.file_download_disable);
						batchDownloadBinder.addToDownloadQueue(helper);
					}
				}
			});
			return convertView;
		}
	}

	private class MyServiceConnection implements ServiceConnection
	{

		@Override
		public void onServiceConnected(ComponentName name, IBinder service)
		{
			batchDownloadBinder = (BatchDownloadBinder) service;
		}

		@Override
		public void onServiceDisconnected(ComponentName name)
		{

		}
	}

	private class ViewHolder
	{
		public View fl_icon;
		public ProgressBar pb_download;
		public TextView tv_date;
		public TextView tv_size;
		public TextView tv_time;
		public TextView tv_title_one;
		public TextView tv_title_two;
	}

}
