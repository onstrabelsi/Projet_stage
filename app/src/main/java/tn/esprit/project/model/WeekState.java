/*
 * This file is part of TrackWorkTime (TWT).
 *
 * TWT is free software: you can redistribute it and/or modify it
 * under the terms of the GNU General Public License 3.0 as published by
 * the Free Software Foundation.
 *
 * TWT is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License 3.0 for more details.
 *
 * You should have received a copy of the GNU General Public License 3.0
 * along with TWT. If not, see <http://www.gnu.org/licenses/>.
 */
package tn.esprit.project.model;

import androidx.annotation.NonNull;

import java.time.DayOfWeek;

public class WeekState {

	public enum HighlightType {
		NONE,
		REGULAR_FREE,
		FREE,
		CHANGED_TARGET_TIME
	}
	
	public static class DayRowState {
		public String label = "";
		public HighlightType labelHighlighted = HighlightType.NONE;
		public String in = "";
		public String out = "";
		public String worked = "";
		public String workedDecimal = "";
		public String flexi = "";
		public String flexiDecimal = "";

		public boolean highlighted = false;

		@NonNull @Override
		public String toString() {
			return "values: " + label + ", " + in + ", " + out + ", " + worked + ", " + flexi
					+ ", highlighted: " + highlighted;
		}
	}
	
	public static class SummaryRowState {
		public String label = "";
		public String worked = "";
		public String workedDecimal = "";
		public String flexi = "";
		public String flexiDecimal = "";

		@NonNull @Override
		public String toString() {
			return "values: " + label + ", " + worked + ", " + flexi;
		}
	}
	
	public String topLeftCorner = "";
	public final SummaryRowState totals = new SummaryRowState();

	private final DayRowState[] dayRowStates = {
			new DayRowState(), new DayRowState(), new DayRowState(), new DayRowState(),
			new DayRowState(), new DayRowState(), new DayRowState()
	};
	
	public DayRowState getRowForDay(DayOfWeek dayOfWeek) {
		return dayRowStates[dayOfWeek.ordinal()];
	}

	@NonNull @Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(topLeftCorner); sb.append("\n");

		for (DayOfWeek day : DayOfWeek.values()) {
			sb.append(getRowForDay(day).toString());
			sb.append("\n");
		}

		sb.append(totals); sb.append("\n");

		return sb.toString();
	}
}
