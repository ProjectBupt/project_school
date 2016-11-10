package com.example.yangxiang.testhuan.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVMobilePhoneVerifyCallback;
import com.avos.avoscloud.AVOSCloud;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.RequestMobileCodeCallback;
import com.example.yangxiang.testhuan.R;
import com.example.yangxiang.testhuan.bean.User;

/**
 * Created by 宽伟 on 2016/10/21.
 */

public class ModifyLoginPhoneFragment extends Fragment {
    private View view;

    private Button getCode;
    private Button submit;

    private EditText verCode;
    private EditText phoneNumber;

    private String phoneNumberString;
    private String verCodeString;

    private AVUser ttUser;

    private FinishSelf myFinishSelf;

    public interface FinishSelf{
           void doFinish();
    }

    public void setFinishSelf(FinishSelf finishSelf){
        this.myFinishSelf = finishSelf;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view  = inflater.inflate(R.layout.modify_loginphone_fragment,container,false);

        ttUser = AVUser.getCurrentUser(User.class);
        initBtn();

        return view;
    }
    private void initBtn() {
        getCode = (Button) view.findViewById(R.id.id_getCode);
        verCode = (EditText) view.findViewById(R.id.id_code);
        submit  = (Button) view.findViewById(R.id.id_submit);
        phoneNumber = (EditText) view.findViewById(R.id.id_phoneNumber);

        //获取验证码
        getCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                phoneNumberString = phoneNumber.getText().toString().trim();
                if(phoneNumberString.length()!=11){
                    Toast.makeText(getActivity(),"请手机号是否输入正确",Toast.LENGTH_SHORT).show();
                }
                else{
                    AVOSCloud.requestSMSCodeInBackground(phoneNumberString, "邮你", "更改电话号码", 10, new RequestMobileCodeCallback() {
                        @Override
                        public void done(AVException e) {
                            if (e == null) {
                                Toast.makeText(getActivity(), "发送成功，注意查收", Toast.LENGTH_SHORT).show();
                                new CountDownTimer(30000,1000){
                                    @Override
                                    public void onTick(long millisUntilFinished) {
                                        getCode.setEnabled(false);
                                        String s = millisUntilFinished/1000 + "秒后发送";
                                        getCode.setText(s);
                                    }
                                    @Override
                                    public void onFinish() {
                                        getCode.setEnabled(true);
                                        getCode.setText("获取验证码");
                                    }
                                }.start();
                            } else {
                                Toast.makeText(getActivity(), "发送失败，检查网络", Toast.LENGTH_SHORT).show();
                                Log.e("Home.OperationVerify", e.getMessage());
                            }
                        }
                    });
                }
            }
        });

        //确定修改
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isOk()) {
                    AVOSCloud.verifyCodeInBackground(verCodeString, phoneNumberString, new AVMobilePhoneVerifyCallback() {
                        @Override
                        public void done(AVException e) {
                            if (e == null) {
                                ttUser.setMobilePhoneNumber(phoneNumberString);
                                ttUser.saveInBackground();
                                ttUser.put("mobilePhoneVerified",true);
                                Toast.makeText(getActivity(), "修改成功", Toast.LENGTH_SHORT).show();
                                myFinishSelf.doFinish();
                            } else {
                                Log.e("Home.DoOperationVerify", e.getMessage());
                            }
                        }
                    });

//                    AVOSCloud.verifyCodeInBackground(verCodeString, phoneNumberString,new AVMobilePhoneVerifyCallback() {
//                        @Override
//                        public void done(AVException e) {
//                            if(e == null){
//                                // 验证成功
//                                ttUser.put("mobilePhoneVerified",true);
//                                ttUser.saveInBackground();
//                                Toast.makeText(getActivity(), "hhhhhhhh", Toast.LENGTH_SHORT).show();
//                            } else {
//                                Toast.makeText(getActivity(), "eeeeeeeee", Toast.LENGTH_SHORT).show();
//                                Log.e("SMS", e.toString());
//                            }
//                        }
//                    });
                }
            }
        });
    }

    public boolean isOk() {
        //phoneNumberString = phoneNumber.getText().toString().trim();
        verCodeString = verCode.getText().toString().trim();

        if(verCodeString==null){
            Toast.makeText(getActivity(),"请填写验证码",Toast.LENGTH_SHORT).show();
            return  false;
        }
        else if(verCodeString.equals("")){
            Toast.makeText(getActivity(),"请填写验证码",Toast.LENGTH_SHORT).show();
            return  false;
        }
        else if (verCodeString.length()!=6){
            Toast.makeText(getActivity(),"check your verCode is right",Toast.LENGTH_SHORT).show();
            return  false;
        }
        return true;
    }
}
