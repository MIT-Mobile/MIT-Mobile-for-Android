package edu.mit.mitmobile.objs;

import org.json.JSONObject;

public abstract class BaseItem {

	abstract BaseItem parseJSON(JSONObject jo);
	
}
