package com.jplindgren.jpapp;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

import com.jplindgren.jpapp.httpconnection.HttpNetworkConnection;
import com.jplindgren.jpapp.model.Oferta;
import com.jplindgren.jpapp.util.OfertaListAdapter;

@SuppressLint("NewApi")
public class MainActivity extends Activity  {    
	
	ListView listview;
  
    @Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		listview = (ListView) findViewById(R.id.listview);
		showList();
		
		/*
		 final ListView listview = (ListView) findViewById(R.id.listview);
		    String[] values = new String[] { "Android", "iPhone", "WindowsMobile",
		        "Blackberry", "WebOS", "Ubuntu", "Windows7", "Max OS X",
		        "Linux", "OS/2", "Ubuntu", "Windows7", "Max OS X", "Linux",
		        "OS/2", "Ubuntu", "Windows7", "Max OS X", "Linux", "OS/2",
		        "Android", "iPhone", "WindowsMobile" };
		
		    final ArrayList<String> list = new ArrayList<String>();
		    for (int i = 0; i < values.length; ++i) {
		      list.add(values[i]);
		    }
		    final StableArrayAdapter adapter = new StableArrayAdapter(this,android.R.layout.simple_list_item_1, list);
		    listview.setAdapter(adapter);
		    
		    listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
		    	 @Override
		         public void onItemClick(AdapterView<?> parent, final View view,int position, long id) {
		    		 final String item = (String) parent.getItemAtPosition(position);
		    		 view.animate().setDuration(1000).alpha(0).withEndAction(new Runnable() {
		                 @Override
		                 public void run() {
		                   list.remove(item);
		                   adapter.notifyDataSetChanged();
		                   view.setAlpha(1);
		                 }
		    		 });
		         }		    	
		    });
		    */
	}

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
    	switch(item.getItemId()){
	    	case R.id.action_search:
	    		openSearch();
	    		return true;
	    	case R.id.action_anotherOne:
	    		openAnotherBehavior();
	    		return true;
			default:
				super.onOptionsItemSelected(item);
    	}  
    	return true;
	}
    
    public void openSearch(){
    	ConnectivityManager connMgr = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
		HttpNetworkConnection conn = new HttpNetworkConnection(connMgr);
		
		boolean test =conn.CheckWifiConnection(); 
		if (test == true){
			new GetAsyncDataTask().execute("http://echo.jsontest.com/product/jeans%20levis/price/99.99/");
		}else{
						
		}
    } 
    
    public void openAnotherBehavior(){
    	Intent intent = new Intent(this,SampleTextShowSample.class);
    	startActivity(intent);
    } 
    
	private void showList() {		 
		ArrayList<Oferta> ofertaList = new ArrayList<Oferta>();
		ofertaList.clear();
		ofertaList.add(new Oferta("Calça Jeans Levi´s", new BigDecimal("99.99"),null,0,0));
		ofertaList.add(new Oferta("Casaco Calvin Klein", new BigDecimal("299.99"),null,0,0));
		ofertaList.add(new Oferta("Edredon Swingão", new BigDecimal("39.99"),null,0,0));
		ofertaList.add(new Oferta("Jogo Assasin´s Creed", new BigDecimal("69.99"),null,0,0));
		ofertaList.add(new Oferta("Camisa do Brasil", new BigDecimal("149.99"),null,0,0));
		OfertaListAdapter ofertaListAdapter = new OfertaListAdapter(MainActivity.this, ofertaList);
		listview.setAdapter(ofertaListAdapter);	 
	 }
    
	private class GetAsyncDataTask extends AsyncTask<String, Void, ArrayList<Oferta>> {
		   @Override
		   protected ArrayList<Oferta> doInBackground(String... urls) {		         
		       // params comes from the execute() call: params[0] is the url.			   
		       try {
		           return downloadUrl(urls[0]);
		       } catch (IOException e) {
	    	   		return new ArrayList<Oferta>();
		           //return "Unable to retrieve web page. URL may be invalid.";
		       }
		       
		   }
		   
		   // onPostExecute displays the results of the AsyncTask.
		   @Override
		   protected void onPostExecute(ArrayList<Oferta> listaProdutos) {
			   listview = (ListView) findViewById(R.id.listview);
			   OfertaListAdapter productListAdapter = new OfertaListAdapter(MainActivity.this, listaProdutos);
			   listview.setAdapter(productListAdapter);	
		   }
		   
		   private ArrayList<Oferta> downloadUrl(String myurl) throws IOException {
				InputStream is = null;
				// Only display the first 500 characters of the retrieved
				// web page content.
				int len = 500;
		        
		   	    try {
		   	        URL url = new URL(myurl);
		   	        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		   	        conn.setReadTimeout(10000 /* milliseconds */);
		   	        conn.setConnectTimeout(15000 /* milliseconds */);
		   	        conn.setRequestMethod("GET");
		   	        conn.setDoInput(true);
		   	        // Starts the query
		   	        conn.connect();
		   	        int response = conn.getResponseCode();
		   	        Log.d("debug response", "The response is: " + response);
		   	        is = conn.getInputStream();
		
		   	        // Convert the InputStream into a string
		   	        String contentAsString = readIt(is, len);
		   	        //return contentAsString;
		   	        
		   	        ArrayList<Oferta> productList = new ArrayList<Oferta>();
					productList.clear();
					productList.add(new Oferta("Bone Adidas", new BigDecimal("39.99"),null,0,0));
					productList.add(new Oferta("Camisa Social C.A.", new BigDecimal("49.99"),null,0,0));
					productList.add(new Oferta("Impressora HP Laser", new BigDecimal("499.99"),null,0,0));
					productList.add(new Oferta("Monitor LG LED 21'", new BigDecimal("699.99"),null,0,0));
					productList.add(new Oferta("Conjunto de Cordas de Guitarra A126", new BigDecimal("19.99"),null,0,0));
					productList.add(new Oferta("Mousepad SteelSeries", new BigDecimal("89.99"),null,0,0));
					productList.add(new Oferta("Mochila Sansonite 20l", new BigDecimal("189.99"),null,0,0));
					productList.add(new Oferta("Bermuda Sandpiper", new BigDecimal("59.99"),null,0,0));
					productList.add(new Oferta("Camisa Polo Sandpiper", new BigDecimal("109.99"),null,0,0));
					productList.add(new Oferta("Cortinas Pet V6", new BigDecimal("109.99"),null,0,0));
		   	        return productList;
		   	    } finally {
		   	        if (is != null) {
		   	            is.close();
		   	        } 
		   	    }
		   	}
		   	
	   		// Reads an InputStream and converts it to a String.
			public String readIt(InputStream stream, int len) throws IOException, UnsupportedEncodingException {
			    Reader reader = null;
			    reader = new InputStreamReader(stream, "UTF-8");        
			   	    char[] buffer = new char[len];
			   	    reader.read(buffer);
			   	    return new String(buffer);
	   		}
		}
    
}//class
