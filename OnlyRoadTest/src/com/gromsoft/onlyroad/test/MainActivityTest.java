package com.gromsoft.onlyroad.test;

import android.app.Instrumentation;
import android.content.Context;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.test.ActivityInstrumentationTestCase2;
import android.test.suitebuilder.annotation.LargeTest;
import android.test.suitebuilder.annotation.SmallTest;

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

	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}

	@SmallTest
	public void testActionBarNavigationMode() {
		assertEquals("���� ������ ���� � ���", ActionBar.NAVIGATION_MODE_TABS, mActionBar.getNavigationMode());
	}

	@LargeTest
	public void testSpeakerphone() throws Exception {
		//�������� ���������� ��������
		CheckSpeakerSavedState(speakerMenuItem, "�� ������������");
		mSolo.clickOnImageButton(1);//��� (��� ����)
		mMainActivity.finish(); //�������������
		this.tearDown();
		this.setUp();
		CheckSpeakerSavedState(speakerMenuItem, "����� ������������");//��� ��� ���������
		
		
		//�������� ���������/����������
		CheckSpeakerSwitch(speakerMenuItem);//��������� ��������
		mSolo.clickOnImageButton(1);//��� (��� ����)
		CheckSpeakerSwitch(speakerMenuItem);
		mSolo.clickOnImageButton(1);//���� (��� ���)
		CheckSpeakerSwitch(speakerMenuItem);
	}

	private void CheckSpeakerSwitch(MenuItem item) {
		boolean isChecked = item.isChecked();
		boolean isSpeakerOn = mAudioManager.isSpeakerphoneOn();
		assertEquals("���� �������� " + String.valueOf(isChecked) + ", ������ ", isChecked, isSpeakerOn);
	}

	private void CheckSpeakerSavedState(MenuItem item, String test){
		boolean speaker_settings = settings.getBoolean("SpeakerPhone", false);
		boolean speaker_menu = item.isChecked();
		assertEquals(test+". ��������� �������� �������� "+String.valueOf(speaker_settings) + ", ���� ",speaker_settings,speaker_menu);
		boolean isSpeakerOn = mAudioManager.isSpeakerphoneOn();
		assertEquals(test+". ����������� ���������� � ���� ���������" + String.valueOf(speaker_settings) + " �� ������ �� � ���� ��������� ", speaker_settings, isSpeakerOn);
	}
	

}
