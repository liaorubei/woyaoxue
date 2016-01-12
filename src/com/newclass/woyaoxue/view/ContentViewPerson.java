package com.newclass.woyaoxue.view;

import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.StatusCode;
import com.newclass.woyaoxue.bean.User;
import com.voc.woyaoxue.R;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class ContentViewPerson extends ContentView implements OnClickListener
{

	private Button bt_histroy, bt_setting;
	private ImageView iv_avater, iv_gender;
	private TextView tv_nickname, tv_username;

	public ContentViewPerson(Context context)
	{
		super(context);
		initData();
	}

	public void initData()
	{
		showView(ViewState.SUCCESS);
		if (NIMClient.getStatus() != StatusCode.LOGINED)
		{

			tv_nickname.setText("");
			tv_username.setText("");
			iv_gender.setVisibility(View.GONE);
			return;
		}
		SharedPreferences sp = getContext().getSharedPreferences("user", Context.MODE_PRIVATE);
		User user = new User();
		user.Username = sp.getString("username", "");
		user.NickName = sp.getString("nickname", "");
		user.Gender = sp.getInt("gender", 0);

		tv_nickname.setText(user.NickName);
		tv_username.setText(user.Username);
		iv_gender.setImageResource(user.Gender == 0 ? R.drawable.gender_female : R.drawable.gender_male);

	}

	@Override
	public void onClick(View v)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public View onCreateSuccessView()
	{
		View inflate = View.inflate(getContext(), R.layout.contentview_person, null);
		tv_nickname = (TextView) inflate.findViewById(R.id.tv_nickname);
		tv_username = (TextView) inflate.findViewById(R.id.tv_username);
		iv_avater = (ImageView) inflate.findViewById(R.id.iv_avater);
		iv_gender = (ImageView) inflate.findViewById(R.id.iv_gender);

		bt_histroy = (Button) inflate.findViewById(R.id.bt_history);
		bt_setting = (Button) inflate.findViewById(R.id.bt_setting);

		bt_histroy.setOnClickListener(this);
		bt_setting.setOnClickListener(this);
		return inflate;
	}
}
