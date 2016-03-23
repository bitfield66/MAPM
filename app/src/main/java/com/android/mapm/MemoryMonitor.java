package com.android.mapm;

import android.app.ActivityManager;
import android.content.Context;
import android.os.Debug;
import android.util.Log;

import com.android.mapm.utils.LogUtils;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;

/**
 * Created by Vinay on 3/16/2016.
 */
public class MemoryMonitor {

    private ActivityManager activityManager;
    private Context mContext;
    private String TAG = "MemoryMonitor";

    public MemoryMonitor(ActivityManager activityManager, Context mContext) {
        this.activityManager = activityManager;
        this.mContext = mContext;
    }

    public void getDeviceRAMUsage() {

        ActivityManager.MemoryInfo mi = new ActivityManager.MemoryInfo();
        activityManager.getMemoryInfo(mi);
        LogUtils.infoLog("totalMem", mi.totalMem / 1048576L + "");
        LogUtils.infoLog("availMem", mi.availMem / 1048576L + "");
        LogUtils.infoLog("threshold", mi.threshold / 1048576L + "");
        LogUtils.infoLog("lowMemory", mi.lowMemory + "");
        getNativeMemory();
    }

    public void getNativeMemory() {
        LogUtils.infoLog("Native totalMemory", Runtime.getRuntime().totalMemory() + "");
        LogUtils.infoLog("Native HeapMemory", Debug.getNativeHeapAllocatedSize() + "");
        LogUtils.infoLog("Native freeeMemory", Runtime.getRuntime().freeMemory() + "");
        int pids[] = new int[1];
        pids[0] = android.os.Process.myPid();
        android.os.Debug.MemoryInfo[] memoryInfoArray = activityManager.getProcessMemoryInfo(pids);
        for (android.os.Debug.MemoryInfo pidMemoryInfo : memoryInfoArray) {
            LogUtils.infoLog(TAG, " pidMemoryInfo.getTotalPrivateDirty(): " + pidMemoryInfo.getTotalPrivateDirty() + "\n");
            LogUtils.infoLog(TAG, " pidMemoryInfo.getTotalPss(): " + pidMemoryInfo.getTotalPss() + "\n");
            LogUtils.infoLog(TAG, " pidMemoryInfo.getTotalSharedDirty(): " + pidMemoryInfo.getTotalSharedDirty() + "\n");
        }
    }


}
