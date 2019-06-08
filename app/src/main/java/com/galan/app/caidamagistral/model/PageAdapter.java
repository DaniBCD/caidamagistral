package com.galan.app.caidamagistral.model;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.galan.app.caidamagistral.fragments.DuoFragment;
import com.galan.app.caidamagistral.fragments.SoloFragment;
import com.galan.app.caidamagistral.fragments.SquadFragment;

public class PageAdapter extends FragmentPagerAdapter {

    private int numOfTabs;

    public PageAdapter(FragmentManager fm, int numOfTabs) {
        super(fm);
        this.numOfTabs = numOfTabs;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new SoloFragment();
            case 1:
                return new DuoFragment();
            case 2:
                return new SquadFragment();
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return numOfTabs;
    }
}
