package me.sunhapper.monitor.fps;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Display;
import android.view.FrameMetrics;
import android.view.Window;
import android.view.WindowManager;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by sunhapper on 2019/3/13 .
 */
public class FpsMonitor implements Application.ActivityLifecycleCallbacks {
    private static final String TAG = "FpsMonitor";
    private FpsFrameSampler mFpsFrameSampler;
    @SuppressLint("StaticFieldLeak")
    private static FpsMonitor instance = new FpsMonitor();
    private FpsCallback mFpsCallback;
    private boolean enableLog = true;
    public static final long CHECK_DELAY = 600;
    private boolean foreground = true, paused = true;
    private Handler handler = new Handler();
    private Runnable check;
    private Map<String, Window.OnFrameMetricsAvailableListener> frameMetricsAvailableListenerMap = new HashMap<>();
    private FrameSampleCallback mFrameSampleCallback = new FrameSampleCallback() {
        @Override
        public void onReceiveFrameSample(List<Long> samples) {
            int fps = FpsUtil.sampleToFps(samples);
            if (mFpsCallback != null) {
                mFpsCallback.onFps(fps, getSkippedFrames(), getLongestFrameDurationNs());
            }
            if (enableLog) {
                Log.d(TAG, "FPS: " + fps);
            }
        }
    };

    private FpsMonitor() {

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
        application.registerActivityLifecycleCallbacks(this);
        return this;
    }


    public FpsMonitor enableLog(boolean enable) {
        enableLog = enable;
        return this;
    }

    public void reset() {
        if (mFpsFrameSampler != null) {
            mFpsFrameSampler.reset();
        }
    }

    public void start() {
        if (mFpsFrameSampler == null) {
            mFpsFrameSampler = new FpsFrameSampler();
            mFpsFrameSampler.setFrameSampleCallback(mFrameSampleCallback);
        }
        mFpsFrameSampler.start();
    }

    public void stop() {
        if (mFpsFrameSampler != null) {
            mFpsFrameSampler.stop();
        }
    }

    public FpsMonitor setFpsCallback(FpsCallback fpsCallback) {
        mFpsCallback = fpsCallback;
        return this;
    }

    public long getLongestFrameDurationNs() {
        return mFpsFrameSampler.getLongestFrameDurationNs();
    }

    public int[] getSkippedFrames() {
        return mFpsFrameSampler.getSkippedFrames();
    }


    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {

    }

    @Override
    public void onActivityStarted(Activity activity) {

    }

    @Override
    public void onActivityResumed(Activity activity) {
        paused = false;
        boolean wasBackground = !foreground;
        foreground = true;

        if (check != null) {
            handler.removeCallbacks(check);
        }

        if (wasBackground) {
            Log.i(TAG, "went foreground");
        } else {
            Log.i(TAG, "still foreground");
        }
        startFrameMetrics(activity);
    }

    @Override
    public void onActivityPaused(Activity activity) {
        paused = true;

        if (check != null) {
            handler.removeCallbacks(check);
        }

        handler.postDelayed(check = new Runnable() {
            @Override
            public void run() {
                if (foreground && paused) {
                    foreground = false;
                    Log.i(TAG, "went background");
                } else {
                    Log.i(TAG, "still foreground");
                }
            }
        }, CHECK_DELAY);
        stopFrameMetrics(activity);
    }

    @Override
    public void onActivityStopped(Activity activity) {
    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

    }

    @Override
    public void onActivityDestroyed(Activity activity) {

    }


    @TargetApi(Build.VERSION_CODES.N)
    private void startFrameMetrics(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            final String activityName = activity.getClass().getSimpleName();
            Window.OnFrameMetricsAvailableListener listener = new Window.OnFrameMetricsAvailableListener() {


                @TargetApi(Build.VERSION_CODES.O)
                @Override
                public void onFrameMetricsAvailable(Window window, FrameMetrics frameMetrics,
                        int dropCountSinceLastInvocation) {

                }
            };
            activity.getWindow().addOnFrameMetricsAvailableListener(listener, new Handler());
            frameMetricsAvailableListenerMap.put(activityName, listener);
        } else {
            Log.w("FrameMetrics", "FrameMetrics can work only with Android SDK 24 (Nougat) and higher");
        }
    }

    @TargetApi(Build.VERSION_CODES.N)
    private void stopFrameMetrics(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            String activityName = activity.getClass().getName();
            Window.OnFrameMetricsAvailableListener onFrameMetricsAvailableListener =
                    frameMetricsAvailableListenerMap.get(activityName);
            if (onFrameMetricsAvailableListener != null) {
                activity.getWindow().removeOnFrameMetricsAvailableListener(onFrameMetricsAvailableListener);
                frameMetricsAvailableListenerMap.remove(activityName);
            }
        }
    }

}
