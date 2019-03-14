package me.sunhapper.monitor.fps;

import java.util.List;

/**
 * Created by sunhapper on 2019/3/13 .
 */
public interface FrameSampleCallback {
    void onReceiveFrameSample(List<Long> samples);
}
