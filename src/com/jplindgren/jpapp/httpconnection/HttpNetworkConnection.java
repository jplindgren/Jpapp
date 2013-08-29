package com.jplindgren.jpapp.httpconnection;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class HttpNetworkConnection {
	private ConnectivityManager connectivityService;
	public HttpNetworkConnection(ConnectivityManager connectivityService){
		this.connectivityService = connectivityService;
	}
	
	public boolean CheckConnection(){
		NetworkInfo networkInfo = this.connectivityService.getActiveNetworkInfo();
		
	    if (networkInfo != null && networkInfo.isConnected()) {
	        return true;
	    }else{
	    	return false;
	    }
	}
	
	public boolean CheckWifiConnection(){
		NetworkInfo networkInfo = connectivityService.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
		if (networkInfo != null && networkInfo.isConnected()) {
	        return true;
	    }
	    return false;
	}
} //class
