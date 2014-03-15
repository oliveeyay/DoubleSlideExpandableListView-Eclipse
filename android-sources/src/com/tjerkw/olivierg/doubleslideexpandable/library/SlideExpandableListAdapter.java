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
import android.view.View;
import android.widget.ListAdapter;

import com.tjerkw.olivierg.doubleslideexpandable.R;

import java.util.concurrent.Callable;

/**
 * ListAdapter that adds sliding functionality to a list. Uses R.id.expandalbe_toggle_button and R.id.expandable id's if no ids are given in
 * the contructor.
 * 
 * @author tjerk Modified by O. Goutay [a526588] for double ExpandableView implementation
 * @date 6/13/12 8:04 AM
 */
public class SlideExpandableListAdapter extends AbstractSlideExpandableListAdapter {
	private int toggle_button_id;
	private int expandable_view_id;

	public SlideExpandableListAdapter(ListAdapter wrapped, int toggle_button_id, int expandable_view_id, boolean isSecondLevel,
			Context context, Callable<Void> refreshView, boolean animParent, int position, boolean isSearch) {
		super(wrapped, isSecondLevel, context, refreshView, animParent, position, isSearch);
		this.toggle_button_id = toggle_button_id;
		this.expandable_view_id = expandable_view_id;
	}

	public SlideExpandableListAdapter(ListAdapter wrapped, boolean isSecondLevel, Context context, Callable<Void> refreshView,
			boolean animParent, int position, boolean isSearch) {
		this(wrapped, R.id.expandable_toggle_button, R.id.expandable, isSecondLevel, context, refreshView, animParent, position, isSearch);
	}

	@Override
	public View getExpandToggleButton(View parent) {
		return parent.findViewById(toggle_button_id);
	}

	@Override
	public View getExpandableView(View parent) {
		return parent.findViewById(expandable_view_id);
	}
}
