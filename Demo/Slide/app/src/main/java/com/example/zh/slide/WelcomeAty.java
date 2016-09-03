package com.example.zh.slide;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by charlene on 2016/8/25.
 */
public class WelcomeAty extends AppCompatActivity {

    private boolean isFirstIn=false;
    private static final int TIME=2000;
    private static final int GO_HOME=1000;
    private static final int GO_GUIDE=1001;

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
    }

    private void init(){
        SharedPreferences preferences=getSharedPreferences("weibo",MODE_PRIVATE);
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
        startActivity(new Intent(WelcomeAty.this,MainActivity.class));
        finish();
    }

    private void goGuide(){
        startActivity(new Intent(WelcomeAty.this,GuideAty.class));
        finish();
    }
}
