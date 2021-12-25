package com.example.testMgmtAndroid.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RadioButton;
import android.widget.TextView;

import com.example.testMgmtAndroid.R;
import com.example.testMgmtAndroid.activity.TestMainPageActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ListAdapter3 extends BaseAdapter {
    private final Context mContext;
    private ArrayList<Object> arrayList;

    public ListAdapter3(Context context, ArrayList<Object> arrayList) {
        this.mContext = context;
        this.arrayList = arrayList;
        Log.e("pp", "ss" + arrayList);
    }

    @Override
    public int getCount() {
        return arrayList.size();
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    // 5
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View rowView;
        rowView = gridMenu(position);
        return rowView;
    }

    @SuppressLint("ResourceAsColor")
    private View gridMenu(int position) {

        LayoutInflater inflater = LayoutInflater.from(mContext);
        View rowView = inflater.inflate(R.layout.circletextview, null, true);
        TextView textView = rowView.findViewById(R.id.tvcircleshape);

        final JSONObject data = (JSONObject) arrayList.get(position);

        String answer = (data.optString("answer"));
        String answer1 = (data.optString("is_flag"));
        Log.e("hkhk", "rr" + data);

        try {
            if (answer1.equals("1")) {
                textView.setBackgroundResource(R.drawable.orangecircle);
            } else if (!answer.equals("")) {
                textView.setBackgroundResource(R.drawable.greencirclelayout);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }


        try {
            textView.setText(position + 1 + "");
//            Log.e("hkhk", "--------------------------------------------");
//            Log.e("hkhk", "rr" + answer);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return rowView;
    }
}
