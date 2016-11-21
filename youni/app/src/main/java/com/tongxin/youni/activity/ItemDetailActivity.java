package com.tongxin.youni.activity;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.avos.avoscloud.AVException;
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

public class ItemDetailActivity extends Activity {

    private static final String TAG = "ItemActivity----->";
    private TextView mInfo;
    private Button Send_message,Dial;
    private Express mExpress;
    private String mUserID;
    private User mUser;
    private ImageView avatar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_detail);

        mInfo = (TextView) findViewById(R.id.info);
        Send_message = (Button) findViewById(R.id.btn_message);
        Dial= (Button) findViewById(R.id.btn_dial);
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
        

        Send_message.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_SENDTO
                        , Uri.parse("sms:"+mUser.getMobilePhoneNumber()));
                startActivity(intent);
//                new AlertDialog.Builder(ItemDetailActivity.this)
//                        .setMessage("请选择操作")
//                        .setPositiveButton("打电话", new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialogInterface, int i) {
//                                Intent intent = new Intent(Intent.ACTION_DIAL
//                                        , Uri.parse("tel:" + mUser.getMobilePhoneNumber()));
//                                startActivity(intent);
//                            }
//                        })
//                        .setNegativeButton("发短信", new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialogInterface, int i) {
//                                Intent intent = new Intent(Intent.ACTION_SENDTO
//                                        , Uri.parse("sms:"+mUser.getMobilePhoneNumber()));
//                                startActivity(intent);
//                            }
//                        })
//                        .show();

            }
        });

        Dial.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_DIAL
                        , Uri.parse("tel:" + mUser.getMobilePhoneNumber()));
                startActivity(intent);
            }
        });
    }

    public void doClick(View view) {
        int id = view.getId();
        switch (id){
            case R.id.got_it:
                setResult(RESULT_OK);
                finish();
                break;
        }
    }

}
