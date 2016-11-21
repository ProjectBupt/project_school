package com.tongxin.youni.adapter;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVQuery;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.tongxin.youni.R;
import com.tongxin.youni.bean.Express;
import com.tongxin.youni.bean.User;
import com.tongxin.youni.widget.DataUtils;

import java.util.List;

import jp.wasabeef.glide.transformations.CropCircleTransformation;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;


/**
 * Created by charlene on 2016/9/27.
 */
public class MyListViewAdapter extends BaseAdapter {
    private static final String TAG = "ListAdapter----->";
    private User iUser;
    private Context context;
    private List<Express> data;
    private LayoutInflater inflater=null;
    private long time = System.currentTimeMillis();

    public RemoveItem removeItem;

    public MyListViewAdapter(Context context, List<Express> data){
        this.context=context;
        this.data=data;
        this.inflater= LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int i) {
        return i;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(final int i, View view, ViewGroup viewGroup) {
        ViewHolder holder;
        if (view==null){
            holder=new ViewHolder();
            view=inflater.inflate(R.layout.item,viewGroup,false);

            holder.user_name= (TextView) view.findViewById(R.id.username);
            holder.avatar = (ImageView) view.findViewById(R.id.avatar);
            holder.company = (TextView) view.findViewById(R.id.express_company);
            holder.room = (TextView) view.findViewById(R.id.room);
            holder.pickBt = (Button) view.findViewById(R.id.pick_up_bt);
            holder.time = (TextView) view.findViewById(R.id.time);
            holder.money = (TextView) view.findViewById(R.id.money);
            holder.post_time = (TextView) view.findViewById(R.id.post_time);
            holder.mCardView = (CardView) view.findViewById(R.id.background);

            holder.pickBt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    removeItem.Skip(data.get(i).getObjectId());
                }
            });

            view.setTag(holder);
        }else{
            holder= (ViewHolder) view.getTag();
        }
        holder.user_name.setText(data.get(i).getUserName());
        holder.company.setText("快递公司:"+data.get(i).getExpressCompany());
        holder.room.setText("宿舍号:"+data.get(i).getRoomID());
        holder.time.setText("快递到达时间:"+data.get(i).getTime());
        holder.money.setText("打赏:￥"+data.get(i).getMoney()+"元");
        holder.post_time.setText( DataUtils.formatDateTime(data.get(i).getCreatedAt()));
        holder.mCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(context, "带领后方可查看", Toast.LENGTH_SHORT).show();
            }
        });

        final String id = data.get(i).getUserID();
        final AVQuery<User> query = new AVQuery<>("_User");

        final ViewHolder finalHolder = holder;
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
                                     .into(finalHolder.avatar);
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
        return view;
    }

    private static class ViewHolder{
        TextView user_name;
        ImageView avatar;
        TextView company;
        TextView room;
        TextView time;
        Button pickBt;
        TextView money;
        TextView post_time;
        CardView mCardView;
    }

    public interface RemoveItem{
        void Skip(String ID);
    }

    public void setRemoveItem(RemoveItem removeItem) {
        this.removeItem = removeItem;
    }
}
