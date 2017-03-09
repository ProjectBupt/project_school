package com.tongxin.youni.fragment;


import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.avos.avoscloud.AVCloud;
import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.FindCallback;
import com.avos.avoscloud.GetCallback;
import com.avos.avoscloud.SaveCallback;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.tongxin.youni.MyApplication;
import com.tongxin.youni.R;
import com.tongxin.youni.activity.ItemDetailActivity;
import com.tongxin.youni.activity.MainActivity;
import com.tongxin.youni.activity.PostFeed;
import com.tongxin.youni.activity.Screen;
import com.tongxin.youni.activity.UserCenterActivity;
import com.tongxin.youni.adapter.RecyclerViewAdapter;
import com.tongxin.youni.bean.Express;
import com.tongxin.youni.bean.ExpressDao;
import com.tongxin.youni.bean.User;
import com.tongxin.youni.utils.MyToast;
import com.xiaomi.xmpush.server.Message;
import com.xiaomi.xmpush.server.Sender;

import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import jp.wasabeef.glide.transformations.CropCircleTransformation;
import rx.Observable;
import rx.Observer;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.functions.Func1;
import rx.schedulers.Schedulers;
/**
 * A simple {@link Fragment} subclass.
 */
public class ExpressFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener, RecyclerViewAdapter.OnPick, RecyclerViewAdapter.OnItemClick {
    private static final int SEE_INFO = 0x33;
    private static final int REFRESH_COMPLETE=0x110;
    private static final int POST_EXPRESS = 2;
    private static final int SCREEN_EXPRESS = 0xff;
    private static final String TAG = "-------------->";
    private boolean isButtonOut = false;
    private RecyclerView recyclerView;
    private RecyclerViewAdapter adapter;

    private List<Express> mData;
    private SwipeRefreshLayout refreshLayout;
    private FloatingActionButton mAddButton;
    private FloatingActionButton book;
    private FloatingActionButton express;
    private Toolbar mToolbar;

    private int mLastFirstVisibleItem = 0;

    public ExpressFragment() {
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.main2,menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id){
            case R.id.setting:
                Intent intent = new Intent(getActivity(), Screen.class);
                startActivityForResult(intent,SCREEN_EXPRESS);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_express, container, false);

        recyclerView= (RecyclerView) view.findViewById(R.id.recycler_view);
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                AnimateBack();
                if (Math.abs(dy)>4){
                    if (dy<0) {
                        mAddButton.show();
                        book.show();
                        express.show();
                    }else {
                        book.hide();
                        express.hide();
                        mAddButton.hide();
                    }
                }
            }
        });
        LinearLayoutManager manager=
                new LinearLayoutManager(getContext(),LinearLayoutManager.VERTICAL,false);
        RecyclerView.ItemAnimator animator=new DefaultItemAnimator();
        recyclerView.setLayoutManager(manager);
        recyclerView.setItemAnimator(animator);

        refreshLayout= (SwipeRefreshLayout) view.findViewById(R.id.swipe_refresh);
        mAddButton = (FloatingActionButton) view.findViewById(R.id.float_add);
        book= (FloatingActionButton) view.findViewById(R.id.float_book);
        express= (FloatingActionButton) view.findViewById(R.id.float_express);
        mToolbar = (Toolbar) view.findViewById(R.id.toolbar);
        this.setHasOptionsMenu(true);

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        ImageView han = (ImageView) mToolbar.findViewById(R.id.han);
        han.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getContext(), UserCenterActivity.class);
                startActivity(intent);
//                DrawerLayout drawerLayout = (DrawerLayout) getActivity().findViewById(R.id.drawer_layout);
//                drawerLayout.openDrawer(GravityCompat.START);
            }
        });

//        Glide.with(this)
//                .load(User.getCurrentUser(User.class).getAvatar())
//                .crossFade()
//                .diskCacheStrategy(DiskCacheStrategy.ALL)
//                .placeholder(R.drawable.default_header)
//                .bitmapTransform(new CropCircleTransformation(getActivity()))
//                .into(han);

        refreshLayout.setOnRefreshListener(this);
        refreshLayout.post(new Runnable() {
            @Override
            public void run() {
                refreshLayout.setRefreshing(true);
            }
        });

        mData = new ArrayList<>();
        adapter=new RecyclerViewAdapter(getContext(),mData);
        adapter.setOnPick(this);
        adapter.setOnItemClick(this);
        recyclerView.setAdapter(adapter);
        onRefresh();

        mAddButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isButtonOut){
                    AnimateBack();
                }else{
                    AnimateBegin();
                }
            }
        });

        express.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent postIntent = new Intent(getActivity(), PostFeed.class);
                startActivityForResult(postIntent,POST_EXPRESS);
                AnimateBack();
            }
        });

        book.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AnimateBack();
            }
        });

        AppCompatActivity activity = ((MainActivity)getActivity());
        activity.setSupportActionBar(mToolbar);
        activity.getSupportActionBar().setDisplayShowTitleEnabled(false);

        recyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
            }
        });
//        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
//            @Override
//            public void onScrollStateChanged(AbsListView view, int scrollState) {
//            }
//
//            @Override
//            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
//                if(mLastFirstVisibleItem<firstVisibleItem)
//                {
//                    Log.i("SCROLLING DOWN","TRUE");
//                    book.hide();
//                    express.hide();
//                    mAddButton.hide();
//                }
//                if(mLastFirstVisibleItem>firstVisibleItem)
//                {
//                    Log.i("SCROLLING UP","TRUE");
//                    book.show();
//                    express.show();
//                    mAddButton.show();
//                }
//                mLastFirstVisibleItem=firstVisibleItem;
//
//                if (isButtonOut){
//                    AnimateBack();
//                    isButtonOut=false;
//                }
//            }
//        });

    }

    private void AnimateBegin() {
        if (!isButtonOut){
            ObjectAnimator animator1=ObjectAnimator.ofFloat(express,"translationY",0f,-300f);
            animator1.setDuration(300);
            ObjectAnimator animator2=ObjectAnimator.ofFloat(book,"translationY",0f,-150f);
            animator2.setDuration(300);
            animator1.setStartDelay(100);
            animator1.start();
            animator2.start();
        }
        isButtonOut=true;
    }

    private void AnimateBack() {
        if (isButtonOut){
            ObjectAnimator animator1=ObjectAnimator.ofFloat(express,"translationY",-300f,0f);
            animator1.setDuration(300);
            ObjectAnimator animator2=ObjectAnimator.ofFloat(book,"translationY",-150f,0f);
            animator2.setDuration(300);
            animator1.setStartDelay(100);
            animator1.start();
            animator2.start();
        }
        isButtonOut=false;
    }

    //处理筛选请求
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == POST_EXPRESS){
            if(resultCode == Activity.RESULT_OK){
                refreshLayout.setRefreshing(true);
                onRefresh();
                Log.i(TAG, "onActivityResult: 应该刷新了啊");
            }
        }

        if(requestCode == SEE_INFO){
            if(resultCode == Activity.RESULT_OK){
                refreshLayout.setRefreshing(true);
                onRefresh();
                Log.i(TAG, "onActivityResult: 应该刷新了啊");
            }
        }
        if(requestCode == SCREEN_EXPRESS){
            if(resultCode == Activity.RESULT_OK){
                refreshLayout.setRefreshing(true);
                Bundle extra = data.getExtras();
                final int building = extra.getInt("Building");
                final int exCom = extra.getInt("Express");
                Log.i(TAG, "onActivityResult: "+building+"\n"+exCom);
//                AVQuery<Express> query1 = new AVQuery<>("Express");
                final String company;
                company = getCompany(exCom);

                final char build = (char) ('A'+building - 1);

                Observable.create(new Observable.OnSubscribe<List<Express>>() {
                    @Override
                    public void call(Subscriber<? super List<Express>> subscriber) {
                        AVQuery<Express> query=new AVQuery<>("Express");
                        query.limit(100);
                        query.orderByDescending("createdAt");
                        query.whereEqualTo(ExpressDao.state,ExpressDao.isWaiting);
                        try {
                            subscriber.onNext(query.find());
                        } catch (AVException e) {
                            subscriber.onError(e);
                        }
                        subscriber.onCompleted();
                    }
                }).subscribeOn(Schedulers.io())
                .doOnSubscribe(new Action0() {
                    @Override
                    public void call() {
                        mData.clear();
                        adapter.notifyDataSetChanged();
                    }
                }).subscribeOn(AndroidSchedulers.mainThread())
                 .flatMap(new Func1<List<Express>, Observable<Express>>() {
                     @Override
                     public Observable<Express> call(List<Express> expresses) {
                         return Observable.from(expresses);
                     }
                 }).observeOn(AndroidSchedulers.mainThread())
                 .subscribe(new Subscriber<Express>() {
                     @Override
                     public void onCompleted() {
                         adapter.notifyDataSetChanged();
                         if(refreshLayout.isRefreshing()){
                             refreshLayout.setRefreshing(false);
                         }
                         Log.i(TAG, "onCompleted: 物品刷新完成");
                     }

                     @Override
                     public void onError(Throwable e) {
                         MyToast.showToast(getContext(),"网络异常");
                         Log.i(TAG, "onError: "+e.getMessage());
                         refreshLayout.setRefreshing(false);
                     }

                     @Override
                     public void onNext(Express express) {
                         Log.i(TAG, "done: "+express.getRoomID()+"\n"+express.getExpressCompany());
                         if(exCom ==0 && building == 0) {
                             mData.add(express);
                         }
                         else if(exCom == 0 && express.getRoomID().charAt(0) == build) {
                             mData.add(express);
                         }
                         else if(company.equals(express.getExpressCompany()) && express.getRoomID().charAt(0) == build) {
                             mData.add(express);
                         }
                         else if(company.equals(express.getExpressCompany()) && building == 0) {
                             mData.add(express);
                         }
                     }
                 });

            }
        }
    }

    @Override
    public void onRefresh() {
        Observable.create(new Observable.OnSubscribe<List<Express>>() {
            @Override
            public void call(Subscriber<? super List<Express>> subscriber) {
                AVQuery<Express> query=new AVQuery<>("Express");
                query.limit(40);
                query.orderByDescending("createdAt");
                query.whereEqualTo(ExpressDao.state,ExpressDao.isWaiting);
                try {
                    subscriber.onNext(query.find());
                } catch (AVException e) {
                    subscriber.onError(e);
                }
                subscriber.onCompleted();
            }
        }).subscribeOn(Schedulers.io())
          .doOnSubscribe(new Action0() {
              @Override
              public void call() {
                  mData.clear();
                  adapter.notifyDataSetChanged();
              }
          }).subscribeOn(AndroidSchedulers.mainThread())
        .flatMap(new Func1<List<Express>, Observable<Express>>() {
            @Override
            public Observable<Express> call(List<Express> expresses) {
                return Observable.from(expresses);
            }
        }).observeOn(AndroidSchedulers.mainThread())
          .subscribe(new Subscriber<Express>() {
              @Override
              public void onCompleted() {
                  adapter.notifyDataSetChanged();
                  if(refreshLayout.isRefreshing()){
                      refreshLayout.setRefreshing(false);
                  }
                  Log.i(TAG, "onCompleted: 物品刷新完成");
              }

              @Override
              public void onError(Throwable e) {
                  MyToast.showToast(getContext(),"网络异常");
                  Log.i(TAG, "onError: "+e.getMessage());
                  refreshLayout.setRefreshing(false);
              }

              @Override
              public void onNext(Express express) {
                  mData.add(express);
              }
          });
    }

    @NonNull
    private String getCompany(int exCom) {
        String company;
        switch (exCom){
            case 1:
                company = "圆通";
                break;
            case 2:
                company = "中通";
                break;
            case 3:
                company = "申通";
                break;
            case 4:
                company = "韵达";
                break;
            case 5:
                company = "京东";
                break;
            case 6:
                company = "顺丰";
                break;
            case 7:
                company = "如风达";
                break;
            case 8:
                company = "天天";
                break;
            case 9:
                company = "一统飞鸿";
                break;
            case 10:
                company = "全峰";
                break;
            case 11:
                company = "唯品会";
                break;
            case 12:
                company = "优速";
                break;
            case 13:
                company = "德邦";
                break;
            case 0:
                company = "全部";
                break;
            default:
                company = "";

        }
        return company;
    }

    private List<Express> getData() {
        mData.clear();
        AVQuery<Express> query=new AVQuery<>("Express");
        query.limit(40);
        query.orderByDescending("createdAt");
        query.whereEqualTo(ExpressDao.state,ExpressDao.isWaiting);
        query.findInBackground(new FindCallback<Express>() {
            @Override
            public void done(List<Express> list, AVException e) {
                if(e == null){
                    for(Express express:list){
                        mData.add(express);
                    }
                }
                else{
                    MyToast.showToast(getContext(),"boom");
                }
            }
        });
        return mData;
    }

    @Override
    public void pick(final String ID) {
        AVQuery<Express> data = new AVQuery<>("Express");
        data.getInBackground(ID, new GetCallback<Express>() {
            @Override
            public void done(final Express express, AVException e) {
                if(e == null) {
                    if(express.getState() != ExpressDao.isWaiting) {
                        MyToast.showToast(getContext(),"已经被领取咯");
                    }
                    else if(!express.getUserID().equals(User.getCurrentUser(User.class).getObjectId())){
                        Log.i(TAG, "done: "+express.getUserID());
                        Log.i(TAG, "done: "+User.getCurrentUser(User.class).getObjectId());
                        express.setState(ExpressDao.isTaking);
                        express.saveInBackground(new SaveCallback() {
                            @Override
                            public void done(final AVException e) {
                                if(e == null) {
                                    Intent intent = new Intent(getActivity(), ItemDetailActivity.class);

                                    Observable.create(new Observable.OnSubscribe<Boolean>() {
                                        @Override
                                        public void call(Subscriber<? super Boolean> subscriber) {
                                            Message message = new Message.Builder().title("您的快递已被领取")
                                                    .description(User.getCurrentUser(User.class).getUsername()+"代领了您的快递")
                                                    .passThrough(0)
                                                    .notifyType(Message.NOTIFY_TYPE_ALL)
                                                    .build();
                                            Sender sender = new Sender(MyApplication.SECRUIT_CODE);
                                            try {
                                                sender.sendToAlias(message,express.getPhone(),20);
                                            } catch (IOException e1) {
                                                subscriber.onError(e1);
                                            } catch (ParseException e1) {
                                                subscriber.onError(e1);
                                            }
                                        }
                                    }).subscribeOn(Schedulers.io())
                                            .observeOn(AndroidSchedulers.mainThread())
                                            .subscribe(new Observer<Boolean>() {
                                                @Override
                                                public void onCompleted() {

                                                }

                                                @Override
                                                public void onError(Throwable e) {
                                                    MyToast.showToast(getContext(),e.getMessage());
                                                }

                                                @Override
                                                public void onNext(Boolean aBoolean) {
                                                    MyToast.showToast(getContext(),"您领取的消息以发送给发布者");
                                                }
                                            });

                                    intent.putExtra("ExpressID",ID);
                                    startActivityForResult(intent, SEE_INFO);
                                }
                                else{
                                    Log.i(TAG, "done in skip: "+e.getMessage());
                                }
                            }
                        });
                    }
                    else {
                        MyToast.showToast(getContext(),"不可以代领自己的哦");
                    }
                }
                else {
                    MyToast.showToast(getContext(),"无法获取物品信息");
                }
            }
        });
    }

    @Override
    public void onItemClick(View view) {
        AnimateBack();
    }


}