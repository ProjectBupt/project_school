package com.tongxin.youni;

import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Process;
import android.util.Log;

import com.avos.avoscloud.AVOSCloud;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVUser;
import com.tongxin.youni.bean.Express;
import com.tongxin.youni.bean.User;
import com.xiaomi.mipush.sdk.MiPushClient;

import java.util.List;

/**
 * Created by yangxiang on 2016/9/12.
 */
public class MyApplication extends Application {

    public static final String APP_ID = "2882303761517527140";
    public static final String APP_KEY = "5141752750140";
    public static final String SECRUIT_CODE = "hX1lI0V8NCYaGXbdNI/tPw==";
    private static final String TAG = "App--->";

    @Override
    public void onCreate() {
        super.onCreate();
        AVUser.alwaysUseSubUserClass(User.class);
        AVObject.registerSubclass(Express.class);
        AVOSCloud.initialize(this,"IOlLaA1POAOgTtwbEzw47Njj-gzGzoHsz","O2Y2h3y0XMNseivaAMhYq9uP");

        if(shouldInit()) {
            MiPushClient.registerPush(this, APP_ID, APP_KEY);
            Log.i(TAG, "onCreate: init");
        }
    }

    private boolean shouldInit() {
        ActivityManager am = ((ActivityManager) getSystemService(Context.ACTIVITY_SERVICE));
        List<ActivityManager.RunningAppProcessInfo> processInfos = am.getRunningAppProcesses();
        String mainProcessName = getPackageName();
        int myPid = Process.myPid();
        for (ActivityManager.RunningAppProcessInfo info : processInfos) {
            if (info.pid == myPid && mainProcessName.equals(info.processName)) {
                return true;
            }
        }
        return false;
    }

    public boolean isConnected(){

        ConnectivityManager cm = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeInfo = cm.getActiveNetworkInfo();
        if(activeInfo != null){
            if(activeInfo.getType() == ConnectivityManager.TYPE_WIFI){
                return true;
            }
            else if(activeInfo.getType() == ConnectivityManager.TYPE_MOBILE){
                return true;
            }
        }
        else{
            return false;
        }
        return true;
    }
}
