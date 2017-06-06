package com.allyn.lives.activity.music;


import android.app.AlertDialog;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.allyn.lives.R;
import com.allyn.lives.activity.base.BaseActivity;
import com.allyn.lives.app.MainApplication;
import com.allyn.lives.bean.MusicBean;
import com.allyn.lives.events.MusicBeamEvent;
import com.allyn.lives.events.MusicCodeEvent;
import com.allyn.lives.manage.PlayManager;
import com.allyn.lives.service.MusicService;
import com.allyn.lives.utils.Config;
import com.allyn.lives.utils.RxBus;
import com.allyn.lives.utils.TextFormater;
import com.allyn.lives.utils.blur.BlurTransformation;
import com.litesuits.orm.db.assit.WhereBuilder;

import java.util.List;
import java.util.Random;

import butterknife.Bind;
import butterknife.ButterKnife;
import rx.Observable;
import rx.functions.Action1;
import rx.functions.Func1;

public class MusicPlayActivivy extends BaseActivity {

    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.btnReturn)
    ImageButton btnReturn;

    @Bind(R.id.btnPrevious)
    ImageButton mPrevios;
    @Bind(R.id.btnPlay)
    ImageButton mPlay;

    @Bind(R.id.btnNext)
    ImageButton mNext;

    @Bind(R.id.btnLike)
    ImageButton mLike;
    @Bind(R.id.btnCode)
    ImageButton btnCode;
    @Bind(R.id.btnMore)
    ImageButton btnMore;

    @Bind(R.id.ivBg)
    ImageView mBg;
    @Bind(R.id.pro_len)
    SeekBar mSeekbar;
    @Bind(R.id.tvStart)
    TextView tvStart;
    @Bind(R.id.tvEnd)
    TextView tvEnd;

    @Bind(R.id.tvMusicNmae)
    TextView tvMusicNmae;
    @Bind(R.id.tvAuthorName)
    TextView tvAuthorName;

    MediaPlayer media;
    int mPosition;

    private boolean mUpdateTimeFlag = false;
    private boolean misPlaying = false;
    Intent intent;

    MusicBean music;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music_play_activivy);
        ButterKnife.bind(this);

        intent = new Intent(MusicPlayActivivy.this, MusicService.class);

        com.bumptech.glide.Glide.with(this)
                .load("http://img3.shijue.cvidea.cn/tf/140326/2326374/5332974e3dfae93ce7000001.PNG")
                .crossFade()
                .placeholder(R.drawable.ic_music)
                .bitmapTransform(new BlurTransformation(this, 95, 3))
                .into(mBg);

        RxBus.getDefault().toObserverable(MusicBeamEvent.class).subscribe(new Action1<MusicBeamEvent>() {
            @Override
            public void call(MusicBeamEvent musicBeamEvent) {
                music = PlayManager.getList().get(musicBeamEvent.getIndex());
                tvMusicNmae.setText(music.getName());
                tvAuthorName.setText(music.getArtist());
            }
        });

        listener();
    }


    Handler h = new Handler();
    Runnable run = new Runnable() {
        @Override
        public void run() {
            MediaPlayer mediaPlayer = PlayManager.mediaPlayer;
            if (mediaPlayer != null) {
                mSeekbar.setProgress(mediaPlayer.getCurrentPosition() * mSeekbar.getMax() / mediaPlayer.getDuration());
            }
            h.postDelayed(run, 100);
        }

    };

    @Override
    protected void onResume() {
        super.onResume();
        media = PlayManager.mediaPlayer;
        mPosition = MusicService.servicePosition;
        if (media != null) {
            new Thread(run).start();
            tvEnd.setText(PlayManager.formatTime(media.getDuration()));

            //更新播放时间
            if (mUpdateTimeFlag == false) {
                mUpdateTimeFlag = true;
                new TimeThread().start();
            }
            UpdateButton(media.isPlaying());
        }

        if (PlayManager.getList() == null) {
            tvMusicNmae.setText("没有歌曲");
            tvAuthorName.setText("");
            return;
        }
        if (mPosition == -1) {
            mPosition = 0;
        }

        music = PlayManager.getList().get(mPosition);
        tvMusicNmae.setText(music.getName());
        tvAuthorName.setText(music.getArtist());

        boolean isLike = IsLike(music);
        if (isLike) {
            mLike.setBackgroundResource(R.mipmap.ic_like_white);
        } else {
            mLike.setBackgroundResource(R.mipmap.ic_unlike_white);
        }

        switch (PlayManager.getCode()) {
            case PlayManager.ORDER:
                btnCode.setBackgroundResource(R.mipmap.ic_order);
                break;
            case PlayManager.RANDOM:
                btnCode.setBackgroundResource(R.mipmap.ic_random);
                break;
            case PlayManager.REPOT:
                btnCode.setBackgroundResource(R.mipmap.ic_circulation);
                break;
        }

    }

    class TimeThread extends Thread {
        @Override
        public void run() {
            while (mUpdateTimeFlag) {
                h.post(new Runnable() {
                    @Override
                    public void run() {
                        int timeLen = 0;
                        if (PlayManager.mediaPlayer != null) {
                            timeLen = PlayManager.mediaPlayer.getCurrentPosition();
                        } else {
                            timeLen = 0;
                        }
                        tvStart.setText(PlayManager.formatTime(timeLen));
                    }
                });
                //过一秒更新
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }
        }
    }

    public void UpdateButton(boolean isPlaying) {
        if (isPlaying) {
            misPlaying = false;
            mPlay.setBackgroundResource(R.mipmap.ic_play);
        } else {
            misPlaying = true;
            mPlay.setBackgroundResource(R.mipmap.ic_pause);
        }
    }

    private void listener() {
        btnCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (PlayManager.getCode()) {
                    case PlayManager.ORDER:
                        PlayManager.setCode(PlayManager.RANDOM);
                        Toast.makeText(MusicPlayActivivy.this, "随机", Toast.LENGTH_SHORT).show();
                        btnCode.setBackgroundResource(R.mipmap.ic_random);
                        break;
                    case PlayManager.RANDOM:
                        PlayManager.setCode(PlayManager.REPOT);
                        Toast.makeText(MusicPlayActivivy.this, "单循环", Toast.LENGTH_SHORT).show();
                        btnCode.setBackgroundResource(R.mipmap.ic_circulation);
                        break;
                    case PlayManager.REPOT:
                        PlayManager.setCode(PlayManager.ORDER);
                        Toast.makeText(MusicPlayActivivy.this, "顺序", Toast.LENGTH_SHORT).show();
                        btnCode.setBackgroundResource(R.mipmap.ic_order);
                        break;
                }
            }
        });
        mLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Observable.just(music).map(new Func1<MusicBean, Boolean>() {
                    @Override
                    public Boolean call(MusicBean musicBean) {
                        return IsLike(musicBean);
                    }
                }).subscribe(new Action1<Boolean>() {
                    @Override
                    public void call(Boolean aBoolean) {
                        if (aBoolean) {
                            MainApplication.getLiteOrm().delete(new WhereBuilder(MusicBean.class,
                                    MusicBean.MusicBeamId + "=?",
                                    new String[]{String.valueOf(music.getMusicId())}));
                            mLike.setBackgroundResource(R.mipmap.ic_unlike_white);
                        } else {
                            MainApplication.getLiteOrm().save(music);
                            mLike.setBackgroundResource(R.mipmap.ic_like_white);
                        }
                    }
                });
            }
        });
        btnMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MusicPlayActivivy.this);
                builder.setTitle("歌曲详情");
                builder.setItems(new String[]{
                        "歌名: " + music.getName(),
                        "歌手: " + music.getArtist(),
                        "专辑: " + music.getAlbum(),
                        "时间: " + PlayManager.formatTime(music.getDuration()),
                        "大小: " + TextFormater.getCacheSize(music.getSize()),
                        "文件地址: " + music.getFileData()
                }, null);
                builder.setNegativeButton("确定", null).show();
            }
        });
//        btnDeleteMusic.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                AlertDialog.Builder builder = new AlertDialog.Builder(MusicPlayActivivy.this);
//                builder.setTitle("提示");
//                builder.setMessage("确定删除吗?");
//                //取消删除
//                builder.setNegativeButton("取消", new android.content.DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        dialog.dismiss();
//                    }
//                });
//                //删除
//                builder.setPositiveButton("删除", new android.content.DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        boolean isDeleteOk = PlayManager.delete(MusicPlayActivivy.this, music);
////                        boolean isDeleteOk = PlayManager.deleteMusic(music.getFileData());
//                        if (isDeleteOk) {
//                            Snackbar.make(btnDeleteMusic, "删除功能有些Bug，后期修复", Snackbar.LENGTH_SHORT).show();
//                            RxBus.getDefault().post(new MusicBeamEvent(mPosition));
//                            UpdatePlay(true);
//                        } else {
//                            Snackbar.make(btnDeleteMusic, "删除失败", Snackbar.LENGTH_SHORT).show();
//                        }
//                        dialog.dismiss();
//                    }
//                });
//                builder.show();
//            }
//        });
        btnReturn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        mNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UpdatePlay(true);
            }
        });

        mPrevios.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UpdatePlay(false);
            }
        });

        mPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle = new Bundle();
                bundle.putInt(Config.position, mPosition);
                intent.putExtra(Config.bunder, bundle);
                startService(intent);
                new Thread(run).start();
                UpdateButton(misPlaying);
            }
        });

        mSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            boolean state = false;

            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                //结束拖动时播放拖动位置的音乐，
                int pro = seekBar.getProgress();
                if (PlayManager.mediaPlayer != null) {
                    //公式：      拖动后的播放进度=当前进度条的进度 X 歌曲的总长度 / 进度条的总大小
                    PlayManager.mediaPlayer.seekTo(pro * PlayManager.mediaPlayer.getDuration() / seekBar.getMax());
                    if (state) {
                        PlayManager.mediaPlayer.start();
                    }
                    new Thread(run).start();
                }

            }
        });
    }

    public boolean IsLike(MusicBean musicBean) {
        List<MusicBean> bean = MainApplication.getLiteOrm().query(MusicBean.class);
        for (MusicBean musicBean1 : bean) {
            if (musicBean.getName().equals(musicBean1.getName())) {
                return true;
            }
        }
        return false;
    }

    public void UpdatePlay(boolean isNext) {
        if (PlayManager.getCode() == PlayManager.ORDER) {
            if (isNext) {
                if (mPosition == PlayManager.getMusicSize() - 1) {
                    mPosition = 0;
                }
                mPosition++;
            } else {
                if (mPosition == 0) {
                    mPosition = PlayManager.getMusicSize() - 1;
                }
                mPosition--;
            }

        } else if (PlayManager.getCode() == PlayManager.RANDOM) {
            mPosition = new Random().nextInt(PlayManager.getMusicSize() - 1);
        } else {

        }
        PlayManager.stop();
        PlayManager.play(mPosition, false);
        //修改当前播放音乐的位置
        MusicService.servicePosition = mPosition;
        //根据歌曲位置获取歌曲
        music = PlayManager.getList().get(mPosition);
        //显示音乐的信息
        tvMusicNmae.setText(music.getName());
        tvAuthorName.setText(music.getArtist());
        tvEnd.setText(PlayManager.formatTime(PlayManager.mediaPlayer.getDuration()));
        RxBus.getDefault().post(new MusicCodeEvent());
        UpdateButton(true);
        boolean isLike = IsLike(music);
        if (isLike) {
            mLike.setBackgroundResource(R.mipmap.ic_like_white);
        } else {
            mLike.setBackgroundResource(R.mipmap.ic_unlike_white);
        }
    }


}
