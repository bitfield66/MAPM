package com.android.mapm;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.TrafficStats;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.android.mapm.utils.LogUtils;

/**
 * Created by Vinay on 3/16/2016.
 */
public class NetworkMonitorService extends Service {

    private int appUID=-1;
    private long tRXMobileDataBytesDifference = 0;
    private long tRXWIFIBytesDifference = 0;

    public class NetworkStateChangeReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
                monitorConnection();
        }

    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        tRXMobileDataBytesDifference = 0;
        tRXWIFIBytesDifference = 0;
        for (ApplicationInfo app :
                getPackageManager().getInstalledApplications(0)) {
            if (app.packageName.contains("com.whatsapp")) {
                appUID = app.uid;
                break;
            }
        }

        monitorConnection();
    }

    public NetworkMonitorService() {
    }

    public void resetTraffic(boolean isMobileConnection, boolean isWIFIConnection) {

        if (isMobileConnection)
            tRXMobileDataBytesDifference = Math.abs(tRXMobileDataBytesDifference - (TrafficStats.getUidTxBytes(appUID) + TrafficStats.getUidRxBytes(appUID)));
        else if(isWIFIConnection)
            tRXWIFIBytesDifference = Math.abs(tRXWIFIBytesDifference - (TrafficStats.getUidTxBytes(appUID) + TrafficStats.getUidRxBytes(appUID)));

        LogUtils.errorLog("tRXMobileDataBytesDifference",""+tRXMobileDataBytesDifference);
        LogUtils.errorLog("tRXWIFIBytesDifference",""+tRXWIFIBytesDifference);
    }

    private void monitorConnection(){

        if(appUID!=-1){

            try {
                ConnectivityManager connMgr = (ConnectivityManager)
                        getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo networkInfo = connMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
                boolean isWifiConn = networkInfo.isConnected();
                networkInfo = connMgr.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
                boolean isMobileConn = networkInfo.isConnected();
                resetTraffic(isMobileConn, isWifiConn);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
