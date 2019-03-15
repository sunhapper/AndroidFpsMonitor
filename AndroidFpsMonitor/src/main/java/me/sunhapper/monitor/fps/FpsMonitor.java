package me.sunhapper.monitor.fps;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.view.Display;
import android.view.FrameMetrics;
import android.view.Window;
import android.view.WindowManager;

import java.util.HashMap;
import java.util.Map;

import me.sunhapper.monitor.fps.callback.Callback;
import me.sunhapper.monitor.fps.contract.FpsContract;
import me.sunhapper.monitor.fps.presenter.FpsPresenter;

/**
 * Created by sunhapper on 2019/3/13 .
 */
public class FpsMonitor implements FpsContract.View {
    private static final String TAG = "FpsMonitor";
    @SuppressLint("StaticFieldLeak")
    private static FpsMonitor instance = new FpsMonitor();
    public static final long CHECK_DELAY = 600;
    private boolean foreground = true, paused = true;
    private Handler handler = new Handler();
    private Runnable check;
    private Map<String, Window.OnFrameMetricsAvailableListener> frameMetricsAvailableListenerMap = new HashMap<>();

    private FpsPresenter mFpsPresenter;
    private Callback<Integer> mFpsCallback;

    private FpsMonitor() {
        mFpsPresenter = new FpsPresenter();
        mFpsPresenter.takeView(this);
    }

    public static FpsMonitor getInstance() {
        return instance;
    }


    public FpsMonitor maxSkippedFrames(int max) {
        if (max > 0) {
            Config.MAX_SKIPPED_FRAME = max;
        }
        return this;
    }


    public FpsMonitor init(Application application) {
        try {
            Display display = ((WindowManager) application.getSystemService(
                    Context.WINDOW_SERVICE)).getDefaultDisplay();
            if (display.getRefreshRate() > 0) {
                Config.DISPLAY_REFRESH_RATE = (long) display.getRefreshRate();
                Config.VSYNC_PERIOD_NS = Config.calculateVsyncPeriodNs();
            }
        } catch (Exception ignored) {

        }
        return this;
    }


    public FpsMonitor startTraceFps() {
        mFpsPresenter.startTraceFps();
        return this;
    }

    public FpsMonitor stopTraceFps() {
        mFpsPresenter.stopTraceFps();
        return this;
    }

    public FpsMonitor resetFpsData() {
        mFpsPresenter.resetFpsData();
        return this;
    }

    public FpsMonitor setFpsCallback(Callback<Integer> fpsCallback) {
        mFpsCallback = fpsCallback;
        return this;
    }

    public long getLongestFrameDurationNs() {
        return mFpsPresenter.getLongestFrameDurationNs();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public FpsMonitor startTraceFrameStatsGlobal(Application application) {
        mFpsPresenter.startTraceFrameStatsGlobal(application);
        return this;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public FpsMonitor stopTraceFrameStatsGlobal(Application application) {
        mFpsPresenter.stopTraceFrameStatsGlobal(application);
        return this;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public FpsMonitor startTraceFrameStats(Activity activity) {
        mFpsPresenter.startTraceFrameStats(activity);
        return this;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public FpsMonitor stopTraceFrameStats(Activity activity) {
        mFpsPresenter.stopTraceFrameStats(activity);
        return this;
    }

    public int[] getSkippedFrameCounts() {
        return mFpsPresenter.getSkippedFrameCounts();
    }

    @Override
    public void updateFps(int fps) {
        if (mFpsCallback != null) {
            mFpsCallback.onResult(fps);
        }

    }

    @Override
    public void updateFrameStats(String activityName, FrameMetrics frameMetrics) {
        Log.i(TAG, "updateFrameStats: " + activityName + " " + frameMetrics);
    }

}
