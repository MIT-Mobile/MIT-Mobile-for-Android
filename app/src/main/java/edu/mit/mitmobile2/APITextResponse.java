package edu.mit.mitmobile2;


import org.apache.http.Header;

/**
 * Created by sseligma on 2/24/15.
 */
public class APITextResponse extends APIResponse {
    public String text;

    public APITextResponse(int statusCode,Header[] headers,byte[]response) {
        this.statusCode = statusCode;
        this.headers = headers;
        this.response = response;
        this.text = new String(response);
    }
}
