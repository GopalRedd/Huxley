package com.example.audioplayer;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {

    private MediaPlayer mediaPlayer;
    private TextView playButton;

    private TextView currentTimeTextView;
    private TextView endTimeTextView;
    private SeekBar seekBar;
    private Handler handler = new Handler();


    private String audioUrl = "https://phr-files-local.s3.amazonaws.com/d235c9b2-29e0-40a2-b8d9-ca012ab60600.wav?AWSAccessKeyId=ASIAYR7IVQALXKV34OJB&Signature=digXfUproLbi3NwiV1gF0O4Vdq8%3D&x-amz-security-token=IQoJb3JpZ2luX2VjEEEaCmFwLXNvdXRoLTEiRzBFAiAZJtABq2XNn11ppKdGDvn3ubfTs9H9hY76%2FCf9LH0iSgIhAMABEynSPif2F3Wz45XXx6CMBQqTXxA4fN%2B9JZO9M5mwKvECCEoQBBoMNTg4MzYxNTk2OTUxIgyj5oGTAWFyGSES%2BegqzgLQS%2BeRLuM7XGJSS8bsw0TgePicC0udKxVoSHHgN6dcII4RPd%2Bc8KTvWcFUcAGTUOB6MkplnmacnvtBOcA0leGoZjrO20ft8ZUmLZvjofcdkJpzmnPAo9LGuPCQwfv7ixtQ8grwO3%2B5PSsS0Kw78Hik673q%2Fy09iQBWDvK2bRMeaWGYzaAEw5lp%2FRDYbnrUMmUW%2BHjrPJlIvXAz7Y8tWTpW9JxBOv5xLdk7HlR1kK5WjU%2BgexVZmnhdboJvx0dpOOZUnrDmtTkUqB%2BR1fkZtuK%2FHClMRl68Qxb1eKKN90mLgkANBNeUzwwDLYc3PfLdQa1bjsA6XEJMl724rbuKD7TYiF1fTAkLodJ%2FBZoapMJf0ro6ztKAE4c2N30p4YCJ%2FnsipWl4qtB0%2BS533kiP%2F%2BOvNPtv9SzmbzBGmO0GMgkslsG6GfbPHnBdvBWTAgojMOGVwq8GOp4BoOM5uTEFpc52rgzjTl%2FCeniPkFSr5oJrQIyF4xawkwZhIsFsj6gWqyzVztjC%2FRZIe%2F9hmCid1lvm5T%2B7VRf4P3H0Ul1iAyYmvr0uIJBMYMHMt2Zu%2BclqIuDSIYNfTdT08xLkOFwv%2FmXZAix1OoXf3TFruk%2Bv861nyD4sqzYnqAdtORr41DOJGQAwMOYOHbAmOoyL2R3Ns5y2JF%2B8%2FgA%3D&Expires=1710767818";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        playButton = findViewById(R.id.play);
        currentTimeTextView = findViewById(R.id.currentTimeTextView);
        endTimeTextView = findViewById(R.id.endTimeTextView);
        seekBar = findViewById(R.id.seekBar);

        mediaPlayer = new MediaPlayer();
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);



        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mediaPlayer.isPlaying()) {
                    mediaPlayer.pause();
                    playButton.setText("Play");
                } else {
                    if (mediaPlayer.getCurrentPosition() > 0) {
                        mediaPlayer.start();
                        playButton.setText("Pause");
                    } else {
                        try {
                            mediaPlayer.setDataSource(audioUrl);
                            mediaPlayer.prepareAsync();
                            mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                                @Override
                                public void onPrepared(MediaPlayer mp) {
                                    mediaPlayer.start();
                                    playButton.setText("Pause");
                                    updateTime();
                                }
                            });
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        });

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    mediaPlayer.seekTo(progress);
                    updateTime();
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });


        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                playButton.setText("Play");
            }
        });


        mediaPlayer.setOnSeekCompleteListener(new MediaPlayer.OnSeekCompleteListener() {
            @Override
            public void onSeekComplete(MediaPlayer mp) {
                if (!mediaPlayer.isPlaying()) {
                    mediaPlayer.start();
                    playButton.setText("Pause");
                }
            }
        });
    }

    private void updateTime() {
        final long duration = mediaPlayer.getDuration();
        long current = mediaPlayer.getCurrentPosition();
        currentTimeTextView.setText(String.format("%d:%02d",
                TimeUnit.MILLISECONDS.toMinutes(current),
                TimeUnit.MILLISECONDS.toSeconds(current) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(current))
        ));
        endTimeTextView.setText(String.format("%d:%02d",
                TimeUnit.MILLISECONDS.toMinutes(duration),
                TimeUnit.MILLISECONDS.toSeconds(duration) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(duration))
        ));
        seekBar.setMax((int) duration);
        seekBar.setProgress((int) current);

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (mediaPlayer.isPlaying()) {
                    updateTime();
                }
            }
        }, 1000);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }



}