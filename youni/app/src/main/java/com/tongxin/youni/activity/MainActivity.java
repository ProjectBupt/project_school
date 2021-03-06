package com.tongxin.youni.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.opengl.GLUtils;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.avos.avoscloud.AVUser;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.tongxin.youni.R;
import com.tongxin.youni.fragment.ExpressFragment;
import com.tongxin.youni.bean.User;
import com.tongxin.youni.utils.MyActivity;

import java.util.ArrayList;
import java.util.List;

import jp.wasabeef.glide.transformations.CropCircleTransformation;

public class MainActivity extends AppCompatActivity{

    private static final String TAG = "MainActivity";
    private static final int FINISH_INFO = 0xf;

    private List<Fragment> mFragments;
    private FragmentManager mFragmentManager;
    FrameLayout mContent;
    static Activity activity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SharedPreferences preferences=getSharedPreferences("YOUNI",MODE_PRIVATE);
        boolean isFirst=preferences.getBoolean("isFirstIn",true);

        if(isFirst){
            Intent intent = new Intent(this, ChangeInformation.class);
            Bundle bundle = new Bundle();
            bundle.putInt("doWhat",1);
            intent.putExtras(bundle);
            startActivity(intent);
        }

        activity =this;

        mContent = (FrameLayout) findViewById(R.id.content);
        mFragmentManager = getSupportFragmentManager();

        initFragments();

        mFragmentManager.beginTransaction().replace(R.id.content,mFragments.get(0)).commit();

        
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

}
