package com.jplindgren.jpapp.model;

import java.math.BigDecimal;

public class Produto {
	private String name;
	private BigDecimal price;
	
	public Produto(String nome, BigDecimal preco){
		this.name = nome;
		this.price = preco;		
	}
	
	public String getName() {
		return name;
	}
	
	public  BigDecimal getPrice() {
		return price;
	}

}
