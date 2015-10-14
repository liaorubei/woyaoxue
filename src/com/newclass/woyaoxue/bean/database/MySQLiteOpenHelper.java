package com.newclass.woyaoxue.bean.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class MySQLiteOpenHelper extends SQLiteOpenHelper
{

	private static String name = "woyaoxue";
	private static CursorFactory factory = null;
	private static int version = 1;

	public MySQLiteOpenHelper(Context context)
	{
		super(context, name, factory, version);
	}

	@Override
	public void onCreate(SQLiteDatabase db)
	{
		db.execSQL("create table document(Id int primary key,DocumentId int,TitleOne varchar,TitleTwo varchar,SoundPath varchar,DownloadPath varchar,Length long,Duration int,Md5 varchar,IsDownload int ,ModifyTime varchar);");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
	{
		// TODO Auto-generated method stub

	}

}
