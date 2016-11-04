package com.example.yangxiang.testhuan.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

import com.example.yangxiang.testhuan.R;
import com.example.yangxiang.testhuan.fragment.First;
import com.example.yangxiang.testhuan.fragment.Second;
import com.example.yangxiang.testhuan.fragment.Third;

import java.util.ArrayList;
import java.util.List;

import me.relex.circleindicator.CircleIndicator;

/**
 * Created by charlene on 2016/8/25.
 */
public class GuideAty extends AppCompatActivity {

    private ViewPager viewPager;
    private List<Fragment> fragments;
    private PagerAdapter adapter;
    private CircleIndicator circleIndicator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.aty_guide);

        initViews();
    }


    private void initViews() {
        viewPager= (ViewPager) findViewById(R.id.view_pager);
        circleIndicator= (CircleIndicator) findViewById(R.id.indicator);
        fragments=new ArrayList<>();
        fragments.add(new First());
        fragments.add(new Second());
        fragments.add(new Third());

        adapter=new MyAdapter(getSupportFragmentManager());
        viewPager.setAdapter(adapter);
        viewPager.setOffscreenPageLimit(3);
        circleIndicator.setViewPager(viewPager);
    }


    class MyAdapter extends FragmentPagerAdapter {

        public MyAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
        }

        @Override
        public int getCount() {
            return fragments.size();
        }
    }
}
