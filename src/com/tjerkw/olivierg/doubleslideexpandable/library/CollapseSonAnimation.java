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

import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.LinearLayout;
import android.widget.ListAdapter;

import com.tjerkw.olivierg.doubleslideexpandable.R;

/**
 * Animation that either expands or collapses a view by sliding it down to make it visible. Or by sliding it up so it will hide. It will
 * look like it slides behind the view above.
 * 
 * @author tjerk Modified by O. Goutay [a526588] for double ExpandableView implementation
 * @date 6/9/12 4:58 PM
 */
public class CollapseSonAnimation extends Animation {
	private View mAnimatedView;
	private int mEndHeight;
	private LinearLayout.LayoutParams mLayoutParams;
	private LayoutParams mLayoutParamsParent;
	private int parentInitialHeight;
	private boolean moveParent;
	private AbstractSlideExpandableListAdapter slideAdapter;
	private int position;

	/**
	 * Initializes expand collapse animation, has two types, collapse (1) and expand (0).
	 * 
	 * @param view
	 *            The view to animate
	 * @param type
	 *            The type of animation: 0 will expand from gone and 0 size to visible and layout size defined in xml. 1 will collapse view
	 *            and set to gone
	 */
	public CollapseSonAnimation(View view, ListAdapter listAdapter, int position, AbstractSlideExpandableListAdapter slideAdapter,
			boolean moveParent) {

		this.mAnimatedView = view;
		this.moveParent = moveParent;
		this.slideAdapter = slideAdapter;
		this.position = position;

		slideAdapter.resetParentInitialHeight();

		View listItem = listAdapter.getView(0, null, (ViewGroup) view.getParent());

		// if there is no son
		LinearLayout item = (LinearLayout) listItem.findViewById(R.id.expandable);
		item.measure(0, 0);
		this.mEndHeight = item.getMeasuredHeight();

		if (!moveParent) {
			// save the current height to show on realloc
			slideAdapter.getViewHeights().put(position, mEndHeight);
		}

		this.mLayoutParamsParent = ((LayoutParams) slideAdapter.findParentView(view).getLayoutParams());
		this.parentInitialHeight = mLayoutParamsParent.height;

		this.mLayoutParams = ((LinearLayout.LayoutParams) view.getLayoutParams());
		this.mLayoutParams.height = mEndHeight;
		view.setVisibility(View.VISIBLE);
	}

	@Override
	protected void applyTransformation(float interpolatedTime, Transformation t) {
		super.applyTransformation(interpolatedTime, t);
		int interpolated = (int) (mEndHeight * interpolatedTime);
		if (interpolatedTime < 1.0f) {
			mLayoutParams.height = mEndHeight - interpolated;
			if (moveParent) {
				if (parentInitialHeight > slideAdapter.getSavedParentInitialHeight()) {
					mLayoutParamsParent.height = parentInitialHeight - interpolated;
				}
				// save the current height to show on realloc
				slideAdapter.getViewHeights().put(position, mLayoutParams.height);
			}
			mAnimatedView.requestLayout();
		} else {
			mLayoutParams.height = 0;
			if (moveParent) {
				if (parentInitialHeight > slideAdapter.getSavedParentInitialHeight()) {
					mLayoutParamsParent.height = parentInitialHeight - interpolated;
				}
				// save the current height to show on realloc
				slideAdapter.getViewHeights().put(position, 0);
			}
			mAnimatedView.setVisibility(View.GONE);
			mAnimatedView.requestLayout();
		}
	}
}
