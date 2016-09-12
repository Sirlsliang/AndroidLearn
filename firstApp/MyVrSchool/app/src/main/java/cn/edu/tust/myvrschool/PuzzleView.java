package cn.edu.tust.myvrschool;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.Log;
import android.view.View;

/**
 * Created by MyPC on 2016/9/12.
 */
public class PuzzleView extends View {
    private static final String TAG = "Sudoku";
    private final Game game;

    private float width;  // width of one title
    private float height; // height of one title
    private int     selX; //X index of selection
    private int     selY; //Y index of selection
    private final Rect selRect = new Rect();

    public PuzzleView(Context context){
        super(context);
        this.game = (Game)context;
        setFocusable(true);
        setFocusableInTouchMode(true);
        setFocusableInTouchMode(true);
    }

    protected void onSizeChanged(int w,int h,int oldw,int oldh){
        width = w / 9f;
        height = h / 9f;
        getRect(selX,selY,selRect);
        Log.d(TAG,"onSizeChanged: width"+width+", height:"+height);
        super.onSizeChanged(w,h,oldw,oldh);
    }
    private void getRect(int x,int y, Rect rect){
        rect.set((int)(x*width),(int)(y*height),(int)(x*width+width),(int)(y*height+height));
    }

    @Override
    protected void onDraw(Canvas canvas){
        Paint background = new Paint();
        background.setColor(getResources().getColor(R.color.puzzle_background));
        canvas.drawRect(0,0,getWidth(),getHeight(),background);

    }
}
