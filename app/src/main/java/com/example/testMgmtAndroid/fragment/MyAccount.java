package com.example.testMgmtAndroid.fragment;

import static android.content.Context.MODE_PRIVATE;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;
import com.example.testMgmtAndroid.R;
import com.example.testMgmtAndroid.activity.Constant;
import com.example.testMgmtAndroid.activity.MainActivity;
import com.example.testMgmtAndroid.adapter.ParentAndChildListAdapter;
import com.example.testMgmtAndroid.helper.ApiCallHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class MyAccount extends Fragment {
    SharedPreferences preferences;
    ListView listView;
    ParentAndChildListAdapter parentAndChildListAdapter;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_my_account, container, false);
        ((MainActivity) getActivity()).getSupportActionBar().setTitle("My Account");
        preferences = this.getActivity().getSharedPreferences("user_data", MODE_PRIVATE);

        try {
            JSONObject data = new JSONObject();
            ArrayList<Object> accountData = new ArrayList<Object>();


           String parentName = preferences.getString("firstName", "");

            TextView textviewPaentName = view.findViewById(R.id.tv_Parent_Name);
            TextView textviewPaentMobileNo = view.findViewById(R.id.tv_Parent_mobileMo);
            TextView textviewPaentemail = view.findViewById(R.id.tv_Parent_email);
            TextView textviewPaentaddress = view.findViewById(R.id.tv_Parent_Addresss);
            ImageView imgParentProfile = view.findViewById(R.id.img_parent);
            Log.e("MyAccount", " parentName ==== " + parentName.substring(0, 1));


            textviewPaentName.setText(preferences.getString("firstName", ""));
            textviewPaentemail.setText(preferences.getString("email", ""));
            textviewPaentMobileNo.setText(preferences.getString("phone", ""));


            ColorGenerator cGenerator = ColorGenerator.DEFAULT;
            TextDrawable drawable = TextDrawable.builder()
                    .buildRound(parentName.substring(0, 1), cGenerator.getColor(parentName));
            imgParentProfile.setImageDrawable(drawable);





            JSONArray childData = new JSONArray(preferences.getString("children", ""));
            if (childData != null) {
                for (int i = 0; i < childData.length(); i++) {
                    accountData.add(childData.get(i));
                }
            }

            parentAndChildListAdapter = new ParentAndChildListAdapter(getContext(), accountData);
            listView = view.findViewById(R.id.list_parent);
            listView.setAdapter(parentAndChildListAdapter);
            parentAndChildListAdapter.notifyDataSetChanged();
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                    try {
                        JSONObject data = new JSONObject();
                        data.put("schoolId",1);
                        data.put("studentId",58);
                        ApiCallHandler childDetailsHandler = new ApiCallHandler("POST","https://schooladmin.headwaytechies.com/api/students/getStudentProfileDetails", getContext(), Constant.LOAD_CHILD_DETAILS);
                        childDetailsHandler.execute(data.toString());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    Fragment someFragment = new PageDetails();
                    Fragment Profile = new ProfileChild();
                    Bundle args = new Bundle();
                    args.putString("accountData1", String.valueOf(accountData.get(position)));
                    Profile.setArguments(args);
                    FragmentTransaction transaction = getFragmentManager().beginTransaction();
                    transaction.replace(R.id.content_frame, someFragment); // give your fragment container id in first parameter
                    transaction.addToBackStack(null);  // if written, this transaction will be added to backstack
                    transaction.commit();
//                    FragmentTransaction transaction = getFragmentManager().beginTransaction();
//                    transaction.replace(R.id.content_frame, someFragment); // give your fragment container id in first parameter
//                    transaction.addToBackStack(null);  // if written, this transaction will be added to backstack
//                    transaction.commit();




                }
            });

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return view;
    }

}