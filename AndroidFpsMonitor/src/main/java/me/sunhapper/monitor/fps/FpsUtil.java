package me.sunhapper.monitor.fps;

import static me.sunhapper.monitor.fps.Constant.NS_PER_SECOND;

import java.util.List;

/**
 * Created by sunhapper on 2019/3/13 .
 */
public class FpsUtil {
    public static int sampleToFps(List<Long> samples) {
        if (samples == null || samples.size() < 1) {
            return 0;
        }
        long timeInNS = samples.get(samples.size() - 1) - samples.get(0);
        int size = samples.size();
        return Math.round((size - 1) * NS_PER_SECOND * 1f / timeInNS);
    }

}
