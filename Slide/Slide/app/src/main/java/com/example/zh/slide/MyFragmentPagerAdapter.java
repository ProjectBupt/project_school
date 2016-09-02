package com.example.zh.slide;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

public class MyFragmentPagerAdapter extends FragmentPagerAdapter {
    //存储所有的fragment  
    private List<Fragment> list;
      
    public MyFragmentPagerAdapter(FragmentManager fm, List<Fragment> list){
        super(fm);  
        this.list = list;  
    }  
  
    @Override  
    public Fragment getItem(int arg0) {  
        // TODO Auto-generated method stub  
        return list.get(arg0);  
    }  
  
    @Override  
    public int getCount() {  
        // TODO Auto-generated method stub  
        return list.size();  
    }

}