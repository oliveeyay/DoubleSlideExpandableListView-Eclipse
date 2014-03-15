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

package com.tjerkw.olivierg.doubleslideexpandable;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.tjerkw.olivierg.doubleslideexpandable.library.AbstractSlideExpandableListAdapter;
import com.tjerkw.olivierg.doubleslideexpandable.library.AbstractViewHolder;
import com.tjerkw.olivierg.doubleslideexpandable.library.ActionSlideExpandableListView;
import com.tjerkw.olivierg.doubleslideexpandable.library.CustomExpandableOnTouchListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

public class MainActivity extends Activity {

	private ActionSlideExpandableListView exampleList;
	public static ExampleAdapter mainAdapter;

	public static int lastParentPositionClicked = -1;
	public static int lastSonPositionClicked = -1;

	private List<String> activities;

	private Map<String, List<TestObject>> testObjects;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		exampleList = (ActionSlideExpandableListView) findViewById(R.id.exampleList);
		initView();
	}

	private void initView() {
		// init objects
		initDummyObjects();

		// init category list
		mainAdapter = new ExampleAdapter(getApplicationContext());
		exampleList.setAdapter(mainAdapter, false, this, null, true, 0, false);
	}

	public class ExampleAdapter extends BaseAdapter {

		private Context context;
		public SparseArray<ExampleSecondLevelListAdapter> secondLevelListAdapters;
		public ProgressBar progressBarParent;
		public SparseArray<View> views;

		public ExampleAdapter(Context context) {
			this.context = context;
			secondLevelListAdapters = new SparseArray<MainActivity.ExampleSecondLevelListAdapter>();
			views = new SparseArray<View>();
		}

		@Override
		public int getCount() {
			return activities.size();
		}

		@Override
		public Object getItem(int arg0) {
			return activities.get(arg0);
		}

		@Override
		public long getItemId(int arg0) {
			return arg0;
		}

		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
			ViewHolder holder;

			if (views.indexOfKey(position) < 0) {
				convertView = LayoutInflater.from(context).inflate(R.layout.item_list_expandable, null);

				// Set up the ViewHolder.
				holder = new ViewHolder();
				holder.text = (TextView) convertView.findViewById(R.id.text);
				holder.slideExpandableListView = (ActionSlideExpandableListView) convertView.findViewById(R.id.listSecondLevel);
				holder.progressBarParent = (ProgressBar) convertView.findViewById(R.id.progressBarExpandable);

				// Store the holder with the view.
				convertView.setTag(holder);

				// Assign values
				holder.text.setText(activities.get(position));

				if (position % 2 == 1) {
					convertView.setBackgroundResource(R.drawable.table_odd_lines);
				} else {
					convertView.setBackgroundResource(R.drawable.table_pair_lines);
				}

				final ExampleSecondLevelListAdapter secondLevelListAdapter = new ExampleSecondLevelListAdapter(testObjects.get(activities
						.get(position)), activities.get(position));

				Callable<Void> refreshSon = new Callable<Void>() {
					@Override
					public Void call() throws Exception {
						// todo if you want to refresh something
						return null;
					}
				};

				holder.slideExpandableListView.setAdapter(secondLevelListAdapter, true, MainActivity.this, refreshSon, true, position,
						false);
				secondLevelListAdapters.put(position, secondLevelListAdapter);

			} else {
				convertView = views.get(position);
				holder = (ViewHolder) convertView.getTag();
			}

			final ViewHolder finalHolder = holder;
			convertView.setOnTouchListener(new CustomExpandableOnTouchListener() {
				@Override
				public void onTouchClick(View v) {
					finalHolder.progressBarParent.setVisibility(View.VISIBLE);
					lastParentPositionClicked = position;
					AbstractSlideExpandableListAdapter.setCurrentSelectedActivity(activities.get(position));
					AbstractSlideExpandableListAdapter.setNbCellToAnim(testObjects.get(activities.get(position)).size());
					progressBarParent = finalHolder.progressBarParent;
					if (android.os.Build.VERSION.SDK_INT >= 11) {
						if (secondLevelListAdapters.get(position) != null) {
							secondLevelListAdapters.get(position).resetLastSelection();
						}
					}

					performDownloadAfter = false;
					super.onTouchClick(v);
				}
			});

			return convertView;
		}

		class ViewHolder extends AbstractViewHolder {
			TextView text;
			ProgressBar progressBarParent;
			ActionSlideExpandableListView slideExpandableListView;
		}
	}

	public class ExampleSecondLevelListAdapter extends BaseAdapter {

		public List<TestObject> testObjects;
		private ViewHolder previousView;
		private int clicOnSameView = 0;
		public boolean isSearch = false;

		public ExampleSecondLevelListAdapter(List<TestObject> testObjects, String currentActivity) {
			this.testObjects = testObjects;
		}

		@Override
		public int getCount() {
			return testObjects.size();
		}

		@Override
		public Object getItem(int arg0) {
			return testObjects.get(arg0);
		}

		@Override
		public long getItemId(int arg0) {
			return arg0;
		}

		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
			ViewHolder holder;

			if (convertView == null) {
				convertView = LayoutInflater.from(MainActivity.this).inflate(R.layout.item_list_expandable_second_level, null);

				// Set up the ViewHolder.
				holder = new ViewHolder();
				final TextView textView = (TextView) convertView.findViewById(R.id.text);
				holder.text = textView;
				holder.progressBarSon = (ProgressBar) convertView.findViewById(R.id.progressBarExpandable);

				holder.view = convertView.findViewById(R.id.item);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}

			// Assign values
			holder.text.setText(testObjects.get(position).getText());

			final ViewHolder finalHolder = holder;
			finalHolder.view.setOnTouchListener(new CustomExpandableOnTouchListener() {
				@Override
				public void onTouchClick(View v) {
					lastSonPositionClicked = position;
					AbstractSlideExpandableListAdapter.setCurrentObject(testObjects.get(position));
					if (previousView != null) {
						previousView.progressBarSon.setVisibility(View.GONE);
					}
					if (previousView != finalHolder || clicOnSameView % 2 == 1) {
						if (previousView != finalHolder) {
							clicOnSameView = 0;
						} else {
							clicOnSameView += 1;
						}
						previousView = finalHolder;
						previousView.progressBarSon.setVisibility(View.VISIBLE);
					} else {
						clicOnSameView += 1;
					}

					performDownloadAfter = testObjects.indexOf(lastSonPositionClicked) < 0 ? true : false;
					super.onTouchClick(v);
				}
			});
			return convertView;
		}

		class ViewHolder extends AbstractViewHolder {
			TextView text;
			ProgressBar progressBarSon;
			View view;
		}

		public void resetLastSelection() {
			if (previousView != null) {
				previousView.progressBarSon.setVisibility(View.GONE);
			}
			previousView = null;
		}

		public ViewHolder getPreviousView() {
			return previousView;
		}

	}

	private void initDummyObjects() {
		activities = new ArrayList<String>();
		activities.add("Sports");
		activities.add("Cinema");
		activities.add("Television");
		activities.add("Work");

		testObjects = new HashMap<String, List<TestObject>>();
		ArrayList<TestObject> tempList = new ArrayList<TestObject>();
		tempList.add(new TestObject("Football", "Foot", "Ball", "11 players", "Test"));
		tempList.add(new TestObject("Rugby", "Hand", "Ball", "15 players", "Test2"));
		tempList.add(new TestObject("Handball", "Hand", "Ball", "6 players", "Test3"));
		tempList.add(new TestObject("Pool", "Pool", "Water", "1 players", "Test4"));
		testObjects.put(activities.get(0), tempList);

		tempList = new ArrayList<TestObject>();
		tempList.add(new TestObject("Action", "Boum boum", "Hungry guys", "Dead", "Test"));
		tempList.add(new TestObject("Comedy", "Funny", "Love", "People's life", "Test2"));
		tempList.add(new TestObject("Drama", "Sad", "Very Sad", "Cry me a river", "Test3"));
		tempList.add(new TestObject("Horror", "Fear", "Monster", "All dead", "Test4"));
		testObjects.put(activities.get(1), tempList);

		tempList = new ArrayList<TestObject>();
		tempList.add(new TestObject("TV shows", "Dexter", "Come on", "A woodcutter ?", "Test"));
		tempList.add(new TestObject("Movies", "Terminator", "is terminated", "are you ?", "Test2"));
		tempList.add(new TestObject("Cartoon", "Mickey", "Mouse", "Not the one in your hand", "Test3"));
		tempList.add(new TestObject("Documentary", "Fishing", "Fish", "A lot of word beginning by fish", "Test4"));
		testObjects.put(activities.get(2), tempList);

		tempList = new ArrayList<TestObject>();
		tempList.add(new TestObject("Boss", "Boring and strict", "Or fun and open", "good luck", "Test"));
		tempList.add(new TestObject("Fun", "Ping pong contest", "Momentum of ball in head", "Let's start", "Test2"));
		tempList.add(new TestObject("Computer", "Passion", "Innovation", "Always in the move", "Test3"));
		tempList.add(new TestObject("Cool stuffs", "This", "Awesome", "Library", "Test4"));
		testObjects.put(activities.get(3), tempList);
	}
}
