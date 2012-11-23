package com.gromsoft.onlyroad.test;

import android.app.Instrumentation;
import android.content.Context;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.os.Bundle;
import android.test.ActivityInstrumentationTestCase2;
import android.test.suitebuilder.annotation.LargeTest;
import android.test.suitebuilder.annotation.SmallTest;
import android.util.Log;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.gromsoft.onlyroad.MainActivity;
import com.jayway.android.robotium.solo.Solo;

public class MainActivityTest extends ActivityInstrumentationTestCase2<MainActivity> {

	MainActivity mMainActivity;
	Instrumentation mInstrumentation;
	com.actionbarsherlock.app.ActionBar mActionBar;
	Solo mSolo;
	AudioManager mAudioManager;
	Menu mMenu;
	Bundle mBundle;
	SharedPreferences settings;
	MenuItem speakerMenuItem;
	boolean isPhoneSpeakeOn;
	final static String LOG = "MyLog";
	
	
	public MainActivityTest(String name) {
		super(MainActivity.class);
	}

	public MainActivityTest() {
		this("Only Road MainActivity Tests");
	}

	protected void setUp() throws Exception {

		super.setUp();
		setActivityInitialTouchMode(false);
		mMainActivity = getActivity();
		mInstrumentation = getInstrumentation();
		mSolo = new Solo(mInstrumentation, mMainActivity);
		mActionBar = mMainActivity.getSupportActionBar();
		mAudioManager = (AudioManager) mMainActivity.getSystemService(Context.AUDIO_SERVICE);
		settings = mMainActivity.getPreferences(0);
		mMenu = mMainActivity.mMenu;
		speakerMenuItem = mMenu.findItem(com.gromsoft.onlyroad.R.id.speaker);
		mSolo.setActivityOrientation(Solo.LANDSCAPE);// �������� ���������� ����������

	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}

	@SmallTest
	public void testActionBarNavigationMode() {
		mSolo.setActivityOrientation(Solo.LANDSCAPE);
		assertEquals("Landscape. ���� ������ ���� � ���", ActionBar.NAVIGATION_MODE_TABS, mActionBar.getNavigationMode());
		mSolo.setActivityOrientation(Solo.PORTRAIT);
		assertEquals("Portrait. ���� ������ ���� � ���", ActionBar.NAVIGATION_MODE_TABS, mActionBar.getNavigationMode());	
		mSolo.setActivityOrientation(Solo.LANDSCAPE);
	}

	@SmallTest
	public void testSavedState() throws Exception {
		// �������� ������������ ��������
		CheckSpeakerSavedState("�� ������������");

		// ����������� � �������������
		mSolo.clickOnImageButton(1);
		mMainActivity.finish();
		this.tearDown();
		this.setUp();

		//�������� ����� ������������
		CheckSpeakerSavedState("����� ������������");
	}

	private void CheckSpeakerSavedState(String test) {

		boolean speaker_settings = settings.getBoolean("SpeakerPhone", false);
		boolean speaker_menu = speakerMenuItem.isChecked();
		boolean isSpeakerOn = mAudioManager.isSpeakerphoneOn();

		assertEquals(test + ". ��������� �������� �������� " + String.valueOf(speaker_settings) + ", ���� ", speaker_settings, speaker_menu);
		assertEquals(test + ". �� ������������ �������, � ���� " + String.valueOf(speaker_settings), speaker_settings, isSpeakerOn);
		

	}

	@LargeTest
	public void testSpeakerphone() throws Exception {

		// ---- LANDSCAPE ------
		mSolo.setActivityOrientation(Solo.LANDSCAPE);
		// �������� ���������/����������
		Log.d(LOG, "testSpeakerphone ��������� ��������");
		CheckSpeakerState(speakerMenuItem);// ��������� ��������
		mSolo.clickOnImageButton(1);// ��� (��� ����)
		Log.d(LOG, "testSpeakerphone ������ ���");
		CheckSpeakerState(speakerMenuItem);
		mSolo.clickOnImageButton(1);// ���� (��� ���)
		Log.d(LOG, "testSpeakerphone ������ ���");
		CheckSpeakerState(speakerMenuItem);

		
		// ---- PORTRAIT ----------
		mSolo.setActivityOrientation(Solo.PORTRAIT);
		Log.d(LOG, "testSpeakerphone ����������");
		// �������� ���������/����������
		CheckSpeakerState(speakerMenuItem);// ��������� ��������
		mSolo.clickOnImageButton(1);// ��� (��� ����)
//		mSolo.waitForActivity("mMainActivity");
//		Log.d(LOG, "testSpeakerphone ������ ���");
//		CheckSpeakerState(speakerMenuItem);
//		mSolo.clickOnImageButton(1);// ���� (��� ���)
//		Log.d(LOG, "testSpeakerphone ������ ������");
//		CheckSpeakerState(speakerMenuItem);
		mSolo.setActivityOrientation(Solo.LANDSCAPE);

	}

	private void CheckSpeakerState(MenuItem item) {
		boolean isChecked = item.isChecked();
		boolean isSpeakerOn = mAudioManager.isSpeakerphoneOn();
		
		Log.d(LOG, "Test isChecked=" + String.valueOf(isChecked));
		Log.d(LOG, "Test isSpeakerOn=" + String.valueOf(isSpeakerOn));
		
		assertEquals("���� �������� " + String.valueOf(isChecked) + ", ������ ", isChecked, isSpeakerOn);
	}

}
