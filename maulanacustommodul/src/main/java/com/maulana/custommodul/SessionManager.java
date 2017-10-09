package com.maulana.custommodul;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class SessionManager {
	// Shared Preferences
	SharedPreferences pref;
	
	// Editor for Shared preferences
	Editor editor;
	
	// Context
	Context context;
	
	// Shared pref mode
	int PRIVATE_MODE = 0;
	
	// Sharedpref file name
	private static final String PREF_NAME = "GmediaUserPSPLF";
	
	// All Shared Preferences Keys
	public static final String TAG_KODE_AREA = "kodearea";
	public static final String TAG_USERNAME = "username";
	public static final String TAG_PASSWORD = "password";

	// Constructor
	public SessionManager(Context context){
		this.context = context;
		pref = this.context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
		editor = pref.edit();
	}
	
	/**
	 * Create login session
	 * */
	public void createLoginSession(String username, String password, String kodearea){

		editor.putString(TAG_KODE_AREA, kodearea);

		editor.putString(TAG_USERNAME, username);

		editor.putString(TAG_PASSWORD, password);
		// commit changes
		editor.commit();
	}

	public String getUserInfo(String key){
		return pref.getString(key, null);
	}

	public String getUser(){
		return pref.getString(TAG_KODE_AREA, null);
	}

	/**
	 * Clear session details
	 * */
	public void logoutUser(Intent logoutIntent){

		// Clearing all data from Shared Preferences
		try {
			editor.clear();
			editor.commit();
		}catch (Exception e){
			e.printStackTrace();
		}

		logoutIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		context.startActivity(logoutIntent);
		((Activity)context).finish();
		((Activity)context).overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
	}

}
