package me.sunhapper.androidfpsmonitor.demo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import me.sunhapper.monitor.fps.FpsMonitor;
import me.sunhapper.monitor.fps.utils.PermissionUtil;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }


    public void showFloatWindow(View view) {
        if (PermissionUtil.checkOverlayPermission(this)) {
            FpsMonitor.getInstance().showFpsMonitorView(this);
        }
    }


    public void hideFloatWindow(View view) {
        FpsMonitor.getInstance().hideFpsMonitorView();
    }

    public void sleep(View view) {
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void sleepMultiTime(View view) {
        try {
            Thread.sleep(20);
            Thread.sleep(20);
            Thread.sleep(20);
            Thread.sleep(20);
            Thread.sleep(20);
            Thread.sleep(20);
            Thread.sleep(20);
            Thread.sleep(20);
            Thread.sleep(20);
            Thread.sleep(20);
            Thread.sleep(20);
            Thread.sleep(20);
            Thread.sleep(20);
            Thread.sleep(20);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void sleepLoop(View view) {
        try {
            for (int i = 0; i < 20; i++) {
                Thread.sleep(50);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
