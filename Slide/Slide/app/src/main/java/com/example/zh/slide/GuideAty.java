package com.example.zh.slide;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by charlene on 2016/8/25.
 */
public class GuideAty extends AppCompatActivity implements ViewPager.OnPageChangeListener {

    private ViewPager viewPager;
    private List<View> viewList;
    private MyAdapter adapter;

    private int[] pointIDs=new int[]{R.id.point1,R.id.point2,R.id.point3};
    private List<ImageView> points;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guide);

        initViews();
        initPoints();
    }

    public void initViews(){
        viewPager= (ViewPager) findViewById(R.id.view_pager);
        viewList=new ArrayList<>();
        LayoutInflater inflater=LayoutInflater.from(this);
        viewList.add(inflater.inflate(R.layout.guide1,null));
        viewList.add(inflater.inflate(R.layout.guide2,null));
        viewList.add(inflater.inflate(R.layout.guide3,null));

        viewList.get(2).findViewById(R.id.start_main).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(GuideAty.this,MainActivity.class));
                finish();
            }
        });

        adapter=new MyAdapter(this,viewList);
        viewPager.setAdapter(adapter);
        viewPager.setOnPageChangeListener(this);
    }

    public void initPoints(){
        points=new ArrayList<>();
        for (int i=0;i<viewList.size();i++){
            points.add((ImageView) findViewById(pointIDs[i]));
        }
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        for (int i=0;i<viewList.size();i++){
            if (i==position){
                points.get(i).setImageResource(R.drawable.point_selected);
            }else{
                points.get(i).setImageResource(R.drawable.point);
            }
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }
}
