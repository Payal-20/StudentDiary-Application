package com.example.testMgmtAndroid.fragment;

import static android.content.ContentValues.TAG;

import static com.example.testMgmtAndroid.R.color.design_default_color_primary_dark;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.CountDownTimer;
import android.os.SystemClock;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toolbar;

import com.example.testMgmtAndroid.R;
import com.example.testMgmtAndroid.activity.MainActivity;
import com.example.testMgmtAndroid.adapter.ListAdapter;
import com.example.testMgmtAndroid.helper.DbHelper;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Array;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class ExamListFragment extends Fragment {

    private static final String LOG_TAG = ExamListFragment.class.getSimpleName();
    Context mContext;
    public ListView listView;
    List<JSONObject> examList, examList1;
    JSONObject CardData;
    Button take_exam_btn;
    BottomSheetDialog bottomSheetDialog;
    BottomNavigationView bottomNavigationView;
    Calendar calendarSelectedDate;
    SimpleDateFormat rdf, mdf, ddf, cdf, mdc, pdf, ymdhi;
    SearchView searchView1;
    public TextView NoMatchFound;
    ImageView RefreshIcon;
    MainActivity mainActivity;
    String sortBYName = "ExamName";

    String sortBYValue = "asc";
    JSONArray examCardData;
    DbHelper dbHelper;
    Toolbar toolbar;


    String searchSpinnerSelectedValue = "ExamName";
    DatePicker picker;
    private long mLastClickTime = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_exam_list_fragment, container, false);
        mContext = getContext();
        view.setTag(LOG_TAG);
        ((MainActivity) getActivity()).getSupportActionBar().setTitle("Upcoming Exams");



        take_exam_btn = view.findViewById(R.id.Take_exam_btn);
        NoMatchFound = view.findViewById(R.id.tv_noMatchesFound);
        RefreshIcon = view.findViewById(R.id.image_refresh);

        try {
            rdf = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
            mdf = new SimpleDateFormat("dd-MM-yyyy", Locale.US);
            mdc = new SimpleDateFormat("MM-dd-yyyy", Locale.US);
            ddf = new SimpleDateFormat("dd", Locale.US);
            cdf = new SimpleDateFormat("MMMM - yyyy", Locale.US);
            pdf = new SimpleDateFormat("dd MMM yyyy", Locale.US);
            ymdhi = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.US);


            listView = view.findViewById(R.id.mainList);
            bottomNavigationView = view.findViewById(R.id.bottom_Navigation);
            bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    Log.e("", "onNavigationItemReselected ==========");
                    switch (item.getItemId()) {
                        case R.id.sort: {
                            openSortBottomSheet();
                            break;
                        }
                        case R.id.search_bottom_iem: {
                            opendilog();
                            break;
                        }
                    }
                    return false;
                }
            });

            examList = new ArrayList<>();
            dbHelper = new DbHelper(mContext);
            Date currentDate = new Date();
            JSONArray result = dbHelper.getExamListCardData(0);
            if (result != null && result.length() > 0) {
                for (int i = 0; i < result.length(); i++) {
                    JSONObject jsonObject = result.optJSONObject(i);
                    String startDate = jsonObject.optString("start_date");
                    String startTime = jsonObject.optString("start_time");
                    String appointmentStartTime = startDate + " " + startTime;

                    Date parseAppointmentStartTime = ymdhi.parse(appointmentStartTime);
                    if (parseAppointmentStartTime.after(currentDate)) {
                        examList.add(jsonObject);
                    }
                }
            }

            System.out.println("before " + examList);


            displayExamList(examList);


        } catch (Exception e) {
            e.printStackTrace();
        }
        return view;
    }

    public void refreshExamList() {
        ListAdapter listAdapter = new ListAdapter(mContext, examList);
        listAdapter.setExamListFragment(ExamListFragment.this);
        listView.setAdapter(listAdapter);
        listAdapter.notifyDataSetChanged();
    }


    private void displayExamList(List<JSONObject> examList) {

        if (examList.size() > 0) {
            ListAdapter listAdapter = new ListAdapter(mContext, examList);
            listAdapter.setExamListFragment(ExamListFragment.this);
            listAdapter.ButtonHide("UpcomingExams");
            listView.setAdapter(listAdapter);
            listAdapter.notifyDataSetChanged();
            listView.setVisibility(View.VISIBLE);
            NoMatchFound.setVisibility(View.GONE);
        } else {
            listView.setVisibility(View.GONE);
            NoMatchFound.setVisibility(View.VISIBLE);
        }
    }


    private void sortExamList(String sortVal) {
        Collections.sort(examList, new Comparator<JSONObject>() {
            private static final String KEY_NAME = "tittle";
            private static final String KEY_NAME2 = "start_date";

            @Override
            public int compare(JSONObject a, JSONObject b) {
                String str1 = new String();
                String str2 = new String();
                String str3 = new String();
                try {
                    str1 = (String) a.get(KEY_NAME);
                    str2 = (String) b.get(KEY_NAME);
                    str3 = (String) b.get(KEY_NAME);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if (sortVal.equals("asc")) {
                    return str1.compareTo(str2);
                } else {
                    return str2.compareTo(str1);
                }
            }
        });

        ArrayList sortedJsonArray = new ArrayList<>();
        for (int i = 0; i < examList.size(); i++) {
            sortedJsonArray.add(examList.get(i));
        }
        System.out.println("Sorted JSON Array with Name: " + sortedJsonArray);

        displayExamList(sortedJsonArray);
    }

    private void sortExamList1(String sortVal) {
        Collections.sort(examList, new Comparator<JSONObject>() {
            private final String KEY_NAME = "start_date";

            @Override
            public int compare(JSONObject a, JSONObject b) {
                String str1 = new String();
                String str2 = new String();
                String str3 = new String();
                try {
                    str1 = (String) a.get(KEY_NAME);
                    str2 = (String) b.get(KEY_NAME);
                    String.format(str2, pdf);
                    str3 = (String) b.get(KEY_NAME);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if (sortVal.equals("asc")) {
                    return str1.compareTo(str2);
                } else {
                    return str2.compareTo(str1);
                }
            }
        });

        ArrayList sortedJsonArray = new ArrayList<>();
        for (int i = 0; i < examList.size(); i++) {
            sortedJsonArray.add(examList.get(i));
        }
        System.out.println("Sorted JSON Array with Name: " + sortedJsonArray);

        displayExamList(sortedJsonArray);
    }

    public void goToExamInstructionFragment(String examId) {
        ExamInstructionFragment examInstructionFragment = new ExamInstructionFragment();
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        Bundle bundle = new Bundle();
        bundle.putString("examId", examId);
        examInstructionFragment.setArguments(bundle);
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.content_frame, examInstructionFragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    private void opendilog() {
        View dialogView = LayoutInflater.from(mContext).inflate(R.layout.dilogbox_layout, null);

        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        Spinner spinner = dialogView.findViewById(R.id.spinner);
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(mContext, android.R.layout.simple_spinner_item, getResources().getStringArray(R.array.listtt));
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        spinner.setAdapter(arrayAdapter);
        builder.setView(dialogView);
        final AlertDialog alertDialog = builder.create();
        alertDialog.show();
        EditText editText12 = dialogView.findViewById(R.id.examdate_edittext2);
        EditText editText11 = dialogView.findViewById(R.id.examdate_edit_text);
        Button buttoncancelsort = dialogView.findViewById(R.id.btnTsCancel_dilogboxsort);
        buttoncancelsort.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });
        TextView textView = dialogView.findViewById(R.id.textlabel);
        TextView textView2 = dialogView.findViewById(R.id.textlabel2);
        picker = dialogView.findViewById(R.id.datePicker1);
        editText12.setInputType(InputType.TYPE_NULL);
        Button btnSubmit = dialogView.findViewById(R.id.btnTsSubmit_dilogboxsort);
        calendarSelectedDate = Calendar.getInstance();
        final DatePickerDialog.OnDateSetListener submissionDateListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int monthOfYear, int dayOfMonth) {
                calendarSelectedDate.set(Calendar.YEAR, year);
                calendarSelectedDate.set(Calendar.MONTH, monthOfYear);
                calendarSelectedDate.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                editText12.setText(rdf.format(calendarSelectedDate.getTime()));
            }
        };
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String txtExamName = editText11.getText().toString().toLowerCase(Locale.ROOT);
                String txtExamDate = editText12.getText().toString().toLowerCase(Locale.ROOT);
                String examdate = String.format(txtExamDate, rdf);
                if (searchSpinnerSelectedValue.equals("ExamName")) {
                    if (txtExamName.isEmpty()) {
                        AlertDialog.Builder builder1 = new AlertDialog.Builder(mContext);
                        builder1.setMessage("Write something");
                        builder1.setCancelable(true);

                        builder1.setPositiveButton(
                                "Ok",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        dialog.cancel();
                                    }
                                });

                        AlertDialog alert11 = builder1.create();
                        alert11.show();

                    }

                } else {
                    if (txtExamDate.isEmpty()) {
                        AlertDialog.Builder builder1 = new AlertDialog.Builder(mContext);
                        builder1.setMessage("Please select the exam Date");
                        builder1.setCancelable(true);

                        builder1.setPositiveButton(
                                "Ok",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        dialog.cancel();
                                    }
                                });

                        AlertDialog alert11 = builder1.create();
                        alert11.show();


                    }
                }
                List<JSONObject> searchExamList = new ArrayList<>();

                for (int i = 0; i < examList.size(); i++) {
                    JSONObject object = examList.get(i);
                    if (searchSpinnerSelectedValue.equals("ExamName")) {
                        if (object.optString("tittle").toLowerCase(Locale.ROOT).contains(txtExamName)) {
                            searchExamList.add(object);
                            Log.e("hey", "dc" + object.optString("tittle"));

                        }
                    } else {
                        if (object.optString("start_date").contains(examdate)) {
                            searchExamList.add(object);
                            Log.e("hey", "dc" + object.optString("start_date"));
                        }
                    }

                }
                alertDialog.dismiss();
                displayExamList(searchExamList);


            }

        });
        editText12.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//editText12.selectAll();
                new DatePickerDialog(v.getContext(), submissionDateListener, calendarSelectedDate
                        .get(Calendar.YEAR), calendarSelectedDate.get(Calendar.MONTH),
                        calendarSelectedDate.get(Calendar.DAY_OF_MONTH)).show();
                if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                    return;
                }
                mLastClickTime = SystemClock.elapsedRealtime();
            }
        });

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                switch (arg2) {
                    case 0:
                        editText11.setVisibility(View.VISIBLE);
                        textView.setVisibility(View.VISIBLE);
                        editText12.setVisibility(View.GONE);
                        textView2.setVisibility(View.GONE);
                        searchSpinnerSelectedValue = "ExamName";
                        break;
                    case 1:
                        editText12.setVisibility(View.VISIBLE);
                        textView2.setVisibility(View.VISIBLE);
                        editText11.setVisibility(View.GONE);
                        textView.setVisibility(View.GONE);
                        searchSpinnerSelectedValue = "ExamDate";
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
                // TODO Auto-generated method stub
            }
        });
    }

    @SuppressLint("ResourceAsColor")
    private void openSortBottomSheet() {
        bottomSheetDialog = new BottomSheetDialog(mContext, R.style.Theme_Design_Light_BottomSheetDialog);
        View sheetView = LayoutInflater.from(mContext).inflate(R.layout.bootamsheet, null);
        bottomSheetDialog.setContentView(sheetView);
        FrameLayout bottomSheet = (FrameLayout) bottomSheetDialog.findViewById(com.google.android.material.R.id.design_bottom_sheet);
        assert bottomSheet != null;
        bottomSheet.setBackground(null);
        TextView textViewAscending = sheetView.findViewById(R.id.btn_sortbyexamName_ASC);
        TextView buttonAscExamDate = sheetView.findViewById(R.id.btn_sortbyexamDate_ASC);
        TextView buttonDscExamDate = sheetView.findViewById(R.id.btn_sortbyexamDate_DSC);
        LinearLayout linearLayout = sheetView.findViewById(R.id.linear_layout_by_examname);
        LinearLayout linearLayout2 = sheetView.findViewById(R.id.linear_layout_by_examdate);
        TextView textViewDscending = sheetView.findViewById(R.id.btn_sortbyexamName_DSC);
        LinearLayout textViewClose = sheetView.findViewById(R.id.tv_close_bottom_nav_pop_up);

        if (sortBYName.equals("ExamName")) {
            linearLayout2.setBackgroundResource(R.color.white);
            linearLayout.setBackgroundResource(R.drawable.rounded_corner);
            buttonDscExamDate.setBackgroundResource(R.color.colorGrey);
            buttonAscExamDate.setBackgroundResource(R.color.colorGrey);
            if (sortBYValue.equals("asc")) {
                textViewAscending.setBackgroundResource(R.color.colorPrimaryDark);
                textViewDscending.setBackgroundResource(R.color.colorGrey);
            } else {
                textViewAscending.setBackgroundResource(R.color.colorGrey);
                textViewDscending.setBackgroundResource(R.color.colorPrimaryDark);
            }
        } else {
            linearLayout.setBackgroundResource(R.color.white);
            linearLayout2.setBackgroundResource(R.drawable.rounded_corner);
            textViewAscending.setBackgroundResource(R.color.colorGrey);
            textViewDscending.setBackgroundResource(R.color.colorGrey);

            if (sortBYValue.equals("asc")) {
                buttonDscExamDate.setBackgroundResource(R.color.colorPrimaryDark);
                buttonAscExamDate.setBackgroundResource(R.color.colorGrey);
            } else {
                buttonDscExamDate.setBackgroundResource(R.color.colorGrey);
                buttonAscExamDate.setBackgroundResource(R.color.colorPrimaryDark);
            }
        }

        linearLayout.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("ResourceAsColor")
            @Override
            public void onClick(View v) {
                sortBYName = "ExamName";
                linearLayout2.setBackgroundResource(R.color.white);
                linearLayout.setBackgroundResource(R.drawable.rounded_corner);
                if (sortBYValue.equals("asc")) {
                    textViewAscending.setBackgroundResource(R.color.colorPrimaryDark);
                    textViewDscending.setBackgroundResource(R.color.colorGrey);
                    sortBYValue = "desc";
                } else {
                    textViewAscending.setBackgroundResource(R.color.colorGrey);
                    textViewDscending.setBackgroundResource(R.color.colorPrimaryDark);
                    sortBYValue = "asc";
                }

                bottomSheetDialog.dismiss();
                sortExamList(sortBYValue);
            }
        });

        linearLayout2.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("ResourceAsColor")
            @Override
            public void onClick(View v) {
                sortBYName = "ExamDate";

                linearLayout.setBackgroundResource(R.color.white);
                linearLayout2.setBackgroundResource(R.drawable.rounded_corner);
                textViewAscending.setBackgroundResource(R.color.colorGrey);
                textViewDscending.setBackgroundResource(R.color.colorGrey);

                if (sortBYValue.equals("asc")) {
                    buttonDscExamDate.setBackgroundResource(R.color.colorPrimaryDark);
                    buttonAscExamDate.setBackgroundResource(R.color.colorGrey);
                    sortBYValue = "desc";
                } else {
                    buttonDscExamDate.setBackgroundResource(R.color.colorGrey);
                    buttonAscExamDate.setBackgroundResource(R.color.colorPrimaryDark);
                    sortBYValue = "asc";
                }

                bottomSheetDialog.dismiss();
                sortExamList1(sortBYValue);
            }
        });

        textViewClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomSheetDialog.dismiss();
            }
        });

        bottomSheetDialog.show();
    }

    private void setSortButtonsInActionShit(String sortName, String sortValue) {
    }
}

