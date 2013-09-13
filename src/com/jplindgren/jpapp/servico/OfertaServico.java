package com.jplindgren.jpapp.servico;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;

import com.jplindgren.jpapp.model.Oferta;
import com.jplindgren.jpapp.model.OfertaFactory;

public class OfertaServico {
	private static final String GET_OFERTA_URL = "http://restapi-2.apphb.com/oferta/getoferta/";
	
	public ArrayList<Oferta> ListarOfertas(){
		return null;
	}
	
	public Oferta getOferta(long id) throws IOException{
		Oferta oferta = null;
		InputStream is = null;
		
   	    try {
   	    	String teste = String.format("%s/%s", GET_OFERTA_URL , String.valueOf(id));
   	        URL url = new URL(teste);
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
        } catch (JSONException e) {
            e.printStackTrace();
        } finally {
   	        if (is != null) {
   	            is.close();
   	        } 
   	    }
   	    return oferta;
	}
}// class
