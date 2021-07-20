package com.example.mymusic;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.karumi.dexter.Dexter;

import java.io.File;
import java.util.ArrayList;

public class PlayerActivity extends AppCompatActivity {
    Button btn_next2, btn_previous, btn_pause;
    TextView songTextLabel;
    ImageView shuffle_btn , repeat_btn,cover_art;
    SeekBar songSeekBar;
    static MediaPlayer myMediaPlayer;
    int position;
    String sname;
    ArrayList<File> mySongs;
    Thread updateseekBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player2);
        btn_next2 = (Button) findViewById(R.id.next);
        btn_previous = (Button) findViewById(R.id.previous);
        btn_pause = (Button) findViewById(R.id.pause);
        songTextLabel = (TextView) findViewById(R.id.songLabel);
        songSeekBar = (SeekBar) findViewById(R.id.seekBar);
        shuffle_btn =(ImageView)findViewById(R.id.id_shuffle);
        repeat_btn =(ImageView)findViewById(R.id.id_repeat);
        cover_art = (ImageView)findViewById(R.id.cover_art) ;
        getSupportActionBar().setTitle("Now playing");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        updateseekBar = new Thread() {
            @Override
            public void run() {
                int totalDuration = myMediaPlayer.getDuration();
                int currentPosition = 0;
                while (currentPosition < totalDuration) {
                    try {
                        sleep(500);
                        currentPosition = myMediaPlayer.getCurrentPosition();
                        songSeekBar.setProgress(currentPosition);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        };
        if (myMediaPlayer != null) {
            myMediaPlayer.stop();
            myMediaPlayer.release();
        }
        Intent i = getIntent();
        Bundle bundle = i.getExtras();
        mySongs = (ArrayList) bundle.getParcelableArrayList("songs");
        sname = mySongs.get(position).getName().toString();
        String songName = i.getStringExtra("songname");
        songTextLabel.setText(songName);
        songTextLabel.setSelected(true);
        position = bundle.getInt("pos",0);
        Uri uri = Uri.parse(mySongs.get(position).toString());
        myMediaPlayer = MediaPlayer.create(getApplicationContext(),uri);
        myMediaPlayer.start();
        songSeekBar.setMax(myMediaPlayer.getDuration());
        updateseekBar.start();
        songSeekBar.getProgressDrawable().setColorFilter(getResources().getColor(R.color.teal_200), PorterDuff.Mode.MULTIPLY);
        songSeekBar.getThumb().setColorFilter(getResources().getColor(R.color.background),PorterDuff.Mode.SRC_IN);
        songSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            myMediaPlayer.seekTo(seekBar.getProgress());
            }
        });
        btn_pause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                songSeekBar.setMax(myMediaPlayer.getDuration());
            if(myMediaPlayer.isPlaying()){
                btn_pause.setBackgroundResource(R.drawable.icon_play);
                myMediaPlayer.pause();
            }else{
                btn_pause.setBackgroundResource(R.drawable.icon_pause);
                myMediaPlayer.start();
            }
            }
        });
        btn_next2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myMediaPlayer.stop();
                myMediaPlayer.release();
                position =((position+1)%mySongs.size());
                Uri uri = Uri.parse(mySongs.get(position).toString());
                myMediaPlayer = MediaPlayer.create(getApplicationContext(),uri);
                sname = mySongs.get(position).getName().toString();
                songTextLabel.setText(sname);
                myMediaPlayer.start();
            }
        });
        btn_previous.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myMediaPlayer.stop();
                myMediaPlayer.release();
                position =((position-1)<0)?(mySongs.size()-1):(position-1);
                Uri uri = Uri.parse(mySongs.get(position).toString());
                myMediaPlayer = MediaPlayer.create(getApplicationContext(),uri);
                sname = mySongs.get(position).getName().toString();
                songTextLabel.setText(sname);
                myMediaPlayer.start();
            }
        });

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId()== android.R.id.home){
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }
    public void metaData(Uri uri){
        MediaMetadataRetriever retriever= new MediaMetadataRetriever();
        retriever.setDataSource(uri.toString());
        byte[] art =retriever.getEmbeddedPicture();
        if(art != null){
            Glide.with(this)
                    .asBitmap()
                    .load(art)
                    .into(cover_art);
        }else
        {
            Glide.with(this)
                .asBitmap()
                .load(R.drawable.logo)
                .into(cover_art);
        }
    }
}