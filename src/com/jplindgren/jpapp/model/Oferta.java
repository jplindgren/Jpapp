package com.jplindgren.jpapp.model;

import java.math.BigDecimal;
import java.util.Date;

public class Oferta {
	private long id;
	private String nomeProduto;
	private BigDecimal preco;
	private String nomeLoja;
	private double longitude;
	private double latitude;
	private String categoria;
	private Date dataPublicacao;
	
	public Oferta(long id, String nome, BigDecimal preco, String nomeLoja, double longitude, 
					double latidute, String categoria, Date dataPublicacao){
		this.id = id;
		this.nomeProduto = nome;
		this.preco = preco;
		this.nomeLoja = nomeLoja;
		this.longitude = longitude;
		this.latitude = latidute;
		this.categoria = categoria;
		this.dataPublicacao = dataPublicacao;
	}
	
	public long getId() {
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

	public double getLongitude() {
		return longitude;
	}

	public double getLatitude() {
		return latitude;
	}
	
	public String getCategoria() {
		return categoria;
	}
	
	public Date getDataPublicacao() {
		return dataPublicacao;
	}
}
