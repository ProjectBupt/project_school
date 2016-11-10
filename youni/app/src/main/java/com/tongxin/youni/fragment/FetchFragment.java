package com.tongxin.youni.fragment;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.tongxin.youni.bean.User;

import java.util.List;

import jp.wasabeef.glide.transformations.CropCircleTransformation;

/**
 * Created by charlene on 2016/10/12.
 * 我帮别人代领的快递
 */

public class FetchFragment extends Fragment {

    private User this_user;
    private LinearLayout linearLayout;
    private LayoutInflater mInflater;
    private View view;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view=inflater.inflate(R.layout.fragment_ask_fetch,null);
        linearLayout= (LinearLayout) view.findViewById(R.id.line);
        mInflater=LayoutInflater.from(getContext());
        this_user= AVUser.getCurrentUser(User.class);
        setContentFetch();
        return view;
    }

    private void setContentFetch() {
        if (this_user!=null){
            AVRelation<Express> relation=this_user.getRelation("fetch");
            AVQuery<Express> query=relation.getQuery();
            query.findInBackground(new FindCallback<Express>() {
                @Override
                public void done(List<Express> list, AVException e) {
                    if (e==null){
                        if (list.size()!=0){
                            for (Express ex:list){
                                addContentFetchView(ex);
                            }
                        }else{
                            TextView textView=new TextView(getContext());
                            textView.setTextSize(50);
                            textView.setGravity(Gravity.CENTER);
                            textView.setText("这里空空哒~~");
                            linearLayout.addView(textView);
                        }
                    }else{
                        TextView textView=new TextView(getContext());
                        textView.setTextSize(50);
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

    public void addContentFetchView(final Express express){
        AVQuery<User> query=new AVQuery<>("_User");
        query.getInBackground(express.getUserID(), new GetCallback<User>() {
            @Override
            public void done(final User user, AVException e) {
                if (e==null) {
                    CardView cardView = (CardView) mInflater.inflate(R.layout.card_fetch, null);

                    ImageView header= (ImageView) cardView.findViewById(R.id.header);
                    Glide.with(getContext())
                            .load(user.getAvatar())
                            .crossFade()
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                            .placeholder(R.mipmap.ic_launcher)
                            .bitmapTransform(new CropCircleTransformation(getContext()))
                            .into(header);

                    ImageButton Message,Dial,Attention;
                    Message= (ImageButton) cardView.findViewById(R.id.btn_message);
                    Dial= (ImageButton) cardView.findViewById(R.id.btn_dial);
                    Attention= (ImageButton) cardView.findViewById(R.id.btn_attention);

                    Dial.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent=new Intent(Intent.ACTION_DIAL, Uri.parse("tel:"+user.getMobilePhoneNumber()));
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                        }
                    });
                    Message.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent=new Intent(Intent.ACTION_SENDTO,Uri.parse("sms:"+user.getMobilePhoneNumber()));
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                        }
                    });
                    Attention.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Log.i("FETCH_FRAGMENT","查看详细信息");
                            ShowDetail(user,express);
                        }
                    });

                    TextView UserName, RoomNumber, PhoneNumber, Company, Time, Payment;
                    UserName = (TextView) cardView.findViewById(R.id.user_name);
                    UserName.setText(user.getUsername());
                    RoomNumber= (TextView) cardView.findViewById(R.id.room_number);
                    RoomNumber.setText("地址:"+user.getRoomID());
                    PhoneNumber = (TextView) cardView.findViewById(R.id.phone_number);
                    PhoneNumber.setText("电话:"+user.getMobilePhoneNumber());
                    Company = (TextView) cardView.findViewById(R.id.company);
                    Company.setText("快递公司:"+express.getExpressCompany());
                    Time= (TextView) cardView.findViewById(R.id.time);
                    Time.setText("派件时间:"+express.getTime());
                    Payment= (TextView) cardView.findViewById(R.id.payment);
                    Payment.setText("打赏金额:￥"+express.getMoney());
                    linearLayout.addView(cardView);
                }else{
                    Log.e("YOUNI","User is null!");
                    //Toast.makeText(getContext(), "Error!!!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void ShowDetail(User user,Express express){
        LinearLayout linearLayout= (LinearLayout) mInflater.inflate(R.layout.express_details,null);
        //设置detail中的信息
        TextView user_name=(TextView)linearLayout.findViewById(R.id.user_name);
        user_name.setText("用户名:"+user.getUsername());
        TextView room=(TextView)linearLayout.findViewById(R.id.room);
        room.setText("宿舍号:"+user.getRoomID());
        TextView payment=(TextView)linearLayout.findViewById(R.id.payment);
        payment.setText("打赏金额:"+express.getMoney());
        TextView type= (TextView) linearLayout.findViewById(R.id.type);
        type.setText("类型:"+express.getType());
        TextView attention=(TextView)linearLayout.findViewById(R.id.attention);
        if ("".equals(express.getExtra())){
            attention.setText("注意事项:发布人很懒,什么注意事项也没写-_-\"");
        }else{
            attention.setText("注意事项:"+express.getExtra());
        }

        final AlertDialog.Builder builder=new AlertDialog.Builder(getContext());
        builder.setView(linearLayout);
        final AlertDialog dialog=builder.create();
        dialog.show();
        linearLayout.findViewById(R.id.btn_confirm).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }
}
