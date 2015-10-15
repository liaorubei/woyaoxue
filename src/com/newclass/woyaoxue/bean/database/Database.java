package com.newclass.woyaoxue.bean.database;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.newclass.woyaoxue.bean.Document;
import com.newclass.woyaoxue.bean.Folder;
import com.newclass.woyaoxue.bean.Level;
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
	 * 请及时关闭数据库连接,否则容易出现内存泄漏,数据库连接是比较耗内存的资源
	 */
	public void closeConnection()
	{
		helper.close();
	}

	/**
	 * @param DocumentId
	 * @return 数据库是否有这条记录的判断,如果有则true,反之false
	 */
	public boolean docsExists(int id)
	{
		Cursor cursor = readableDatabase.rawQuery("select Id from document where Id=?", new String[] { id + "" });
		int count = cursor.getCount();
		cursor.close();
		return count > 0;
	}

	public void docsInsert(Document item)
	{
		ContentValues values = new ContentValues();

		values.put("Id", item.Id);
		values.put("LevelId", item.LevelId);
		values.put("FolderId", item.FolderId);
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

	public int docsCountByFolderId(int folderId)
	{
		Cursor cursor = readableDatabase.rawQuery("select count(FolderId) from document where FolderId=?", new String[] { folderId + "" });
		int count = 0;
		if (cursor.moveToNext())
		{
			count = cursor.getInt(0);
		}
		return count;
	}

	public void levelClear()
	{}

	public void levelInsert(Level level)
	{
		ContentValues values = new ContentValues();
		values.put("Id", level.Id);
		values.put("Name", level.Name);
		write.insert("level", null, values);
	}

	public boolean levelExists(int id)
	{
		Cursor cursor = readableDatabase.rawQuery("select Id from level where Id=?", new String[] { id + "" });
		int count = cursor.getCount();
		cursor.close();
		return count > 0;
	}

	public void folderInsert(Folder folder)
	{
		ContentValues values = new ContentValues();
		values.put("Id", folder.Id);
		values.put("Name", folder.Name);
		write.insert("folder", null, values);
	}

	public boolean folderExists(int id)
	{
		Cursor cursor = readableDatabase.rawQuery("select Id from folder where Id=?", new String[] { id + "" });
		int count = cursor.getCount();
		cursor.close();
		return count > 0;
	}

	public List<Folder> folderSelectList()
	{
		Cursor cursor = readableDatabase.rawQuery("select Id,Name from folder", null);
		List<Folder> list = new ArrayList<Folder>();
		while (cursor.moveToNext())
		{
			Folder folder = new Folder();
			folder.Id = cursor.getInt(0);
			folder.Name = cursor.getString(1);
			list.add(folder);
		}
		return list;
	}

	/**
	 * @return select Id,Name,(select count(FolderId) from document where FolderId=folder.Id) as DocsCount from folder order by folder.Id
	 */
	public List<Folder> folderSelectListWithDocsCount()
	{
		Cursor cursor = readableDatabase.rawQuery("select Id,Name,(select count(FolderId) from document where FolderId=folder.Id) as DocsCount from folder order by folder.Id", null);
		List<Folder> list = new ArrayList<Folder>();
		while (cursor.moveToNext())
		{
			Folder folder = new Folder();
			folder.Id = cursor.getInt(0);
			folder.Name = cursor.getString(1);
			folder.DocsCount = cursor.getInt(2);
			list.add(folder);
		}
		return list;
	}

}
