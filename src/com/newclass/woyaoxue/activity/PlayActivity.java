package com.newclass.woyaoxue.activity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnBufferingUpdateListener;
import android.media.MediaPlayer.OnErrorListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.newclass.woyaoxue.bean.Document;
import com.newclass.woyaoxue.bean.Lyric;
import com.newclass.woyaoxue.util.NetworkUtil;
import com.newclass.woyaoxue.view.SpecialLyricView;
import com.voc.woyaoxue.R;

public class PlayActivity extends Activity implements OnClickListener, OnBufferingUpdateListener, OnPreparedListener, OnErrorListener
{
	protected static final int REFRESH_SEEKBAR = 0;
	@ViewInject(R.id.bt_paly)
	private ImageView bt_play;
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

	// 是否单句循环
	private boolean isOneLineLoop = false;

	@ViewInject(R.id.iv_line)
	private ImageView iv_line;
	@ViewInject(R.id.ll_lyrics)
	private LinearLayout ll_lyrics;

	private MediaPlayer mediaPlayer;

	@ViewInject(R.id.seekBar)
	private SeekBar seekBar;

	private List<SpecialLyricView> specialLyricViews;

	private List<Integer> subTitleIcons;
	private Integer subTitleState = 0;

	@ViewInject(R.id.sv_lyrics)
	private ScrollView sv_lyrics;

	@ViewInject(R.id.tv_aSide)
	private TextView tv_aSide;
	@ViewInject(R.id.tv_bSide)
	private TextView tv_bSide;

	@ViewInject(R.id.tv_title)
	private TextView tv_title;

	private int sideA = 0;
	private int sideB = 0;

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
			e.printStackTrace();
		}
	}

	protected String millisecondsFormat(int milliseconds)
	{

		long minutes = (milliseconds % (1000 * 60 * 60)) / (1000 * 60);
		long seconds = (milliseconds % (1000 * 60)) / 1000;

		return minutes + ":" + seconds;
	}

	@Override
	public void onBufferingUpdate(MediaPlayer mp, int percent)
	{
		// Log.i("logi", "onBufferingUpdate Position:" + mp.getCurrentPosition() + " Duration:" + mp.getDuration() + " percent:" + percent + "%");
		// seekBar.setSecondaryProgress((int) (mp.getDuration() * percent * 0.01));
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

		case R.id.iv_line:
			isOneLineLoop = !isOneLineLoop;
			iv_line.setImageResource(isOneLineLoop ? R.drawable.ico_line_loop_on : R.drawable.ico_line_loop_off);
			if (isOneLineLoop)
			{
				setSideASideB();
			}
			break;
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_play);
		ViewUtils.inject(this);

		subTitleIcons = new ArrayList<Integer>();
		subTitleIcons.add(R.drawable.ico_actionbar_subtitle_none);
		subTitleIcons.add(R.drawable.ico_actionbar_subtitle_cn);
		subTitleIcons.add(R.drawable.ico_actionbar_subtitle_encn);

		Intent intent = getIntent();
		int id = intent.getIntExtra("Id", 429);

		bt_play.setOnClickListener(this);
		iv_line.setOnClickListener(this);

		ActionBar actionBar = getActionBar();

		// 返回按钮
		actionBar.setDisplayHomeAsUpEnabled(true);

		Log.i("logi", "actionBar=" + actionBar);

		new HttpUtils().send(HttpMethod.GET, NetworkUtil.getDocById(id), new RequestCallBack<String>()
		{

			@Override
			public void onFailure(HttpException error, String msg)
			{

			}

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
					// 在刚开始的时候,是不显示字幕的
					specialLyricView.showEnCn(SpecialLyricView.SHOW_NONE);
					ll_lyrics.addView(specialLyricView);
				}

				// 因为使用的是相对路径,但是在实际请求时要加上域名
				initMediaPlayer(NetworkUtil.getFullPath(document.SoundPath));
			}
		});

		seekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener()
		{

			@Override
			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser)
			{
				// 如果由用户手动拖动则更改左右两边的时间标签内容
				if (fromUser)
				{
					tv_aSide.setText(millisecondsFormat(mediaPlayer.getCurrentPosition()));
				}

			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar)
			{

			}

			@Override
			public void onStopTrackingTouch(SeekBar sb)
			{

				mediaPlayer.seekTo(sb.getProgress());

			}
		});

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.play_activity, menu);
		return true;
	}

	@Override
	protected void onDestroy()
	{
		handler.removeCallbacksAndMessages(null);
		mediaPlayer.release();
		mediaPlayer = null;
		super.onDestroy();
	}

	@Override
	public boolean onError(MediaPlayer mp, int what, int extra)
	{

		return false;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		switch (item.getItemId())
		{
		case android.R.id.home:
			this.finish();
			return true;
		case R.id.menu_switch:
			subTitleState++;
			int state = subTitleState % subTitleIcons.size();
			showOrHideSubtitle(state);
			item.setIcon(subTitleIcons.get(state));
			Toast.makeText(this, "点击了语言切换", Toast.LENGTH_LONG).show();

			return true;
		default:
			return super.onOptionsItemSelected(item);
		}

	}

	@Override
	public void onPrepared(MediaPlayer mp)
	{
		mp.start();
		seekBar.setMax(mp.getDuration());
		tv_bSide.setText(millisecondsFormat(mp.getDuration()));

		sideB = mp.getDuration();

		// 定时更新歌词及SeekBar,找出当前播放的那一句
		new Timer().schedule(new TimerTask()
		{

			@Override
			public void run()
			{

				if (mediaPlayer != null)// && mediaPlayer.isPlaying())
				{
					handler.sendEmptyMessage(REFRESH_SEEKBAR);

				}

			}
		}, 0, 1000);
	}

	protected void refresh_seekbar()
	{
		long currentLineTime = 0;
		long nextLineTime = 0;

		// Color.parseColor("#ffffff")

		if (mediaPlayer == null)
		{
			return;
		}

		int currentPosition = mediaPlayer.getCurrentPosition();
		seekBar.setProgress(mediaPlayer.getCurrentPosition());
		tv_aSide.setText(millisecondsFormat(mediaPlayer.getCurrentPosition()));

		if (isOneLineLoop && currentPosition > sideB)
		{
			mediaPlayer.seekTo(sideA);
		}

		for (int i = 0; i < specialLyricViews.size(); i++)
		{
			SpecialLyricView view = specialLyricViews.get(i);
			currentLineTime = view.getTimeLabel();

			nextLineTime = (i + 1 == specialLyricViews.size()) ? mediaPlayer.getDuration() : specialLyricViews.get(i + 1).getTimeLabel();

			if (currentLineTime < currentPosition && currentPosition < nextLineTime)
			{
				// 高亮显示字幕
				view.highlight();

				// 高亮字幕位置自动滚动功能,即如果现在播放到某一个时刻,字幕却不在屏幕显示时
				if (sv_lyrics.getScrollY() < view.getTop() && view.getTop() < sv_lyrics.getScrollY() + 800)
				{
					// Log.i("logi", "不用跳");
				}
				else
				{
					sv_lyrics.scrollTo(0, view.getTop());
				}

			}
			else
			{
				view.resetColor();
			}
		}

		if (isOneLineLoop)
		{
			if (mediaPlayer.getCurrentPosition() > sideB)
			{
				mediaPlayer.seekTo(sideA);
			}
		}

	}

	private void setSideASideB()
	{
		if (specialLyricViews != null && mediaPlayer != null && mediaPlayer.getDuration() > 0)
		{
			int currentPosition = mediaPlayer.getCurrentPosition();

			for (int i = 0; i < specialLyricViews.size(); i++)
			{
				Integer timeA = specialLyricViews.get(i).getTimeLabel();
				Integer timeB = i == (specialLyricViews.size() - 1) ? mediaPlayer.getDuration() : specialLyricViews.get(i + 1).getTimeLabel();
				if (timeA < currentPosition && currentPosition < timeB)
				{
					sideA = timeA;
					sideB = timeB;

					Log.i("logi", "isOneLineLoop=" + isOneLineLoop + " sideA=" + sideA + " sideB=" + sideB);
				}
			}
		}

	}

	private void showOrHideSubtitle(int state)
	{
		Integer integer = subTitleIcons.get(state);
		switch (integer)
		{
		case R.drawable.ico_actionbar_subtitle_none:
			for (SpecialLyricView view : specialLyricViews)
			{
				view.showEnCn(SpecialLyricView.SHOW_NONE);
			}
			break;
		case R.drawable.ico_actionbar_subtitle_cn:
			for (SpecialLyricView view : specialLyricViews)
			{
				view.showEnCn(SpecialLyricView.SHOW_CN);
			}
			break;
		case R.drawable.ico_actionbar_subtitle_encn:
			for (SpecialLyricView view : specialLyricViews)
			{
				view.showEnCn(SpecialLyricView.SHOW_ENCN);
			}
			break;
		}

	}

}
