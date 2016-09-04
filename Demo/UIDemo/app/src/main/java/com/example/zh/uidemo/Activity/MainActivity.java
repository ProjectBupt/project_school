package com.example.zh.uidemo.Activity;

import android.app.FragmentTransaction;
import android.graphics.drawable.Drawable;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.zh.uidemo.R;
import com.example.zh.uidemo.fragment.FragmentGroup;
import com.example.zh.uidemo.fragment.FragmentHomePage;
import com.example.zh.uidemo.fragment.FragmentMe;
import com.example.zh.uidemo.fragment.FragmentSettings;
import com.nightonke.boommenu.BoomMenuButton;
import com.nightonke.boommenu.Types.BoomType;
import com.nightonke.boommenu.Types.ButtonType;
import com.nightonke.boommenu.Types.PlaceType;
import com.nightonke.boommenu.Util;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private Toolbar toolbar;
    private Button HomePage;
    private Button Group;
    private Button Me;
    private Button Settings;
    private BoomMenuButton boomMenuButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initToolBar();
        initView();
        initBoomMenuButton();
    }

    private void initToolBar() {
        toolbar= (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()){
                }
                return true;
            }
        });
    }

    private void initBoomMenuButton() {
        boomMenuButton= (BoomMenuButton) findViewById(R.id.boom_button);
        BoomMenuButton.Builder builder=new BoomMenuButton.Builder();
        Drawable[] subButtonDrawables = new Drawable[6];
        int[] drawablesResource = new int[]{
                R.drawable.zero,
                R.drawable.one,
                R.drawable.two,
                R.drawable.three,
                R.drawable.four,
                R.drawable.six
        };
        for (int i = 0; i < drawablesResource.length; i++)
            subButtonDrawables[i] = ContextCompat.getDrawable(this, drawablesResource[i]);

        String[] subButtonTexts = new String[]{"", "", "","","",""};
        int[][] subButtonColors = new int[6][2];
        for (int i = 0; i < drawablesResource.length; i++) {
            subButtonColors[i][1] = ContextCompat.getColor(this, R.color.white);
            subButtonColors[i][0] = Util.getInstance().getPressedColor(subButtonColors[i][1]);
        }
//        boomMenuButton.init(subButtonDrawables,
//                subButtonTexts,
//                subButtonColors,
//                ButtonType.CIRCLE,     // 子按钮的类型
//                BoomType.PARABOLA,  // 爆炸类型
//                PlaceType.CIRCLE_6_3,  // 排列类型
//                null,               // 展开时子按钮移动的缓动函数类型
//                null,               // 展开时子按钮放大的缓动函数类型
//                null,               // 展开时子按钮旋转的缓动函数类型
//                null,               // 隐藏时子按钮移动的缓动函数类型
//                null,               // 隐藏时子按钮缩小的缓动函数类型
//                null,               // 隐藏时子按钮旋转的缓动函数类型
//                null  );
        builder.subButtons(subButtonDrawables,subButtonColors,subButtonTexts)
                .button(ButtonType.CIRCLE)
                .place(PlaceType.CIRCLE_6_3)
                .boom(BoomType.PARABOLA)
                .onSubButtonClick(new BoomMenuButton.OnSubButtonClickListener() {
                        @Override
                        public void onClick(int buttonIndex) {
                            switch(buttonIndex){
                                case 0:
                                    Toast.makeText(MainActivity.this, "发布活动", Toast.LENGTH_SHORT).show();
                                    break;
                                case 1:
                                    Toast.makeText(MainActivity.this, "借东西", Toast.LENGTH_SHORT).show();
                                    break;
                                case 2:
                                    Toast.makeText(MainActivity.this, "找人取快递", Toast.LENGTH_SHORT).show();
                                    break;
                                case 3:
                                    Toast.makeText(MainActivity.this, "失物招领", Toast.LENGTH_SHORT).show();
                                    break;
                                case 4:
                                    Toast.makeText(MainActivity.this, "二手交易", Toast.LENGTH_SHORT).show();
                                    break;
                                case 6:
                                    Toast.makeText(MainActivity.this, "拼团凑单", Toast.LENGTH_SHORT).show();
                                    break;
                            }
                        }
                    })
                .boomButtonShadow(Util.getInstance().dp2px(0), Util.getInstance().dp2px(0))
                .subButtonsShadow(Util.getInstance().dp2px(0), Util.getInstance().dp2px(0))
                .init(boomMenuButton);
    }

    private void initView() {
        HomePage= (Button) findViewById(R.id.btn_home_page);
        Group= (Button) findViewById(R.id.btn_group);
        Me= (Button) findViewById(R.id.btn_me);
        Settings= (Button) findViewById(R.id.btn_settings);

        HomePage.setOnClickListener(this);
        Group.setOnClickListener(this);
        Me.setOnClickListener(this);
        Settings.setOnClickListener(this);

        FragmentManager fragmentManager=getSupportFragmentManager();
        android.support.v4.app.FragmentTransaction transaction=fragmentManager.beginTransaction();
        transaction.replace(R.id.container,new FragmentHomePage());
        transaction.commit();
    }


    public void onClick(View view) {
        FragmentManager fragmentManager=getSupportFragmentManager();
        android.support.v4.app.FragmentTransaction transaction=fragmentManager.beginTransaction();
        switch (view.getId()) {
            case R.id.btn_home_page:
                transaction.replace(R.id.container,new FragmentHomePage());
                break;
            case R.id.btn_group:
                transaction.replace(R.id.container,new FragmentGroup());
                break;
            case R.id.btn_me:
                transaction.replace(R.id.container,new FragmentMe());
                break;
            case R.id.btn_settings:
                transaction.replace(R.id.container,new FragmentSettings());
                break;

        }
        transaction.commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menu,menu);
        return true;
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        initBoomMenuButton();
    }
}
