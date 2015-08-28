package com.newclass.woyaoxue.activity;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.TreeSet;

import org.w3c.dom.Text;

import com.google.gson.Gson;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.newclass.woyaoxue.R;
import com.newclass.woyaoxue.bean.Document;
import com.newclass.woyaoxue.bean.Lyric;
import com.newclass.woyaoxue.util.NetworkUtil;
import com.newclass.woyaoxue.view.SpecialLyricView;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Intent;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnBufferingUpdateListener;
import android.media.MediaPlayer.OnErrorListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.os.Bundle;
import android.os.Handler;
import android.text.format.DateFormat;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;

public class PlayActivity extends Activity implements OnClickListener, OnBufferingUpdateListener, OnPreparedListener, OnErrorListener
{
	protected static final int REFRESH_SEEKBAR = 0;
	@ViewInject(R.id.tv_title)
	private TextView tv_title;
	@ViewInject(R.id.tv_aSide)
	private TextView tv_aSide;
	@ViewInject(R.id.tv_bSide)
	private TextView tv_bSide;

	@ViewInject(R.id.bt_paly)
	private ImageView bt_play;
	@ViewInject(R.id.bt_pause)
	private Button bt_pause;
	@ViewInject(R.id.seekBar)
	private SeekBar seekBar;
	@ViewInject(R.id.sv_lyrics)
	private ScrollView sv_lyrics;

	@ViewInject(R.id.ll_lyrics)
	private LinearLayout ll_lyrics;

	private MediaPlayer mediaPlayer;

	List<Lyric> lyrics = new ArrayList<Lyric>();
	private List<SpecialLyricView> specialLyricViews;

	private Handler handler = new Handler()
	{
		public void handleMessage(android.os.Message msg)
		{
			switch (msg.what)
			{
			case REFRESH_SEEKBAR:
				refresh_seekbar();
				break;

			default:
				break;
			}

		};
	};

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{// http://voc2015.cloudapp.net/NewClass/DocById/431
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_play);
		ViewUtils.inject(this);

		Intent intent = getIntent();
		int id = intent.getIntExtra("Id", 429);

		bt_play.setOnClickListener(this);
		bt_pause.setOnClickListener(this);

		new HttpUtils().send(HttpMethod.GET, NetworkUtil.getDocById(id), new RequestCallBack<String>()
		{

			@Override
			public void onSuccess(ResponseInfo<String> responseInfo)
			{
				Document document = new Gson().fromJson(responseInfo.result, Document.class);
				tv_title.setText(document.Title);

				specialLyricViews = new ArrayList<SpecialLyricView>();

				for (Lyric lyric : document.Lyrics)
				{
					SpecialLyricView specialLyricView = new SpecialLyricView(PlayActivity.this, lyric);
					specialLyricViews.add(specialLyricView);
				}

				Collections.sort(specialLyricViews);

				for (SpecialLyricView specialLyricView : specialLyricViews)
				{
					ll_lyrics.addView(specialLyricView);
				}

				// 因为使用的是相对路径,但是在实际请求时要加上域名
				initMediaPlayer(NetworkUtil.getFullPath(document.SoundPath));
			}

			@Override
			public void onFailure(HttpException error, String msg)
			{
				// TODO Auto-generated method stub

			}
		});

		seekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener()
		{

			@Override
			public void onStopTrackingTouch(SeekBar sb)
			{
				// TODO Auto-generated method stub

				mediaPlayer.seekTo(sb.getProgress());

			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar)
			{
				// TODO Auto-generated method stub

			}

			@Override
			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser)
			{
				// 如果由用户手动拖动则更改左右两边的时间标签内容
				if (fromUser)
				{
					tv_aSide.setText(millisecondsFormat(mediaPlayer.getCurrentPosition()));
				}

			}
		});

	}

	private void initView()
	{
		Log.i("logi", "---------------");
		RelativeLayout message_popup = (RelativeLayout) View.inflate(this, R.layout.message_popup, null);
		Button confirmBtn = (Button) message_popup.findViewById(R.id.confirm_message_btn);
		EditText mSourceText = (EditText) message_popup.findViewById(R.id.source_text);
		TextView readingMessageNum = (TextView) message_popup.findViewById(R.id.reading_message);
		TextView totalMessageNum = (TextView) message_popup.findViewById(R.id.total_messages);
		readingMessageNum.setText("1");
		totalMessageNum.setText("顶戴sdf");
		confirmBtn.setEnabled(true);
		confirmBtn.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				Toast.makeText(PlayActivity.this, "show it", Toast.LENGTH_LONG).show();
			}
		});

		PopupWindow popupWindow = new PopupWindow(message_popup);
		popupWindow.showAsDropDown(sv_lyrics, 5, 5);

	}

	protected String millisecondsFormat(int milliseconds)
	{

		long days = milliseconds / (1000 * 60 * 60 * 24);
		long hours = (milliseconds % (1000 * 60 * 60 * 24)) / (1000 * 60 * 60);
		long minutes = (milliseconds % (1000 * 60 * 60)) / (1000 * 60);
		long seconds = (milliseconds % (1000 * 60)) / 1000;

		int min = milliseconds / (60 * 1000);
		int sec = milliseconds / (1000);
		return minutes + ":" + seconds;
	}

	protected void refresh_seekbar()
	{
		long currentLineTime = 0;
		long nextLineTime = 0;

		// Color.parseColor("#ffffff")
		// TODO Auto-generated method stub
		int currentPosition = mediaPlayer.getCurrentPosition();
		seekBar.setProgress(mediaPlayer.getCurrentPosition());
		tv_aSide.setText(millisecondsFormat(mediaPlayer.getCurrentPosition()));

		for (int i = 0; i < specialLyricViews.size(); i++)
		{
			SpecialLyricView view = specialLyricViews.get(i);
			currentLineTime = view.getTimeLabel();

			nextLineTime = (i + 1 == specialLyricViews.size()) ? mediaPlayer.getDuration() : specialLyricViews.get(i + 1).getTimeLabel();

			if (currentLineTime < currentPosition && currentPosition < nextLineTime)
			{
				view.highlight();
			}
			else
			{
				view.resetColor();
			}
		}

	}

	@Override
	public void onClick(View v)
	{
		switch (v.getId())
		{
		case R.id.bt_paly:
			if (mediaPlayer.isPlaying())
			{
				mediaPlayer.pause();
			}
			else
			{
				mediaPlayer.start();
			}

			break;

		case R.id.bt_pause:
			break;
		}
	}

	/**
	 * 使用指定的音频路径初始化MediaPlayer
	 * 
	 * @param url 音频的全路径
	 */
	private void initMediaPlayer(String url)
	{
		try
		{
			mediaPlayer = new MediaPlayer();
			mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
			mediaPlayer.setOnBufferingUpdateListener(this);
			mediaPlayer.setOnPreparedListener(this);
			mediaPlayer.setOnErrorListener(this);
			mediaPlayer.setDataSource(url);
			// mediaPlayer.prepare(); // might take long! (for buffering, etc)
			mediaPlayer.prepareAsync();
		}
		catch (Exception e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();

		}
	}

	@Override
	public void onBufferingUpdate(MediaPlayer mp, int percent)
	{
		Log.i("logi", "onBufferingUpdate Position:" + mp.getCurrentPosition() + " Duration:" + mp.getDuration() + " percent:" + percent + "%");
		seekBar.setSecondaryProgress((int) (mp.getDuration() * percent * 0.01));
	}

	@Override
	public void onPrepared(MediaPlayer mp)
	{
		// TODO Auto-generated method stub
		mediaPlayer.start();
		seekBar.setMax(mp.getDuration());
		tv_bSide.setText("" + mp.getDuration());

		// 定时更新歌词及SeekBar
		new Timer().schedule(new TimerTask()
		{

			@Override
			public void run()
			{
				// TODO Auto-generated method stub
				if (mediaPlayer.isPlaying())
				{
					handler.sendEmptyMessage(REFRESH_SEEKBAR);
				}

			}
		}, 0, 1000);
	}

	@Override
	public boolean onError(MediaPlayer mp, int what, int extra)
	{
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	protected void onDestroy()
	{
		// TODO Auto-generated method stub
		super.onDestroy();

		handler.removeCallbacksAndMessages(null);

		mediaPlayer.release();
		mediaPlayer = null;
	}

}
