package com.example.testMgmtAndroid.fragment;

import static android.app.Activity.RESULT_OK;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.provider.DocumentsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.testMgmtAndroid.R;
import com.example.testMgmtAndroid.activity.Constant;
import com.example.testMgmtAndroid.adapter.DocumentAdapter;
import com.example.testMgmtAndroid.helper.FilePath;
import com.example.testMgmtAndroid.helper.Utility;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputLayout;

import net.gotev.uploadservice.MultipartUploadRequest;

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


public class DocoumentsChild extends Fragment implements SingleUploadBroadcastReceiver.Delegate {

View view;
    EditText childDocumentNameText, childUploadDocument;
    TextView childDocumentInstructions, childDocumentNotAvailable;
    TextInputLayout childDocumentNameTextInputLayout;
    Spinner documentTypeSpinner;
    CardView uploadChildDocumentCardView;
    FloatingActionButton editChildDocumentButton;
    SharedPreferences preferences;

    List<String> documentTypeIds = new ArrayList<String>();
    List<String> documentTypes = new ArrayList<String>();
    List<MyDocument> documentList;
    DocumentAdapter documentAdapter;
    RecyclerView childDocumentsRecyclerView;

    int documentTypeSpinnerClickCount;
    //file request code
    private int PICK_FILE_REQUEST = 1;

    //Uri to store the image uri
    private Uri filePath;
    String actualPath;

    private final SingleUploadBroadcastReceiver uploadReceiver =
            new SingleUploadBroadcastReceiver();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_docouments_child, container, false);
        preferences = view.getContext().getSharedPreferences(Constant.USER_DATA, view.getContext().MODE_PRIVATE);

        if (!documentTypeIds.contains("0")) {
            documentTypeIds.add("0");
        }
        if (!documentTypes.contains(getResources().getString(R.string.select_document_type_msg))) {
            documentTypes.add(getResources().getString(R.string.select_document_type_msg));
        }

        documentTypeSpinner = (Spinner) view.findViewById(R.id.childDocumentTypeSpinner);

        if (preferences.contains("documentTypes")) {
            try {
                JSONArray documentTypeArray = new JSONArray(preferences.getString("documentTypes", ""));
                String documentTypeId, documentType;

                for (int i = 0; i < documentTypeArray.length(); i++) {
                    JSONObject documentTypeDetails = documentTypeArray.getJSONObject(i);

                    documentTypeId = documentTypeDetails.getString("document_type_id");
                    if (!documentTypeIds.contains(documentTypeId)) {
                        documentTypeIds.add(documentTypeId);
                    }

                    documentType = documentTypeDetails.getString("document_type");
                    if (!documentTypes.contains(documentType)) {
                        documentTypes.add(documentType);
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        ArrayAdapter<String> documentTypeAdapter = new ArrayAdapter<String>(view.getContext(), android.R.layout.simple_spinner_item, documentTypes);
        documentTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        documentTypeSpinner.setAdapter(documentTypeAdapter);

        childDocumentNameTextInputLayout= (TextInputLayout) view.findViewById(R.id.childDocumentNameTextInputLayout);

        childDocumentNameText= (EditText) view.findViewById(R.id.childDocumentNameText);

        childUploadDocument= (EditText) view.findViewById(R.id.childUploadDocument);

        childDocumentInstructions= (TextView) view.findViewById(R.id.childDocumentInstructions);
        childDocumentNotAvailable= (TextView) view.findViewById(R.id.childDocumentNotAvailable);

        childUploadDocument.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                showFileChooser();
            }
        });

        Button uploadChildDocumentButton = (Button) view.findViewById(R.id.uploadChildDocumentButton);

        Button cancelEditChildDocumentButton = (Button) view.findViewById(R.id.cancelEditChildDocumentButton);

        editChildDocumentButton = (FloatingActionButton) view.findViewById(R.id.editChildDocumentButton);

        uploadChildDocumentCardView = (CardView) view.findViewById(R.id.uploadChildDocumentCardView);

        childDocumentsRecyclerView = (RecyclerView) view.findViewById(R.id.childDocumentsRecyclerView);

        if (preferences.contains("documentInstructions") && !preferences.getString("documentInstructions", "").isEmpty()) {
            childDocumentInstructions.setVisibility(View.VISIBLE);
            childDocumentInstructions.setText(preferences.getString("documentInstructions", ""));
        }
        else {
            childDocumentInstructions.setVisibility(View.GONE);
            childDocumentInstructions.setText("");
        }

        childDocumentNameTextInputLayout.setVisibility(View.GONE);

        documentTypeSpinnerClickCount = 0;
        documentTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                if (documentTypeSpinnerClickCount >0) {
                    if (documentTypes.get(position).toLowerCase().equals("other")) {
                        childDocumentNameTextInputLayout.setVisibility(View.VISIBLE);
                    }
                    else {
                        childDocumentNameTextInputLayout.setVisibility(View.GONE);
                    }
                }

                documentTypeSpinnerClickCount = documentTypeSpinnerClickCount + 1;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {

            }

        });

        editChildDocumentButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                hideDocumentList();
            }
        });

        uploadChildDocumentButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                uploadDocument();
            }
        });

        cancelEditChildDocumentButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                displayDocumentList();
            }
        });

        if (preferences.getString("activeFeature", "").equals(Integer.toString(Constant.PARENT_FEATURE_CHILD_PROFILE))) {
            displayDocuments();
            displayDocumentList();
        }
        return view;
    }
    @Override
    public void onDestroyView() {
        documentTypeSpinnerClickCount = 0;
        super.onDestroyView();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //you can set the title for your toolbar here for different fragments different titles
//        getActivity().setTitle("Child Details");
    }

    public void displayDocumentList() {
        childDocumentsRecyclerView.setVisibility(View.VISIBLE);
        editChildDocumentButton.setVisibility(View.VISIBLE);
        uploadChildDocumentCardView.setVisibility(View.GONE);

        if (documentList.size() <= 0) {
            childDocumentNotAvailable.setVisibility(View.VISIBLE);
        }
        else {
            childDocumentNotAvailable.setVisibility(View.GONE);
        }
    }

    public void hideDocumentList() {
        childDocumentsRecyclerView.setVisibility(View.GONE);
        childDocumentNotAvailable.setVisibility(View.GONE);
        editChildDocumentButton.setVisibility(View.GONE);
        uploadChildDocumentCardView.setVisibility(View.VISIBLE);
    }

    private void loadDocumentAdapter() {
        documentList = new ArrayList<>();
        if (preferences.contains("studentDocuments")) {
            try {
                JSONArray documentArray = new JSONArray(preferences.getString("studentDocuments", ""));

                for (int i = 0; i < documentArray.length(); i++) {
                    JSONObject documentDetails = documentArray.getJSONObject(i);

                    documentList.add(new MyDocument(Integer.parseInt(documentDetails.getString("student_document_id")), documentDetails.getString("document_name"), documentDetails.getString("document_actual_filename"), documentDetails.getString("document_url"), Integer.parseInt(documentDetails.getString("student_id")), Constant.DOCUMENT_TYPE_STUDENT_DOCUMENT));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        if (documentAdapter != null && documentAdapter.getItemCount() > 0) {
            documentAdapter.clear();
        }
        documentAdapter = new DocumentAdapter(documentList);
        documentAdapter.notifyDataSetChanged();

        if(preferences.getString("selectChildDetailsTabNumber", "0").equals("3") && documentList.size() <= 0) {
            Utility.showSnackBar(getResources().getString(R.string.no_document_msg), getActivity());
        }
    }

    private void displayDocuments() {

        loadDocumentAdapter();

        childDocumentsRecyclerView.setHasFixedSize(true);

        LinearLayoutManager documentLayoutManager = new LinearLayoutManager(view.getContext());
        documentLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        childDocumentsRecyclerView.setLayoutManager(documentLayoutManager);

        childDocumentsRecyclerView.setItemAnimator(new DefaultItemAnimator());

        childDocumentsRecyclerView.setAdapter(documentAdapter);
        childDocumentsRecyclerView.invalidate();

    }

    public void uploadDocument() {

        try {
        String actualPath = FilePath.getPath(getContext(), filePath);

            String schoolId = preferences.getString("selectedChildSchoolId", "0");
            String studentId = preferences.getString("selectedChildStudentId", "0");
            String documentTypeId = documentTypeIds.get(documentTypeSpinner.getSelectedItemPosition());
            String documentType = documentTypes.get(documentTypeSpinner.getSelectedItemPosition());
            String documentName = childDocumentNameText.getText().toString();
            String selectedDocumentName = childUploadDocument.getText().toString();

//        Log.e("selectedChildSchoolId", schoolId);
//        Log.e("selectedChildStudentId", studentId);

            int validationSuccessfull = 1;

            if (documentTypeId.equals("0")) {
                validationSuccessfull = 0;
                Utility.showSnackBar("Please select document type", getActivity());
            }
            else if (documentType.toLowerCase().equals("other") && documentName.trim().isEmpty()) {
                validationSuccessfull = 0;
                Utility.showSnackBar("Please enter document name", getActivity());
            } else if (selectedDocumentName.trim().isEmpty() || actualPath == null) {
                validationSuccessfull = 0;
                Utility.showSnackBar("Please select document", getActivity());
            }

            if (validationSuccessfull == 1) {
                String uploadId = UUID.randomUUID().toString();

                uploadReceiver.setDelegate(this);
                uploadReceiver.setUploadID(uploadId);

                //Creating a multi part request
                new MultipartUploadRequest(getContext(), uploadId, "https://schooladmin.headwaytechies.com/api/students/uploadDocument")
                        .addFileToUpload(actualPath, "documentAttachment[]") //Adding file
                        .addHeader("token", preferences.getString("token", ""))
                        .addParameter("schoolId", "1")
                        .addParameter("studentId", "58")
                        .addParameter("documentTypeId", documentTypeId)
                        .addParameter("documentName", documentName)
//                        .setNotificationConfig(new UploadNotificationConfig())
                        .setMaxRetries(2)
                        .startUpload();

                Utility.showProgressDialog(getContext(), "Uploading");
            }
        } catch (Exception exc) {
//            Utility.showSnackBar(getResources().getString(R.string.app_error_msg), getActivity());
        }
    }

    //method to show file chooser
    private void showFileChooser() {
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {

//            Intent intent = new Intent();
//            intent.setType("image/*");
//            intent.setAction(Intent.ACTION_GET_CONTENT);

            Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.setType("*/*");
            String[] mimetypes = {"image/*", "application/pdf", "text/plain", "application/msword", "application/vnd.openxmlformats-officedocument.wordprocessingml.document"};
            intent.putExtra(Intent.EXTRA_MIME_TYPES, mimetypes);

//            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

//            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Files.getContentUri("pdf"));

//            Intent intent = new Intent(Intent.ACTION_VIEW);
//            intent.setType("application/pdf");
//            intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);

            startActivityForResult(Intent.createChooser(intent, "Select File"), PICK_FILE_REQUEST);
        }
        else {
            Utility.showSnackBar("For uploading document, storage permission required to access files on device", getActivity());
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
                childUploadDocument.setText(file.getName());

            }
        } catch (IllegalArgumentException Iexc) {
//            Utility.showSnackBar(getResources().getString(R.string.file_retrieve_error_msg), getActivity());
        } catch (Exception exc) {
//            Utility.showSnackBar(getResources().getString(R.string.app_error_msg), getActivity());
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
        ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, Constant.STORAGE_PERMISSION_CODE);
    }


    //This method will be called when the user will tap on allow or deny
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        //Checking the request code of our request
        if (requestCode == Constant.STORAGE_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(getContext(), "Now you can select document", Toast.LENGTH_LONG).show();
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

    @Override
    public void onProgress(int progress) {

    }

    @Override
    public void onProgress(long uploadedBytes, long totalBytes) {

    }

    @Override
    public void onError(Exception exception) {
//        progressDialog.hide();
        Utility.hideProgressDialog();
        Utility.showSnackBar(getResources().getString(R.string.exception_msg), getActivity());
    }

    @Override
    public void onCancelled() {
//        progressDialog.hide();
        Utility.hideProgressDialog();
    }

    @Override
    public void onCompleted(int serverResponseCode, byte[] serverResponseBody) throws UnsupportedEncodingException, JSONException {

        Utility.hideProgressDialog();

        JSONObject response = new JSONObject(new String(serverResponseBody, "UTF-8"));
        if (response.getString("success").equals("true")) {
            Toast.makeText(getContext(), "Document Uploaded successfully", Toast.LENGTH_LONG).show();
            this.setPreference(response);
            clearPage();
            displayDocuments();
            displayDocumentList();
        } else {
            Utility.showSnackBar(response.getString("statusMsg"), getActivity());
        }
    }

    private void clearPage() {
        childDocumentNameText.setText("");
        actualPath = null;
        childUploadDocument.setText("");
    }

    private void setPreference(JSONObject response) throws JSONException {
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

        edit.putString("selectChildDetailsTabNumber", "3");

        edit.commit();
    }
}