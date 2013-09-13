package com.jplindgren.jpapp.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.util.Log;

import com.jplindgren.jpapp.httpconnection.HttpNetworkConnection;

public class NetworkProviderStatusReceiver extends BroadcastReceiver{
	final String logTag = "Monitor Location";
	
	@Override
	public void onReceive(Context context, Intent intent) {
		String action = intent.getAction();
		Bundle extras = intent.getExtras();
		
		Log.d(logTag, "Monitor Location Broadcast received: " + action);
		
		if (action.equalsIgnoreCase(Intent.ACTION_AIRPLANE_MODE_CHANGED)){
			boolean state = extras.getBoolean("state");
			Log.d(logTag, String.format("Monitor location Airplano mode changed to {1}", state ? "On" : "Off"));
		}else if(action.equalsIgnoreCase(ConnectivityManager.CONNECTIVITY_ACTION)){
			ConnectivityManager connManager =(ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
			HttpNetworkConnection conn = new HttpNetworkConnection(connManager);
			Log.d(logTag, String.format("Monitor Location Wifi radio available: ", 
					conn.CheckWifiConnection() ? "Sim" : "Não"));
		}
	}
	
	public void start(Context context){
		IntentFilter filter = new IntentFilter();
		filter.addAction(Intent.ACTION_AIRPLANE_MODE_CHANGED);
		filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
		
		context.registerReceiver(this, filter);		
	}
	
	public void stop(Context context){		
		context.unregisterReceiver(this);		
	}

} // class
