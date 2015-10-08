package com.newclass.woyaoxue.util;

import android.content.Context;

import com.lidroid.xutils.DbUtils;
import com.lidroid.xutils.exception.DbException;
import com.newclass.woyaoxue.bean.Document;

public class DocDbUtil
{

	/**
	 * 添加一条记录到表Doc中
	 * 
	 * @param doc
	 * @param context
	 */
	public static void insert(Document doc, Context context)
	{

		try
		{
			DbUtils db = DbUtils.create(context);
			com.newclass.woyaoxue.bean.database.Document document = new com.newclass.woyaoxue.bean.database.Document();
			document.Id = doc.Id;
			document.TitleOne = doc.Title;
			document.TitleTwo = doc.TitleTwo;
			document.Date = doc.DateString;
			document.Length = doc.Length;
			document.Time = doc.LengthString;
			document.Path = doc.SoundPath;
			db.save(document);// 使用saveBindingId保存实体时会为实体的id赋值
		}
		catch (DbException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	
	
}
