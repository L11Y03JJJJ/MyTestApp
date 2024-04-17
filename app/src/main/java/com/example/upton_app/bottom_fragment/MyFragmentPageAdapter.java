package com.example.upton_app.bottom_fragment;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import java.util.ArrayList;
import java.util.List;

public class MyFragmentPageAdapter extends FragmentStateAdapter {
    List<Fragment> fragmentList=new ArrayList<>(); //接收传来的fragment
    public MyFragmentPageAdapter(FragmentManager fragmentManager, Lifecycle lifecycle, ArrayList<Fragment> fragments) {
        super(fragmentManager, lifecycle);
        fragmentList=fragments;
        //Log.e("TAG", "MyFragmentPageAdapter: "+ fragmentList.size());
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        return fragmentList.get(position);
    }

    @Override
    public int getItemCount() {
        return fragmentList.size();
    }
}
