package com.qicode.kakaxicm.videolib;

import android.content.Context;
import android.graphics.Color;
import android.graphics.SurfaceTexture;
import android.media.AudioManager;
import android.net.Uri;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.Surface;
import android.view.TextureView;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import java.io.IOException;
import java.util.Map;

import tv.danmaku.ijk.media.player.AndroidMediaPlayer;
import tv.danmaku.ijk.media.player.IMediaPlayer;
import tv.danmaku.ijk.media.player.IjkMediaPlayer;

public class KVideoPlayer extends FrameLayout implements IKVideoPlayer, TextureView.SurfaceTextureListener {

    /**
     * 播放错误
     **/
    public static final int STATE_ERROR = -1;
    /**
     * 播放未开始
     **/
    public static final int STATE_IDLE = 0;
    /**
     * 播放准备中
     **/
    public static final int STATE_PREPARING = 1;
    /**
     * 播放准备就绪
     **/
    public static final int STATE_PREPARED = 2;
    /**
     * 正在播放
     **/
    public static final int STATE_PLAYING = 3;
    /**
     * 暂停播放
     **/
    public static final int STATE_PAUSED = 4;
    /**
     * 正在缓冲(播放器正在播放时，缓冲区数据不足，进行缓冲，缓冲区数据足够后恢复播放)
     **/
    public static final int STATE_BUFFERING_PLAYING = 5;
    /**
     * 正在缓冲(播放器正在播放时，缓冲区数据不足，进行缓冲，此时暂停播放器，继续缓冲，缓冲区数据足够后恢复暂停
     **/
    public static final int STATE_BUFFERING_PAUSED = 6;
    /**
     * 播放完成
     **/
    public static final int STATE_COMPLETED = 7;

    /**
     * 普通模式
     **/
    public static final int MODE_NORMAL = 10;
    /**
     * 全屏模式
     **/
    public static final int MODE_FULL_SCREEN = 11;
    /**
     * 小窗口模式
     **/
    public static final int MODE_TINY_WINDOW = 12;

    /**
     * IjkPlayer
     **/
    public static final int TYPE_IJK = 111;
    /**
     * MediaPlayer
     **/
    public static final int TYPE_NATIVE = 222;

    private int playerType = TYPE_IJK;
    private int currentState = STATE_IDLE;
    private int currentMode = MODE_NORMAL;

    private Context context;
    private AudioManager audioManager;//控制音量
    private IMediaPlayer mediaPlayer;
    private FrameLayout container;
    private TextureView textureView;
    private KVideoController mController;
    private SurfaceTexture surfaceTexture;
    private Surface surface;
    private String uri;
    private Map<String, String> headers;

    private int bufferPercentage;
    //    private boolean continueFromLastPosition = true;
    private long skipToPosition;


    public KVideoPlayer(Context context) {
        this(context, null);
    }

    public KVideoPlayer(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public KVideoPlayer(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        init();
    }

    /**
     * 添加一个黑色背景容器view,为啥要多这一层，是为了全屏和小窗口模式切换做rm/add
     */
    private void init() {
        container = new FrameLayout(context);
        container.setBackgroundColor(Color.BLACK);
        LayoutParams params = new LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        addView(container, params);
    }

    /**
     * 更新控制器
     *
     * @param controller
     */
    public void setVideoController(KVideoController controller) {
        container.removeView(mController);
        mController = controller;
        //todo controller.reset
        mController.setVideoPlayer(this);
        LayoutParams params = new LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        container.addView(mController, params);
    }

    /**
     * 设置播放器类型
     *
     * @param playerType IjkPlayer or MediaPlayer.
     */
    public void setPlayerType(int playerType) {
        this.playerType = playerType;
    }

    @Override
    public void setSource(String uri, Map<String, String> headers) {
        this.uri = uri;
        this.headers = headers;
    }

    @Override
    public void start() {
        if (currentState == STATE_IDLE) {
            //TODO
//             NiceVideoPlayerManager.instance().setCurrentNiceVideoPlayer(this);
            initAudioManager();
            initMediaPlayer();
            initTextureView();
            addTextureView();
        } else {
            Log.e("Player", "NiceVideoPlayer只有在mCurrentState == STATE_IDLE时才能调用start方法");
        }

    }

    private void initTextureView() {
        if (textureView == null) {
            textureView = new TextureView(context);
            textureView.setSurfaceTextureListener(this);
        }
    }

    private void addTextureView() {
        container.removeView(textureView);
        LayoutParams params = new LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT,
                Gravity.CENTER);
        container.addView(textureView, 0, params);
    }

    private void initMediaPlayer() {
        if (mediaPlayer == null) {
            switch (playerType) {
                case TYPE_NATIVE:
                    mediaPlayer = new AndroidMediaPlayer();
                    break;
                case TYPE_IJK:
                default:
                    mediaPlayer = new IjkMediaPlayer();
                    break;

            }
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        }
    }

    private void initAudioManager() {
        if (audioManager == null) {
            audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
            audioManager.requestAudioFocus(null, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);
        }
    }

    @Override
    public void start(long position) {
        skipToPosition = position;
        start();
    }

    @Override
    public void restart() {
        if (currentState == STATE_PAUSED) {
            mediaPlayer.start();
            currentState = STATE_PLAYING;
            mController.onPlayStateChanged(currentState);
        } else if (currentState == STATE_BUFFERING_PAUSED) {
            mediaPlayer.start();
            currentState = STATE_BUFFERING_PLAYING;
            mController.onPlayStateChanged(currentState);
        } else if (currentState == STATE_COMPLETED || currentState == STATE_ERROR) {
            mediaPlayer.reset();
            openMediaPlayer();
        } else {
            Log.e("Player", "NiceVideoPlayer在mCurrentState ==" + currentState + "时不能调用restart()方法.");
        }
    }

    @Override
    public void pause() {
        if (currentState == STATE_PLAYING) {
            mediaPlayer.pause();
            currentState = STATE_PAUSED;
            mController.onPlayStateChanged(currentState);
        }
        if (currentState == STATE_BUFFERING_PLAYING) {
            mediaPlayer.pause();
            currentState = STATE_BUFFERING_PAUSED;
            mController.onPlayStateChanged(currentState);
        }
    }

    @Override
    public void seekTo(long pos) {
        if (mediaPlayer != null) {
            mediaPlayer.seekTo(pos);
        }
    }

    @Override
    public void setVolume(int volume) {
        if (audioManager != null) {
            audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, volume, 0);
        }
    }

    @Override
    public void setSpeed(float speed) {
        if (mediaPlayer instanceof IjkMediaPlayer) {
            ((IjkMediaPlayer) mediaPlayer).setSpeed(speed);
        } else {
            Log.e("Player", "只有IjkPlayer才能设置播放速度");
        }
    }

    @Override
    public void continueFromLastPosition(boolean continueFromLastPosition) {
    }

    @Override
    public boolean isIdle() {
        return currentState == STATE_IDLE;
    }

    @Override
    public boolean isPreparing() {
        return currentState == STATE_PREPARING;
    }

    @Override
    public boolean isPrepared() {
        return currentState == STATE_PREPARED;
    }

    @Override
    public boolean isBufferingPlaying() {
        return currentState == STATE_BUFFERING_PLAYING;
    }

    @Override
    public boolean isBufferingPaused() {
        return currentState == STATE_BUFFERING_PAUSED;
    }

    @Override
    public boolean isPlaying() {
        return currentState == STATE_PLAYING;
    }

    @Override
    public boolean isPaused() {
        return currentState == STATE_PAUSED;
    }

    @Override
    public boolean isError() {
        return currentState == STATE_ERROR;
    }

    @Override
    public boolean isCompleted() {
        return currentState == STATE_COMPLETED;
    }

    @Override
    public boolean isFullScreen() {
        return currentMode == MODE_FULL_SCREEN;
    }

    @Override
    public boolean isTinyWindow() {
        return currentMode == MODE_TINY_WINDOW;
    }

    @Override
    public boolean isNormal() {
        return currentMode == MODE_NORMAL;
    }

    @Override
    public int getMaxVolume() {
        if (audioManager != null) {
            return audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        }
        return 0;
    }

    @Override
    public int getVolume() {
        if (audioManager != null) {
            return audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        }
        return 0;
    }

    @Override
    public long getDuration() {
        return mediaPlayer != null ? mediaPlayer.getDuration() : 0;
    }

    @Override
    public long getCurrentPosition() {
        return mediaPlayer != null ? mediaPlayer.getCurrentPosition() : 0;
    }

    @Override
    public int getBufferPercentage() {
        return bufferPercentage;
    }

    @Override
    public float getSpeed(float speed) {
        if (mediaPlayer instanceof IjkMediaPlayer) {
            return ((IjkMediaPlayer) mediaPlayer).getSpeed(speed);
        }
        return 0;
    }

    @Override
    public long getTcpSpeed() {
        if (mediaPlayer instanceof IjkMediaPlayer) {
            return ((IjkMediaPlayer) mediaPlayer).getTcpSpeed();
        }
        return 0;
    }

    @Override
    public void enterFullScreen() {
        //TODO
    }

    @Override
    public boolean exitFullScreen() {
        //TODO
        return false;
    }

    @Override
    public void enterTinyWindow() {
        //TODO
    }

    @Override
    public boolean exitTinyWindow() {
        //TODO
        return false;
    }

    @Override
    public void releasePlayer() {
        if (audioManager != null) {
            audioManager.abandonAudioFocus(null);
            audioManager = null;
        }
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
        container.removeView(textureView);
        if (surface != null) {
            surface.release();
            surface = null;
        }
        if (surfaceTexture != null) {
            surfaceTexture.release();
            surfaceTexture = null;
        }
        currentState = STATE_IDLE;

    }

    @Override
    public void release() {

    }

    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
        if (surfaceTexture == null) {
            surfaceTexture = surface;
            openMediaPlayer();
        } else {
            textureView.setSurfaceTexture(surfaceTexture);
        }

    }

    private void openMediaPlayer() {
        // 屏幕常亮
        container.setKeepScreenOn(true);
        //各种监听
        mediaPlayer.setOnPreparedListener(onPreparedListener);
        mediaPlayer.setOnVideoSizeChangedListener(onVideoSizeChangedListener);
        mediaPlayer.setOnCompletionListener(onCompletionListener);
        mediaPlayer.setOnErrorListener(onErrorListener);
        mediaPlayer.setOnInfoListener(infoListener);
        mediaPlayer.setOnBufferingUpdateListener(onBufferingListener);
        //设置源
        try {
            mediaPlayer.setDataSource(context.getApplicationContext(), Uri.parse(uri), headers);
            if (surface == null) {
                surface = new Surface(surfaceTexture);
            }
            mediaPlayer.setSurface(surface);
            mediaPlayer.prepareAsync();
            currentState = STATE_PREPARING;
            mController.onPlayStateChanged(currentState);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {

    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
        return surfaceTexture == null;
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surface) {

    }

    private final IMediaPlayer.OnPreparedListener onPreparedListener = new IMediaPlayer.OnPreparedListener() {
        @Override
        public void onPrepared(IMediaPlayer mp) {
            currentState = STATE_PREPARED;
            mController.onPlayStateChanged(currentState);
            mp.start();//开始播放
            //TODO  从上次的保存位置播放
//            if (continueFromLastPosition) {
//                long savedPlayPosition = NiceUtil.getSavedPlayPosition(mContext, mUrl);
//                mp.seekTo(savedPlayPosition);
//            }

            //跳到指定位置播放
            if (skipToPosition != 0) {
                mp.seekTo(skipToPosition);
            }
        }
    };

    private final IMediaPlayer.OnVideoSizeChangedListener onVideoSizeChangedListener = new IMediaPlayer.OnVideoSizeChangedListener() {
        @Override
        public void onVideoSizeChanged(IMediaPlayer iMediaPlayer, int i, int i1, int i2, int i3) {
            //TODO
//            mTextureView.adaptVideoSize(width, height);
        }
    };

    private final IMediaPlayer.OnCompletionListener onCompletionListener = new IMediaPlayer.OnCompletionListener() {
        @Override
        public void onCompletion(IMediaPlayer iMediaPlayer) {
            currentState = STATE_COMPLETED;
             mController.onPlayStateChanged(currentState);
            // 清除屏幕常亮
            container.setKeepScreenOn(false);
        }
    };

    private final IMediaPlayer.OnErrorListener onErrorListener = new IMediaPlayer.OnErrorListener() {
        @Override
        public boolean onError(IMediaPlayer iMediaPlayer, int what, int extra) {
            currentState = STATE_ERROR;
            // 直播流播放时去调用mediaPlayer.getDuration会导致-38和-2147483648错误，忽略该错误
            if (what != -38 && what != -2147483648 && extra != -38 && extra != -2147483648) {
                currentState = STATE_ERROR;
                mController.onPlayStateChanged(currentState);
            }
            return true;
        }
    };

    private final IMediaPlayer.OnInfoListener infoListener = new IMediaPlayer.OnInfoListener() {
        @Override
        public boolean onInfo(IMediaPlayer iMediaPlayer, int what, int extra) {
            if (what == IMediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START) {
                // 播放器开始渲染
                currentState = STATE_PLAYING;
                mController.onPlayStateChanged(currentState);
//                LogUtil.d("onInfo ——> MEDIA_INFO_VIDEO_RENDERING_START：STATE_PLAYING");
            } else if (what == IMediaPlayer.MEDIA_INFO_BUFFERING_START) {
                // MediaPlayer暂时不播放，以缓冲更多的数据
                if (currentState == STATE_PAUSED || currentState == STATE_BUFFERING_PAUSED) {
                    currentState = STATE_BUFFERING_PAUSED;
//                    LogUtil.d("onInfo ——> MEDIA_INFO_BUFFERING_START：STATE_BUFFERING_PAUSED");
                } else {
                    currentState = STATE_BUFFERING_PLAYING;
//                    LogUtil.d("onInfo ——> MEDIA_INFO_BUFFERING_START：STATE_BUFFERING_PLAYING");
                }
                mController.onPlayStateChanged(currentState);
            } else if (what == IMediaPlayer.MEDIA_INFO_BUFFERING_END) {
                // 填充缓冲区后，MediaPlayer恢复播放/暂停
                if (currentState == STATE_BUFFERING_PLAYING) {
                    currentState = STATE_PLAYING;
                    mController.onPlayStateChanged(currentState);
//                    LogUtil.d("onInfo ——> MEDIA_INFO_BUFFERING_END： STATE_PLAYING");
                }
                if (currentState == STATE_BUFFERING_PAUSED) {
                    currentState = STATE_PAUSED;
                    mController.onPlayStateChanged(currentState);
//                    LogUtil.d("onInfo ——> MEDIA_INFO_BUFFERING_END： STATE_PAUSED");
                }
            } else if (what == IMediaPlayer.MEDIA_INFO_VIDEO_ROTATION_CHANGED) {
                // 视频旋转了extra度，需要恢复
                if (textureView != null) {
                    textureView.setRotation(extra);
                }
            } else if (what == IMediaPlayer.MEDIA_INFO_NOT_SEEKABLE) {
//                LogUtil.d("视频不能seekTo，为直播视频");
            } else {
//                LogUtil.d("onInfo ——> what：" + what);
            }
            return true;
        }
    };

    private final IMediaPlayer.OnBufferingUpdateListener onBufferingListener = new IMediaPlayer.OnBufferingUpdateListener() {
        @Override
        public void onBufferingUpdate(IMediaPlayer iMediaPlayer, int percent) {
            bufferPercentage = percent;
        }
    };

}
