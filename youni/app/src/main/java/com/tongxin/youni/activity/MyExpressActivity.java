package com.tongxin.youni.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.tongxin.youni.R;
import com.tongxin.youni.fragment.AskFragment;
import com.tongxin.youni.fragment.FetchFragment;
import com.tongxin.youni.fragment.ModifyLoginPhoneFragment;
import com.tongxin.youni.fragment.ModifyPasswordFragment;

/**
 * Created by charlene on 2017/3/1.
 */

public class MyExpressActivity extends AppCompatActivity  {
    private FragmentManager manager;
    private FragmentTransaction transaction;
    private int tag;

    private Toolbar toolbar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_express);
        Intent intent=getIntent();
        tag=intent.getIntExtra("tag",UserCenterActivity.GO_ASK);
        manager=getSupportFragmentManager();
        transaction=manager.beginTransaction();
        initView();
    }

    private void initView() {
        toolbar= (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        Bundle b;
        switch (tag){
            case UserCenterActivity.GO_ASK:
                toolbar.setTitle("别人帮我代领的快递");
                AskFragment ask=new AskFragment();
                transaction.replace(R.id.container,ask).commit();
                break;
            case UserCenterActivity.GO_FETCH:
                toolbar.setTitle("我帮别人代领的快递");
                FetchFragment fetch=new FetchFragment();
                transaction.replace(R.id.container,fetch).commit();
                break;
            case UserCenterActivity.GO_MODIFY_PASSWORD:
                toolbar.setTitle("修改密码");
                ModifyPasswordFragment modifyPasswordFragment = ModifyPasswordFragment.getInstance();
                transaction.replace(R.id.container,modifyPasswordFragment);
                transaction.commit();
                modifyPasswordFragment.setFinishSelf(new ModifyPasswordFragment.FinishSelf() {
                    @Override
                    public void doFinish() {
                        finish();
                    }
                });
                break;
            case UserCenterActivity.GO_PHONE_NUMBER:
                toolbar.setTitle("修改电话号码");
                ModifyLoginPhoneFragment modifyLoginPhoneFragment=new ModifyLoginPhoneFragment();
                modifyLoginPhoneFragment.setFinishSelf(new ModifyLoginPhoneFragment.FinishSelf() {
                    @Override
                    public void doFinish(String phone_number) {
                        Intent data=new Intent();
                        data.putExtra("data",phone_number);
                        setResult(RESULT_OK,data);
                        finish();
                    }
                });
                transaction.replace(R.id.container,modifyLoginPhoneFragment);
                transaction.commit();
                break;
        }
    }
}
