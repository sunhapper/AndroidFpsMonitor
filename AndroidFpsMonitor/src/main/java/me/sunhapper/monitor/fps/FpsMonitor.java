package me.sunhapper.monitor.fps;

import static android.view.FrameMetrics.TOTAL_DURATION;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.graphics.PixelFormat;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.view.Display;
import android.view.FrameMetrics;
import android.view.Gravity;
import android.view.WindowManager;

import me.sunhapper.monitor.fps.callback.Callback;
import me.sunhapper.monitor.fps.contract.FpsContract;
import me.sunhapper.monitor.fps.presenter.FpsPresenter;
import me.sunhapper.monitor.fps.utils.PermissionUtil;
import me.sunhapper.monitor.fps.view.FpsMonitorView;
import me.sunhapper.monitor.fps.view.TouchListener;

/**
 * Created by sunhapper on 2019/3/13 .
 */
public class FpsMonitor implements FpsContract.View {
    private static final String TAG = "FpsMonitor";
    @SuppressLint("StaticFieldLeak")
    private static FpsMonitor instance = new FpsMonitor();
    private FpsPresenter mFpsPresenter;
    private Callback<Integer> mFpsCallback;
    private FpsMonitorView fpsMonitorView;
    private WindowManager windowManager;

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
        if (fpsMonitorView != null) {
            fpsMonitorView.setFpsInfo(String.valueOf(fps));
        }

    }

    @Override
    public void updateFrameStats(String activityName, FrameMetrics frameMetrics) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            if (fpsMonitorView != null) {
                fpsMonitorView.setFrameStatsInfo(activityName + " " + frameMetrics.getMetric(TOTAL_DURATION));
            }
            Log.i(TAG, "updateFrameStats: " + activityName + " " + frameMetrics);
        }
    }

    public void showFpsMonitorView(Context context) {
        if (fpsMonitorView == null) {
            windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
            fpsMonitorView = new FpsMonitorView(context);
            fpsMonitorView.setFpsMonitorViewListener(new FpsMonitorView.FpsMonitorViewListener() {
                @Override
                public void onClose() {
                    fpsMonitorView = null;
                }
            });
            final WindowManager.LayoutParams params = new WindowManager.LayoutParams();
            params.type = PermissionUtil.getSupportParamType();
            int flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
            params.flags = flags;
            params.format = PixelFormat.TRANSLUCENT;
            params.width = WindowManager.LayoutParams.WRAP_CONTENT;
            params.height = WindowManager.LayoutParams.WRAP_CONTENT;
            params.gravity = Gravity.TOP | Gravity.START;
            fpsMonitorView.setOnTouchListener(new TouchListener(params, windowManager));
            windowManager.addView(fpsMonitorView, params);
        }

    }

    public void hideFpsMonitorView() {
        if (fpsMonitorView != null) {
            windowManager.removeViewImmediate(fpsMonitorView);
            fpsMonitorView = null;
        }

    }

}
