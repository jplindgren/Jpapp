package com.jplindgren.jpapp.util;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;

@SuppressLint("ValidFragment")
public class AlertUserDialog extends DialogFragment implements DialogInterface.OnClickListener{
	private String displayMessage;
	private String settingsActivityAction;
	
	public AlertUserDialog(String displayMessage, String settingsActivityAction){
		this.displayMessage = displayMessage != null ? displayMessage : "Nenhuma mensagem";
		this.settingsActivityAction = settingsActivityAction;
	}
	
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState){
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
    	builder.setMessage(displayMessage)
    	       .setTitle("Atenção!")
    	       .setPositiveButton("Ok", this);
    	// 3. Get the AlertDialog from create()
    	AlertDialog dialog = builder.create();
    	dialog.setCanceledOnTouchOutside(true);
    	return dialog;
	}
	
	@Override
	public void onClick(DialogInterface dialogInterface, int i) {
		switch(i){
		case Dialog.BUTTON_POSITIVE : 
			if(settingsActivityAction.equals(Settings.ACTION_WIFI_SETTINGS))
				dialogInterface.cancel();
				startActivity(new Intent(settingsActivityAction));
			break;
		}
	}

} // class
