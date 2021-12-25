package com.example.testMgmtAndroid.fragment;

import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.TextView;

import com.example.testMgmtAndroid.R;
import com.example.testMgmtAndroid.adapter.ListAdapter1;
import com.example.testMgmtAndroid.helper.DbHelper;
import com.google.gson.JsonParser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class QuestionsFragment extends Fragment {
    String questionData, questionNo1, myAnswer,options;
    View view;
    Context context;
    JSONObject jsonObject = new JSONObject();
    JsonParser parse;
    DbHelper dbHelper;
    private String idd;
    TextView allquestion;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        context = getContext();
        try {
            questionData = getArguments().getString("questionData");
            questionNo1 = getArguments().getString("questionNo");
            options = getArguments().getString("options");
            JSONObject parseQuestionData = new JSONObject(questionData);
            String questiontype = parseQuestionData.optString("question_type");
            myAnswer = parseQuestionData.optString("answer");
            jsonObject.put("fill_in_blank", R.layout.fragment_fillinthe_blanks);
            jsonObject.put("multiple_radiobutton", R.layout.fragment_radiobutton);
            jsonObject.put("multiple_correct", R.layout.fragment_radiobutton);
            jsonObject.put("essay_evaluated_by_admin", R.layout.fragment_easy);
            jsonObject.put("true_or_false", R.layout.trueorfalse);
            jsonObject.put("yes_or_no", R.layout.yesorno);
            jsonObject.put("Matching", R.layout.fragment_match_the_pair);
            jsonObject.put("drag_and_match", R.layout.dragandmatch);
            jsonObject.put("multiple_dropdown", R.layout.fragment_multiple_correct);
            view = inflater.inflate((Integer) jsonObject.get(questiontype), container, false);
            allquestion=view.findViewById(R.id.tv_allquestion);
            allquestion.setMovementMethod(new ScrollingMovementMethod());

            if (questiontype.equals("fill_in_blank")) {
                fillInBlank(view, parseQuestionData);

            } else if (questiontype.equals("multiple_radiobutton") || questiontype.equals("multiple_correct")) {
                multipleRadiobutton(view, parseQuestionData);
            } else if (questiontype.equals("essay_evaluated_by_admin")) {
                essyQuestion(view, parseQuestionData);

            } else if (questiontype.equals("true_or_false")) {
                trueOrFalse(view, parseQuestionData);

            } else if (questiontype.equals("yes_or_no")) {
                yesOrNo(view, parseQuestionData);
            } else if (questiontype.equals("Matching")) {
                Matching(view, parseQuestionData);
            } else if (questiontype.equals("drag_and_match")) {
                dragAndMatch(view, parseQuestionData);
            } else if (questiontype.equals("multiple_dropdown")) {
                multipleDropdown(view, parseQuestionData);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return view;
    }

    private void multipleRadiobutton(View v, JSONObject data) throws JSONException {
        TextView textViewQustionRadio = v.findViewById(R.id.tv_allquestion);
        allquestion.setText(data.optString("question"));
        TextView textquestionradio = v.findViewById(R.id.tv_question_no_allquestion);
        textquestionradio.setText(questionNo1);

        JSONArray json = new JSONArray(data.optString("options"));




        ArrayList<Object> listdata = new ArrayList<Object>();
        if (json != null) {
            for (int i = 0; i < json.length(); i++) {
                listdata.add(json.get(i)); }
        }

        Log.e("data","mydata"+listdata);
        ListView listView = v.findViewById(R.id.radiobtnoptionlistview);

        ListAdapter1 listAdapter1 = new ListAdapter1(getContext(), listdata, data, "Radiobutton");
        listView.setAdapter(listAdapter1);
        listView.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            listAdapter1.setSelectedItemPosition(position+1);
            listAdapter1.notifyDataSetChanged();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }

    public void fillInBlank(View v, JSONObject data) throws JSONException {
        TextView textViewQuestionFillBlank = v.findViewById(R.id.tv_allquestion);
        TextView textquestionNo = v.findViewById(R.id.tv_question_no_allquestion);

        textViewQuestionFillBlank.setText(data.optString("question"));
        textViewQuestionFillBlank.setMovementMethod(new ScrollingMovementMethod());
        JSONArray json = new JSONArray(myAnswer);
       JSONObject object= (JSONObject) json.get(0);
        EditText editText=v.findViewById(R.id.edittext_fillinblank);
            editText.setText(object.optString("answer"));
        textquestionNo.setText(questionNo1);

    }

//    public void multipleCorrect(View v, JSONObject data) {
//        TextView textViewMultipleCorrect = v.findViewById(R.id.tv_multiple_question);
//        textViewMultipleCorrect.setText(data.optString("question"));
//
//    }

    public void essyQuestion(View v, JSONObject data) throws JSONException {
        TextView textViewEssy = v.findViewById(R.id.tv_allquestion);
        textViewEssy.setText(data.optString("question"));
        TextView textViewIndexEssy = v.findViewById(R.id.tv_question_no_allquestion);
        textViewIndexEssy.setText(questionNo1);
        JSONArray json = new JSONArray(myAnswer);
        JSONObject object= (JSONObject) json.get(0);
        EditText editText=v.findViewById(R.id.edittext_essy);
        editText.setText(object.optString("answer"));


    }

    public void trueOrFalse(View v, JSONObject data) throws JSONException {
        TextView textViewTrueOrFalse = v.findViewById(R.id.tv_allquestion);
        textViewTrueOrFalse.setText(data.optString("question"));
        TextView textViewIndexTrueOrFalse = v.findViewById(R.id.tv_question_no_allquestion);
        textViewIndexTrueOrFalse.setText(questionNo1);
        JSONArray json = new JSONArray(data.optString("options"));
        ArrayList<Object> listdata = new ArrayList<Object>();
        if (json != null) {
            for (int i = 0; i < json.length(); i++) {
                listdata.add(json.get(i)); }
        }
        ListView listView1 = v.findViewById(R.id.listviewtrueorfalse);
        ListAdapter1 listAdapter1 = new ListAdapter1(getContext(), listdata,data, "trueOrFalse");
        listView1.setAdapter(listAdapter1);
        listAdapter1.notifyDataSetChanged();


    }

    public void yesOrNo(View v, JSONObject data) throws JSONException {
        TextView textViewYesOrNo = v.findViewById(R.id.tv_allquestion);
        textViewYesOrNo.setText(data.optString("question"));
        TextView textViewIndexYesOrNo = v.findViewById(R.id.tv_question_no_allquestion);
        textViewIndexYesOrNo.setText(questionNo1);


        JSONArray json = new JSONArray(data.optString("options"));
        ArrayList<Object> listdata = new ArrayList<Object>();
        if (json != null) {
            for (int i = 0; i < json.length(); i++) {
                listdata.add(json.get(i)); }
        }
        ListView listView1 = v.findViewById(R.id.listviewyesorno);
        ListAdapter1 listAdapter1 = new ListAdapter1(getContext(), listdata,data, "yesOrNo");
        listView1.setAdapter(listAdapter1);
        listAdapter1.notifyDataSetChanged();

    }

    public void Matching(View v, JSONObject data) throws JSONException {
        TextView textViewMatching = v.findViewById(R.id.tv_allquestion);
        textViewMatching.setText(data.optString("question"));
        TextView textViewIndexMatching = v.findViewById(R.id.tv_question_no_allquestion);
        textViewIndexMatching.setText(questionNo1);

        JSONArray json = new JSONArray(data.optString("options"));
        ArrayList<Object> listdata = new ArrayList<Object>();
        if (json != null) {
            for (int i = 0; i < json.length(); i++) {
                listdata.add(json.get(i)); }
        }
        ListView listView1 = v.findViewById(R.id.listviewMatchthePair);
        ListAdapter1 listAdapter1 = new ListAdapter1(getContext(), listdata,data, "MatchThePair");
        listView1.setAdapter(listAdapter1);
        listAdapter1.notifyDataSetChanged();
    }

    public void dragAndMatch(View v, JSONObject data) {
        TextView textViewDragAndMatch = v.findViewById(R.id.tv_allquestion);
        textViewDragAndMatch.setText(data.optString("question"));
        TextView textViewIndexDragAndMatch = v.findViewById(R.id.tv_question_no_allquestion);
        textViewIndexDragAndMatch.setText(questionNo1);
    }

    public void multipleDropdown(View v, JSONObject data) throws JSONException {
        TextView textViewMultipleDropDown = v.findViewById(R.id.tv_allquestion);
        textViewMultipleDropDown.setText(data.optString("question"));
        TextView textViewMultPleCorrectIndex = v.findViewById(R.id.tv_question_no_allquestion);
        textViewMultPleCorrectIndex.setText(questionNo1);

        JSONArray json1 = new JSONArray(data.optString("options"));
        ArrayList<Object> listdata1 = new ArrayList<Object>();

        if (json1 != null) {
            for (int i = 0; i < json1.length(); i++) {
                listdata1.add(json1.get(i));

            }
        }
        Log.e("heyy","jfj"+data);
        ListView listView1 = v.findViewById(R.id.checkboxlistview);
        ListAdapter1 listAdapter1 = new ListAdapter1(getContext(), listdata1, data, "checkBox");
        listView1.setAdapter(listAdapter1);
        listAdapter1.notifyDataSetChanged();

    }



}