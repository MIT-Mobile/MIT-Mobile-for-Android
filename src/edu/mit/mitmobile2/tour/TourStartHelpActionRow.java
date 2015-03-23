package edu.mit.mitmobile2.tour;

import android.content.Context;
import android.util.AttributeSet;
import edu.mit.mitmobile2.ActionRow;
import edu.mit.mitmobile2.R;

public class TourStartHelpActionRow extends ActionRow {

	public TourStartHelpActionRow(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	@Override
	protected int getLayoutId() {
		return R.layout.tour_start_help_action_row;
	}
	
}
