package com.example.zh.uidemo.Activity;

import android.app.FragmentTransaction;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.example.zh.uidemo.R;
import com.example.zh.uidemo.fragment.FragmentGroup;
import com.example.zh.uidemo.fragment.FragmentHomePage;
import com.example.zh.uidemo.fragment.FragmentMe;
import com.example.zh.uidemo.fragment.FragmentSettings;
import com.nightonke.boommenu.BoomMenuButton;

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
