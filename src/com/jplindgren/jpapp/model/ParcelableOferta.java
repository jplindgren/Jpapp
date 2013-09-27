package com.jplindgren.jpapp.model;

import java.math.BigDecimal;

import android.os.Parcel;
import android.os.Parcelable;

public class ParcelableOferta implements Parcelable {
	private Oferta oferta;
	public Oferta getOferta(){
		return oferta;
	}
	
	public ParcelableOferta(Oferta oferta) {
        this.oferta = oferta;
   }
	
	public ParcelableOferta(Parcel in) { 
		readFromParcel(in); 
	} 

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeLong(oferta.getId());	
		dest.writeString(oferta.getNomeProduto());
		dest.writeString(oferta.getCategoria());
		dest.writeString(oferta.getNomeLoja());
		//dest.write(oferta.getd()); TODO <-- implementar Date? COmo faz?
		dest.writeDouble(oferta.getLatitude());
		dest.writeDouble(oferta.getLongitude());
		dest.writeString(oferta.getPreco().toString());
		//dest.writeValue(oferta.getDataPublicacao());
	}
	
	 public static final Parcelable.Creator<ParcelableOferta> CREATOR = new Parcelable.Creator<ParcelableOferta>() {
		 public ParcelableOferta createFromParcel(Parcel in) {
		     return new ParcelableOferta(in);
		 }
		
		 public ParcelableOferta[] newArray(int size) {
		     return new ParcelableOferta[size];
		 }
	};
	
	private void readFromParcel(Parcel in) {
		long idOferta = in.readLong();
		String nomeProduto = in.readString();
		String categoria = in.readString();
		String nomeLoja = in.readString();
        double latitude = in.readDouble();
        double longitude = in.readDouble();
        BigDecimal preco = new BigDecimal(in.readString());
        oferta = OfertaFactory.Criar(idOferta, nomeProduto, preco, nomeLoja, longitude, latitude, categoria, null);
	}
}// class
