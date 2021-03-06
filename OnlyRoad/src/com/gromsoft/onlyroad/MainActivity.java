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
import android.media.AudioManager;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.ActionBar.Tab;
import com.actionbarsherlock.app.ActionBar.TabListener;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.MyLocationOverlay;
import com.google.android.maps.Overlay;
import com.google.android.maps.OverlayItem;

public class MainActivity extends SherlockFragmentActivity implements TabListener, LocationListener, OnPageChangeListener {
	MapController mMapController;
	MapView mMapView;
	ViewPager mViewPager;
	com.actionbarsherlock.app.ActionBar mActionBar;
	LocationManager mLocationManager;
	List<Overlay> mapOverlays;
	Drawable startLocationMarker;
	RouteOverlay mRouteOverlay = null;
	int latitude, longtitude;
	GeoPoint myLocationGp;
	MyLocationOverlay mMyLocationOverlay;
	Context mContext;
	AudioManager mAudioManager;
	boolean isSpeakerOn;
	boolean isDefaultSpeakerOn;
	int speakerDefaulValue;
	public Menu mMenu; // TODO public для теста, не могу никак по-другому получить. Может быть роботиумом isToggleButtonChecked или тест положить в тот же пакет?

	final static String LOG = "MyLog";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		mContext = this;

		// Запомнить состояние динамика и громкости  
		mAudioManager = (AudioManager) this.getSystemService(Context.AUDIO_SERVICE);
		isDefaultSpeakerOn = mAudioManager.isSpeakerphoneOn();// Запомнить состояние до запуска
		speakerDefaulValue = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
		
		// Не выключать экран
		Window w = this.getWindow();
		w.setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

		// ---------------- Swipe -------------------------------------------
		LayoutInflater inflater = LayoutInflater.from(this);
		final View pageMap = findViewById(R.id.fragment_root);
		final View pagePhone = inflater.inflate(R.layout.layout_phone, null);
		final View pageVideo = inflater.inflate(R.layout.layout_video, null);

		List<View> pages = new ArrayList<View>();
		pages.add(pageMap);
		pages.add(pagePhone);
		pages.add(pageVideo);

		final MyPagerAdapter mMyPagerAdapter = new MyPagerAdapter(pages);
		mViewPager = new ViewPager(this);
		mViewPager.setAdapter(mMyPagerAdapter);
		mViewPager.setCurrentItem(0);
		mViewPager.setOffscreenPageLimit(3);  // MapView это тоже типа группа, чтобы он внутри не искал????
		setContentView(mViewPager);
		mViewPager.setOnPageChangeListener(this);

		// --------------- Карта ----------------------------------------------------
		mMapView = (MapView) pageMap.findViewById(R.id.mapview); // искать View нужно в pageMap
		mMapController = mMapView.getController();

		mLocationManager = (LocationManager) this.getSystemService(LOCATION_SERVICE);
		mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);

		mapOverlays = mMapView.getOverlays();// Получить слои, привязанные к карте

		mMyLocationOverlay = new MyLocationOverlay(this, mMapView);
		mMyLocationOverlay.enableMyLocation();
		mMyLocationOverlay.disableCompass();
		mapOverlays.add(mMyLocationOverlay);

		mMyLocationOverlay.runOnFirstFix(new Runnable() {
			@Override
			public void run() {
				mMapController.animateTo(mMyLocationOverlay.getMyLocation());

			}
		});

		// ---------------- ActionBar ----------------------------------
		mActionBar = getSupportActionBar();
		mActionBar.setDisplayHomeAsUpEnabled(true);// Иконка приложения для перехода назад
		mActionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);// Закладки
		mActionBar.setDisplayShowTitleEnabled(false);// Не показывать название приложения рядом с иконкой

		Tab tab = mActionBar.newTab().setText(R.string.menu_dvr).setTabListener(this);
		mActionBar.addTab(tab);
		tab = mActionBar.newTab().setText(R.string.menu_phone).setTabListener(this);
		mActionBar.addTab(tab);
		tab = mActionBar.newTab().setText(R.string.menu_videos).setTabListener(this);
		mActionBar.addTab(tab);

	}

	// ============================== Конец onCreate =====================================================

	// -------- Жизненный цикл --------------------------------------
	@Override
	protected void onPause() {
		// TODO Непонятно, мне ведь не нужно останавливать запись, ну чтобы в фоне тоже записывало
		mLocationManager.removeUpdates(this);
		mMyLocationOverlay.disableMyLocation();

		SaveSettings();

		super.onPause();
	}

	@Override
	protected void onResume() {

		RestoreSettings();

		mMyLocationOverlay.enableMyLocation();

		super.onResume();
	}

	@Override
	protected void onDestroy() {
		// Восстановить состояние динамика и громкости до запуска
		mAudioManager.setSpeakerphoneOn(isDefaultSpeakerOn);
		mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, speakerDefaulValue, 0);
		super.onDestroy();

	}

	private void SaveSettings() {
		SharedPreferences settings = this.getPreferences(0);
		SharedPreferences.Editor editor = settings.edit();
		editor.putBoolean("SpeakerPhone", isSpeakerOn);
		editor.putInt("Zoom", mMapView.getZoomLevel());
		editor.putInt("Volume", mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC));
		editor.commit();

	}

	private void RestoreSettings() {
		SharedPreferences settings = this.getPreferences(0);
		isSpeakerOn = settings.getBoolean("SpeakerPhone", false);
		mAudioManager.setSpeakerphoneOn(isSpeakerOn);
		mMapController.setZoom(settings.getInt("Zoom", mMapView.getMaxZoomLevel()));
		mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, settings.getInt("Volume", mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC)), 0);

		String mapvid = settings.getString("pref_map_vid_key", getString(R.string.pref_map_vid_default));
		mMapView.setSatellite((mapvid.compareTo("Спутник") == 0));

	}

	// ============================ @Overrides ====================================================

	// ----------------- Action Bar ------------------------------------------
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getSupportMenuInflater(); // getSupport.. важно для Sherlock
		inflater.inflate(R.menu.menu_main, menu);
		mMenu = menu;

		// Динамик
		MenuItem speakerMenu = mMenu.findItem(R.id.speaker);
		speakerMenu.setChecked(isSpeakerOn);
		speakerMenu.setIcon(isSpeakerOn ? R.drawable.speaker_on : R.drawable.speaker_off);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case (R.id.record):
			if (item.isChecked()) {
				item.setChecked(false);
				item.setIcon(R.drawable.record_on);
				mRouteOverlay.setRecording(false);
				return true;
			} else {
				item.setChecked(true);
				item.setIcon(R.drawable.record_off);
				mRouteOverlay.clearItems();
				mRouteOverlay.setRecording(true);
				return true;
			}
		case (R.id.speaker):
			isSpeakerOn = !item.isChecked();  // новое значение
		// Новые состояния
			mAudioManager.setSpeakerphoneOn(isSpeakerOn);// динамик
			item.setChecked(isSpeakerOn);// MenuItem
			item.setIcon(isSpeakerOn ? R.drawable.speaker_on : R.drawable.speaker_off);// иконка

			Log.d(LOG, "onOptionMenuSelected");
			Log.d(LOG, "Приложение isSpeakerOn=" + String.valueOf(isSpeakerOn));

			return true;
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
		mViewPager.setCurrentItem(tab.getPosition());
		if (tab.getPosition() == 0) {
			mMyLocationOverlay.enableMyLocation();
			mMapView.postInvalidate();
		} else
			mMyLocationOverlay.disableMyLocation();

	}

	@Override
	public void onPageSelected(int position) {

		mActionBar.setSelectedNavigationItem(position);

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

		if (mRouteOverlay == null) {
			mRouteOverlay = new RouteOverlay(getResources().getDrawable(R.drawable.location), Color.BLUE);
			mapOverlays.add(mRouteOverlay);
		}

		OverlayItem cOverlayItem = new OverlayItem(lastLocation, "", "");// слой с myLocation
		mRouteOverlay.addOverlay(cOverlayItem);// добавить к слоям карты
		mMapView.postInvalidate();

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


	
	// ========================= Процедуры =================================================

}
