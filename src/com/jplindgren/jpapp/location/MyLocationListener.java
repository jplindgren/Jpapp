package com.jplindgren.jpapp.location;

import java.util.ArrayList;

import android.location.Location;
import android.os.Bundle;

import com.jplindgren.jpapp.LocationSubject;
import com.jplindgren.jpapp.LocationSubscriber;
import com.jplindgren.jpapp.ShowOfertaActivity;

public class MyLocationListener implements LocationSubject{
	private ArrayList<LocationSubscriber> subscribers;
	private ShowOfertaActivity ofertaAcShowOfertaActivity; // <-- apenas para lembrar que posso passar a acitivy	
	
	public MyLocationListener(){
		subscribers = new ArrayList<LocationSubscriber>();
	}
	
	@Override
	public void onLocationChanged(Location location) {
		for (LocationSubscriber subscriber : subscribers)
			subscriber.locationChanged(location);
	}

	@Override
	public void onProviderDisabled(String arg0) {
		
	}

	@Override
	public void onProviderEnabled(String arg0) {
		
	}

	@Override
	public void onStatusChanged(String arg0, int arg1, Bundle arg2) {
		
	}

	@Override
	public void registerSubscriber(LocationSubscriber subscriber) {
		subscribers.add(subscriber);		
	}

	@Override
	public void unRegisterSubscriber(LocationSubscriber subscriber) {
		subscribers.remove(subscriber);		
	}

}
