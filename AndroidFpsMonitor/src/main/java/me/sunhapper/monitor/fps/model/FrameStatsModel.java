package me.sunhapper.monitor.fps.model;


import android.app.Activity;
import android.app.Application;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.IntDef;
import android.support.annotation.RequiresApi;
import android.view.FrameMetrics;
import android.view.Window;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import me.sunhapper.monitor.fps.callback.DoubleParamsCallback;

/**
 * Created by sunhapper on 2019/3/15 .
 */
@RequiresApi(Build.VERSION_CODES.N)
public class FrameStatsModel implements Application.ActivityLifecycleCallbacks,
        Window.OnFrameMetricsAvailableListener {
    private String currentActivityName;
    private DoubleParamsCallback<String, FrameMetrics> mCallback;
    @Mode
    private int mMode = Mode.IDLE;

    public void startGlobal(Application application, DoubleParamsCallback<String, FrameMetrics> frameMetricsCallback) {
        if (mMode != Mode.IDLE) {
            return;
        }
        mMode = Mode.GLOBAL;
        mCallback = frameMetricsCallback;
        application.registerActivityLifecycleCallbacks(this);

    }

    public void stopGlobal(Application application) {
        if (mMode != Mode.GLOBAL) {
            return;
        }
        application.unregisterActivityLifecycleCallbacks(this);
        mMode = Mode.IDLE;
    }

    public void start(Activity activity, DoubleParamsCallback<String, FrameMetrics> frameMetricsCallback) {
        if (mMode != Mode.IDLE) {
            return;
        }
        mMode = Mode.SINGLE;
        mCallback = frameMetricsCallback;
        activity.getWindow().addOnFrameMetricsAvailableListener(this, new Handler());
    }

    public void stop(Activity activity) {
        if (mMode != Mode.SINGLE) {
            return;
        }
        mMode = Mode.IDLE;
    }

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {

    }

    @Override
    public void onActivityStarted(Activity activity) {

    }

    @Override
    public void onActivityResumed(Activity activity) {
        currentActivityName = activity.getClass().getSimpleName();
        activity.getWindow().addOnFrameMetricsAvailableListener(this, new Handler());
    }

    @Override
    public void onActivityPaused(Activity activity) {
        activity.getWindow().removeOnFrameMetricsAvailableListener(this);
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

    @Override
    public void onFrameMetricsAvailable(Window window, FrameMetrics frameMetrics, int dropCountSinceLastInvocation) {
        if (mMode == Mode.IDLE) {
            window.removeOnFrameMetricsAvailableListener(this);
            return;
        }
        if (mCallback != null) {
            mCallback.onResult(currentActivityName, frameMetrics);
        }
    }

    @IntDef( {
            Mode.IDLE, Mode.GLOBAL, Mode.SINGLE
    })
    @Retention(RetentionPolicy.SOURCE)
    private @interface Mode {
        int IDLE = 0;
        int GLOBAL = 1;
        int SINGLE = 2;
    }
}
