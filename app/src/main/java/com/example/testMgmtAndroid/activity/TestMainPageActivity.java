package com.example.testMgmtAndroid.activity;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.testMgmtAndroid.R;
import com.example.testMgmtAndroid.adapter.ListAdapter1;
import com.example.testMgmtAndroid.adapter.ListAdapter3;
import com.example.testMgmtAndroid.fragment.ExamInstructionFragment;
import com.example.testMgmtAndroid.fragment.ExamListFragment;
import com.example.testMgmtAndroid.fragment.QuestionsFragment;
import com.example.testMgmtAndroid.helper.DbHelper;
import com.example.testMgmtAndroid.helper.OnApiTaskCompleted;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class TestMainPageActivity extends AppCompatActivity implements OnApiTaskCompleted {
    private static final String TAG = TestMainPageActivity.class.getSimpleName();

    private String is_flag, answer1, idd;
    private String apiUrl;
    private JSONObject responseObject;
    private boolean isNetworkAvailable = true;
    private Context mContext;
    int finalresult;
    ExamListFragment examListFragment;
    ExamInstructionFragment examInstructionFragment;
    int readQuestionCount = 1;
    Button btnNext, btnPrevious, btnSkip, btnfinsh;
    DbHelper dbHelper;
    String examId;
    int AnswerCount;

    JSONObject optionsData;
    ImageView imageViewMenu, RestBtn, SubmitTheExamAnyTime;
    ListAdapter3 listAdapter3;
    TextView Flag;
    TextView TextViewAns;
    ArrayList<Object> listdata;


    ArrayList<String> arrayList = new ArrayList<>();
    JSONArray examQuestionData;
    private static final String FORMAT = "%002d:%02d:%0202d";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_mainpage);
        mContext = this;
        examListFragment = new ExamListFragment();
        dbHelper = new DbHelper(mContext);
        examInstructionFragment = new ExamInstructionFragment();
        Button button = findViewById(R.id.start_exam_btn);
        examId = getIntent().getStringExtra("examId");
        Log.e("aa", "dd" + examId);
        TextView textViewQuestionRemain = findViewById(R.id.tv_remaning_question);
        TextView textViewtotalRemain = findViewById(R.id.tv_total_question);

        String str2 = String.valueOf(readQuestionCount);
        textViewQuestionRemain.setText(str2 + " /");

        Flag = findViewById(R.id.flag);
        LinearLayout linearLayout = findViewById(R.id.flaglinearlayout);
        linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String iddd = optionsData.optString("id");
                String iddd2 = optionsData.optString("answer");

                if (is_flag.equals("0")) {
                    Flag.setBackgroundResource(R.drawable.orangeflag);

                    dbHelper.setFlag(iddd, 1);
                    is_flag = "1";
                } else {
                    Flag.setBackgroundResource(R.drawable.ic_baseline_flag_24);

                    dbHelper.setFlag(iddd, 0);
                    is_flag = "0";
                }

            }
        });


        imageViewMenu = findViewById(R.id.btnmenuclick);
        RestBtn = findViewById(R.id.resetbtn);

        imageViewMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
             AnswerCount=0;
                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                ViewGroup viewGroup = findViewById(android.R.id.content);
                View dialogView = LayoutInflater.from(v.getContext()).inflate(R.layout.menudilogboxanswer, viewGroup, false);
                builder.setView(dialogView);
                TextView textViewAnswerd = dialogView.findViewById(R.id.tv_answed);

                AlertDialog alertDialog = builder.create();
                GridView gridView = dialogView.findViewById(R.id.gridview);
                TextView textView = dialogView.findViewById(R.id.tv_total_questionmenu);
                String strI = String.valueOf(finalresult);
                textView.setText(strI);
                alertDialog.getWindow().setGravity(Gravity.TOP | Gravity.RIGHT);
                ArrayList<Object> arrayList = new ArrayList<>();
                JSONArray result = dbHelper.getAllData(examId, "0");
                if (result != null && result.length() > 0) {
                    for (int i = 0; i < result.length(); i++) {
                        JSONObject jsonObject = result.optJSONObject(i);
                        String answerGiven=jsonObject.optString("answer");
                        if(!answerGiven.isEmpty()){
                            AnswerCount++;
                        }
                        arrayList.add(jsonObject);
                        Log.e("Tag", "jsonobjeeee" + AnswerCount);
                    }


                }
                textViewAnswerd.setText(AnswerCount+"");
                try {

                    listAdapter3 = new ListAdapter3(mContext, arrayList);
                    gridView.setAdapter(listAdapter3);
                    gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            Log.e("hh", "pos" + position);
                            callFunction(position);
                        }

                        private void callFunction(int position) {
                            readQuestionCount = position + 1;
                            alertDialog.dismiss();
                            String str3 = String.valueOf(position + 1);
                            textViewQuestionRemain.setText(str3 + " /");
                            if (position == 0) {
                                btnPrevious.setVisibility(View.GONE);
                            } else {
                                btnPrevious.setVisibility(View.VISIBLE);
                            }
                            if (position + 1 == finalresult) {
                                btnPrevious.setVisibility(View.VISIBLE);
                                btnfinsh.setVisibility(View.VISIBLE);
                                btnNext.setVisibility(View.GONE);
                                btnSkip.setVisibility(View.GONE);
                                Log.e("hhh", "pos" + finalresult);
                            } else {
                                btnfinsh.setVisibility(View.GONE);
                                btnNext.setVisibility(View.VISIBLE);
                                btnSkip.setVisibility(View.VISIBLE);
                            }
                            getAllData();

                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
                alertDialog.show();
                alertDialog.getWindow().setLayout(600, 1300);
                alertDialog.getWindow().setBackgroundDrawable(getDrawable(R.drawable.rectangleshape));
            }
        });
        try {
            btnNext = findViewById(R.id.btn_next);
            btnPrevious = findViewById(R.id.btn_previous);
            btnSkip = findViewById(R.id.btn_skip);
            btnfinsh = findViewById(R.id.btn_finish);
            TextView examDate = findViewById(R.id.exam_date);
            SubmitTheExamAnyTime = findViewById(R.id.submittheexam);
            SubmitTheExamAnyTime.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    openFinshcardView();
                }
            });
            btnfinsh.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    openFinshcardView();
                }


            });
            btnSkip.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    readQuestionCount++;
                    String str2 = String.valueOf(readQuestionCount);
                    textViewQuestionRemain.setText(str2 + " /");
                    btnPrevious.setVisibility(View.VISIBLE);
                    if (readQuestionCount == finalresult) {
                        btnNext.setVisibility(View.GONE);
                        btnSkip.setVisibility(View.GONE);
                        btnfinsh.setVisibility(View.VISIBLE);
                    }
                    getAllData();
                }
            });
            btnNext.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.e("tag", "hello" + optionsData);
                    String questionType = optionsData.optString("question_type");
                    idd = optionsData.optString("id");
                    String iddd = optionsData.optString("id");
                    if (questionType.equals("fill_in_blank")) {
                        QuestionsFragment questionsFragment = new QuestionsFragment();
                        EditText editText = findViewById(R.id.edittext_fillinblank);
                        String content = editText.getText().toString();
                        if (content.isEmpty()) {
                            AlertDialog.Builder builder1 = new AlertDialog.Builder(mContext);
                            builder1.setMessage("Answer can't be empty");
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
                            btnPrevious.setVisibility(View.GONE);
                            readQuestionCount--;


                        } else {
                            try {
                                JSONObject object = new JSONObject();
                                object.put("correct_options", "A");
                                object.put("answer", content);
                                listdata = new ArrayList<Object>();
                                listdata.add(object);
                                addAnswer(idd, listdata);

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                    }
                    if (questionType.equals("multiple_radiobutton")) {
                        QuestionsFragment questionsFragment = new QuestionsFragment();
                    }
                    if (questionType.equals("essay_evaluated_by_admin")) {
                        QuestionsFragment questionsFragment = new QuestionsFragment();
                        EditText editText = findViewById(R.id.edittext_essy);
                        String content = editText.getText().toString();
                        if (content.isEmpty()) {
                            AlertDialog.Builder builder1 = new AlertDialog.Builder(mContext);
                            builder1.setMessage("Answer can't be empty");
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
                            readQuestionCount--;
                        } else {
                            try {
                                JSONObject object = new JSONObject();
                                object.put("correct_options", "A");
                                object.put("answer", content);
                                ArrayList<Object> listdata = new ArrayList<Object>();
                                listdata.add(object);
//                                JSONArray array=new JSONArray();
//                                array.put(object);
                                addAnswer(idd, listdata);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                    if (questionType.equals("true_or_false")) {
                        QuestionsFragment questionsFragment = new QuestionsFragment();
                    }
                    if (questionType.equals("yes_or_no")) {


                    }
                    if (questionType.equals("Matching")) {


                    }
                    if (questionType.equals("drag_and_match")) {


                    }
                    if (questionType.equals("multiple_dropdown")) {

                    }
                    readQuestionCount++;
                    getAllData();
                    String str2 = String.valueOf(readQuestionCount);
                    textViewQuestionRemain.setText(str2 + " /");
                    btnPrevious.setVisibility(View.VISIBLE);
                    if (readQuestionCount == finalresult) {
                        btnNext.setVisibility(View.GONE);
                        btnSkip.setVisibility(View.GONE);
                        btnfinsh.setVisibility(View.VISIBLE);
                    } else if (readQuestionCount == 1) {
                        btnPrevious.setVisibility(View.GONE);
                    }
                }
            });
            RestBtn.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    String questionType1 = optionsData.optString("question_type");

                    if (questionType1.equals("fill_in_blank")) {
                        EditText editText = findViewById(R.id.edittext_fillinblank);
                        editText.setText("");
                        Log.e("myyyyanser", "nfjffo" + optionsData);
                        answer1 = optionsData.optString("answer");
                        dbHelper.UpdateAnswer2(idd, answer1);

//                        readQuestionCount=1;
                    }
                    if (questionType1.equals("multiple_radiobutton")) {
                        ListAdapter1 listAdapter1 = new ListAdapter1(mContext);
                        RadioButton radioButton1 = findViewById(R.id.radio);
                        radioButton1.setChecked(false);

                        answer1 = optionsData.optString("answer");
                        dbHelper.UpdateAnswer2(idd, answer1);


                    }
                    if (questionType1.equals("essay_evaluated_by_admin")) {
                        EditText editText = findViewById(R.id.edittext_essy);
                        editText.setText("");

                        answer1 = optionsData.optString("answer");
                        dbHelper.UpdateAnswer2(idd, answer1);

                    }
                    if (questionType1.equals("true_or_false")) {
                        RadioButton radioButton11 = findViewById(R.id.radiotrue1);
                        radioButton11.setChecked(false);
                        answer1 = optionsData.optString("answer");
                        dbHelper.UpdateAnswer2(idd, answer1);

                    } else {

                    }
                }
            });
            btnPrevious.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String str2 = String.valueOf(readQuestionCount);
                    textViewQuestionRemain.setText(str2 + " /");

                    if (readQuestionCount == 2) {
                        readQuestionCount--;
                        btnPrevious.setVisibility(View.GONE);

                    } else if (!(readQuestionCount == 1)) {
                        btnPrevious.setVisibility(View.VISIBLE);
                        readQuestionCount--;

                    }
                    if (readQuestionCount == finalresult - 1) {
                        btnNext.setVisibility(View.VISIBLE);
                        btnSkip.setVisibility(View.VISIBLE);
                        btnfinsh.setVisibility(View.GONE);
                    }
                    getAllData();

                }
            });
            if (readQuestionCount == 1) {
                btnPrevious.setVisibility(View.GONE);
            }

            dbHelper.questionCount(idd);
            {
                JSONArray result = dbHelper.questionCount(examId);
                finalresult = result.length();
                String strI = String.valueOf(finalresult);
                textViewtotalRemain.setText(strI);

            }

            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
            Date a = null, b = null;
            a = sdf.parse("27-09-2021");
            b = sdf.parse("28-09-2021");
            CountDownTimer cdt = new CountDownTimer(3600000, 1000) {
                public void onTick(long millisUntilFinished) {
                    // TODO Auto-generated method stub
                    int days = (int) (millisUntilFinished / (1000 * 60 * 60 * 24));
                    int hour = (int) (millisUntilFinished / (1000 * 60 * 60));
                    int min = (int) (millisUntilFinished / (1000 * 60));
                    int sec = (int) (millisUntilFinished / (1000) % 60);

                    SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
                    examDate.setText(String.format(" %02d:%02d:%02d", hour, min, sec));

                }

                public void onFinish() {

                    dbHelper.setFlag(idd,1);
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(intent);
                    // TODO Auto-generated method stub
                }
            }.start();

            getAllData();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void openFinshcardView() {
        View dialogView = LayoutInflater.from(mContext).inflate(R.layout.finishcardview, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setView(dialogView);
        final AlertDialog alertDialog = builder.create();
        TextView textView = dialogView.findViewById(R.id.cancel_finish_cardview);
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });
        Button button1 = dialogView.findViewById(R.id.confirm);
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
//                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
//                startActivity(intent);
                dbHelper.setFinish(examId);
                Log.e("ss", "dd" + examId);
            }
        });
        alertDialog.show();
    }

    public void addAnswer(String idd, ArrayList<Object> array) {
        dbHelper.UpdateAnswer(idd, array);
    }

    private void getAllData() {
        dbHelper.getAllData(examId, readQuestionCount + "");

        JSONArray result = dbHelper.getAllData(examId, readQuestionCount + "");
        if (result != null && result.length() > 0) {
            JSONObject jsonObject = result.optJSONObject(0);
            optionsData = jsonObject;
            is_flag = optionsData.optString("is_flag");
     String  myAnswer = optionsData.optString("answer");
            Log.e(TAG, "getAllData: " +myAnswer);
            if (is_flag.equals("0")) {
                Flag.setBackgroundResource(R.drawable.ic_baseline_flag_24);
            } else {
                Flag.setBackgroundResource(R.drawable.orangeflag);
            }
            loadFragment(jsonObject);

        }

    }


    private void loadFragment(Object questionData) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        Fragment fragment = new QuestionsFragment();
        Bundle bundle = new Bundle();
        bundle.putString("questionData", questionData.toString());
        bundle.putString("questionNo", String.valueOf(readQuestionCount) + ".");
        fragment.setArguments(bundle);
        transaction.replace(R.id.fragment_container_view_tag, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    @Override
    public void afterReceivingData(int callback, JSONObject response) {

        try {
            if (response.optString("success").equalsIgnoreCase("true")) {
                if (callback == 1) {

                }
            } else {
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {
        if (getFragmentManager().getBackStackEntryCount() == 0) {
            this.finish();
        } else {
            getFragmentManager().popBackStack();
        }
    }

}
