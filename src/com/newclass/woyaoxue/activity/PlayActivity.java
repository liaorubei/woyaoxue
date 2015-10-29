package com.newclass.woyaoxue.activity;

import java.io.File;
import java.io.FileDescriptor;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import com.google.gson.Gson;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;
import com.newclass.woyaoxue.bean.Document;
import com.newclass.woyaoxue.bean.DownloadInfo;
import com.newclass.woyaoxue.bean.Lyric;
import com.newclass.woyaoxue.bean.UrlCache;
import com.newclass.woyaoxue.database.Database;
import com.newclass.woyaoxue.util.FolderUtil;
import com.newclass.woyaoxue.util.Log;
import com.newclass.woyaoxue.util.NetworkUtil;
import com.newclass.woyaoxue.view.SpecialLyricView;
import com.voc.woyaoxue.R;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnErrorListener;
import android.media.MediaPlayer.OnInfoListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

public class PlayActivity extends Activity implements OnClickListener, OnPreparedListener, OnErrorListener, OnInfoListener
{
	protected static final int REFRESH_SEEKBAR = 0;
	private Database database;

	private int documentId;
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
	private ImageView iv_line, iv_microphone, iv_next, iv_play, iv_prev;
	private LinearLayout ll_lyrics, ll_play, ll_record;
	private MediaPlayer mediaPlayer;
	private MediaRecorder mediaRecorder;
	private ProgressBar pb_buffering;
	private SeekBar seekBar;
	private ImageView iv_cover;
	private int sideA = 0, sideB = 0;

	private List<SpecialLyricView> specialLyricViews;
	private List<Integer> subTitleIcons;
	private Integer subTitleState = 0;

	private ScrollView sv_lyrics;
	private TextView tv_bSide, tv_aSide, tv_title;
	private View fl_control;
	private View iv_renext;
	private ImageView iv_back;

	private void initView()
	{
		iv_line = (ImageView) findViewById(R.id.iv_line);
		iv_microphone = (ImageView) findViewById(R.id.iv_microphone);
		iv_next = (ImageView) findViewById(R.id.iv_next);
		iv_play = (ImageView) findViewById(R.id.iv_paly);
		iv_prev = (ImageView) findViewById(R.id.iv_prev);
		iv_renext = findViewById(R.id.iv_renext);

		ll_lyrics = (LinearLayout) findViewById(R.id.ll_lyrics);
		pb_buffering = (ProgressBar) findViewById(R.id.pb_buffering);
		sv_lyrics = (ScrollView) findViewById(R.id.sv_lyrics);
		tv_aSide = (TextView) findViewById(R.id.tv_aSide);
		tv_bSide = (TextView) findViewById(R.id.tv_bSide);
		tv_title = (TextView) findViewById(R.id.tv_title);

		seekBar = (SeekBar) findViewById(R.id.seekBar);
		iv_cover = (ImageView) findViewById(R.id.iv_cover);

		ll_play = (LinearLayout) findViewById(R.id.ll_play);
		ll_record = (LinearLayout) findViewById(R.id.ll_record);
		fl_control = findViewById(R.id.fl_control);

		iv_back = (ImageView) findViewById(R.id.iv_back);
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
			} else
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
		{
			ValueAnimator a = ValueAnimator.ofInt(0, 320);
			a.addUpdateListener(new AnimatorUpdateListener()
			{

				@Override
				public void onAnimationUpdate(ValueAnimator animation)
				{
					int value = (Integer) animation.getAnimatedValue();
					Log.i("value=" + value);

					FrameLayout.LayoutParams layoutParams1 = (android.widget.FrameLayout.LayoutParams) ll_play.getLayoutParams();
					layoutParams1.width = 320;
					layoutParams1.leftMargin = -value;
					ll_play.setLayoutParams(layoutParams1);

					FrameLayout.LayoutParams layoutParams2 = (android.widget.FrameLayout.LayoutParams) ll_record.getLayoutParams();
					layoutParams2.width = 320;
					layoutParams2.leftMargin = 320 - value;
					ll_record.setLayoutParams(layoutParams2);
				}
			});

			a.setDuration(1000);
			a.start();

		}
			break;

		case R.id.iv_renext:
			Log.i("点击了iv_renext");
			break;
		case R.id.iv_back:
		{
			ValueAnimator a = ValueAnimator.ofInt(0, 320);
			a.addUpdateListener(new AnimatorUpdateListener()
			{

				@Override
				public void onAnimationUpdate(ValueAnimator animation)
				{
					int value = (Integer) animation.getAnimatedValue();
					Log.i("value=" + value);

					FrameLayout.LayoutParams layoutParams1 = (android.widget.FrameLayout.LayoutParams) ll_play.getLayoutParams();
					layoutParams1.width = 320;
					layoutParams1.leftMargin = value - 320;
					ll_play.setLayoutParams(layoutParams1);

					FrameLayout.LayoutParams layoutParams2 = (android.widget.FrameLayout.LayoutParams) ll_record.getLayoutParams();
					layoutParams2.width = 320;
					layoutParams2.leftMargin = value;
					ll_record.setLayoutParams(layoutParams2);
				}
			});

			a.setDuration(1000);
			a.start();
		}

			break;
		default:
			break;
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.play_activity, menu);
		return true;
	}

	@Override
	public boolean onError(MediaPlayer mp, int what, int extra)
	{
		Log.i("logi", "onError:" + " what=" + what + " extra=" + extra);
		return false;
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

				if (mediaPlayer != null) // && mediaPlayer.isPlaying())
				{
					handler.sendEmptyMessage(REFRESH_SEEKBAR);

				}

			}
		}, 0, 100);
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

	private void initData()
	{
		// 如果已经下载,那么直接使用下载的数据
		DownloadInfo info = database.docsSelectById(documentId);
		if (info != null && info.IsDownload == 1 && !TextUtils.isEmpty(info.Json))
		{
			Document document = new Gson().fromJson(info.Json, Document.class);
			fillData(document);
			return;
		}

		String url = NetworkUtil.getDocById(documentId);

		UrlCache cache = database.cacheSelectByUrl(url);
		if (cache == null || (System.currentTimeMillis() - cache.UpdateAt > 6000000)) // 60分钟
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
		} else
		{
			Log.i("logi", "使用缓存:" + url);
			Document document = new Gson().fromJson(cache.Json, Document.class);
			fillData(document);
		}

	}

	/**
	 * 使用指定的音频路径初始化MediaPlayer
	 * 
	 * @param path
	 *            音频的相对路径
	 */
	private void initMediaPlayer(String path)
	{
		try
		{
			mediaPlayer = new MediaPlayer();
			mediaPlayer.setLooping(true);// 默认开启循环播放
			mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
			mediaPlayer.setOnPreparedListener(this);
			mediaPlayer.setOnErrorListener(this);
			mediaPlayer.setOnInfoListener(this);

			File file = new File(FolderUtil.rootDir(this), path);
			if (file.exists())
			{
				mediaPlayer.setDataSource(file.getAbsolutePath());
				mediaPlayer.prepare();
			} else
			{
				mediaPlayer.setDataSource(NetworkUtil.getFullPath(path));
				mediaPlayer.prepareAsync();
			}
		} catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	private void initMediaRecorder(FileDescriptor path)
	{
		try
		{
			mediaRecorder = new MediaRecorder();
			mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
			mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
			mediaRecorder.setOutputFile(path);
			mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
			mediaRecorder.prepare();
		} catch (Exception e)
		{
			e.printStackTrace();
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
				iv_cover.setVisibility(View.VISIBLE);

				// ScaleAnimation scaleAnimation = new ScaleAnimation(0, 1, 0,
				// 1, Animation.RELATIVE_TO_SELF, 0.5f,
				// Animation.RELATIVE_TO_SELF, 0.5f);
				// scaleAnimation.setDuration(1000);
				// scaleAnimation.setFillAfter(true);
				// iv_cover.startAnimation(scaleAnimation);

			}
			break;
		case R.drawable.ico_actionbar_subtitle_cn:
			for (SpecialLyricView view : specialLyricViews)
			{
				view.showEnCn(SpecialLyricView.SHOW_CN);
				iv_cover.setVisibility(View.INVISIBLE);
			}
			break;
		case R.drawable.ico_actionbar_subtitle_encn:
			for (SpecialLyricView view : specialLyricViews)
			{
				view.showEnCn(SpecialLyricView.SHOW_ENCN);
				iv_cover.setVisibility(View.INVISIBLE);
			}
			break;
		}

	}

	protected String millisecondsFormat(int milliseconds)
	{
		long minutes = (milliseconds % (1000 * 60 * 60)) / (1000 * 60);
		long seconds = (milliseconds % (1000 * 60)) / 1000;
		return (minutes < 10 ? "0" : "") + minutes + ":" + (seconds < 10 ? "0" : "") + seconds;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_play);
		initView();

		subTitleIcons = new ArrayList<Integer>();
		subTitleIcons.add(R.drawable.ico_actionbar_subtitle_none);
		subTitleIcons.add(R.drawable.ico_actionbar_subtitle_cn);
		subTitleIcons.add(R.drawable.ico_actionbar_subtitle_encn);

		tv_title = (TextView) findViewById(R.id.tv_title);

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
		iv_renext.setOnClickListener(this);
		iv_back.setOnClickListener(this);

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

	@Override
	protected void onDestroy()
	{
		super.onDestroy();
		handler.removeCallbacksAndMessages(null);
		if (mediaPlayer != null)
		{
			mediaPlayer.release();
			mediaPlayer = null;
		}

		if (mediaRecorder != null)
		{
			mediaRecorder.release();
			mediaRecorder = null;
		}
		if (database != null)
		{
			database.closeConnection();
			database = null;
		}
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
				} else
				{
					sv_lyrics.scrollTo(0, view.getTop());
				}

			} else
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
}
