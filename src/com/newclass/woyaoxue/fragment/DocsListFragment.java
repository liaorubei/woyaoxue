package com.newclass.woyaoxue.fragment;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

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
import com.newclass.woyaoxue.util.FolderUtil;
import com.voc.woyaoxue.R;

public class DocsListFragment extends Fragment
{
	private BaseAdapter<Document> adapter;

	private List<Document> documents;
	private List<DownloadHelper> callbacks;

	@ViewInject(R.id.listView)
	private ListView listView;
	@ViewInject(R.id.tv_none_data)
	private TextView tv_none_data;

	private String mFullPath;

	public DocsListFragment(String fullPath)
	{
		this.mFullPath = fullPath;
	}

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		if (callbacks == null)
		{
			callbacks = new ArrayList<DocsListFragment.DownloadHelper>();
		}

		if (documents == null)
		{
			documents = new ArrayList<Document>();
		}
		if (adapter == null)
		{
			adapter = new MyAdatper(getActivity(), R.layout.listitem_docslist, documents);
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
				intent.putExtra("Id", documents.get(position).Id);
				startActivity(intent);
			}
		});
		return inflate;
	}

	public void fillData()
	{
		Log.i("logi", "开始填充数据:" + this.mFullPath);
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
					documents.clear();
					documents.addAll(fromJson);

					callbacks.clear();
					for (Document document : fromJson)
					{
						callbacks.add(new DownloadHelper(document));
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

	private class MyAdatper extends BaseAdapter<Document>
	{

		public MyAdatper(Context context, int resource, List<Document> objects)
		{
			super(context, resource, objects);
			// TODO Auto-generated constructor stub
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent)
		{
			final DownloadHelper helper = callbacks.get(position);
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
			tag.tv_title_two.setText("Life is the art of drawing without an eraser");
			tag.tv_date.setText("2012-12-12");
			tag.tv_time.setText("00:56");
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

	private class ViewHolder
	{
		public TextView tv_title_one;
		public TextView tv_title_two;
		public TextView tv_date;
		public TextView tv_time;
		public TextView tv_size;

		public View fl_icon;
		public ProgressBar pb_download;
	}

	private BatchDownloadBinder batchDownloadBinder;

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
		private long mTotal;
		private long mCurrent;

		public void setProgressBar(ProgressBar progressBar)
		{
			this.bar = progressBar;
			this.bar.setMax((int) mTotal);
			this.bar.setProgress((int) mCurrent);
			this.bar.setTag(doc.SoundPath);
		}

		public boolean exists()
		{
			File file = new File(FolderUtil.rootDir(getActivity()), this.doc.SoundPath);
			return file.exists();
		}

		public DownloadHelper(Document document)
		{
			this.doc = document;
		}

		@Override
		public void onStart()
		{
			BatchDownloadService.isDownloading = true;
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
		public void onSuccess(ResponseInfo<File> responseInfo)
		{
			BatchDownloadService.isDownloading = false;
			BatchDownloadService.downloadCount++;
		}

		@Override
		public void onFailure(HttpException error, String msg)
		{
			BatchDownloadService.isDownloading = false;
			BatchDownloadService.downloadCount++;
		}

		public Document getDoc()
		{
			return doc;
		}
	}

}
