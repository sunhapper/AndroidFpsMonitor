package me.sunhapper.monitor.fps;

import android.content.Context;
import android.util.Log;
import android.view.Display;
import android.view.WindowManager;

import java.util.List;

/**
 * Created by sunhapper on 2019/3/13 .
 */
public class FpsMonitor {
    private static final String TAG = "FpsMonitor";
    private FpsFrameSampler mFpsFrameSampler;
    private static FpsMonitor instance = new FpsMonitor();
    private FpsCallback mFpsCallback;
    private boolean enableLog = true;
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


    public FpsMonitor refreshRate(Context context) {
        try {
            Display display = ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
            if (display.getRefreshRate() > 0) {
                Config.DISPLAY_REFRESH_RATE = (long) display.getRefreshRate();
                Config.VSYNC_PERIOD_NS = Config.calculateVsyncPeriodNs();
            }

        } catch (Exception ignored) {

        }
        return this;
    }

    public FpsMonitor enableLog(boolean enable) {
        enableLog = enable;
        return this;
    }

    public void reset() {
        mFpsFrameSampler.reset();
    }

    public void start() {
        if (mFpsFrameSampler == null) {
            mFpsFrameSampler = new FpsFrameSampler();
            mFpsFrameSampler.setFrameSampleCallback(mFrameSampleCallback);
        }
        mFpsFrameSampler.start();
    }

    public void stop() {
        mFpsFrameSampler.stop();
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


}
