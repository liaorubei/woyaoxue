package com.newclass.woyaoxue;

import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.Observer;
import com.netease.nimlib.sdk.SDKOptions;
import com.netease.nimlib.sdk.StatusBarNotificationConfig;
import com.netease.nimlib.sdk.auth.LoginInfo;
import com.netease.nimlib.sdk.avchat.AVChatManager;
import com.netease.nimlib.sdk.avchat.model.AVChatData;
import com.netease.nimlib.sdk.avchat.model.AVChatRingerConfig;
import com.netease.nimlib.sdk.msg.MsgServiceObserve;
import com.netease.nimlib.sdk.msg.constant.SessionTypeEnum;
import com.netease.nimlib.sdk.msg.model.CustomNotification;
import com.netease.nimlib.sdk.uinfo.UserInfoProvider;
import com.newclass.woyaoxue.activity.MessageActivity;
import com.newclass.woyaoxue.util.ConstantsUtil;
import com.newclass.woyaoxue.util.Log;
import com.voc.woyaoxue.R;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Environment;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.WindowManager;

public class MyApplication extends Application
{
	private static Context mContext = null;

	@Override
	public void onCreate()
	{
		super.onCreate();
		mContext = this;
		SDKOptions options = getOptions();
		LoginInfo loginInfo = getLoginInfo();
		NIMClient.init(this, loginInfo, options);
		// 监听音频视频实时交流来电
		// enableAVChat();

		// 注册NIMClinet关的观察者
		//registerNimClientObserver();
	}

	private void registerNimClientObserver()
	{
		// 如果有自定义通知是作用于全局的，不依赖某个特定的 Activity，那么这段代码应该在 Application 的 onCreate 中就调用
		// 自定义通知。
		// 区别于IMMessage，SDK仅透传该类型消息，不负责解析和存储。消息内容由第三方APP自由扩展。
		// 发送给个人的自定义通知消息可选择要不要发送给当前不在线的用户。
		// 发送给群的自定义通知消息则只有当前在线的群成员才能收到。
		NIMClient.getService(MsgServiceObserve.class).observeCustomNotification(new Observer<CustomNotification>()
		{
			private static final long serialVersionUID = 1L;

			@Override
			public void onEvent(CustomNotification message)
			{
				// 在这里处理自定义通知。

			}
		}, true);
	}

	private void enableAVChat()
	{
		setupAVChat();
		registerAVChatIncomingCallObserver(true);
	}

	private void setupAVChat()
	{
		AVChatRingerConfig config = new AVChatRingerConfig();
		config.res_connecting = R.raw.avchat_connecting;
		config.res_no_response = R.raw.avchat_no_response;
		config.res_peer_busy = R.raw.avchat_peer_busy;
		config.res_peer_reject = R.raw.avchat_peer_reject;
		config.res_ring = R.raw.avchat_ring;
		AVChatManager.getInstance().setRingerConfig(config); // 铃声配置
	}

	private void registerAVChatIncomingCallObserver(boolean register)
	{
		AVChatManager.getInstance().observeIncomingCall(new Observer<AVChatData>()
		{
			@Override
			public void onEvent(AVChatData chatData)
			{
				Log.i("logi", "实时音频视频情况:" + chatData);
			}
		}, register);
	}

	private SDKOptions getOptions()
	{
		SDKOptions options = new SDKOptions();
		options.appKey = ConstantsUtil.NimAppKey;

		// 如果将新消息通知提醒托管给 SDK 完成，需要添加以下配置。否则无需设置。
		StatusBarNotificationConfig config = new StatusBarNotificationConfig();
		config.notificationEntrance = MessageActivity.class; // 点击通知栏跳转到该Activity
		config.notificationSmallIconId = R.drawable.ic_launcher;
		options.statusBarNotificationConfig = config;

		// 配置保存图片，文件，log 等数据的目录
		// 如果 options 中没有设置这个值，SDK 会使用下面代码示例中的位置作为 SDK 的数据目录。
		// 该目录目前包含 log, file, image, audio, video, thumb 这6个目录。
		// 如果第三方 APP 需要缓存清理功能， 清理这个目录下面个子目录的内容即可。
		String sdkPath = Environment.getExternalStorageDirectory() + "/" + getPackageName() + "/nim";
		options.sdkStorageRootPath = sdkPath;

		// 配置是否需要预下载附件缩略图，默认为 true
		options.preloadAttach = true;

		// 配置附件缩略图的尺寸大小，该值一般应根据屏幕尺寸来确定， 默认值为 Screen.width / 2
		WindowManager manager = (WindowManager) getSystemService(WINDOW_SERVICE);
		DisplayMetrics outMetrics = new DisplayMetrics();
		manager.getDefaultDisplay().getMetrics(outMetrics);
		options.thumbnailSize = outMetrics.widthPixels / 2;

		// 用户资料提供者, 目前主要用于提供用户资料，用于新消息通知栏中显示消息来源的头像和昵称
		options.userInfoProvider = new UserInfoProvider()
		{
			@Override
			public UserInfo getUserInfo(String account)
			{
				return null;
			}

			@Override
			public int getDefaultIconResId()
			{
				return R.drawable.ic_launcher;
			}

			@Override
			public Bitmap getTeamIcon(String tid)
			{
				return null;
			}

			@Override
			public Bitmap getAvatarForMessageNotifier(String account)
			{
				return null;
			}

			@Override
			public String getDisplayNameForMessageNotifier(String account, String sessionId, SessionTypeEnum sessionType)
			{
				return null;
			}
		};
		return options;
	}

	private LoginInfo getLoginInfo()
	{
		SharedPreferences sp = getSharedPreferences("user", MODE_PRIVATE);
		String accid = sp.getString("accid", "");
		String token = sp.getString("token", "");
		if (TextUtils.isEmpty(accid) || TextUtils.isEmpty(token))
		{
			return null;
		}
		else
		{
			return null;// new LoginInfo(accid, token);
		}
	}

	public static Context getContext()
	{
		return mContext;
	}

	public static void setContext(Context context)
	{
		mContext = context;
	}

}
