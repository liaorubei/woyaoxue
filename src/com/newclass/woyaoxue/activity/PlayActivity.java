package com.newclass.woyaoxue.activity;

import java.io.File;
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
import android.media.MediaPlayer.OnInfoListener;
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
import android.widget.ProgressBar;
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
import com.newclass.woyaoxue.bean.database.Database;
import com.newclass.woyaoxue.bean.database.UrlCache;
import com.newclass.woyaoxue.util.FolderUtil;
import com.newclass.woyaoxue.util.NetworkUtil;
import com.newclass.woyaoxue.view.SpecialLyricView;
import com.voc.woyaoxue.R;

public class PlayActivity extends Activity implements OnClickListener, OnBufferingUpdateListener, OnPreparedListener, OnErrorListener, OnInfoListener
{
	protected static final int REFRESH_SEEKBAR = 0;
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
	@ViewInject(R.id.iv_microphone)
	private ImageView iv_microphone;

	@ViewInject(R.id.iv_next)
	private ImageView iv_next;
	@ViewInject(R.id.pb_buffering)
	private ProgressBar pb_buffering;

	@ViewInject(R.id.iv_paly)
	private ImageView iv_play;

	@ViewInject(R.id.iv_prev)
	private ImageView iv_prev;

	@ViewInject(R.id.ll_lyrics)
	private LinearLayout ll_lyrics;

	private MediaPlayer mediaPlayer;

	@ViewInject(R.id.seekBar)
	private SeekBar seekBar;

	private int sideA = 0;

	private int sideB = 0;
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
	private int documentId;
	private Database database;

	/**
	 * 使用指定的音频路径初始化MediaPlayer
	 * 
	 * @param url
	 *            音频的相对路径
	 */
	private void initMediaPlayer(String url)
	{
		try
		{
			mediaPlayer = new MediaPlayer();
			mediaPlayer.setLooping(true);// 默认开启循环播放
			mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
			mediaPlayer.setOnBufferingUpdateListener(this);
			mediaPlayer.setOnPreparedListener(this);
			mediaPlayer.setOnErrorListener(this);
			mediaPlayer.setOnInfoListener(this);
			File file = new File(FolderUtil.rootDir(this), url);

			if (file.exists())
			{
				mediaPlayer.setDataSource(file.getAbsolutePath());
				mediaPlayer.prepare();
			}
			else
			{
				mediaPlayer.setDataSource(NetworkUtil.getFullPath(url));
				mediaPlayer.prepareAsync();
			}
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
		return (minutes < 10 ? "0" : "") + minutes + ":" + (seconds < 10 ? "0" : "") + seconds;
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

		case R.id.iv_line:
			isOneLineLoop = !isOneLineLoop;
			iv_line.setImageResource(isOneLineLoop ? R.drawable.selector_line_loop_enable : R.drawable.selector_line_loop_disable);
			if (isOneLineLoop)
			{
				setSideASideB();
			}
			break;
		case R.id.iv_prev:
			getPrevLine();
			break;
		case R.id.iv_paly:
			if (mediaPlayer.isPlaying())
			{
				mediaPlayer.pause();
			}
			else
			{
				mediaPlayer.start();
			}

			Log.i("logi", "isPlaying=" + mediaPlayer.isPlaying());
			iv_play.setImageResource(mediaPlayer.isPlaying() ? R.drawable.selector_play_enable : R.drawable.selector_play_disable);
			break;

		case R.id.iv_next:
			getNextLine();
			break;

		case R.id.iv_microphone:
			Toast.makeText(this, "功能待定", Toast.LENGTH_LONG).show();
			break;

		}
	}

	private void getNextLine()
	{
		int index = getCurrentIndex();

		if (index + 2 < specialLyricViews.size())
		{
			SpecialLyricView next = specialLyricViews.get(index + 1);
			SpecialLyricView nextNext = specialLyricViews.get(index + 2);
			sideA = next.getTimeLabel();
			sideB = nextNext.getTimeLabel();
			mediaPlayer.seekTo(sideA);
		}
		Log.i("logi", "isPlay=" + mediaPlayer.isPlaying());

	}

	private void getPrevLine()
	{
		int index = getCurrentIndex();
		if (index > 0)
		{
			SpecialLyricView prev = specialLyricViews.get(index - 1);
			SpecialLyricView curr = specialLyricViews.get(index);
			sideA = prev.getTimeLabel();
			sideB = curr.getTimeLabel();
			mediaPlayer.seekTo(sideA);
		}

	}

	private int getCurrentIndex()
	{

		if (specialLyricViews != null && mediaPlayer != null && mediaPlayer.getDuration() > 0)
		{
			int current = mediaPlayer.getCurrentPosition();
			for (int i = 0; i < specialLyricViews.size(); i++)
			{
				Integer timeA = specialLyricViews.get(i).getTimeLabel();
				Integer timeB = i == (specialLyricViews.size() - 1) ? mediaPlayer.getDuration() : specialLyricViews.get(i + 1).getTimeLabel();
				// 含头不含尾,因为当暂停之后再seekto时,current会等于sideA,所以要含头不含尾
				if (timeA <= current && current < timeB)
				{
					return i;
				}
			}
		}
		return -1;
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

		tv_aSide.setText("");
		tv_bSide.setText("");
		tv_title.setText("");

		Intent intent = getIntent();
		documentId = intent.getIntExtra("Id", 429);

		// 从数据库读取缓存,如果时间超过10分钟

		iv_line.setOnClickListener(this);
		iv_prev.setOnClickListener(this);
		iv_play.setOnClickListener(this);
		iv_next.setOnClickListener(this);
		iv_microphone.setOnClickListener(this);

		ActionBar actionBar = getActionBar();
		// 返回按钮
		actionBar.setDisplayHomeAsUpEnabled(true);

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
		database = new Database(this);
		initData();
	}

	private void initData()
	{
		//如果已经下载,那么直接使用下载的数据
		database.docsSelectById(documentId);
		
		
		
		String url = NetworkUtil.getDocById(documentId);

		UrlCache cache = database.cacheSelectByUrl(url);
		if (cache == null || (System.currentTimeMillis() - cache.UpdateAt > 6000000))// 60分钟
		{
			Log.i("logi", "使用网络:" + url);
			new HttpUtils().send(HttpMethod.GET, url, new RequestCallBack<String>()
			{

				@Override
				public void onFailure(HttpException error, String msg)
				{

				}

				@Override
				public void onSuccess(ResponseInfo<String> responseInfo)
				{
					Document document = new Gson().fromJson(responseInfo.result, Document.class);
					fillData(document);

					UrlCache urlCache = new UrlCache();
					urlCache.Url = this.getRequestUrl();
					urlCache.Json = responseInfo.result;
					urlCache.UpdateAt = System.currentTimeMillis();
					database.cacheInsertOrUpdate(urlCache);
				}

			});
		}
		else
		{
			Log.i("logi", "使用缓存:" + url);
			Document document = new Gson().fromJson(cache.Json, Document.class);
			fillData(document);
		}

	}

	private void fillData(Document document)
	{
		tv_aSide.setText("00:00");
		tv_bSide.setText(document.LengthString);
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
			// 在刚开始的时候,显示中文字幕的
			specialLyricView.showEnCn(SpecialLyricView.SHOW_NONE);
			ll_lyrics.addView(specialLyricView);
		}

		// 因为使用的是相对路径,但是在实际请求时要加上域名
		initMediaPlayer(document.SoundPath);
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
		if (mediaPlayer != null)
		{
			mediaPlayer.release();
			mediaPlayer = null;
		}
		database.closeConnection();
		super.onDestroy();
	}

	@Override
	public boolean onError(MediaPlayer mp, int what, int extra)
	{
		Log.i("logi", "onError:" + " what=" + what + " extra=" + extra);
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
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}

	}

	@Override
	public void onPrepared(MediaPlayer mp)
	{
		pb_buffering.setVisibility(View.INVISIBLE);
		mp.start();
		seekBar.setMax(mp.getDuration());
		tv_bSide.setText(millisecondsFormat(mp.getDuration()));
		sideB = mp.getDuration();

		// 点出播放单句,同时重置A-B两端
		if (specialLyricViews != null && mediaPlayer != null && mediaPlayer.getDuration() > 0)
		{
			for (int i = 0; i < specialLyricViews.size(); i++)
			{
				final Integer timeA = specialLyricViews.get(i).getTimeLabel();
				final Integer timeB = i == (specialLyricViews.size() - 1) ? mediaPlayer.getDuration() : specialLyricViews.get(i + 1).getTimeLabel();
				specialLyricViews.get(i).setOnClickListener(new OnClickListener()
				{

					@Override
					public void onClick(View v)
					{
						sideA = timeA;
						sideB = timeB;
						mediaPlayer.seekTo(sideA);
					}
				});

			}
		}

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
		}, 0, 100);
	}

	protected void refresh_seekbar()
	{

		long currentLineTime = 0;
		long nextLineTime = 0;

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

			if (currentLineTime <= currentPosition && currentPosition < nextLineTime)
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

	@Override
	public boolean onInfo(MediaPlayer mp, int what, int extra)
	{
		// MEDIA_INFO_UNKNOWN
		// MEDIA_INFO_VIDEO_TRACK_LAGGING
		// MEDIA_INFO_VIDEO_RENDERING_START
		// MEDIA_INFO_BUFFERING_START
		// MEDIA_INFO_BUFFERING_END
		// MEDIA_INFO_BAD_INTERLEAVING
		// MEDIA_INFO_NOT_SEEKABLE
		// MEDIA_INFO_METADATA_UPDATE
		// MEDIA_INFO_UNSUPPORTED_SUBTITLE
		// MEDIA_INFO_SUBTITLE_TIMED_OUT

		switch (what)
		{
		case MediaPlayer.MEDIA_INFO_UNKNOWN:
			Log.i("logi", "INFO=MEDIA_INFO_UNKNOWN" + what);
			break;
		case MediaPlayer.MEDIA_INFO_VIDEO_TRACK_LAGGING:
			Log.i("logi", "INFO=MEDIA_INFO_VIDEO_TRACK_LAGGING" + what);
			break;
		case MediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START:
			Log.i("logi", "INFO=MEDIA_INFO_VIDEO_RENDERING_START" + what);
			break;
		case MediaPlayer.MEDIA_INFO_BUFFERING_START:
			Log.i("logi", "INFO=MEDIA_INFO_BUFFERING_START" + what);
			break;
		case MediaPlayer.MEDIA_INFO_BUFFERING_END:
			Log.i("logi", "INFO=MEDIA_INFO_BUFFERING_END" + what);
			break;
		case MediaPlayer.MEDIA_INFO_BAD_INTERLEAVING:
			Log.i("logi", "INFO=MEDIA_INFO_BAD_INTERLEAVING" + what);
			break;
		case MediaPlayer.MEDIA_INFO_METADATA_UPDATE:
			Log.i("logi", "INFO=MEDIA_INFO_METADATA_UPDATE" + what);
			break;
		case MediaPlayer.MEDIA_INFO_NOT_SEEKABLE:
			Log.i("logi", "INFO=MEDIA_INFO_NOT_SEEKABLE" + what);
			break;
		case MediaPlayer.MEDIA_INFO_UNSUPPORTED_SUBTITLE:
			Log.i("logi", "INFO=MEDIA_INFO_UNSUPPORTED_SUBTITLE" + what);
			break;
		case MediaPlayer.MEDIA_INFO_SUBTITLE_TIMED_OUT:
			Log.i("logi", "INFO=MEDIA_INFO_SUBTITLE_TIMED_OUT" + what);
			break;
		}
		return true;
	}
}
