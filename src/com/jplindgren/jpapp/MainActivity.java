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
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ListView;

import com.jplindgren.jpapp.httpconnection.HttpNetworkConnection;
import com.jplindgren.jpapp.model.Oferta;
import com.jplindgren.jpapp.model.OfertaFactory;
import com.jplindgren.jpapp.model.ParcelableOferta;
import com.jplindgren.jpapp.util.AlertUserDialog;
import com.jplindgren.jpapp.util.OfertaListAdapter;

@SuppressLint("NewApi")
public class MainActivity extends Activity  {    
	
	ListView listview;
	public final static String ID_OFERTA_SELECIONADA = "com.jplindgren.jpapp.IdOfertaSelecionada";
	public ProgressDialog loadingDialog;
	
	private OnItemClickListener ofertaListViewClick = new AdapterView.OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> parent, final View view,int position, long id) {
			final Oferta selectedItem = (Oferta) parent.getItemAtPosition(position);
   		 	view.animate().setDuration(100).alpha(0).withEndAction(new Runnable() {
   		 		@Override
                public void run() {	
   		 			view.setAlpha(1);
   		 			OpenOferta(selectedItem);
   		 		}	                 
   		 	});
        }		
	};
	
	private OnItemLongClickListener ofertaListViewLongClick = new AdapterView.OnItemLongClickListener() {
		@Override
		public boolean onItemLongClick(AdapterView<?> parent, final View view, int arg2, long arg3) {
			return false;
		}		
	};
	
  
    @Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main); 
		
		listview = (ListView) findViewById(R.id.listview);
		showList(listview);
		
		listview.setOnItemClickListener(ofertaListViewClick);
		listview.setOnItemLongClickListener(ofertaListViewLongClick);
		registerForContextMenu(listview);
	}

    public void OpenOferta(Oferta oferta){
    	Intent intent = new Intent(getBaseContext(),ShowOfertaActivity.class);
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
			ShowMessage("Não há conexão! Que tal habilitar o 3g ou o Wifi? ;)", Settings.ACTION_WIFI_SETTINGS);		
		}
    } 
    
    private void ShowMessage(String message, String settingsActivityAction){
    	AlertUserDialog alert = new AlertUserDialog(message, settingsActivityAction);    	
    	alert.show(getFragmentManager(),null);
    }
    
    public void openAnotherBehavior(){
    	Intent intent = new Intent(this,SampleTextShowSample.class);
    	startActivity(intent);
    } 
    
	private void showList(ListView listview) {		 
		ArrayList<Oferta> ofertaList = new ArrayList<Oferta>();
		
		ArrayList<ParcelableOferta> ofertasExtra = getIntent().getParcelableArrayListExtra("preFetchedOfertas");
		for (ParcelableOferta parcelableOferta : ofertasExtra){
			ofertaList.add(parcelableOferta.getOferta());
		}
		
		if (ofertaList.size() == 0){
			ShowMessage("Nenhuma oferta encontrada, tem certeza que seu wifi ou 3g estão ligados? ;)", Settings.ACTION_WIFI_SETTINGS);
		}
		
		OfertaListAdapter ofertaListAdapter = new OfertaListAdapter(MainActivity.this, ofertaList);
		listview.setAdapter(ofertaListAdapter);					 
	}
	
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
		if (v.getId() == R.id.listview) {
			AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)menuInfo;
			Oferta selectedItem = (Oferta)listview.getItemAtPosition(info.position);
			menu.setHeaderTitle(selectedItem.getNomeProduto());
			String[] menuItems = getResources().getStringArray(R.array.context_menu_oferta_array);
			for (int i = 0; i<menuItems.length; i++) {
				menu.add(Menu.NONE, i, i, menuItems[i]);
			}
		}
	}
	
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)item.getMenuInfo();
		int menuItemIndex = item.getItemId();
		String[] menuItems = getResources().getStringArray(R.array.context_menu_oferta_array);
		String menuItemName = menuItems[menuItemIndex];
		
		Oferta selectedItem = (Oferta)listview.getItemAtPosition(info.position);
		if (menuItemName.equals("Ver Detalhes")){						
			OpenOferta(selectedItem);
		}else{
			ShowMessage(String.format("Selected %s for item %s", menuItemName, selectedItem) , null);
		}
		
		return true;
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
