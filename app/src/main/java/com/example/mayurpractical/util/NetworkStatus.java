package com.example.mayurpractical.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Created by peacock on 21/1/17.
 */

public class NetworkStatus {

    //Internet Type
    private static int TYPE_WIFI = 1;

    private static int TYPE_MOBILE = 2;

    private static int TYPE_NOT_CONNECTED = 0;

    public static boolean getConnectivityStatusString(Context context) {

        int connection = getConnectivityStatus(context);

        boolean status = false;

        if (connection == TYPE_WIFI || connection == TYPE_MOBILE || connection !=
                TYPE_NOT_CONNECTED) {

            status = true;

        } else {

            status = false;

        }

        return status;

    }

    public static int getConnectivityStatus(Context context) {

        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.
                CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        if (null != activeNetwork && activeNetwork.isConnectedOrConnecting()) {

            if (activeNetwork.getType() == ConnectivityManager.TYPE_WIFI)
                return TYPE_WIFI;

            if (activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE)
                return TYPE_MOBILE;

        }

        return TYPE_NOT_CONNECTED;

    }
}
