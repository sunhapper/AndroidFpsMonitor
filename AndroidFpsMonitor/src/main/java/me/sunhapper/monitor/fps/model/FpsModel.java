package me.sunhapper.monitor.fps.model;

import static me.sunhapper.monitor.fps.Config.MAX_SKIPPED_FRAME;

import android.view.Choreographer;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import me.sunhapper.monitor.fps.Config;
import me.sunhapper.monitor.fps.utils.FpsUtil;
import me.sunhapper.monitor.fps.callback.Callback;

/**
 * Created by sunhapper on 2019/3/15 .
 */
public class FpsModel implements Choreographer.FrameCallback {


    /**
     * fps采样周期
     */
    private static final long SAMPLE_PERIOD_IN_NS = TimeUnit.NANOSECONDS.convert(100, TimeUnit.MILLISECONDS);
    private long frameStartInNs = 0;
    private long lastFrameTimeNanos = 0;
    private boolean stop;
    private List<Long> frameTimeNsList = new ArrayList<>();
    private Callback<Integer> fpsCallback;
    private int[] skippedFrames = new int[MAX_SKIPPED_FRAME];
    private long longestFrameDurationNs = 0;
    private Callback<Long> frameDurationCallback;

    @Override
    public void doFrame(long frameTimeNanos) {
        if (stop) {
            clear();
            return;
        }
        if (frameStartInNs == 0) {
            frameStartInNs = frameTimeNanos;
            lastFrameTimeNanos = frameTimeNanos;
        } else {
            long diffNs = frameTimeNanos - lastFrameTimeNanos;
            if (frameDurationCallback != null) {
                frameDurationCallback.onResult(diffNs);
            }
            lastFrameTimeNanos = frameTimeNanos;
            int skippedFrame = (int) (diffNs / Config.VSYNC_PERIOD_NS);
            if (skippedFrame > MAX_SKIPPED_FRAME - 1) {
                skippedFrame = MAX_SKIPPED_FRAME - 1;
            }
            skippedFrames[skippedFrame]++;
            if (diffNs > longestFrameDurationNs) {
                longestFrameDurationNs = diffNs;
            }
        }
        if (beyondSamplePeriod(frameTimeNanos)) {
            List<Long> frameNsSampleTemp = new ArrayList<>(frameTimeNsList);
            int fps = FpsUtil.sampleToFps(frameNsSampleTemp);
            if (fpsCallback != null) {
                fpsCallback.onResult(fps);
            }
            frameTimeNsList.clear();
            frameStartInNs = frameTimeNanos;
        }
        frameTimeNsList.add(frameTimeNanos);
        Choreographer.getInstance().postFrameCallback(this);
    }

    private boolean beyondSamplePeriod(long frameTimeNanos) {
        return frameTimeNanos - frameStartInNs > SAMPLE_PERIOD_IN_NS;
    }

    private void clear() {
        frameTimeNsList.clear();
        frameStartInNs = 0;
        lastFrameTimeNanos = 0;
    }

    public long getLongestFrameDurationNs() {
        return longestFrameDurationNs;
    }

    public int[] getSkippedFrameCounts() {
        return skippedFrames;
    }

    public void startTraceFps(Callback<Integer> callback) {
        stop = false;
        fpsCallback = callback;
        Choreographer.getInstance().postFrameCallback(this);
    }

    public void stopTraceFps() {
        this.stop = true;
    }

    public void reset() {
        for (int i = 0; i < skippedFrames.length; i++) {
            skippedFrames[i] = 0;
        }
        longestFrameDurationNs = 0;
    }

    public void setFrameDurationCallback(Callback<Long> frameDurationCallback) {
        this.frameDurationCallback = frameDurationCallback;
    }
}
