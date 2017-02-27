package com.tongxin.youni.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVRelation;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.SaveCallback;
import com.tongxin.youni.R;
import com.tongxin.youni.bean.Express;
import com.tongxin.youni.bean.ExpressDao;
import com.tongxin.youni.bean.User;
import com.tongxin.youni.bean.UserDao;


import java.util.Arrays;

/**
 * Created by 宽伟 on 2016/9/27.
 */

/**
 * 此类为发送feed
 */

public class PostFeed extends Activity {
    /**
     * 表示要存储到服务器上的数据
     */
    private String expressCompanyForHold;//快递公司
    private String timeForHold="";//到货时间
    private int moneyForHold;//打赏金额
    private int isDormitory;
    private String typeThingForHold;//物品类型
    private String addressForHold;//收货地址，此值代表储存收货地址
    private String attentionTingForHold;//注意事项

    private String currentDormitory;//当前用户宿舍号，默认收货地址

    private String[] expressCompanyList ;

    private User ttUser;
    private Button submit;//发送
    private Button cancelPost;//取消发送

    private LinearLayout modifyAddress;

    private Spinner expressCompany;
    private RadioGroup timeOfArrive;
    private RadioGroup moneyOfReward;
    private RadioGroup typeOfThing;
    private RadioGroup getThingAddress;
    private ViewGroup.LayoutParams layoutParams;
    private EditText addressOfGetThing;//收货地址
    private EditText attentionThing;//注意事项

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.acticity_post_feed);
        //得到登录用户
        ttUser = AVUser.getCurrentUser(User.class);
        if(ttUser == null)
            Toast.makeText(PostFeed.this,"登录失败",Toast.LENGTH_SHORT).show();
        currentDormitory = (String) ttUser.get(UserDao.ROOM_ID);//得到宿舍号

        initId();//findViewById...
        initSpinner();//初始化选择快递公司Spinner控件
        initChooseData();//初始化各项要发布到服务器的数据
        initChoose();
        initBtn();//提交和取消
    }

    private void initChooseData() {
        //初始化到达时间，默认选中为中午
        timeForHold = "中午";
        //打赏金额
        moneyForHold = 1;
        //默认收货地址
        isDormitory = 1;
    }

    private void initSpinner() {
        expressCompanyList = getResources().getStringArray(R.array.expressCompany);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,R.layout.spinner_style_layout,expressCompanyList);
        expressCompany.setAdapter(adapter);
    }

    private void initId() {
        expressCompany = (Spinner) findViewById(R.id.id_expressCompany);
        timeOfArrive  = (RadioGroup) findViewById(R.id.id_time);
        moneyOfReward = (RadioGroup) findViewById(R.id.id_money);
        typeOfThing = (RadioGroup) findViewById(R.id.id_type);
        getThingAddress = (RadioGroup) findViewById(R.id.id_getThingAddress);
        modifyAddress = (LinearLayout) findViewById(R.id.modifyAddress);
        addressOfGetThing = (EditText) findViewById(R.id.id_getThingAddressOther);
        attentionThing = (EditText) findViewById(R.id.id_attentionThing);
    }

    private void initBtn() {
        cancelPost = (Button) findViewById(R.id.cancelPost);
        //取消发布
        cancelPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                //intent.putExtra("expressCompany",expressConpany);
                setResult(RESULT_CANCELED,intent);
                finish();
            }
        });

        submit= (Button) findViewById(R.id.submit);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //数据填写完整
                if(dataIsOk()){
                    preserveData();//保存数据
                }
            }
        });
    }

    private void preserveData(){
//        final AVObject feed = new AVObject("Express");

        //将AV
        final Express feed = new Express();
        feed.setState(ExpressDao.isWaiting);
        feed.setUserID(ttUser.getObjectId());
        feed.setExpressCompany(expressCompanyForHold);
        feed.setExtra(attentionTingForHold);
        feed.setRoomID(addressForHold);
        feed.setTime(timeForHold);
        feed.setMoney(moneyForHold);
        feed.setPhone(ttUser.getMobilePhoneNumber());
        feed.setType(typeThingForHold);
        feed.setUserName(ttUser.getUsername());

        Express.saveAllInBackground(Arrays.asList(feed),new SaveCallback() {
            @Override
            public void done(AVException e) {
                AVRelation<AVObject> relation = ttUser.getRelation("add");
                relation.add(feed);

                ttUser.saveInBackground(new  SaveCallback() {
                    @Override
                    public void done(AVException e) {
                        if(e==null){
                            Toast.makeText(PostFeed.this,"保存成功",Toast.LENGTH_SHORT).show();
                            setResult(RESULT_OK);
                            finish();
                        }
                        else{
                            Toast.makeText(PostFeed.this,"保存失败",Toast.LENGTH_SHORT).show();
                            finish();
                            Log.i("wrong-------->",e.toString());
                        }
                    }
                });
            }
        });

    }

    //函数判断快递信息是否填写完整
    private boolean dataIsOk() {
        attentionTingForHold = attentionThing.getText().toString().trim();
        //得到收货地址
        if(isDormitory==1){
            addressForHold = currentDormitory;
        }
        else{
            addressForHold = addressOfGetThing.getText().toString().trim();
        }

        if(expressCompanyForHold==null||expressCompanyForHold.equals("")){
            Toast.makeText(PostFeed.this,"请选择快递公司",Toast.LENGTH_SHORT).show();
            return false;
        }
        if(isDormitory==0&&addressForHold.equals("")) {
            Toast.makeText(PostFeed.this,"请填写收获地址",Toast.LENGTH_SHORT).show();
            return false;
        }
        if(typeThingForHold==null||typeThingForHold.equals("")){
            Toast.makeText(PostFeed.this,"请选择物品类型",Toast.LENGTH_SHORT).show();
            return false;
        }
        if(timeForHold==null||timeForHold.equals("")){
            Toast.makeText(PostFeed.this,"请选择物品到达时间",Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void initChoose() {

        //快递公司选择
        expressCompany.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                expressCompanyForHold = expressCompanyList[i];
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
        //到货时间选择
        timeOfArrive.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                if(i==R.id.id_time_noon)
                    timeForHold = "中午";
                else
                    timeForHold = "下午";
            }
        });
        //打赏金额选择
        moneyOfReward.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                switch(i){
                    case R.id.id_zero:
                        moneyForHold = 0;
                        break;
                    case R.id.id_one:
                        moneyForHold = 1;
                        break;
                    case R.id.id_two:
                        moneyForHold = 2;
                        break;
                    case R.id.id_three:
                        moneyForHold = 3;
                        break;
                    case R.id.id_five:
                        moneyForHold = 5;
                        break;
                }
            }
        });
        //快递物品种类选择
        typeOfThing.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                switch (i){
                    case R.id.id_clothes:
                        typeThingForHold = "clothes";
                        break;
                    case R.id.id_electronics:
                        typeThingForHold = "electronics";
                        break;
                    case R.id.id_life:
                        typeThingForHold = "life";
                        break;
                    case R.id.id_others:
                        typeThingForHold = "others";
                        break;
                }
            }
        });

        //收货地址选择
        layoutParams=modifyAddress.getLayoutParams();
        getThingAddress.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {

                if(i==R.id.id_no){
                    //弹出要填写注意事项的框框
                     addressForHold = "";
                     isDormitory = 0;
                     layoutParams.height = 160;
                     modifyAddress.setLayoutParams(layoutParams);
                }
                else{
                    layoutParams.height = 0;
                    modifyAddress.setLayoutParams(layoutParams);
                    isDormitory=1;
                }
            }
        });

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()){
            case MotionEvent.ACTION_UP:
                finish();
                break;
        }
        return true;
    }

    //
    public void in(View view) {
    }
}
