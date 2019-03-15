package me.sunhapper.monitor.fps;

import static me.sunhapper.monitor.fps.Constant.MS_PER_SECOND;
import static me.sunhapper.monitor.fps.Constant.NS_PER_MS;

/**
 * Created by sunhapper on 2019/3/13 .
 */
public class Config {
    static long DISPLAY_REFRESH_RATE = 60;
    public static long VSYNC_PERIOD_NS = calculateVsyncPeriodNs(); //以ms为精度向上取整
    public static int MAX_SKIPPED_FRAME = 20; //以ms为精度向下取整

    static long calculateVsyncPeriodNs() {
        return (MS_PER_SECOND / DISPLAY_REFRESH_RATE + 1) * NS_PER_MS;
    }
}
