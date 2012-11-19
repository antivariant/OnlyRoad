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
import android.view.Window;
import android.view.WindowManager;

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
	
	
	final static String LOG = "MyLog";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		//�� ��������� �����
		Window w=this.getWindow();
		w.setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
			
		//---------------- Swipe ------------------------------------------- 
		LayoutInflater inflater = LayoutInflater.from(this);
		final View pageMap = (MapView) inflater.inflate(R.layout.layout_map, null);
		final View pagePhone = inflater.inflate(R.layout.layout_phone, null);
		final View pageVideo = inflater.inflate(R.layout.layout_video, null);
		mContext = this;

		List<View> pages = new ArrayList<View>();
		pages.add(pageMap);
		pages.add(pagePhone);
		pages.add(pageVideo);

		final MyPagerAdapter mMyPagerAdapter = new MyPagerAdapter(pages);
		mViewPager = new ViewPager(this);
		mViewPager.setAdapter(mMyPagerAdapter);
		mViewPager.setCurrentItem(0);
		mViewPager.setOffscreenPageLimit(3); // MapView ��� ���� ���� ������, ����� �� ������ �� �����????
		setContentView(mViewPager);
		mViewPager.setOnPageChangeListener(this);

		// --------------- ����� ----------------------------------------------------
		mMapView = (MapView) pageMap.findViewById(R.id.map); // ������ View ����� � pageMap
		mMapController = mMapView.getController();

		mLocationManager = (LocationManager) this.getSystemService(LOCATION_SERVICE);
		mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);

		mapOverlays = mMapView.getOverlays();// �������� ����, ����������� � �����

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

		mMapController.setZoom(mMapView.getMaxZoomLevel());

		// ---------------- ActionBar ----------------------------------
		mActionBar = getSupportActionBar();
		mActionBar.setDisplayHomeAsUpEnabled(true);// ������ ���������� ��� �������� �����
		mActionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);// ��������
		mActionBar.setDisplayShowTitleEnabled(false);// �� ���������� �������� ���������� ����� � �������

		Tab tab = mActionBar.newTab().setText(R.string.menu_dvr).setTabListener(this);
		mActionBar.addTab(tab);
		tab = mActionBar.newTab().setText(R.string.menu_phone).setTabListener(this);
		mActionBar.addTab(tab);
		tab = mActionBar.newTab().setText(R.string.menu_videos).setTabListener(this);
		mActionBar.addTab(tab);

	}

//============================== ����� onCreate =====================================================	
	
	// -------- ��������� ���� --------------------------------------
	@Override
	protected void onPause() {
		// TODO ���������, ��� ���� �� ����� ������������� ������, �� ����� � ���� ���� ����������
		mLocationManager.removeUpdates(this);
		mMyLocationOverlay.disableMyLocation();
		super.onPause();
	}

	@Override
	protected void onResume() {
		super.onResume();
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
		String regular = prefs.getString("pref_map_vid_key", getString(R.string.pref_map_vid_default));
		if (regular.contains("�������"))
			mMapView.setSatellite(true);
		else
			mMapView.setSatellite(false);
		mMyLocationOverlay.enableMyLocation();

	}

	
	
	
//============================ @Overrides ====================================================

	
	//----------------- Action Bar ------------------------------------------
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getSupportMenuInflater(); // getSupport.. ����� ��� Sherlock
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

	
	// ---------------------- ����� ------------------------
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

		OverlayItem cOverlayItem = new OverlayItem(lastLocation, "", "");// ��������� ��������� �����
		mRouteOverlay.addOverlay(cOverlayItem);// �������� �� � ����
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

	

	//========================= ��������� =================================================
	
	

}
