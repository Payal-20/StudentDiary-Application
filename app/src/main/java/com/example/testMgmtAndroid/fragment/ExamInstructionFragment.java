package com.example.testMgmtAndroid.fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;

import com.example.testMgmtAndroid.R;
import com.example.testMgmtAndroid.activity.TestMainPageActivity;
import com.example.testMgmtAndroid.helper.DbHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;


public class ExamInstructionFragment extends Fragment {
    DbHelper dbHelper;
    String questionData;
    String examId;
    JSONObject jsonObject = new JSONObject();
    JSONArray examQuestionData;
CheckBox checkBox;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        examId = getArguments().getString("examId");
        Log.e("hr","jj"+examId);
        View view = inflater.inflate(R.layout.fragment_exam_instrustion, container, false);
        dbHelper = new DbHelper(getContext());
        Button button = view.findViewById(R.id.start_exam_btn);

       checkBox=view.findViewById(R.id.checkbox_cheese);
       checkBox.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               if(checkBox.isChecked()){
                   button.setVisibility(View.VISIBLE);
               }
               else  if(!checkBox.isChecked()) {
                   button.setVisibility(View.INVISIBLE);
               }
           }
       });


        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String json;
                try {
                    dbHelper.deleteById(examId);
                    InputStream is = getContext().getAssets().open("a.json");
                    int size = is.available();
                    byte[] buffer = new byte[size];
                    is.read(buffer);
                    json = new String(buffer, "UTF-8");
                    examQuestionData = new JSONArray(json);
                    is.close();
                    for (int i = 0; i < examQuestionData.length(); i++) {
                        JSONObject object = (JSONObject) examQuestionData.get(i);
                        dbHelper.insertData(examId,
                                object.optString("question_type"),
                                object.optString("question"),
                                object.optString("options"),
                                object.optString("options_label"), i + 1);
                    }
                    Intent intent = new Intent(getActivity(), TestMainPageActivity.class);
                    intent.putExtra("examId", examId);
                    intent.putExtra("examId", examId);
                    startActivity(intent);

                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
        return view;
    }


}