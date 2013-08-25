package com.jplindgren.jpapp;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

public class SampleTextShowSample extends Activity {
	
	public final static String EXTRA_MESSAGE = "com.jplindgren.jpapp.MESSAGE";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sample_text_show_sample);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.sample_text_show_sample, menu);
		return true;
	}
	
	@Override
    public boolean onOptionsItemSelected(MenuItem item) {
    	switch(item.getItemId()){
	    	case R.id.action_search:
	    		openSearch();
	    		return true;
			default:
				super.onOptionsItemSelected(item);
    	}  
    	return true;
	}

	public void openSearch(){
    	Intent intent = new Intent(this,ListActivity.class);
    	startActivity(intent);
    }
	 
    public void sendMessage(View view){
    	Intent intent = new Intent(this,DisplayMessageActivity.class);
    	EditText editText = (EditText)findViewById(R.id.edit_message);
    	String message = editText.getText().toString();
    	intent.putExtra(EXTRA_MESSAGE, message);
    	startActivity(intent);
    }

    
} //class
