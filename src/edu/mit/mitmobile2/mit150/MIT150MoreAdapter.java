package edu.mit.mitmobile2.mit150;

import java.util.ArrayList;

import edu.mit.mitmobile2.R;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class MIT150MoreAdapter extends ArrayAdapter<MIT150MoreItem> {

	private Context ctx;
	
	public MIT150MoreAdapter(Context context, int textViewResourceId, ArrayList<MIT150MoreItem> items) {	
		super(context, textViewResourceId, items);
		this.ctx = context;
		
		final Handler imgHandler = new Handler() {
			@Override
			public void handleMessage(Message message) {
				super.handleMessage(message);
				notifyDataSetInvalidated();
			}
		};
		MIT150Model m = new MIT150Model(context);
		m.fetchThumbnails(imgHandler,items);
		
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		
		View v = convertView;
		if (v == null) {
			LayoutInflater vi = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			v = vi.inflate(R.layout.mit150_row, null);
		}
		
		MIT150MoreItem mi = getItem(position);
		
		if (mi!=null) {
			
			TextView tv = (TextView) v.findViewById(R.id.mit150RowTitleTV);
			tv.setText(mi.title);
			
			tv = (TextView) v.findViewById(R.id.mit150RowBodyTV);
			tv.setText(mi.subtitle);
			
			ImageView iv = (ImageView) v.findViewById(R.id.mit150RowIV);
			
			if (mi.bd!=null) iv.setImageDrawable(mi.bd);
		
		}
		
		return v;
	}

	
	
}
