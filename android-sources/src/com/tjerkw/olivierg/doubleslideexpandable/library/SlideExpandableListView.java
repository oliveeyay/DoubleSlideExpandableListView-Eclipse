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

import android.content.Context;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;

import java.util.concurrent.Callable;

/**
 * Simple subclass of listview which does nothing more than wrap any ListAdapter in a SlideExpandalbeListAdapter
 * 
 * @author tjerk Modified by O. Goutay [a526588] for double ExpandableView implementation
 */
class SlideExpandableListView extends ListView {
	private SlideExpandableListAdapter adapter;

	public SlideExpandableListView(Context context) {
		super(context);
	}

	public SlideExpandableListView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public SlideExpandableListView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	/**
	 * Collapses the currently open view.
	 * 
	 * @return true if a view was collapsed, false if there was no open view.
	 */
	public boolean collapse() {
		if (adapter != null) {
			return adapter.collapseLastOpen();
		}
		return false;
	}

	public void setAdapter(ListAdapter adapter, boolean isSecondLevel, Context context, Callable<Void> refreshView, boolean animParent,
			int position, boolean isSearch) {
		this.adapter = new SlideExpandableListAdapter(adapter, isSecondLevel, context, refreshView, animParent, position, isSearch);
		super.setAdapter(this.adapter);
	}

	/**
	 * Registers a OnItemClickListener for this listview which will expand the item by default. Any other OnItemClickListener will be
	 * overriden.
	 * 
	 * To undo call setOnItemClickListener(null)
	 * 
	 * Important: This method call setOnItemClickListener, so the value will be reset
	 */
	public void enableExpandOnItemClick() {
		this.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
				SlideExpandableListAdapter adapter = (SlideExpandableListAdapter) getAdapter();
				adapter.getExpandToggleButton(view).performClick();
			}
		});
	}

	@Override
	public Parcelable onSaveInstanceState() {
		try {
			return adapter.onSaveInstanceState(super.onSaveInstanceState());
		} catch (Exception e) {
			return null;
		}
	}

	@Override
	public void onRestoreInstanceState(Parcelable state) {
		if (!(state instanceof AbstractSlideExpandableListAdapter.SavedState)) {
			super.onRestoreInstanceState(state);
			return;
		}

		AbstractSlideExpandableListAdapter.SavedState ss = (AbstractSlideExpandableListAdapter.SavedState) state;
		super.onRestoreInstanceState(ss.getSuperState());

		adapter.onRestoreInstanceState(ss);
	}
}