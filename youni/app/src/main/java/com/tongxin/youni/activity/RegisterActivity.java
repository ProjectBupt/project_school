package com.tongxin.youni.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
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
import com.tongxin.youni.picker.DormitoryPopWindow2;

import java.util.regex.Pattern;

public class RegisterActivity extends AppCompatActivity implements DormitoryPopWindow2.DormitoryListener {

    private static final String TAG = "RegisterActivity---->";
    private EditText _userName;
    private EditText _stuNumber;
    private TextView _dormitoryNumber;
    private EditText _phone;
    private EditText _SMScode;
    private EditText _password;
    private TextView _sendSMS;
    private TextView _login;

    private LinearLayout rootView;

    private Activity mActivity;

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
            final String stu_number=_stuNumber.getText().toString().trim();
            final String dormitory_number=_dormitoryNumber.getText().toString().trim();
            final String password = _password.getText().toString().trim();
            final String phone = _phone.getText().toString().trim();
            User.signUpOrLoginByMobilePhoneInBackground(phone, SMScode, new LogInCallback<AVUser>() {
                @Override
                public void done(AVUser avUser, AVException e) {
                    if(e == null){
                        Log.i(TAG, "done: 短信验证成功");
                        User user = AVUser.getCurrentUser(User.class);
                        user.setUsername(username);
                        user.setStudentID(stu_number);
                        user.setRoomID(dormitory_number);
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
        String stu_number=_stuNumber.getText().toString();
        String dormitory_number=_dormitoryNumber.getText().toString();

        boolean stunum_matches= Pattern.matches("^\\d{1,10}",stu_number);

        if(username.isEmpty() || username.length()<2){
            valid = false;
            _userName.setError("你确定这是个名字？");
        }

        else if(password.isEmpty() ||password.length() < 6){
            _password.setError(("密码不合法"));
            valid = false;
        }

        else if(!stunum_matches){
            _stuNumber.setError("学号错误");
            valid=false;
        }

        else if("宿舍号:如D1-111".equals(dormitory_number)){
            Toast.makeText(this, "未填写宿舍号", Toast.LENGTH_SHORT).show();
            valid=false;
        }

        else if(SMScode.length() != 6){
            Toast.makeText(this, "验证码错误!!", Toast.LENGTH_SHORT).show();
            valid = false;
        }

        return valid;
    }

    private void initView() {
        mActivity=this;
        rootView= (LinearLayout) findViewById(R.id.root_layout);
        _stuNumber= (EditText) findViewById(R.id.stuNumber);
        _dormitoryNumber = (TextView) findViewById(R.id.dormitoryNumber);
        _userName = (EditText) findViewById(R.id.username);
        _phone = (EditText) findViewById(R.id.phone_number);
        _password = (EditText) findViewById(R.id.password);

        _login = (TextView) findViewById(R.id.login);
        _register_bt = (Button) findViewById(R.id.register_bt);

        _sendSMS = (TextView) findViewById(R.id.send_SMS);
        _sendSMS.setText(Html.fromHtml("<font color=#22a322>|获取验证码</font>"));
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
                            _sendSMS.setText("|"+(millisUntilFinished/1000)+"秒后重发");
                        }

                        @Override
                        public void onFinish() {
                            _sendSMS.setEnabled(true);
                            _sendSMS.setText(Html.fromHtml("<font color=#22a322>|获取验证码</font>"));

                        }
                    }.start();
                }
                else {
                    Log.i("info", "done: error"+e.getMessage());
                }
            }
        });
    }

    public void changeDormitoryNumber(View view) {
//        CityPickerPopWindow mPopWindow = new CityPickerPopWindow(mContext);
//        mPopWindow.showPopupWindow(rootView);
//        mPopWindow.setCityPickListener(this);

//        DormitoryPopWindow mPopWindow= new DormitoryPopWindow(mContext);
//        mPopWindow.showPopupWindow(rootView);
//        mPopWindow.setDormitoryListener(this);

        InputMethodManager m=(InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        m.hideSoftInputFromWindow(mActivity.getCurrentFocus().getWindowToken(),InputMethodManager.HIDE_NOT_ALWAYS);
        DormitoryPopWindow2 mPopWindow= new DormitoryPopWindow2(this);
        mPopWindow.showPopupWindow(rootView);
        mPopWindow.setDormitoryListener(this);
    }

    @Override
    public void pickValue2(String value) {
        _dormitoryNumber.setText(value);
    }
}
