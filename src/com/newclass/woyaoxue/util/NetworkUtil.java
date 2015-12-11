package com.newclass.woyaoxue.util;

public class NetworkUtil
{
	// public static String domain = "http://192.168.3.121:801";

	public static String domain = "http://voc2015.azurewebsites.net";

	public static final String userRefresh = domain + "/api/user/refresh";
	public static final String userSignIn = domain + "/api/user/login";
	public static final String userLogout = domain + "/api/user/logout";
	public static final String userCreate = domain + "/api/user/create";
	public static final String userSearch = domain + "/api/user/search";
	public static final String teacherEnqueue = domain + "/api/teacher/enqueue";
	public static final String studentCall = domain + "/api/student/call";

	public static String format(String text, Object... para)
	{
		for (int i = 0; i < para.length; i++)
		{
			text = text.replaceAll("\\{" + i + "\\}", para[i] + "");
		}
		return text;
	}

	public static String getDocById(int id)
	{

		return domain + "/NewClass/DocById/" + id;
	}

	public static String getDocs(String folderId, String skip, String take)
	{
		return domain + format("/NewClass/GetDocs?folderId={0}&skip={1}&take={2}", folderId, skip, take);// "/NewClass/GetDocs?folderId=" + folderId + "&skip=" + skip + "&take=" + take;
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

	public static String getLevels()
	{
		return domain + "/NewClass/levels";
	}

}
