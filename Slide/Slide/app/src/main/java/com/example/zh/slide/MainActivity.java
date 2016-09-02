package com.example.zh.slide;

import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.drawable.Drawable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.nightonke.boommenu.BoomMenuButton;
import com.nightonke.boommenu.Types.ButtonType;
import com.nightonke.boommenu.Types.DimType;
import com.nightonke.boommenu.Types.PlaceType;
import com.nightonke.boommenu.Util;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, ViewPager.OnPageChangeListener, NavigationView.OnNavigationItemSelectedListener {

    private TextView btn_first;
    private TextView btn_second;
    private TextView btn_third;
    private TextView btn_four;
    private TextView btn_fifth;

    private BoomMenuButton menuButton;

    private List<Fragment> fragmentList=new ArrayList<Fragment>();

    private Toolbar toolbar;
    private ViewPager myviewpager;
    private NavigationView navigationView;
    private DrawerLayout drawerLayout;

    //作为指示标签的按钮
    private ImageView cursor;
    //标志指示标签的横坐标
    float cursorX = 0;
    //所有按钮的宽度的数组
    private int[] widthArgs;
    //所有标题按钮的数组
    private TextView[] btnArgs;

    private int offset=0;
    private int tranW=0;
    private int one=0;
    private ImageView imageView;
    private int currentIndex=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initToolBar();
        initView();
        initCursorPos();
        currentIndex=myviewpager.getCurrentItem();
    }

    public void initToolBar(){
        toolbar= (Toolbar) findViewById(R.id.tool_bar);
        navigationView= (NavigationView) findViewById(R.id.nav_view);
        drawerLayout= (DrawerLayout) findViewById(R.id.drawer_layout);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        toolbar.setTitle("");
        toolbar.setOnMenuItemClickListener(onMenuItemClickListener);
        toolbar.setNavigationIcon(R.drawable.ic_action_storage);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RotateAnimation rotateAnimation=new RotateAnimation(0,90);
                rotateAnimation.setDuration(10);
                drawerLayout.openDrawer(Gravity.LEFT);
            }
        });

//        ActionBarDrawerToggle toggle=new ActionBarDrawerToggle(this,drawerLayout,toolbar,R.string.navigation_drawer_open,R.string.navigation_drawer_close);
//        drawerLayout.setDrawerListener(toggle);
//        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu,menu);
        return true;
    }

    public Toolbar.OnMenuItemClickListener onMenuItemClickListener=new Toolbar.OnMenuItemClickListener() {
        @Override
        public boolean onMenuItemClick(MenuItem item) {
            switch (item.getItemId()){
                case R.id.search:
                    Toast.makeText(MainActivity.this, "你点击了Search按钮!", Toast.LENGTH_SHORT).show();
                    break;
            }
            return true;
        }
    };

    public void initView(){
        myviewpager = (ViewPager)this.findViewById(R.id.myviewpager);

        btn_first = (TextView)this.findViewById(R.id.btn_first);
        btn_second = (TextView) this.findViewById(R.id.btn_second);
        btn_third = (TextView) this.findViewById(R.id.btn_third);
        btn_four = (TextView) this.findViewById(R.id.btn_four);
        btn_fifth = (TextView) this.findViewById(R.id.btn_fifth);
        //初始化按钮数组
        btnArgs = new TextView[]{btn_first,btn_second,btn_third,btn_four,btn_fifth};
        widthArgs = new int[]{btn_first.getWidth(),
                btn_second.getWidth(),
                btn_third.getWidth(),
                btn_four.getWidth(),
                btn_fifth.getWidth()};

        btn_first.setOnClickListener(this);
        btn_second.setOnClickListener(this);
        btn_third.setOnClickListener(this);
        btn_four.setOnClickListener(this);
        btn_fifth.setOnClickListener(this);

        fragmentList.add(new FirstFragment());
        fragmentList.add(new ScecondFragment());
        fragmentList.add(new ThirdFragment());
        fragmentList.add(new ForthFragment());
        fragmentList.add(new FifthFragment());

        MyFragmentPagerAdapter adapter = new MyFragmentPagerAdapter(getSupportFragmentManager(),fragmentList);
        myviewpager.setAdapter(adapter);
        myviewpager.setOnPageChangeListener(this);

        //先重置所有按钮颜色
        resetButtonColor();
        btn_first.setTextColor(Color.WHITE);

    }

    public void initBoomButton(){
        menuButton= (BoomMenuButton) findViewById(R.id.boom);

        int[] drawablesIDs=new int[]{R.drawable.heart,R.drawable.copy,R.drawable.github};
        Drawable[] drawables=new Drawable[3];
        for (int i=0;i<3;i++){
            drawables[i]= ContextCompat.getDrawable(this,drawablesIDs[i]);
        }
        String[] subButtonTexts = new String[]{"", "", ""};
        int[][] subButtonColors = new int[3][2];
        subButtonColors[0][1] = ContextCompat.getColor(this, R.color.colorPrimary);
        subButtonColors[0][0] = Util.getInstance().getPressedColor(subButtonColors[0][1]);

        subButtonColors[1][1] = ContextCompat.getColor(this, R.color.doubanlv);
        subButtonColors[1][0] = Util.getInstance().getPressedColor(subButtonColors[1][1]);

        subButtonColors[2][1] = ContextCompat.getColor(this, R.color.font_white);
        subButtonColors[2][0] = Util.getInstance().getPressedColor(subButtonColors[2][1]);
        BoomMenuButton.Builder builder=new BoomMenuButton.Builder();
        builder.subButtons(drawables,subButtonColors,subButtonTexts)
                .place(PlaceType.CIRCLE_3_3)
                .button(ButtonType.CIRCLE)
                .onSubButtonClick(new BoomMenuButton.OnSubButtonClickListener() {
                    @Override
                    public void onClick(int buttonIndex) {
                        switch(buttonIndex){
                            case 0:
                                Toast.makeText(MainActivity.this, "haha"+buttonIndex, Toast.LENGTH_SHORT).show();
                                break;
                            case 1:
                                Toast.makeText(MainActivity.this, "haha"+buttonIndex, Toast.LENGTH_SHORT).show();
                                break;
                            case 2:
                                Toast.makeText(MainActivity.this, "haha"+buttonIndex, Toast.LENGTH_SHORT).show();
                                break;
                        }
                    }
                })
                .duration(700)
                .delay(100)
                .rotateDegree(360)
                .dim(DimType.DIM_9)
                .init(menuButton);
    }

    public void resetButtonColor(){
        btn_first.setTextColor(Color.parseColor("#CDCDCD"));
        btn_second.setTextColor(Color.parseColor("#CDCDCD"));
        btn_third.setTextColor(Color.parseColor("#CDCDCD"));
        btn_four.setTextColor(Color.parseColor("#CDCDCD"));
        btn_fifth.setTextColor(Color.parseColor("#CDCDCD"));
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_first:
                myviewpager.setCurrentItem(0);
                break;
            case R.id.btn_second:
                myviewpager.setCurrentItem(1);
                break;
            case R.id.btn_third:
                myviewpager.setCurrentItem(2);
                break;
            case R.id.btn_four:
                myviewpager.setCurrentItem(3);
                break;
            case R.id.btn_fifth:
                myviewpager.setCurrentItem(4);
                break;
        }
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        Animation animation=null;
        switch (position){
            case 0:
                switch (currentIndex){
                    case 1:
                        animation=new TranslateAnimation(one,0,0,0);
                        break;
                    case 2:
                        animation=new TranslateAnimation(one*2,0,0,0);
                        break;
                    case 3:
                        animation=new TranslateAnimation(one*3,0,0,0);
                        break;
                    case 4:
                        animation=new TranslateAnimation(one*4,0,0,0);
                }
                break;
            case 1:
                switch (currentIndex){
                    case 0:
                        animation=new TranslateAnimation(0,one,0,0);
                        break;
                    case 2:
                        animation=new TranslateAnimation(one*2,one,0,0);
                        break;
                    case 3:
                        animation=new TranslateAnimation(one*3,one,0,0);
                        break;
                    case 4:
                        animation=new TranslateAnimation(one*4,one,0,0);
                }
                break;
            case 2:
                switch (currentIndex){
                    case 0:
                        animation=new TranslateAnimation(0,one*2,0,0);
                        break;
                    case 1:
                        animation=new TranslateAnimation(one,one*2,0,0);
                        break;
                    case 3:
                        animation=new TranslateAnimation(one*3,one*2,0,0);
                        break;
                    case 4:
                        animation=new TranslateAnimation(one*4,one*2,0,0);
                }
                break;
            case 3:
                switch (currentIndex){
                    case 0:
                        animation=new TranslateAnimation(0,one*3,0,0);
                        break;
                    case 1:
                        animation=new TranslateAnimation(one,one*3,0,0);
                        break;
                    case 2:
                        animation=new TranslateAnimation(one*2,one*3,0,0);
                        break;
                    case 4:
                        animation=new TranslateAnimation(one*4,one*3,0,0);
                }
                break;
            case 4:
                switch (currentIndex){
                    case 0:
                        animation=new TranslateAnimation(0,one*4,0,0);
                        break;
                    case 1:
                        animation=new TranslateAnimation(one,one*4,0,0);
                        break;
                    case 2:
                        animation=new TranslateAnimation(one*2,one*4,0,0);
                        break;
                    case 3:
                        animation=new TranslateAnimation(one*3,one*4,0,0);
                }
                break;
        }

        currentIndex=position;
        animation.setFillAfter(true);
        animation.setDuration(300);
        imageView.startAnimation(animation);
        //每次滑动首先重置所有按钮的颜色
        resetButtonColor();
        btnArgs[position].setTextColor(Color.WHITE);
        //cursorAnim(position);
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    public void initCursorPos(){
        imageView= (ImageView) findViewById(R.id.cursor);
        tranW= BitmapFactory.decodeResource(getResources(),R.drawable.tran).getWidth();

        DisplayMetrics displayMetrics=new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int screenW=displayMetrics.widthPixels;
        offset=(screenW/btnArgs.length-tranW)/2;
        one=offset*2+tranW;

        Matrix matrix=new Matrix();
        matrix.postTranslate(offset,0);
        imageView.setImageMatrix(matrix);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        return true;
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        initBoomButton();
    }
}
