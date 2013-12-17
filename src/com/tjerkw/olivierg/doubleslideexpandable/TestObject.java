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

import java.io.Serializable;

public class TestObject implements Serializable {

	private static final long serialVersionUID = -2548996372277790042L;

	private String text;
	private String detail1;
	private String detail2;
	private String detail3;
	private String detail4;

	public TestObject(String text, String detail1, String detail2, String detail3, String detail4) {
		super();
		this.text = text;
		this.detail1 = detail1;
		this.detail2 = detail2;
		this.detail3 = detail3;
		this.detail4 = detail4;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public String getDetail1() {
		return detail1;
	}

	public void setDetail1(String detail1) {
		this.detail1 = detail1;
	}

	public String getDetail2() {
		return detail2;
	}

	public void setDetail2(String detail2) {
		this.detail2 = detail2;
	}

	public String getDetail3() {
		return detail3;
	}

	public void setDetail3(String detail3) {
		this.detail3 = detail3;
	}

	public String getDetail4() {
		return detail4;
	}

	public void setDetail4(String detail4) {
		this.detail4 = detail4;
	}

}
