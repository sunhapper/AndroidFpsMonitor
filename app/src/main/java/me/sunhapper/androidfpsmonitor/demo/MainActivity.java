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
}
