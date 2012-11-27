package com.gromsoft.onlyroad;

import android.hardware.Camera;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.actionbarsherlock.app.SherlockFragment;


public class CameraFragment extends SherlockFragment{
    
	private Camera mCamera;
    private CameraPreview mPreview;
	
    @Override
	public void onCreate(Bundle arg0) {
		// TODO Auto-generated method stub
		super.onCreate(arg0);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup vg, Bundle savedInstanceState) {
		
        // Create an instance of Camera
      mCamera = getCameraInstance();

      // Create our Preview view and set it as the content of our activity.
      mPreview = new CameraPreview(this.getActivity().getApplicationContext(), mCamera);
		
      ViewGroup v=(ViewGroup)inflater.inflate(R.layout.camera_layout, vg, false);
      v.addView(mPreview);
      
		return ((View)v);
	}
	
	
    public static Camera getCameraInstance(){
        Camera c = null;    
        try {
            c = Camera.open(); // �������� ��������� ������
        }    catch (Exception e){
            // ������ ���������� (��� ��� ������)
        }
        return c; // null ���� ��� ��� ������
    }

	@Override
	public void onActivityCreated(Bundle arg0) {
		// TODO Auto-generated method stub
		super.onActivityCreated(arg0);
		
	}
	
	@Override
	public void onPause() {
		super.onPause();
		if(mCamera!=null) mCamera.release();
		
	}
	
}
