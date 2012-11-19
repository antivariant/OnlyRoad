package com.gromsoft.onlyroad;


import android.os.Bundle;
import android.view.MenuItem;

import com.actionbarsherlock.app.SherlockPreferenceActivity;
import com.actionbarsherlock.view.Menu;
import com.gromsoft.onlyroad.R;


public class SettingsActivity extends SherlockPreferenceActivity {

@SuppressWarnings("deprecation")
@Override
protected void onCreate(Bundle savedInstanceState) {
	setTheme(R.style.AppTheme);
	super.onCreate(savedInstanceState);
	addPreferencesFromResource(R.xml.preferences);
}
	
@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO Auto-generated me thod stub
		
	menu.add(R.string.pref_map_vid)
	.setShowAsAction(MenuItem.SHOW_AS_ACTION_WITH_TEXT);
	return super.onCreateOptionsMenu(menu);
	}

}

