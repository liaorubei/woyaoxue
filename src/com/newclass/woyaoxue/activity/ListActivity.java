package com.newclass.woyaoxue.activity;

import java.io.File;
import java.io.FileDescriptor;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.HttpHandler;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.voc.woyaoxue.R;
import com.newclass.woyaoxue.base.BaseAdapter;
import com.newclass.woyaoxue.bean.Document;
import com.newclass.woyaoxue.util.NetworkUtil;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

public class ListActivity extends Activity implements OnClickListener
{
	@ViewInject(R.id.bt_prev)
	private Button bt_prev;
	@ViewInject(R.id.bt_play)
	private Button bt_play;
	@ViewInject(R.id.bt_next)
	private Button bt_next;
	private MediaPlayer mediaPlayer;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_list);
		ViewUtils.inject(this);

		bt_prev.setOnClickListener(this);
		bt_play.setOnClickListener(this);
		bt_next.setOnClickListener(this);
		File file = new File(Environment.getExternalStorageDirectory(), "Download/冬天的秘密-周传雄.mp3");
		Log.i("logi", "file.exists=" + file.exists());

		try
		{
			mediaPlayer = new MediaPlayer();
			mediaPlayer.setDataSource(file.getAbsolutePath());
			mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
			mediaPlayer.prepare();
			mediaPlayer.setLooping(true);
			mediaPlayer.setOnPreparedListener(new OnPreparedListener()
			{

				@Override
				public void onPrepared(MediaPlayer mp)
				{
					mp.start();
				}
			});
			mediaPlayer.setOnCompletionListener(new OnCompletionListener()
			{

				@Override
				public void onCompletion(MediaPlayer mp)
				{
					Log.i("logi", "MediaPlayer播放结束");
				//	mp.start();
				}
			});
		}
		catch (Exception e)
		{

			e.printStackTrace();
		}

	}

	@Override
	public void onClick(View v)
	{
		switch (v.getId())
		{
		case R.id.bt_prev:
		//	mediaPlayer.start();
			mediaPlayer.seekTo(mediaPlayer.getCurrentPosition() - 10000);
			break;

		case R.id.bt_play:
			if (mediaPlayer.isPlaying())
			{
				mediaPlayer.pause();
				bt_play.setText("播放");
			}
			else
			{
				mediaPlayer.start();
				bt_play.setText("暂停");
			}
			break;
		case R.id.bt_next:
		//	mediaPlayer.start();
			mediaPlayer.seekTo(mediaPlayer.getCurrentPosition() + 10000);
			break;

		}
	}

}
