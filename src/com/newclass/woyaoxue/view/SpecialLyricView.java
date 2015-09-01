package com.newclass.woyaoxue.view;

import com.newclass.woyaoxue.bean.Lyric;

import android.content.Context;
import android.graphics.Color;
import android.widget.LinearLayout;
import android.widget.TextView;

public class SpecialLyricView extends LinearLayout implements Comparable<SpecialLyricView>
{
	private Lyric mLyric;
	private TextView originalTextView;

	public SpecialLyricView(Context context, Lyric lyric)
	{
		super(context);
		this.mLyric = lyric;

		// 因为是放在线性布局中的，所以要使用LinearLayout.LayoutParams
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
		params.setMargins(0, 5, 0, 5);
		this.setLayoutParams(params);
		this.setOrientation(LinearLayout.VERTICAL);

		originalTextView = new TextView(context);

		originalTextView.setText(lyric.Original);

		this.addView(originalTextView);

		TextView textView2 = new TextView(context);
		textView2.setText(lyric.Translate);
		this.addView(textView2);
	}

	@Override
	public int compareTo(SpecialLyricView another)
	{
		return Long.valueOf(this.mLyric.TimeLabel).compareTo(another.mLyric.TimeLabel);
	}

	public Long getTimeLabel()
	{
		return this.mLyric.TimeLabel;
	}

	public void highlight()
	{
		this.originalTextView.setTextColor(Color.BLUE);
	}

	public void resetColor()
	{
		this.originalTextView.setTextColor(Color.BLACK);
	}

}
