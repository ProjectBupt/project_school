package com.example.zh.uidemo.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.zh.uidemo.R;
import com.nightonke.boommenu.BoomMenuButton;

/**
 * Created by charlene on 2016/9/3.
 */
public class BottomFragment extends Fragment implements View.OnClickListener {

    private Button HomePage;
    private Button Group;
    private Button Me;
    private Button Settings;
    private BoomMenuButton boomMenuButton;

    private FragmentManager fragmentManager;
    private FragmentTransaction transaction;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root=inflater.inflate(R.layout.fragment_bottom,container,false);
        initView(root);
        initBoomMenuButton(root);
        return root;
    }

    private void initBoomMenuButton(View root) {
        boomMenuButton= (BoomMenuButton) root.findViewById(R.id.boom_button);
        BoomMenuButton.Builder builder=new BoomMenuButton.Builder();

    }

    private void initView(View root) {
        HomePage= (Button) root.findViewById(R.id.btn_home_page);
        Group= (Button) root.findViewById(R.id.btn_group);
        Me= (Button) root.findViewById(R.id.btn_me);
        Settings= (Button) root.findViewById(R.id.btn_settings);
        fragmentManager=getFragmentManager();
        transaction=fragmentManager.beginTransaction();

        HomePage.setOnClickListener(this);
        Group.setOnClickListener(this);
        Me.setOnClickListener(this);
        Settings.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_home_page:
                transaction.replace(R.id.container,new FragmentHomePage());
                Log.i("info","~~~");
                break;
            case R.id.btn_group:
                transaction.replace(R.id.container,new FragmentGroup());
                break;
            case R.id.btn_me:
                break;
            case R.id.btn_settings:
                break;

        }
    }
}

