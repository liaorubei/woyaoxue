package com.newclass.woyaoxue.bean.database;

public class Document
{
	@com.lidroid.xutils.db.annotation.Id
	public int Id;
	/**
	 * 这个是文档Id,因为不想与自动生成的Id冲突,所以加前缀doc,当处理业务时,要使用DocId这个字段,而非Id
	 */
	public int DocId;
	public int LevelId;
	public String TitleOne;
	public String TitleTwo;
	public String Date;
	public long Length;
	public String Time;
	public String Lyrics;
	public String Path;
}
