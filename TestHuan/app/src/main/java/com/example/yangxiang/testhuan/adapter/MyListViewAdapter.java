package com.example.yangxiang.testhuan.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.GetCallback;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.yangxiang.testhuan.R;
import com.example.yangxiang.testhuan.bean.Express;
import com.example.yangxiang.testhuan.bean.User;

import java.util.List;

import jp.wasabeef.glide.transformations.CropCircleTransformation;


/**
 * Created by charlene on 2016/9/27.
 */
public class MyListViewAdapter extends BaseAdapter {
    private User iUser;
    private Context context;
    private List<Express> data;
    private LayoutInflater inflater=null;

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
        ViewHolder holder=null;
        //如果缓存为空,则创建view
        if (view==null){
            holder=new ViewHolder();
            view=inflater.inflate(R.layout.item,viewGroup,false);

            holder.user_name= (TextView) view.findViewById(R.id.username);
            holder.avatar = (ImageView) view.findViewById(R.id.avatar);
            holder.company = (TextView) view.findViewById(R.id.express_company);
            holder.room = (TextView) view.findViewById(R.id.room);
            holder.pickBt = (Button) view.findViewById(R.id.pick_up_bt);
            holder.time = (TextView) view.findViewById(R.id.post_time);
            holder.money = (TextView) view.findViewById(R.id.money);

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
        holder.company.setText(data.get(i).getExpressCompany());
        holder.room.setText(data.get(i).getRoomID());
        holder.time.setText(data.get(i).getTime());
        holder.money.setText("打赏:"+data.get(i).getMoney()+"元");

        final String id = data.get(i).getUserID();
        AVQuery<User> query = new AVQuery<>("_User");

        final ViewHolder finalHolder = holder;
        query.getInBackground(id, new GetCallback<User>() {
            @Override
            public void done(User user, AVException e) {
                if(e == null){
                    iUser = user;
                    Glide.with(context)
                            .load(iUser.getAvatar())
                            .crossFade()
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                            .placeholder(R.mipmap.ic_launcher)
                            .bitmapTransform(new CropCircleTransformation(context))
                            .into(finalHolder.avatar);
                }
                else {
                    Log.i("---->", "done: "+id);
                    Log.i("-------->", "done: "+e.getMessage());
                }
            }
        });


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

    }

    public interface RemoveItem{
        void Skip(String ID);
    }

    public void setRemoveItem(RemoveItem removeItem) {
        this.removeItem = removeItem;
    }
}
