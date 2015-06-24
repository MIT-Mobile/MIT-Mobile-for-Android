package edu.mit.mitmobile2.shared;

import android.text.TextUtils;

public class StringUtils {

    public static String sanitizeMapSearchString(String query) {
        String buildingNumber = "";
        String[] roomComponents = query.split("-");

        if (roomComponents.length > 1) {
            String firstComponent = (roomComponents != null && roomComponents.length > 0) ? roomComponents[0] : null;
            if (!TextUtils.isEmpty(firstComponent) && firstComponent.length() == 1 && firstComponent.matches("[a-zA-Z]")) {
                // First component is a letter.  Someone probably put N-51 or E-15 instead of N51 or E15
                if (roomComponents.length >= 2) {
                    String secondComponent = roomComponents[1];
                    if (Integer.getInteger(secondComponent) != null && Integer.getInteger(secondComponent) > 0) {
                        buildingNumber = firstComponent + secondComponent;
                    }
                } else {
                    buildingNumber = query;
                }
            } else {
                buildingNumber = firstComponent;
            }
            return buildingNumber.trim();
        } else {
            String[] components = query.split(" ");
            for (String s : components) {
                if (s.length() == 1 && Character.isDigit(s.charAt(0))) {
                    return s;
                }
            }

            return query;
        }
    }
}
