package com.newclass.woyaoxue.bean;

public class Answer
{
	public String desc;
	public Integer code;
	public Info info;

	public class Info
	{
		public String accid;
		public String token;
		public String name;
		public String username;
		public String password;
		@Override
		public String toString()
		{
			return "Info [accid=" + accid + ", token=" + token + ", name=" + name + ", username=" + username + ", password=" + password + "]";
		}		
	}

	@Override
	public String toString()
	{
		return "Answer [desc=" + desc + ", code=" + code + ", info=" + info + "]";
	}
	
}
