/*
This file is part of the project DoubleSlideExpandableListView, which is an Android
visual library under GPL license v3.
Copyright (C) 2013  Olivier Goutay

DoubleSlideExpandableListView is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

DoubleSlideExpandableListView is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with DoubleSlideExpandableListView.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.tjerkw.olivierg.doubleslideexpandable.library;

import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;

import java.util.Calendar;

public abstract class CustomExpandableOnTouchListener implements OnTouchListener {

	protected boolean performDownloadAfter = true;

	private final int MAX_CLICK_DURATION = 400;
	private final int MAX_CLICK_DISTANCE = 5;
	private long startClickTime;
	private float x1;
	private float y1;
	private float x2;
	private float y2;
	private float dx;
	private float dy;

	@Override
	public final boolean onTouch(View v, MotionEvent event) {
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN: {
			startClickTime = Calendar.getInstance().getTimeInMillis();
			x1 = event.getX();
			y1 = event.getY();
			break;
		}
		case MotionEvent.ACTION_UP: {
			long clickDuration = Calendar.getInstance().getTimeInMillis() - startClickTime;
			x2 = event.getX();
			y2 = event.getY();
			dx = x2 - x1;
			dy = y2 - y1;

			if (clickDuration < MAX_CLICK_DURATION && dx < MAX_CLICK_DISTANCE && dy < MAX_CLICK_DISTANCE) {
				onTouchClick(v);
			}
		}
		}

		return true;
	}

	public void onTouchClick(View v) {
		if (performDownloadAfter) {
			AbstractSlideExpandableListAdapter.downloadDatas = true;
		} else {
			AbstractSlideExpandableListAdapter.downloadDatas = false;
		}
		v.performClick();
		((View) v.getParent()).performClick();
	}
}
