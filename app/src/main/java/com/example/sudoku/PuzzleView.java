package com.example.sudoku;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.IdRes;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AnimationUtils;

/**
 * Created by User on 6/3/2016.
 */
public class PuzzleView extends View {
    public static final String TAG="Sudoku";
    private final Game game;

    private static final String SELX = "selX";
    private static final String SELY = "selY";
    private static final String VIEW_STATE = "viewState";
    @IdRes
    private static final int ID = 42;
    public PuzzleView(Context context) {
        super(context);
        this.game= (Game) context;
        setFocusable(true);
        setFocusableInTouchMode(true);
        setId(ID);   //manually has to set id ,no xml is here.
    }
    private float width,height;
    private int selX,selY;
    private final Rect selRect= new Rect();
    private Rect puzRect =new Rect();
    protected void onSizeChanged(int w,int h,int oldw,int oldh)
    {
        width = w/9f;
        height = h/9f;
        getRect(selX,selY,selRect);
        Log.d(TAG,"inSizeChanged width = "+width+" and height = "+height+" whole view size : w="+w+" h="+h);
        super.onSizeChanged(w,h,oldw,oldh);
    }
    private void getRect(int x,int y,Rect rect)
    {
        rect.set((int)(x*width),(int)(y*height),(int)(x*width+width),(int)(y*height+height));
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        Log.d(TAG,"onRestoreInstanceState");
        Bundle bundle= (Bundle)state;
        select(bundle.getInt(SELX),bundle.getInt(SELY));

        super.onRestoreInstanceState(bundle.getParcelable(VIEW_STATE));
    }

    @Override
    protected Parcelable onSaveInstanceState() {
        Parcelable p = super.onSaveInstanceState();
        Log.d(TAG,"onSaveInstanceState");
        Bundle bundle = new Bundle();
        bundle.putInt(SELX,selX);
        bundle.putInt(SELY,selY);
        bundle.putParcelable(VIEW_STATE,p);
        return bundle;
    }

    protected void onDraw(Canvas canvas)
    {   // Drawing background color.
        Paint background = new Paint();
        background.setColor(getResources().getColor(R.color.puzzle_background));

        canvas.drawRect(0,0,getWidth(),getHeight(),background);

        //drawing board
        Paint dark=new Paint();
        dark.setColor(getResources().getColor(R.color.puzzle_dark));
        Paint light=new Paint();
        light.setColor(getResources().getColor(R.color.puzzle_light));
        Paint hilight=new Paint();
        hilight.setColor(getResources().getColor(R.color.puzzle_hilight));
        Paint blur =new Paint();
        blur.setColor(getResources().getColor(R.color.puzzle_blur));
        // drawing minor grid lines.

        for (int i=0;i<9;i++)
        {
            canvas.drawLine(0,i*height,getWidth(),i*height,light);
            canvas.drawLine(0,i*height+1,getWidth(),i*height+1,light);
            canvas.drawLine(0,i*height+2,getWidth(),i*height+2,hilight);
            canvas.drawLine(i*width,0,i*width,getHeight(),light);
            canvas.drawLine(i*width+1,0,i*width+1,getHeight(),light);
            canvas.drawLine(i*width+2,0,i*width+2,getHeight(),hilight);

        }
        // drawing major grid lines.
        for(int i=0;i<9;i++)
        {
            if(i%3==0)
            {
                canvas.drawLine(0,i*height,getWidth(),i*height,dark);
                canvas.drawLine(0,i*height+1,getWidth(),i*height+1,dark);
                canvas.drawLine(0,i*height+3,getWidth(),i*height+3,hilight);
                canvas.drawLine(i*width,0,i*width,getHeight(),dark);
                canvas.drawLine(i*width+1,0,i*width+1,getHeight(),dark);
                //canvas.drawLine(i*width+2,0,i*width+2,getHeight(),hilight);
                canvas.drawLine(i*width+3,0,i*width+3,getHeight(),hilight);
            }
        }

        //drawing the numbers...
        //defining color and style for numbers.
        Paint foreground = new Paint(Paint.ANTI_ALIAS_FLAG);
        foreground.setColor(getResources().getColor(R.color.puzzle_foreground));
        foreground.setStyle(Paint.Style.FILL);
        foreground.setTextSize(height*0.70f);
        foreground.setTextScaleX(width/height);
        foreground.setTextAlign(Paint.Align.CENTER);

        //draw the number in center of tile.
        Paint.FontMetrics fm = foreground.getFontMetrics();
        //centering in x
        float x = width/2;
        // centering in y: below mid-height = base of number.
        float y = height/2 - (fm.ascent+fm.descent)/2;
        for(int i=0;i<9;i++)
            for(int j=0;j<9;j++)
            {

                if(this.game.origPuzzle[j*9+i] != 0)
                {
                    getRect(i,j,puzRect);
                    canvas.drawRect(puzRect,blur);
                    canvas.drawText(String.valueOf(this.game.origPuzzle[j*9+i]),i*width+x,j*height+y,foreground);
                }
                else
                    canvas.drawText(this.game.getTileString(i,j),i*width+x,j*height+y,foreground);
            }
        //draw the selection...
        Log.d(TAG,"selRect = "+selRect);
        Paint selected = new Paint();
        selected.setColor(getResources().getColor(R.color.puzzle_selected));
        canvas.drawRect(selRect,selected);
    }
    // giving touchEvents

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if(event.getAction()!=MotionEvent.ACTION_DOWN)
            return super.onTouchEvent(event);
        select((int)(event.getX()/width),(int)(event.getY()/height));

        game.showKeypadOrError(selX,selY);
        Log.d(TAG,"onTouchEvent: x "+selX+", y "+selY);
        return  true;
    }
    private void select(int x,int y)
    {
        invalidate(selRect);
        selX = Math.min(Math.max(x,0),8);
        selY = Math.min(Math.max(y,0),8);
        getRect(selX,selY,selRect);
        invalidate(selRect);

    }
    public void setSelectedTile(int tile)
    {
        if(game.setTileIfValid(selX,selY,tile))
        {
            invalidate();
            //
        }
        else
        {
            //number is not valid for this tile.
            Log.d(TAG,"setSelectedTile: invalid: "+tile);
        }
    }
}
