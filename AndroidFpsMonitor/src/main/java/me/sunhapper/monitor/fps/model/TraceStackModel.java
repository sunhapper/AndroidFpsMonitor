package me.sunhapper.monitor.fps.model;

import static me.sunhapper.monitor.fps.Constant.TAG;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.util.Log;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by sunhapper on 2019/3/20 .
 */
public class TraceStackModel {
    private static final String SEPARATOR = "\r\n";
    private static final int DEFAULT_SAMPLE_INTERVAL = 50;
    private AtomicBoolean mRunning = new AtomicBoolean(false);
    private HandlerThread mStackThread;
    private Handler mStackHandler;
    final private Set<String> mStackSet = new LinkedHashSet<>();

    public TraceStackModel() {
        if (mStackThread == null) {
            mStackThread = new HandlerThread("TraceStackModel") {
                @Override
                protected void onLooperPrepared() {
                    mStackHandler = new Handler(mStackThread.getLooper());
                }
            };
            mStackThread.start();
        }
    }


    public ArrayList<String> getThreadStackEntries() {
        ArrayList<String> result;
        synchronized (mStackSet) {
            result = new ArrayList<>(mStackSet);
            for (String stack : result) {
                Log.i(TAG, stack);
            }
        }
        return result;
    }

    public void restartDump() {
        synchronized (mStackSet) {
            mStackSet.clear();
        }
        startDump();
    }

    public void startDump() {
        if (mStackHandler == null) {
            return;
        }
        if (mRunning.get()) {
            return;
        }
        mRunning.set(true);
        mStackHandler.removeCallbacks(mRunnable);
        mStackHandler.postDelayed(mRunnable, DEFAULT_SAMPLE_INTERVAL);
    }


    public void stopDump() {
        if (mStackHandler == null) {
            return;
        }
        if (!mRunning.get()) {
            return;
        }
        synchronized (mStackSet) {
            mStackSet.clear();
        }
        mRunning.set(false);
        mStackHandler.removeCallbacks(mRunnable);
    }

    public void shutDown() {
        stopDump();
        if (mStackThread != null) {
            mStackThread.quit();
        }
    }


    private Runnable mRunnable = new Runnable() {
        @Override
        public void run() {
            dumpInfo();
            if (mRunning.get()) {
                mStackHandler.postDelayed(mRunnable, DEFAULT_SAMPLE_INTERVAL);
            }
        }
    };

    private void dumpInfo() {
        StringBuilder stringBuilder = new StringBuilder();
        Thread thread = Looper.getMainLooper().getThread();
        for (StackTraceElement stackTraceElement : thread.getStackTrace()) {
            stringBuilder
                    .append(stackTraceElement.toString())
                    .append(SEPARATOR);
        }

        synchronized (mStackSet) {
            mStackSet.add(stringBuilder.toString());
        }
    }
}
