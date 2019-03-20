package me.sunhapper.androidfpsmonitor.demo;

import android.app.Application;
import android.os.Build;

import me.sunhapper.monitor.fps.FpsMonitor;
import me.sunhapper.monitor.fps.callback.Callback;

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
                .maxSkippedFrames(10)
                .setFpsCallback(new Callback<Integer>() {
                    @Override
                    public void onResult(Integer result) {
//                        StringBuilder builder = new StringBuilder();
//                        builder.append("Fps :").append(result);
//                        int[] skippedFrameCountList = FpsMonitor.getInstance().getSkippedFrameCounts();
//                        long longestFrameDurationNs = FpsMonitor.getInstance().getLongestFrameDurationNs();
//                        for (int i = 0; i < skippedFrameCountList.length; i++) {
//                            if (i == 0) {
//                                builder.append("  normal: ");
//                            } else {
//                                builder.append("  skip ").append(i).append(" frames: ");
//                            }
//                            builder.append(skippedFrameCountList[i]);
//                        }
//                        builder.append("  longestFrameDurationMs").append(0.000001 * longestFrameDurationNs);
//                        Log.i(TAG, builder.toString());
                    }
                })
                .startTraceFps();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            FpsMonitor.getInstance().startTraceFrameStatsGlobal(this);
        }
    }
}
