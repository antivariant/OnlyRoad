package com.gromsoft.onlyroad;

import android.content.Context;
import android.graphics.Canvas;
import android.location.Location;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapView;
import com.google.android.maps.MyLocationOverlay;

public class MyLocationOverlayRecord extends MyLocationOverlay {

	public boolean record;
	public float orientation;

	public MyLocationOverlayRecord(Context context, MapView mapView) {
		super(context, mapView);
	}

	@Override
	protected void drawMyLocation(Canvas canvas, MapView mapView, Location location, GeoPoint geoPoint, long when) {


		super.drawMyLocation(canvas, mapView, location, geoPoint, when);
		mapView.postInvalidate();
	}
	
	@Override
	public synchronized void onLocationChanged(Location arg0) {
		// TODO Auto-generated method stub
		super.onLocationChanged(arg0);
	}

	public void setOrientation(float angle){
	
		orientation=angle;
	}
	
	
}
