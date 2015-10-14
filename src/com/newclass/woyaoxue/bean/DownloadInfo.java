package com.newclass.woyaoxue.bean;

import java.io.File;

import com.newclass.woyaoxue.view.CircularProgressBar;

public class DownloadInfo
{
	public String Url;
	public File Target;
	public String Md5;
	public long Total;
	public long Current;
	public CircularProgressBar bar;
	public String Title;
}
