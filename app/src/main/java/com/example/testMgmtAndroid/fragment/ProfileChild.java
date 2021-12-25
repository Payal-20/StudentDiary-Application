package com.example.testMgmtAndroid.fragment;

import static android.content.ContentValues.TAG;
import static android.content.Context.MODE_PRIVATE;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.testMgmtAndroid.R;
import com.example.testMgmtAndroid.activity.Constant;
import com.example.testMgmtAndroid.activity.MainActivity;
import com.example.testMgmtAndroid.helper.ApiCallHandler;
import com.example.testMgmtAndroid.helper.CircleTransform;
import com.example.testMgmtAndroid.helper.OnApiTaskCompleted;
import com.example.testMgmtAndroid.helper.Utility;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;


public class ProfileChild extends Fragment  {


    SharedPreferences preferences;
    //    TableLayout childProfileTable;
    LinearLayout editChildProfileLayout, displayChildProfileLayout;
    EditText childProfileFirstNameText, childProfileMiddleNameText, childProfileLastNameText, childProfileDOBText, childProfileEmergencyPhoneText, childProfileStreetAddressText, childProfileCityText, childProfilePincodeText;
    Spinner childProfileGenderSpinner, childProfileStateSpinner, childProfileBloodGroupSpinner;
    FloatingActionButton editChildProfileButton;
    ImageView imgChildProfile;
    JSONArray bloodGroupArray;

    String studentName;

    List<String> genders = new ArrayList<String>();
    List<String> states = new ArrayList<String>();
    List<String> bloodGroups = new ArrayList<>();

    SimpleDateFormat sdf, rdf, ddf;
    Calendar dateOfBirthCalendarDialog;

    View view;
    JSONObject parseQuestionData;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,

                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_profile_child, container, false);
        preferences = view.getContext().getSharedPreferences(Constant.USER_DATA, view.getContext().MODE_PRIVATE);

        sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.US);
        rdf = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        ddf = new SimpleDateFormat("dd MMM yyyy", Locale.US);


        dateOfBirthCalendarDialog = Calendar.getInstance();


        childProfileFirstNameText = (EditText) view.findViewById(R.id.childProfileFirstNameText);
        childProfileMiddleNameText = (EditText) view.findViewById(R.id.childProfileMiddleNameText);
        childProfileLastNameText = (EditText) view.findViewById(R.id.childProfileLastNameText);
        childProfileDOBText = (EditText) view.findViewById(R.id.childProfileDOBText);
        childProfileEmergencyPhoneText = (EditText) view.findViewById(R.id.childProfileEmergencyPhoneText);
        childProfileStreetAddressText = (EditText) view.findViewById(R.id.childProfileStreetAddressText);
        childProfileCityText = (EditText) view.findViewById(R.id.childProfileCityText);
        childProfilePincodeText = (EditText) view.findViewById(R.id.childProfilePincodeText);

        if (!genders.contains("Male")) {
            genders.add("Male");
        }
        if (!genders.contains("Female")) {
            genders.add("Female");
        }
        if (!genders.contains("Transgender")) {
            genders.add("Transgender");
        }
        if (preferences.contains("states")) {
            try {
                JSONArray stateArray = new JSONArray(preferences.getString("states", ""));
                String state;

                for (int i = 0; i < stateArray.length(); i++) {
                    JSONObject stateDetails = stateArray.getJSONObject(i);

                    state = stateDetails.getString("state_name");
                    if (!states.contains(state)) {
                        states.add(state);
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        if (preferences.contains("bloodGroupTypes")) {
            try {
                bloodGroupArray = new JSONArray(preferences.getString("bloodGroupTypes", ""));
                String bloodGroup;

                for (int i = 0; i < bloodGroupArray.length(); i++) {
                    JSONObject bloodGroopDetails = bloodGroupArray.getJSONObject(i);

                    bloodGroup = bloodGroopDetails.getString("blood_group_name");

                    if (!bloodGroups.contains(bloodGroup)) {
                        bloodGroups.add(bloodGroup);
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        childProfileGenderSpinner = (Spinner) view.findViewById(R.id.childProfileGenderSpinner);
        childProfileStateSpinner = (Spinner) view.findViewById(R.id.childProfileStateSpinner);
        childProfileBloodGroupSpinner = (Spinner) view.findViewById(R.id.childProfileBloodgroupSpinner);

        ArrayAdapter<String> genderAdapter = new ArrayAdapter<String>(view.getContext(), android.R.layout.simple_spinner_item, genders);
        genderAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        childProfileGenderSpinner.setAdapter(genderAdapter);

        ArrayAdapter<String> stateAdapter = new ArrayAdapter<String>(view.getContext(), android.R.layout.simple_spinner_item, states);
        stateAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        childProfileStateSpinner.setAdapter(stateAdapter);

        ArrayAdapter<String> bloodgroupAdapter = new ArrayAdapter<>(view.getContext(), android.R.layout.simple_spinner_item, bloodGroups);
        bloodgroupAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        childProfileBloodGroupSpinner.setAdapter(bloodgroupAdapter);
        studentName = "";
        imgChildProfile = (ImageView) view.findViewById(R.id.imgChildProfile);
        final DatePickerDialog.OnDateSetListener dateOfBirthListener = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {

                dateOfBirthCalendarDialog.set(Calendar.YEAR, year);
                dateOfBirthCalendarDialog.set(Calendar.MONTH, monthOfYear);
                dateOfBirthCalendarDialog.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                childProfileDOBText.setText(ddf.format(dateOfBirthCalendarDialog.getTime()));

            }

        };

        childProfileDOBText.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                new DatePickerDialog(view.getContext(), dateOfBirthListener, dateOfBirthCalendarDialog
                        .get(Calendar.YEAR), dateOfBirthCalendarDialog.get(Calendar.MONTH),
                        dateOfBirthCalendarDialog.get(Calendar.DAY_OF_MONTH)).show();
            }
        });
        displayChildProfileLayout = (LinearLayout) view.findViewById(R.id.displayChildProfileLayout);
        ;
        editChildProfileLayout = (LinearLayout) view.findViewById(R.id.editChildProfileLayout);
        ;

//        Button editChildProfileButton = (Button) view.findViewById(R.id.editChildProfileButton);
        editChildProfileButton = (FloatingActionButton) view.findViewById(R.id.editChildProfileButton);

        Button updateChildProfileButton = (Button) view.findViewById(R.id.updateChildProfileButton);
        Button cancelEditChildProfileButton = (Button) view.findViewById(R.id.cancelEditChildProfileButton);

        editChildProfileButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                hideProfile();
            }
        });

        updateChildProfileButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
//                builder.setTitle(R.string.app_name);
                builder.setMessage("Are you sure you want to update profile?");
//                builder.setIcon(R.mipmap.ic_launcher);
                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                });
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                        updateProfile();
                    }
                });
                AlertDialog alert = builder.create();
                alert.show();
            }
        });

        cancelEditChildProfileButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                displayProfile();
            }
        });

        SharedPreferences.Editor edit = preferences.edit();
        edit.putString("selectChildDetailsTabNumber", "1");
        edit.commit();

        if (preferences.getString("activeFeature", "").equals(Integer.toString(Constant.PARENT_FEATURE_CHILD_PROFILE))) {
            displayProfile();
        }
        setData();
        displayProfilePhoto();

        return view;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            getFragmentManager().beginTransaction().detach(this).attach(this).commit();
        }
    }

    public void displayProfile() {
        displayChildProfileLayout.setVisibility(View.VISIBLE);
        editChildProfileButton.setVisibility(View.VISIBLE);
        editChildProfileLayout.setVisibility(View.GONE);
    }

    public void hideProfile() {
        displayChildProfileLayout.setVisibility(View.GONE);
        editChildProfileButton.setVisibility(View.GONE);
        editChildProfileLayout.setVisibility(View.VISIBLE);
    }

    public void displayProfilePhoto() {
        String photoUrl = "";
        String c=preferences.getString("studentPhotos","");
        Log.e(TAG, "displayProfilePhoto111: "+c );
        if (preferences.contains("studentPhotos")) {
            try {
                JSONArray photoArray = new JSONArray(preferences.getString("studentPhotos", ""));
                for (int i = 0; i < photoArray.length(); i++) {
                    JSONObject photoDetails = photoArray.getJSONObject(i);
                    if (photoDetails.getString("student_photo_type_id").equals("1")) {
                        photoUrl = photoDetails.getString("photo_url");
                    }
                    Log.e(TAG, "displayProfilePhoto: "+photoUrl );

                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        if (!photoUrl.isEmpty()) {
            Glide.with(getContext()).load(photoUrl)
                    .crossFade()
                    .thumbnail(0.5f)
                    .bitmapTransform(new CircleTransform(getContext()))
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(imgChildProfile);
        }
        else {

            ColorGenerator cGenerator = ColorGenerator.DEFAULT;
            TextDrawable drawable = TextDrawable.builder()
                    .buildRound(studentName.substring(0,1), cGenerator.getColor(studentName));
            imgChildProfile.setImageDrawable(drawable);
        }
    }

    public void updateProfile() throws JSONException {

        String firstName = childProfileFirstNameText.getText().toString();
        String middleName = childProfileMiddleNameText.getText().toString();
        String lastName = childProfileLastNameText.getText().toString();
        String gender = childProfileGenderSpinner.getSelectedItem().toString();
        String dateOfBirth = rdf.format(dateOfBirthCalendarDialog.getTime());
        String phone = childProfileEmergencyPhoneText.getText().toString();
        String streetAddress = childProfileStreetAddressText.getText().toString();
        String city = childProfileCityText.getText().toString();
        String pincode = childProfilePincodeText.getText().toString();
        String state = childProfileStateSpinner.getSelectedItem().toString();
        String bloodGroup = childProfileBloodGroupSpinner.getSelectedItem().toString();

        int validationSuccessfull = 1;
        if (firstName.trim().isEmpty()) {
            validationSuccessfull = 0;
            Utility.showSnackBar("Please enter first name", getActivity());

        } else if (lastName.trim().isEmpty()) {
            validationSuccessfull = 0;
            Utility.showSnackBar("Please enter last name", getActivity());

        }    else if (!Utility.isValidMobileNumber(phone)) {
            validationSuccessfull = 0;
            Utility.showSnackBar("Please enter valid phone number", getActivity());
        }
        Log.e("Blood Group :::", bloodGroup);
        Log.e("Blood Group :::", getBloodGroupId(bloodGroup));

        if (validationSuccessfull == 1) {
            try {
                JSONObject data = new JSONObject();
                data.put("schoolId","1" );
                data.put("studentId", "58");
                data.put("firstName", firstName);
                data.put("middleName", middleName);
                data.put("lastName", lastName);
                data.put("gender", gender);
                data.put("dateOfBirth", dateOfBirth);
                data.put("phone", phone);
                data.put("streetAddress", streetAddress);
                data.put("city", city);
                data.put("pincode", pincode);
                data.put("state", state);
                data.put("bloodGroup", getBloodGroupId(bloodGroup));
                Log.e("Blood Group :::",""+data);
                Log.e("Blood Group :::",""+data);
            }
        }
    }

    public String getBloodGroupId(String blo


                ApiCallHandler profileHandler = new ApiCallHandler("POST","https://schooladmin.headwaytechies.com/api/students/updateProfile",view.getContext(),Constant.RELOAD_CHILD_PROFILE_AFTER_UPDATE );
                profileHandler.execute(data.toString());
            } catch (org.json.JSONException e) {
odGroupName) {
        int length = bloodGroupArray.length();
        String bloodGroupId = "0";
        for (int i = 0; i < length; i++) {
            try {
                JSONObject bloodGroopDetails = bloodGroupArray.getJSONObject(i);
                if (bloodGroupName.equals(bloodGroopDetails.getString("blood_group_name"))) {
                    bloodGroupId = bloodGroopDetails.getString("id");
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return bloodGroupId;
    }
    public void setData(){
        if (preferences.contains("studentProfile")) {
            try {

                TextView txtChildProfileName = (TextView) view.findViewById(R.id.txtChildProfileName);
                TextView txtChildProfileDOB = (TextView) view.findViewById(R.id.txtChildProfileDOB);
                TextView txtChildProfileEmergencyPhone = (TextView) view.findViewById(R.id.txtChildProfileEmergencyPhone);
                TextView txtChildProfileAddress = (TextView) view.findViewById(R.id.txtChildProfileAddress);
                TextView txtChildProfileGender = (TextView) view.findViewById(R.id.txtChildProfileGender);
                TextView txtChildBloodGroup = view.findViewById(R.id.txtChildBloodGroup);
                JSONObject profileDetails = new JSONObject(preferences.getString("studentProfile", ""));
                Log.e(TAG, "onCreateView: "+profileDetails );
                String firstName = profileDetails.getString("first_name");
                String middleName = profileDetails.getString("middle_name");
                String lastName = profileDetails.getString("last_name");
                studentName = firstName+" "+lastName;
                String streetAddress = profileDetails.getString("street_address");
                String city = profileDetails.getString("city");
                String pincode = profileDetails.getString("pincode");
                String state = profileDetails.getString("state");
                String dateOfBirth = profileDetails.getString("date_of_birth");
                String bloodGroup = profileDetails.getString("blood_group_name");


                txtChildProfileName.setText(firstName+" "+middleName+" "+lastName);
                txtChildProfileDOB.setText(profileDetails.getString("date_of_birth"));
                txtChildProfileEmergencyPhone.setText(profileDetails.getString("phone"));
                txtChildProfileAddress.setText(streetAddress+", "+city+" - "+pincode+", "+state);
                txtChildProfileGender.setText(profileDetails.getString("gender"));
                txtChildBloodGroup.setText(profileDetails.getString("blood_group_name"));

                childProfileFirstNameText.setText(firstName);
                childProfileMiddleNameText.setText(middleName);
                childProfileLastNameText.setText(lastName);
                try {
                    dateOfBirthCalendarDialog.setTime(rdf.parse(dateOfBirth));
                    txtChildProfileDOB.setText(ddf.format(rdf.parse(dateOfBirth)));
                    childProfileDOBText.setText(ddf.format(rdf.parse(dateOfBirth)));
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                childProfileEmergencyPhoneText.setText(profileDetails.getString("phone"));
                childProfileStreetAddressText.setText(streetAddress);
                childProfileCityText.setText(city);
                childProfilePincodeText.setText(pincode);

                childProfileGenderSpinner.setSelection(genders.indexOf(profileDetails.getString("gender")));
                childProfileStateSpinner.setSelection(states.indexOf(state));
                childProfileBloodGroupSpinner.setSelection(bloodGroups.indexOf(bloodGroup));

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }


    }




