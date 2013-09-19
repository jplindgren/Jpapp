package com.jplindgren.jpapp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.jplindgren.jpapp.httpconnection.HttpNetworkConnection;
import com.jplindgren.jpapp.model.Oferta;
import com.jplindgren.jpapp.model.OfertaFactory;
import com.jplindgren.jpapp.util.AlertUserDialog;
import com.jplindgren.jpapp.util.OfertaListAdapter;

@SuppressLint("NewApi")
public class MainActivity extends Activity  {    
	
	ListView listview;
	public final static String ID_OFERTA_SELECIONADA = "com.jplindgren.jpapp.IdOfertaSelecionada";
	public ProgressDialog loadingDialog;
  
    @Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		
		Intent intent = new Intent(this, CompassAcivityNew.class);
		startActivity(intent);
		
		listview = (ListView) findViewById(R.id.listview);
		listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
	    	 @Override
	         public void onItemClick(AdapterView<?> parent, final View view,int position, long id) {
	    		 final Oferta selectedItem = (Oferta) parent.getItemAtPosition(position);
	    		 view.animate().setDuration(1000).alpha(0).withEndAction(new Runnable() {
	                 @Override
	                 public void run() {	
	                	 view.setAlpha(1);
	                	 OpenOferta(selectedItem);
	                 }	                 
	    		 });
	         }		    	
	    });
		showList();		
	}

    public void OpenOferta(Oferta oferta){
    	Intent intent = new Intent(this,ShowOfertaActivity.class);
    	intent.putExtra(ID_OFERTA_SELECIONADA, oferta.getId());
    	startActivity(intent);
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
		
		boolean isConnected = conn.CheckConnection(); 
		if (isConnected){
			new GetAsyncDataTask(this).execute("http://restapi-2.apphb.com/oferta/ofertasdodia");
		}else{
			ShowMessage();		
		}
    } 
    
    private void ShowMessage(){
    	/*
    	AlertDialog.Builder builder = new AlertDialog.Builder(this);
    	// 2. Chain together various setter methods to set the dialog characteristics
    	builder.setMessage("Não há conexão! Habilite o 3g ou o Wifi")
    	       .setTitle("Nada feito");
    	// 3. Get the AlertDialog from create()
    	AlertDialog dialog = builder.create();
    	*/
    	AlertUserDialog alert = new AlertUserDialog("Não há conexão! Habilite o 3g ou o Wifi", Settings.ACTION_WIFI_SETTINGS);    	
    	alert.show(getFragmentManager(),null);
    }
    
    public void openAnotherBehavior(){
    	Intent intent = new Intent(this,SampleTextShowSample.class);
    	startActivity(intent);
    } 
    
	private void showList() {		 
		ArrayList<Oferta> ofertaList = new ArrayList<Oferta>();
		ofertaList.clear();
		ofertaList.add(new Oferta(1,"Calça Jeans Levi´s", new BigDecimal("99.99"),null,0,0,"categoria",new Date()));
		ofertaList.add(new Oferta(2,"Casaco Calvin Klein", new BigDecimal("299.99"),null,0,0,"categoria",new Date()));
		ofertaList.add(new Oferta(3,"Edredon Swingão", new BigDecimal("39.99"),null,0,0,"categoria",new Date()));
		ofertaList.add(new Oferta(4,"Jogo Assasin´s Creed", new BigDecimal("69.99"),null,0,0,"categoria",new Date()));
		ofertaList.add(new Oferta(5,"Camisa do Brasil", new BigDecimal("149.99"),null,0,0,"categoria",new Date()));
		OfertaListAdapter ofertaListAdapter = new OfertaListAdapter(MainActivity.this, ofertaList);
		listview.setAdapter(ofertaListAdapter);	 
	 }
    
	private class GetAsyncDataTask extends AsyncTask<String, Void, ArrayList<Oferta>> {
		  Context context;
		  
		  	GetAsyncDataTask(Context context) {
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

		        loadingDialog = new ProgressDialog(context);
		        loadingDialog.setMessage("Carregando...");
		        loadingDialog.show();
		    }
		   
		   // onPostExecute displays the results of the AsyncTask.
		   @Override
		   protected void onPostExecute(ArrayList<Oferta> listaProdutos) {
			   loadingDialog.dismiss();
			   listview = (ListView) findViewById(R.id.listview);
			   OfertaListAdapter productListAdapter = new OfertaListAdapter(MainActivity.this, listaProdutos);
			   listview.setAdapter(productListAdapter);
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
    
}//class
