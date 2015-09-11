package com.newclass.woyaoxue.util;

import com.newclass.woyaoxue.bean.Document;

public abstract class UpdateListenerUtil
{
	private Document document;

	public UpdateListenerUtil(Document doc)
	{
		this.document = doc;
	}

	public Document getDocument()
	{
		return document;
	}

	public abstract void onLoading(long total, long current, boolean isUploading);

}
