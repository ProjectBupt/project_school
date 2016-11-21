package com.tongxin.youni.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.NestedScrollView;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
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

public class UserInfoActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener, View.OnTouchListener {

    private static final int CHANGE_INFO = 0x1;
    private static final int COMPLETE_REFRESH=0x1001;

    private MyViewPager viewPager;
    private FragmentAdapter adapter;
    private List<String> titleList;
    private List<Fragment> fragmentList;
    private ImageView avatar;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private NestedScrollView mScrollView;
    private AppBarLayout appBarLayout;
    private TextView name;

    private Handler mHandler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case COMPLETE_REFRESH:
                    adapter.notifyDataSetChanged();
                    viewPager.setAdapter(adapter);
                    mSwipeRefreshLayout.setRefreshing(false);
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info);

        initViews();
    }

    private void initViews(){
        viewPager= (MyViewPager) findViewById(R.id.view_pager);
        viewPager.setOnTouchListener(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        toolbar.setNavigationIcon(R.drawable.back);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                Log.i("YOUNI","Back from UserInfoActivity.");
            }
        });

        final FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(UserInfoActivity.this,ChangeInformation.class);
                startActivityForResult(intent,CHANGE_INFO);
            }
        });

        TabLayout tabLayout= (TabLayout) findViewById(R.id.tab);

        avatar = (ImageView) findViewById(R.id.header);
        name = (TextView) findViewById(R.id.user_name);
        name.setText(User.getCurrentUser(User.class).getUsername());
        Glide.with(this)
                .load(User.getCurrentUser(User.class).getAvatar())
                .crossFade()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .placeholder(R.drawable.default_header)
                .bitmapTransform(new CropCircleTransformation(this))
                .into(avatar);

        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh);
        mSwipeRefreshLayout.setOnRefreshListener(this);
        //设置样式刷新显示的位置
        mSwipeRefreshLayout.setProgressViewOffset(true, -20, 100);

        mScrollView= (NestedScrollView) findViewById(R.id.nested_scroll_view);
        mScrollView.getViewTreeObserver().addOnScrollChangedListener(new  ViewTreeObserver.OnScrollChangedListener() {
            @Override
            public void onScrollChanged() {
                if (mScrollView.getScrollY()!=0){
                    mSwipeRefreshLayout.setEnabled(false);
                }
            }
        });

        appBarLayout = (AppBarLayout) findViewById(R.id.app_bar);
        appBarLayout.setOnSystemUiVisibilityChangeListener(new View.OnSystemUiVisibilityChangeListener() {
            @Override
            public void onSystemUiVisibilityChange(int visibility) {
                if (visibility==View.VISIBLE){
                    mSwipeRefreshLayout.setEnabled(true);
                    name.setVisibility(View.VISIBLE);
                }else{
                    mSwipeRefreshLayout.setEnabled(false);
                    name.setVisibility(View.GONE);
                }
            }
        });
        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {

                if (verticalOffset >= 0) {
                    mSwipeRefreshLayout.setEnabled(true);
                } else {
                    mSwipeRefreshLayout.setEnabled(false);
                }
            }
        });

        titleList=new ArrayList<>();
        titleList.add("我帮别人代领的快递");
        titleList.add("别人帮我代领的快递");

        fragmentList=new ArrayList<>();
        fragmentList.add(new FetchFragment());
        fragmentList.add(new AskFragment());

        adapter=new FragmentAdapter(getSupportFragmentManager(),fragmentList,titleList);

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

    @Override
    public void onRefresh() {
        fragmentList.clear();
        fragmentList.add(new FetchFragment());
        fragmentList.add(new AskFragment());
        mHandler.sendEmptyMessageDelayed(COMPLETE_REFRESH,1000);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        int action = event.getAction();
        switch (action) {
            case MotionEvent.ACTION_DOWN:// 经测试，ViewPager的DOWN事件不会被分发下来
            case MotionEvent.ACTION_MOVE:
                mSwipeRefreshLayout.setEnabled(false);
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                mSwipeRefreshLayout.setEnabled(true);
                break;
        }
        return false;
    }
}
