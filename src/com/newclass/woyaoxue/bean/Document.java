package com.newclass.woyaoxue.bean;

import java.util.List;

public class Document
{
	public Document(int id, String title, String soundPath)
	{
		this.Id = id;
		this.Title = title;
		this.SoundPath = soundPath;
	}

	public int Id;
	public String Title;
	public List<Lyric> Lyrics;
	public String SoundPath;
	public double Duration;
	public double Size;
	
	//是否需要下载
	public boolean NeedDownLoad;
}
