package com.newclass.woyaoxue.activity;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.newclass.woyaoxue.R;
import com.newclass.woyaoxue.bean.Document;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

public class PlayActivity extends Activity
{
	@ViewInject(R.id.tv_title)
	private TextView tv_title;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_play);
		ViewUtils.inject(this);

		Intent intent = getIntent();
		int id = intent.getIntExtra("Id", 0);
		String title = intent.getStringExtra("Title");
		String soundPath = intent.getStringExtra("SoundPath");
		Document document = new Document(id, title, soundPath);

		tv_title.setText(title);

	}
}
