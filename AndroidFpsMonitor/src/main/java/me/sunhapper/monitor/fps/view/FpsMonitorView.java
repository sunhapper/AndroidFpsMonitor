package me.sunhapper.monitor.fps.view;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

import me.sunhapper.monitor.fps.R;

/**
 * Created by sunhapper on 2019/3/18 .
 */
public class FpsMonitorView extends FrameLayout {

    private Button mBtnClose;
    private TextView mTvFrameStats;
    private TextView mTvFpsInfo;
    private FpsMonitorViewListener mFpsMonitorViewListener;

    public FpsMonitorView(@NonNull final Context context) {
        super(context);
        LayoutInflater.from(context).inflate(R.layout.layout_fps_monitor, this, true);
        initView();
        mBtnClose.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
                    windowManager.removeViewImmediate(FpsMonitorView.this);
                    if (mFpsMonitorViewListener != null) {
                        mFpsMonitorViewListener.onClose();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });
    }

    public void setFpsInfo(String info) {
        mTvFpsInfo.setText(info);
    }

    public void setFrameStatsInfo(String info) {
        mTvFrameStats.setText(info);
    }

    private void initView() {
        mBtnClose = (Button) findViewById(R.id.btn_close);
        mTvFrameStats = (TextView) findViewById(R.id.tv_frame_stats);
        mTvFpsInfo = (TextView) findViewById(R.id.tv_fps_info);
    }

    public void setFpsMonitorViewListener(FpsMonitorViewListener fpsMonitorViewListener) {
        mFpsMonitorViewListener = fpsMonitorViewListener;
    }

    public interface FpsMonitorViewListener {
        void onClose();
    }
}
