package com.example.sudoku;

import android.app.Activity;
import android.app.Dialog;
import android.nfc.Tag;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.widget.Toast;

/**
 * Created by User on 6/3/2016.
 */
public class Game extends Activity{
    public static final String TAG="Sudoku";
    public static final String KEY_DIFFICULTY="com.example.sudoku.difficulty";
    public static final int Difficulty_easy = 0;
    public static final int Difficulty_medium = 1;
    public static final int Difficulty_hard = 2;

    private int puzzle[];
    protected int origPuzzle[];
    private PuzzleView puzzleView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG,"onCreate of Game");

        int diff=getIntent().getIntExtra(KEY_DIFFICULTY,Difficulty_easy);
        puzzle = getPuzzle(diff);
        origPuzzle=getPuzzle(diff);
        calculateUsedTiles();

        puzzleView = new PuzzleView(this);
        setContentView(puzzleView);
        puzzleView.requestFocus();

        //if activity is restarted,do continue next time.
        getIntent().putExtra(KEY_DIFFICULTY,DIFFICULTY_CONTINUE);
    }
    protected void showKeypadOrError(int x,int y)
    {
        if(origPuzzle[y*9+x]==0)
        {
            int tiles[] = getUsedTiles(x,y);
            if(tiles.length == 9)
            {
                Toast toast = Toast.makeText(this,R.string.no_move_l,Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER,0,0);
                toast.show();
            }
            else
            {
                Log.d(TAG,"show keypad used: used= "+toPuzzleString(tiles));
                Dialog v=new Keypad(this,tiles,puzzleView);
                v.show();
            }
        }

    }
    protected boolean setTileIfValid(int x,int y,int value)
    {
        int tiles[] = getUsedTiles(x,y);
        if(value!=0)
        {
            for(int tile : tiles)
            {
                if(tile == value)
                    return false;
            }
        }
        setTitle(x,y,value);
        calculateUsedTiles();
        return true;
    }

    private final int used[][][] = new int[9][9][];

    protected int[] getUsedTiles(int x,int y)
    {
        return  used[x][y];
    }

    private void calculateUsedTiles()
    {
        for (int x=0;x<9;x++){
            for(int y=0;y<9;y++)
            {
                used[x][y] = calculateUsedTiles(x,y);
                //Log.d(TAG,"used["+x+"]["+y+"] = "+toPuzzleString(used[x][y]));

            }
        }
    }
    private int[] calculateUsedTiles(int x,int y)
    {
        int c[] = new int[9];
        //horizontal
        for(int i=0;i<9;i++)
        {
            if(i==x)
                continue;
            int t=getTile(i,y);
            if(t!=0)
                c[t-1]=t;

        }
        //vertical
        for(int i=0;i<9;i++)
        {
            if(i==y)
                continue;
            int t=getTile(x,i);
            if(t!=0)
                c[t-1]=t;

        }
        //same cell block
        int startx = (x/3)*3;
        int starty = (y/3)*3;
        for(int i=startx;i<startx+3;i++){
            for (int j=starty;j<starty+3;j++)
            {
                if(i==x && j==y)
                    continue;
                int t=getTile(i,j);
                if(t!=0)
                    c[t-1]=t;
            }
        }
        //compress
        int nused=0;
        for(int t:c)
        {
            if(t!=0)
                nused++;
        }
        int c1[] = new int[nused];
        nused=0;
        for(int t:c)
        {
            if(t!=0)
                c1[nused++]=t;
        }
        return c1;
    }

    private final String easyPuzzle =
                    "360000000004230800000004200"+
                    "070460003820000014500013020"+
                    "001900000007048300000000045";
    private final String mediumPuzzle =
                    "650000070000506000014000005"+
                    "007009000002314700000700800"+
                    "500000630000201000030000097";
    private final String hardPuzzle=
                    "009000000080605020501078000"+
                    "000000700706040102004000000"+
                    "000720903090301080000000600";
    private int[] getPuzzle(int diff)
    {
        String puz;
        switch (diff)
        {
            case DIFFICULTY_CONTINUE:
                puz = getPreferences(MODE_PRIVATE).getString(PREF_PUZZLE,easyPuzzle);
                Log.d(TAG,"getPuzzle="+puz);
                break;
            case Difficulty_hard: puz=hardPuzzle;
                break;
            case Difficulty_medium: puz=mediumPuzzle;
                break;
            case Difficulty_easy: puz=easyPuzzle;
                break;
            default: puz=easyPuzzle;
                break;
        }
        return fromePuzzleString(puz);
    }
    static private String toPuzzleString(int[] puz)
    {
        StringBuilder buf=new StringBuilder();
        for (int element:puz)
        {
            buf.append(element);
        }
        return buf.toString();

    }
     static protected int[] fromePuzzleString(String s)
    {
        int[] puz= new int[s.length()];
        for(int i=0;i<puz.length;i++)
        {
            puz[i] =s.charAt(i)-'0';
        }
        return puz;
    }

    private int getTile(int x,int y)
    {
        return puzzle[y*9+x];
    }
    private void setTitle(int x,int y,int value)
    {
        puzzle[y*9+x]=value;
    }
    protected String getTileString(int x,int y) {
        int v = getTile(x, y);
        if (v == 0) {
            return "";
        } else {
            return String.valueOf(v);
        }
    }

    private static final String PREF_PUZZLE = "puzzle";
    protected static final int DIFFICULTY_CONTINUE = -1;

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG,"onPause");
        //Music.stop(this);

        //save the current puzzle;
        Log.d(TAG,"onPausePuzzle="+toPuzzleString(puzzle));
        getPreferences(MODE_PRIVATE).edit().putString(PREF_PUZZLE,toPuzzleString(puzzle)).commit();
    }
}
