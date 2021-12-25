package com.example.testMgmtAndroid.adapter;

import static android.content.ContentValues.TAG;

import static com.example.testMgmtAndroid.R.id.list_title;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.testMgmtAndroid.R;
import com.example.testMgmtAndroid.activity.TestMainPageActivity;
import com.example.testMgmtAndroid.fragment.ExamInstructionFragment;
import com.example.testMgmtAndroid.fragment.ExamListFragment;
import com.example.testMgmtAndroid.fragment.ExpiredExams;
import com.example.testMgmtAndroid.fragment.QuestionsFragment;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class ListAdapter extends BaseAdapter {
    List<JSONObject> examList;
    Context context;
    private String listType ="UpcomingExams";
    ExamListFragment examListFragment;
    ExpiredExams expiredExams;
    Button takeExamBtn;
    public ListAdapter(@NonNull Context context, List<JSONObject> examList) {
        this.examList = examList;
        this.context = context;
    }

    @Override
    public int getCount() {
        if (examList == null)
            return 0;
        return examList.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    public void setExamListFragment(ExamListFragment fragment) {
        this.examListFragment = fragment;
    }  public void setExpiredExam(ExpiredExams fragment) {
        this.expiredExams = fragment;
    }


    public void ButtonHide(String listType){
        this.listType=listType;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_main, parent, false);

        TextView listView1, listView2, listView3, listView4, listView5, listview6;
        Button takeExamBtn;
        TextView textViewDays, textViewHour, textViewMin, textViewSec;
        SimpleDateFormat rdf, mdf, ddf, cdf, pdf, ymdhi,pdf1;


        try {
            rdf = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
            mdf = new SimpleDateFormat("dd-MM-yyyy", Locale.US);
            ddf = new SimpleDateFormat("dd", Locale.US);
            cdf = new SimpleDateFormat("MMMM - yyyy", Locale.US);
            pdf = new SimpleDateFormat("dd MMM yyyy HH:mm", Locale.US);
            pdf1 = new SimpleDateFormat("dd MMM yyyy", Locale.US);
            ymdhi = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.US);
            LinearLayout allDateLinearLayout=view.findViewById(R.id.linear_layout_alldatecard);
            View myview=view.findViewById(R.id.line);

            if (examList.size() > 0) {
                listView1 = view.findViewById(R.id.tv_exam_id);
                listView2 = view.findViewById(R.id.tv_duration_hr);
                listView3 = view.findViewById(R.id.tv_question_no);
                listView4 = view.findViewById(R.id.tv_start_date_no);
                listView5 = view.findViewById(R.id.tv_time_inMIn);
                listview6 = view.findViewById(R.id.list_title11);
                takeExamBtn = view.findViewById(R.id.Take_exam_btn);
                textViewDays = view.findViewById(R.id.tv_main_days);
                textViewHour = view.findViewById(R.id.tv_main_hour);
                textViewMin = view.findViewById(R.id.tv_main_mins);
                textViewSec = view.findViewById(R.id.tv_main_sec);

                if(listType.equals("UpcomingExams")){
                    takeExamBtn.setVisibility(View.VISIBLE);
                    allDateLinearLayout.setVisibility(View.VISIBLE);
                }
                else {
                    takeExamBtn.setVisibility(View.INVISIBLE);
                    allDateLinearLayout.setVisibility(View.GONE);
                    myview.setVisibility(View.GONE);
                }
                Log.e("atf", "nikk" + listType);


             JSONObject data = examList.get(position);
              String aa=  data.optString("id");

                Date currentDate = new Date();
                String appointmentStartTime = data.optString("start_date") + " " + data.optString("start_time");
                Date parseAppointmentStartTime = ymdhi.parse(appointmentStartTime);
                listView1.setText(data.optString("exam_code"));
                listView2.setText(data.optString("duration"));
                listView3.setText(data.optString("question_count"));
                listView4.setText(pdf1.format(parseAppointmentStartTime));
                listView5.setText(data.optString("start_time"));
                listview6.setText(data.optString("tittle"));
                textViewDays.setText(data.optString("days"));
                textViewHour.setText(data.optString("hour"));
                textViewMin.setText(data.optString("min"));
                textViewSec.setText(data.optString("sec"));
                long diff = parseAppointmentStartTime.getTime() - currentDate.getTime();
                CountDownTimer cdt = new CountDownTimer(diff, 1000) {
                    public void onTick(long millisUntilFinished) {
                        long days = (millisUntilFinished / (86400 * 1000));
                        millisUntilFinished -= days * (86400 * 1000);
                        long hour = (millisUntilFinished / (60 * 60 * 1000));
                        millisUntilFinished -= hour * (60 * 60 * 1000);
                        long min = (millisUntilFinished / (60 * 1000));
                        long sec = (millisUntilFinished / (1000) % 60);

                        textViewSec.setText("" + sec);
                        textViewHour.setText("" + hour);
                        textViewMin.setText("" + min);
                        textViewDays.setText("" + days);
                    }


                    public void onFinish() {
                        // TODO Auto-generated method stub
                    }

                }.start();


                takeExamBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String aa=  data.optString("id");
                        examListFragment.goToExamInstructionFragment(aa);
                    }
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return view;

    }


}
