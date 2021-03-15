package com.example.simplemusicplayer;

import android.content.res.AssetFileDescriptor;
import android.net.Uri;

public class Song {
    private String name;

    private String time;

    private String singer;

    private String albumt;

    private boolean ischeck = false;

    private Uri songuri;

    public Song(String name, String time, String singer,String albumt) {
        this.name = name;
        this.time = time;
        this.singer = singer;
        this.albumt = albumt;
    }

    public Song(){

    }

    public String getName() {
        return name;
    }

    public String getTime() {
        return time;
    }

    public String getSinger() {
        return singer;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public void setSinger(String singer) {
        this.singer = singer;
    }

    public String getAlbumt() {
        return albumt;
    }

    public void setAlbumt(String albumt) {
        this.albumt = albumt;
    }

    public boolean isIscheck() {
        return ischeck;
    }

    public void setIscheck(boolean ischeck) {
        this.ischeck = ischeck;
    }

    public Uri getSonguri() {
        return songuri;
    }

    public void setSonguri(Uri songuri) {
        this.songuri = songuri;
    }
}
