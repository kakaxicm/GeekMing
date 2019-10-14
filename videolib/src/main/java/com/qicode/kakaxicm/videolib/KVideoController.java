package com.qicode.kakaxicm.videolib;

import android.content.Context;
import android.support.annotation.DrawableRes;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;

import java.util.Timer;
import java.util.TimerTask;

/**
 * 控制器ui
 */
public abstract class KVideoController extends FrameLayout implements View.OnTouchListener {
    protected Context context;
    protected IKVideoPlayer videoPlayer;//绑定的播放器

    private Timer mUpdateProgressTimer;
    private TimerTask mUpdateProgressTimerTask;
    private float mDownX;
    private float mDownY;
    private boolean mNeedChangePosition;
    private boolean mNeedChangeVolume;
    private boolean mNeedChangeBrightness;

    private static final int THRESHOLD = 80;
    private long mGestureDownPosition;
    private float mGestureDownBrightness;
    private int mGestureDownVolume;
    private long mNewPosition;

    public KVideoController(Context context) {
        this(context, null);
    }

    public KVideoController(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public KVideoController(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        init();
    }

    protected void init() {

    }

    public void setVideoPlayer(IKVideoPlayer videoPlayer) {
        this.videoPlayer = videoPlayer;
    }

    /**
     * 设置视频标题
     */
    public abstract void setTitle(String title);

    /**
     * 视频底图
     *
     * @param resId 视频底图资源
     */
    public abstract void setImage(@DrawableRes int resId);

    /**
     * 视频底图ImageView控件，提供给外部用图片加载工具来加载网络图片
     *
     * @return 底图ImageView
     */
    public abstract ImageView imageView();

    /**
     * 设置总时长.
     */
    public abstract void setLength(long len);

    /**
     * 当播放器的播放状态发生变化，在此方法中国你更新不同的播放状态的UI
     *
     * @param playState 播放状态：
     *                  <ul>
     * @link KVideoPlayer#STATE_IDLE}
     * {@link KVideoPlayer#STATE_PREPARING}
     * {@link KVideoPlayer#STATE_PREPARED}
     * {@link KVideoPlayer#STATE_PLAYING}
     * {@link KVideoPlayer#STATE_PAUSED}
     * @link KVideoPlayer#STATE_BUFFERING_PLAYING}
     * {@link KVideoPlayer#STATE_BUFFERING_PAUSED}>
     * {@link KVideoPlayer#STATE_ERROR}
     * {@link KVideoPlayer#STATE_COMPLETED}
     */
    protected abstract void onPlayStateChanged(int playState);

    /**
     * 当播放器的播放模式发生变化，在此方法中更新不同模式下的控制器界面。
     *
     * @param playMode 播放器的模式：
     *                 <ul>
     *                 <li>{@link KVideoPlayer#MODE_NORMAL}</li>
     *                 <li>{@link KVideoPlayer#MODE_FULL_SCREEN}</li>
     *                 <li>{@link KVideoPlayer#MODE_TINY_WINDOW}</li>
     *                 </ul>
     */
    protected abstract void onPlayModeChanged(int playMode);

    /**
     * 重置控制器，将控制器恢复到初始状态。
     */
    protected abstract void reset();

    protected void startUpdateProgressTimer() {
        cancelUpdateProgressTimer();
        if (mUpdateProgressTimer == null) {
            mUpdateProgressTimer = new Timer();
        }
        if (mUpdateProgressTimerTask == null) {
            mUpdateProgressTimerTask = new TimerTask() {
                @Override
                public void run() {
                    KVideoController.this.post(new Runnable() {
                        @Override
                        public void run() {
                            updateProgress();
                        }
                    });
                }
            };
        }
        mUpdateProgressTimer.schedule(mUpdateProgressTimerTask, 0, 1000);
    }

    /**
     * 更新进度，包括进度条进度，展示的当前播放位置时长，总时长等。
     */
    protected abstract void updateProgress();

    /**
     * 取消更新进度的计时器。
     */
    protected void cancelUpdateProgressTimer() {
        if (mUpdateProgressTimer != null) {
            mUpdateProgressTimer.cancel();
            mUpdateProgressTimer = null;
        }
        if (mUpdateProgressTimerTask != null) {
            mUpdateProgressTimerTask.cancel();
            mUpdateProgressTimerTask = null;
        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        //只有全屏时候 才支持手势
        if (videoPlayer == null || !videoPlayer.isFullScreen()) {
            return false;
        }

        // 只有在播放、暂停、缓冲的时候能够拖动改变位置、亮度和声音
        if (videoPlayer.isIdle() || videoPlayer.isError() || videoPlayer.isPreparing() || videoPlayer.isPrepared() || videoPlayer.isCompleted()) {
            hideChangePosition();
            hideChangeBrightness();
            hideChangeVolume();
            return false;
        }

        float x = event.getX();
        float y = event.getY();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mDownX = x;
                mDownY = y;
                mNeedChangePosition = false;
                mNeedChangeVolume = false;
                mNeedChangeBrightness = false;
                break;
            case MotionEvent.ACTION_MOVE:
                float deltaX = x - mDownX;
                float deltaY = y - mDownY;

                float absDeltaX = Math.abs(deltaX);
                float absDeltaY = Math.abs(deltaY);

                //探测哪些手势生效,一旦一个生效则不执行探测
                if (!mNeedChangePosition && !mNeedChangeVolume && !mNeedChangeBrightness) {
                    if (absDeltaX >= THRESHOLD) {//水平滑动
                        cancelUpdateProgressTimer();
                        mNeedChangePosition = true;
                        mGestureDownPosition = videoPlayer.getCurrentPosition();
                    } else if (absDeltaY >= THRESHOLD) {
                        if (mDownX <= getWidth() * 0.5f) {//左侧改变亮度
                            mNeedChangeBrightness = true;
                            mGestureDownBrightness = KUtil.scanForActivity(context)
                                    .getWindow().getAttributes().screenBrightness;
                        } else if (mDownX > getWidth() * 0.5f) {//右边改变音量
                            mNeedChangeVolume = true;
                            mGestureDownVolume = videoPlayer.getVolume();
                        }
                    }
                }

                if (mNeedChangePosition) {
                    long duration = videoPlayer.getDuration();
                    long toPosition = (long) (mGestureDownPosition + deltaX * duration / getWidth());
                    mNewPosition = Math.max(0, Math.min(duration, toPosition));
                    int newPositionProgress = (int) (100f * mNewPosition / duration);
                    showChangePosition(duration, newPositionProgress);
                }

                if (mNeedChangeBrightness) {
                    float deltaBrightness = deltaY * 3 / getHeight();
                    float newBrightness = mGestureDownBrightness + deltaBrightness;
                    newBrightness = Math.max(0, Math.min(newBrightness, 1));
                    float newBrightnessPercentage = newBrightness;
                    WindowManager.LayoutParams params = KUtil.scanForActivity(context)
                            .getWindow().getAttributes();
                    params.screenBrightness = newBrightnessPercentage;
                    KUtil.scanForActivity(context).getWindow().setAttributes(params);
                    int newBrightnessProgress = (int) (100f * newBrightnessPercentage);
                    showChangeBrightness(newBrightnessProgress);
                }

                if (mNeedChangeVolume) {
                    deltaY = -deltaY;
                    int maxVolume = videoPlayer.getMaxVolume();
                    int deltaVolume = (int) (maxVolume * deltaY * 3 / getHeight());
                    int newVolume = mGestureDownVolume + deltaVolume;
                    newVolume = Math.max(0, Math.min(maxVolume, newVolume));
                    videoPlayer.setVolume(newVolume);
                    int newVolumeProgress = (int) (100f * newVolume / maxVolume);
                    showChangeVolume(newVolumeProgress);
                }
                break;

            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                if (mNeedChangePosition) {
                    videoPlayer.seekTo(mNewPosition);
                    hideChangePosition();
                    startUpdateProgressTimer();
                    return true;
                }
                if (mNeedChangeBrightness) {
                    hideChangeBrightness();
                    return true;
                }
                if (mNeedChangeVolume) {
                    hideChangeVolume();
                    return true;
                }
                break;
        }
        return false;
    }

    /**
     * 隐藏
     */
    protected abstract void hideChangeVolume();

    /**
     * 隐藏
     */
    protected abstract void hideChangeBrightness();

    /**
     * 隐藏
     */
    protected abstract void hideChangePosition();

    /**
     * 手势左右滑动改变播放位置时，显示控制器中间的播放位置变化视图，
     * 在手势滑动ACTION_MOVE的过程中，会不断调用此方法。
     *
     * @param duration            视频总时长ms
     * @param newPositionProgress 新的位置进度，取值0到100。
     */
    protected abstract void showChangePosition(long duration, int newPositionProgress);

    /**
     * 手势在左侧上下滑动改变亮度时，显示控制器中间的亮度变化视图，
     * 在手势滑动ACTION_MOVE的过程中，会不断调用此方法。
     *
     * @param newBrightnessProgress 新的亮度进度，取值1到100。
     */
    protected abstract void showChangeBrightness(int newBrightnessProgress);

    /**
     * 手势在右侧上下滑动改变音量时，显示控制器中间的音量变化视图，
     * 在手势滑动ACTION_MOVE的过程中，会不断调用此方法。
     *
     * @param newVolumeProgress 新的音量进度，取值1到100。
     */
    protected abstract void showChangeVolume(int newVolumeProgress);

}
