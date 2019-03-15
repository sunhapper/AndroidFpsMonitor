package me.sunhapper.monitor.fps.presenter;

import android.app.Activity;
import android.app.Application;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.view.FrameMetrics;

import me.sunhapper.monitor.fps.callback.Callback;
import me.sunhapper.monitor.fps.callback.DoubleParamsCallback;
import me.sunhapper.monitor.fps.contract.FpsContract;
import me.sunhapper.monitor.fps.model.FpsModel;
import me.sunhapper.monitor.fps.model.FrameStatsModel;

/**
 * Created by sunhapper on 2019/3/15 .
 */
public class FpsPresenter implements FpsContract.Presenter, DoubleParamsCallback<String, FrameMetrics> {

    private FpsContract.View mView;
    private FpsModel mFpsModel;
    @RequiresApi(Build.VERSION_CODES.N)
    private FrameStatsModel mFrameStatsModel;


    public FpsPresenter() {
        mFpsModel = new FpsModel();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            mFrameStatsModel = new FrameStatsModel();
        }
    }

    @Override
    public void startTraceFps() {
        mFpsModel.startTraceFps(new Callback<Integer>() {
            @Override
            public void onResult(Integer result) {
                if (mView != null) {
                    mView.updateFps(result);
                }
            }
        });
    }

    @Override
    public void stopTraceFps() {
        mFpsModel.stopTraceFps();
    }

    @Override
    public void resetFpsData() {
        mFpsModel.reset();
    }

    @Override
    public int[] getSkippedFrameCounts() {
        return mFpsModel.getSkippedFrameCounts();
    }

    @Override
    public long getLongestFrameDurationNs() {
        return mFpsModel.getLongestFrameDurationNs();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void startTraceFrameStatsGlobal(Application application) {
        mFrameStatsModel.startGlobal(application, this);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void stopTraceFrameStatsGlobal(Application application) {
        mFrameStatsModel.stopGlobal(application);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void startTraceFrameStats(Activity activity) {
        mFrameStatsModel.start(activity, this);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void stopTraceFrameStats(Activity activity) {
        mFrameStatsModel.stop(activity);
    }

    @Override
    public void takeView(FpsContract.View view) {
        mView = view;
    }

    @Override
    public void dropView() {
        mView = null;
    }

    @Override
    public void onResult(String result1, FrameMetrics result2) {
        if (mView != null) {
            mView.updateFrameStats(result1, result2);
        }
    }
}
