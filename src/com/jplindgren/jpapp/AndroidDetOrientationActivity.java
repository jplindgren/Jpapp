package com.jplindgren.jpapp;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.widget.Toast;

import com.jplindgren.jpapp.model.Compass;

public class AndroidDetOrientationActivity extends Activity  implements SensorEventListener{
	private static SensorManager mySensorManager;
	private boolean sersorrunning;
	private Compass myCompassView;

	 /** Called when the activity is first created. */
	 @Override
	 public void onCreate(Bundle savedInstanceState) {
	     super.onCreate(savedInstanceState);
	     setContentView(R.layout.activity_android_det_orientation);
	  
	     myCompassView = (Compass)findViewById(R.id.mycompassview);
	  
	     mySensorManager = (SensorManager)getSystemService(Context.SENSOR_SERVICE);
	     List<Sensor> mySensors = mySensorManager.getSensorList(Sensor.TYPE_ORIENTATION);
	  
	     if(mySensors.size() > 0){
		      mySensorManager.registerListener(mySensorEventListener, mySensors.get(0), SensorManager.SENSOR_DELAY_NORMAL);
		      sersorrunning = true;
		      Toast.makeText(this, "Start ORIENTATION Sensor", Toast.LENGTH_LONG).show();	    
	     }else{
		      Toast.makeText(this, "No ORIENTATION Sensor", Toast.LENGTH_LONG).show();
		      sersorrunning = false;
		      finish();
	     }
	 }

	 private SensorEventListener mySensorEventListener = new SensorEventListener(){
		@Override
		public void onAccuracyChanged(Sensor sensor, int accuracy) {
		 // TODO Auto-generated method stub
	
		}
	
		@Override
		public void onSensorChanged(SensorEvent event) {
			myCompassView.updateDirection((float)event.values[0]);
		 
		}
	 };

	@Override
	protected void onDestroy() {
	super.onDestroy();
		if(sersorrunning){
			mySensorManager.unregisterListener(mySensorEventListener);
		}
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onSensorChanged(SensorEvent event) {
		myCompassView.updateDirection((float)event.values[0]);		
	}
}
