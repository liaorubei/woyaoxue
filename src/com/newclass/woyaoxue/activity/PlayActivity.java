package com.newclass.woyaoxue.activity;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

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

public class PlayActivity extends Activity
		implements OnClickListener, OnBufferingUpdateListener, OnPreparedListener, OnErrorListener {
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

	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case REFRESH_SEEKBAR:
				refresh_seekbar();
				break;

			default:
				break;
			}

		};
	};

	private String url;
	private List<LinearLayout> lines;
	private List<TextView> originalTextViews;
	private List<TextView> translateTextViews;

	@Override
	protected void onCreate(Bundle savedInstanceState) {// http://voc2015.cloudapp.net/NewClass/DocById/431
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_play);
		ViewUtils.inject(this);

		Intent intent = getIntent();
		int id = intent.getIntExtra("Id", 431);

		bt_play.setOnClickListener(this);
		bt_pause.setOnClickListener(this);

		new HttpUtils().send(HttpMethod.GET, NetworkUtil.getDocById(id), new RequestCallBack<String>() {

			@Override
			public void onSuccess(ResponseInfo<String> responseInfo) {
				Document fromJson = new Gson().fromJson(responseInfo.result, Document.class);
				tv_title.setText(fromJson.Title);

			}

			@Override
			public void onFailure(HttpException error, String msg) {
				// TODO Auto-generated method stub

			}
		});

		seekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

			@Override
			public void onStopTrackingTouch(SeekBar sb) {
				// TODO Auto-generated method stub

				mediaPlayer.seekTo(sb.getProgress());

			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
				// 如果由用户手动拖动则更改左右两边的时间标签内容
				if (fromUser) {
					tv_aSide.setText(millisecondsFormat(mediaPlayer.getCurrentPosition()));
				}

			}
		});

		lines = new ArrayList<LinearLayout>();
		originalTextViews = new ArrayList<TextView>();
		translateTextViews = new ArrayList<TextView>();
		for (int i = 0; i < 25; i++) {
			Lyric lyric = new Lyric();
			lyric.Original = "为什么老周和老罗都对跑分有这样的反应？我认为，前者是在质疑其必要性，后者是在质疑其客观性。";
			lyric.Translate = "Why did Lao zhou and Mr. Luo to run points have this reaction?I think that the former is in question its necessity, the latter is in doubt its objectivity.";
			lyric.Timestamp = i;
			lyrics.add(lyric);

			LinearLayout line = generateLine(lyric);

			lines.add(line);

			ll_lyrics.addView(line);
		}

		initMediaPlayer();
	}

	private LinearLayout generateLine(Lyric lyric) {

		LinearLayout layout = new LinearLayout(this);
		layout.setOrientation(LinearLayout.VERTICAL);

		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
				LinearLayout.LayoutParams.WRAP_CONTENT);
		params.setMargins(0, 5, 0, 5);
		layout.setLayoutParams(params);

		TextView textView1 = new TextView(this);

		textView1.setText(lyric.Original);
		originalTextViews.add(textView1);

		TextView textView2 = new TextView(this);
		textView2.setText(lyric.Translate);
		translateTextViews.add(textView2);

		layout.addView(textView1);
		layout.addView(textView2);
		return layout;
	}

	private void initView() {
		Log.i("logi", "---------------");
		RelativeLayout message_popup = (RelativeLayout) View.inflate(this, R.layout.message_popup, null);
		Button confirmBtn = (Button) message_popup.findViewById(R.id.confirm_message_btn);
		EditText mSourceText = (EditText) message_popup.findViewById(R.id.source_text);
		TextView readingMessageNum = (TextView) message_popup.findViewById(R.id.reading_message);
		TextView totalMessageNum = (TextView) message_popup.findViewById(R.id.total_messages);
		readingMessageNum.setText("1");
		totalMessageNum.setText("顶戴sdf");
		confirmBtn.setEnabled(true);
		confirmBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Toast.makeText(PlayActivity.this, "show it", Toast.LENGTH_LONG).show();
			}
		});

		PopupWindow popupWindow = new PopupWindow(message_popup);
		popupWindow.showAsDropDown(sv_lyrics, 5, 5);

	}

	protected String millisecondsFormat(int milliseconds) {

		long days = milliseconds / (1000 * 60 * 60 * 24);
		long hours = (milliseconds % (1000 * 60 * 60 * 24)) / (1000 * 60 * 60);
		long minutes = (milliseconds % (1000 * 60 * 60)) / (1000 * 60);
		long seconds = (milliseconds % (1000 * 60)) / 1000;

		int min = milliseconds / (60 * 1000);
		int sec = milliseconds / (1000);
		return minutes + ":" + seconds;
	}

	protected void refresh_seekbar() {
		// TODO Auto-generated method stub

		seekBar.setProgress(mediaPlayer.getCurrentPosition());
		seekBar.setSecondaryProgress(mediaPlayer.getDuration() / 2);
		tv_aSide.setText(millisecondsFormat(mediaPlayer.getCurrentPosition()));
		// tv_bSide.setText(mediaPlayer.getDuration() + "");
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.bt_paly:
			if (mediaPlayer.isPlaying()) {
				mediaPlayer.pause();
			} else {
				mediaPlayer.start();
			}

			break;

		case R.id.bt_pause:
			break;
		}
	}

	private void initMediaPlayer() {
		try {
			url = "http://voc2015.cloudapp.net/File/20150822/5e00f7a6-ed99-4ae0-9065-8796d28f4145.mp3";
			mediaPlayer = new MediaPlayer();
			mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
			mediaPlayer.setOnBufferingUpdateListener(this);
			mediaPlayer.setOnPreparedListener(this);
			mediaPlayer.setOnErrorListener(this);
			mediaPlayer.setDataSource(url);
			// mediaPlayer.prepare(); // might take long! (for buffering, etc)
			mediaPlayer.prepareAsync();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();

		}
	}

	@Override
	public void onBufferingUpdate(MediaPlayer mp, int percent) {
		// Log.i("logi", "onBufferingUpdate Position:" + mp.getCurrentPosition()
		// + " Duration:" + mp.getDuration() + " percent:" + percent + "%");

		// seekBar.setProgress(mp.getCurrentPosition());
		// seekBar.setSecondaryProgress((int) (mp.getDuration() * percent *
		// 0.01));
		// tv_aSide.setText("" + mp.getCurrentPosition());
	}

	@Override
	public void onPrepared(MediaPlayer mp) {
		// TODO Auto-generated method stub
		mediaPlayer.start();
		seekBar.setMax(mp.getDuration());
		tv_bSide.setText("" + mp.getDuration());

		// 定时更新歌词及SeekBar
		new Timer().schedule(new TimerTask() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				if (mediaPlayer.isPlaying()) {
					handler.sendEmptyMessage(REFRESH_SEEKBAR);
				}

			}
		}, 0, 1000);
	}

	@Override
	public boolean onError(MediaPlayer mp, int what, int extra) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();

		handler.removeCallbacksAndMessages(null);

		mediaPlayer.release();
		mediaPlayer = null;
	}

}
