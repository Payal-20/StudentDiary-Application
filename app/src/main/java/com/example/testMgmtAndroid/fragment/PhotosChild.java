package com.example.testMgmtAndroid.fragment;

import static android.app.Activity.RESULT_OK;
import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.testMgmtAndroid.R;
import com.example.testMgmtAndroid.activity.Constant;
import com.example.testMgmtAndroid.adapter.DocumentAdapter;
import com.example.testMgmtAndroid.helper.FilePath;
import com.example.testMgmtAndroid.helper.Utility;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import net.gotev.uploadservice.MultipartUploadRequest;
import net.gotev.uploadservice.UploadNotificationConfig;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Document;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;


public class PhotosChild extends Fragment implements SingleUploadBroadcastReceiver.Delegate {
    String imgUri = "";
    EditText editText;
    ImageView imageView;
    List<String> photoTypeIds = new ArrayList<String>();
    List<String> photoTypes = new ArrayList<String>();
    Spinner photoTypeSpinner;
    EditText childUploadPhoto;
    TextView childPhotoInstructions, childPhotoNotAvailable;
    CardView uploadChildPhotoCardView;
    FloatingActionButton editChildPhotoButton;
    SharedPreferences preferences;
    List<MyDocument> photoList;
    RecyclerView childPhotosRecyclerView;
    //file request code
    private int PICK_FILE_REQUEST = 1;

    //Uri to store the image uri
    private Uri filePath;
    String actualPath;
    DocumentAdapter photoAdapter;
    View view;

    private final SingleUploadBroadcastReceiver uploadReceiver = new SingleUploadBroadcastReceiver();
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,

                             Bundle savedInstanceState) {

       view = inflater.inflate(R.layout.fragment_photos_child, container, false);
        preferences = view.getContext().getSharedPreferences("user_data", view.getContext().MODE_PRIVATE);

        if (!photoTypeIds.contains("0")) {
            photoTypeIds.add("0");
        }
        if (!photoTypes.contains(getResources().getString(R.string.select_photo_type_msg))) {
            photoTypes.add(getResources().getString(R.string.select_photo_type_msg));
        }

        photoTypeSpinner = (Spinner) view.findViewById(R.id.childPhotoTypeSpinner);

//        Log.e("photos", preferences.getString("studentPhotos",""));

        if (preferences.contains("studentPhotoTypes")) {
            try {
                JSONArray photoTypeArray = new JSONArray(preferences.getString("studentPhotoTypes", ""));
                String photoTypeId, photoType;

                for (int i = 0; i < photoTypeArray.length(); i++) {
                    JSONObject photoTypeDetails = photoTypeArray.getJSONObject(i);

                    photoTypeId = photoTypeDetails.getString("student_photo_type_id");
                    if (!photoTypeIds.contains(photoTypeId)) {
                        photoTypeIds.add(photoTypeId);
                    }

                    photoType = photoTypeDetails.getString("student_photo_type");
                    if (!photoTypes.contains(photoType)) {
                        photoTypes.add(photoType);
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        ArrayAdapter<String> photoTypeAdapter = new ArrayAdapter<String>(view.getContext(), android.R.layout.simple_spinner_item, photoTypes);
        photoTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        photoTypeSpinner.setAdapter(photoTypeAdapter);

        childUploadPhoto = (EditText) view.findViewById(R.id.childUploadPhoto);

        childPhotoInstructions = (TextView) view.findViewById(R.id.childPhotoInstructions);
        childPhotoNotAvailable = (TextView) view.findViewById(R.id.childPhotoNotAvailable);

        childUploadPhoto.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                showFileChooser();
            }
        });

        Button uploadChildPhotoButton = (Button) view.findViewById(R.id.uploadChildPhotoButton);

        Button cancelEditChildPhotoButton = (Button) view.findViewById(R.id.cancelEditChildPhotoButton);

        editChildPhotoButton = (FloatingActionButton) view.findViewById(R.id.editChildPhotoButton);

        uploadChildPhotoCardView = (CardView) view.findViewById(R.id.uploadChildPhotoCardView);


        childPhotosRecyclerView = (RecyclerView) view.findViewById(R.id.childPhotosRecyclerView);


        if (preferences.contains("photoInstructions") && !preferences.getString("photoInstructions", "").isEmpty()) {
            childPhotoInstructions.setVisibility(View.VISIBLE);
            childPhotoInstructions.setText(preferences.getString("photoInstructions", ""));
        } else {
            childPhotoInstructions.setVisibility(View.GONE);
            childPhotoInstructions.setText("");
        }

        uploadChildPhotoButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                uploadPhoto();
                Log.e(TAG, "onClick: ");

            }
        });

        editChildPhotoButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                hidePhotoList();
            }
        });

        cancelEditChildPhotoButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                displayPhotoList();
            }
        });

        if (preferences.getString("activeFeature", "").equals(7)) {
            displayPhotos();
            displayPhotoList();
        }
        return view;
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //you can set the title for your toolbar here for different fragments different titles
//        getActivity().setTitle("Child Details");
    }

    public void displayPhotoList() {
        childPhotosRecyclerView.setVisibility(View.VISIBLE);
        editChildPhotoButton.setVisibility(View.VISIBLE);
        uploadChildPhotoCardView.setVisibility(View.GONE);

        if (photoList.size() <= 0) {
            childPhotoNotAvailable.setVisibility(View.VISIBLE);
        } else {
            childPhotoNotAvailable.setVisibility(View.GONE);
        }
    }

    public void hidePhotoList() {
        childPhotosRecyclerView.setVisibility(View.GONE);
        childPhotoNotAvailable.setVisibility(View.GONE);
        editChildPhotoButton.setVisibility(View.GONE);
        uploadChildPhotoCardView.setVisibility(View.VISIBLE);
    }


    private void displayPhotos() {

        loadPhotoAdapter();

        childPhotosRecyclerView.setHasFixedSize(true);

        LinearLayoutManager ChildrenLayoutManager = new LinearLayoutManager(view.getContext());
        ChildrenLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        childPhotosRecyclerView.setLayoutManager(ChildrenLayoutManager);

        childPhotosRecyclerView.setItemAnimator(new DefaultItemAnimator());

        childPhotosRecyclerView.setAdapter(photoAdapter);
        childPhotosRecyclerView.invalidate();
    }


    private void loadPhotoAdapter() {
        photoList = new ArrayList<>();
        if (preferences.contains("studentPhotos")) {
            try {
                JSONArray photoArray = new JSONArray(preferences.getString("studentPhotos", ""));

                for (int i = 0; i < photoArray.length(); i++) {
                    JSONObject photoDetails = photoArray.getJSONObject(i);

                    photoList.add(new MyDocument(Integer.parseInt(photoDetails.getString("student_photo_id")), photoDetails.getString("student_photo_type"), photoDetails.getString("photo_actual_filename"), photoDetails.getString("photo_url"), Integer.parseInt(photoDetails.getString("student_id")), Constant.DOCUMENT_TYPE_STUDENT_PHOTO));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        if (photoAdapter != null && photoAdapter.getItemCount() > 0) {
            photoAdapter.clear();
        }
        photoAdapter = new DocumentAdapter(photoList);
        photoAdapter.notifyDataSetChanged();

        if(preferences.getString("selectChildDetailsTabNumber", "0").equals("2") && photoList.size() <= 0) {
            Utility.showSnackBar(getResources().getString(R.string.no_photo_msg), getActivity());
        }
    }

    public void uploadPhoto() {
        try {
//        String actualPath = FilePath.getPath(getContext(), filePath);

            String typeId = photoTypeIds.get(photoTypeSpinner.getSelectedItemPosition());
//        Log.e("selectedChildSchoolId", schoolId);
//        Log.e("selectedChildStudentId", studentId);
            int validationSuccessfull = 1;

            if (typeId.equals("0")) {
                validationSuccessfull = 0;
                Utility.showSnackBar("Please select photo type", getActivity());
            } else if (childUploadPhoto.equals("") || actualPath == null) {
                validationSuccessfull = 0;
                Utility.showSnackBar("Please select photo", getActivity());
            }

            if (validationSuccessfull == 1) {
                String uploadId = UUID.randomUUID().toString();
                uploadReceiver.setDelegate(this);
                uploadReceiver.setUploadID(uploadId);

                String x = preferences.getString("token", "");
                Log.e(TAG, "uploadPhoto: " + x);


                //Creating a multi part request
                new MultipartUploadRequest(getContext(), uploadId, "https://schooladmin.headwaytechies.com/api/students/uploadPhoto")
                        .addFileToUpload(actualPath, "photoAttachment[]") //Adding file
                        .addHeader("token", preferences.getString("token", ""))
                        .addParameter("schoolId", "1")
                        .addParameter("studentId", "58")
                        .addParameter("photoTypeId", typeId)
//                        .setNotificationConfig(new UploadNotificationConfig())
                        .setMaxRetries(2)
                        .startUpload();


                Utility.showProgressDialog(getContext(), "Uploading");
            }
        } catch (Exception exc) {
            Utility.showSnackBar("Something went wrong. Please try again.", getActivity());
        }
    }

    //method to show file chooser
    private void showFileChooser() {
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {

            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);

//            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

//            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Files.getContentUri("pdf"));

//            Intent intent = new Intent(Intent.ACTION_VIEW);
//            intent.setType("application/pdf");
//            intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);

            startActivityForResult(Intent.createChooser(intent, "Select File"), PICK_FILE_REQUEST);
        } else {
            Utility.showSnackBar("For uploading photo, storage permission required to access files on device", getActivity());
            requestStoragePermission();
        }
    }

    //handling the image chooser activity result
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            if (requestCode == PICK_FILE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
                filePath = data.getData();

                //getting the actual path of the image
                actualPath = FilePath.getPath(getContext(), filePath);

                File file = new File(actualPath);
//            File file = new File(filePath.getPath());
                childUploadPhoto.setText(file.getName());

            }
        } catch (IllegalArgumentException Iexc) {
            Utility.showSnackBar("File cannot be retrieved", getActivity());
        } catch (Exception exc) {
            Utility.showSnackBar("Something went wrong. Please try again", getActivity());
        }
    }

    //Requesting permission
    private void requestStoragePermission() {
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)
            return;

        if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            //If the user has denied the permission previously your code will come to this block
            //Here you can explain why you need this permission
            //Explain here why you need this permission
        }
        //And finally ask for the permission
        ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 123);
    }


    //This method will be called when the user will tap on allow or deny
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        //Checking the request code of our request
        if (requestCode == 123) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(getContext(), "Now you can select photo", Toast.LENGTH_LONG).show();
            } else {
//                Toast.makeText(getContext(), "You denied the permission", Toast.LENGTH_LONG).show();
            }
        }
    }

    //SingleUploadBroadcastReceiver
    @Override
    public void onResume() {
        super.onResume();
        uploadReceiver.register(getContext());
    }

    @Override
    public void onPause() {
        super.onPause();
        uploadReceiver.unregister(getContext());
    }

    private void clearPage() {
        photoTypeSpinner.setSelection(0);
        actualPath = null;
        childUploadPhoto.setText("");
    }

    @Override
    public void onProgress(int progress) {

    }

    @Override
    public void onProgress(long uploadedBytes, long totalBytes) {

    }

    @Override
    public void onError(Exception exception) {

    }

    @Override
    public void onCompleted(int serverResponseCode, byte[] serverResponseBody) throws UnsupportedEncodingException, JSONException {
        Utility.hideProgressDialog();

        JSONObject response = new JSONObject(new String(serverResponseBody, "UTF-8"));
        Log.e(TAG, "onCompleted: " + response);

        if (response.getString("success").equals("true")) {
            Toast.makeText(getContext(), "Photo Uploaded successfully", Toast.LENGTH_LONG).show();
            this.setPreference(response);
            clearPage();
            displayPhotos();
            displayPhotoList();
            updateHomePageProfilePhoto();
            ProfileChild profileChild=new ProfileChild();

            reloadChildDetailsFragment();
        } else {
            Utility.showSnackBar(response.getString("statusMsg"), getActivity());
        }
    }

    @Override
    public void onCancelled() {
        Utility.hideProgressDialog();

    }

    private void setPreference(JSONObject response) throws JSONException {
        SharedPreferences.Editor edit = preferences.edit();

        Iterator<?> keys = response.keys();

        while (keys.hasNext()) {
            String key = (String) keys.next();
            if (!new String("success").equals(key)
                    && !new String("responseCode").equals(key)
                    && !new String("statusMsg").equals(key)) {
                edit.putString(key, response.getString(key));
            }
        }

        edit.putString("selectChildDetailsTabNumber", "2");

        edit.commit();
    }
    public void reloadChildDetailsFragment() {
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        Fragment currentFragment;

        ft.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out, R.anim.right_to_left, R.anim.right_to_left);
        currentFragment = getFragmentManager().findFragmentById(R.id.fragment_container);
        ft.detach(currentFragment);
        ft.replace(R.id.fragment_container, new ProfileChild());
        ft.commitAllowingStateLoss();
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
        if (preferences.contains("children")) {
            try {
                JSONArray childrenArray = new JSONArray(preferences.getString("children", ""));

                for (int i = 0; i < childrenArray.length(); i++) {
                    JSONObject childrenDetails = childrenArray.getJSONObject(i);

                    if (childrenDetails.getString("studentId").equals(preferences.getString("selectedChildStudentId", "0"))) {
                        childrenDetails.put("profileImageUrl", photoUrl);
                    }
                }
                SharedPreferences.Editor edit = preferences.edit();
                edit.putString("children", childrenArray.toString());
                edit.commit();
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

    }

}
