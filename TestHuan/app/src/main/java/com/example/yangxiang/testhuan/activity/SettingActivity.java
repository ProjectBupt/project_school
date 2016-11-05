package com.example.yangxiang.testhuan.activity;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.avos.avoscloud.AVUser;
import com.example.yangxiang.testhuan.R;
import com.example.yangxiang.testhuan.fragment.ModifyLoginPhoneFragment;
import com.example.yangxiang.testhuan.fragment.ModifyPasswordFragment;
import com.example.yangxiang.testhuan.widget.TitleBar;

/**
 * Created by 宽伟 on 2016/10/16.
 */

public class SettingActivity extends Activity {
    private Button modifyLoginPhoneButton;//修改登录电话号的按钮
    private Button modifyPasswordButton;//修改密码的按钮
    private Button out;

    private ModifyPasswordFragment modifyPasswordFragment;//修改密码fragment
    private ModifyLoginPhoneFragment modifyLoginPhoneFragment;//修改登录电话号fragment

    private boolean isFrist = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        initBtn();
        initTitleBar();
    }

    private void initBtn() {
        modifyLoginPhoneButton = (Button) findViewById(R.id.id_modify_loginPhone);
        modifyPasswordButton = (Button) findViewById(R.id.id_modify_password);
        out = (Button) findViewById(R.id.id_out);

        modifyLoginPhoneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //弹出diog让用户输入密码
                Intent intent = new Intent(SettingActivity.this,ConfirmActivity.class);
                startActivityForResult(intent ,1);
            }
        });

        modifyPasswordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //直接加载fragment
                modifyPasswordFragment = ModifyPasswordFragment.getInstance();
                FragmentManager fragmentManager = getFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.id_fragment,modifyPasswordFragment);
                if(isFrist) {
                    fragmentTransaction.addToBackStack(null);
                    isFrist=false;
                }
                fragmentTransaction.commit();
                //结束fragment
                modifyPasswordFragment.setFinishSelf(new ModifyPasswordFragment.FinishSelf() {
                    @Override
                    public void doFinish() {
                        FragmentTransaction fra =  getFragmentManager().beginTransaction();
                        fra.remove(modifyPasswordFragment).commit();
                        modifyPasswordFragment = null;
                    }
                });
            }
        });

        out.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AVUser.logOut();
                Intent intent = new Intent(SettingActivity.this,LoginActivity.class);
                startActivity(intent);
                MainActivity.activity.finish();
                finish();
            }
        });
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(data!=null){
            Bundle bundle = data.getExtras();
            boolean isTrue = bundle.getBoolean("intent");
            if(isTrue==true){
                initFragment();//密码正确加载fragment
            }
        }
    }

    private void initFragment() {
        modifyLoginPhoneFragment = new ModifyLoginPhoneFragment();
        FragmentManager fragmentManager = getFragmentManager();
        final FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.id_fragment,modifyLoginPhoneFragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();

        //结束fragment
        modifyLoginPhoneFragment.setFinishSelf(new ModifyLoginPhoneFragment.FinishSelf() {
            @Override
            public void doFinish() {
                FragmentTransaction fra =  getFragmentManager().beginTransaction();
                fra.remove(modifyLoginPhoneFragment).commit();
                modifyLoginPhoneFragment = null;
               // fragmentTransaction.remove(modifyLoginPhoneFragment).commit();
            }
        });
    }

    private void initTitleBar(){
        boolean isImmersive = false;

        final TitleBar titleBar = (TitleBar) findViewById(R.id.titleBar2);

        titleBar.setImmersive(isImmersive);

        titleBar.setBackgroundColor(Color.parseColor("#87CEEB"));

        titleBar.setLeftImageResource(R.mipmap.back_green);
        titleBar.setLeftText("返回");
        titleBar.setLeftTextColor(Color.WHITE);
        titleBar.setLeftClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

            titleBar.setTitle("设置");
        titleBar.setTitleColor(Color.WHITE);
        titleBar.setSubTitleColor(Color.WHITE);
        titleBar.setDividerColor(Color.GRAY);
        titleBar.setActionTextColor(Color.WHITE);
        //title收藏按钮
//        mCollectView = (ImageView) titleBar.addAction(new TitleBar.ImageAction(R.mipmap.collect) {
//            @Override
//            public void performAction(View view) {
//                Toast.makeText(MainActivity.this, "点击了收藏", Toast.LENGTH_SHORT).show();
//                mCollectView.setImageResource(R.mipmap.fabu);
//                titleBar.setTitle(mIsSelected ? "文章详情\n朋友圈" : "帖子详情");
//                mIsSelected = !mIsSelected;
//            }
//        });
    }

}
