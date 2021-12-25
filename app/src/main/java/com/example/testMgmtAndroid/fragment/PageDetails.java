package com.example.testMgmtAndroid.fragment;

import static com.example.testMgmtAndroid.R.id.tabMode22;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.testMgmtAndroid.R;
import com.google.android.material.tabs.TabLayout;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


public class PageDetails extends Fragment {

    TabLayout tabLayout;
    ViewPager viewPager;
    Adapter adapter;
    Context mContext;
    View view;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tab_layout, container, false);
        tabLayout = view.findViewById(tabMode22);
        viewPager = view.findViewById(R.id.pager2);
        try {
            mContext = getContext();

            tabLayout = view.findViewById(tabMode22);
            viewPager = view.findViewById(R.id.pager2);

            tabLayout.setupWithViewPager(viewPager);
            setupViewPager(viewPager);
            TabLayout.Tab tabProfile = tabLayout.getTabAt(0);
            LinearLayout tabLinearLayoutMonthView = (LinearLayout) inflater.inflate(R.layout.layoutcustom, null);
            TextView tabContentProfile = tabLinearLayoutMonthView.findViewById(R.id.tabContent);
            tabContentProfile.setText("Profile");
            tabContentProfile.setTextColor(getResources().getColor(R.color.white));



//            tabContentMonthView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_calendar_today_24, 0,0,0);
            assert tabProfile != null;
            tabProfile.setCustomView(tabContentProfile);

            TabLayout.Tab tabPhotos = tabLayout.getTabAt(1);
            LinearLayout tabLinearLayoutWeekView = (LinearLayout) inflater.inflate(R.layout.layoutcustom, null);
            TextView tabContentphotos = tabLinearLayoutWeekView.findViewById(R.id.tabContent);
            tabContentphotos.setText("Photos");
            tabContentphotos.setTextColor(getResources().getColor(R.color.white));
//            tabContentWeekView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_calendar_today_24, 0,0,0);
            assert tabPhotos != null;
            tabPhotos.setCustomView(tabContentphotos);

            TabLayout.Tab tabDocouments = tabLayout.getTabAt(2);
            LinearLayout tabLinearLayout = (LinearLayout) inflater.inflate(R.layout.layoutcustom, null);
            TextView tabContentDocouments = tabLinearLayout.findViewById(R.id.tabContent);
            tabContentDocouments.setText("Docouments");
            tabContentDocouments.setTextColor(getResources().getColor(R.color.white));
//            tabContentWeekView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_calendar_today_24, 0,0,0);
            assert tabDocouments != null;
            tabDocouments.setCustomView(tabContentDocouments);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return view;
    }


    private void setupViewPager(ViewPager viewPager) {
        adapter = new Adapter(getChildFragmentManager());
        adapter.addFragment(new ProfileChild(), getResources().getString(R.string.user_profile));
        adapter.addFragment(new PhotosChild(), getResources().getString(R.string.my_doctor));
        adapter.addFragment(new DocoumentsChild(), getResources().getString(R.string.my_doctor));

        viewPager.setAdapter(adapter);
    }

    static class Adapter extends FragmentPagerAdapter {

        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public Adapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }

}