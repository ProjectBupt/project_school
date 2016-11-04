package com.example.yangxiang.testhuan.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.yangxiang.testhuan.R;
import com.example.yangxiang.testhuan.adapter.FragmentAdapter;
import com.example.yangxiang.testhuan.fragment.AskFragment;
import com.example.yangxiang.testhuan.fragment.FetchFragment;
import com.example.yangxiang.testhuan.widget.MyViewPager;
import com.example.yangxiang.testhuan.bean.User;

import java.util.ArrayList;
import java.util.List;

import jp.wasabeef.glide.transformations.CropCircleTransformation;

public class UserInfoActivity extends AppCompatActivity {

    private MyViewPager viewPager;
    private List<String> titleList;
    private List<Fragment> fragmentList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info);

        initViews();
    }

    private void initViews(){
        viewPager= (MyViewPager) findViewById(R.id.view_pager);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        toolbar.setNavigationIcon(R.drawable.ease_back);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                Log.e("YOUNI","Back from UserInfoActivity.");
            }
        });

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(UserInfoActivity.this,ChangeInformation.class);
                startActivity(intent);
            }
        });

        TabLayout tabLayout= (TabLayout) findViewById(R.id.tab);

        ImageView avatar = (ImageView) findViewById(R.id.header);
        TextView name = (TextView) findViewById(R.id.user_name);
        name.setText(User.getCurrentUser(User.class).getNick());
        Glide.with(this)
                .load(User.getCurrentUser(User.class).getAvatar())
                .crossFade()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .placeholder(R.mipmap.ic_launcher)
                .bitmapTransform(new CropCircleTransformation(this))
                .into(avatar);

        titleList=new ArrayList<>();
        titleList.add("我帮别人代领的快递");
        titleList.add("别人帮我代领的快递");

        fragmentList=new ArrayList<>();
        fragmentList.add(new FetchFragment());
        fragmentList.add(new AskFragment());

        FragmentAdapter adapter=new FragmentAdapter(getSupportFragmentManager(),fragmentList,titleList);

        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);
    }

}