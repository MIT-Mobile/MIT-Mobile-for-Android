package edu.mit.mitmobile2;

import android.util.Log;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

/**
 * Created by sseligma on 2/24/15.
 */
public class APIJsonResponse extends APIResponse {
    public JSONArray jsonArray;

    public APIJsonResponse(int statusCode,Header[] headers,byte[]response) {
        this.statusCode = statusCode;
        this.headers = headers;
        this.response = response;


        // Code to convert byte arr to str:
        String res = new String(response);
        Object json;

        try {
            json = new JSONTokener(res).nextValue();
            if (json instanceof JSONObject){
                // if the result is an object, create a JSON array where json is the element of the array
                jsonArray = new JSONArray();
                jsonArray.put(json);
            }
            else if (json instanceof JSONArray) {
                jsonArray = (JSONArray)json;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

}
