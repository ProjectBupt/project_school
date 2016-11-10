package com.tongxin.youni.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Spinner;
import com.tongxin.youni.R;

/**
 * Created by 宽伟 on 2016/9/25.
 *
 * 此activity 为筛选界面
 * 当用户需要筛选时，点击弹出页面
 * 并向此界面传递数据
 * 代表以选择中的数据
 */
public class Screen extends Activity {
    private Button cancel;
    private Button sureScreen;
    private String Building;
    private int BuildingType;//用来表示那选择的那栋楼传递给MainActivity
    private int ExpressType;//表示传过来的快递公司
    private String expressCompany;
    private Spinner BuildingSpinner;
    private Spinner ExpressSpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.screen_layout);

        initSpinner();
        initBtn();
    }

    private void initSpinner() {

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        if(bundle!=null){
            BuildingType = bundle.getInt("defaultBuilding");
            ExpressType = bundle.getInt("defaultExpress");
        }
        else{
            BuildingType = 0;
            ExpressType = 0;
        }

        ExpressSpinner = (Spinner) findViewById(R.id.expressCompanyChoose);
        BuildingSpinner = (Spinner) findViewById(R.id.BuildingChoose);

        ExpressSpinner.setSelection(ExpressType);//
        BuildingSpinner.setSelection(BuildingType);//设置Spinner的默认选项

        ExpressSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                ExpressType = i;
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        BuildingSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                BuildingType = i;
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
    }

    private void initBtn() {
        cancel = (Button) findViewById(R.id.cancelScreen);
        sureScreen = (Button) findViewById(R.id.sureScreen);

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        sureScreen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Toast.makeText(Screen.this,Building,Toast.LENGTH_SHORT).show();
                Intent intent = new Intent();
                intent.putExtra("Building",BuildingType);
                intent.putExtra("Express",ExpressType);
                //intent.putExtra("expressCompany",expressCompany);
                setResult(RESULT_OK,intent);
                finish();
            }
        });
    }
}
