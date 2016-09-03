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
public class Welcome extends AppCompatActivity {

    private boolean isFirstIn=false;
    private static final int TIME=2000;
    private static final int GO_HOME=100;
    private static final int GO_GUIDE=101;

    private Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case GO_GUIDE:
                    goHome();
                    break;
                case GO_HOME:
                    goGuide();
                    break;
            }
        }
    };

    private void goGuide() {
        startActivity(new Intent(Welcome.this,GuideAty.class));
        finish();
    }

    private void goHome() {
        startActivity(new Intent(Welcome.this,MainActivity.class));
        finish();
    }


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        init();
    }

    private void init(){
        SharedPreferences preferences=getSharedPreferences("slide", MODE_PRIVATE);
        isFirstIn=preferences.getBoolean("isFirstIn",true);
        if (!isFirstIn){
            handler.sendEmptyMessageDelayed(GO_HOME,TIME);
        }else {
            handler.sendEmptyMessageDelayed(GO_GUIDE,TIME);
            SharedPreferences.Editor editor=preferences.edit();
            editor.putBoolean("isFirstIn",false);
            editor.commit();
        }
    }
}
