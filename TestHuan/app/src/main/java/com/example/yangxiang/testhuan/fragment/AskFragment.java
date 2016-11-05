package com.example.yangxiang.testhuan.fragment;

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
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.AVRelation;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.FindCallback;
import com.avos.avoscloud.GetCallback;
import com.example.yangxiang.testhuan.R;
import com.example.yangxiang.testhuan.bean.Express;
import com.example.yangxiang.testhuan.bean.ExpressDao;
import com.example.yangxiang.testhuan.bean.User;
import java.util.List;


/**
 * Created by charlene on 2016/10/12.
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
                    boolean tag=false;
                    if (e==null){
                        if (list.size()!=0){
                            for (Express ex:list){
                                if (ex.getState()!= ExpressDao.isFinished){
                                    addContentAskView(ex);
                                    tag=true;
                                }
                            }
                            if (tag==false){
                                TextView textView=new TextView(getContext());
                                textView.setText("这里空空哒~~");
                                linearLayout.addView(textView);
                            }
                        }else{
                            TextView textView=new TextView(getContext());
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
            Toast.makeText(getContext(), "Error!!!", Toast.LENGTH_SHORT).show();
        }
    }

    public void addContentAskView(final Express express){
        if (express.getState()== ExpressDao.isWaiting){
            final CardView cardView= (CardView) mInflater.inflate(R.layout.card_ask_waiting,null);

            Button Cancel;
            Cancel= (Button) cardView.findViewById(R.id.btn_cancel);
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
                        Button Message,Dial,Confirm;
                        Message= (Button) cardView.findViewById(R.id.btn_message);
                        Dial= (Button) cardView.findViewById(R.id.btn_dial);
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
                                Toast.makeText(getContext(), "确认送达", Toast.LENGTH_SHORT).show();
                            }
                        });

                        TextView UserName,RoomNumber,PhoneNumber,Company,Time,Attention;
                        UserName=(TextView)cardView.findViewById(R.id.user_name);
                        UserName.setText("用户名:"+FetchUser.getUsername());
                        PhoneNumber= (TextView) cardView.findViewById(R.id.phone_number);
                        PhoneNumber.setText("Tele:"+FetchUser.getMobilePhoneNumber());
                        RoomNumber= (TextView) cardView.findViewById(R.id.room_number);
                        RoomNumber.setText("Room:"+user.getRoomID());
                        linearLayout.addView(cardView);
                    }else{
                        Log.e("YOUNI","Error:"+e);
                    }
                }
            });
        }
    }
}
