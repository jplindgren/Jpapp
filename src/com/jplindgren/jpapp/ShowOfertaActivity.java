package com.jplindgren.jpapp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
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

import com.jplindgren.jpapp.location.LocationClient;
import com.jplindgren.jpapp.location.MyLocationUpdateReceiver;
import com.jplindgren.jpapp.model.Oferta;
import com.jplindgren.jpapp.model.OfertaFactory;

public class ShowOfertaActivity extends Activity {

	public ProgressDialog loadingDialog;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_show_oferta);
		// Show the Up button in the action bar.
		setupActionBar();
		
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
		
		//REFATORAR: Colocar LocationClient como observer da View. Ao atualizar o método o proprio locationclient dispara o método
		updateCurrentLocationTextView(location);
		requestLocationUpdatesUsingLocationListener(locationClient);
	}
	
	private void requestLocationUpdatesUsingLocationListener(LocationClient locationClient){
		//REFATORAR: Colocar LocationClient como observer da View. Ao atualizar o método o proprio locationclient dispara o método	
		int tempoMinimoEntreAtualizacao = 5000; // milliseconds
		int distanciaMinima = 5; // meters
		
		LocationListener myLocationListener = new LocationListener() {
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
		
		//locationManager.removeUpdates(myLocationListener); //<-- se quiser remover é so usar
	}
	
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
	
	private void updateCurrentLocationTextView(Location location){
		TextView myLocationText;
		myLocationText = (TextView)findViewById(R.id.myLocationText);
		String latLongString = "No location found";
		if (location != null) {
			double lat = location.getLatitude();
			double lng = location.getLongitude();
			latLongString = "Lat:" + lat + "\nLong:" + lng;
		}
		myLocationText.setText("Você está em " + latLongString);
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
