package com.tongxin.youni.activity;

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
import com.tongxin.youni.R;
import com.tongxin.youni.adapter.FragmentAdapter;
import com.tongxin.youni.fragment.AskFragment;
import com.tongxin.youni.fragment.FetchFragment;
import com.tongxin.youni.widget.MyViewPager;
import com.tongxin.youni.bean.User;

import java.util.ArrayList;
import java.util.List;

import jp.wasabeef.glide.transformations.CropCircleTransformation;

public class UserInfoActivity extends AppCompatActivity {

    private static final int CHANGE_INFO = 0x1;

    private MyViewPager viewPager;
    private List<String> titleList;
    private List<Fragment> fragmentList;
    private ImageView avatar;

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
        toolbar.setNavigationIcon(R.drawable.back);
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
                startActivityForResult(intent,CHANGE_INFO);
            }
        });

        TabLayout tabLayout= (TabLayout) findViewById(R.id.tab);

        avatar = (ImageView) findViewById(R.id.header);
        TextView name = (TextView) findViewById(R.id.user_name);
        name.setText(User.getCurrentUser(User.class).getUsername());
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == CHANGE_INFO) {
            if(resultCode == ChangeInformation.HASCHANGEDIMAGE){
                Glide.with(this)
                        .load(User.getCurrentUser(User.class).getAvatar())
                        .crossFade()
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .placeholder(R.mipmap.ic_launcher)
                        .bitmapTransform(new CropCircleTransformation(this))
                        .into(avatar);
            }
        }
    }
}
