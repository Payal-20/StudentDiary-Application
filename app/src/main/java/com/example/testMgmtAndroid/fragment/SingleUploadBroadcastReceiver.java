package com.example.testMgmtAndroid.fragment;

import android.util.Log;

import net.gotev.uploadservice.UploadServiceBroadcastReceiver;

import org.json.JSONException;

import java.io.UnsupportedEncodingException;

public class SingleUploadBroadcastReceiver  extends UploadServiceBroadcastReceiver {
    private static final String LOG_TAG = SingleUploadBroadcastReceiver.class.getSimpleName();

    public interface Delegate {
        void onProgress(int progress);

        void onProgress(long uploadedBytes, long totalBytes);

        void onError(Exception exception);

        void onCompleted(int serverResponseCode, byte[] serverResponseBody) throws UnsupportedEncodingException, JSONException;

        void onCancelled();
    }

    private String mUploadID;
    private Delegate mDelegate;

    public void setUploadID(String uploadID) {
        mUploadID = uploadID;
    }

    public void setDelegate(Delegate delegate) {
        mDelegate = delegate;
    }

    @Override
    public void onProgress(String uploadId, int progress) {
        if (uploadId.equals(mUploadID) && mDelegate != null) {
            mDelegate.onProgress(progress);
        }
    }

    @Override
    public void onProgress(String uploadId, long uploadedBytes, long totalBytes) {
        if (uploadId.equals(mUploadID) && mDelegate != null) {
            mDelegate.onProgress(uploadedBytes, totalBytes);
        }
    }

    @Override
    public void onError(String uploadId, Exception exception) {
        if (uploadId.equals(mUploadID) && mDelegate != null) {
            mDelegate.onError(exception);
        }
    }

    @Override
    public void onCompleted(String uploadId, int serverResponseCode, byte[] serverResponseBody) {
        Log.e(LOG_TAG, "onCompleted: serverResponseCode = " + serverResponseCode);
        try {
            if (uploadId.equals(mUploadID) && mDelegate != null) {
                mDelegate.onCompleted(serverResponseCode, serverResponseBody);
            }
        } catch (Exception e) {
            Log.e(LOG_TAG, "onCompleted: exception = " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public void onCancelled(String uploadId) {
        if (uploadId.equals(mUploadID) && mDelegate != null) {
            mDelegate.onCancelled();
        }
    }
}
