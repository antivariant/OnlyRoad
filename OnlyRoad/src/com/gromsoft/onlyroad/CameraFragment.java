package com.gromsoft.onlyroad;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.content.Context;
import android.hardware.Camera;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;

public class CameraFragment extends SherlockFragment {

	private Camera mCamera;
	private CameraPreview mPreview;
	private MediaRecorder mMediaRecorder;
	public static final int MEDIA_TYPE_IMAGE = 1;
	public static final int MEDIA_TYPE_VIDEO = 2;
	final static String LOG = "MyLog";
	private static Context mContext;
	Camera.Parameters cameraParams;
	
	
	@Override
	public void onCreate(Bundle arg0) {

		mContext = getActivity().getApplicationContext();

		super.onCreate(arg0);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup vg, Bundle savedInstanceState) {

		setHasOptionsMenu(true);

		// 1 - Получить экземпляр камеры
		mCamera = getCameraInstance();
		
		cameraParams = mCamera.getParameters();

		// Внутри CameraPreview пункты 2,3
		mPreview = new CameraPreview(this.getActivity().getApplicationContext(), mCamera, getActivity());

		// TODO тут не знаю, может перед добавление превью к лэйауту нужно его приостановить?
		mCamera.stopPreview();
		ViewGroup v = (ViewGroup) inflater.inflate(R.layout.camera_fragment, vg, false);
		v.addView(mPreview);
		mCamera.startPreview();

		return ((View) v);
	}

	private Camera getCameraInstance() {
		Camera c = null;
		try {
			c = Camera.open(); // получить экземпляр камеры
		} catch (Exception e) {
			// нет в системе
		}
		return c; // null не готова
	}

	@Override
	public void onActivityCreated(Bundle arg0) {
		super.onActivityCreated(arg0);

	}

	@Override
	public void onPause() {
		super.onPause();
		if (mCamera != null)
			mCamera.release();
	}

	@Override
	public void onResume() {
		super.onResume();
		if (mCamera == null) {
			mCamera = getCameraInstance();
		}

		if (mMediaRecorder != null) {
			mMediaRecorder.reset();
		}

	}

	private boolean prepareVideoRecorder() {

		// mCamera.stopPreview();
		// mCamera.release();
		// mCamera=null;

		if (mMediaRecorder == null)
			mMediaRecorder = new MediaRecorder();

		// ------------- Первые три этапа пропускаем, я их делаю сразу при старте приложения - превью всегда включено
		// 1 - Создать экземпляр камеры
		if (mCamera == null)
			mCamera = getCameraInstance();
		else {
			mCamera.stopPreview(); // Отключить старое превью
			mCamera.release();
			mCamera = null;
			mCamera = getCameraInstance();
		}

		// 2 - подключить preview
		try {
			mCamera.setPreviewDisplay(mPreview.getHolder());
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		// 3 - начать превью
		mCamera.startPreview();

		// --------- 4 - начать запись видео

		// 4.1 - Unlock camera
		mCamera.unlock();

		// -- 4.2 - Конфигурация MediaRecorder
		// 4.2.1. - С какой камеры записывать
		mMediaRecorder.setCamera(mCamera);

		// 4.2.2. - Источник аудио
		mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.CAMCORDER);

		// 4.2.3 - Источник видео
		mMediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);

		// 4.2.4. - Формат сжатия и сохранения
		mMediaRecorder.setProfile(CamcorderProfile.get(CamcorderProfile.QUALITY_HIGH));

		// 4.2.5. - Файл для сохранения
		mMediaRecorder.setOutputFile(getOutputMediaFile(MEDIA_TYPE_VIDEO).toString());

		// 4.2.6. - Указать превью
		mMediaRecorder.setPreviewDisplay(mPreview.getHolder().getSurface());

		// mCamera.unlock();

		// 4.3. - Подготовка MediaRecorder
		try {
			mMediaRecorder.prepare();
		} catch (IllegalStateException e) {
			Log.d(LOG, "IllegalStateException preparing MediaRecorder: " + e.getMessage());
			releaseMediaRecorder();
			return false;
		} catch (IOException e) {
			Log.d(LOG, "IOException preparing MediaRecorder: " + e.getMessage());
			e.printStackTrace();
			releaseMediaRecorder();
			return false;
		}
		return true;
	}

	// Файл для сохранения видео
	private static File getOutputMediaFile(int type) {

		File mediaStorageDir = mContext.getExternalFilesDir(null);// путь /sdcard/Android/data/com.gromsoft.onlyroad/files/

		// Создать директорию если нет
		if (!mediaStorageDir.exists()) {
			if (!mediaStorageDir.mkdirs()) {
				Log.d(LOG, "ошибка при создании директории");
				return null;
			}
		}

		// Генерируем имя файла
		String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
		File mediaFile;
		if (type == MEDIA_TYPE_IMAGE) { // На всякий случай, может пригодится...
			mediaFile = new File(mediaStorageDir.getPath() + File.separator + "IMG_" + timeStamp + ".jpg");
		} else if (type == MEDIA_TYPE_VIDEO) {
			mediaFile = new File(mediaStorageDir.getPath() + File.separator + "VID_" + timeStamp + ".mp4");
		} else {
			return null;
		}

		return mediaFile;
	}

	// Отпустить MediaRecorder
	private void releaseMediaRecorder() {
		if (mMediaRecorder != null) {
			// 5.2. - Сбросить конфигурацию
			mMediaRecorder.reset();
			// 5.3. - Отпустить MediaRecorder
			mMediaRecorder.release();
			mMediaRecorder = null;// TODO mMediaRecorder=null а надо?
			// 5.4. - TODO заблокировать камеру что-то там было про Android 4.0
			mCamera.lock();
		}
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
//		inflater.inflate(R.menu.activity_nav, menu);
	
		
		
		super.onCreateOptionsMenu(menu, inflater);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == R.id.menu_record) {
			if (item.isChecked()) {
				// 4.5.1 - остановить запись
				mMediaRecorder.stop();
				releaseMediaRecorder();
				mCamera.lock();
			} else {
				if (prepareVideoRecorder()) {
					// 4.4. - Начать запись
					mMediaRecorder.start();

				} else {
					releaseMediaRecorder();
				}
			}
			item.setIcon(item.isChecked() ? R.drawable.record_off : R.drawable.record_on);
			item.setChecked(!item.isChecked());
			return true;
		}

		return super.onOptionsItemSelected(item);
	}

}
