package com.jplindgren.jpapp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Locale;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.jplindgren.jpapp.broadcast.ProximityIntentReceiver;
import com.jplindgren.jpapp.location.LocationClient;
import com.jplindgren.jpapp.location.MyLocationUpdateReceiver;
import com.jplindgren.jpapp.model.Oferta;
import com.jplindgren.jpapp.model.OfertaFactory;

public class ShowOfertaActivity extends Activity {

	public ProgressDialog loadingDialog;
	//LocationListener myLocationListener;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_show_oferta);
		// Show the Up button in the action bar.
		setupActionBar();
		
		IntentFilter filter = new IntentFilter(TREASURE_PROXIMITY_ALERT);
		registerReceiver(new ProximityIntentReceiver(), filter);
		
		popularOferta();
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
		LocationManager locationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
		LocationClient locationClient = new LocationClient(locationManager);		
		
		
		Location location = locationClient.getLastKnowLocation();
		
		//REFATORAR: Colocar LocationClient como observer da View. Ao atualizar o m�todo o proprio locationclient dispara o m�todo
		updateCurrentLocationTextView(location);
		requestLocationUpdatesUsingLocationListener(locationClient);
	}
	
	@Override
	protected void onPause() {
		LocationManager locationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
		LocationClient locationClient = new LocationClient(locationManager);	
		unregisterLocationUpdatesUsingLocationListernet(locationClient);
		super.onPause();
	}

	@Override
	protected void onResume() {
		LocationManager locationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
		LocationClient locationClient = new LocationClient(locationManager);	
		requestLocationUpdatesUsingLocationListener(locationClient);
		super.onResume();
	}
	
	private void unregisterLocationUpdatesUsingLocationListernet(LocationClient locationClient){
		locationClient.getLocationManager().removeUpdates(myLocationListener);
	}
	
	private LocationListener myLocationListener = new LocationListener() {
		public void onLocationChanged(Location location) {
			updateCurrentLocationTextView(location);
		}
		public void onProviderDisabled(String provider) {
		}
		public void onProviderEnabled(String provider) {
			//registerListener();
		}
		public void onStatusChanged(String provider, int status, Bundle extras) {}
	};

	private void requestLocationUpdatesUsingLocationListener(LocationClient locationClient){
		//REFATORAR: Colocar LocationClient como observer da View. Ao atualizar o m�todo o proprio locationclient dispara o m�todo	
		int tempoMinimoEntreAtualizacao = 5000; // milliseconds
		int distanciaMinima = 5; // meters
		
		myLocationListener = new LocationListener() {
			public void onLocationChanged(Location location) {
				updateCurrentLocationTextView(location);
			}
			public void onProviderDisabled(String provider){
				// Update application if provider disabled.
			}
			public void onProviderEnabled(String provider){
				// Update application if provider enabled.
			}
			
			public void onStatusChanged(String provider, int status, Bundle extras){
				// Update application if provider hardware status changed.
			}
		};
		//locationClient.getLocationManager().requestLocationUpdates(locationClient.getBestProvider(), tempoMinimoEntreAtualizacao, distanciaMinima, myLocationListener);
		locationClient.getLocationManager().requestLocationUpdates(locationClient.getBestProvider(), 0, 0, myLocationListener);
		
		//locationManager.removeUpdates(myLocationListener); //<-- se quiser remover � so usar
	}
	
	private void requestLocationUpdatesUsingPendingIntent(LocationManager locationManager){
		String provider = LocationManager.GPS_PROVIDER;
		int tempoMinimoEntreAtualizacao = 5000; // milliseconds
		int distanciaMinima = 5; // meters
		final int locationUpdateRC = 0;
		
		int flags = PendingIntent.FLAG_UPDATE_CURRENT;
		Intent intent = new Intent(this, MyLocationUpdateReceiver.class); // <-- MyLocationUpdateReceiver � um broadcast
		PendingIntent pendingIntent = PendingIntent.getBroadcast(this,
		locationUpdateRC, intent, flags);
		locationManager.requestLocationUpdates(provider, tempoMinimoEntreAtualizacao,distanciaMinima, pendingIntent);
		
		//locationManager.removeUpdates(pendingIntent); //<-- se quiser remover � so usar
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
		
		myLocationText.setText("Voc� est� em " + latLongString + "\n" + getCurrentAddress(location));
	}
	
	private String getCurrentAddress(Location location){
		String addressString = "Endere�o desconhecido";
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
	
	
	private static final String TREASURE_PROXIMITY_ALERT = "com.paad.treasurealert";
	private void setProximityAlert() {
		LocationManager locationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
		LocationClient locationClient = new LocationClient(locationManager);
		double lat = -22.9065641;
		double lng = -43.0867279;
		float radius = 100f; // meters
		long expiration = -1; // do not expire
		Intent intent = new Intent(TREASURE_PROXIMITY_ALERT);
		PendingIntent proximityIntent = PendingIntent.getBroadcast(this, -1, intent,0);
		locationClient.getLocationManager().addProximityAlert(lat, lng, radius,expiration,proximityIntent);
	}
	
	private void popularOferta(){
		Intent intent = getIntent();
		int idOferta = intent.getIntExtra(MainActivity.ID_OFERTA_SELECIONADA, 0);
		new GetAsyncDataTask(this).execute("http://restapi-2.apphb.com/oferta/getoferta/" + idOferta);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.show_oferta, menu);
		return true;
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
	
	private class GetAsyncDataTask extends AsyncTask<String, Void, Oferta> {		
		Context context;
		  
		GetAsyncDataTask(Context context) {
		    this.context = context;
		}
		
	   @Override
	   protected Oferta doInBackground(String... urls) {		         			   
	       try {
	    	   Oferta oferta = downloadOferta(urls[0]);
	    	   return oferta;
	       } catch (IOException e) {
	    	    e.printStackTrace();
    	   		return null;
	       }
	       
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
		   
		   TextView tvNomeProduto = (TextView)findViewById(R.id.tvNomeProduto);
		   tvNomeProduto.setText(oferta.getNomeProduto());
	   }
	   
	   private Oferta downloadOferta(String myurl) throws IOException {
		   	Oferta oferta = null;
			InputStream is = null;
			
	   	    try {
	   	        URL url = new URL(myurl);
	   	        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
	   	        conn.setReadTimeout(10000 /* milliseconds */);
	   	        conn.setConnectTimeout(15000 /* milliseconds */);
	   	        conn.setRequestMethod("GET");
	   	        conn.setDoInput(true);
	   	        // Starts the query
	   	        conn.connect();
	   	        BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream(),"utf-8"));
	
	   	        String line;
	   	        StringBuilder sb = new StringBuilder();
			    while ((line = in.readLine()) != null) {
			    	sb.append(line);						
				}
			    
				JSONObject jsonOferta = new JSONObject(sb.toString());
				oferta = OfertaFactory.Criar(jsonOferta);
			    
	   	    }catch (MalformedURLException e) {
	            e.printStackTrace();
	        } catch (IOException e) {
	            e.printStackTrace();
	        } catch (JSONException e) {
	            e.printStackTrace();
	        } finally {
	   	        if (is != null) {
	   	            is.close();
	   	        } 
	   	    }
	   	    return oferta;
	   	}		   	
	} //inner class
 

} 
