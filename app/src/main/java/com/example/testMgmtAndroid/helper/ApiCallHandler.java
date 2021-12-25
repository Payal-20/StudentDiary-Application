package com.example.testMgmtAndroid.helper;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.example.testMgmtAndroid.fragment.ProfileChild;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;


public class ApiCallHandler extends AsyncTask<String, String, Void> {
    private final static String LOG_TAG = ApiCallHandler.class.getSimpleName();

    private String method;
    private String apiUrl;
    private OnApiTaskCompleted onApiTaskCompleted;
    private JSONObject responseObject;
    private boolean isNetworkAvailable = true;
    private int callback = -1;
    private Context mContext;
    ProfileChild profileChild;

    public ApiCallHandler(String method, String apiUrl, Context context, int callback) {
        this.method = method;
        this.apiUrl = apiUrl;
        this.mContext = context;
        this.onApiTaskCompleted = (OnApiTaskCompleted) context;
        this.callback = callback;
    }

    public ApiCallHandler(String method, String apiUrl, Context context, Fragment fragment, int callback) {
        this.method = method;
        this.apiUrl = apiUrl;
        this.mContext = context;
        this.profileChild = (ProfileChild) fragment;
        this.callback = callback;
    }

    @Override
    protected Void doInBackground(String... data) {
        URL url;
        HttpURLConnection conn;
        OutputStream os;
        DataOutputStream dos = null;

        try {
            SharedPreferences preferences = mContext.getSharedPreferences("user_data", mContext.MODE_PRIVATE);
            Log.e(LOG_TAG, "API URL = " + apiUrl);
            url = new URL(apiUrl);
            conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(10000);
            conn.setConnectTimeout(15000);
            conn.setRequestMethod(method);

            if (preferences.contains("token")) {
                conn.setRequestProperty("Cookie", new String("token=").concat(preferences.getString("token", "")));
                conn.setRequestProperty("token", preferences.getString("token", ""));

            }

            if (data.length > 0 && ("POST".equals(method) || "PUT".equals(method))) {
                Log.d(LOG_TAG, "data = " + data.toString());

                StringBuilder sb = new StringBuilder();
                sb.append(data[0]);

                conn.setDoInput(true);
                conn.setDoOutput(true);
                conn.setRequestProperty("Content-Type", "application/json");
                conn.setRequestProperty("charset", "utf-8");
                //conn.setRequestProperty("Content-Length", Integer.toString(sb.length()));


                os = conn.getOutputStream();
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));

                writer.write(sb.toString());
                writer.flush();
                writer.close();
                os.close();
            }

            InputStream is = conn.getInputStream();
            BufferedReader rd = new BufferedReader(new InputStreamReader(is));
            String line;
            StringBuffer response = new StringBuffer();
            while ((line = rd.readLine()) != null) {
                response.append(line);
                response.append('\r');
            }
            rd.close();
            Log.e(LOG_TAG, " responseObject == " + responseObject);

            if (callback == 3) {
                responseObject = new JSONObject();
                responseObject.put("success", true);
                responseObject.put("responseCode", response.toString());
                responseObject.put("statusMsg", response.toString());
                responseObject.put("requestData", data[0]);
            } else {
                responseObject = new JSONObject(response.toString());
            }
            conn.connect();

        } catch (java.net.SocketTimeoutException e) {
            Log.e(LOG_TAG, "no internet exception...");
            isNetworkAvailable = false;
        } catch (Exception e) {
            Log.e(LOG_TAG, "doInBackground Exception = " + e.getMessage());
            e.printStackTrace();
        }

        return null;
    }

    protected void onPostExecute(Void unused) {
        Log.e(LOG_TAG, "onPostExecute responseObject == " + responseObject);
        if (responseObject != null && isNetworkAvailable) {
            try {
                Log.e(LOG_TAG, "response = " + responseObject.toString());
                onApiTaskCompleted.afterReceivingData(callback, responseObject);
            } catch (Exception e) {
                Log.e(LOG_TAG, "onPostExecute Exception = " + e.toString());
            }
        } else {
            Log.e(LOG_TAG, "Network error! Problem with internet connection OR didn't find the response JSON object.");
            if (mContext != null) {
                Toast.makeText(mContext, "We had trouble connecting to the server. Please check your internet connectivity and restart the app.", Toast.LENGTH_LONG).show();
            }

        }
    }
}
