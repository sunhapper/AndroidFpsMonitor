package me.sunhapper.monitor.fps.contract;

import android.app.Activity;
import android.app.Application;
import android.view.FrameMetrics;

import me.sunhapper.monitor.fps.presenter.BasePresenter;
import me.sunhapper.monitor.fps.view.BaseView;

/**
 * Created by sunhapper on 2019/3/15 .
 */
public interface FpsContract {

    interface View extends BaseView<Presenter> {
        void updateFps(int fps);

        void updateFrameStats(String activityName, FrameMetrics frameMetrics);

    }

    interface Presenter extends BasePresenter<View> {
        void startTraceFps();

        void stopTraceFps();

        void resetFpsData();

        int[] getSkippedFrameCounts();

        long getLongestFrameDurationNs();

        void startTraceFrameStatsGlobal(Application application);

        void stopTraceFrameStatsGlobal(Application application);

        void startTraceFrameStats(Activity activity);

        void stopTraceFrameStats(Activity activity);
    }
}
