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
import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;
import android.util.SparseIntArray;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ProgressBar;

import com.tjerkw.olivierg.doubleslideexpandable.R;
import com.tjerkw.olivierg.doubleslideexpandable.TestObject;

import java.util.BitSet;
import java.util.concurrent.Callable;

/**
 * Wraps a ListAdapter to give it expandable list view functionality. The main thing it does is add a listener to the getToggleButton which
 * expands the getExpandableView for each list item.
 * 
 * @author tjerk Modified by O. Goutay [a526588] for double ExpandableView implementation
 * @date 6/9/12 4:41 PM
 */
public abstract class AbstractSlideExpandableListAdapter extends WrapperListAdapterImpl {

	public final static int COLLAPSE = 1;
	public final static int EXPAND = 0;
	protected boolean isSecondLevel = false;

	public static ExpandableType expandableType = ExpandableType.TEST;

	private Integer parentInitialHeight;
	private SparseIntArray savedParentInitialHeight;

	private int currentParentPosition;

	private static String currentSelectedActivity;

	private static TestObject currentObject;

	/**
	 * Reference to the last expanded list item. Since lists are recycled this might be null if though there is an expanded list item
	 */
	private View lastOpen = null;
	/**
	 * The position of the last expanded list item. If -1 there is no list item expanded. Otherwise it points to the position of the last
	 * expanded list item
	 */
	private int lastOpenPosition = -1;
	private int lastLastOpenPosition = -1;

	/**
	 * Default Animation duration Set animation duration with @see setAnimationDuration
	 */
	private int animationDuration = 350;
	private int animationDurationLong = 500;
	private int animationDurationUltraLong = 2500;
	private int animationDurationSon = 400;

	private static int nbCellToAnim;
	private static final int nbCellLongAnim = 4;
	private static final int nbCellNoAnim = 30;

	public static boolean downloadDatas = false;

	/**
	 * A list of positions of all list items that are expanded. Normally only one is expanded. But a mode to expand multiple will be added
	 * soon.
	 * 
	 * If an item onj position x is open, its bit is set
	 */
	private BitSet openItems = new BitSet();
	/**
	 * We remember, for each collapsable view its height. So we dont need to recalculate. The height is calculated just before the view is
	 * drawn.
	 */
	private final SparseIntArray viewHeights = new SparseIntArray(10);

	public AbstractSlideExpandableListAdapter(ListAdapter wrapped, boolean isSecondLevel, Context context, Callable<Void> refreshView,
			boolean animParent, int position, boolean isSearch) {
		super(wrapped, context, refreshView, animParent, position, isSearch);
		this.isSecondLevel = isSecondLevel;

		if (savedParentInitialHeight == null) {
			savedParentInitialHeight = new SparseIntArray();
		}
	}

	@Override
	public View getView(int position, View view, ViewGroup viewGroup) {
		view = wrapped.getView(position, view, viewGroup);
		if (view != null) {
			enableFor(view, position);
		}
		return view;
	}

	/**
	 * This method is used to get the Button view that should expand or collapse the Expandable View. <br/>
	 * Normally it will be implemented as:
	 * 
	 * <pre>
	 * return parent.findViewById(R.id.expand_toggle_button)
	 * </pre>
	 * 
	 * A listener will be attached to the button which will either expand or collapse the expandable view
	 * 
	 * @see #getExpandableView(View)
	 * @param parent
	 *            the list view item
	 * @ensure return!=null
	 * @return a child of parent which is a button
	 */
	public abstract View getExpandToggleButton(View parent);

	/**
	 * This method is used to get the view that will be hidden initially and expands or collapse when the ExpandToggleButton is pressed @see
	 * getExpandToggleButton <br/>
	 * Normally it will be implemented as:
	 * 
	 * <pre>
	 * return parent.findViewById(R.id.expandable)
	 * </pre>
	 * 
	 * @see #getExpandToggleButton(View)
	 * @param parent
	 *            the list view item
	 * @ensure return!=null
	 * @return a child of parent which is a view (or often ViewGroup) that can be collapsed and expanded
	 */
	public abstract View getExpandableView(View parent);

	/**
	 * Gets the duration of the collapse animation in ms. Default is 330ms. Override this method to change the default.
	 * 
	 * @return the duration of the anim in ms
	 */
	public int getAnimationDuration() {
		if (isSecondLevel) {
			return animationDurationSon;
		} else {
			if (nbCellToAnim > nbCellNoAnim) {
				return animationDurationUltraLong;
			} else if (nbCellToAnim > nbCellLongAnim) {
				return animationDurationLong;
			} else {
				return animationDuration;
			}
		}
	}

	/**
	 * Set's the Animation duration for the Expandable animation
	 * 
	 * @param duration
	 *            The duration as an integer in MS (duration > 0)
	 * @exception IllegalArgumentException
	 *                if parameter is less than zero
	 */
	public void setAnimationDuration(int duration) {
		if (duration < 0) {
			throw new IllegalArgumentException("Duration is less than zero");
		}

		animationDuration = duration;
	}

	/**
	 * Check's if any position is currently Expanded To collapse the open item @see collapseLastOpen
	 * 
	 * @return boolean True if there is currently an item expanded, otherwise false
	 */
	public boolean isAnyItemExpanded() {
		return (lastOpenPosition != -1) ? true : false;
	}

	public void enableFor(View parent, int position) {
		AbstractViewHolder holder = (AbstractViewHolder) parent.getTag();

		if (holder.more == null || holder.itemToolbar == null) {
			holder.more = getExpandToggleButton(parent);
			holder.itemToolbar = getExpandableView(parent);
			holder.itemToolbar.measure(parent.getWidth(), parent.getHeight());
		}

		enableFor(holder.more, holder.itemToolbar, position);
	}

	private void enableFor(final View button, final View target, final int position) {
		// Enable this if you want items to be closed on realloc
		// if (!isSecondLevel) {
		// openItems.set(position, false);
		// }

		if (position == lastLastOpenPosition && target.getParent().getParent() != null) {
			// re reference to the last view
			// so when can animate it when collapsed
			lastOpen = target;
		}
		if (isSecondLevel) {
			updateExpandableSecondLevel(target, position);
		} else {
			updateExpandable(target, position);
		}

		button.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(final View view) {
				final int type = target.getVisibility() == View.VISIBLE ? COLLAPSE : EXPAND;

				if (type == COLLAPSE) {
					if (lastOpenPosition == position) {
						lastLastOpenPosition = lastOpenPosition;
						lastOpenPosition = -1;
					}
					animateView(target, type, position);
				} else {
					final Callable<Void> callableSuccess = new Callable<Void>() {
						@Override
						public Void call() throws Exception {
							Animation a = target.getAnimation();

							if (a != null && a.hasStarted() && !a.hasEnded()) {

								a.setAnimationListener(new Animation.AnimationListener() {
									@Override
									public void onAnimationStart(Animation animation) {
									}

									@Override
									public void onAnimationEnd(Animation animation) {
										view.performClick();
									}

									@Override
									public void onAnimationRepeat(Animation animation) {
									}
								});

							} else {
								target.setAnimation(null);

								// remember the state
								if (type == EXPAND) {
									openItems.set(position, true);
								}

								// check if we need to collapse a different view
								if (type == EXPAND) {
									if (lastOpenPosition != -1 && lastOpenPosition != position) {
										if (lastOpen != null) {
											if (android.os.Build.VERSION.SDK_INT >= 11 || !isSecondLevel || !animParent) {
												animateView(lastOpen, COLLAPSE, lastOpenPosition);
												openItems.set(lastOpenPosition, false);
											} else {
												collapseLastOpen();
											}
										} else {
											openItems.set(lastOpenPosition, true);
										}
									}
									lastOpen = target;
									lastLastOpenPosition = lastOpenPosition;
									lastOpenPosition = position;
								} else if (lastOpenPosition == position) {
									lastLastOpenPosition = lastOpenPosition;
									lastOpenPosition = -1;
								}
								animateView(target, type, position);
							}
							return null;
						}
					};

					// DO what you want before calling callable (download datas etc...)

					try {
						callableSuccess.call();
					} catch (Exception e) {
						Log.e("Error", e.toString());
					}
				}
			}
		});
	}

	private void updateExpandable(final View target, final int position) {
		final LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) target.getLayoutParams();
		if (openItems.get(position) && position != lastLastOpenPosition) {
			target.setVisibility(View.VISIBLE);
			params.height = viewHeights.get(position);
		} else {
			if (position == lastLastOpenPosition && lastLastOpenPosition != lastOpenPosition && openItems.get(position)) {
				openItems.set(position, false);
			} else {
				params.height = 0;
				target.setVisibility(View.GONE);
			}
		}
	}

	private void updateExpandableSecondLevel(final View target, final int position) {
		final LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) target.getLayoutParams();
		if (openItems.get(position) && position != lastLastOpenPosition) {
			target.setVisibility(View.VISIBLE);
			params.height = viewHeights.get(position);

			if (android.os.Build.VERSION.SDK_INT < 11) {
				View view = (View) target.getParent();

				// init at the end for android 2.3, graphical glitch either
				if (currentObject != null) {
					ExpandSonAnimation.initItem((View) target.getParent(), position, (Activity) context, this);
				}
			}
		} else {
			if (position == lastLastOpenPosition && lastLastOpenPosition != lastOpenPosition && openItems.get(position)) {
				openItems.set(position, false);
			} else {
				params.height = 0;
				target.setVisibility(View.GONE);
			}
			if (android.os.Build.VERSION.SDK_INT < 11) {
				View view = (View) target.getParent();

				ProgressBar progressBarSon = (ProgressBar) view.findViewById(R.id.progressBarExpandable);
				progressBarSon.setVisibility(View.GONE);
			}
		}
	}

	public View findParentView(View view) {
		boolean notFound = true;
		int i = 0;

		while (notFound && i < 10) {
			i++;
			if (view.getParent() == null) {
				notFound = false;
			} else if (view.getParent() instanceof ActionSlideExpandableListView) {
				view = (View) view.getParent().getParent();
				notFound = false;
			} else {
				view = (View) view.getParent();
			}
		}

		return view;
	}

	public SparseIntArray getViewHeights() {
		return viewHeights;
	}

	public int getLastOpenPosition() {
		return lastOpenPosition;
	}

	/**
	 * Performs either COLLAPSE or EXPAND animation on the target view
	 * 
	 * @param target
	 *            the view to animate
	 * @param type
	 *            the animation type, either ExpandCollapseAnimation.COLLAPSE or ExpandCollapseAnimation.EXPAND
	 */
	private void animateView(final View target, final int type, final int position) {
		Animation anim = null;
		if (type == EXPAND) {
			if (isSecondLevel) {
				anim = new ExpandSonAnimation(target, wrapped, position, this, animParent);
			} else {
				currentParentPosition = position;
				anim = new ExpandAnimation(target, wrapped, position, this);
			}
		} else {
			if (isSecondLevel) {
				anim = new CollapseSonAnimation(target, wrapped, position, this, (position == lastLastOpenPosition));
				lastLastOpenPosition = -1;
			} else {
				anim = new CollapseAnimation(target, wrapped, position, this);
			}
		}
		final Animation tempAnim = anim;
		// Collapse last son if on a parent
		anim.setAnimationListener(new AnimationListener() {
			@Override
			public void onAnimationStart(Animation arg0) {
			}

			@Override
			public void onAnimationRepeat(Animation arg0) {
			}

			@Override
			public void onAnimationEnd(Animation arg0) {
				if (tempAnim instanceof CollapseAnimation) {
					ActionSlideExpandableListView sonListView = (ActionSlideExpandableListView) target.findViewById(R.id.listSecondLevel);
					sonListView.collapse();
				}

				ProgressBar progressBarSon = (ProgressBar) ((View) target.getParent()).findViewById(R.id.progressBarExpandable);
				if (progressBarSon != null) {
					progressBarSon.setVisibility(View.GONE);
				}

				if (type == COLLAPSE) {
					openItems.set(position, false);
				}

				if (android.os.Build.VERSION.SDK_INT < 11) {
					target.getParent().getParent().requestLayout();
				}
			}
		});

		anim.setDuration(getAnimationDuration());
		target.startAnimation(anim);
	}

	/**
	 * Closes the current open item. If it is current visible it will be closed with an animation.
	 * 
	 * @return true if an item was closed, false otherwise
	 */
	public boolean collapseLastOpen() {
		if (isAnyItemExpanded()) {
			// if visible close it out
			if (lastOpen != null) {
				// animateView(lastOpen, COLLAPSE, lastOpenPosition);
				final LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) lastOpen.getLayoutParams();
				viewHeights.put(lastOpenPosition, 0);
				params.height = 0;
				if (isSecondLevel) {
					((View) lastOpen.getParent()).requestLayout();
				}
			}
			openItems.set(lastOpenPosition, false);
			lastOpenPosition = -1;
			lastOpen.setVisibility(View.GONE);
			return true;
		}
		return false;
	}

	public Parcelable onSaveInstanceState(Parcelable parcelable) {

		SavedState ss = new SavedState(parcelable);
		ss.lastOpenPosition = this.lastOpenPosition;
		ss.openItems = this.openItems;
		return ss;
	}

	public void onRestoreInstanceState(SavedState state) {

		this.lastOpenPosition = state.lastOpenPosition;
		this.openItems = state.openItems;
	}

	public static TestObject getCurrentObject() {
		return currentObject;
	}

	public static void setCurrentObject(TestObject currentObject) {
		AbstractSlideExpandableListAdapter.currentObject = currentObject;
	}

	public static String getCurrentSelectedActivity() {
		return currentSelectedActivity;
	}

	public static void setCurrentSelectedActivity(String currentSelectedActivity) {
		AbstractSlideExpandableListAdapter.currentSelectedActivity = currentSelectedActivity;
	}

	public int getCurrentParentPosition() {
		return currentParentPosition;
	}

	public void setCurrentParentPosition(int currentParentPosition) {
		this.currentParentPosition = currentParentPosition;
	}

	/**
	 * Utility methods to read and write a bitset from and to a Parcel
	 */
	private static BitSet readBitSet(Parcel src) {
		int cardinality = src.readInt();

		BitSet set = new BitSet();
		for (int i = 0; i < cardinality; i++) {
			set.set(src.readInt());
		}

		return set;
	}

	private static void writeBitSet(Parcel dest, BitSet set) {
		int nextSetBit = -1;

		dest.writeInt(set.cardinality());

		while ((nextSetBit = set.nextSetBit(nextSetBit + 1)) != -1) {
			dest.writeInt(nextSetBit);
		}
	}

	/**
	 * The actual state class
	 */
	static class SavedState extends View.BaseSavedState {
		public BitSet openItems = null;
		public int lastOpenPosition = -1;

		SavedState(Parcelable superState) {
			super(superState);
		}

		private SavedState(Parcel in) {
			super(in);
			in.writeInt(lastOpenPosition);
			writeBitSet(in, openItems);
		}

		@Override
		public void writeToParcel(Parcel out, int flags) {
			super.writeToParcel(out, flags);
			lastOpenPosition = out.readInt();
			openItems = readBitSet(out);
		}

		// required field that makes Parcelables from a Parcel
		public static final Parcelable.Creator<SavedState> CREATOR = new Parcelable.Creator<SavedState>() {
			public SavedState createFromParcel(Parcel in) {
				return new SavedState(in);
			}

			public SavedState[] newArray(int size) {
				return new SavedState[size];
			}
		};
	}

	public static void setNbCellToAnim(int nbCellToAnim) {
		AbstractSlideExpandableListAdapter.nbCellToAnim = nbCellToAnim;
	}

	public int getSavedParentInitialHeight() {
		if (savedParentInitialHeight != null && !(savedParentInitialHeight.indexOfKey(position) < 0)) {
			return savedParentInitialHeight.get(position);
		} else {
			return 0;
		}
	}

	public void addSavedParentHeight(int initialHeight) {
		if (savedParentInitialHeight.indexOfKey(position) < 0) {
			savedParentInitialHeight.put(position, initialHeight);
		}
	}

	public int getParentInitialHeight() {
		return parentInitialHeight;
	}

	public void resetParentInitialHeight() {
		parentInitialHeight = -1;
	}

	public void setParentInitialHeight(Integer parentInitialHeight) {
		this.parentInitialHeight = parentInitialHeight;
	}

}
