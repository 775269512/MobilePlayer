package com.lzc.mobileplayer.pager;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.lzc.mobileplayer.base.BasePager;

public class ReplaceFragment extends Fragment {

    private BasePager currPager;

    @SuppressLint({"NewApi", "ValidFragment"})
    public ReplaceFragment(BasePager pager) {
        this.currPager = pager;
    }

    public ReplaceFragment(){}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return currPager.rootview;
    }
}