package com.gromsoft.onlyroad;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.ActionBar.Tab;
import com.actionbarsherlock.app.ActionBar.TabListener;
import com.actionbarsherlock.app.SherlockMapActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.MyLocationOverlay;
import com.google.android.maps.Overlay;
import com.google.android.maps.OverlayItem;


public class MainActivity extends SherlockMapActivity implements TabListener, LocationListener, OnPageChangeListener {
	MapController cMapController;
	MapView cMapView;
	ViewPager cViewPager;
	com.actionbarsherlock.app.ActionBar cActionBar;
	LocationManager cLocationManager;
	List<Overlay> mapOverlays;
	Drawable startLocationMarker;
	RouteOverlay itemizedOverlay = null;
	int latitude, longtitude;
	GeoPoint myLocationGp;
	MyLocationOverlay myLocationOverlay;
	Context context;

	final static String LOG = "MyLog";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		//---------------- Swipe ------------------------------------------- 
		LayoutInflater inflater = LayoutInflater.from(this);
		final View pageMap = (MapView) inflater.inflate(R.layout.layout_map, null);
		final View pagePhone = inflater.inflate(R.layout.layout_phone, null);
		final View pageVideo = inflater.inflate(R.layout.layout_video, null);
		context = this;

		List<View> pages = new ArrayList<View>();
		pages.add(pageMap);
		pages.add(pagePhone);
		pages.add(pageVideo);

		MyPagerAdapter pagerAdapter = new MyPagerAdapter(pages);
		cViewPager = new ViewPager(this);
		cViewPager.setAdapter(pagerAdapter);
		cViewPager.setCurrentItem(0);
		cViewPager.setOffscreenPageLimit(3); // MapView это тоже типа группа, чтобы он внутри не искал????
		setContentView(cViewPager);
		cViewPager.setOnPageChangeListener(this);

		// --------------- Карта ----------------------------------------------------
		cMapView = (MapView) pageMap.findViewById(R.id.map); // искать View нужно в pageMap
		cMapController = cMapView.getController();

		cLocationManager = (LocationManager) this.getSystemService(LOCATION_SERVICE);
		cLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);

		mapOverlays = cMapView.getOverlays();// Получить слои, привязанные к карте

		myLocationOverlay = new MyLocationOverlay(this, cMapView);
		myLocationOverlay.enableMyLocation();
		myLocationOverlay.disableCompass();
		mapOverlays.add(myLocationOverlay);

		myLocationOverlay.runOnFirstFix(new Runnable() {
			@Override
			public void run() {
				cMapController.animateTo(myLocationOverlay.getMyLocation());
				
			}
		});

		cMapController.setZoom(cMapView.getMaxZoomLevel());

		// ---------------- ActionBar ----------------------------------
		cActionBar = getSupportActionBar();
		cActionBar.setDisplayHomeAsUpEnabled(true);// Иконка приложения для перехода назад
		cActionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);// Закладки
		cActionBar.setDisplayShowTitleEnabled(false);// Не показывать название приложения рядом с иконкой

		Tab tab = cActionBar.newTab().setText(R.string.menu_dvr).setTabListener(this);
		cActionBar.addTab(tab);
		tab = cActionBar.newTab().setText(R.string.menu_phone).setTabListener(this);
		cActionBar.addTab(tab);
		tab = cActionBar.newTab().setText(R.string.menu_videos).setTabListener(this);
		cActionBar.addTab(tab);

	}

//============================== Конец onCreate =====================================================	
	
	// -------- Жизненный цикл --------------------------------------
	@Override
	protected void onPause() {
		// TODO Непонятно, мне ведь не нужно останавливать запись, ну чтобы в фоне тоже записывало
		cLocationManager.removeUpdates(this);
		myLocationOverlay.disableMyLocation();
		super.onPause();
	}

	@Override
	protected void onResume() {
		super.onResume();
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
		String regular = prefs.getString("pref_map_vid_key", getString(R.string.pref_map_vid_default));
		if (regular.contains("Спутник"))
			cMapView.setSatellite(true);
		else
			cMapView.setSatellite(false);
		myLocationOverlay.enableMyLocation();

	}

	
	
	
//============================ @Overrides ====================================================

	
	//----------------- Action Bar ------------------------------------------
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getSupportMenuInflater(); // getSupport.. важно для Sherlock
		inflater.inflate(R.menu.menu_main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case (R.id.record):
			if (item.isChecked()) {
				item.setChecked(false);
				item.setIcon(R.drawable.record_on);
				itemizedOverlay.setRecording(false);

				return true;
			} else {
				item.setChecked(true);
				item.setIcon(R.drawable.record_off);
				itemizedOverlay.clearItems();
				itemizedOverlay.setRecording(true);
				return true;
			}
		case (R.id.speaker):
			if (item.isChecked()) {
				item.setChecked(false);
				item.setIcon(R.drawable.speaker_on);
				return true;
			} else {
				item.setChecked(true);
				item.setIcon(R.drawable.speaker_off);
				return true;
			}
		case (R.id.autoanswer):
			if (item.isChecked()) {
				item.setChecked(false);
				item.setIcon(R.drawable.autoanswer_on);
				return true;
			} else {
				item.setChecked(true);
				item.setIcon(R.drawable.autoanswer_off);
				return true;
			}

		case (R.id.settings_menuitem):
			Intent intent = new Intent();
			intent.setClass(this, SettingsActivity.class);
			startActivity(intent);
			return true;

		default:
			return super.onOptionsItemSelected(item);
		}
	}

	// ------------------ Navigation Tabs----------------
	@Override
	public void onTabSelected(Tab tab, FragmentTransaction ft) {
		Log.d(LOG, "Page=" + String.valueOf(tab.getPosition()));
		cViewPager.setCurrentItem(tab.getPosition());
		if (tab.getPosition() == 0) {
			myLocationOverlay.enableMyLocation();
			cMapView.postInvalidate();
		} else
			myLocationOverlay.disableMyLocation();

	}

	@Override
	public void onPageSelected(int position) {

		cActionBar.setSelectedNavigationItem(position);

	}

	@Override
	public void onTabUnselected(Tab tab, FragmentTransaction ft) {
	}

	@Override
	public void onTabReselected(Tab tab, FragmentTransaction ft) {
	}

	@Override
	public void onPageScrollStateChanged(int arg0) {

	}

	@Override
	public void onPageScrolled(int arg0, float arg1, int arg2) {

	}

	
	// ---------------------- Карта ------------------------
	@Override
	public void onLocationChanged(Location location) {

		Log.d(LOG, "LocationChanged lat=" + String.valueOf(latitude) + " long=" + String.valueOf(longtitude));
		latitude = (int) (location.getLatitude() * 1e6);
		longtitude = (int) (location.getLongitude() * 1e6);

		GeoPoint lastLocation = new GeoPoint(latitude, longtitude);

		if (itemizedOverlay == null) {
			itemizedOverlay = new RouteOverlay(getResources().getDrawable(R.drawable.location), Color.BLUE);
			mapOverlays.add(itemizedOverlay);
		}

		OverlayItem cOverlayItem = new OverlayItem(lastLocation, "", "");// Добавляем следующую точку
		itemizedOverlay.addOverlay(cOverlayItem);// Добавить ее к слою
		cMapView.postInvalidate();

	}

	@Override
	public void onProviderDisabled(String provider) {
	}

	@Override
	public void onProviderEnabled(String provider) {
	}


	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
	}

	@Override
	protected boolean isRouteDisplayed() {
		return false;
	}

	

	//========================= Процедуры =================================================
	
	

}
