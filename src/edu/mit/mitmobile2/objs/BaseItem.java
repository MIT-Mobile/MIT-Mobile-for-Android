package edu.mit.mitmobile2.objs;

import org.json.JSONObject;

public abstract class BaseItem {

	abstract BaseItem parseJSON(JSONObject jo);
	
}
