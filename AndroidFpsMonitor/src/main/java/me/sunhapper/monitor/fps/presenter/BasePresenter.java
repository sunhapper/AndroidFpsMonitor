package me.sunhapper.monitor.fps.presenter;

import me.sunhapper.monitor.fps.view.BaseView;

/**
 * Created by sunhapper on 2019/3/15 .
 */
public interface BasePresenter<T extends BaseView> {
    void takeView(T view);

    void dropView();
}
