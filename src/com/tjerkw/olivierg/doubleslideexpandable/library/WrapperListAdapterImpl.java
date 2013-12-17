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
import android.database.DataSetObserver;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;
import android.widget.WrapperListAdapter;

import java.util.concurrent.Callable;

/**
 * Implementation of a WrapperListAdapter interface in which method delegates to the wrapped adapter.
 * 
 * Extend this class if you only want to change a few methods of the wrapped adapter.
 * 
 * The wrapped adapter is available to subclasses as the "wrapped" field.
 * 
 * @author tjerk Modified by O. Goutay [a526588] for double ExpandableView implementation
 * @date 6/9/12 4:41 PM
 */
public abstract class WrapperListAdapterImpl extends BaseAdapter implements WrapperListAdapter {
	protected ListAdapter wrapped;
	protected boolean isSecondLevel = false;
	protected boolean animParent = true;
	protected Context context;
	protected int position;
	protected boolean isSearch;

	protected Callable<Void> refreshView;

	public WrapperListAdapterImpl(ListAdapter wrapped, Context context, Callable<Void> refreshView, boolean animParent, int position,
			boolean isSearch) {
		this.wrapped = wrapped;
		this.context = context;
		this.refreshView = refreshView;
		this.animParent = animParent;
		this.position = position;
		this.isSearch = isSearch;
	}

	@Override
	public ListAdapter getWrappedAdapter() {
		return wrapped;
	}

	public boolean isSecondLevel() {
		return isSecondLevel;
	}

	public void setSecondLevel(boolean isSecondLevel) {
		this.isSecondLevel = isSecondLevel;
	}

	@Override
	public boolean areAllItemsEnabled() {
		return wrapped.areAllItemsEnabled();
	}

	@Override
	public boolean isEnabled(int i) {
		return wrapped.isEnabled(i);
	}

	@Override
	public void registerDataSetObserver(DataSetObserver dataSetObserver) {
		wrapped.registerDataSetObserver(dataSetObserver);
	}

	@Override
	public void unregisterDataSetObserver(DataSetObserver dataSetObserver) {
		wrapped.unregisterDataSetObserver(dataSetObserver);
	}

	@Override
	public int getCount() {
		return wrapped.getCount();
	}

	@Override
	public Object getItem(int i) {
		return wrapped.getItem(i);
	}

	@Override
	public long getItemId(int i) {
		return wrapped.getItemId(i);
	}

	@Override
	public boolean hasStableIds() {
		return wrapped.hasStableIds();
	}

	@Override
	public View getView(int position, View view, ViewGroup viewGroup) {
		return wrapped.getView(position, view, viewGroup);
	}

	@Override
	public int getItemViewType(int i) {
		return wrapped.getItemViewType(i);
	}

	@Override
	public int getViewTypeCount() {
		return wrapped.getViewTypeCount();
	}

	@Override
	public boolean isEmpty() {
		return wrapped.isEmpty();
	}

	public Context getContext() {
		return context;
	}

	public void setContext(Context context) {
		this.context = context;
	}

}
