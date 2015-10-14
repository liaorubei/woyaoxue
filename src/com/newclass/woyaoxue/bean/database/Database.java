package com.newclass.woyaoxue.bean.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.newclass.woyaoxue.bean.Document;
import com.newclass.woyaoxue.util.NetworkUtil;

public class Database
{

	private SQLiteDatabase readableDatabase;
	private SQLiteDatabase write;

	public Database(Context context)
	{
		MySQLiteOpenHelper helper = new MySQLiteOpenHelper(context);
		readableDatabase = helper.getReadableDatabase();
		write = helper.getWritableDatabase();
	}

	public boolean docsExists(int id)
	{
		Cursor cursor = readableDatabase.rawQuery("select Id from document where DocumentId=?", new String[] { id + "" });
		return cursor.getCount() > 0;
	}

	public void docsInsert(Document item)
	{
		ContentValues values = new ContentValues();

		values.put("DocumentId", item.Id);
		values.put("TitleOne", item.Title);
		values.put("TitleTwo", item.TitleTwo);
		values.put("SoundPath", item.SoundPath);
		values.put("IsDownload", 0);
		values.put("DownloadPath", NetworkUtil.getFullPath(item.SoundPath));
		write.insert("document", null, values);
	}

	public void docsUpdateSuccessByDownloadPath(String downloadPath)
	{
		write.execSQL("update document set IsDownload=1 where DownloadPath=?", new String[] { downloadPath });
	}

}
