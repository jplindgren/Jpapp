package com.jplindgren.jpapp.servico.message;

public class RequestGetOferta {
	private long idOferta;
	
	public RequestGetOferta(long idOferta){
		this.idOferta = idOferta;
	}
	
	public long getIdOferta(){
		return idOferta;
	}
}
