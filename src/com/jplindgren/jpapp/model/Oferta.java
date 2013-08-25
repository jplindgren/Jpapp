package com.jplindgren.jpapp.model;

import java.math.BigDecimal;

public class Oferta {
	private int id;
	private String nomeProduto;
	private BigDecimal preco;
	private String nomeLoja;
	private int longitude;
	private int latitude;
	
	public Oferta(int id, String nome, BigDecimal preco, String nomeLoja, int longitude, int latidute){
		this.id = id;
		this.nomeProduto = nome;
		this.preco = preco;
		this.nomeLoja = nomeLoja;
		this.longitude = longitude;
		this.latitude = latidute;
	}
	
	public int getId() {
		return id;
	}
	
	public String getNomeProduto() {
		return nomeProduto;
	}
	
	public  BigDecimal getPreco() {
		return preco;
	}

	public String getNomeLoja() {
		return nomeLoja;
	}

	public int getLongitude() {
		return longitude;
	}

	public int getLatitude() {
		return latitude;
	}
}
