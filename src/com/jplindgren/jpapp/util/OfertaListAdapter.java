package com.jplindgren.jpapp.util;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.jplindgren.jpapp.R;
import com.jplindgren.jpapp.model.Oferta;

public class OfertaListAdapter extends BaseAdapter {
	 
    Context context;
    ArrayList<Oferta> ofertaList;
 
    public OfertaListAdapter(Context context, ArrayList<Oferta> list) { 
        this.context = context;
        ofertaList = list;
    }
 
    @Override
    public int getCount() { 
        return ofertaList.size();
    }
 
    @Override
    public Object getItem(int position) { 
        return ofertaList.get(position);
    }
 
    @Override
    public long getItemId(int position) { 
        return position;
    }
 
    @Override
    public View getView(int position, View convertView, ViewGroup arg2) {
    	Oferta ofertaItem = (Oferta)ofertaList.get(position);
 
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.produto_list_row, null);
 
        }
        
        TextView tvNomeProduto = (TextView) convertView.findViewById(R.id.product_name);        
        tvNomeProduto.setText(ofertaItem.getNomeProduto());
        
        TextView tvPreco = (TextView) convertView.findViewById(R.id.product_price);
        tvPreco.setText(ofertaItem.getPreco().toString());
 
        return convertView;
    }
 
}
