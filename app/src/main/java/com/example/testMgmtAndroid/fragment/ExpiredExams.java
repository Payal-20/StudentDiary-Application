package com.example.testMgmtAndroid.fragment;

import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.example.testMgmtAndroid.R;
import com.example.testMgmtAndroid.activity.MainActivity;
import com.example.testMgmtAndroid.adapter.ListAdapter;
import com.example.testMgmtAndroid.helper.DbHelper;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ExpiredExams extends Fragment {

    public ListView listView;
    List<JSONObject> examList1;
    DbHelper dbHelper;
    Context mContext;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_expired_exams, container, false);
        listView = view.findViewById(R.id.examHistoryListview);
        mContext = getContext();
        ((MainActivity) getActivity()).getSupportActionBar().setTitle("Exam History");


        examList1 = new ArrayList<>();
        dbHelper =new DbHelper(mContext);
        JSONArray result1 = dbHelper.getExamListCardData(1);
        if (result1 != null && result1.length() > 0) {
            for (int i=0; i<result1.length();i++){
                JSONObject jsonObject1 = result1.optJSONObject(i);
                examList1.add(jsonObject1);
                Log.e("atf", "nikk" + examList1);
            }
        }
        ListAdapter listAdapter = new ListAdapter(mContext, examList1);
        listAdapter.setExpiredExam(ExpiredExams.this);
        listAdapter.ButtonHide("ExpiredExams");
        listView.setAdapter(listAdapter);
        listAdapter.notifyDataSetChanged();
        return view;
    }
}