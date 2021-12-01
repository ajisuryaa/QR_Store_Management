package com.putrabatam.qrstore.util;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class RequestHandler {
    public boolean status;
    public String message, version;
    public int code;
    public JSONObject data_object;
    public JSONArray data_array;

    public void parseDataObject(JSONObject return_from_api){
        try {
            this.status = return_from_api.getBoolean("status");
            this.code = return_from_api.getInt("code");
            this.message = return_from_api.getString("message");
            this.version = return_from_api.getString("version");
            if(return_from_api.getBoolean("status")){
                this.data_object = return_from_api.getJSONObject("data");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

}
