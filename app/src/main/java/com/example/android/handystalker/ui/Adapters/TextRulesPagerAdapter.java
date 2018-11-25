package com.example.android.handystalker.ui.Adapters;

import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import com.example.android.handystalker.ui.AddTextArrivalFragment;
import com.example.android.handystalker.ui.AddTextDepartureFragment;


public class TextRulesPagerAdapter extends FragmentStatePagerAdapter {
    int mNumberOfTabs;

    public TextRulesPagerAdapter(FragmentManager fm, int NumberOfTabs) {
        super(fm);
        this. mNumberOfTabs = NumberOfTabs;
    }

    @Override
    public Fragment getItem(int position) {

        switch (position) {
            case 0:
                AddTextArrivalFragment tab1 = new AddTextArrivalFragment();
                return tab1;
            case 1:
                AddTextDepartureFragment tab2 = new AddTextDepartureFragment();
                return tab2;
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return  mNumberOfTabs;
    }
}