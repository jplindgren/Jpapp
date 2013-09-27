package com.jplindgren.jpapp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

import com.jplindgren.jpapp.model.Oferta;
import com.jplindgren.jpapp.model.OfertaFactory;
import com.jplindgren.jpapp.model.ParcelableOferta;

public class SplashScreen extends Activity{
	
	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(com.jplindgren.jpapp.R.layout.activity_splash); 
		
		new PrefetchData(this).execute("http://restapi-2.apphb.com/oferta/ofertasdodia");
	}
	
	/*
	 * Fazendo chamada http assincrona
	 */
	private class PrefetchData extends AsyncTask<String, Void, ArrayList<Oferta>>{
		Context context;
		  
		PrefetchData(Context context) {
	        this.context = context;
	    }
		
		@Override
		   protected ArrayList<Oferta> doInBackground(String... urls) {		         
		       // params comes from the execute() call: params[0] is the url.			   
		       try {
		    	   ArrayList<Oferta> listaOfertas = downloadUrl(urls[0]);
		    	   return listaOfertas;
		       } catch (IOException e) {
		    	    e.printStackTrace();
	    	   		return new ArrayList<Oferta>();
		           //return "Unable to retrieve web page. URL may be invalid.";
		       }		       
		   }		   
		   @Override
		    protected void onPreExecute() {
		        super.onPreExecute();
		    }
		   
		   // onPostExecute displays the results of the AsyncTask.
		   @Override
		   protected void onPostExecute(ArrayList<Oferta> listaOfertas) {
			   Intent intent = new Intent(context, MainActivity.class);
			   ArrayList<ParcelableOferta> ofertasExtra = new ArrayList<ParcelableOferta>();
			   if (listaOfertas != null){
				   for (Oferta oferta : listaOfertas){
					   ofertasExtra.add(new ParcelableOferta(oferta));
				   }
			   }
			   
			   intent.putExtra("preFetchedOfertas", ofertasExtra);
			   startActivity(intent);
		   }
		   
		   private ArrayList<Oferta> downloadUrl(String myurl) throws IOException {
			   ArrayList<Oferta> productList = new ArrayList<Oferta>();
				InputStream is = null;
				JSONArray jObj = null;
				
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
				    
				    try {
				    	jObj = new JSONArray(sb.toString());
			        } catch (JSONException e) {
			            Log.e("JSON Parser", "Error parsing data " + e.toString());
			        }
				    
				    for (int i = 0; i < jObj.length(); i++) {
						JSONObject jsonOferta = jObj.getJSONObject(i);
						Oferta oferta = OfertaFactory.Criar(jsonOferta);
						productList.add(oferta);
				    }
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
		   	    return productList;
		   	}
	}
}// class
