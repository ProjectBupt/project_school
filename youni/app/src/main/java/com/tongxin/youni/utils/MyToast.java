package com.tongxin.youni.utils;

import android.content.Context;
import android.widget.Toast;

/**
 * Created by charlene on 2017/2/26.
 */

public class MyToast {

    private static Toast toast;

    public static void showToast(Context context,String content) {
        if (toast==null){
            toast=Toast.makeText(context,content,Toast.LENGTH_SHORT);
        }else{
            toast.setText(content);
        }
        toast.show();
    }
}
