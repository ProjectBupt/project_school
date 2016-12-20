package com.tongxin.youni.fragment;

import android.animation.ObjectAnimator;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Interpolator;
import android.view.animation.OvershootInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import com.tongxin.youni.R;

/**
 * Created by charlene on 2016/10/15.
 */

public class First extends Fragment {

    private ImageView image1;
    private ImageView image2;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_first,null);
        initViews(view);
        startAnim();
        return view;
    }

    private void initViews(View view) {
        image1= (ImageView) view.findViewById(R.id.image1);
        image2= (ImageView) view.findViewById(R.id.image2);
        image1.setVisibility(View.GONE);
        image2.setVisibility(View.GONE);
    }


    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        if (isVisibleToUser==true) {
            startAnim();
        }else{
            if(image2!=null&&image1!=null){
                image1.setVisibility(View.GONE);
                image2.setVisibility(View.GONE);
            }
        }
        super.setUserVisibleHint(isVisibleToUser);
    }

    private void startAnim() {
        if(image2!=null&&image1!=null){
            image1.setVisibility(View.VISIBLE);
            image2.setVisibility(View.VISIBLE);
            ObjectAnimator animator1=ObjectAnimator.ofFloat(image1,"translationX",-500f,0f);
            animator1.setInterpolator(new AccelerateDecelerateInterpolator());
            animator1.setDuration(1000);
            animator1.start();

            ObjectAnimator animator2=ObjectAnimator.ofFloat(image2,"translationX",500f,0f);
            animator2.setInterpolator(new AccelerateDecelerateInterpolator());
            animator2.setDuration(1000);
            animator2.start();
        }
    }
}
