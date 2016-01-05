package com.newclass.woyaoxue.activity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.newclass.woyaoxue.base.BaseAdapter;
import com.newclass.woyaoxue.bean.CallLog;
import com.newclass.woyaoxue.bean.Response;
import com.newclass.woyaoxue.util.CommonUtil;
import com.newclass.woyaoxue.util.HttpUtil;
import com.newclass.woyaoxue.util.HttpUtil.Parameters;
import com.newclass.woyaoxue.util.Log;
import com.newclass.woyaoxue.util.NetworkUtil;
import com.voc.woyaoxue.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.format.Formatter;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

public class HistoryActivity extends Activity
{
	protected static final String TAG = "HistoryActivity";
	private ListView listview;
	private List<CallLog> list;
	private BaseAdapter<CallLog> adapter;
	private int id;
	private String targetAccid;
	private SimpleDateFormat sdf;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_history);

		initView();
		initData();
		getActionBar().setDisplayHomeAsUpEnabled(true);
		sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	}

	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item)
	{
		switch (item.getItemId())
		{
		case android.R.id.home:
			this.finish();
			break;

		default:
			break;
		}

		return true;
	}

	private void initView()
	{
		listview = (ListView) findViewById(R.id.listview);
		list = new ArrayList<CallLog>();
		adapter = new MyAdapter(list);
		listview.setAdapter(adapter);

	}

	private void initData()
	{
		Intent intent = getIntent();
		id = intent.getIntExtra("id", 1);
		targetAccid = intent.getStringExtra("target.Accid");

		Parameters parameters = new Parameters();
		parameters.add("accid", targetAccid);
		parameters.add("skip", 0 + "");
		parameters.add("take", 15 + "");

		HttpUtil.post(NetworkUtil.GetStudentCalllogByAccId, parameters, new RequestCallBack<String>()
		{
			@Override
			public void onSuccess(ResponseInfo<String> responseInfo)
			{
				Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();

				Response<List<CallLog>> resp = gson.fromJson(responseInfo.result, new TypeToken<Response<List<CallLog>>>()
				{}.getType());
				if (resp.code == 200)
				{
					list.clear();
					List<CallLog> logs = resp.info;
					for (CallLog callLog : logs)
					{
						list.add(callLog);
					}
					adapter.notifyDataSetChanged();
				}
			}

			@Override
			public void onFailure(HttpException error, String msg)
			{
				Log.i(TAG, "获取记录失败:" + msg);
			}
		});
	}

	private class MyAdapter extends BaseAdapter<CallLog>
	{

		public MyAdapter(List<CallLog> list)
		{
			super(list);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent)
		{
			CallLog item = getItem(position);
			View inflate = View.inflate(getApplication(), R.layout.listitem_history, null);
			TextView tv_theme = (TextView) inflate.findViewById(R.id.tv_theme);
			TextView tv_tiem = (TextView) inflate.findViewById(R.id.tv_time);
			TextView tv_date = (TextView) inflate.findViewById(R.id.tv_date);
			TextView tv_teacher = (TextView) inflate.findViewById(R.id.tv_teacher);
			TextView tv_score = (TextView) inflate.findViewById(R.id.tv_score);

			tv_theme.setText("主题");
			if (item.Finish != null)
			{
				tv_tiem.setText(CommonUtil.millisecondsFormat(item.Finish.getTime() - item.Start.getTime()));
			}

			tv_date.setText(sdf.format(item.Start));
			tv_teacher.setText(item.Teacher.Name);
			tv_score.setText(item.Score);

			return inflate;
		}
	}
}
