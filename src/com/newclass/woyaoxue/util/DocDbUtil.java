package com.newclass.woyaoxue.util;

import android.content.Context;

import com.lidroid.xutils.DbUtils;
import com.lidroid.xutils.db.annotation.Id;
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
			Doc user = new Doc(); // 这里需要注意该对象必须有id属性，或者有通过@ID注解的属性
			user.docId = doc.Id;
			user.title = doc.Title;
			db.save(user);// 使用saveBindingId保存实体时会为实体的id赋值
		}
		catch (DbException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public static class Doc
	{
		public String title;
		@Id
		private int docId;
	}

}
