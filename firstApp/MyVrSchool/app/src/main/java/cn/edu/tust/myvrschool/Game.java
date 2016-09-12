package cn.edu.tust.myvrschool;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

/**
 * Created by MyPC on 2016/9/12.
 */
public class Game extends Activity {
    private static final String TAG ="Sudoku";
    public static final String KEY_DIFFICULTY="cn.edu.tust.myvrschool.difficulty";
    public static final int DIFFICULTY_EASY     = 0;
    public static final int DIFFICULTY_MEDIUM   = 1;
    public static final int DIFFICULTY_HARD     = 2;

    private int puzzle[] = new int[9*9];

    private PuzzleView puzzlieView;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        Log.d(TAG,"onCreate");
        //从intent对象中提取表示难度的数字并选择一局要玩的游戏
        int diff = getIntent().getIntExtra(KEY_DIFFICULTY,DIFFICULTY_EASY);
        //puzzle = getPuzzle(diff);
        //根据数独游戏的规则计算哪些数字对该单元格无效
       // calculateUsedTitles();

        puzzlieView = new PuzzleView(this);

       // setContentView(puzzleView);
        puzzlieView.requestFocus();
    }
}
