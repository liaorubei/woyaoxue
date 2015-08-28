package com.newclass.woyaoxue.util;

public class NetworkUtil {
	// public static String domain = "http://192.168.3.121:801";
	public static String domain = "http://voc2015.cloudapp.net";

	public static String getDocs() {
		return domain + "";
	}

	public static String getLevels() {
		return domain + "/NewClass/levels";
	}

	public static String getDocsByLevelId(int levelId) {
		return domain + "/NewClass/DocsByLevel?levelid=" + levelId;
	}

	public static String getDocById(int id) {

		return domain + "/NewClass/DocById/" + id;
	}

}
