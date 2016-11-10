package com.tongxin.youni.fragment;

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
import com.avos.avoscloud.AVOSCloud;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.RequestMobileCodeCallback;
import com.avos.avoscloud.UpdatePasswordCallback;
import com.tongxin.youni.R;

/**
 * Created by 宽伟 on 2016/10/17.
 */

public class ModifyPasswordFragment extends Fragment {

    private View view;

    private Button getCode;
    private Button submit;
    private EditText phoneNumber;
    private EditText verCode;
    private EditText newPassword;

    private String phoneNumberString;
    private String verCodeString;
    private String newPasswordString;

    private FinishSelf finishSelf;

    public interface FinishSelf{
        void doFinish();
    }

    public void setFinishSelf(FinishSelf finishSelf){
        this.finishSelf = finishSelf;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public static ModifyPasswordFragment mo;

    public static ModifyPasswordFragment getInstance(){
        if (mo==null){
            mo=new ModifyPasswordFragment();
        }
        return mo;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.modify_password_fragment,container,false);
        initId();

        return view;
    }

    private void initId() {
        getCode = (Button) view.findViewById(R.id.id_getCode);
        submit = (Button) view.findViewById(R.id.id_submit);
        phoneNumber = (EditText) view.findViewById(R.id.id_phoneNumber);
        verCode = (EditText) view.findViewById(R.id.id_code);
        newPassword = (EditText) view.findViewById(R.id.id_newPassword);

        //获取验证码
        getCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                phoneNumberString = phoneNumber.getText().toString().trim();
                if(phoneNumberString.length()!=11){
                    Toast.makeText(getActivity(),"请检查验证码是否输入正确",Toast.LENGTH_SHORT).show();
                }
                else{
                    AVUser.requestPasswordResetBySmsCodeInBackground(phoneNumberString, new RequestMobileCodeCallback() {
                        @Override
                        public void done(AVException e) {
                            if(e==null){
                                Toast.makeText(getActivity(),"发送成功",Toast.LENGTH_SHORT).show();
                                new CountDownTimer(30000, 1000) {
                                    @Override
                                    public void onTick(long millisUntilFinished) {
                                        getCode.setEnabled(false);
                                        String s = millisUntilFinished/1000+"秒后发送";
                                        getCode.setText(s);
                                    }
                                    @Override
                                    public void onFinish() {
                                        getCode.setEnabled(true);
                                        getCode.setText("获取验证码");
                                    }
                                }.start();
                            }
                            else{
                                Toast.makeText(getActivity(),"发送失败？？"+e.getMessage(),Toast.LENGTH_SHORT).show();
                                Log.e("wrong------->",e.toString());
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
                    AVUser.resetPasswordBySmsCodeInBackground(verCodeString, newPasswordString, new UpdatePasswordCallback() {
                        @Override
                        public void done(AVException e) {
                            if(e==null){
                                Toast.makeText(getActivity(),"修改成功",Toast.LENGTH_SHORT).show();
                                finishSelf.doFinish();
                            }
                            else{
                                Log.e("wrong------>",e.toString());
                                Toast.makeText(getActivity(),"修改失败，验证码是否正确",Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });
    }
    @Override
    public void onResume() {
        super.onResume();
    }

    public boolean isOk() {

        verCodeString = verCode.getText().toString().trim();
        newPasswordString = newPassword.getText().toString().trim();

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
        else if(newPasswordString ==null||newPasswordString.equals("")){
            Toast.makeText(getActivity(),"please input new password",Toast.LENGTH_SHORT).show();
            return  false;
        }
        return true;
    }
}
