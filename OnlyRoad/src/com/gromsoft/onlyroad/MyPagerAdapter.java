package com.gromsoft.onlyroad;

import java.util.List;

import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;

public class MyPagerAdapter extends PagerAdapter {

	List<View> pages=null;
	
	public MyPagerAdapter(List<View> pages) {
		this.pages=pages;
	}
	
	@Override
	public Object instantiateItem(View container, int position) {
		View v=pages.get(position);
		((ViewPager)container).addView(v);
			
		return v; 
	}
	
	@Override
	public int getCount() {
		return pages.size();
	}

	@Override
	public boolean isViewFromObject(View arg0, Object arg1) {
		return arg0.equals(arg1);
	}
	
}
