package com.example.yangxiang.testhuan.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.avos.avoscloud.AVUser;
import com.example.yangxiang.testhuan.R;
import com.example.yangxiang.testhuan.bean.User;

/**
 * Created by charlene on 2016/8/25.
 */
public class Welcome extends AppCompatActivity {

    private boolean isFirstIn=false;
    private static final int TIME=2000;
    private static final int GO_HOME=0x1000;
    private static final int GO_GUIDE=0x1001;
    private User currentUser;

    private Handler mHandler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case GO_HOME:
                    goHome();
                    break;
                case GO_GUIDE:
                    goGuide();
                    break;
            }
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        init();
        currentUser= AVUser.getCurrentUser(User.class);
    }

    private void init(){
        SharedPreferences preferences=getSharedPreferences("YOUNI",MODE_PRIVATE);
        isFirstIn=preferences.getBoolean("isFirstIn",true);
        if (!isFirstIn){
            mHandler.sendEmptyMessageDelayed(GO_HOME,TIME);
        }else {
            mHandler.sendEmptyMessageDelayed(GO_GUIDE,TIME);
            SharedPreferences.Editor editor=preferences.edit();
            editor.putBoolean("isFirstIn",false);
            editor.commit();
        }
    }

    private void goHome(){
        startActivity(new Intent(Welcome.this,MainActivity.class));
        overridePendingTransition(R.anim.in,R.anim.out);
        finish();
    }

    private void goGuide(){
        Intent i=new Intent(Welcome.this,GuideAty.class);
        if (currentUser!=null){
            i.putExtra("needPassword",false);
        }else{
            i.putExtra("needPassword",true);
        }
        startActivity(new Intent(Welcome.this,GuideAty.class));
        finish();
    }
}