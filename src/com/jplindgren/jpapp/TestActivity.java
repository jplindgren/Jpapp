package com.jplindgren.jpapp;

import java.util.List;

import android.os.Bundle;
import android.view.Menu;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.MyLocationOverlay;
import com.google.android.maps.Overlay;

public class TestActivity extends MapActivity {
	MapView map;
	long start;
	long stop;
	MyLocationOverlay compass;
	MapController controller;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_test);
		
		map = (MapView) findViewById(R.id.mapview);
		map.setBuiltInZoomControls(true);
				
		List<Overlay> overlayList = map.getOverlays();
		compass = new MyLocationOverlay(this, map);
		overlayList.add(compass);
		
		controller = map.getController();
		GeoPoint point = new GeoPoint(-2294145, -4318146);
		controller.animateTo(point);
		controller.setZoom(6);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.test, menu);
		return true;
	}

	@Override
	protected boolean isRouteDisplayed() {
		// TODO Auto-generated method stub
		return true;
	}
	
	@Override
	protected void onPause(){
		compass.disableCompass();
		super.onPause();
	}

	@Override
	protected void onResume() {
		compass.enableCompass();
		super.onResume();
	}
	
	

}
