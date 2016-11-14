package com.tongxin.youni;

import android.app.Application;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.avos.avoscloud.AVOSCloud;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVUser;
import com.tongxin.youni.bean.Express;
import com.tongxin.youni.bean.User;

/**
 * Created by yangxiang on 2016/9/12.
 */
public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        AVUser.alwaysUseSubUserClass(User.class);
        AVObject.registerSubclass(Express.class);
        AVOSCloud.initialize(this,"IOlLaA1POAOgTtwbEzw47Njj-gzGzoHsz","O2Y2h3y0XMNseivaAMhYq9uP");
    }
}
