package com.example.yangxiang.testhuan.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVFile;
import com.avos.avoscloud.SaveCallback;
import com.bumptech.glide.Glide;
import com.diycoder.citypick.widget.CityPickerPopWindow;
import com.example.yangxiang.testhuan.R;
import com.example.yangxiang.testhuan.picker.DormitoryPopWindow;
import com.example.yangxiang.testhuan.picker.DormitoryPopWindow2;
import com.example.yangxiang.testhuan.widget.TitleBar;
import com.example.yangxiang.testhuan.bean.User;
import com.example.yangxiang.testhuan.bean.UserDao;
import com.yuyh.library.imgsel.ImageLoader;
import com.yuyh.library.imgsel.ImgSelActivity;
import com.yuyh.library.imgsel.ImgSelConfig;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

/**
 * Created by 宽伟 on 2016/10/2.
 * 此页面为更改信息和完善信息界面
 * 当用户初次注册时可以选择是否完善信息
 * 若不完善，等到用户选择发送或代领别人的快递时
 * 弹出此页面，要求用户完善信息
 *
 * 用户也可通过此页面来更改信息
 * 目前支持更爱的信息为
 * 头像
 * 姓名
 * 电话
 * 宿舍
 * 默认收货地址
 */
//private String nick;//用户名
//private String avatar;//头像
//private String mCampus;//校区
//private String sex;//性别
//private String roomID;//宿舍号
//private String studentID;//学号
//private String InstallationId;//推送id

public class ChangeInformation extends Activity implements CityPickerPopWindow.CityPickListener,
        DormitoryPopWindow.DormitoryListener ,DormitoryPopWindow2.DormitoryListener{
    private EditText name;
    private EditText telNumber;
    private TextView dormitoryNumber;//宿舍号
    private EditText defaultAddress;//默认收货地址，默认为宿舍
    private Spinner sex;//选择性别
    private String sexString;//表示性别
    private int sexInt;//表示性别储存到服务器上，0代表男生，1代表女生
    private String headImageUrl; //保存头像的的地址

    private Button changeHead;
    private String tvResult = "";//传来的图像地址

    private User currentUser = User.getCurrentUser(User.class);
    private ImageView imageView;

    //弹出宿舍选择器
    private Activity mContext;
    private LinearLayout rootView;

    private int doWhat=2;//表示此页面是用来完善信息还是更改信息

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.change_information);

        Intent intent = getIntent();//接收上一个Activity传过来的值判断是修改信息还是完善信息
        if(intent!=null){
            Bundle bundle = intent.getExtras();
            if(bundle!=null)
                doWhat = bundle.getInt("doWhat");
        }
        mContext = this;
        init();
        initInformation();
        initTitleBar();//初始化标题栏

        //更改头像的监听事件
        changeHead  = (Button) findViewById(R.id.changeHead);
        changeHead.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PickImage();//挑选图片跳转activity
            }
        });
    }
    private void init() {
        rootView = (LinearLayout) findViewById(R.id.root_layout);
        name = (EditText) findViewById(R.id.name);
        telNumber = (EditText) findViewById(R.id.telNumber);
        dormitoryNumber = (TextView) findViewById(R.id.dormitoryNumber);
        defaultAddress = (EditText) findViewById(R.id.defaultAddress);
        imageView = (ImageView) findViewById(R.id.imageView);
        sex = (Spinner) findViewById(R.id.sex);
        sex.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if(i==0)
                    sexInt = 0;
                else
                    sexInt = 1;
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
    }
    //根据登录用户从服务器获取信息并显示到相应位置
    private void initInformation() {
        name.setText((String) currentUser.get(UserDao.NICK));
        telNumber.setText((String) currentUser.get("mobilePhoneNumber"));
        dormitoryNumber.setText((String) currentUser.get(UserDao.ROOM_ID));
        defaultAddress.setText((String) currentUser.get(UserDao.ADDRESS));

        sexInt = currentUser.getInt(UserDao.SEX);
        sex.setSelection(sexInt);

        //显示头像
        headImageUrl = (String) currentUser.get(UserDao.AVATARURL);
        if(!"".equals(headImageUrl)){
            Glide.with(this).load(headImageUrl).into(imageView);
            //更换头像
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(data !=null){
            List<String> pathList = data.getStringArrayListExtra(ImgSelActivity.INTENT_RESULT);
            for (String path : pathList) {
                //tvResult保存的为图片的地址
                if(path!=null)
                    tvResult = path;
                Log.i("path------------>",path);
                if(tvResult != "")
                {
                    //currentUser.put("userName",tvResult);
                    Toast.makeText(ChangeInformation.this,tvResult,Toast.LENGTH_SHORT).show();
                    //加载选择图片
                    Glide.with(this).load(tvResult).into(imageView);
                    //更换头像
                    try {
                        final AVFile file  = AVFile.withFile("avatar.png",new File((new URI("file:"+tvResult))));
                        file.saveInBackground(new SaveCallback() {
                            @Override
                            public void done(AVException e){
                                if(e==null)
                                {
                                    headImageUrl = currentUser.getAvatar();
                                    AVFile oldFile = new AVFile("avatar.png",headImageUrl,null);
                                    oldFile.deleteInBackground();
                                    headImageUrl = file.getUrl();
                                    currentUser.put(UserDao.AVATARURL,headImageUrl);
                                    currentUser.saveInBackground();
                                    Log.i("wrong","保存成功");
                                }
                                else
                                    Log.i("wrong",e.toString());
                            }
                        });
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (URISyntaxException e) {
                        e.printStackTrace();
                    }
                }
                else
                    Toast.makeText(ChangeInformation.this,"未选择图片",Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void initTitleBar(){
        boolean isImmersive = false;

        final TitleBar titleBar = (TitleBar) findViewById(R.id.titleBar2);

        titleBar.setImmersive(isImmersive);
        titleBar.setBackgroundColor(Color.parseColor("#011320"));

        titleBar.setLeftImageResource(R.mipmap.back_green);
        titleBar.setLeftText("返回");
        titleBar.setLeftTextColor(Color.WHITE);
        titleBar.setLeftClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        if(doWhat == 1){
            //完善信息
            titleBar.setTitle("完善信息");
        }
        else if(doWhat == 2){
            //修改信息
            titleBar.setTitle("修改信息");
        }
        titleBar.setTitleColor(Color.WHITE);
        titleBar.setSubTitleColor(Color.WHITE);
        titleBar.setDividerColor(Color.GRAY);
        titleBar.setActionTextColor(Color.WHITE);
        //title收藏按钮
//        mCollectView = (ImageView) titleBar.addAction(new TitleBar.ImageAction(R.mipmap.collect) {
//            @Override
//            public void performAction(View view) {
//                Toast.makeText(MainActivity.this, "点击了收藏", Toast.LENGTH_SHORT).show();
//                mCollectView.setImageResource(R.mipmap.fabu);
//                titleBar.setTitle(mIsSelected ? "文章详情\n朋友圈" : "帖子详情");
//                mIsSelected = !mIsSelected;
//            }
//        });
        titleBar.addAction(new TitleBar.TextAction("确定") {
            @Override
            public void performAction(View iew) {

                if(dataIsOk()){
                    //保存各项数据
                    if(!name.getText().toString().trim().equals("")){
                        currentUser.put(UserDao.NICK , name.getText().toString().trim());//姓名
                    }
                    if(!telNumber.getText().toString().trim().equals("")){
                        currentUser.put("phoneNumber",telNumber.getText().toString().trim());//电话号
                    }
                    if(!dormitoryNumber.getText().toString().trim().equals("")){
                        currentUser.put(UserDao.ROOM_ID , dormitoryNumber.getText().toString().trim());//宿舍号
                    }
                    currentUser.put(UserDao.SEX,sexInt);//性别
                    if(defaultAddress.getText().toString().trim().equals("")){
                        currentUser.put(UserDao.ADDRESS , dormitoryNumber.getText().toString().trim());
                    }
                    currentUser.saveInBackground(new SaveCallback(){
                        @Override
                        public void done(AVException e){
                            if(e==null){
                                Toast.makeText(ChangeInformation.this,"保存信息成功",Toast.LENGTH_SHORT).show();
                                finish();
                            }
                            else{
                                Toast.makeText(ChangeInformation.this,"保存信息失败",Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });
    }

    private boolean dataIsOk() {
        return true;
    }

    private void PickImage() {
        // 自定义图片加载器
        ImageLoader loader = new ImageLoader() {
            @Override
            public void displayImage(Context context, String path, ImageView imageView) {
                // TODO 在这边可以自定义图片加载库来加载ImageView，例如Glide、Picasso、ImageLoader等
                Glide.with(context).load(path).into(imageView);
            }
        };
        //自由配置选项
        ImgSelConfig config = new ImgSelConfig.Builder(loader)
                // 是否多选
                .multiSelect(false)
                // “确定”按钮背景色
                .btnBgColor(Color.GRAY)
                // “确定”按钮文字颜色
                .btnTextColor(Color.BLUE)
                // 使用沉浸式状态栏
                .statusBarColor(Color.parseColor("#3F51B5"))
                // 返回图标ResId
                .backResId(R.mipmap.back_green)
                // 标题
                .title("图片")
                // 标题文字颜色
                .titleColor(Color.WHITE)
                // TitleBar背景色
                .titleBgColor(Color.parseColor("#9932CC"))
                // 裁剪大小。needCrop为true的时候配置
                .cropSize(1, 1, 200, 200)
                .needCrop(true)
                // 第一个是否显示相机
                .needCamera(true)
                // 最大选择图片数量
                .maxNum(1)
                .build();
        //跳转到图片选择器
        ImgSelActivity.startActivity(ChangeInformation.this,config,3);
    }

    //接受选择的宿舍号
    @Override
    public void pickValue(String value) {
        dormitoryNumber.setText(value);
    }

    //点击更改宿舍号
    public void changeDormitoryNumber(View view) {
//        CityPickerPopWindow mPopWindow = new CityPickerPopWindow(mContext);
//        mPopWindow.showPopupWindow(rootView);
//        mPopWindow.setCityPickListener(this);

//        DormitoryPopWindow mPopWindow= new DormitoryPopWindow(mContext);
//        mPopWindow.showPopupWindow(rootView);
//        mPopWindow.setDormitoryListener(this);

        DormitoryPopWindow2 mPopWindow= new DormitoryPopWindow2(mContext);
        mPopWindow.showPopupWindow(rootView);
        mPopWindow.setDormitoryListener(this);
    }

    @Override
    public void pidkValue(String value) {
        dormitoryNumber.setText(value);
    }

    //接口函数获得滚轮传来的数据
    @Override
    public void pickValue2(String value) {
        dormitoryNumber.setText(value);
    }
}
