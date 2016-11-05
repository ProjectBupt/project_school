package com.example.yangxiang.testhuan.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.LogInCallback;
import com.example.yangxiang.testhuan.R;
import com.example.yangxiang.testhuan.bean.User;


/**
 * Created by 宽伟 on 2016/10/21.
 */

public class ConfirmActivity extends Activity {
    private Button sure;
    private AVUser ttUser;
    private String passwordInput;//保存用户输入密码
    private EditText passwordEditText;

    private String phoneNumber;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm);
        ttUser = AVUser.getCurrentUser(User.class);

        phoneNumber = ttUser.getMobilePhoneNumber();

        initBtn();
    }

    private void initBtn() {
        passwordEditText = (EditText) findViewById(R.id.passwordEditText);
        sure = (Button) findViewById(R.id.sure);

        sure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                passwordInput = passwordEditText.getText().toString().trim();
                final Intent intent =new Intent();
                AVUser.loginByMobilePhoneNumberInBackground(phoneNumber, passwordInput, new LogInCallback<AVUser>() {
                    @Override
                    public void done(AVUser avUser, AVException e) {
                        if(e==null){
                            intent.putExtra("intent",true);
                            setResult(1,intent);
                            finish();
                        }
                        else{
                            intent.putExtra("intent",false);
                            Toast.makeText(ConfirmActivity.this,"密码输入错误",Toast.LENGTH_SHORT).show();
                        }
                    }
                });

            }
        });
    }


}
