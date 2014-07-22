package com.mike.messagereceive;

import com.mike.virtuallocallife.R;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import cn.bmob.push.BmobPushMessageReceiver;

public class MyMessageReceiver extends BmobPushMessageReceiver {

	@Override
	public void onMessage(Context context, String message) {
		// 发送通知
		NotificationManager nm = (NotificationManager) context
				.getSystemService(Context.NOTIFICATION_SERVICE);

		Notification n = new Notification();
		n.icon = R.drawable.ic_launcher;
		n.tickerText = "收到消息推送";
		n.when = System.currentTimeMillis();
		Intent intent = new Intent();
		PendingIntent pi = PendingIntent.getActivity(context, 0, intent, 0);
		n.setLatestEventInfo(context, "消息", message, pi);
		n.defaults |= Notification.DEFAULT_SOUND;
		n.flags = Notification.FLAG_AUTO_CANCEL;
		nm.notify(1, n);
	}

}
