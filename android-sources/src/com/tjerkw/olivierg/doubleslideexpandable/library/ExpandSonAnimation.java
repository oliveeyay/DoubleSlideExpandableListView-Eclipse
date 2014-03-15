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

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.TextView;

import com.tjerkw.olivierg.doubleslideexpandable.R;
import com.tjerkw.olivierg.doubleslideexpandable.TestObject;

/**
 * Animation that either expands or collapses a view by sliding it down to make it visible. Or by sliding it up so it will hide. It will
 * look like it slides behind the view above.
 * 
 * @author tjerk Modified by O. Goutay [a526588] for double ExpandableView implementation
 * @date 6/9/12 4:58 PM
 */
public class ExpandSonAnimation extends Animation {
	private View mAnimatedView;
	private int mEndHeight;
	private LinearLayout.LayoutParams mLayoutParams;
	private LayoutParams mLayoutParamsParent;
	private AbstractSlideExpandableListAdapter slideAdapter;

	private boolean animParent;

	private int position;

	private static View previousView;

	/**
	 * Initializes expand collapse animation, has two types, collapse (1) and expand (0).
	 * 
	 * @param view
	 *            The view to animate
	 * @param type
	 *            The type of animation: 0 will expand from gone and 0 size to visible and layout size defined in xml. 1 will collapse view
	 *            and set to gone
	 */
	public ExpandSonAnimation(View view, ListAdapter listAdapter, int position, AbstractSlideExpandableListAdapter slideAdapter,
			boolean animParent) {

		this.mAnimatedView = view;
		this.animParent = animParent;
		this.slideAdapter = slideAdapter;
		this.position = position;

		View listItem = listAdapter.getView(0, null, (ViewGroup) view.getParent());

		this.mLayoutParams = ((LinearLayout.LayoutParams) view.getLayoutParams());

		if (android.os.Build.VERSION.SDK_INT >= 11 || !animParent) {
			ExpandSonAnimation.initItem(view, position, (Activity) slideAdapter.getContext(), slideAdapter);
		}

		LinearLayout item = (LinearLayout) listItem.findViewById(R.id.expandable);
		item.measure(0, 0);
		this.mEndHeight = item.getMeasuredHeight();

		this.mLayoutParamsParent = ((LayoutParams) slideAdapter.findParentView(view).getLayoutParams());
		slideAdapter.setParentInitialHeight(mLayoutParamsParent.height);
		slideAdapter.addSavedParentHeight(mLayoutParamsParent.height);
		this.mLayoutParams.height = 0;

		slideAdapter.getViewHeights().put(position, 0);
		if (slideAdapter.getSavedParentInitialHeight() != slideAdapter.getParentInitialHeight()) {
			// save the current height to show on realloc
			slideAdapter.getViewHeights().put(position, mEndHeight);
		}

		view.setVisibility(View.VISIBLE);
	}

	@Override
	protected void applyTransformation(float interpolatedTime, Transformation t) {
		super.applyTransformation(interpolatedTime, t);
		if (interpolatedTime < 1.0f) {
			mLayoutParams.height = (int) (mEndHeight * interpolatedTime);
			if (animParent
					&& (slideAdapter.getSavedParentInitialHeight() == slideAdapter.getParentInitialHeight() || android.os.Build.VERSION.SDK_INT < 11)) {
				mLayoutParamsParent.height = slideAdapter.getParentInitialHeight() + mLayoutParams.height;
				// save the current height to show on realloc
				slideAdapter.getViewHeights().put(position, mLayoutParams.height);
			}
			mAnimatedView.requestLayout();
		} else {
			mLayoutParams.height = mEndHeight;
			if (animParent && (slideAdapter.getSavedParentInitialHeight() == slideAdapter.getParentInitialHeight())) {
				mLayoutParamsParent.height = slideAdapter.getParentInitialHeight() + mLayoutParams.height;
				// save the current height to show on realloc
				slideAdapter.getViewHeights().put(position, mLayoutParams.height);
			} else if (animParent && android.os.Build.VERSION.SDK_INT < 11) {
				mLayoutParamsParent.height = slideAdapter.getParentInitialHeight();
			}
			mAnimatedView.requestLayout();
		}
	}

	public static void initItem(View view, int position, Activity baseActivity, AbstractSlideExpandableListAdapter currentAbstract) {
		switch (AbstractSlideExpandableListAdapter.expandableType) {
		case TEST:
			initItemTest(view, position, baseActivity);
			break;
		default:
			break;
		}
	}

	public static void initItemTest(View view, int position, final Activity baseActivity) {
		if (view != previousView) {
			previousView = view;

			TestObject testObject = AbstractSlideExpandableListAdapter.getCurrentObject();

			TextView detail1 = (TextView) view.findViewById(R.id.detail1);
			TextView detail2 = (TextView) view.findViewById(R.id.detail2);
			TextView detail3 = (TextView) view.findViewById(R.id.detail3);
			TextView detail4 = (TextView) view.findViewById(R.id.detail4);

			detail1.setText(testObject.getDetail1());
			detail2.setText(testObject.getDetail2());
			detail3.setText(testObject.getDetail3());
			detail4.setText(testObject.getDetail4());
		}
	}
}
