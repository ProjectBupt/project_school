package com.tongxin.youni.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.avos.avoscloud.AVUser;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.tongxin.youni.R;
import com.tongxin.youni.fragment.ExpressFragment;
import com.tongxin.youni.bean.User;

import java.util.ArrayList;
import java.util.List;

import jp.wasabeef.glide.transformations.CropCircleTransformation;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener{

    private static final String TAG = "MainActivity----->";

    private List<Fragment> mFragments;
    private FragmentManager mFragmentManager;
    FrameLayout mContent;
    private ImageView avatar;
    private User current_user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mContent = (FrameLayout) findViewById(R.id.content);
        mFragmentManager = getSupportFragmentManager();

        initFragments();

        mFragmentManager.beginTransaction().add(R.id.content,mFragments.get(0)).commit();

        NavigationView view = (NavigationView) findViewById(R.id.nav_view);
        assert view != null;
        view.setNavigationItemSelectedListener(this);
        View v = view.getHeaderView(0);
        avatar = (ImageView) v.findViewById(R.id.avatar);

        avatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,UserInfoActivity.class);
                DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                assert drawer != null;
                drawer.closeDrawer(GravityCompat.START);
                startActivity(intent);
            }
        });
        TextView textView = (TextView) v.findViewById(R.id.username);
        current_user=AVUser.getCurrentUser(User.class);
        if (current_user!=null){
            textView.setText(current_user.getNick());
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        Glide.with(this)
                .load(current_user.getAvatar())
                .crossFade()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .placeholder(R.mipmap.ic_launcher)
                .bitmapTransform(new CropCircleTransformation(this))
                .into(avatar);
    }

    private void initFragments() {
        mFragments = new ArrayList<>();
        mFragments.add(new ExpressFragment());
    }

    //按下返回键不要退出
    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        switch (id){

            default:
                break;
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        assert drawer != null;
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}