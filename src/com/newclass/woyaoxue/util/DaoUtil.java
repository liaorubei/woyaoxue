package com.newclass.woyaoxue.util;

import java.util.List;

import android.content.Context;

import com.lidroid.xutils.DbUtils;
import com.lidroid.xutils.db.sqlite.Selector;
import com.lidroid.xutils.exception.DbException;
import com.newclass.woyaoxue.bean.database.Document;

public class DaoUtil
{

	public static List<Document> getAllDocs(Context context, boolean desc)
	{
		DbUtils db = DbUtils.create(context);
		try
		{
			return db.findAll(Selector.from(Document.class).orderBy("Date", desc));
		}
		catch (DbException e)
		{

			e.printStackTrace();
		}
		return null;
	}

	public static void addDocument(com.newclass.woyaoxue.bean.Document doc, Context context)
	{
		try
		{
			DbUtils db = DbUtils.create(context);
			Document document = new Document();
			document.DocId = doc.Id;
			document.TitleOne = doc.Title;
			document.TitleTwo = doc.TitleTwo;
			document.Date = doc.DateString;
			document.Length = doc.Length;
			document.Time = doc.LengthString;
			document.Path = doc.SoundPath;

			db.save(document);

		}
		catch (Exception e)
		{

		}

	}

	public static void documentSaveorUpdate(com.newclass.woyaoxue.bean.Document doc, Context context)
	{
		try
		{
			DbUtils utils = DbUtils.create(context);
			Document document = utils.findFirst(Selector.from(Document.class).where("DocId", "=", doc.Id));
			if (document == null)
			{
				document = new Document();
				document.DocId = doc.Id;
				document.TitleOne = doc.Title;
				document.TitleTwo = doc.TitleTwo;
				document.Date = doc.DateString;
				document.Length = doc.Length;
				document.Time = doc.LengthString;
				document.Path = doc.SoundPath;
				utils.saveOrUpdate(document);
			}

		}
		catch (Exception e)
		{

		}

	}

}
