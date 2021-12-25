package com.example.testMgmtAndroid.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.testMgmtAndroid.R;
import com.example.testMgmtAndroid.activity.TestMainPageActivity;
import com.example.testMgmtAndroid.fragment.QuestionsFragment;
import com.example.testMgmtAndroid.helper.DbHelper;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class ListAdapter1 extends BaseAdapter {
    Context context;
    private ArrayList<Object> optionsData;
    private String listType = "Radiobutton";
    QuestionsFragment fragment;
    String idd;
    JSONObject data;
    private int selectedItemPosition = -1;
    DbHelper dbHelper;
    TestMainPageActivity testMainPageActivity;

    public ListAdapter1(Context context, ArrayList<Object> optionsData, JSONObject data, String checkBox) {
        this.context = context;
        this.optionsData = optionsData;
        this.listType = checkBox;
        this.data = data;
        idd = data.optString("id");
        dbHelper = new DbHelper(context);
    }

    public ListAdapter1(Context context) {
        this.context = context;
    }

    @Override
    public int getCount() {
        return optionsData.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }


    public void setSelectedItemPosition(int position) {
        selectedItemPosition = position;
    }

    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View rowView;
        switch (listType) {
            case "Radiobutton":
                rowView = loadOptionsRadioButton(position);
                break;
            case "checkBox":
                rowView = loadOptionsCheckBoxButton(position);
                break;
            case "trueOrFalse":
                rowView = loadOptionsTrueorFalseButton(position);
                break;
            case "yesOrNo":
                rowView = loadOptionsYesOrNo(position);
                break;

            case "MatchThePair":
                rowView = loadOptionsMatchthePair(position);
                break;

            default:
                throw new IllegalStateException("Unexpected value: " + listType);
        }
        return rowView;

    }

    private View loadOptionsMatchthePair(int position) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View rowView = inflater.inflate(R.layout.matchthepaircardview, null, true);
        Spinner spinner = rowView.findViewById(R.id.spinner_match_the_pair);
        TextView textView = rowView.findViewById(R.id.spinertextView);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(context, R.array.array_name, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                String selectedString;
                //1: From the Adapter
                selectedString = adapter.getItem(position).toString();

                //2: From the spinner
                selectedString = spinner.getSelectedItem().toString();

                //3: From the view
                TextView spinner_text = (TextView) view;
                selectedString = spinner_text.getText().toString();

                //All of them works just fine choose any one method
                textView.setText(selectedString);
                ((TextView)view).setText(null);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        TextView textViewYes = rowView.findViewById(R.id.tv_options_math_the_pair);
        TextView textViewLabelMatchThePair = rowView.findViewById(R.id.tv_label_Match_the_pair);
        if (optionsData.size() > 0) {
            Log.e("pp", "ss" + optionsData);
            final JSONObject data = (JSONObject) optionsData.get(position);
            textViewYes.setText(data.optString("options"));
            textViewLabelMatchThePair.setText(data.optString("option_name") + ".");
        }
        return rowView;
    }

    private View loadOptionsYesOrNo(int position) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View rowView = inflater.inflate(R.layout.yesornocardview, null, true);
        RadioButton radioButton111 = rowView.findViewById(R.id.radioYes1);
        TextView textViewYes = rowView.findViewById(R.id.tvyes);
        TextView textViewLabelYes = rowView.findViewById(R.id.tv_label_Yes_or_No);
        if (optionsData.size() > 0) {
            Log.e("pp", "ss" + optionsData);
            final JSONObject data = (JSONObject) optionsData.get(position);
            radioButton111.setChecked(position == selectedItemPosition);
            radioButton111.setTag(position);
            radioButton111.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    selectedItemPosition = (Integer) view.getTag();
                    testMainPageActivity = new TestMainPageActivity();
                    JSONObject object = new JSONObject();
                    radioButton111.setChecked(position == selectedItemPosition);
                    try {
                        object.put(String.valueOf(optionsData), "A");
                        object.put("answer", selectedItemPosition);
                        ArrayList<Object> listdata1 = new ArrayList<Object>();
                        listdata1.add(optionsData.get(selectedItemPosition));
                        addAnswer(idd, listdata1);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    notifyDataSetChanged();
                }
            });
            textViewYes.setText(data.optString("options"));
            textViewLabelYes.setText(data.optString("option_name") + ".");
        }
        return rowView;


    }

    private View loadOptionsTrueorFalseButton(int position) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View rowView = inflater.inflate(R.layout.trueorfalsecardview, null, true);

        RadioButton radioButton11 = rowView.findViewById(R.id.radiotrue1);
//        RadioButton radioButton22 = rowView.findViewById(R.id.radiofalse1);
        TextView textViewtrue = rowView.findViewById(R.id.tvtrue);
        TextView textViewtrueLabel = rowView.findViewById(R.id.tv_label_true_or_false);
        if (optionsData.size() > 0) {
            Log.e("pp", "ss" + optionsData);
            final JSONObject data = (JSONObject) optionsData.get(position);
            radioButton11.setChecked(position == selectedItemPosition);
            radioButton11.setTag(position);
            radioButton11.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    selectedItemPosition = (Integer) view.getTag();
                    testMainPageActivity = new TestMainPageActivity();
                    JSONObject object = new JSONObject();
                    radioButton11.setChecked(position == selectedItemPosition);
                    try {
                        object.put(String.valueOf(optionsData), "A");
                        object.put("answer", selectedItemPosition);
                        ArrayList<Object> listdata1 = new ArrayList<Object>();
                        listdata1.add(optionsData.get(selectedItemPosition));
                        addAnswer(idd, listdata1);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    notifyDataSetChanged();
                }
            });
            textViewtrue.setText(data.optString("options"));
            ;
            textViewtrueLabel.setText(data.optString("option_name") + ".");
        }
        return rowView;

    }

    private View loadOptionsCheckBoxButton(int position) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View rowView = inflater.inflate(R.layout.multiplecheckboxcardview, null, true);
        TextView checkboxOptions = rowView.findViewById(R.id.tv_checkboxbtn);
        TextView checkboxLabel = rowView.findViewById(R.id.tv_label_Checkbox);

        CheckBox checkBox = rowView.findViewById(R.id.checkbox);
        if (optionsData.size() > 0) {
            final JSONObject data = (JSONObject) optionsData.get(position);
            checkBox.setChecked(position == selectedItemPosition);
            checkBox.setTag(position);
            checkBox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    selectedItemPosition = (Integer) view.getTag();
                    testMainPageActivity = new TestMainPageActivity();
                    JSONObject object = new JSONObject();
                    checkBox.setChecked(position == selectedItemPosition);
                    try {
                        object.put(String.valueOf(optionsData), "A");
                        object.put("answer", selectedItemPosition);
                        ArrayList<Object> listdata1 = new ArrayList<Object>();
                        listdata1.add(optionsData.get(selectedItemPosition));

//                        addAnswer(idd, listdata1);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    notifyDataSetChanged();
                }
            });
            checkboxOptions.setText(data.optString("options"));
            checkboxLabel.setText(data.optString("option_name") + ".");
        }
        return rowView;
    }

    public View loadOptionsRadioButton(int position) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View rowView = inflater.inflate(R.layout.radiobuttoncardbiew, null, true);
        TextView radioButton = rowView.findViewById(R.id.tv_radiobtn);
        TextView radioButtonLabel = rowView.findViewById(R.id.tv_label_Radiobutton);
        RadioButton radioButton1 = rowView.findViewById(R.id.radio);


        if (optionsData.size() > 0) {
            Log.e("pp", "ss" + optionsData);
            final JSONObject data = (JSONObject) optionsData.get(position);
            radioButton1.setChecked(position == selectedItemPosition);
            radioButton1.setTag(position);
            radioButton1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    radioButton1.setChecked(position == selectedItemPosition);
                    selectedItemPosition = (Integer) view.getTag();
                    testMainPageActivity = new TestMainPageActivity();
                    JSONObject object = new JSONObject();
                    radioButton1.setChecked(position == selectedItemPosition);
                    try {
                        object.put(String.valueOf(optionsData), "A");
                        object.put("answer", selectedItemPosition);
                        ArrayList<Object> listdata1 = new ArrayList<Object>();
                        listdata1.add(optionsData.get(selectedItemPosition));
                        addAnswer(idd, listdata1);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    notifyDataSetChanged();
                }
            });
            radioButton.setText(data.optString("options"));
            radioButtonLabel.setText(data.optString("option_name") + ".");
        }
        return rowView;
    }

    public void addAnswer(String idd, ArrayList<Object> array) {

        dbHelper.UpdateAnswer(idd, array);
    }


}
