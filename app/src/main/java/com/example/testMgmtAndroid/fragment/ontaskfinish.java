package com.example.testMgmtAndroid.fragment;

import org.json.JSONException;

import java.io.UnsupportedEncodingException;

public interface ontaskfinish {
    void onCompleted(int serverResponseCode, byte[] serverResponseBody) throws UnsupportedEncodingException, JSONException;
}
