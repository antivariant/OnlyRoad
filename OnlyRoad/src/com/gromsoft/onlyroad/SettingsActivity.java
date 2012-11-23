package com.gromsoft.onlyroad;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.telephony.TelephonyManager;
import android.view.MenuItem;

import com.actionbarsherlock.app.SherlockPreferenceActivity;
import com.actionbarsherlock.view.Menu;

public class SettingsActivity extends SherlockPreferenceActivity implements OnSharedPreferenceChangeListener {

	SharedPreferences prefs;
	SharedPreferences.Editor editor;
	TelephonyManager mTelephonyManager;

	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setTheme(R.style.Theme_Sherlock);
		super.onCreate(savedInstanceState);

		prefs = PreferenceManager.getDefaultSharedPreferences(this);
		editor = prefs.edit();

		mTelephonyManager = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);
		if (mTelephonyManager.getDataState() == TelephonyManager.DATA_DISCONNECTED)
			editor.putBoolean("pref_gprs_key", false);
		else
			editor.putBoolean("pref_gprs_key", true);
		editor.commit();

		addPreferencesFromResource(R.xml.preferences);
		
		SetSummary("pref_map_vid_key");
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(R.string.pref_map_vid).setShowAsAction(MenuItem.SHOW_AS_ACTION_WITH_TEXT);
		menu.add(R.string.pref_gprs).setShowAsAction(MenuItem.SHOW_AS_ACTION_WITH_TEXT);

		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {

		if (key.compareTo("pref_gprs_key") == 0) {
			setMobileDataEnabled(this, sharedPreferences.getBoolean("pref_gprs_key", true));
		}

		if (key.compareTo("pref_map_vid_key") == 0) {
			SetSummary(key);
		}
		
	}

	private void SetSummary(String key){
			@SuppressWarnings("deprecation")
			Preference map_vid = findPreference(key);
			map_vid.setSummary(prefs.getString(key, ""));
	} 
	
	
	@SuppressWarnings("deprecation")
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
	}

	@SuppressWarnings("deprecation")
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
	}

	private void setMobileDataEnabled(Context context, boolean enabled) {
		try {
			final ConnectivityManager conman = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
			final Class conmanClass = Class.forName(conman.getClass().getName());
			final Field iConnectivityManagerField = conmanClass.getDeclaredField("mService");
			iConnectivityManagerField.setAccessible(true);
			final Object iConnectivityManager = iConnectivityManagerField.get(conman);
			final Class iConnectivityManagerClass = Class.forName(iConnectivityManager.getClass().getName());
			final Method setMobileDataEnabledMethod = iConnectivityManagerClass.getDeclaredMethod("setMobileDataEnabled", Boolean.TYPE);
			setMobileDataEnabledMethod.setAccessible(true);
			setMobileDataEnabledMethod.invoke(iConnectivityManager, enabled);
		} catch (Exception e) {
		}
	}

}
