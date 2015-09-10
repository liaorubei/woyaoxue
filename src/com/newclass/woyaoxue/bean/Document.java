package com.newclass.woyaoxue.bean;

import java.util.List;

import android.widget.ProgressBar;

public class Document
{
	public int Id;
	public String Title;
	public List<Lyric> Lyrics;
	public String SoundPath;
	public double Duration;
	public long Length;

	// 是否需要下载
	public boolean NeedDownLoad;
	// 声音文件是否已经下载,并已经存在
	public boolean SoundFileExists;

	public ProgressBar progressBar;
}
