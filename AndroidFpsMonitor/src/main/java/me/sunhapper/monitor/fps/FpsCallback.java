package me.sunhapper.monitor.fps;

/**
 * Created by sunhapper on 2019/3/13 .
 */
public interface FpsCallback {
    void onFps(int fps, int[] skippedFrameCountList, long longestFrameDurationNs);
}
