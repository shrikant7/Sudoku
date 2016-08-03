package com.example.sudoku;

import android.content.Context;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.util.Log;

/**
 * Created by User on 6/2/2016.
 */
public class Prefs extends PreferenceActivity{
    //private static final String TAG ="Sudoku";
    private static final String OPT_MUSIC="music";
    private static final String OPT_HINTS="hints";
    private static final boolean OPT_HINTS_DEF=true;
    private static final boolean OPT_MUSIC_DEF =true;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Log.d(TAG,"setting title: Sudoku settingsssssss");
        addPreferencesFromResource(R.xml.settings);

    }

    public static boolean getMusic(Context  context)
    {
        return PreferenceManager.getDefaultSharedPreferences(context).getBoolean(OPT_MUSIC,OPT_MUSIC_DEF);
    }
    public static boolean getHints(Context  context)
    {
        return PreferenceManager.getDefaultSharedPreferences(context).getBoolean(OPT_HINTS,OPT_HINTS_DEF);
    }
}
