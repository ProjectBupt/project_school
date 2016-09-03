package com.example.zh.uidemo.MyView;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.CornerPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.zh.uidemo.R;

import java.util.List;

/**
 * Created by charlene on 2016/9/3.
 */
public class ViewPagerIndicator extends LinearLayout {

    private Paint mPaint;
    private Path mPath;
    private int mWidth;
    private int mHeight;
    private static final float RADIO_TRIANGLE_WIDTH=1/10f;
    private final int DIMENSION_TRIANGLE_WIDTH_MAX= (int) (getScreenWidth()/3*RADIO_TRIANGLE_WIDTH);
    private int initTranslationX;
    private int mTranslationX;

    private int TabVisibleCount;
    private static final int COUNT_DEFAULT_TAB=4;

    private List<String> mTitles;

    public ViewPagerIndicator(Context context) {
        this(context,null);
    }

    public ViewPagerIndicator(Context context, AttributeSet attrs) {
        super(context, attrs);
        //获取可见Tab数量
        TypedArray a=context.obtainStyledAttributes(attrs, R.styleable.ViewPagerIndicator);
        TabVisibleCount=a.getInt(R.styleable.ViewPagerIndicator_visible_tab_count,COUNT_DEFAULT_TAB);
        if (TabVisibleCount<0){
            TabVisibleCount=COUNT_DEFAULT_TAB;
        }
        a.recycle();
        //初始化画笔
        mPaint=new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setColor(Color.parseColor("#ffffffff"));
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setPathEffect(new CornerPathEffect(3));
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        canvas.save();
        canvas.translate(initTranslationX+mTranslationX,getHeight());
        canvas.drawPath(mPath,mPaint);
        canvas.restore();

        super.dispatchDraw(canvas);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mWidth= (int) (w/TabVisibleCount*RADIO_TRIANGLE_WIDTH);
        mWidth=Math.min(mWidth,DIMENSION_TRIANGLE_WIDTH_MAX);
        initTranslationX=w/TabVisibleCount/2-mWidth/2;
        initTriangle();
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        int cCount=getChildCount();
        if (cCount==0){
            return;
        }
        for (int i=0;i<cCount;i++){
            View view=getChildAt(i);
            LayoutParams lp= (LayoutParams) view.getLayoutParams();
            lp.weight=0;
            lp.width=getScreenWidth()/TabVisibleCount;
            view.setLayoutParams(lp);
        }

        setItemClickEvent();
    }

    /**
     * 获得屏幕宽度
     * @return
     */
    private int getScreenWidth() {
        WindowManager wm= (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics=new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(outMetrics);
        return outMetrics.widthPixels;
    }

    private void initTriangle() {
        mHeight=mWidth/2;

        mPath=new Path();
        mPath.moveTo(0,0);
        mPath.lineTo(mWidth,0);
        mPath.lineTo(mWidth/2,-mHeight);
        mPath.close();
    }

    public void scroll(int position, float positionOffset) {
        int tabWidth=getWidth()/TabVisibleCount;
        mTranslationX= (int) (tabWidth*positionOffset+tabWidth*position);

        //容器移动，在Tab处于移动至最后一个时
        if ((position>=TabVisibleCount-2)&&positionOffset>0&&getChildCount()>TabVisibleCount&&position<(getChildCount()-2)){
            if (TabVisibleCount!=1){
                this.scrollTo((int) ((position-(TabVisibleCount-2))*tabWidth+tabWidth*positionOffset),0);
            }else {
                this.scrollTo((int) (position*tabWidth+tabWidth*positionOffset),0);
            }
        }

        invalidate();
    }

    public void setTabItemTitles(List<String> titles){
        if (titles!=null&&titles.size()>0){
            this.removeAllViews();
            mTitles=titles;
            for (String title:mTitles) {
                addView(generateTextView(title));
            }
        }
        setItemClickEvent();
    }

    /**
     * 设置可见tab数量
     * @param count
     */
    public void setVisibleTabCount(int count){
        TabVisibleCount=count;
    }

    private static final int COLOR_TEXT_NORMAL=0x77FFFFFF;
    private static final int COLOR_TEXT_SELECTED=0xFFFFFFFF;

    /**
     * 根据title创建tab
     * @param title
     * @return
     */
    private View generateTextView(String title) {
        TextView tv=new TextView(getContext());
        LayoutParams lp=new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        lp.width=getScreenWidth()/TabVisibleCount;
        tv.setText(title);
        tv.setGravity(Gravity.CENTER);
        tv.setTextSize(TypedValue.COMPLEX_UNIT_SP,16);
        tv.setTextColor(COLOR_TEXT_NORMAL);
        tv.setLayoutParams(lp);
        return tv;
    }

    /**
     * 重置tab文本颜色
     */
    public void resetTextView(){
        for(int i=0;i<getChildCount();i++){
            TextView tv= (TextView) getChildAt(i);
            tv.setTextColor(COLOR_TEXT_NORMAL);
        }
    }

    private ViewPager mViewPager;

    /**
     * 设置关联的ViewPager
     * @param viewPager
     * @param position
     */
    public void setViewPager(ViewPager viewPager, int position){
        mViewPager=viewPager;
        mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                scroll(position,positionOffset);
            }

            @Override
            public void onPageSelected(int position) {
                resetTextView();
                //高亮选中tab的文本
                TextView tv= (TextView) getChildAt(position);
                tv.setTextColor(COLOR_TEXT_SELECTED);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        TextView tv= (TextView) getChildAt(position);
        //高亮选中tab的文本
        tv.setTextColor(COLOR_TEXT_SELECTED);
        mViewPager.setCurrentItem(position);
    }

    /**
     * 设置tab点击事件
     */
    private void setItemClickEvent(){
        int cCount=getChildCount();
        for (int i=0;i<cCount;i++){
            final int j=i;
            View view=getChildAt(i);
            view.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    mViewPager.setCurrentItem(j);
                }
            });
        }
    }
}
