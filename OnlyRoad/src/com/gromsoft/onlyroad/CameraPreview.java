package com.gromsoft.onlyroad;

import java.io.IOException;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.hardware.Camera;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class CameraPreview extends SurfaceView implements SurfaceHolder.Callback {

	private SurfaceHolder mHolder; // Контейнер для превью
	private Camera mCamera;
	private final static String TAG = "MyLog";
	List<Camera.Size> supportedPreviewSizes;
	Context mContext;
	Activity mActivity;

	// Конструктор
	public CameraPreview(Context context, Camera camera, Activity activity) {
		super(context);
		mCamera = camera;
		mHolder = getHolder();
		mHolder.addCallback(this);
		mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS); // C API 11 (Android 3.0) depricated, ничего не значит
		mContext = context;
		mActivity = activity;
	}

	public void surfaceCreated(SurfaceHolder holder) {
		// Превью подготовлено, стартуем
//		try {
//			// 2 - подключить превью
//			mCamera.setPreviewDisplay(holder);
//			// 3 - начать превью
//			//mCamera.startPreview();
//		} catch (IOException e) {
//			Log.d(TAG, "Error setting camera preview: " + e.getMessage());
//		}
//		//mCamera.unlock();
	}

	public void surfaceDestroyed(SurfaceHolder holder) {
		// empty. Take care of releasing the Camera preview in your activity.
	}

	public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
		// Всегда перед поворотом, ресайзом и т.п.
		if (mHolder.getSurface() == null) {
			// нет камеры
			return;
		}
		// Остановить перед поворотом
		try {
			mCamera.stopPreview();
		} catch (Exception e) {
		}
		// Тут всякие обработки
//		Camera.Parameters param = mCamera.getParameters();
//		supportedPreviewSizes = param.getSupportedPreviewSizes();
//		int pWidth = supportedPreviewSizes.get(0).width;
//		int pHeight = supportedPreviewSizes.get(0).height;
//		param.setPreviewSize(pWidth, pHeight);
//		requestLayout();
//		mCamera.setParameters(param);
//
//		int rotation = mActivity.getWindow().getWindowManager().getDefaultDisplay().getOrientation();
//		int degrees = 0;
//		switch (rotation) {
//		case Surface.ROTATION_0:
//			degrees = 0;
//			break;
//		case Surface.ROTATION_90:
//			degrees = 90;
//			break;
//		case Surface.ROTATION_180:
//			degrees = 180;
//			break;
//		case Surface.ROTATION_270:
//			degrees = 270;
//			break;
//		}
//
//		View cameraPlace = mActivity.findViewById(R.id.camera_place);
//
//		LayoutParams lp = cameraPlace.getLayoutParams();
//
//		if (degrees == 0 || degrees == 180) {
//			lp.width = pWidth;
//			lp.height = pHeight;
//		} else {
//			lp.width = pHeight;
//			lp.height = pWidth;
//		}
//
//		cameraPlace.setLayoutParams(lp);
//
//		mCamera.setDisplayOrientation(degrees);
//
		try {
			mCamera.setPreviewDisplay(mHolder);
			mCamera.startPreview();
			//mCamera.unlock();
		} catch (Exception e) {
			Log.d(TAG, "Error starting camera preview: " + e.getMessage());
		}
	}

}
