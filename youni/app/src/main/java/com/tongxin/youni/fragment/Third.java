package com.tongxin.youni.fragment;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.ScaleAnimation;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import com.tongxin.youni.R;
import com.tongxin.youni.activity.LoginActivity;
import com.tongxin.youni.activity.MainActivity;

/**
 * Created by charlene on 2016/10/15.
 */

public class Third extends Fragment implements View.OnClickListener {
    private ImageView image1;
    private ImageView image2;
    private ImageView image3;
    private Intent intent;
    private Handler mHandler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case 1:
                    Go();
                    break;
            }
        }
    };

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_third,null);
        intent=getActivity().getIntent();
        image1= (ImageView) view.findViewById(R.id.image1);
        image2= (ImageView) view.findViewById(R.id.image2);
        image3= (ImageView) view.findViewById(R.id.image3);
        image1.setAlpha(0f);
        image2.setAlpha(0f);
        image3.setAlpha(0f);
        image3.setOnClickListener(this);

        return view;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        if (isVisibleToUser==true) {
            startAnim();
        }else{
            if(image2!=null&&image1!=null&&image3!=null){
                image1.setAlpha(0f);
                image2.setAlpha(0f);
                image3.setAlpha(0f);
            }
        }
        super.setUserVisibleHint(isVisibleToUser);
    }

    private void startAnim() {
        ObjectAnimator animator1=ObjectAnimator.ofFloat(image1,"alpha",0f,1f);
        animator1.setDuration(1000);
        animator1.setInterpolator(new LinearInterpolator());

        ObjectAnimator animator2=ObjectAnimator.ofFloat(image2,"alpha",0f,1f);
        animator2.setDuration(800);
        animator2.setInterpolator(new LinearInterpolator());
        ObjectAnimator animator3=ObjectAnimator.ofFloat(image3,"alpha",0f,1f);
        animator3.setDuration(800);
        animator3.setInterpolator(new LinearInterpolator());

        AnimatorSet set=new AnimatorSet();
        set.play(animator2).after(animator1);
        set.play(animator3).after(animator2);
        set.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {

            }

            @Override
            public void onAnimationEnd(Animator animator) {
                new Thread(){
                    @Override
                    public void run() {
                        mHandler.sendEmptyMessageDelayed(1,300);
                    }
                }.start();
            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });
        set.start();
    }

    @Override
    public void onClick(View view) {
       Go();
    }

    private void Go(){
        boolean needPassword=intent.getBooleanExtra("needPassword",true);
        if (needPassword){
            startActivity(new Intent(getActivity(), LoginActivity.class));
            getActivity().overridePendingTransition(R.anim.in,R.anim.out);
            getActivity().finish();
        }else {
            startActivity(new Intent(getActivity(), MainActivity.class));
            getActivity().overridePendingTransition(R.anim.in, R.anim.out);
            getActivity().finish();
        }
    }
}
