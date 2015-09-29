package com.newclass.woyaoxue.util;

public class NetworkUtil
{
	// public static String domain = "http://192.168.3.121:801";

	public static String domain = "http://voc2015.azurewebsites.net";

	public static String getDocs(String folderId, String levelid, String skip, String take)
	{
		return domain + "/NewClass/GetDocs?folderId=" + folderId + "&levelId=" + levelid + "&skip=" + skip + "&take=" + take;
	}

	public static String getLevels()
	{
		return domain + "/NewClass/levels";
	}

	/**
	 * /NewClass/DocsByLevelId/{levelId}
	 * 
	 * @param id
	 * @return
	 */
	public static String getDocsByLevelId(int id)
	{
		return domain + "/NewClass/DocsByLevelId/" + id;
	}

	public static String getDocById(int id)
	{

		return domain + "/NewClass/DocById/" + id;
	}

	/**
	 * 把一个网站的相对路径转为这个网站的标准HTTP全路径
	 * 
	 * @param soundPath
	 * @return
	 */
	public static String getFullPath(String path)
	{

		return domain + path;
	}

	/**
	 * /NewClass/GetLatestPackage
	 * 
	 * @return
	 */
	public static String getLatest()
	{

		return domain + "/NewClass/GetLatestPackage";
	}

	/**
	 * 
	 * 
	 * @param levelId 取得指定LevelId下文件夹内文档的个数, 如果不大于0,表示取得所有数据
	 * @return domain + "/NewClass/Folders?levelId=" + levelId
	 */
	public static String getFolders(int levelId)
	{
		return domain + "/NewClass/Folders?levelId=" + levelId;
	}

}
