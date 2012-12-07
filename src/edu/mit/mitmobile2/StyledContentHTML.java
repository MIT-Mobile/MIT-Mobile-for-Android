package edu.mit.mitmobile2;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

import android.content.Context;


public class StyledContentHTML {

	public static String html(Context context, String bodyHTML) {
		HashMap<String, String> content = new HashMap<String, String>();
		content.put("BODY", bodyHTML);
		return populateTemplate(context, "content_template.html", content);
	}
	
	public static String imageHtml(Context context, String url) {
		HashMap<String, String> content = new HashMap<String, String>();
		content.put("URL", url);
		return populateTemplate(context, "image_template.html", content);
	}
    
	public static String populateTemplate(Context context, String templateName, Map<String, String> content) {
		InputStream templateInputStream;
		try {
			templateInputStream = context.getAssets().open(templateName);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new RuntimeException(e.getMessage());
		}
		String template = slurp(templateInputStream);
		return replaceTokens(template, content);
	}
	
    private static String slurp(InputStream stream) {
        
        BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
        StringBuilder stringBuilder = new StringBuilder();
 
        String line = null;
        try {
            while ((line = reader.readLine()) != null) {
                stringBuilder.append(line + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e.getMessage());
        } 
        
        return stringBuilder.toString();
    }
    
    /*
     * Thanks to stack overflow for this code snippet
     * http://stackoverflow.com/questions/959731/how-to-replace-a-set-of-tokens-in-a-java-string
     */
    private static String replaceTokens(String template, Map<String, String> replacements) {
    	Pattern pattern = Pattern.compile("_(.+?)_");
    	Matcher matcher = pattern.matcher(template);
    	
    	StringBuffer buffer = new StringBuffer();
    	while(matcher.find()) {
    		String replacement = replacements.get(matcher.group(1));
    		
    		if(replacement != null) {
    			matcher.appendReplacement(buffer, "");
    			buffer.append(replacement);
    		}
    	}
    	matcher.appendTail(buffer);
    	return buffer.toString();
    }
}
