package com.allyn.lives.service;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;

import com.allyn.lives.events.MusicBeamEvent;
import com.allyn.lives.manage.PlayManager;
import com.allyn.lives.utils.Config;
import com.allyn.lives.utils.RxBus;

import java.util.Random;

public class MusicService extends Service {

    private final IBinder mBinder = new MyBinder();

    public static int servicePosition = -1;//当前播放音乐下标，

    @Override
    public int onStartCommand(Intent intent, final int flags, int startId) {
        final int startCommand = super.onStartCommand(intent, flags, startId);
        Bundle bundle = intent.getBundleExtra(Config.bunder);
        int position = bundle.getInt(Config.position, -1);
        if (position == -1) {
            int post = bundle.getInt(Config.position_liteorm, -1);
            //更新当前播放音乐下标，
            servicePosition = post;
            PlayManager.stop();

            PlayManager.play(servicePosition,true).setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mediaPlayer) {
                    //这是当前音乐播放结束之后的监听，先判断播放类型，在播放音乐，
                    switch (PlayManager.getCode()) {
                        case PlayManager.ORDER:
                            //判断是不是最后一首歌曲
                            if (servicePosition == PlayManager.getMusicSize() - 1) {
                                servicePosition = 0;
                            }
                            servicePosition++;
                            break;
                        case PlayManager.RANDOM:
                            servicePosition = new Random().nextInt(PlayManager.getMusicSize() - 1);
                            break;
                        case PlayManager.REPOT:
                            break;
                    }
                    PlayManager.stop();
                    PlayManager.play(servicePosition, true);
                    RxBus.getDefault().post(new MusicBeamEvent(servicePosition));
                }
            });
            //发送当前播放音乐下标，
            RxBus.getDefault().post(new MusicBeamEvent(servicePosition));
        } else {
            if (servicePosition == position) {  // 当前播放的下标 == 传过来的坐标
                if (PlayManager.mediaPlayer.isPlaying()) { // 正在播放，停止播放
                    PlayManager.pause();
                    RxBus.getDefault().post(new MusicBeamEvent(servicePosition));
                } else { // 停止播放，开始播放
                    PlayManager.play(position, false);
                    RxBus.getDefault().post(new MusicBeamEvent(servicePosition));
                }
            } else {
                //更新当前播放音乐下标，
                servicePosition = position;
                PlayManager.stop();

                PlayManager.play(position, false).setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mediaPlayer) {
                        //这是当前音乐播放结束之后的监听，先判断播放类型，在播放音乐，
                        switch (PlayManager.getCode()) {
                            case PlayManager.ORDER:
                                //判断是不是最后一首歌曲
                                if (servicePosition == PlayManager.getMusicSize() - 1) {
                                    servicePosition = 0;
                                }
                                servicePosition++;
                                break;
                            case PlayManager.RANDOM:
                                servicePosition = new Random().nextInt(PlayManager.getMusicSize() - 1);
                                break;
                            case PlayManager.REPOT:
                                break;
                        }
                        PlayManager.stop();
                        PlayManager.play(servicePosition,false);
                        RxBus.getDefault().post(new MusicBeamEvent(servicePosition));
                    }
                });
                //发送当前播放音乐下标，
                RxBus.getDefault().post(new MusicBeamEvent(servicePosition));
            }
        }


        return startCommand;
    }

    public class MyBinder extends Binder {
        public MusicService getService() {
            return MusicService.this;
        }
    }


    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

}
