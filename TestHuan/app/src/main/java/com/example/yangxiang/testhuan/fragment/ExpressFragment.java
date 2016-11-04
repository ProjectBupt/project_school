package com.example.yangxiang.testhuan.fragment;


import android.app.Activity;
import android.content.Intent;
import android.content.res.XmlResourceParser;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarDrawerToggle;
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
import android.widget.ToggleButton;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.FindCallback;
import com.avos.avoscloud.GetCallback;
import com.avos.avoscloud.SaveCallback;
import com.example.yangxiang.testhuan.R;
import com.example.yangxiang.testhuan.activity.ItemDetailActivity;
import com.example.yangxiang.testhuan.activity.MainActivity;
import com.example.yangxiang.testhuan.activity.PostActivity;
import com.example.yangxiang.testhuan.activity.PostFeed;
import com.example.yangxiang.testhuan.activity.Screen;
import com.example.yangxiang.testhuan.adapter.MyListViewAdapter;
import com.example.yangxiang.testhuan.bean.Express;
import com.example.yangxiang.testhuan.bean.ExpressDao;
import com.example.yangxiang.testhuan.bean.User;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class ExpressFragment extends Fragment
        implements MyListViewAdapter.RemoveItem, SwipeRefreshLayout.OnRefreshListener {

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

    private Handler mHandler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case REFRESH_COMPLETE:
                    adapter.notifyDataSetChanged();
                    Toast.makeText(getActivity(), "更新完成,棒棒哒~~", Toast.LENGTH_SHORT).show();
                    refreshLayout.setRefreshing(false);
                    break;
            }
        }
    };
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
        mData=getData();
        adapter=new MyListViewAdapter(getActivity(),mData);
        adapter.setRemoveItem(this);
        listView.setAdapter(adapter);

        mAddButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent postIntent = new Intent(getActivity(), PostFeed.class);
                startActivityForResult(postIntent,POST_EXPRESS);
            }
        });

        AppCompatActivity activity = ((MainActivity)getActivity());
        activity.setSupportActionBar(mToolbar);
        activity.getSupportActionBar().setDisplayShowTitleEnabled(false);

        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if(mLastFirstVisibleItem<firstVisibleItem)
                {
                    Log.i("SCROLLING DOWN","TRUE");
                    mAddButton.hide();
                }
                if(mLastFirstVisibleItem>firstVisibleItem)
                {
                    Log.i("SCROLLING UP","TRUE");
                    mAddButton.show();
                }
                mLastFirstVisibleItem=firstVisibleItem;
            }
        });

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == POST_EXPRESS){
            if(resultCode == Activity.RESULT_OK){
                refreshLayout.setRefreshing(true);
                mData=getData();
                mHandler.sendEmptyMessageDelayed(REFRESH_COMPLETE,2000);
            }
        }
        if(requestCode == SCREEN_EXPRESS){
            if(resultCode == Activity.RESULT_OK){
                refreshLayout.setRefreshing(true);
                Bundle extra = data.getExtras();
                int building = extra.getInt("Building");
                final int exCom = extra.getInt("Express");
                String commpany;
                AVQuery<Express> query1 = new AVQuery<>("Express");
                switch (exCom){
                    case 0:
                        commpany = "圆通";
                    break;
                    case 1:
                        commpany = "中通";
                    break;
                    case 2:
                        commpany = "申通";
                    break;
                    case 3:
                        commpany = "韵达";
                    break;
                    case 4:
                        commpany = "京东";
                    break;
                    case 5:
                        commpany = "顺丰";
                    break;
                    case 6:
                        commpany = "如风达";
                    break;
                    case 7:
                        commpany = "天天";
                    break;
                    case 8:
                        commpany = "一统飞鸿";
                    break;
                    case 9:
                        commpany = "全峰";
                    break;
                    default:
                        commpany = "";

                }
                query1.whereEqualTo(ExpressDao.company,commpany);
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
                            Log.e(TAG, "done: "+e.getMessage());
                        }
                    }
                });
            }
        }
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
                    Toast.makeText(getActivity(), "boom", Toast.LENGTH_SHORT).show();
                }
            }
        });
        return mData;
    }

    @Override
    public void Skip(final String ID) {
        Log.i(TAG, "Skip: jin lai la");
        AVQuery<Express> data = new AVQuery<>("Express");
        data.getInBackground(ID, new GetCallback<Express>() {
            @Override
            public void done(Express express, AVException e) {
                if(e == null) {
                    if(express.getState() != ExpressDao.isWaiting) {
                        Toast.makeText(getActivity(), "已经被领取咯", Toast.LENGTH_SHORT).show();
                    }
                    //!express.getUserID().equals(User.getCurrentUser(User.class).getObjectId())
                    else if(1+1 == 2){
                        express.setState(ExpressDao.isTaking);
                        express.saveInBackground(new SaveCallback() {
                            @Override
                            public void done(AVException e) {
                                if(e == null) {
                                    Intent intent = new Intent(getActivity(), ItemDetailActivity.class);
                                    intent.putExtra("ExpressID",ID);
                                    startActivity(intent);
                                }
                                else{
                                    Log.i(TAG, "done: "+e.getMessage());
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
        mData=getData();
        mHandler.sendEmptyMessageDelayed(REFRESH_COMPLETE,2000);
    }
}
