package com.tongxin.youni.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Created by yangxiang on 2016/10/2.
 */

public class NetStateReceiver extends BroadcastReceiver {
    private static boolean wifiState;
    private static boolean mobileState;
    public static final String ACTION = "android.net.conn.CONNECTIVITY_CHANGE";

    @Override
    public void onReceive(Context context, Intent intent) {
        if(ACTION.equals(intent.getAction())){
            ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo activeInfo = cm.getActiveNetworkInfo();
            if(activeInfo != null){
                if(activeInfo.getType() == ConnectivityManager.TYPE_WIFI){
                    wifiState = true;
                    mobileState = false;
                }
                else if(activeInfo.getType() == ConnectivityManager.TYPE_MOBILE){
                    wifiState = false;
                    mobileState = true;
                }
            }
            else{
                wifiState = false;
                mobileState = false;
            }
        }
    }

    public static boolean isConnected(){

        return wifiState||mobileState;
    }
}
