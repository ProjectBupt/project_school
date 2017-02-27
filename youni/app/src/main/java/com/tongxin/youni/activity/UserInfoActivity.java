package com.tongxin.youni.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.widget.NestedScrollView;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.avos.avoscloud.AVUser;
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
    private static final int COMPLETE_REFRESH=0x1001;

    private User CurrentUser;

    private MyViewPager viewPager;
    private FragmentAdapter adapter;
    private List<String> titleList;
    private List<Fragment> fragmentList;
    private ImageView avatar;
    private NestedScrollView mScrollView;
    private AppBarLayout appBarLayout;
    private TextView Name;
    private TextView Credit;
    private TextView QuantityOfOrder;
    private TextView QuantityOfAsking;

    private Handler mHandler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case COMPLETE_REFRESH:
                    adapter.notifyDataSetChanged();
                    viewPager.setAdapter(adapter);
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info);
        CurrentUser= AVUser.getCurrentUser(User.class);
        initViews();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (CurrentUser!=null){
            avatar = (ImageView) findViewById(R.id.header);
            Glide.with(this)
                    .load(CurrentUser.getAvatar())
                    .crossFade()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .placeholder(R.drawable.default_header)
                    .bitmapTransform(new CropCircleTransformation(this))
                    .into(avatar);
        }else{
            Toast.makeText(this, "网络异常！！", Toast.LENGTH_SHORT).show();
            Log.e("UserInfoActivity","CurrentUser is null!!");
        }
    }

    private void initViews(){
        Name = (TextView) findViewById(R.id.user_name);
        Credit= (TextView) findViewById(R.id.credit);
        QuantityOfOrder= (TextView) findViewById(R.id.quantity_of_order);
        QuantityOfAsking= (TextView) findViewById(R.id.quantity_of_asking);
        if (CurrentUser!=null){
            Name.setText(CurrentUser.getUsername());
            Credit.setText("积    分:"+CurrentUser.getCredit());
            QuantityOfAsking.setText("请求数:"+CurrentUser.getQuantityOfAsking());
            QuantityOfOrder.setText("接单数:"+CurrentUser.getQuantityOfOrder());
        }else{
            Toast.makeText(this, "网络异常！！", Toast.LENGTH_SHORT).show();
            Log.e("UserInfoActivity","CurrentUser is null!!");
        }

        viewPager= (MyViewPager) findViewById(R.id.view_pager);

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

        mScrollView= (NestedScrollView) findViewById(R.id.nested_scroll_view);

        appBarLayout = (AppBarLayout) findViewById(R.id.app_bar);

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
}
