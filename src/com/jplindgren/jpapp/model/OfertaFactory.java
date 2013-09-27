package com.jplindgren.jpapp.model;

import java.math.BigDecimal;
import java.util.Date;

import org.json.JSONException;
import org.json.JSONObject;

public class OfertaFactory {
	public static Oferta Criar(JSONObject json){
		Oferta oferta = null;
		try{
			String nomeProduto =  json.getString("NomeProduto");
			String preco = json.getString("Preco");
			String nomeLoja = json.getString("NomeLoja");
			int idOferta = json.getInt("Id");
			double latitude = json.getDouble("Latidude");
			double longitude = json.getDouble("Longitude");
			String jsonDate = json.getString("DataPublicacao");
			jsonDate = jsonDate.substring(6,19);
			long jsonIntgerDateInMillis = Long.valueOf(jsonDate);
			Date dataPublicacao = new Date(jsonIntgerDateInMillis);
					// /Date(1377657513114)\
			JSONObject categoriaJSon = json.getJSONObject("Categoria");
			String categoriaNome = categoriaJSon.getString("Nome");
			
			oferta = new Oferta(idOferta,nomeProduto,new BigDecimal(preco),
					nomeLoja,longitude,latitude, categoriaNome, dataPublicacao);
		}catch(JSONException e){
			e.printStackTrace();			
		}catch(Exception e){
			e.printStackTrace();
		}	
		
		return oferta;
	}
	
	public static Oferta Criar(long idOferta, String nomeProduto, BigDecimal preco, String nomeLoja, double longitude, double latitude, 
			String nomeCategoria, Date dataPublicacao){
		return new Oferta(idOferta,nomeProduto, preco, nomeLoja,longitude,latitude, nomeCategoria, dataPublicacao);
	}
}
