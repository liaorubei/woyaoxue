package com.newclass.woyaoxue.util;

import android.content.Context;
import android.graphics.Typeface;

public class TypeFaceUtil {

	private static Typeface font;

	public static Typeface get(Context context) {
		if (font == null) {
			font = Typeface.createFromAsset(context.getAssets(), "fonts/xiyuan.ttf");
		}
		return font;
	}
}
