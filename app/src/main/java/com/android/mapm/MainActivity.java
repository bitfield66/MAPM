package com.android.mapm;

import android.app.ActivityManager;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.android.mapm.utils.LogUtils;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private ActivityManager activityManager;
    private MemoryMonitor memoryMonitor;
    private CPUMonitor cpuMonitor;
    private Button btnStats;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initializeControls();

        btnStats.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnStats.setClickable(false);
                btnStats.setEnabled(false);
                memoryMonitor.getDeviceRAMUsage();
                float cpuUsage = cpuMonitor.syncGetProcessCpuUsage(android.os.Process.myPid(), 10000);
                LogUtils.infoLog("CPUMonitor", "" + cpuUsage);
                Toast.makeText(MainActivity.this, "CPUMonitor" + cpuUsage, Toast.LENGTH_LONG).show();
                if(!isMyServiceRunning()){
                    startService(new Intent(MainActivity.this,NetworkMonitorService.class));
                }

                btnStats.setClickable(true);
                btnStats.setEnabled(true);
            }
        });
    }

    private void initializeControls() {
        activityManager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        memoryMonitor = new MemoryMonitor(activityManager, MainActivity.this);
        cpuMonitor = new CPUMonitor();
        btnStats = (Button) findViewById(R.id.btnStats);
    }

    @Override
    protected void onResume() {
        super.onResume();
//        if(!isMyServiceRunning()){
//            startService(new Intent(MainActivity.this,NetworkMonitorService.class));
//        }
    }

    private boolean isMyServiceRunning() {
        ActivityManager activityManager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo serviceInfo : activityManager.getRunningServices(Integer.MAX_VALUE)) {
            if (NetworkMonitorService.class.getName().equals(serviceInfo.service.getClassName())) {
                return true;
            }
        }
        return false;
    }
}
