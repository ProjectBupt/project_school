package com.tongxin.youni.receiver;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.avos.avoscloud.AVOSCloud;
import com.tongxin.youni.R;
import com.tongxin.youni.activity.UserInfoActivity;

import org.json.JSONObject;

/**
 * Created by lecoan on 2016/10/11.
 */

public class PushReceiver extends BroadcastReceiver {
    private static final String TAG = "PushReceiver------->";
    @Override
    public void onReceive(Context context, Intent intent) {
        if("com.lecoan.push.action".equals(intent.getAction())){
            try {
                JSONObject data = new JSONObject(intent.getExtras().getString("com.avos.avoscloud.Data"));
                String msg = data.getString("msg");
                Intent filpIntent = new Intent(context, UserInfoActivity.class);
                PendingIntent pending = PendingIntent
                        .getActivity(context,0,filpIntent,PendingIntent.FLAG_UPDATE_CURRENT);
                NotificationCompat.Builder mBuilder =
                        new NotificationCompat.Builder(AVOSCloud.applicationContext)
                                .setSmallIcon(R.mipmap.ic_launcher)
                                .setContentTitle(context.getResources().getString(R.string.app_name))
                                .setContentText(msg);
                mBuilder.setContentIntent(pending);
                mBuilder.setAutoCancel(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        else{
            Log.i(TAG, "onReceive: 有问题");
        }
    }
}
