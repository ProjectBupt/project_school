package com.tongxin.youni.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVOSCloud;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.LogInCallback;
import com.avos.avoscloud.RequestMobileCodeCallback;
import com.tongxin.youni.R;
import com.tongxin.youni.SmsObserver;
import com.tongxin.youni.bean.User;

public class RegisterActivity extends AppCompatActivity {

    private static final String TAG = "RegisterActivity---->";
    private EditText _userName;
    private EditText _phone;
    private EditText _SMScode;
    private EditText _password;
    private Button _sendSMS;
    private TextView _login;

    private Button _register_bt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_register);

        initObserver();
        initView();

        _login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        _register_bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                register();
            }
        });


    }

    public static int MSG_RECEIVED_CODE = 1;
    private SmsObserver _smsObserver;

    private void initObserver() {

        Handler handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                if(msg.what == MSG_RECEIVED_CODE){
                    _SMScode.setText((String)msg.obj);
                }
            }
        };
        _smsObserver = new SmsObserver(RegisterActivity.this, handler);
        Uri uri = Uri.parse("content://sms");
        getContentResolver().registerContentObserver(uri,true,_smsObserver);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        getContentResolver().unregisterContentObserver(_smsObserver);
    }

    private void register() {
        if(!isValid()){
            onRegisterFailed();
        }
        else {

            final ProgressDialog progressDialog = new ProgressDialog(RegisterActivity.this, ProgressDialog.STYLE_SPINNER);
            progressDialog.setMessage("等待服务器响应");
            progressDialog.setIndeterminate(true);
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.show();

            String SMScode = _SMScode.getText().toString();
            final String username = _userName.getText().toString().trim();
            final String password = _password.getText().toString().trim();
            final String phone = _phone.getText().toString().trim();
            User.signUpOrLoginByMobilePhoneInBackground(phone, SMScode, new LogInCallback<AVUser>() {
                @Override
                public void done(AVUser avUser, AVException e) {
                    if(e == null){
                        Log.i(TAG, "done: 短信验证成功");
                        User user = AVUser.getCurrentUser(User.class);
                        user.setUsername(username);
                        user.setPassword(password);
                        user.setMobilePhoneNumber(phone);
                        user.saveInBackground();
                        Toast.makeText(RegisterActivity.this, "注册成功", Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                        Intent data = new Intent();
                        data.putExtra("phone",phone);
                        data.putExtra("password",password);
                        RegisterActivity.this.setResult(RESULT_OK,data);

                        RegisterActivity.this.finish();

                    }
                    else{
                        Toast.makeText(RegisterActivity.this, "验证码错误", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    private void onRegisterFailed() {
        Toast.makeText(this,"注册失败",Toast.LENGTH_SHORT).show();
    }

    private boolean isValid() {
        boolean valid = true;

        String SMScode = _SMScode.getText().toString();
        String username = _userName.getText().toString();
        String password = _password.getText().toString();

        if(username.isEmpty() || username.length()<2){
            valid = false;
            _userName.setError("你确定这是个名字？");
        }

        else if(password.isEmpty() ||password.length() < 6){
            _password.setError(("密码不合法"));
            valid = false;
        }

        else if(SMScode.length() != 6){
            _SMScode.setError("验证码错误");
            valid = false;
        }

        return valid;
    }

    private void initView() {
        _userName = (EditText) findViewById(R.id.username);
        _phone = (EditText) findViewById(R.id.phone_number);
        _password = (EditText) findViewById(R.id.password);

        _login = (TextView) findViewById(R.id.login);
        _register_bt = (Button) findViewById(R.id.register_bt);

        _sendSMS = (Button) findViewById(R.id.send_SMS);
        _SMScode = (EditText) findViewById(R.id.SMScode);

    }

    public void sendSMS(View view){
        String phone = _phone.getText().toString();

        AVOSCloud.requestSMSCodeInBackground(phone, new RequestMobileCodeCallback() {
            @Override
            public void done(AVException e) {
                if(e == null){

                    Toast.makeText(RegisterActivity.this,"验证码已发送",Toast.LENGTH_SHORT).show();

                    new CountDownTimer(60000, 1000) {
                        @Override
                        public void onTick(long millisUntilFinished) {
                            _sendSMS.setEnabled(false);
                            String s = millisUntilFinished/1000+"秒后重新发送";
                            _sendSMS.setText(s);
                        }

                        @Override
                        public void onFinish() {
                            _sendSMS.setEnabled(true);
                            _sendSMS.setText("重新发送");

                        }
                    }.start();
                }
                else {
                    Log.i("info", "done: error"+e.getMessage());
                }
            }
        });
    }
}
