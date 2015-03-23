package edu.mit.mitmobile2.tour;

import java.util.HashMap;

import android.content.Context;

import edu.mit.mitmobile2.StyledContentHTML;
import edu.mit.mitmobile2.tour.Tour.SideTrip;

public class TourHtml {
	
	public static String tourStopHtml(Context context, String html, String photoUrl, String photoLabel) {
		HashMap<String, String> content = new HashMap<String, String>();
		content.put("BODY", html);
		if(photoUrl != null) {
			content.put("PHOTO-URL", photoUrl);
			content.put("PHOTO-LABEL", photoLabel);
			content.put("PHOTO-DISPLAY", "block");
		} else {
			content.put("PHOTO-URL", "");
			content.put("PHOTO-LABEL", "");
			content.put("PHOTO-DISPLAY", "none");
		}
		
		return StyledContentHTML.populateTemplate(context, "tour/content_template.html", content);
		
	}
	
	public static String sideTripLinkHtmlFragment(SideTrip sideTrip) {
		return "" +
			"<p class=\"sidetrip\">" +
				"<a href=\"sidetrip://" + sideTrip.getId() + "\">Side Trip: " + sideTrip.getTitle() + "</a>" +
			"</p>";
	}
}
