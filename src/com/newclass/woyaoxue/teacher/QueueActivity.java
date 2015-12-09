package com.newclass.woyaoxue.teacher;

import org.json.JSONException;
import org.json.JSONObject;

import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;
import com.newclass.woyaoxue.util.NetworkUtil;
import com.voc.woyaoxue.R;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

public class QueueActivity extends Activity implements OnClickListener
{
	private Button bt_queue;
	private String accid;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_queue);

		accid = getIntent().getStringExtra("accid");
		initView();
	}

	private void initView()
	{
		bt_queue = (Button) findViewById(R.id.bt_queue);
		bt_queue.setOnClickListener(this);
	}

	@Override
	public void onClick(View v)
	{
		switch (v.getId())
		{
		case R.id.bt_queue:
			bt_queue.setEnabled(false);
			RequestParams pa = new RequestParams();
			pa.addBodyParameter("accid", accid);
			new HttpUtils().send(HttpMethod.POST, NetworkUtil.userEnqueue, pa, new RequestCallBack<String>()
			{

				@Override
				public void onSuccess(ResponseInfo<String> responseInfo)
				{
					bt_queue.setEnabled(true);
					int rank = -1;
					try
					{
						JSONObject object = new JSONObject(responseInfo.result);
						rank = object.getInt("rank");
					}
					catch (JSONException e)
					{
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					Toast.makeText(QueueActivity.this, "排队成功,当前名次为:" + rank, Toast.LENGTH_SHORT).show();
				}

				@Override
				public void onFailure(HttpException error, String msg)
				{
					bt_queue.setEnabled(true);
					Toast.makeText(QueueActivity.this, "排除失败", Toast.LENGTH_SHORT).show();
				}
			});
			break;

		default:
			break;
		}
	}
}
