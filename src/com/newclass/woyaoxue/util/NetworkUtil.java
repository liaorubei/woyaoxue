package com.newclass.woyaoxue.util;

public class NetworkUtil
{
	public static String domain = "http://192.168.3.121:801";

	public static String getDocs()
	{
		return domain + "";
	}

	public static String getLevels()
	{
		return domain + "/NewClass/levels";
	}

	public static String getDocsById(int levelId)
	{
		return domain + "/NewClass/DocsByLevel?levelid=" + levelId;
	}

}
