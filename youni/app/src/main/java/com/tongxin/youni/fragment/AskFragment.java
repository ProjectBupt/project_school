package com.tongxin.youni.fragment;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.AVRelation;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.FindCallback;
import com.avos.avoscloud.GetCallback;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.tongxin.youni.R;
import com.tongxin.youni.bean.Express;
import com.tongxin.youni.bean.ExpressDao;
import com.tongxin.youni.bean.User;
import java.util.List;

import jp.wasabeef.glide.transformations.CropCircleTransformation;

/**
 * Created by charlene on 2016/10/12.
 * 我的请求
 */

//test

public class AskFragment extends Fragment {

    private User this_user;
    private LinearLayout linearLayout;
    private LayoutInflater mInflater;
    private View view;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view=inflater.inflate(R.layout.fragment_ask_fetch,null);
        linearLayout= (LinearLayout) view.findViewById(R.id.line);
        mInflater= (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this_user= AVUser.getCurrentUser(User.class);
        setContentAsk();
        return view;
    }

    private void setContentAsk() {
        if (this_user!=null){
            AVRelation<Express> relation=this_user.getRelation("add");
            AVQuery<Express> query=relation.getQuery();
            query.findInBackground(new FindCallback<Express>() {
                @Override
                public void done(List<Express> list, AVException e) {
                    if (e==null){
                        if (list.size()!=0){
                            for (Express ex:list){
                                if (ex.getState()!= ExpressDao.isFinished){
                                    addContentAskView(ex);
                                }
                            }
                        }else{
                            TextView textView=new TextView(getContext());
                            textView.setTextSize(50);
                            textView.setGravity(Gravity.CENTER);
                            textView.setGravity(Gravity.CENTER);
                            textView.setText("这里空空哒~~");
                            linearLayout.addView(textView);
                        }
                    }else{
                        TextView textView=new TextView(getContext());
                        textView.setTextSize(50);
                        textView.setGravity(Gravity.CENTER);
                        textView.setGravity(Gravity.CENTER);
                        textView.setText("这里空空哒~~");
                        linearLayout.addView(textView);
                        Log.e("YOUNI","Error:"+e);
                    }
                }
            });
        }else{
            Log.e("YOUNI","User is null!");
            //Toast.makeText(getContext(), "Error!!!", Toast.LENGTH_SHORT).show();
        }
    }

    public void addContentAskView(final Express express){
        if (express.getState()== ExpressDao.isWaiting){
            final CardView cardView= (CardView) mInflater.inflate(R.layout.card_ask_waiting,null);

            TextView Company= (TextView) cardView.findViewById(R.id.company);
            Company.setText("快递公司:"+express.getExpressCompany());
            TextView Time= (TextView) cardView.findViewById(R.id.time);
            Time.setText("派件时间:"+express.getTime());
            TextView Payment= (TextView) cardView.findViewById(R.id.payment);
            Payment.setText("打赏金额:￥"+express.getMoney());

            Button Cancel= (Button) cardView.findViewById(R.id.btn_cancel);
            Cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    express.setState(ExpressDao.isFinished);
                    express.saveInBackground();
                    linearLayout.removeView(cardView);
                }
            });
            linearLayout.addView(cardView);
        }else{
            AVQuery<User> query=new AVQuery<>("_User");
            query.getInBackground(express.getDeliverID(), new GetCallback<User>() {
                @Override
                public void done(User user, AVException e) {
                    if (e==null){
                        final User FetchUser=user;
                        final CardView cardView= (CardView) mInflater.inflate(R.layout.card_ask_taken,null);

                        ImageView header= (ImageView) cardView.findViewById(R.id.header);
                        Glide.with(getContext())
                                .load(user.getAvatar())
                                .crossFade()
                                .diskCacheStrategy(DiskCacheStrategy.ALL)
                                .placeholder(R.mipmap.ic_launcher)
                                .bitmapTransform(new CropCircleTransformation(getContext()))
                                .into(header);

                        ImageButton Message,Dial;
                        Button Confirm;
                        Message= (ImageButton) cardView.findViewById(R.id.btn_message);
                        Dial= (ImageButton) cardView.findViewById(R.id.btn_dial);
                        Confirm= (Button) cardView.findViewById(R.id.btn_confirm);
                        Message.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent intent=new Intent(Intent.ACTION_SENDTO, Uri.parse("sms:"+FetchUser.getMobilePhoneNumber()));
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                                Toast.makeText(getContext(), "聊天", Toast.LENGTH_SHORT).show();
                            }
                        });
                        Dial.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + FetchUser.getMobilePhoneNumber()));
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                                Toast.makeText(getContext(), "聊天", Toast.LENGTH_SHORT).show();
                            }
                        });
                        Confirm.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                express.setState(ExpressDao.isFinished);
                                express.deleteInBackground();
                                linearLayout.removeView(cardView);
                                AddOrder(FetchUser);
                                Toast.makeText(getContext(), "确认送达", Toast.LENGTH_SHORT).show();
                            }
                        });

                        TextView UserName,RoomNumber,PhoneNumber,StuNumber,Time,Payment;
                        UserName=(TextView)cardView.findViewById(R.id.user_name);
                        UserName.setText(FetchUser.getUsername());
                        PhoneNumber= (TextView) cardView.findViewById(R.id.phone_number);
                        PhoneNumber.setText("电话:"+FetchUser.getMobilePhoneNumber());
                        RoomNumber= (TextView) cardView.findViewById(R.id.room_number);
                        RoomNumber.setText("宿舍号:"+FetchUser.getRoomID());
                        StuNumber= (TextView) cardView.findViewById(R.id.stu_number);
                        StuNumber.setText("学号:"+FetchUser.getStudentID());
                        Time= (TextView) cardView.findViewById(R.id.time);
                        Time.setText("派件时间:"+express.getTime());
                        Payment= (TextView) cardView.findViewById(R.id.payment);
                        Payment.setText("打赏金额:￥"+express.getMoney());
                        linearLayout.addView(cardView);
                    }else{
                        Log.e("YOUNI","Error:"+e);
                    }
                }
            });
        }
    }

    private void AddOrder(User FetchUser) {
        this_user.setQuantityOfAsking(this_user.getQuantityOfAsking()+1);
        this_user.saveInBackground();

        FetchUser.setQuantityOfOrder(FetchUser.getQuantityOfOrder()+1);
        FetchUser.saveInBackground();
    }
}
