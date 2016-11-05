package com.tongxin.youni.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import com.avos.avoscloud.AVUser;
import com.tongxin.youni.R;
import com.tongxin.youni.bean.Express;
import com.tongxin.youni.bean.ExpressDao;
import com.tongxin.youni.receiver.NetStateReceiver;
import com.tongxin.youni.bean.User;

public class PostActivity extends AppCompatActivity {

    private EditText mName;
    private EditText mPhone;
    private EditText mRoomID;
    private EditText mExtra;
    private Spinner mTime;
    private Spinner mExpressCompany;
    private RadioGroup mSizeGroup;
    private RadioGroup isFragileGroup;

    private String mCompany;
    private boolean mFragile;
    private int mSize;
    private User mUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);
        initView();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        initListener();
        setDefaultContent();
    }

    private void setDefaultContent() {
        mName.setText(mUser.getUsername());
        mRoomID.setText(mUser.getRoomID());
        mPhone.setText(mUser.getMobilePhoneNumber().substring(7));
    }

    private void initListener() {
        mExpressCompany.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String[] com = getResources().getStringArray(R.array.express_company);
                mCompany = com[position];
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        isFragileGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                RadioButton bt = (RadioButton) findViewById(checkedId);
                assert bt != null;
                String s = bt.getText().toString();
                if(getResources().getString(R.string.express_isfragile_yes).equals(s)){
                    mFragile = true;
                }
                else{
                    mFragile = false;
                }
            }
        });

        mSizeGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                RadioButton bt = (RadioButton) findViewById(checkedId);
                assert bt != null;
                String s = bt.getText().toString();
                if(getResources().getString(R.string.express_size_big).equals(s)){
                    mSize = ExpressDao.big;
                }
                else if(getResources().getString(R.string.express_size_mid).equals(s)){
                    mSize = ExpressDao.midden;
                }
                else if(getResources().getString(R.string.express_size_small).equals(s)){
                    mSize = ExpressDao.small;
                }
            }
        });
    }

    private void initView() {
        mUser = User.getCurrentUser(User.class);

        mName = (EditText) findViewById(R.id.name);
        mPhone = (EditText) findViewById(R.id.phone);
        mRoomID = (EditText) findViewById(R.id.room);
        mExtra = (EditText) findViewById(R.id.extra);
        mExpressCompany = (Spinner) findViewById(R.id.express_company);
        mSizeGroup = (RadioGroup) findViewById(R.id.express_size);
        isFragileGroup = (RadioGroup) findViewById(R.id.is_fragile);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter
                .createFromResource(this,R.array.express_company,android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mExpressCompany.setAdapter(adapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
         getMenuInflater().inflate(R.menu.post_main,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id){
            case R.id.btn_ok:
                if(NetStateReceiver.isConnected()){
                    Intent intent = new Intent();
                    CreateNewExpress();
                    setResult(RESULT_OK,intent);
                    finish();
                }
                else {
                    Toast.makeText(this, "network not available", Toast.LENGTH_SHORT).show();
                }
                return true;
        }
        return false;
    }

    private void CreateNewExpress() {
        String name = mName.getText().toString();
        String room= mRoomID.getText().toString();
        String phone = mPhone.getText().toString();
        String Extra = mExtra.getText().toString();
        String install = mUser.getInstallationId();
        String userID = AVUser.getCurrentUser(User.class).getObjectId();
        Express ex = new Express(userID,install,name,mCompany,room,phone,Extra,mSize,mFragile);
        ex.saveInBackground();
    }
}
