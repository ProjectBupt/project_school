package com.tongxin.youni.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVInstallation;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVPush;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.AVRelation;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.GetCallback;
import com.avos.avoscloud.SendCallback;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.tongxin.youni.R;
import com.tongxin.youni.bean.Express;
import com.tongxin.youni.bean.User;
import com.tongxin.youni.bean.UserDao;

import jp.wasabeef.glide.transformations.CropCircleTransformation;

public class ItemDetailActivity extends AppCompatActivity {

    private static final String TAG = "ItemActivity----->";
    private TextView mInfo;
    private Button mChat;
    private Express mExpress;
    private String mUserID;
    private User mUser;
    private ImageView avatar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_detail);

        mInfo = (TextView) findViewById(R.id.info);
        mChat = (Button) findViewById(R.id.chat);
        avatar = (ImageView) findViewById(R.id.avatar);

        String ID = getIntent().getStringExtra("ExpressID");

        AVQuery<Express> query = new AVQuery<>("Express");
        query.getInBackground(ID, new GetCallback<Express>() {
            @Override
            public void done(Express express, AVException e) {
                if(e == null) {
                    mExpress = express;
                    Log.i(TAG, "done: "+express.getState());
                    final String info = "收货人："+mExpress.getUserName()+"\n"
                            +"快递公司："+mExpress.getExpressCompany()+"\n"
                            +"收货地址："+mExpress.getRoomID()+"\n"
                            +"手机号码："+mExpress.getPhone()+"\n"
                            +"打赏金额："+mExpress.getMoney()+"\n"
                            +"附加信息："+mExpress.getExtra();
                    mExpress.setDeliverID(User.getCurrentUser(User.class).getObjectId());
                    mExpress.saveInBackground();
                    mInfo.setText(info);

                    AVQuery<User> userAVQuery = new AVQuery<>("_User");
                    userAVQuery.getInBackground(mExpress.getUserID(), new GetCallback<User>() {
                        @Override
                        public void done(User user, AVException e) {
                            if(e == null) {
                                mUser = user;
                                pushToOwner();
                                AVRelation<Express> relation = AVUser.getCurrentUser(User.class).getRelation("fetch");
                                relation.add(mExpress);
                                Glide.with(ItemDetailActivity.this)
                                        .load(user.getAvatar())
                                        .crossFade()
                                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                                        .placeholder(R.mipmap.ic_launcher)
                                        .bitmapTransform(new CropCircleTransformation(ItemDetailActivity.this))
                                        .into(avatar);
                                AVUser.getCurrentUser(User.class).saveInBackground();
                            }
                            else{
                                Log.i(TAG, "done: "+e.getMessage());
                            }
                        }
                    });
                }
            }
        });
        



        mChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AlertDialog.Builder(ItemDetailActivity.this)
                        .setMessage("qing xuan ze cao zuo")
                        .setNeutralButton("da dan hua", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                Intent intent = new Intent(Intent.ACTION_CALL
                                        , Uri.parse("tel:" + mUser.getMobilePhoneNumber()));
                                startActivity(intent);
                            }
                        })
                        .setNeutralButton("fa duan xin", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                Intent intent = new Intent(Intent.ACTION_SENDTO
                                        , Uri.parse("sms:"+mUser.getMobilePhoneNumber()));
                                startActivity(intent);
                            }
                        })
                        .show();

            }
        });
    }

    public void doClick(View view) {
        int id = view.getId();
        switch (id){
            case R.id.got_it:
                finish();
                break;
        }
    }

    private void pushToOwner(){
        String pushID = mUser.getInstallationId();
        Log.i(TAG, "pushToOwner: "+pushID);
        AVPush push = new AVPush();

//        JSONObject data = new JSONObject();
//        data.put("action","com.lecoan.push.action");
//        data.put("meg",mUser.getUsername()+"领了你的快递");
//
//        push.setData(data);
//        push.setMessage("test");
//        push.setPushToAndroid(true);
//        push.setCloudQuery("select * from _Installation where installationId ='" + pushID + "'");
//        //push.setQuery(AVInstallation.getQuery().whereEqualTo(UserDao.INSTALLATIONID,pushID));
//        push.setChannel("private");
//        push.sendInBackground(new SendCallback() {
//            @Override
//            public void done(AVException e) {
//                if(e == null)
//                    Toast.makeText(ItemDetailActivity.this, "您领取的消息已经发送给了发布者", Toast.LENGTH_SHORT).show();
//                else
//                    Log.i(TAG, "done: "+e.getMessage());
//            }
//        });
        AVQuery pushQuery = AVInstallation.getQuery();
        pushQuery.whereEqualTo(UserDao.INSTALLATIONID, pushID);
        AVPush.sendMessageInBackground("message to installation",  pushQuery, new SendCallback() {
            @Override
            public void done(AVException e) {
                if(e == null)
                    Toast.makeText(ItemDetailActivity.this, "您领取的消息已经发送给了发布者", Toast.LENGTH_SHORT).show();
                else
                    Log.i(TAG, "done: "+e.getMessage());
            }
        });
    }
}
