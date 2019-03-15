package me.sunhapper.monitor.fps;

import static me.sunhapper.monitor.fps.Config.MAX_SKIPPED_FRAME;

import android.view.Choreographer;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created by sunhapper on 2019/3/13 .
 */
class FpsFrameSampler implements Choreographer.FrameCallback {
    /**
     * fps采样周期
     */
    private static final long SAMPLE_PERIOD_IN_NS = TimeUnit.NANOSECONDS.convert(100, TimeUnit.MILLISECONDS);
    private long frameStartInNs = 0;
    private long lastFrameTimeNanos = 0;
    private boolean stop;
    private List<Long> frameTimeNsList = new ArrayList<>();
    private FrameSampleCallback frameSampleCallback;
    private int[] skippedFrames = new int[MAX_SKIPPED_FRAME];
    private long longestFrameDurationNs = 0;

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
            if (frameSampleCallback != null) {
                frameSampleCallback.onReceiveFrameSample(frameNsSampleTemp);
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

    void start() {
        stop = false;
        Choreographer.getInstance().postFrameCallback(this);
    }

    void stop() {
        this.stop = true;
    }

    private void clear() {
        frameTimeNsList.clear();
        frameStartInNs = 0;
        lastFrameTimeNanos = 0;
    }

    void setFrameSampleCallback(FrameSampleCallback frameSampleCallback) {
        this.frameSampleCallback = frameSampleCallback;
    }

    void reset() {
        for (int i = 0; i < skippedFrames.length; i++) {
            skippedFrames[i] = 0;
        }
        longestFrameDurationNs = 0;
    }

    long getLongestFrameDurationNs() {
        return longestFrameDurationNs;
    }

    int[] getSkippedFrames() {
        return skippedFrames;
    }
}
