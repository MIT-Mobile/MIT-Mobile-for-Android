package edu.mit.mitmobile.maps;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.maps.OverlayItem;
import edu.mit.mitmobile.R;

public class BalloonOverlayView extends FrameLayout {

        private LinearLayout layout;
        private TextView title;
        private TextView snippet;

        public BalloonOverlayView(Context context, int balloonBottomOffset) {

                super(context);

                setPadding(10, 0, 10, balloonBottomOffset);
                
                layout = new LinearLayout(context);
                layout.setVisibility(VISIBLE);

                LayoutInflater inflater = (LayoutInflater) context
                                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                
                View v = inflater.inflate(R.layout.map_bubble, layout);
                
                title = (TextView) v.findViewById(R.id.balloon_item_title);
                snippet = (TextView) v.findViewById(R.id.balloon_item_snippet);

                /*
                ImageView close = (ImageView) v.findViewById(R.id.close_img_button);
                close.setOnClickListener(new OnClickListener() {
                        public void onClick(View v) {
                                layout.setVisibility(GONE);
                        }
                });
                 */
                FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                                LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
                
                params.gravity = Gravity.NO_GRAVITY;

                addView(layout, params);

        }
        
      
        public void setData(OverlayItem item) {
                
                layout.setVisibility(VISIBLE);
                
                if (item.getTitle() != null) {
                        title.setVisibility(VISIBLE);
                        title.setText(item.getTitle());
                } else {
                        title.setVisibility(GONE);
                }
                
                if (item.getSnippet() != null) {
                        snippet.setVisibility(VISIBLE);
                        snippet.setText(item.getSnippet());
                } else {
                        snippet.setVisibility(GONE);
                }
                
        }

}
