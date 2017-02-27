package com.tongxin.youni.adapter;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVQuery;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.tongxin.youni.R;
import com.tongxin.youni.bean.Express;
import com.tongxin.youni.bean.User;
import com.tongxin.youni.utils.MyToast;
import com.tongxin.youni.widget.DataUtils;

import java.util.List;

import jp.wasabeef.glide.transformations.CropCircleTransformation;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * Created by charlene on 2017/2/26.
 */

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {

    private static final String TAG = "RecycleViewAdapter";
    private Context context;
    private List<Express> data;
    private LayoutInflater inflater=null;
    private long time = System.currentTimeMillis();
    public OnPick onPick;
    private OnItemClick onItemClick;

    public RecyclerViewAdapter(Context context, List<Express> data){
        this.context=context;
        this.data=data;
        this.inflater=LayoutInflater.from(context);
    }

    @Override
    public RecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View root=inflater.inflate(R.layout.item,parent,false);
        ViewHolder holder=new ViewHolder(root);
        return holder;
    }

    @Override
    public void onBindViewHolder(final RecyclerViewAdapter.ViewHolder holder, final int i) {
        holder.user_name.setText(data.get(i).getUserName());
        holder.company.setText("快递公司:"+data.get(i).getExpressCompany());
        holder.room.setText("宿舍号:"+data.get(i).getRoomID());
        holder.time.setText("快递到达时间:"+data.get(i).getTime());
        holder.money.setText("打赏:￥"+data.get(i).getMoney()+"元");
        holder.post_time.setText( DataUtils.formatDateTime(data.get(i).getCreatedAt()));
        holder.mCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onItemClick.onItemClick(holder.mCardView);
                MyToast.showToast(context, "带领后方可查看");
            }
        });
        holder.pickBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onPick.pick(data.get(i).getObjectId());
            }
        });

        final String id = data.get(i).getUserID();
        final AVQuery<User> query = new AVQuery<>("_User");

        Observable.create(new Observable.OnSubscribe<User>() {
            @Override
            public void call(Subscriber<? super User> subscriber) {
                try {
                    User user = query.get(id);
                    subscriber.onNext(user);
                } catch (AVException e) {
                    subscriber.onError(e);
                }
                subscriber.onCompleted();
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<User>() {
                               @Override
                               public void call(User user) {
                                   Glide.with(context)
                                           .load(user.getAvatar())
                                           .crossFade()
                                           .diskCacheStrategy(DiskCacheStrategy.ALL)
                                           .placeholder(R.mipmap.ic_launcher)
                                           .bitmapTransform(new CropCircleTransformation(context))
                                           .into(holder.avatar);
                                   Log.i(TAG, "userID: "+user.getUsername()+"\n url: "+user.getAvatar());
                               }
                           }
                        , new Action1<Throwable>() {
                            @Override
                            public void call(Throwable throwable) {
                                Log.i("---->", "done: "+id);
                                Log.i("-------->", "done: "+throwable.getMessage());
                            }
                        }
                );
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public interface OnPick{
        void pick(String ID);
    }

    public interface OnItemClick{
        void onItemClick(View view);
    }

    public void setOnItemClick(OnItemClick onItemClick){
        this.onItemClick=onItemClick;
    }

    public void setOnPick(OnPick onPick) {
        this.onPick = onPick;
    }


    class ViewHolder extends RecyclerView.ViewHolder{

        public TextView user_name;
        public ImageView avatar;
        public TextView company;
        public TextView room;
        public TextView time;
        public Button pickBt;
        public TextView money;
        public TextView post_time;
        public CardView mCardView;

        public ViewHolder(View itemView) {
            super(itemView);
            user_name= (TextView) itemView.findViewById(R.id.username);
            avatar = (ImageView) itemView.findViewById(R.id.avatar);
            company = (TextView) itemView.findViewById(R.id.express_company);
            room = (TextView) itemView.findViewById(R.id.room);
            pickBt = (Button) itemView.findViewById(R.id.pick_up_bt);
            time = (TextView) itemView.findViewById(R.id.time);
            money = (TextView) itemView.findViewById(R.id.money);
            post_time = (TextView) itemView.findViewById(R.id.post_time);
            mCardView = (CardView) itemView.findViewById(R.id.background);
        }
    }
}
