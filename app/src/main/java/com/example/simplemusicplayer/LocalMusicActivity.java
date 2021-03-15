package com.example.simplemusicplayer;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.textview.MaterialTextView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

// 多加了一行注释

public class LocalMusicActivity extends AppCompatActivity implements View.OnClickListener {

    private List<Song> songList = null;

    private MusicAdpater adpater = null;

    private LinearLayoutManager manager = new LinearLayoutManager(LocalMusicActivity.this);

    private RecyclerView recyclerView = null;

    private Window window;

    private int oldPosition = -1;

    private SwipeRefreshLayout swipeRefreshLayout;

    private int currentPos;

    private MaterialTextView songname;

    private MaterialButton btnPlay;

    private MediaPlayer mediaPlayer;

    private RelativeLayout relativeLayout;

   private Animation roater;

    private ShapeableImageView imageView;


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mediaPlayer = new MediaPlayer();
        roater = AnimationUtils.loadAnimation(this,R.anim.view_ainm);
        LinearInterpolator lin = new LinearInterpolator();
        roater.setInterpolator(lin);
        window = getWindow();
        window.setStatusBarColor(Color.BLACK);
        setContentView(R.layout.activity_local_music);
        if (ContextCompat.checkSelfPermission(
                this, Manifest.permission.READ_EXTERNAL_STORAGE) ==
                PackageManager.PERMISSION_GRANTED) {
            loadMusic();
            showMusic();
        } else if (shouldShowRequestPermissionRationale("")) {
            // In an educational UI, explain to the user why your app requires this
            // permission for a specific feature to behave as expected. In this UI,
            // include a "cancel" or "no thanks" button that allows the user to
            // continue using your app without granting the permission.
            ;
        } else {
            // You can directly ask for the permission.
            requestPermissions(
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    1);
            // 在实际编写中，这里的CONTEXT默认为this, 貌似只接受两个参数
        }

        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swiperlayout);
        swipeRefreshLayout.setColorSchemeColors(this.getColor(R.color.teal_700));
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refresh();
            }
        });
        songname = (MaterialTextView) findViewById(R.id.song_name_view);
        btnPlay = (MaterialButton) findViewById(R.id.play_btn);
        Button button_back = (Button) findViewById(R.id.button_back);
        button_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        relativeLayout = (RelativeLayout) findViewById(R.id.play_btn_view);
        relativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mediaPlayer == null) {
                    //没有播放过音乐 ,点击之后播放第一首
                    oldPosition = 0;
                    currentPos = 0;
                    songList.get(currentPos).setIscheck(true);
                    adpater.changeState();
                    imageView.startAnimation(roater);
                    playSong(currentPos);
                } else {
                    //播放过音乐  暂停或者播放
                    if (mediaPlayer.isPlaying()) {
                        mediaPlayer.pause();
                        imageView.clearAnimation();
                        btnPlay.setIcon(getDrawable(R.drawable.paly));
                        btnPlay.setIconTint(getColorStateList(R.color.black));
                        btnPlay.setIconSize((int) getResources().getDimension(R.dimen.dp_24));

                    } else {
                        mediaPlayer.start();
                        imageView.startAnimation(roater);
                        btnPlay.setIcon(getDrawable(R.drawable.tomare));
                        btnPlay.setIconTint(getColorStateList(R.color.black));
                        btnPlay.setIconSize((int) getResources().getDimension(R.dimen.dp_24));
                    }
                }

            }
        });
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                int position = -1;
                if (songList != null) {
                    oldPosition = currentPos;
                    if (currentPos == songList.size()-1) {
                        position = 0;
                    } else {
                        position = currentPos++;
                    }
                }
                currentPos = position;
                changeSongs(position);
            }
        });
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                int position = -1;
                if (songList != null) {
                    oldPosition = currentPos;
                    if (currentPos == songList.size()-1) {
                        position = 0;
                    } else {
                        position = currentPos+1;
                    }
                }
                currentPos = position;
                changeSongs(position);
            }
        });

        MusicRoundProgressView progressView = (MusicRoundProgressView) findViewById(R.id.paly_progress);


        Handler handler = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(@NonNull Message msg) {
                switch (msg.what){
                    case 1:
                    int postion = msg.arg1;
                    progressView.setmProgress(postion,mediaPlayer.getDuration());
                    break;
                }
                return true;
            }
        });

        new Thread(new Runnable() {
            @Override
            public void run() {
                while (mediaPlayer!=null){
                    Message msg = Message.obtain();
                    msg.what = 1;
                    msg.arg1 = mediaPlayer.getCurrentPosition();
                    handler.sendMessage(msg);
                    try{
                        Thread.sleep(1000);
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
            }
        }).start();




    }



    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {
        switch (requestCode) {
            case 1:
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 &&
                        grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    loadMusic();
                    showMusic();
                } else {
                    // Explain to the user that the feature is unavailable because
                    // the features requires a permission that the user has denied.
                    // At the same time, respect the user's decision. Don't link to
                    // system settings in an effort to convince the user to change
                    // their decision.
                }
                return;
        }
        // Other 'case' lines to check for other
        // permissions this app might request.
    }

    public void loadMusic() {
        songList = new ArrayList<>();
        Cursor cursor = getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, null, null, null, MediaStore.Audio.AudioColumns.IS_MUSIC);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                Song song = new Song();
                song.setName(cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE)));
                song.setSinger(cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST)));
                song.setTime(cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION)));
                if (song.getName().contains("-")) {
                    String[] str = song.getName().split("-");
                    song.setSinger(str[1]);
                    song.setName(str[0]);
                }
                String mediaid = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID));
                Uri songuri = Uri.parse(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI.toString() + File.separator + mediaid);
                song.setSonguri(songuri);
                if (song.getTime() != null) {
                    songList.add(song);
                }

            }
        }
    }

    public void showMusic() {
        recyclerView = (RecyclerView) findViewById(R.id.toolbar);
        manager.setOrientation(RecyclerView.VERTICAL);
        recyclerView.setLayoutManager(manager);
        adpater = new MusicAdpater(R.layout.music_item, songList, this);
        recyclerView.setAdapter(adpater);
        adpater.setOnItemChildClickListener((adapter, view, position) -> {
            if (view.getId() == R.id.music_item) {
                playPostionControl(position);
                currentPos = position;
                playSong(currentPos);
            }
            adpater.changeState();
        });
    }

    public void refresh() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        resetMusic();
                        swipeRefreshLayout.setRefreshing(false);
                    }
                });
            }
        }).start();
    }

    public void resetMusic() {
        songList = null;
        recyclerView = null;
        loadMusic();
        showMusic();
    }

    private void playPostionControl(int position) {
        if (oldPosition == -1) {
            oldPosition = position;
            songList.get(position).setIscheck(true);
            adpater.notifyItemChanged(position);
        } else {
            if (oldPosition != position) {
                songList.get(oldPosition).setIscheck(false);
                adpater.notifyItemChanged(oldPosition);
                songList.get(position).setIscheck(true);
                oldPosition = position;
                adpater.notifyItemChanged(position);

            }
        }
    }

    public void playSong(int position) {
        if (mediaPlayer == null) {
            mediaPlayer = new MediaPlayer();

        }
        try {
            //切歌前先重置，释放掉之前的资源
            mediaPlayer.reset();

            imageView = (ShapeableImageView) findViewById(R.id.shapeableImageView);
            //设置播放音频的资源路径
            mediaPlayer.setDataSource(this, songList.get(position).getSonguri());
            //设置播放的歌名和歌手
            songname.setText(songList.get(position).getName() + " - " + songList.get(position).getSinger());
            //如果内容超过控件，则启用跑马灯效果
            songname.setSelected(true);
            //开始播放前的准备工作，加载多媒体资源，获取相关信息
            mediaPlayer.prepare();
            //开始播放音频
            mediaPlayer.start();

            //播放按钮控制
            if (mediaPlayer.isPlaying()) {
                imageView.startAnimation(roater);
                btnPlay.setIcon(getDrawable(R.drawable.tomare));
                btnPlay.setIconTint(getColorStateList(R.color.black));
                btnPlay.setIconSize((int) getResources().getDimension(R.dimen.dp_24));

            } else {
                imageView.clearAnimation();
                btnPlay.setIcon(getDrawable(R.drawable.paly));
                btnPlay.setIconTint(getColorStateList(R.color.black));
                btnPlay.setIconSize((int) getResources().getDimension(R.dimen.dp_24));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }



    public void changeSongs(int positon) {
        playPostionControl(positon);
        playSong(positon);
    }


    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.upsong:{
                int position = -1;
                if (songList != null) {
                    oldPosition = currentPos;
                    if (currentPos == 0) {
                        position = songList.size() - 1;
                    } else {
                        position = currentPos-1;
                    }
                }
                currentPos = position;
                changeSongs(position);
                break;
            }
            case R.id.downsong:
                int position = -1;
                if (songList != null) {
                    oldPosition = currentPos;
                    if (currentPos == songList.size()-1) {
                        position = 0;
                    } else {
                        position = currentPos+1;
                    }
                }
                currentPos = position;
                changeSongs(position);
                break;
            default:
                break;
        }
    }
}

