package com.tongxin.youni.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVInstallation;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.LogInCallback;
import com.avos.avoscloud.SaveCallback;
import com.tongxin.youni.R;
import com.tongxin.youni.bean.User;

//TODO 改为用手机号登录

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "LoginActivity";

    private static final int REQUEST_REGISTER = 0;

    private EditText _phone;
    private EditText _password;
    private Button _loginBt;
    private TextView _register;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        initView();


        _loginBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                login();
            }
        });

        _register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this,RegisterActivity.class);
                startActivityForResult(intent,REQUEST_REGISTER);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQUEST_REGISTER && resultCode == RESULT_OK) {
            _phone.setText(data.getStringExtra("phone"));
            _password.setText(data.getStringExtra("password"));
        }
    }

    private void login() {
        if (isValid()) {
            final ProgressDialog progressDialog = new ProgressDialog(LoginActivity.this, ProgressDialog.STYLE_SPINNER);
            progressDialog.setIndeterminate(true);
            progressDialog.setMessage("Authorizing");
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.show();

            final String pushID = AVInstallation.getCurrentInstallation().getInstallationId();
            AVInstallation.getCurrentInstallation().saveInBackground(new SaveCallback() {
                @Override
                public void done(AVException e) {
                    AVInstallation.getCurrentInstallation().saveInBackground();
                }
            });

            final String phone = _phone.getText().toString();
            final String password = _password.getText().toString();


            AVUser.loginByMobilePhoneNumberInBackground(phone, password, new LogInCallback<User>() {

                @Override
                public void done(User user, AVException e) {
                    if(e == null){
                        user.setInstallationId(pushID);
                        Log.i(TAG, "done: AVOS login 成功");
                        progressDialog.dismiss();
                        Intent intent = new Intent(getApplicationContext(),MainActivity.class);
                        startActivity(intent);

                        LoginActivity.this.finish();
//                        EMClient.getInstance().login(phone, password, new EMCallBack() {
//                            @Override
//                            public void onSuccess() {
//                                Log.i(TAG, "onSuccess: login success");
//                                progressDialog.dismiss();
//
//                                Intent intent = new Intent(getApplicationContext(),MainActivity.class);
//                                startActivity(intent);
//
//                                LoginActivity.this.finish();
//                            }
//
//                            @Override
//                            public void onError(int i,final String s) {
//                                Log.i(TAG, "onSuccess: login failed");
//                                runOnUiThread(new Runnable() {
//                                    @Override
//                                    public void run() {
//                                        Toast.makeText(getApplicationContext(),"登陆失败"+s, Toast.LENGTH_LONG).show();
//                                        progressDialog.dismiss();
//                                    }
//                                });
//                            }
//
//                            @Override
//                            public void onProgress(int i, String s) {
//
//                            }
//                        });
                    }
                    else{
                        Log.i(TAG, "done: 登陆失败"+e.getMessage());
                        progressDialog.dismiss();
                    }
                }
            },User.class);
       }
    }

    private boolean isValid() {
        boolean valid = true;

        String phone = _phone.getText().toString();
        String password = _password.getText().toString();
        if(phone.isEmpty()){
            valid = false;
            _phone.setError("invalid username");
        }
        else if(password.isEmpty()) {
            valid = false;
            _password.setError("invalid password");
        }
        return valid;
    }

    private void initView() {
        _phone = (EditText) findViewById(R.id.username);
        _password = (EditText) findViewById(R.id.password);
        _loginBt = (Button) findViewById(R.id.login_bt);
        _register = (TextView) findViewById(R.id.register);

    }
}
