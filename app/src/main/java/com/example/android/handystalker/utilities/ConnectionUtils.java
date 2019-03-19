package com.example.android.handystalker.utilities;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.os.Build;
import android.widget.Toast;

import com.example.android.handystalker.R;
import com.example.android.handystalker.ui.MainActivity;

public class ConnectionUtils {

    private static ConnectionUtils instance = new ConnectionUtils();
    static Context context;
    boolean isConnected = false;

    public static ConnectionUtils getInstance(Context ctx) {
        context = ctx.getApplicationContext();
        return instance;
    }

    public boolean isConnected() {
        boolean isConnected = false;
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (cm != null) {
                NetworkCapabilities capabilities = cm.getNetworkCapabilities(cm.getActiveNetwork());
                if (capabilities != null) {
                    if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) || capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) {
                        isConnected = true;
                    }
                }
            }
        } else {
            if (cm != null) {
                NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
                if (activeNetwork != null) {
                    // connected to the internet
                    isConnected = (activeNetwork.isConnected() && (activeNetwork.getType() == ConnectivityManager.TYPE_WIFI || activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE));
                }
            }
        }
        return isConnected;
    }
}
