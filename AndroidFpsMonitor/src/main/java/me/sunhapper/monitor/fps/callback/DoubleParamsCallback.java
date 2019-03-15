package me.sunhapper.monitor.fps.callback;

/**
 * Created by sunhapper on 2019/3/15 .
 */
public interface DoubleParamsCallback<T, R> {
    void onResult(T result1, R result2);
}