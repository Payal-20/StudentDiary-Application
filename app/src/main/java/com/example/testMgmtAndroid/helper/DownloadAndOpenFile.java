package com.example.testMgmtAndroid.helper;


import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;

import androidx.core.content.FileProvider;

import com.example.testMgmtAndroid.activity.Constant;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;

public class DownloadAndOpenFile extends AsyncTask<String, Void, Void> {
    private static final String LOG_TAG = DownloadAndOpenFile.class.getSimpleName();
    Context currentContext;
    Activity currentActivity;

    public DownloadAndOpenFile(Context context, Activity activity){
        currentContext = context;
        currentActivity = activity;
    }

    @Override
    protected Void doInBackground(String... strings) {
        String fileUrl = strings[0];
        String fileName = strings[1];
        String extension = strings[2];

//        Log.e("DownloadFile fileUrl: ", fileUrl);
//        Log.e("DownloadFile fileName: ", fileName);


//            String extStorageDirectory = Environment.getExternalStorageDirectory().toString();
//            File folder = new File(extStorageDirectory, "SchoolDiaryAttachments");
//            folder.mkdirs();

//            File extStorageDirectory = android.os.Environment.getExternalStorageDirectory();
//            File folder = new File (extStorageDirectory.getAbsolutePath() + "/SchoolDiaryAttachments");

//            File extStorageDirectory = Environment.getExternalStorageDirectory();
//            File folder = new File (extStorageDirectory.getPath() + "/SchoolDiaryAttachments");
//            if(!folder.exists()) {
//                folder.mkdirs();
//            }


//            String extStorageDirectory = Environment.getExternalStorageDirectory().toString();
        File extStorageDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        File folder = new File(extStorageDirectory, "SchoolDiaryAttachments");
        if(!folder.exists()) {
            folder.mkdirs();
        }

        File attachmentFile = new File(folder, fileName);

        try{
            attachmentFile.createNewFile();
        }catch (IOException e){
            e.printStackTrace();
//            Log.e("DownloadFile error: ", e.getMessage());
        }


        try {

            URL url = new URL(fileUrl);

//            Log.e("FileDownload fileUrl: ", fileUrl);
//            Log.e("FileDownload fileUrl: ", url.toString());
//            Log.e("File Directory: ", attachmentFile.toString());


            HttpURLConnection urlConnection = (HttpURLConnection)url.openConnection();
            //urlConnection.setRequestMethod("GET");
            //urlConnection.setDoOutput(true);
            urlConnection.connect();

            InputStream inputStream = urlConnection.getInputStream();
            FileOutputStream fileOutputStream = new FileOutputStream(attachmentFile);
            int totalSize = urlConnection.getContentLength();

//            Log.e("File size: ", Integer.toString(totalSize));

            byte[] buffer = new byte[Constant.MEGABYTE];
            int bufferLength = 0;
            while((bufferLength = inputStream.read(buffer))>0 ){
                fileOutputStream.write(buffer, 0, bufferLength);
//                Log.e("File data: ", buffer.toString());
            }
            fileOutputStream.close();


        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


        //Open file

//        Uri attachmentPath = Uri.fromFile(attachmentFile);
        Uri attachmentPath = FileProvider.getUriForFile(currentActivity,
                "com.headwaytechies.wonderfulKidz.fileprovider",
                attachmentFile);

        Intent attachmentIntent = new Intent(Intent.ACTION_VIEW);

        if (Arrays.asList(Constant.IMAGE_EXTENSIONS).contains(extension)) {
            attachmentIntent.setDataAndType(attachmentPath, "image/*");
        }
        else if (extension.equals("pdf")) {
            attachmentIntent.setDataAndType(attachmentPath, "application/pdf");
        }
        else {
            attachmentIntent.setDataAndType(attachmentPath, "text/*");
        }

//            attachmentIntent.setData(path);

        attachmentIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        attachmentIntent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

        try {
            currentActivity.startActivity(attachmentIntent);
        } catch (ActivityNotFoundException e) {
            Utility.showSnackBar("No Application available to view file", currentActivity);
        } catch (Exception e) {
            Log.e(LOG_TAG, "Exception = " + e.getMessage());
            Utility.showSnackBar("Unable to open the file", currentActivity);
        }



        return null;
    }

    @Override
    protected void onPreExecute(){
        super.onPreExecute();
        Utility.showProgressDialog(currentContext,"Loading");
    }

    @Override
    protected void onPostExecute(Void result){
        Utility.hideProgressDialog();
    }
}
