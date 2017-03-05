package com.tongxin.youni.activity;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVFile;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.SaveCallback;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.tongxin.youni.R;
import com.tongxin.youni.bean.User;
import com.tongxin.youni.bean.UserDao;
import com.tongxin.youni.picker.DormitoryPopWindow2;
import com.tongxin.youni.utils.MyToast;
import com.yuyh.library.imgsel.ImageLoader;
import com.yuyh.library.imgsel.ImgSelActivity;
import com.yuyh.library.imgsel.ImgSelConfig;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

import jp.wasabeef.glide.transformations.CropCircleTransformation;

/**
 * Created by charlene on 2017/2/28.
 */

public class UserCenterActivity extends AppCompatActivity implements View.OnClickListener, DormitoryPopWindow2.DormitoryListener {
    private static final String TAG="UserCenterActivity";
    public static final int GO_ASK=0x1000;
    public static final int GO_FETCH=0x1001;
    public static final int GO_DORMITORY_NUMBER=0x1002;
    public static final int GO_PHONE_NUMBER=0x1003;
    public static final int GO_STU_NUMBER=0x1004;
    public static final int GO_MODIFY_PASSWORD=0x1005;
    public static final int GO_EDIT_USER_NAME=0x1006;
    public static final int GO_SEX=0x1007;
    public static final int CHANGE_HEAD=0x1010;
    public static final int MODIFT_INFO=0x1011;
    private boolean isEditMode=false;
    private Context context;
    private LayoutInflater inflater;
    private View root_view;

    private String HeadPath;
    private String headImageUrl;

    private ImageView Header;
    private ImageButton gotoAsk;
    private ImageButton gotoFetch;
    private Button btnLogOut;
    private Button btnModifyPassword;
    private Button btnChangeHead;
    private TextView tvPhoneNumber;
    private TextView tvDormitory;
    private TextView tvStudentNumber;
    private TextView tvSex;
    private TextView tvUserName;
    private TextView tvEditInfo;
    private TextView tvCancel;
    private TextView tvEditUserName;
    private CardView card;

    private User currentUser;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_center);
        context=this;
        inflater=LayoutInflater.from(this);
        root_view=inflater.inflate(R.layout.activity_user_center,null);
        currentUser= AVUser.getCurrentUser(User.class);
        initViews();
    }

    private void initViews() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        toolbar.setNavigationIcon(R.drawable.back);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                Log.i(TAG,"Back from UserInfoActivity.");
            }
        });

        Header= (ImageView) findViewById(R.id.header);
        gotoAsk= (ImageButton) findViewById(R.id.goto_ask);
        gotoFetch= (ImageButton) findViewById(R.id.goto_fetch);
        btnLogOut= (Button) findViewById(R.id.log_out);
        btnModifyPassword= (Button) findViewById(R.id.modify_password);
        btnChangeHead= (Button) findViewById(R.id.change_head);
        tvPhoneNumber= (TextView) findViewById(R.id.phone_number);
        tvStudentNumber= (TextView) findViewById(R.id.student_number);
        tvDormitory= (TextView) findViewById(R.id.dormitory_number);
        tvSex= (TextView) findViewById(R.id.sex);
        tvUserName= (TextView) findViewById(R.id.user_name);
        tvEditUserName= (TextView) findViewById(R.id.edit_user_name);
        tvCancel= (TextView) findViewById(R.id.cancel);
        tvEditInfo= (TextView) findViewById(R.id.edit_info);
        card= (CardView) findViewById(R.id.edit_card);

        gotoAsk.setOnClickListener(this);
        gotoFetch.setOnClickListener(this);
        btnLogOut.setOnClickListener(this);
        btnModifyPassword.setOnClickListener(this);
        btnChangeHead.setOnClickListener(this);
        tvEditInfo.setOnClickListener(this);
        tvCancel.setOnClickListener(this);
        tvSex.setOnClickListener(this);
        tvPhoneNumber.setOnClickListener(this);
        tvDormitory.setOnClickListener(this);
        tvStudentNumber.setOnClickListener(this);
        tvEditUserName.setOnClickListener(this);
        tvEditUserName.setClickable(false);
        tvSex.setClickable(false);
        tvPhoneNumber.setClickable(false);
        tvDormitory.setClickable(false);
        tvStudentNumber.setClickable(false);

        if (currentUser!=null){
            Glide.with(this)
                    .load(currentUser.getAvatar())
                    .crossFade()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .placeholder(R.drawable.default_header)
                    .bitmapTransform(new CropCircleTransformation(this))
                    .into(Header);
            tvPhoneNumber.setText(currentUser.getMobilePhoneNumber());
            tvDormitory.setText(currentUser.getRoomID());
            tvStudentNumber.setText(currentUser.getStudentID());
            tvUserName.setText(currentUser.getUsername());
            tvEditUserName.setText(currentUser.getUsername());
            if (currentUser.getSex()!=null){
                switch (currentUser.getSex()){
                    case "boy":
                        tvSex.setText("男孩");
                        break;
                    case "girl":
                        tvSex.setText("女孩");
                        break;
                    default:
                        tvSex.setText("其他");
                }
            }
        }else{
            MyToast.showToast(this,"网络异常!!");
            Log.e(TAG,"CurrentUser is null!!");
        }
    }

    @Override
    public void onClick(View view) {
        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        AlertDialog dialog;
        Intent intent;
        switch (view.getId()){
            case R.id.change_head:
                PickImage();//挑选图片跳转activity
                break;
            case R.id.goto_ask:
                intent=new Intent(UserCenterActivity.this,MyExpressActivity.class);
                intent.putExtra("tag",GO_ASK);
                startActivity(intent);
                break;
            case R.id.goto_fetch:
                intent=new Intent(UserCenterActivity.this,MyExpressActivity.class);
                intent.putExtra("tag",GO_FETCH);
                startActivity(intent);
                break;
            case R.id.cancel:
                CancelChange();
                ExitEditMode();
                break;
            case R.id.edit_info:
                if (isEditMode){
                    SaveChange();
                    ExitEditMode();
                }else{
                    EnterEditMode();
                }
                break;
            case R.id.edit_user_name:
                View root1=inflater.inflate(R.layout.modify_user_name_stu_number,null);
                final EditText editText1= (EditText) root1.findViewById(R.id.text_view);
                builder.setView(root1);
                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if ("".equals(editText1.getText())){
                            MyToast.showToast(context,"用户名不能为空!");
                        }else{
                            tvEditUserName.setText(editText1.getText());
                        }
                    }
                });
                builder.setTitle("修改用户名");
                dialog=builder.create();
                dialog.show();
                break;
            case R.id.dormitory_number:
                changeDormitoryNumber();
                break;
            case R.id.student_number:
                View root2=inflater.inflate(R.layout.modify_user_name_stu_number,null);
                final EditText editText2= (EditText) root2.findViewById(R.id.text_view);
                builder.setView(root2);
                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if ("".equals(editText2.getText())){
                            MyToast.showToast(context,"学生号不能为空!");
                        }else{
                            tvStudentNumber.setText(editText2.getText());
                        }
                    }
                });
                builder.setTitle("修改学生号");
                dialog=builder.create();
                dialog.show();
                break;
            case R.id.sex:
                View root3=inflater.inflate(R.layout.modify_sex,null);
                builder.setView(root3);
                final RadioButton r_boy,r_girl,r_either;
                r_boy= (RadioButton) root3.findViewById(R.id.boy);
                r_girl= (RadioButton) root3.findViewById(R.id.girl);
                r_either= (RadioButton) root3.findViewById(R.id.either);
                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (r_boy.isChecked()){
                            tvSex.setText("男孩");
                        }else if (r_girl.isChecked()){
                            tvSex.setText("女孩");
                        }else if(r_either.isChecked()){
                            tvSex.setText("其他");
                        }else{
                            MyToast.showToast(context,"请选择性别");
                        }
                    }
                });
                builder.setTitle("修改性别");
                dialog=builder.create();
                dialog.show();
                break;
            case R.id.phone_number:
                intent=new Intent(UserCenterActivity.this,MyExpressActivity.class);
                intent.putExtra("tag",GO_PHONE_NUMBER);
                startActivity(intent);
                break;
            case R.id.modify_password:
                intent=new Intent(UserCenterActivity.this,MyExpressActivity.class);
                intent.putExtra("tag",GO_MODIFY_PASSWORD);
                startActivity(intent);
                break;
            case R.id.log_out:
                AVUser.logOut();
                intent = new Intent(UserCenterActivity.this,LoginActivity.class);
                startActivity(intent);
                MainActivity.activity.finish();
                finish();
                break;
        }
    }

    private void SaveChange() {
        tvUserName.setText(tvEditUserName.getText());
        currentUser.setUsername(tvEditUserName.getText().toString().trim());
        if (tvSex.getText().equals("男孩")){
            currentUser.setSex("boy");
        }else if(tvSex.getText().equals("女孩")){
            currentUser.setSex("girl");
        }else{
            currentUser.setSex("either");
        }
        currentUser.setMobilePhoneNumber(tvPhoneNumber.getText().toString().trim());
        currentUser.setStudentID(tvStudentNumber.getText().toString().trim());
        currentUser.setRoomID(tvDormitory.getText().toString().trim());
        currentUser.saveInBackground(new SaveCallback() {
            @Override
            public void done(AVException e) {
                if (e==null)
                    MyToast.showToast(context,"保存成功");
                else
                    MyToast.showToast(context,"Error:"+e);
            }
        });
    }
    private void CancelChange(){
        tvEditUserName.setText(currentUser.getUsername());
        tvStudentNumber.setText(currentUser.getStudentID());
        if (currentUser.getSex().equals("boy")){
            tvSex.setText("男孩");
        }else if(currentUser.getSex().equals("girl")){
            tvSex.setText("女孩");
        }else{
            tvSex.setText("其他");
        }
        tvPhoneNumber.setText(currentUser.getMobilePhoneNumber());
        tvDormitory.setText(currentUser.getRoomID());
    }

    private void ExitEditMode() {
        ObjectAnimator animator=ObjectAnimator.ofFloat(card,"Elevation",50f,5f);
        animator.setDuration(100);
        animator.start();
        tvCancel.setVisibility(View.GONE);
        tvEditInfo.setText("编辑");
        tvEditUserName.setClickable(false);
        tvSex.setClickable(false);
        tvPhoneNumber.setClickable(false);
        tvDormitory.setClickable(false);
        tvStudentNumber.setClickable(false);
        isEditMode=false;
    }

    private void EnterEditMode() {
        ObjectAnimator animator=ObjectAnimator.ofFloat(card,"Elevation",5f,50f);
        animator.setDuration(100);
        animator.start();
        tvCancel.setVisibility(View.VISIBLE);
        tvEditInfo.setText("确定");
        tvEditUserName.setClickable(true);
        tvSex.setClickable(true);
        tvPhoneNumber.setClickable(true);
        tvDormitory.setClickable(true);
        tvStudentNumber.setClickable(true);
        isEditMode=true;
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

        ImgSelConfig config = new ImgSelConfig.Builder(loader)
                // 是否多选
                .multiSelect(false)
                // “确定”按钮背景色
                .btnBgColor(0x30be9e)
                // “确定”按钮文字颜色
                .btnTextColor(Color.WHITE)
                // 使用沉浸式状态栏
                .statusBarColor(Color.parseColor("#30be9e"))
                // 返回图标ResId
                .backResId(R.mipmap.back_green)
                //标题
                .title("选择图片")
                // 标题文字颜色
                .titleColor(Color.WHITE)
                // TitleBar背景色
                .titleBgColor(Color.parseColor("#30be9e"))
                // 裁剪大小。needCrop为true的时候配置
                .cropSize(1, 1, 200, 200)
                .needCrop(true)
                // 第一个是否显示相机
                .needCamera(true)
                // 最大选择图片数量
                .maxNum(1)
                .build();
        //跳转到图片选择器
        ImgSelActivity.startActivity(UserCenterActivity.this,config,CHANGE_HEAD);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode==RESULT_OK){
            switch (requestCode){
                case CHANGE_HEAD:
                    ChangHead(data);
                    break;
                case GO_PHONE_NUMBER:
                    tvPhoneNumber.setText(data.getStringExtra("data"));
                    break;
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void ChangHead(Intent data) {
        if(data !=null){
            List<String> pathList = data.getStringArrayListExtra(ImgSelActivity.INTENT_RESULT);
            for (String path : pathList) {
                if(path!=null)
                    HeadPath = path;
                Log.i(TAG,path);
                if(!"".equals(HeadPath)) {
                    //currentUser.put("userName",tvResult);
                    //Toast.makeText(ChangeInformation.this,tvResult,Toast.LENGTH_SHORT).show();
                    //加载选择图片
                    Glide.with(this).load(HeadPath).into(Header);
                    //更换头像
                    try {
                        final AVFile file  = AVFile.withFile("avatar.png",new File((new URI("file:"+HeadPath))));
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
                                    Log.i(TAG,"保存成功");
                                }
                                else
                                    Log.i(TAG,e.toString());
                            }
                        });
                    }
                    catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                    catch (URISyntaxException e) {
                        e.printStackTrace();
                    }
                } else
                    MyToast.showToast(UserCenterActivity.this,"未选择图片");
            }
        }

    }

    //点击更改宿舍号
    public void changeDormitoryNumber() {
//        CityPickerPopWindow mPopWindow = new CityPickerPopWindow(mContext);
//        mPopWindow.showPopupWindow(rootView);
//        mPopWindow.setCityPickListener(this);

//        DormitoryPopWindow mPopWindow= new DormitoryPopWindow(mContext);
//        mPopWindow.showPopupWindow(rootView);
//        mPopWindow.setDormitoryListener(this);

        DormitoryPopWindow2 mPopWindow= new DormitoryPopWindow2(this);
        mPopWindow.showPopupWindow(root_view);
        mPopWindow.setDormitoryListener(this);
    }

    @Override
    public void pickValue2(String value) {
        if (!"".equals(value)){
            tvDormitory.setText(value);
        }
    }
}
