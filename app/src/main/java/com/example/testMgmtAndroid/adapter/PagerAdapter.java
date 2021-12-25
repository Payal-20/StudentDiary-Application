package com.example.testMgmtAndroid.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.example.testMgmtAndroid.fragment.DocoumentsChild;
import com.example.testMgmtAndroid.fragment.PhotosChild;
import com.example.testMgmtAndroid.fragment.ProfileChild;

public class PagerAdapter extends FragmentPagerAdapter {
int tabcount;
    public PagerAdapter(@NonNull FragmentManager fm, int behavior) {
        super(fm, behavior);
        tabcount=behavior;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
       switch (position){
           case 0:return new ProfileChild();
           case 1:return new PhotosChild();
           case 2:return new DocoumentsChild();
       }
       return null;
    }

    @Override
    public int getCount() {
        return 0;
    }
}
