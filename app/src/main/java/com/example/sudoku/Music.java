package com.example.sudoku;

import android.content.Context;
import android.media.MediaPlayer;

/**
 * Created by User on 6/19/2016.
 */
public class Music {
    private static MediaPlayer mp = null;
    public static void play(Context context,int resourse)
    {
        stop(context);
        if(Prefs.getMusic(context))
        {
            mp=MediaPlayer.create(context,resourse);
            mp.setLooping(true);
            mp.start();
        }
    }
    public static void stop(Context context)
    {
        if(mp!=null)
        {
            mp.stop();
            mp.release();
            mp=null;
        }
    }
}
