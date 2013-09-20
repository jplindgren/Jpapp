package com.jplindgren.jpapp;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.GeomagneticField;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.HandlerThread;
import android.os.Looper;
import android.provider.Settings;
import android.support.v4.app.NavUtils;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Surface;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.jplindgren.jpapp.broadcast.NetworkProviderStatusReceiver;
import com.jplindgren.jpapp.broadcast.ProximityIntentReceiver;
import com.jplindgren.jpapp.httpconnection.HttpNetworkConnection;
import com.jplindgren.jpapp.location.LocationClient;
import com.jplindgren.jpapp.location.MyLocationListener;
import com.jplindgren.jpapp.model.Oferta;
import com.jplindgren.jpapp.servico.OfertaServico;
import com.jplindgren.jpapp.servico.message.RequestGetOferta;
import com.jplindgren.jpapp.util.AlertUserDialog;
import com.jplindgren.jpapp.view.CompassView;

public class ShowOfertaActivity extends Activity implements LocationSubscriber {
	Looper looper;
	NetworkProviderStatusReceiver _statusReceiver;
	LocationClient locationClient;
	HttpNetworkConnection httpNetworkConnection;
	
	LocationSubject myLocationListener;
	ProgressDialog loadingDialog;
	
	GeomagneticField geoField;
	
	private float[] aValues = new float[3];
	private float[] mValues = new float[3];
	private CompassView compassView;
	private SensorManager sensorManager;
	private int rotation;
	
	private final SensorEventListener sensorEventListener = new SensorEventListener() {
		public void onSensorChanged(SensorEvent event) {
			if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER)
				aValues = event.values;
			if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD)
				mValues = event.values;
			updateOrientation(calculateOrientation());
		}
		public void onAccuracyChanged(Sensor sensor, int accuracy) {}
	};
	
	private void initCompass(){
		compassView = (CompassView)findViewById(R.id.compassView);
		sensorManager = (SensorManager)getSystemService(Context.SENSOR_SERVICE);
		String windoSrvc = Context.WINDOW_SERVICE;
		WindowManager wm = ((WindowManager) getSystemService(windoSrvc));
		Display display = wm.getDefaultDisplay();
		rotation = display.getRotation();
		updateOrientation(new float[] {0, 0, 0});
	}
	
	private void resumeCompass(){		
		Sensor accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		Sensor magField = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
		sensorManager.registerListener(sensorEventListener, accelerometer, SensorManager.SENSOR_DELAY_FASTEST);
		sensorManager.registerListener(sensorEventListener, magField,SensorManager.SENSOR_DELAY_FASTEST);
	}
	
	private void pauseCompass(){
		sensorManager.unregisterListener(sensorEventListener);
	}
	
	private void updateOrientation(float[] values) {
		if (compassView != null) {
			compassView.setBearing(values[0]);
			//compassView.setPitch(values[1]);
			compassView.setRoll(-values[2]);
			compassView.invalidate();
		}
	}
	
	private float[] calculateOrientation() {
		float[] values = new float[3];
		float[] inR = new float[9];
		float[] outR = new float[9];
		// Determine the rotation matrix
		SensorManager.getRotationMatrix(inR, null, aValues, mValues);
		// Remap the coordinates based on the natural device orientation.
		int x_axis = SensorManager.AXIS_X;
		int y_axis = SensorManager.AXIS_Y;
		switch (rotation) {
			case (Surface.ROTATION_90):
				x_axis = SensorManager.AXIS_Y;
				y_axis = SensorManager.AXIS_MINUS_X;
				break;
			case (Surface.ROTATION_180):
				y_axis = SensorManager.AXIS_MINUS_Y;
				break;
			case (Surface.ROTATION_270):
				x_axis = SensorManager.AXIS_MINUS_Y;
				y_axis = SensorManager.AXIS_X;
				break;
			default: break;
		}
		SensorManager.remapCoordinateSystem(inR, x_axis, y_axis, outR);
		// Obtain the current, corrected orientation.
		SensorManager.getOrientation(outR, values);
		// Convert from Radians to Degrees.
		values[0] = (float) Math.toDegrees(values[0]);
		values[1] = (float) Math.toDegrees(values[1]);
		values[2] = (float) Math.toDegrees(values[2]);
		return values;
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_show_oferta);
		// Show the Up button in the action bar.
		setupActionBar();
		
		IntentFilter filter = new IntentFilter(TREASURE_PROXIMITY_ALERT);
		registerReceiver(new ProximityIntentReceiver(), filter);
		setProximityAlert();
		
		popularOferta();
		
		initCompass();
	}
	
	@Override
	public void locationChanged(Location location) {
		final Location theLocation = location; // <--- tipo um closure
		//vamos executar na thread correta da UI
		this.runOnUiThread(new Runnable() {			
			@Override
			public void run() {
				updateCurrentLocationTextView(theLocation);
				
				geoField = new GeomagneticField(
				         Double.valueOf(theLocation.getLatitude()).floatValue(),
				         Double.valueOf(theLocation.getLongitude()).floatValue(),
				         Double.valueOf(theLocation.getAltitude()).floatValue(),
				         System.currentTimeMillis()
				      );
				
				compassView.setHeading(geoField.getDeclination());
			}
		});		
	}

	/**
	 * Set up the {@link android.app.ActionBar}.
	 */
	private void setupActionBar() {
		getActionBar().setDisplayHomeAsUpEnabled(true);
	}
	
	public void showMap(View view){
		Intent intent = new Intent(this, TestActivity.class);
		startActivity(intent);
	}
	
	public void goActivity(View view){
		Intent intent = new Intent(this, MapNavigationActivity.class);
		startActivity(intent);
	}
	
	public void followOferta(View view){		
		Intent intent = new Intent(this, CompassAcivityNew.class);
		startActivity(intent);
	}
	
	public LocationClient getLocationClient(){
		if (locationClient == null){
			LocationManager locationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
			locationClient = new LocationClient(locationManager);			
		}
		return locationClient;		
	}
	
	private HttpNetworkConnection getHttpNetworkConnection(){
		if (httpNetworkConnection == null){
			ConnectivityManager connMgr = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
			httpNetworkConnection = new HttpNetworkConnection(connMgr);
		}
		return httpNetworkConnection;
	}
	
	
	@Override
	protected void onPause() {
		cleanResources();
		super.onPause();
		
		pauseCompass();
	}

	@Override
	protected void onResume() {
		requestLocationUpdatesUsingLocationListener(getLocationClient());
		super.onResume();
		
		resumeCompass();
	}	
	
	@Override
	protected void onDestroy() {
		cleanResources();		
		super.onDestroy();
	}
	
	private void cleanResources(){
		unregisterLocationUpdatesUsingLocationListerner(getLocationClient());
		
		if (looper != null){
			looper.quit();
			looper = null;
		}
	}

	private void unregisterLocationUpdatesUsingLocationListerner(LocationClient locationClient){		
		if (myLocationListener != null){
			myLocationListener.unRegisterSubscriber(this);
			getLocationClient().stopListening(myLocationListener);
			myLocationListener = null;
		}
		
		if (_statusReceiver != null){
			_statusReceiver.stop(this);
			_statusReceiver = null;
		}
	}

	private void requestLocationUpdatesUsingLocationListener(LocationClient locationClient){
		HandlerThread locationThread = new HandlerThread("locationThread");
		locationThread.start();
		looper = locationThread.getLooper();
		
		//if (confirmNetworkProviderAvailable(locationClient)){
			_statusReceiver = new NetworkProviderStatusReceiver();
			_statusReceiver.start(this);
			
			myLocationListener = new MyLocationListener();
			myLocationListener.registerSubscriber(this);
			
			getLocationClient().startListening(myLocationListener, looper);
		//}
	}
	
	private void updateCurrentLocationTextView(Location location){
		TextView myLocationText;
		myLocationText = (TextView)findViewById(R.id.myLocationText);
		String latLongString = "No location found";
		if (location != null) {
			double lat = location.getLatitude();
			double lng = location.getLongitude();
			latLongString = "Lat:" + lat + "\nLong:" + lng;
		}		
		
		myLocationText.setText("Você está em " + latLongString + "\n" + getCurrentAddress(location));
	}
	
	private String getCurrentAddress(Location location){
		String addressString = "Endereço desconhecido";
		Geocoder gc = new Geocoder(this, Locale.getDefault());
		try {
			List<Address> addresses = gc.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
			StringBuilder sb = new StringBuilder();
			if (addresses.size() > 0) {
				Address address = addresses.get(0);
				for (int i = 0; i < address.getMaxAddressLineIndex(); i++)
					sb.append(address.getAddressLine(i)).append("\n");
				
				sb.append(address.getLocality()).append("\n");
				sb.append(address.getPostalCode()).append("\n");
				sb.append(address.getCountryName());
			}
			addressString = sb.toString();
		} catch (IOException e) {}
		return addressString;
	}
	
	
	private static final String TREASURE_PROXIMITY_ALERT = "com.jplindgren.jpapp.treasurealert";
	private void setProximityAlert() {
		double lat = -22.9065641;
		double lng = -43.0867279;
		float radius = 100f; // meters
		long expiration = -1; // do not expire
		Intent intent = new Intent(TREASURE_PROXIMITY_ALERT);
		PendingIntent proximityIntent = PendingIntent.getBroadcast(this, -1, intent,0);
		getLocationClient().getLocationManager().addProximityAlert(lat, lng, radius,expiration,proximityIntent);
	}
	
	private void popularOferta(){
		Intent intent = getIntent();
		int idOferta = intent.getIntExtra(MainActivity.ID_OFERTA_SELECIONADA, 0);
		new GetAsyncDataTask(this).execute(new RequestGetOferta(idOferta));
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.show_oferta, menu);
		return true;
	}
	
	private boolean confirmNetworkProviderAvailable(LocationClient locationClient){
		return locationClient.confirmProviderEnabled() &&
				confirmAirPlaineModeIsOff() &&
				getHttpNetworkConnection().CheckWifiConnection();				
	}
	
	@SuppressLint("NewApi")
	private boolean confirmAirPlaineModeIsOff(){
		boolean isOff = Settings.System.getInt(getContentResolver(), Settings.System.AIRPLANE_MODE_ON, 0) == 0;
		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR1) {
			isOff =  Settings.System.getInt(getContentResolver(), Settings.System.AIRPLANE_MODE_ON, 0) == 0;          
	    } else {
	    	isOff = Settings.Global.getInt(getContentResolver(), Settings.Global.AIRPLANE_MODE_ON, 0) == 0;
	    }  		
		if (!isOff){
			AlertUserDialog alert = new AlertUserDialog("Por favor desabilite o modo avião", "");
			alert.show(getFragmentManager(), null);
		}
		return isOff;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			NavUtils.navigateUpFromSameTask(this);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	private class GetAsyncDataTask extends AsyncTask<Object, Void, Oferta> {		
		Context context;
		  
		GetAsyncDataTask(Context context) {
		    this.context = context;
		}
		
	   @Override
	   protected Oferta doInBackground(Object...requests) {
		   RequestGetOferta request = (RequestGetOferta) requests[0];
		   OfertaServico ofertaServico = new OfertaServico();
	       Oferta oferta = null;
	       try {
				oferta = ofertaServico.getOferta(request.getIdOferta());
	       } catch (NumberFormatException e) {
				e.printStackTrace();
	       } catch (IOException e) {
				e.printStackTrace();
	       }
		   return oferta;	       
	   }
	   
	   @Override
	    protected void onPreExecute() {
	        super.onPreExecute();

	        loadingDialog = new ProgressDialog(context);
	        loadingDialog.setMessage("Carregando...");
	        loadingDialog.show();
	    }
	   
	   // onPostExecute displays the results of the AsyncTask.
	   @Override
	   protected void onPostExecute(Oferta oferta) {
		   loadingDialog.dismiss();
		   
		   if (oferta != null){
			   TextView tvNomeProduto = (TextView)findViewById(R.id.tvNomeProduto);
			   tvNomeProduto.setText(oferta.getNomeProduto());
		   }else{
			   AlertUserDialog alert = new AlertUserDialog("Ops! Não encontramos essa oferta! Você está conexão a internet?", "");
			   alert.show(getFragmentManager(), null);
		   }
	   }
	   
	 } //inner class
 
	
	/*
	 private void requestLocationUpdatesUsingPendingIntent(LocationManager locationManager){
		String provider = LocationManager.GPS_PROVIDER;
		int tempoMinimoEntreAtualizacao = 5000; // milliseconds
		int distanciaMinima = 5; // meters
		final int locationUpdateRC = 0;
		
		int flags = PendingIntent.FLAG_UPDATE_CURRENT;
		Intent intent = new Intent(this, MyLocationUpdateReceiver.class); // <-- MyLocationUpdateReceiver é um broadcast
		PendingIntent pendingIntent = PendingIntent.getBroadcast(this,
		locationUpdateRC, intent, flags);
		locationManager.requestLocationUpdates(provider, tempoMinimoEntreAtualizacao,distanciaMinima, pendingIntent);
		
		//locationManager.removeUpdates(pendingIntent); //<-- se quiser remover é so usar
	}
	 */

} 
