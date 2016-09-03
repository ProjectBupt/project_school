package com.example.zh.uidemo.fragment;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.PagerTabStrip;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.zh.uidemo.Adapter.FragmentAdapter;
import com.example.zh.uidemo.MyView.ViewPagerIndicator;
import com.example.zh.uidemo.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by charlene on 2016/9/3.
 */
public class FragmentHomePage extends Fragment {

    private List<Fragment> fragments;
    private List<String> titles;
    private ViewPager viewpager;
    private ViewPagerIndicator indicator;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root=inflater.inflate(R.layout.fragment_homepage,container,false);

        initViewPager(root);
        return root;
    }

    private void initViewPager(View root) {

        viewpager= (ViewPager) root.findViewById(R.id.view_pager);
        indicator= (ViewPagerIndicator) root.findViewById(R.id.id_indicator);
        fragments=new ArrayList<>();
        titles=new ArrayList<>();
        fragments.add(new ActivityFragment());
        titles.add("活动");
        fragments.add(new BorrowFragment());
        titles.add("借物");
        fragments.add(new FatchParcelFragment());
        titles.add("代取快递");
        fragments.add(new LostAndFoundFragment());
        titles.add("失物招领");
        fragments.add(new SecondHandFragment());
        titles.add("二手交易");
        fragments.add(new TogetherFragment());
        titles.add("拼团凑单");

        indicator.setVisibleTabCount(3);
        indicator.setTabItemTitles(titles);
        indicator.setViewPager(viewpager,0);
        FragmentAdapter adapter=new FragmentAdapter(getFragmentManager(),fragments);
        viewpager.setAdapter(adapter);
    }
}
