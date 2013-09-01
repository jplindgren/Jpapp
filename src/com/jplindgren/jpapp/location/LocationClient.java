package com.jplindgren.jpapp.location;

import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;

public class LocationClient {
	LocationManager locationManager = null;
	
	public LocationManager getLocationManager(){
		return locationManager;
	}
	
	public LocationClient(LocationManager locationManager){
		this.locationManager = locationManager;
	}
	
	
	private Criteria getLocationProviderCriteria(){
		/*Criteria criteria = new Criteria();
		criteria.setAccuracy(Criteria.ACCURACY_FINE);
		criteria.setPowerRequirement(Criteria.POWER_LOW);
		criteria.setHorizontalAccuracy(Criteria.ACCURACY_HIGH);
		criteria.setVerticalAccuracy(Criteria.ACCURACY_HIGH);*/
		
		Criteria criteria = new Criteria();
		criteria.setAccuracy(Criteria.ACCURACY_FINE);
		criteria.setPowerRequirement(Criteria.POWER_LOW);
		criteria.setAltitudeRequired(false);
		criteria.setBearingRequired(false);
		criteria.setSpeedRequired(false);
		criteria.setCostAllowed(true);
		return criteria;
	}
	
	public String getBestProvider(){	
		String bestProvider = locationManager.getBestProvider(getLocationProviderCriteria(), true);
		return bestProvider;
		//LocationProvider locationProvider = locationManager.getProvider(bestProvider);
		//return locationProvider;
	} 
	
	public String getGpsProvider(){	
		return LocationManager.GPS_PROVIDER;		
	} 	
	
	public Location getLastKnowLocation(){
		Location location = locationManager.getLastKnownLocation(getBestProvider());
		return location;
	}
}
