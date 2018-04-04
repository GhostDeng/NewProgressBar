package com.ghost.newprogressbar;

import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private MusicProgressbar musicProgressbar;

    private TextView tvPlay;

    private Button btnPlay;

    private int count = 0;

    private static final int MSG_PLAY = 0x120;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            ++count;
            //获得我们当前进度
            int progress = count * 100 / 210;
            musicProgressbar.setProgress(progress);

            tvPlay.setText(sToM(count));

            if (progress >= 100) {
                mHandler.removeMessages(MSG_PLAY);
            } else {
                mHandler.sendEmptyMessageDelayed(MSG_PLAY, 1000);
            }
        }
    };

    //秒转为分
    private String sToM(int count) {
        int m = count / 60;
        int s = count % 60;
        if (m <= 9) {
            if (s <= 9) {
                return "0" + m + ":0" + s;
            } else {
                return "0" + m + ":" + s;
            }
        } else {
            if (s <= 9) {
                return m + ":0" + s;
            } else {
                return m + ":" + s;
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //初始化控件
        initView();
        mHandler.sendEmptyMessage(MSG_PLAY);
    }

    int isPlay = 0;

    private void initView() {
        musicProgressbar = findViewById(R.id.play_progressbar);
        tvPlay = findViewById(R.id.tv_play);
        btnPlay = findViewById(R.id.btn_play);
        btnPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isPlay = isPlay % 2;
                if (isPlay == 0) {
                    //暂停播放
                    mHandler.removeMessages(MSG_PLAY);
                    btnPlay.setBackground(getResources().getDrawable(R.drawable.ic_play_arrow_black_30dp));
                } else {
                    //正在播放
                    mHandler.sendEmptyMessage(MSG_PLAY);
                    btnPlay.setBackground(getResources().getDrawable(R.drawable.ic_pause_black_30dp));
                }
                isPlay++;
            }
        });
    }
}
