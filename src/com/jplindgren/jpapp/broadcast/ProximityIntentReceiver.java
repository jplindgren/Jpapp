package com.jplindgren.jpapp.broadcast;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.support.v4.app.NotificationCompat;

import com.jplindgren.jpapp.ShowOfertaActivity;

public class ProximityIntentReceiver extends BroadcastReceiver{
	@Override
	public void onReceive (Context context, Intent intent) {
		String key = LocationManager.KEY_PROXIMITY_ENTERING;
		Boolean entering = intent.getBooleanExtra(key, false);
		
		showNotification(context);
		// TODO [ … perform proximity alert actions … ]
	}
	
	private void showNotification(Context context) {
	    PendingIntent contentIntent = PendingIntent.getActivity(context, 0,
	            new Intent(context, ShowOfertaActivity.class), 0);

	    NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context)
	            .setContentTitle("Promoção a vista!")
	            .setContentText("Você chegou ao seu destino!")
				.setContentIntent(contentIntent)
				.setDefaults(Notification.DEFAULT_SOUND)
				.setAutoCancel(true);
	    NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
	    mNotificationManager.notify(1, mBuilder.build());

	}  
	
	
}
