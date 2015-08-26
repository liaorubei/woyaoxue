package com.newclass.woyaoxue.bean;

import android.os.Parcel;
import android.os.Parcelable;

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
	public String SoundPath;

}
