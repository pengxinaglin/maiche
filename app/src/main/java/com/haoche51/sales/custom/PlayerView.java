package com.haoche51.sales.custom;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.haoche51.sales.R;
import com.haoche51.sales.util.HCLogUtil;
import com.haoche51.sales.util.ToastUtil;
import com.haoche51.sales.util.UnixTimeUtil;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

/**
 * 播放音频的控件  Version 1
 * <p/>
 * 使用：
 * 1、控件初始化之后要 setMediaPlayer()
 * 2、调用 play(String url) 方法播放
 * 3、页面 destory 的时候，需要 调用 playerDestory()
 * <p/>
 * Created by yangming on 2015/11/10.
 */
public class PlayerView extends LinearLayout implements MediaPlayer.OnBufferingUpdateListener, MediaPlayer.OnCompletionListener,
        MediaPlayer.OnPreparedListener {

    private final String TAG = "PlayerView";

    private SeekBar seekBar;// 拖动条
    //  private ImageButton imageButton;// 播放
    private String playUrl = ""; // 媒体url
    private TextView textViewCurrent;
    private TextView textViewTotal;

    private MediaPlayer mediaPlayer;// 媒体播放器
    private Timer timer = new Timer();// 计时器
    private TimerTask timerTask = null;

    private int mProgress;

    public PlayerView(Context context) {
        this(context, null, 0);

    }

    public PlayerView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PlayerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        doInitViews();
    }

    public void doInitViews() {
        LayoutInflater.from(getContext()).inflate(R.layout.music_player_layout, this, true);
        textViewCurrent = (TextView) findViewById(R.id.tv_music_player_layout_current);
        textViewTotal = (TextView) findViewById(R.id.tv_music_player_layout_total);
        seekBar = (SeekBar) findViewById(R.id.seek_bar_music_player_layout);
        ImageButton imageButton = (ImageButton) findViewById(R.id.image_button_music_player_lauyout_play);
        imageButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isPlay()) {
                    pause();
                } else {
                    play();
                }
            }
        });

        seekBar.setOnSeekBarChangeListener(new SeekBarChangeEvent());
//    startTimer();
    }


    public void setMediaPlayer(MediaPlayer mediaPlayer) {
        if (this.mediaPlayer != null) return;
        this.mediaPlayer = mediaPlayer;
        try {
            this.mediaPlayer = new MediaPlayer();
            this.mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            this.mediaPlayer.setOnBufferingUpdateListener(this);
            this.mediaPlayer.setOnPreparedListener(this);
            this.mediaPlayer.setOnCompletionListener(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public SeekBar getSeekBar() {
        return seekBar;
    }

    /**
     * 播放
     */
    public void play() {
        if ("".equals(playUrl)) {
            return;
        }
        if (mediaPlayer == null) {
            return;
        }
        mediaPlayer.start();
        seekBar.setThumb(getResources().getDrawable(R.drawable.music_player_pause));
//    imageButton.setImageDrawable(getResources().getDrawable(R.drawable.music_player_pause));
        startTimer();
    }

    /**
     * @param url url地址
     */
    public void playUrl(final String url) {
        try {
            if (!TextUtils.isEmpty(playUrl)) {
                stop();
            }

            ToastUtil.showText("正在缓冲");
            mediaPlayer.seekTo(0);
            seekBar.setProgress(0);
            textViewTotal.setText("00:00");
            textViewCurrent.setText("00:00");
            playUrl = url;

            mediaPlayer.reset();
            mediaPlayer.setDataSource(playUrl); // 设置数据源
            mediaPlayer.prepare(); // prepare自动播放

            int duration = mediaPlayer.getDuration();
            textViewTotal.setText(UnixTimeUtil.getFormatTime(duration));
            textViewCurrent.setText("00:00");
            startTimer();
        } catch (Exception e) {
        }
    }

    /**
     * 暂停
     */
    public void pause() {
        if (mediaPlayer == null) {
            return;
        }
        try {
            mediaPlayer.pause();
        } catch (Exception e) {
        }
        cancleTimer();
        seekBar.setThumb(getResources().getDrawable(R.drawable.music_player_play));
//    imageButton.setImageDrawable(getResources().getDrawable(R.drawable.music_player_play));
    }

    /**
     * 停止
     */
    public void stop() {
        try {
            if (mediaPlayer != null) {
                mediaPlayer.stop();
                mediaPlayer.reset();
                mediaPlayer.seekTo(0);
                seekBar.setProgress(0);
                textViewTotal.setText("00:00");
                textViewCurrent.setText("00:00");
                seekBar.setThumb(getResources().getDrawable(R.drawable.music_player_play));
//        imageButton.setImageDrawable(getResources().getDrawable(R.drawable.music_player_play));
            }
            playUrl = "";
            cancleTimer();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean isPlay() {
        if (mediaPlayer != null) {
            try {
                return mediaPlayer.isPlaying();
            } catch (Exception e) {
                return false;
            }
        } else {
            return false;
        }
    }

    public void playerDestory() {
        try {
            if (mediaPlayer != null) {
                mediaPlayer.stop();
                mediaPlayer.reset();
                mediaPlayer.release();
                mediaPlayer = null;
            }
            playUrl = "";
        } catch (Exception e) {
            e.printStackTrace();
        }
        cancleTimer();
    }

    private void startTimer() {
        if (timer == null) {
            timer = new Timer();
        }

//    if (timerTask == null) {
        timerTask = new TimerTask() {
            @Override
            public void run() {
                if (mediaPlayer == null)
                    return;
                if (isPlay() && !seekBar.isPressed()) {
                    handleProgress.sendEmptyMessage(0);
                }
            }
        };
//    }

        if (timer != null && timerTask != null) {
            timer.schedule(timerTask, 0, 400);
        }
    }

    /**
     * 停止计时器
     */
    private void cancleTimer() {
        //停止计时器
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
        if (timerTask != null) {
            timerTask.cancel();
            timerTask = null;
        }
    }

    private Handler handleProgress = new Handler() {
        public void handleMessage(Message msg) {

            if (mediaPlayer != null) {
                int position = mediaPlayer.getCurrentPosition();
                int duration = mediaPlayer.getDuration();

                if (duration > 0) {
                    long pos = seekBar.getMax() * position / duration;
                    seekBar.setProgress((int) pos);
                    textViewCurrent.setText(UnixTimeUtil.getFormatTime(position));
                }
            }
        }
    };

    @Override
    public void onBufferingUpdate(MediaPlayer mediaPlayer, int bufferingProgress) {
        seekBar.setSecondaryProgress(bufferingProgress);
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        seekBar.setThumb(getResources().getDrawable(R.drawable.music_player_play));
//    imageButton.setImageDrawable(getResources().getDrawable(R.drawable.music_player_play));
        seekBar.setProgress(0);
        textViewCurrent.setText("00:00");

    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        mediaPlayer.start();
        seekBar.setThumb(getResources().getDrawable(R.drawable.music_player_pause));
//    imageButton.setImageDrawable(getResources().getDrawable(R.drawable.music_player_pause));
    }

    class SeekBarChangeEvent implements SeekBar.OnSeekBarChangeListener {
        int progress;

        @Override
        public void onProgressChanged(SeekBar seekBar, int progress,
                                      boolean fromUser) {
            //seekbar滑动过程中触发（包括自动滑动和手动滑动）
            if (mediaPlayer != null)
                this.progress = progress * mediaPlayer.getDuration()
                        / seekBar.getMax();
            textViewCurrent.setText(UnixTimeUtil.getFormatTime(this.progress));
            HCLogUtil.d(TAG, "onProgressChanged--progress-->" + progress);
            HCLogUtil.d(TAG, "onProgressChanged--this.progress-->" + this.progress);

            if (fromUser && Math.abs(progress - mProgress) <= 9) {
                if (isPlay()) {
                    pause();
                } else {
                    play();
                }
            }

            mProgress = progress;
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
            //点击seekbar任意位置触发，down和up各一次，一共两次触发
        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            mediaPlayer.seekTo(progress);
        }
    }
}
