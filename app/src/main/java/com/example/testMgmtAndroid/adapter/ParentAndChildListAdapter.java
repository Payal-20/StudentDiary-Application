package com.example.testMgmtAndroid.adapter;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;
import com.example.testMgmtAndroid.R;

import org.json.JSONObject;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class ParentAndChildListAdapter extends BaseAdapter {
    Context context;
    private ArrayList<Object> parentData;

    public ParentAndChildListAdapter(Context context, ArrayList<Object> parentData) {
        this.context = context;

        this.parentData = parentData;
    }

    @Override
    public int getCount() {
        return parentData.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View rowView;
        rowView = LoadparentList(position);
        return rowView;
    }

    private View LoadparentList(int position) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.parentdetailscardview, null, true);
        TextView textviewChildName = view.findViewById(R.id.tv_child_Name);
        TextView textviewBranch = view.findViewById(R.id.tv_child_Branch);
        TextView textviewDivision = view.findViewById(R.id.tv_Child_Division);
        TextView textviewRollNo = view.findViewById(R.id.tv_child_Rollno);
        ImageView circleImageView = view.findViewById(R.id.image_view);
        if (parentData.size() > 0) {
            Log.e("pp", "ss" + parentData);
            final JSONObject data = (JSONObject) parentData.get(position);
            textviewChildName.setText(data.optString("studentFirstName"));
            textviewBranch.setText(data.optString("branchName"));
            textviewDivision.setText(data.optString("divisionId"));
            textviewRollNo.setText(data.optString("classId"));
            String childname = (data.optString("studentFirstName"));
            ColorGenerator cGenerator = ColorGenerator.DEFAULT;
            TextDrawable drawable = TextDrawable.builder()
                    .buildRound(childname.substring(0, 1), cGenerator.getColor(childname));
            circleImageView.setImageDrawable(drawable);
        }
        return view;
    }
}
