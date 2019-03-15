package me.sunhapper.androidfpsmonitor.demo;

import android.app.Application;
import android.util.Log;

import me.sunhapper.monitor.fps.FpsCallback;
import me.sunhapper.monitor.fps.FpsMonitor;

/**
 * Created by sunhapper on 2019/3/14 .
 */
public class FpsApplication extends Application {
    private static final String TAG = "FpsApplication";

    @Override
    public void onCreate() {
        super.onCreate();
        FpsMonitor.getInstance()
                .init(this)
                .enableLog(BuildConfig.DEBUG)
                .maxSkippedFrames(10)
                .setFpsCallback(new FpsCallback() {
                    @Override
                    public void onFps(int fps, int[] skippedFrameCountList, long longestFrameDurationNs) {
                        StringBuilder builder = new StringBuilder();
                        builder.append("Fps :").append(fps);
                        for (int i = 0; i < skippedFrameCountList.length; i++) {
                            if (i == 0) {
                                builder.append("  normal: ");
                            } else {
                                builder.append("  skip ").append(i).append(" frames: ");
                            }
                            builder.append(skippedFrameCountList[i]);
                        }
                        builder.append("  longestFrameDurationMs").append(longestFrameDurationNs / 1000 / 1000);
                        Log.i(TAG, builder.toString());
                    }
                })
                .start();
    }
}
