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
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;

import java.util.concurrent.Callable;

/**
 * A more specific expandable listview in which the expandable area consist of some buttons which are context actions for the item itself.
 * 
 * It handles event binding for those buttons and allow for adding a listener that will be invoked if one of those buttons are pressed.
 * 
 * @author tjerk Modified by O. Goutay [a526588] for double ExpandableView implementation
 * @date 6/26/12 7:01 PM
 */
public class ActionSlideExpandableListView extends SlideExpandableListView {
	private OnActionClickListener listener;
	private int[] buttonIds = null;
	private WrapperListAdapterImpl wrapperListAdapterImpl;

	public ActionSlideExpandableListView(Context context) {
		super(context);
	}

	public ActionSlideExpandableListView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public ActionSlideExpandableListView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public void setItemActionListener(OnActionClickListener listener, int... buttonIds) {
		this.listener = listener;
		this.buttonIds = buttonIds;
	}

	/**
	 * Interface for callback to be invoked whenever an action is clicked in the expandle area of the list item.
	 */
	public interface OnActionClickListener {
		/**
		 * Called when an action item is clicked.
		 * 
		 * @param itemView
		 *            the view of the list item
		 * @param clickedView
		 *            the view clicked
		 * @param position
		 *            the position in the listview
		 */
		public void onClick(View itemView, View clickedView, int position);
	}

	public void setAdapter(ListAdapter adapter, boolean isSecondLevel, Context context, Callable<Void> refreshView, boolean animParent,
			int position, boolean isSearch) {
		wrapperListAdapterImpl = new WrapperListAdapterImpl(adapter, context, refreshView, animParent, position, isSearch) {

			@Override
			public View getView(final int position, View view, ViewGroup viewGroup) {
				final View listView = wrapped.getView(position, view, viewGroup);
				// add the action listeners
				if (buttonIds != null && listView != null) {
					for (int id : buttonIds) {
						View buttonView = listView.findViewById(id);
						if (buttonView != null) {
							buttonView.findViewById(id).setOnClickListener(new OnClickListener() {
								@Override
								public void onClick(View view) {
									if (listener != null) {
										listener.onClick(listView, view, position);
									}
								}
							});
						}
					}
				}
				return listView;
			}
		};
		super.setAdapter(wrapperListAdapterImpl, isSecondLevel, context, refreshView, animParent, position, isSearch);
	}

	public WrapperListAdapterImpl getWrapperListAdapterImpl() {
		return wrapperListAdapterImpl;
	}

}
