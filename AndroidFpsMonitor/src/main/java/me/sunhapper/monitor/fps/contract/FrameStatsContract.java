package me.sunhapper.monitor.fps.contract;

import android.app.Activity;
import android.app.Application;
import android.view.FrameMetrics;

import me.sunhapper.monitor.fps.presenter.BasePresenter;
import me.sunhapper.monitor.fps.view.BaseView;

/**
 * Created by sunhapper on 2019/3/15 .
 */
public interface FrameStatsContract {


    interface View extends BaseView<Presenter> {
        void updateFrameStats(String activityName, int fps, FrameMetrics frameMetrics);
    }

    interface Presenter extends BasePresenter<View> {
        void startGlobal(Application application);

        void stopGlobal(Application application);

        void start(Activity activity);

        void stop(Activity activity);
    }
}
