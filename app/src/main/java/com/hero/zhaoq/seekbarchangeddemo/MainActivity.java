package com.hero.zhaoq.seekbarchangeddemo;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.media.AudioManager;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.SeekBar;

/**
 * Android实现  SeekBar更新音量  并实现  和硬件同步
 */
public class MainActivity extends AppCompatActivity {


    private SeekBar seekbar_video;

    private ContentObserver mVoiceObserver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);//获取媒体系统服务
        seekbar_video= (SeekBar) findViewById(R.id.seekbar_video);//注册ID
        seekbar_video.setMax(15);//设置最大音量
        seekbar_video.setProgress(audioManager.getStreamVolume(AudioManager.STREAM_MUSIC));// 当前的媒体音量
        myRegisterReceiver();//注册同步更新的广播

        Log.i("lyj_ring", "mVoiceSeekBar max voluem = "+audioManager.getStreamMaxVolume(AudioManager.STREAM_SYSTEM));
        seekbar_video.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            public void onStopTrackingTouch(SeekBar arg0) {
            }

            public void onStartTrackingTouch(SeekBar arg0) {
            }
            public void onProgressChanged(SeekBar arg0, int arg1, boolean arg2) {
                AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
                Log.v("lyj_ring", "mVoiceSeekBar max progress = "+arg1);
                //系统音量和媒体音量同时更新
                audioManager.setStreamVolume(AudioManager.STREAM_SYSTEM, arg1, 0);
                audioManager.setStreamVolume(3, arg1, 0);//  3 代表  AudioManager.STREAM_MUSIC
            }
        });
        mVoiceObserver = new ContentObserver(new Handler()) {
            @Override
            public void onChange(boolean selfChange) {
                super.onChange(selfChange);
                AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
                seekbar_video.setProgress(audioManager.getStreamVolume(AudioManager.STREAM_SYSTEM));
                //或者你也可以用媒体音量来监听改变，效果都是一样的。
                //mVoiceSeekBar.setProgress(audioManager.getStreamVolume(AudioManager.STREAM_MUSIC));
            }
        };
    }

    private void myRegisterReceiver(){
        MyVolumeReceiver  mVolumeReceiver = new MyVolumeReceiver() ;
        IntentFilter filter = new IntentFilter() ;
        filter.addAction("android.media.VOLUME_CHANGED_ACTION") ;
        registerReceiver(mVolumeReceiver, filter) ;
    }

    /**
     * 处理音量变化时的界面显示
     * @author long
     */
    private class MyVolumeReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            //如果音量发生变化则更改seekbar的位置
            if(intent.getAction().equals("android.media.VOLUME_CHANGED_ACTION")){
                AudioManager mAudioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
                int currVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC) ;// 当前的媒体音量
                seekbar_video.setProgress(currVolume) ;
            }
        }
    }
}
