package com.example.simplemusicplayer;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;


import java.util.List;

public class MusicAdpater extends BaseQuickAdapter<Song, BaseViewHolder> {

    private Context mContext;

    public static String timeParse(long duration) {
        String time = "" ;

        long minute = duration / 60000 ;
        long seconds = duration % 60000 ;

        long second = Math.round((float)seconds/1000) ;

        if( minute < 10 ){
            time += "0" ;
        }
        time += minute+":" ;

        if( second < 10 ){
            time += "0" ;
        }
        time += second ;

        return time ;
    }


    public MusicAdpater(int layoutResId, @Nullable List<Song> data, Context context) {
        super(layoutResId, data);
        mContext = context;
    }


    @Override
    protected void convert(BaseViewHolder helper, Song item){
        helper.setText(R.id.song_name,item.getName());
        helper.setText(R.id.singer,item.getSinger());
        String time = item.getTime();
        helper.setText(R.id.duration_time, timeParse(Long.parseLong(item.getTime())));
        helper.setText(R.id.position,String.valueOf(helper.getAdapterPosition()+1));
        helper.addOnClickListener(R.id.music_item);

        if(item.isIscheck()){
            helper.setTextColor(R.id.singer, mContext.getColor(R.color.green))
                    .setTextColor(R.id.duration_time,mContext.getColor(R.color.green))
                    .setTextColor(R.id.song_name,mContext.getColor(R.color.green));
            helper.setBackgroundColor(R.id.position,mContext.getColor(R.color.green));
            helper.setText(R.id.position," ");
        }else{
            helper.setTextColor(R.id.singer, mContext.getColor(R.color.black))
                    .setTextColor(R.id.duration_time,mContext.getColor(R.color.black))
                    .setTextColor(R.id.song_name,mContext.getColor(R.color.black));
            helper.setBackgroundColor(R.id.position,mContext.getColor(R.color.white));
            helper.setText(R.id.position,String.valueOf(helper.getAdapterPosition()+1));
        }
    }

    public void changeState(){
        notifyDataSetChanged();
    }


}
