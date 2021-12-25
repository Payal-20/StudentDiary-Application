package com.example.testMgmtAndroid.helper;


import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.provider.OpenableColumns;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;

import com.example.testMgmtAndroid.R;
import com.google.android.material.snackbar.Snackbar;


import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Utility {
    private static final String LOG_TAG = Utility.class.getSimpleName();


    public static ProgressDialog progressDialog;

    public static void showSnackBar(String msg, Activity activity) {
        Snackbar snackBar = Snackbar.make(activity.findViewById(android.R.id.content), msg, Snackbar.LENGTH_LONG);
        View snackBarView = snackBar.getView();

//        snackBarView.setBackgroundColor(Color.RED);
        snackBarView.setBackgroundColor(ContextCompat.getColor(activity, R.color.colorSnackbar));
        TextView snackbarTextView = (TextView) snackBarView.findViewById( R.id.snackbar_text );
        snackbarTextView.setTextColor(Color.WHITE);

        snackBar.show();
    }

    public static void showProgressDialog(Context context, String message) {
        progressDialog = new ProgressDialog(context);
        progressDialog.setMessage(message+"...");
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();
    }

    public static void hideProgressDialog(){
        progressDialog.hide();
    }

    public static boolean emailValidator(final String mailAddress) {
        Pattern pattern;
        Matcher matcher;
        final String EMAIL_PATTERN = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@" + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
        pattern = Pattern.compile(EMAIL_PATTERN);
        matcher = pattern.matcher(mailAddress);
        return matcher.matches();
    }

    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null;
    }

    public static boolean isValidMobileNumber(String mobileNo) {
        String pattern = "^[1-9]{1}[0-9]{9}$";
        if (mobileNo.matches(pattern)) {
            return true;
        }
        return false;
    }



    public static void showAlert(String msg, Activity activity) {
        AlertDialog dialog = new AlertDialog.Builder(activity).create();
        dialog.setMessage(msg);
        dialog.setCancelable(true);
        dialog.setButton(AlertDialog.BUTTON_POSITIVE, "Ok",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        dialog.show();
    }

    public static void showAlert(String msg, Activity activity, DialogInterface.OnClickListener onClickListener) {
        AlertDialog dialog = new AlertDialog.Builder(activity).create();
        dialog.setMessage(msg);
        dialog.setCancelable(true);
        dialog.setButton(AlertDialog.BUTTON_POSITIVE, "Ok", onClickListener);
        dialog.show();
    }

    public static void showAlert(String msg, Activity activity, DialogInterface.OnClickListener onClickListener1, DialogInterface.OnClickListener onClickListener2) {
        AlertDialog dialog = new AlertDialog.Builder(activity).create();
        dialog.setMessage(msg);
        dialog.setCancelable(true);
        dialog.setButton(AlertDialog.BUTTON_POSITIVE, "Ok", onClickListener1);
        dialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Cancel", onClickListener2);
        dialog.show();
    }



    public static File getFile(Context context, Uri uri) {
        Uri returnUri = uri;
        Cursor returnCursor = context.getContentResolver().query(returnUri, null, null, null, null);

        int nameIndex = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
        int sizeIndex = returnCursor.getColumnIndex(OpenableColumns.SIZE);
        returnCursor.moveToFirst();
        String name = (returnCursor.getString(nameIndex));
        String size = (Long.toString(returnCursor.getLong(sizeIndex)));
        File file = new File(context.getFilesDir(), name);
        try {
            InputStream inputStream = context.getContentResolver().openInputStream(uri);
            FileOutputStream outputStream = new FileOutputStream(file);
            int read = 0;
            int maxBufferSize = 1 * 1024 * 1024;
            int bytesAvailable = inputStream.available();

            int bufferSize = Math.min(bytesAvailable, maxBufferSize);

            final byte[] buffers = new byte[bufferSize];
            while ((read = inputStream.read(buffers)) != -1) {
                outputStream.write(buffers, 0, read);
            }

            inputStream.close();
            outputStream.close();
        } catch (Exception e) {
            Log.e("Exception", e.getMessage());
        }
        return file;
    }

}