package com.jplindgren.jpapp;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;

public class CompassActivityQuartaTentativaImage extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_compass_activity_quarta_tentativa_image);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(
				R.menu.compass_activity_quarta_tentativa_image, menu);
		return true;
	}
	
	// VER:
	//http://stackoverflow.com/questions/8680268/pointing-to-a-specific-location-compass-using-an-image-instead-of-drwaing
	//http://stackoverflow.com/questions/4308262/calculate-compass-bearing-heading-to-location-in-android/4316717#4316717
	//http://www.vogella.com/code/com.vogella.android.canvas.compass/src/com/paad/compass/CompassSurfaceView.html
	

}
