package com.example.testMgmtAndroid.helper;

import org.json.JSONException;
import org.json.JSONObject;

public interface OnApiTaskCompleted {
    void afterReceivingData(int callback, JSONObject response) throws JSONException;
}
