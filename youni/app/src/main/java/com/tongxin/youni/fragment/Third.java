package com.tongxin.youni.fragment;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
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
    private Button image4;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_third,null);
        image1= (ImageView) view.findViewById(R.id.image1);
        image2= (ImageView) view.findViewById(R.id.image2);
        image3= (ImageView) view.findViewById(R.id.image3);
        image4= (Button) view.findViewById(R.id.image4);
        image1.setVisibility(View.GONE);
        image2.setVisibility(View.GONE);
        image3.setVisibility(View.GONE);
        image4.setVisibility(View.GONE);
        image4.setOnClickListener(this);

        return view;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        if (isVisibleToUser==true) {
            startAnim();
        }else{
            if(image2!=null&&image1!=null&&image3!=null&&image4!=null){
                image1.setVisibility(View.GONE);
                image2.setVisibility(View.GONE);
                image3.setVisibility(View.GONE);
                image4.setVisibility(View.GONE);
            }
        }
        super.setUserVisibleHint(isVisibleToUser);
    }

    private void startAnim() {
        ScaleAnimation animation1=new ScaleAnimation(0,1,0,1,
                Animation.RELATIVE_TO_SELF,0.5f,Animation.RELATIVE_TO_SELF,0.5f);
        animation1.setDuration(500);
        image1.startAnimation(animation1);
        image1.setVisibility(View.VISIBLE);
        animation1.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                ScaleAnimation animation2=new ScaleAnimation(0,1,0,1,
                        Animation.RELATIVE_TO_SELF,0.5f,Animation.RELATIVE_TO_SELF,0.5f);
                animation2.setDuration(500);
                animation2.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        final ScaleAnimation animation3=new ScaleAnimation(0,1,0,1,
                                Animation.RELATIVE_TO_SELF,0.5f,Animation.RELATIVE_TO_SELF,0.5f);
                        animation3.setDuration(500);
                        animation3.setAnimationListener(new Animation.AnimationListener() {
                            @Override
                            public void onAnimationStart(Animation animation) {

                            }

                            @Override
                            public void onAnimationEnd(Animation animation) {
                                ScaleAnimation animation4=new ScaleAnimation(0,1,0,1,
                                        Animation.RELATIVE_TO_SELF,0.5f,Animation.RELATIVE_TO_SELF,0.5f);
                                animation4.setDuration(500);
                                image4.startAnimation(animation4);
                                image4.setVisibility(View.VISIBLE);
                            }

                            @Override
                            public void onAnimationRepeat(Animation animation) {

                            }
                        });
                        image3.startAnimation(animation3);
                        image3.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });
                image2.startAnimation(animation2);
                image2.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }

    @Override
    public void onClick(View view) {
        Intent intent=getActivity().getIntent();
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
