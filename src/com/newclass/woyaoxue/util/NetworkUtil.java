package com.newclass.woyaoxue.util;

public class NetworkUtil
{
	// public static String domain = "http://192.168.3.121:801";

	public static String domain = "http://voc2015.azurewebsites.net";

	public static String getDocs()
	{
		return domain + "";
	}

	public static String getLevels()
	{
		return domain + "/NewClass/levels";
	}

	/**
	 * /NewClass/DocsByLevel?levelid={levelId}
	 * @param levelId
	 * @return
	 */
	public static String getDocsByLevelId(int levelId)
	{
		return domain + "/NewClass/DocsByLevel?levelid=" + levelId;
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
	 * @return
	 */
	public static String getLatest()
	{
		
		return domain+"/NewClass/GetLatestPackage";
	}

}
