package com.example.testMgmtAndroid.adapter;

import static com.example.testMgmtAndroid.activity.Constant.RELOAD_CHILD_DOCUMENT_AFTER_DELETE;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.testMgmtAndroid.R;
import com.example.testMgmtAndroid.activity.Constant;
import com.example.testMgmtAndroid.activity.MainActivity;
import com.example.testMgmtAndroid.fragment.MyDocument;
import com.example.testMgmtAndroid.fragment.ProfileChild;
import com.example.testMgmtAndroid.helper.ApiCallHandler;
import com.example.testMgmtAndroid.helper.DownloadAndOpenFile;
import com.example.testMgmtAndroid.helper.Utility;

import org.json.JSONObject;
import org.w3c.dom.Document;

import java.util.Arrays;
import java.util.List;

public class DocumentAdapter  extends RecyclerView.Adapter<DocumentAdapter.DocumentViewHolder> {
    //this context we will use to inflate the layout
    private Context mCtx;

    //we are storing all the documents in a list
    private List<MyDocument> documentList;

    //getting the context and documents list with constructor
    public DocumentAdapter(List<MyDocument> documentList) {
//        this.mCtx = mCtx;
        this.documentList = documentList;
    }

    @Override
    public DocumentViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //inflating and returning our view holder
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.layout_document_list, null);
        return new DocumentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(DocumentViewHolder holder, int position) {
        //getting the photos/documents of the specified position
        MyDocument document = documentList.get(position);

        //binding the data with the viewholder views
        String documentFileName = document.getDocumentFileName();
        String documentUrl = document.getDocumentUrl();
        String extension = getExtension(documentFileName);

        if (documentUrl.length() > 0 && Arrays.asList(Constant.IMAGE_EXTENSIONS).contains(extension)) {
            Glide.with(holder.context).load(documentUrl)
                    .crossFade()
                    .thumbnail(0.5f)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(holder.imgDocument);
            holder.imgDocument.setAdjustViewBounds(true);
//            holder.imgDocument.setVisibility(View.VISIBLE);
        } else {
            holder.imgDocument.setAdjustViewBounds(false);
//            holder.imgDocument.setImageResource(R.mipmap.ic_open_in_new_black_24dp);
//            holder.imgDocument.setVisibility(View.GONE);
        }

//        holder.imgDocument.setTag(document.getStudentId());

        holder.txtDocumentName.setText(document.getDocumentName());
        holder.txtDocumentName.setTag(documentUrl+"newFileParameter"+documentFileName+"newFileParameter"+extension);

        holder.imgDocumentDelete.setTag(document.getStudentId() + "newImageParameter"+ document.getDocumentId());

        holder.cardView.setTag(document.getDocumentType());

    }


    @Override
    public int getItemCount() {
        return documentList.size();
    }


    class DocumentViewHolder extends RecyclerView.ViewHolder {

        CardView cardView;
        ImageView imgDocument, imgDocumentDelete;
        TextView txtDocumentName;
        Context context;
        MainActivity mainActivity;
        ProfileChild profileChild;

        public DocumentViewHolder(final View itemView) {
            super(itemView);

            cardView = (CardView) itemView.findViewById(R.id.documentListCardView);
            imgDocument = (ImageView) itemView.findViewById(R.id.imgDocument);
            imgDocumentDelete = (ImageView) itemView.findViewById(R.id.imgDocumentDelete);
            txtDocumentName = (TextView) itemView.findViewById(R.id.txtDocumentName);
            context = itemView.getContext();
            mainActivity = (MainActivity) context;


            imgDocument.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    String[] fileParameters = txtDocumentName.getTag().toString().split("newFileParameter");
                    String fileUrl = fileParameters[0];
                    String fileName = fileParameters[1];
                    String fileExtension= fileParameters[2];

//                    Toast.makeText(v.getContext(), fileUrl+" "+fileName+" "+fileExtension, Toast.LENGTH_SHORT).show();

                    if (ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                        DownloadAndOpenFile downloadAndOpenFile = new DownloadAndOpenFile(context, mainActivity);
                        downloadAndOpenFile.execute(fileUrl, fileName, fileExtension);

                    }
                    else {
                        Utility.showSnackBar("Storage permission required to access files on device", mainActivity);
                        requestStoragePermission(context, mainActivity);
                    }
                }
            });

            imgDocumentDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(itemView.getContext());
//                builder.setTitle(R.string.app_name);
                    builder.setMessage("Are you sure you want to delete?");
//                builder.setIcon(R.mipmap.ic_launcher);
                    builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.dismiss();
                        }
                    });
                    builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.dismiss();

                            SharedPreferences preferences = itemView.getContext().getSharedPreferences(Constant.USER_DATA, itemView.getContext().MODE_PRIVATE);
                            Integer documentType = (Integer) cardView.getTag();
                            JSONObject data = new JSONObject();
                            try {
                                String[] imageParameters = imgDocumentDelete.getTag().toString().split("newImageParameter");
                                String studentId = imageParameters[0];
                                String documentId = imageParameters[1];

                                if (documentType == Constant.DOCUMENT_TYPE_STUDENT_PHOTO) {
                                    data.put("studentId", "1");
                                    data.put("schoolId", "58");
                                    data.put("photoId", documentId);

//                                    photoHandler.execute(data.toString());
                                } else if (documentType == Constant.DOCUMENT_TYPE_STUDENT_DOCUMENT) {
                                    data.put("studentId", "1");
                                    data.put("schoolId", "58");
                                    data.put("documentId", documentId);

//                                    photoHandler.execute(data.toString());
                                }
                            } catch (org.json.JSONException e) {
                                Utility.showSnackBar(context.getResources().getString(R.string.exception_msg), mainActivity);
                            }

                        }
                    });
                    AlertDialog alert = builder.create();
                    alert.show();
                }
            });

        }
    }

    public void clear() {
        documentList.clear();
        notifyDataSetChanged();
    }

    public String getExtension(String fileName) {
        int dotposition = fileName.lastIndexOf(".");
//        String filenameWithoutExt = fileName.substring(0,dotposition);
        String ext = fileName.substring(dotposition + 1, fileName.length());
        return ext;
    }

    //Requesting permission
    private void requestStoragePermission(Context context, Activity activity) {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)
            return;

        if (ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            //If the user has denied the permission previously your code will come to this block
            //Here you can explain why you need this permission
            //Explain here why you need this permission
        }
        //And finally ask for the permission
        ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, Constant.STORAGE_PERMISSION_CODE);
    }



}
