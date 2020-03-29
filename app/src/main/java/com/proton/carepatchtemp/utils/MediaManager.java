package com.proton.carepatchtemp.utils;

import android.content.res.AssetFileDescriptor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnErrorListener;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;

import com.proton.carepatchtemp.bean.AlarmBean;
import com.proton.carepatchtemp.component.App;
import com.wms.logger.Logger;

import org.litepal.LitePal;

import java.io.IOException;
import java.util.List;

public class MediaManager {

    private static MediaPlayer mMediaPlayer;
    private static boolean isPause;
    private static MediaManager mInstance = new MediaManager();
    private OnRecordPlayListener onRecordPlayListener;
    private int playTime;
    private boolean isPlaying;
    private Handler mainHandler = new Handler(Looper.getMainLooper());
    private Thread timeThread;
    private String alarmFileName;
    private Runnable mProgressRunnable = new Runnable() {
        @Override
        public void run() {
            while (isPlaying) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if (isPlaying) {
                    playTime += 1;
                    mainHandler.post(() -> {
                        if (onRecordPlayListener != null) {
                            onRecordPlayListener.onPlayProgress(playTime);
                        }
                    });
                }
            }
        }
    };

    private MediaManager() {
    }

    public static MediaManager getInstance() {
        return mInstance;
    }

    public boolean isPlaying() {
        return isPlaying;
    }

    public void setAlarmFileName(String alarmFileName) {
        this.alarmFileName = alarmFileName;
    }

    public void playSound(boolean isLoop) {
        stop();
        AssetFileDescriptor descriptor = null;
        try {
            if (TextUtils.isEmpty(alarmFileName)) {
                alarmFileName = "alarmOne.mp3";
            }
            descriptor = App.get().getAssets().openFd(alarmFileName);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (mMediaPlayer == null) {
            mMediaPlayer = new MediaPlayer();
            mMediaPlayer.setOnErrorListener(new OnErrorListener() {

                @Override
                public boolean onError(MediaPlayer arg0, int arg1, int arg2) {
                    mMediaPlayer.reset();
                    return false;
                }
            });
        } else {
            mMediaPlayer.reset();
        }

        try {
            isPlaying = true;
            playTime = 0;
            mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            if (descriptor != null) {
                mMediaPlayer.setDataSource(descriptor.getFileDescriptor(),
                        descriptor.getStartOffset(), descriptor.getLength());
                descriptor.close();
            }
            mMediaPlayer.setLooping(isLoop);
            mMediaPlayer.prepare();
            mMediaPlayer.start();
            mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    if (onRecordPlayListener != null) {
                        isPlaying = false;
                        onRecordPlayListener.onPlayComplete();
                    }
                }
            });
            timeThread = new Thread(mProgressRunnable);
            timeThread.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void stop() {
        isPlaying = false;
        if (timeThread != null) {
            timeThread.interrupt();
        }
        if (mMediaPlayer != null && mMediaPlayer.isPlaying()) {
            mMediaPlayer.stop();
            isPause = true;
        }
    }

    public void pause() {
        isPlaying = false;
        if (mMediaPlayer != null && mMediaPlayer.isPlaying()) {
            mMediaPlayer.pause();
            isPause = true;
        }
    }

    public void resume() {
        isPlaying = true;
        if (mMediaPlayer != null && isPause) {
            mMediaPlayer.start();
            isPause = false;
        }
    }

    public void release() {
        if (mMediaPlayer != null) {
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
    }

    public void setOnRecordPlayListener(OnRecordPlayListener onRecordPlayListener) {
        //将上次的监听器置为完成
        if (this.onRecordPlayListener != null) {
            this.onRecordPlayListener.onPlayComplete();
        }
        this.onRecordPlayListener = onRecordPlayListener;
    }

    public interface OnRecordPlayListener {
        void onPlayComplete();

        void onPlayProgress(int playTime);
    }
}
