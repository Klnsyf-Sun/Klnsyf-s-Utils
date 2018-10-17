package com.klnsyf.ego.dueltimer.activities;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.klnsyf.ego.R;

public class MainActivity extends AppCompatActivity {
    private MediaPlayer mediaPlayer = new MediaPlayer();

    private long totalTime = 0;
    private final int progressBarMaxValue = 65535;
    private boolean round = false;
    private long startTime = 0;

    private long totalTimeTop = 0;
    private int roundTop = 0;
    private long totalTimeBottom = 0;
    private int roundBottom = 0;

    private Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        handler = new Handler(Looper.getMainLooper());

        setContentView(R.layout.activity_main);

        handler.removeCallbacks(timerThread);

        (findViewById(R.id.comfirmButton)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (((EditText) findViewById(R.id.totalTime)).getText().length() != 0) {
                    init();
                    totalTime = Long.parseLong(((EditText) findViewById(R.id.totalTime)).getText().toString());
                    startTime = 7734;
                    TranslateAnimation hiddenAction = new TranslateAnimation(Animation.RELATIVE_TO_SELF,
                            0.0f, Animation.RELATIVE_TO_SELF, 0.0f,
                            Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF,
                            1.0f);
                    hiddenAction.setDuration(300);
                    findViewById(R.id.buttonLayout).startAnimation(hiddenAction);
                    findViewById(R.id.buttonLayout).setVisibility(View.GONE);
                    ((TextView) findViewById(R.id.timeTop)).setText(milliSecond2TimeString(totalTime));
                    ((TextView) findViewById(R.id.timeBottom)).setText(milliSecond2TimeString(totalTime));
                }
            }
        });
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (totalTime > 0) {
                    if (startTime == 7734) {
                        startTime = System.currentTimeMillis();
                        new Thread(() ->
                                handler.post(timerThread)).start();
                        mediaPlayer.start();
                    } else {
                        mediaPlayer.start();
                        if (round) {
                            roundTop++;
                            totalTimeTop += System.currentTimeMillis() - startTime;
                            ((TextView) findViewById(R.id.roundTop)).setText(prefixRound(roundTop));
                            ((TextView) findViewById(R.id.totalTimeTop)).setText(milliSecond2TimeString(totalTimeTop));
                        } else {
                            roundBottom++;
                            totalTimeBottom += System.currentTimeMillis() - startTime;
                            ((TextView) findViewById(R.id.roundBottom)).setText(prefixRound(roundBottom));
                            ((TextView) findViewById(R.id.totalTimeBottom)).setText(milliSecond2TimeString(totalTimeBottom));
                        }
                        round = !round;
                        startTime = System.currentTimeMillis();
                    }
                }
                break;
            default:
                break;
        }
        return super.dispatchTouchEvent(event);
    }

    private void init() {
        totalTimeTop = 0;
        totalTimeBottom = 0;
        roundTop = 0;
        roundBottom = 0;
        startTime = 0;
        round = true;
        handler.removeCallbacks(timerThread);
        if (mediaPlayer != null) {
            mediaPlayer.release();
        }
        ((TextView) findViewById(R.id.timeTop)).setText(milliSecond2TimeString(0));
        ((TextView) findViewById(R.id.timeBottom)).setText(milliSecond2TimeString(0));
        ((TextView) findViewById(R.id.totalTimeTop)).setText(milliSecond2TimeString(0));
        ((TextView) findViewById(R.id.totalTimeBottom)).setText(milliSecond2TimeString(0));
        ((TextView) findViewById(R.id.roundTop)).setText(prefixRound(0));
        ((TextView) findViewById(R.id.roundBottom)).setText(prefixRound(0));
        ((ProgressBar) findViewById(R.id.progressBarTop)).setProgress(progressBarMaxValue);
        ((ProgressBar) findViewById(R.id.progressBarBottom)).setProgress(progressBarMaxValue);
        int[] color = {Color.parseColor("#66ccff")};
        ((ProgressBar) findViewById(R.id.progressBarTop)).setProgressTintList(new ColorStateList(new int[1][1], color));
        ((ProgressBar) findViewById(R.id.progressBarBottom)).setProgressTintList(new ColorStateList(new int[1][1], color));
        initMediaPlayer();
    }

    private void initMediaPlayer() {
        try {
            mediaPlayer = MediaPlayer.create(this, R.raw.beep);
            mediaPlayer.prepare();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String milliSecond2TimeString(long totalMilliSecond) {
        long hour = totalMilliSecond / 1000 / 60 / 60;
        int minute = (int) (totalMilliSecond - hour * 60 * 60 * 1000) / 1000 / 60;
        int second = (int) (totalMilliSecond - hour * 60 * 60 * 1000 - minute * 60 * 1000) / 1000;
        int milliSecond = (int) (totalMilliSecond - hour * 60 * 60 * 1000 - minute * 60 * 1000 - second * 1000);
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append(hour < 10 ? 0 : "").append(hour).append(":").append(minute < 10 ? 0 : "").append(minute).append(":").append(second < 10 ? 0 : "").append(second).append(".").append(milliSecond < 100 ? 0 : "").append(milliSecond < 10 ? 0 : "").append(milliSecond);
        return stringBuffer.toString();
    }

    private String prefixRound(int round) {
        return "Round " + (round < 10 ? "0" : "") + round;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.setTimer_item:
                if (findViewById(R.id.buttonLayout).getVisibility() != View.VISIBLE) {
                    init();
                    TranslateAnimation showAction = new TranslateAnimation(Animation.RELATIVE_TO_SELF,
                            0.0f, Animation.RELATIVE_TO_SELF, 0.0f,
                            Animation.RELATIVE_TO_SELF, 1.0f, Animation.RELATIVE_TO_SELF,
                            0.0f);
                    showAction.setDuration(300);
                    findViewById(R.id.buttonLayout).startAnimation(showAction);
                    findViewById(R.id.buttonLayout).setVisibility(View.VISIBLE);
                }
                return true;
            default:
                return false;
        }
    }

    Runnable timerThread = new Runnable() {
        @Override
        public void run() {
            TextView timeView, roundView, totalTimeView;
            ProgressBar progressBar;
            if (round) {
                timeView = findViewById(R.id.timeTop);
                progressBar = findViewById(R.id.progressBarTop);
                roundView = findViewById(R.id.roundTop);
                totalTimeView = findViewById(R.id.totalTimeTop);
            } else {
                timeView = findViewById(R.id.timeBottom);
                progressBar = findViewById(R.id.progressBarBottom);
                roundView = findViewById(R.id.roundBottom);
                totalTimeView = findViewById(R.id.totalTimeBottom);
            }
            handler.postDelayed(this, 5);
            if (System.currentTimeMillis() - startTime > totalTime) {
                timeView.setText(milliSecond2TimeString(0));
                progressBar.setProgress(0);
                startTime += 5000;
                if (round) {
                    totalTimeTop += 5000;
                } else {
                    totalTimeBottom += 5000;
                }
                mediaPlayer.start();
            } else {
                timeView.setText(milliSecond2TimeString(totalTime - (System.currentTimeMillis() - startTime)));
                progressBar.setProgress((int) (progressBarMaxValue * (1 - (double) (System.currentTimeMillis() - startTime) / (double) totalTime)));
                int[] color = {Color.parseColor((1 - (double) (System.currentTimeMillis() - startTime) / (double) totalTime) > 0.5 ? "#66ccff" : (1 - (double) (System.currentTimeMillis() - startTime) / (double) totalTime) > 0.2 ? "#39c5bb" : (1 - (double) (System.currentTimeMillis() - startTime) / (double) totalTime) > 0.1 ? "#ff6600" : "#ff0000")};
                progressBar.setProgressTintList(new ColorStateList(new int[1][1], color));
            }
            totalTimeView.setText(milliSecond2TimeString((System.currentTimeMillis() - startTime) + (round ? totalTimeTop : totalTimeBottom)));
        }
    };

}

