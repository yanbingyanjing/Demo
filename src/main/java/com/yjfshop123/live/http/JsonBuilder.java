package com.yjfshop123.live.http;

import org.json.JSONException;
import org.json.JSONObject;

public class JsonBuilder {

    private JSONObject obj;

    public JsonBuilder(){
        obj = new JSONObject();
    }

    public JsonBuilder put(String k, int v) throws JSONException {
        obj.put(k, v);
        return this;
    }

    public JsonBuilder put(String k, long v) throws JSONException{
        obj.put(k, v);
        return this;
    }

    public JsonBuilder put(String k, double v) throws JSONException{
        obj.put(k, v);
        return this;
    }

    public JsonBuilder put(String k, String v) throws JSONException{
        obj.put(k, v);
        return this;
    }

    public JsonBuilder put(String k, JSONObject v) throws JSONException {
        obj.put(k, v);
        return this;
    }

    public String build(){
        return obj.toString();
    }

}
