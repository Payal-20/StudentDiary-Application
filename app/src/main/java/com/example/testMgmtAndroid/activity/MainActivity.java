package com.example.testMgmtAndroid.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.testMgmtAndroid.R;
import com.example.testMgmtAndroid.fragment.ExamListFragment;
import com.example.testMgmtAndroid.fragment.Examhistory;
import com.example.testMgmtAndroid.fragment.ExpiredExams;
import com.example.testMgmtAndroid.fragment.MyAccount;
import com.example.testMgmtAndroid.fragment.PageDetails;
import com.example.testMgmtAndroid.fragment.ProfileChild;
import com.example.testMgmtAndroid.helper.DbHelper;
import com.example.testMgmtAndroid.helper.OnApiTaskCompleted;
import com.example.testMgmtAndroid.helper.Utility;
import com.google.android.material.navigation.NavigationView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.Iterator;

public class MainActivity extends AppCompatActivity implements OnApiTaskCompleted{

    NavigationView navigationView;
    ActionBarDrawerToggle toggle;
    Toolbar toolbar;
    DrawerLayout drawerLayout;
    ImageView refreshIcon;
    DbHelper dbHelper;
    Context context;
    ExamListFragment examListFragment;
    JSONArray examQuestionData;
    SharedPreferences preferences;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context=this;
        dbHelper=new DbHelper(context);
        preferences = getSharedPreferences(Constant.USER_DATA, MODE_PRIVATE);
       String json;
                try {
                    dbHelper.deleteAll();
                    InputStream is = context.getAssets().open("b.json");
                    int size = is.available();
                    byte[] buffer = new byte[size];
                    is.read(buffer);
                    json = new String(buffer, "UTF-8");
                    examQuestionData = new JSONArray(json);
                    is.close();
                    for (int i = 0; i < examQuestionData.length(); i++) {
                        JSONObject object = (JSONObject) examQuestionData.get(i);
                        dbHelper.insertData2(object.optString("exam_id"),
                                object.optString("duration"),
                                object.optString("question_count"),
                                object.optString("start_date"),
                                object.optString("start_time"),
                                object.optString("tittle"));
                    }
                } catch (IOException | JSONException e) {
                    e.printStackTrace();
            }
            toolbar = findViewById(R.id.toolbar);
            drawerLayout = findViewById(R.id.drawer);
            navigationView = findViewById(R.id.nav);
            refreshIcon = findViewById(R.id.image_refresh);
            examListFragment = new ExamListFragment();

        refreshIcon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (examListFragment.isVisible()) {
                        examListFragment.refreshExamList();
                        examListFragment.listView.setVisibility(View.VISIBLE);
                        examListFragment.NoMatchFound.setVisibility(View.GONE);
                    }
                }
            });
            setSupportActionBar(toolbar);
            ActionBar actionBar = getSupportActionBar();

            toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.nav_open, R.string.nav_close);
            drawerLayout.addDrawerListener(toggle);
            toggle.getDrawerArrowDrawable().setColor(getResources().getColor(R.color.white));
            toggle.syncState();
            goToExamListFragment();
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                int id = menuItem.getItemId();
                if(id == R.id.ExamHistory) {
                    loadFragment(new ExpiredExams());
                }  else if(id == R.id.MyAccount) {
                    loadFragment(new MyAccount());
                }
                else if(id == R.id.UpcomingExams) {
                    loadFragment(new ExamListFragment());
                }
                else if(id == R.id.Expired) {
                    loadFragment(new Examhistory());
                }
                else if(id == R.id.Logout) {
                    SharedPreferences sharedPreferences = context.getSharedPreferences("user_data", Context.MODE_PRIVATE);
                    sharedPreferences.edit().clear().commit();
                    Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                    startActivity(intent);
                    finish();
                    Log.e("MyData"," loggedInUserName ==== " + sharedPreferences);
                }
                drawerLayout.closeDrawer(GravityCompat.START);
                return true;
            }
        });
    }

    @Override
    public void onBackPressed() {
        if(drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    public void loadFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.content_frame, fragment);
        transaction.commit();
    }

    public void goToExamListFragment() {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.isAddToBackStackAllowed();
        fragmentTransaction.replace(R.id.content_frame, examListFragment);
        fragmentTransaction.commitAllowingStateLoss();
    }


    private void requestCalendarPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_CALENDAR) == PackageManager.PERMISSION_GRANTED)
            return;

        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_CALENDAR)) {
            //If the user has denied the permission previously your code will come to this block
            //Here you can explain why you need this permission
            //Explain here why you need this permission
        }
        //And finally ask for the permission
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_CALENDAR}, 124);
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 124) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                Toast.makeText(this, "Now calendar sync can be accessed", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this, "Calendar permission is required for calendar sync", Toast.LENGTH_LONG).show();
            }
        }
    }

    private void checkLocationPermissions() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 125);
        }
    }

    private void setPreference(JSONObject response, String feature) throws JSONException {

        SharedPreferences.Editor edit = preferences.edit();
        Iterator<?> keys = response.keys();
        while( keys.hasNext() ) {
            String key = (String)keys.next();
            if(! new String("success").equals(key)
                    && ! new String("responseCode").equals(key)
                    && ! new String("statusMsg").equals(key)) {
                edit.putString(key, response.getString(key));
            }
        }

        if (feature.equals("Assignment")) {
            edit.putString("selectAssignmentTabNumber", "1");
        }  else if (feature.equals("ChildDetails")) {
            edit.putString("selectChildDetailsTabNumber", "1");
        } else if (feature.equals("ChildProfile")) {
            edit.putString("selectChildDetailsTabNumber", "1");
        } else if (feature.equals("ChildPhoto")) {
            edit.putString("selectChildDetailsTabNumber", "2");
        } else if (feature.equals("ChildDocument")) {
            edit.putString("selectChildDetailsTabNumber", "3");
        }

        edit.commit();
    }
    @Override
    public void afterReceivingData(int callback, JSONObject response) {
        try {
            if (response.getString("success").equals("true")) {

                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                Fragment currentFragment;
                switch (callback) {
                    case Constant.LOAD_CHILD_DETAILS:
                        this.setPreference(response, "ChildDetails");

                        ft.setCustomAnimations(R.anim.enter, R.anim.exit, R.anim.left_to_right, R.anim.right_to_left);
                        ft.addToBackStack(null);
                        ft.commitAllowingStateLoss();
                        break;

                    case Constant.RELOAD_CHILD_PROFILE_AFTER_UPDATE:
                        this.setPreference(response, "ChildProfile");
                        reloadChildDetailsFragment();
                        break;

                    case Constant.RELOAD_CHILD_PHOTO_AFTER_DELETE:
                        this.setPreference(response, "ChildPhoto");
                        updateHomePageProfilePhoto();
                        reloadChildDetailsFragment();
                        break;

                    case Constant.RELOAD_CHILD_DOCUMENT_AFTER_DELETE:
                        this.setPreference(response, "ChildDocument");
                        reloadChildDetailsFragment();
                        break;

                }

            }
        } catch (Exception e) {
            Utility.showSnackBar(getResources().getString(R.string.exception_msg), MainActivity.this);
        }
    }
    private void updateHomePageProfilePhoto() {
        String photoUrl = "";
        if (preferences.contains("studentPhotos")) {
            try {
                JSONArray photoArray = new JSONArray(preferences.getString("studentPhotos", ""));

                for (int i = 0; i < photoArray.length(); i++) {
                    JSONObject photoDetails = photoArray.getJSONObject(i);

                    if (photoDetails.getString("student_photo_type_id").equals("1")) {
                        photoUrl = photoDetails.getString("photo_url");
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }


}
    private void reloadChildDetailsFragment() {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        Fragment currentFragment;
        ft.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out, R.anim.left_to_right, R.anim.right_to_left);
        currentFragment = getSupportFragmentManager().findFragmentById(R.id.content_frame);
        ft.detach(currentFragment);
        ft.replace(R.id.content_frame, new ProfileChild());
        ft.commitAllowingStateLoss();
    }
}
