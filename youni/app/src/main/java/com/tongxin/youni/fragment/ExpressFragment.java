package com.tongxin.youni.fragment;


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
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.FindCallback;
import com.avos.avoscloud.GetCallback;
import com.avos.avoscloud.SaveCallback;
import com.tongxin.youni.R;
import com.tongxin.youni.activity.ItemDetailActivity;
import com.tongxin.youni.activity.MainActivity;
import com.tongxin.youni.activity.PostFeed;
import com.tongxin.youni.activity.Screen;
import com.tongxin.youni.adapter.MyListViewAdapter;
import com.tongxin.youni.bean.Express;
import com.tongxin.youni.bean.ExpressDao;
import com.tongxin.youni.bean.User;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.functions.Func1;
import rx.schedulers.Schedulers;
//test
/**
 * A simple {@link Fragment} subclass.
 */
public class ExpressFragment extends Fragment
        implements MyListViewAdapter.RemoveItem, SwipeRefreshLayout.OnRefreshListener {
    private static final int SEE_INFO = 0x33;
    private static final int REFRESH_COMPLETE=0x110;
    private static final int POST_EXPRESS = 2;
    private static final int SCREEN_EXPRESS = 0xff;
    private static final String TAG = "-------------->";
    private ListView listView;
    private MyListViewAdapter adapter;
    private List<Express> mData;
    private SwipeRefreshLayout refreshLayout;
    private FloatingActionButton mAddButton;
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
        listView= (ListView) view.findViewById(R.id.list_view);
        listView.setDivider(null);
        refreshLayout= (SwipeRefreshLayout) view.findViewById(R.id.swipe_refresh);
        mAddButton = (FloatingActionButton) view.findViewById(R.id.float_add);
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
                DrawerLayout drawerLayout = (DrawerLayout) getActivity().findViewById(R.id.drawer_layout);
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });

        refreshLayout.setOnRefreshListener(this);
        mData = new ArrayList<>();
        adapter=new MyListViewAdapter(getActivity(),mData);

        refreshLayout.post(new Runnable() {
            @Override
            public void run() {
                refreshLayout.setRefreshing(true);
            }
        });
        onRefresh();

        adapter.setRemoveItem(this);
        listView.setAdapter(adapter);
        mAddButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent postIntent = new Intent(getActivity(), PostFeed.class);
                startActivityForResult(postIntent,POST_EXPRESS);
            }
        });

        onRefresh();
        AppCompatActivity activity = ((MainActivity)getActivity());
        activity.setSupportActionBar(mToolbar);
        activity.getSupportActionBar().setDisplayShowTitleEnabled(false);

        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if(mLastFirstVisibleItem<firstVisibleItem) {
                    mAddButton.hide();
                }
                if(mLastFirstVisibleItem>firstVisibleItem) {
                    mAddButton.show();
                }
                mLastFirstVisibleItem=firstVisibleItem;
            }
        });

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
                int building = extra.getInt("Building");
                final int exCom = extra.getInt("Express");
                AVQuery<Express> query1 = new AVQuery<>("Express");
                String company;
                company = getCompany(exCom);

                query1.whereEqualTo(ExpressDao.company,company);
                AVQuery<Express> query2 = new AVQuery<>("Express");
                char build = (char) ('A'+building);
                query2.whereContains(ExpressDao.roomId,""+build);
                AVQuery<Express> query = AVQuery.and(Arrays.asList(query1,query2));
                query.orderByDescending("createdAt");
                query.findInBackground(new FindCallback<Express>() {
                    @Override
                    public void done(List<Express> list, AVException e) {
                        if(e == null){
                            mData.clear();
                            for (Express express : list) {
                                if(express.getState() == ExpressDao.isWaiting)
                                    mData.add(express);
                            }
                            adapter.notifyDataSetChanged();
                            refreshLayout.setRefreshing(false);
                        }
                        else {
                            Log.e(TAG, "done in sort express: "+e.getMessage());
                        }
                    }
                });
            }
        }
    }

    @Override
    public void Skip(final String ID) {
        //TODO 把跳转重写，逻辑清晰
//        Observable.create(new Observable.OnSubscribe<Express>() {
//            @Override
//            public void call(Subscriber<? super Express> subscriber) {
//                AVQuery<Express> data = new AVQuery<>("Express");
//                try {
//                    Express ex = data.get(ID);
//                    if(ex.getState() == ExpressDao.isWaiting){
//                        subscriber.onNext(ex);
//                    }
//                } catch (AVException e) {
//                    subscriber.onError(e);
//                }
//                subscriber.onCompleted();
//            }
//        }).subscribeOn(Schedulers.io())
//          .flatMap(new Func1<Express, Observable<User>>() {
//            @Override
//            public Observable<User> call(Express express) {
//                return Observable.create(new Observable.OnSubscribe<User>() {
//                    @Override
//                    public void call(Subscriber<? super User> subscriber) {
//                        if(User.getCurrentUser().getObjectId() != express.getUserID()){
//                            subscriber.onNext();
//                        }
//                    }
//                });
//            }
//        })

        AVQuery<Express> data = new AVQuery<>("Express");
        data.getInBackground(ID, new GetCallback<Express>() {
            @Override
            public void done(Express express, AVException e) {
                if(e == null) {
                    if(express.getState() != ExpressDao.isWaiting) {
                        Toast.makeText(getActivity(), "已经被领取咯", Toast.LENGTH_SHORT).show();
                    }
                    else if(!express.getUserID().equals(User.getCurrentUser(User.class).getObjectId())){
                        Log.i(TAG, "done: "+express.getUserID());
                        Log.i(TAG, "done: "+User.getCurrentUser(User.class).getObjectId());
                        express.setState(ExpressDao.isTaking);
                        express.saveInBackground(new SaveCallback() {
                            @Override
                            public void done(AVException e) {
                                if(e == null) {
                                    Intent intent = new Intent(getActivity(), ItemDetailActivity.class);
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
                        Toast.makeText(getActivity(), "不可以代领自己的哦", Toast.LENGTH_SHORT).show();
                    }
                }
                else {
                    Toast.makeText(getActivity(), "无法获取物品信息", Toast.LENGTH_SHORT).show();
                }
            }
        });
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
                  Toast.makeText(getActivity(), "网络异常", Toast.LENGTH_SHORT).show();
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
            case 0:
                company = "圆通";
                break;
            case 1:
                company = "中通";
                break;
            case 2:
                company = "申通";
                break;
            case 3:
                company = "韵达";
                break;
            case 4:
                company = "京东";
                break;
            case 5:
                company = "顺丰";
                break;
            case 6:
                company = "如风达";
                break;
            case 7:
                company = "天天";
                break;
            case 8:
                company = "一统飞鸿";
                break;
            case 9:
                company = "全峰";
                break;
            case 10:
                company = "唯品会";
                break;
            case 11:
                company = "优速";
                break;
            case 12:
                company = "德邦";
                break;
            default:
                company = "";

        }
        return company;
    }
}