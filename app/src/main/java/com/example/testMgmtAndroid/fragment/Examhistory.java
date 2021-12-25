package com.example.testMgmtAndroid.fragment;

import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;

import com.example.testMgmtAndroid.R;
import com.example.testMgmtAndroid.activity.MainActivity;
import com.example.testMgmtAndroid.adapter.ListAdapter;
import com.example.testMgmtAndroid.helper.DbHelper;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class Examhistory extends Fragment {
    public ListView listView;
    List<JSONObject> examList1;
    DbHelper dbHelper;
    Context mContext;
    Button button;
    SimpleDateFormat rdf, mdf, ddf, cdf, pdf, ymdhi, pdf1;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_examhistory, container, false);
        listView = view.findViewById(R.id.ExpiredDateCardListview);
        button=view.findViewById(R.id.Take_exam_btn);
        mContext = getContext();
        ((MainActivity) getActivity()).getSupportActionBar().setTitle("Expired Exams");
        try {

            rdf = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
            mdf = new SimpleDateFormat("dd-MM-yyyy", Locale.US);
            ddf = new SimpleDateFormat("dd", Locale.US);
            cdf = new SimpleDateFormat("MMMM - yyyy", Locale.US);
            pdf = new SimpleDateFormat("dd MMM yyyy HH:mm", Locale.US);
            pdf1 = new SimpleDateFormat("dd MMM yyyy", Locale.US);
            ymdhi = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.US);

            examList1 = new ArrayList<>();
            dbHelper = new DbHelper(mContext);
            JSONArray result1 = dbHelper.getExamListCardData(0);
            Date currentDate = new Date();


            if (result1 != null && result1.length() > 0) {

                for (int i = 0; i < result1.length(); i++) {
                    JSONObject jsonObject1 = result1.optJSONObject(i);
                    String startDate = jsonObject1.optString("start_date");
                    String startTime = jsonObject1.optString("start_time");
                    String appointmentStartTime = startDate + " " + startTime;

                    Date parseAppointmentStartTime = ymdhi.parse(appointmentStartTime);
                    if (parseAppointmentStartTime.before(currentDate)) {

                        examList1.add(jsonObject1);
                    }
                }
            }

            Log.e("Examhistory", "examList1 === " + examList1);
            ListAdapter listAdapter = new ListAdapter(mContext, examList1);
            listAdapter.ButtonHide("ExpiredExams");
            listView.setAdapter(listAdapter);
            listAdapter.notifyDataSetChanged();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return view;
    }
}