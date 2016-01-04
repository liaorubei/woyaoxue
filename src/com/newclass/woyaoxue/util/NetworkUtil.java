package com.newclass.woyaoxue.util;

public class NetworkUtil
{
	// public static String domain = "http://192.168.3.121:801";

	public static String domain = "http://voc2015.azurewebsites.net";
	public static final String chooseTeacher = domain + "/api/nimUser/ChooseTeacher";
	public static final String groupAccede = domain + "/api/Group/Create";
	public static final String groupCreate = domain + "/api/Group/Create";
	public static final String groupModify = domain + "/api/Group/Create";
	public static final String groupRemove = domain + "/api/Group/Remove";
	public static final String groupSelect = domain + "/api/Group/Select";

	public static final String ObtainTeacher = domain + "/api/nimUser/ObtainTeacher";
	public static final String studentCall = domain + "/api/student/call";
	public static final String teacherEnqueue = domain + "/api/NimUser/Enqueue";
	public static final String teacherRefresh = domain + "/api/NimUser/Refresh";
	public static final String teacherGroup = domain + "/api/teacher/group";
	public static final String teacherInQueue = domain + "/api/NimUser/TeacherInqueue";
	public static final String userCaptcha = domain + "/api/NimUser/Captcha";
	public static final String userCreate = domain + "/api/NimUser/create";
	public static final String userLogout = domain + "/api/NimUser/logout";
	public static final String userSelect = domain + "/api/NimUser/Select";
	public static final String userSignIn = domain + "/api/NimUser/Signin";
	public static final String userVerify = domain + "/api/NimUser/Verify";
	public static final String ThemeSelect = domain + "/api/Theme/Select";

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
