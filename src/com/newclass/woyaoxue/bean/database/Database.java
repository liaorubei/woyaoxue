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
	private MySQLiteOpenHelper helper;

	public Database(Context context)
	{
		helper = new MySQLiteOpenHelper(context);
		readableDatabase = helper.getReadableDatabase();
		write = helper.getWritableDatabase();
	}

	/**
	 * @param DocumentId
	 * @return 数据库是否有这条记录的判断,如果有则true,反之false
	 */
	public boolean docsExists(int id)
	{
		Cursor cursor = readableDatabase.rawQuery("select Id from document where DocumentId=?", new String[] { id + "" });
		int count = cursor.getCount();
		cursor.close();
		return count > 0;
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

	public void docsDeleteByDownloadPath(String downloadPath)
	{
		write.execSQL("delete from document where DownloadPath=?", new String[] { downloadPath });
	}

	public void docsSelectUnfinishedDownload()
	{

	}

	/**
	 * 请及时关闭数据库连接,否则容易出现内存泄漏,数据库连接是比较耗内存的资源
	 */
	public void closeConnection()
	{
		helper.close();
	}

}
