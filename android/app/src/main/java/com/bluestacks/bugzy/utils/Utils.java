package com.bluestacks.bugzy.utils;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.text.TextUtils;

import com.bluestacks.bugzy.models.resp.Case;

/**
 * Created by msharma on 19/06/17.
 */
public class Utils {

   public static boolean isOnline(Context context) {
       boolean haveConnectedWifi = false;
       boolean haveConnectedMobile = false;

       ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
       NetworkInfo[] netInfo = cm.getAllNetworkInfo();
       for (NetworkInfo ni : netInfo) {
           if (ni.getTypeName().equalsIgnoreCase("WIFI"))
               if (ni.isConnected())
                   haveConnectedWifi = true;
           if (ni.getTypeName().equalsIgnoreCase("MOBILE"))
               if (ni.isConnected())
                   haveConnectedMobile = true;
       }
       return haveConnectedWifi || haveConnectedMobile;
   }

   public static boolean isValidInput(String input) {
       return input != null && !TextUtils.isEmpty(input);
   }

    public static boolean isValidPassword(String input) {
        return input != null && !TextUtils.isEmpty(input);
    }
}
