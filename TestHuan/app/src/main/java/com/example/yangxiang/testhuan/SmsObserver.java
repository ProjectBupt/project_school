package com.example.yangxiang.testhuan;

import android.content.Context;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.util.Log;

import com.example.yangxiang.testhuan.activity.RegisterActivity;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by yangxiang on 2016/9/13.
 */
public class SmsObserver extends ContentObserver {
    private Context mContext;
    private Handler mHandler;

    public SmsObserver(Context context, Handler handler) {
        super(handler);
        mContext = context;
        mHandler = handler;
    }

    @Override
    public void onChange(boolean selfChange, Uri uri) {
        super.onChange(selfChange, uri);

        Log.d("main", "SMS has changed!");
        Log.d("main", uri.toString());
        if (uri.toString().equals("content://sms/raw")) {
            return;
        }

        getValidateCode();

    }

    private void getValidateCode() {
        Uri inboxUri = Uri.parse("content://sms/inbox");
        Cursor c = mContext.getContentResolver().query(inboxUri, null, null, null, "date desc");//
        if (c != null) {
            if (c.moveToFirst()) {
                String address = c.getString(c.getColumnIndex("address"));
                String body = c.getString(c.getColumnIndex("body"));

//                if (!address.equals("06902476237")) {
//                    return;
//                }
                Log.d("main", "发件人为:" + address + " ," + "短信内容为:" + body);

                Pattern pattern = Pattern.compile("\\d{6}");
                Matcher matcher = pattern.matcher(body);

                if (matcher.find()) {
                    String code = matcher.group(0);
                    Log.d("main", "验证码为: " + code);
                    mHandler.obtainMessage(RegisterActivity.MSG_RECEIVED_CODE, code).sendToTarget();
                }

            }
            c.close();
        }
    }
}
