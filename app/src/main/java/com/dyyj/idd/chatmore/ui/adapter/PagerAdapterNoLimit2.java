package com.dyyj.idd.chatmore.ui.adapter;

import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.view.ViewGroup;

import com.dyyj.idd.chatmore.ui.dialog.fragment.SlidingViewPagerFragment;

import java.util.ArrayList;

public class PagerAdapterNoLimit2 extends FragmentStatePagerAdapter {

    private final FragmentManager fm;
    private ArrayList<SlidingViewPagerFragment> fragments;


    public PagerAdapterNoLimit2(FragmentManager fm, ViewPager viewPager, ArrayList<SlidingViewPagerFragment> fragments) {
        super(fm);
        this.fm = fm;
        this.fragments = fragments;
        viewPager.setAdapter(this);
        viewPager.setOffscreenPageLimit(1);
        viewPager.setCurrentItem(3 * fragments.size());
    }

    public void notifyChangeData(ViewPager viewPager, ArrayList<SlidingViewPagerFragment> fragments) {
        if (this.fragments != null) {
            FragmentTransaction ft = fm.beginTransaction();
            for (Fragment f : this.fragments) {
                ft.remove(f);
            }
            ft.commit();
            fm.executePendingTransactions();
        }
        this.fragments = fragments;
        notifyDataSetChanged();
//        viewPager.setCurrentItem(3 * fragments.size());
    }

    @Override
    public Fragment getItem(int position) {
        SlidingViewPagerFragment fragment = fragments.get(position % fragments.size());
        FragmentTransaction ft = fm.beginTransaction();
        ft.remove(fragment);
        ft.commit();
        fm.executePendingTransactions();
        return fragment;
    }

    @Override
    public int getCount() {
        return Integer.MAX_VALUE;
    }

    @Override
    public int getItemPosition(@NonNull Object object) {
//        return super.getItemPosition(object);
        return POSITION_NONE;
    }

//    @Override
//    public long getItemId(int position) {
//        return super.getItemId(position % fragments.size());
//    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        return super.instantiateItem(container, position);
    }
}
