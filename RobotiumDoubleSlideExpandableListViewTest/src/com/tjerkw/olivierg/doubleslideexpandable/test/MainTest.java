package com.tjerkw.olivierg.doubleslideexpandable.test;

import com.robotium.solo.Solo;
import com.tjerkw.olivierg.doubleslideexpandable.MainActivity;

import android.test.ActivityInstrumentationTestCase2;
import android.widget.TextView;

public class MainTest extends ActivityInstrumentationTestCase2<MainActivity> {

	private Solo solo;

	public MainTest() {
		super(MainActivity.class);
	}

	public void setUp() throws Exception {
		solo = new Solo(getInstrumentation(), getActivity());
	}

	public void testPreferenceIsSaved() throws Exception {
		//Expand the first list
		solo.clickOnText("Sports");
		waitForLoading();
		assertTrue(solo.searchText("Football", true));
		
		//Expand the first list second son
		solo.clickOnText("Football");
		waitForLoading();
		assertTrue(solo.searchText("Foot", true));
		
		//Expand another list second son
		solo.clickOnText("Handball");
		waitForLoading();
		assertTrue(solo.searchText("Hand", true));
		
		//Expand the last list
		solo.clickOnText("Work");
		waitForLoading();
		assertTrue(solo.searchText("Boss", true));
	}
	
	private void waitForLoading(){
		solo.sleep(1000);
	}

	@Override
	public void tearDown() throws Exception {
		solo.finishOpenedActivities();
	}

}
