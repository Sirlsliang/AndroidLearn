package cn.edu.tust.myvrschool;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.LogPrinter;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
/*
*   这个是主要的Activity，在Manifest.xml文件中配置的启动顺序。
*
* */
public class MainActivity extends AppCompatActivity implements OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG,"on Create");
        super.onCreate(savedInstanceState);
        //设置布局
        setContentView(R.layout.activity_main);
        //set up listeners for all the buttons
        View aboutButton = findViewById(R.id.about_button);
        aboutButton.setOnClickListener(this);
        View newButton  = findViewById(R.id.new_game_button);
        newButton.setOnClickListener(this);
        View exitButton = findViewById(R.id.exit_button);
        exitButton.setOnClickListener(this);
        View continueButton = findViewById(R.id.continue_button);
        continueButton.setOnClickListener(this);
        //这个是利用新建的Graphic类来创建View
        // setContentView(new GraphicsView(this));

    }

    static public class GraphicsView extends View{
        public GraphicsView(Context context){
            super(context);
        }
        //重写onDraw()方法，绘出视图
        @Override
        protected void onDraw(Canvas canvas){
            Path circle = new Path();
            circle.addCircle(150,150,100, Path.Direction.CW);
            Paint cPaint = new Paint();
            int color = getResources().getColor(R.color.myColor);
            cPaint.setColor(color);
            canvas.drawPath(circle,cPaint);
            String qu = getResources().getString(R.string.quote);
            canvas.drawTextOnPath(qu,circle,0,20,cPaint);

        }
    }

    //创建选项按钮，这个是settings中的选项
    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        super.onCreateOptionsMenu(menu);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu,menu);
        return true;
    }

    //选项按钮中的选项被按下之后就会调用该方法。
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){
            case R.id.settings:
                startActivity(new Intent(this,Prefs.class));
                return true;
        }
        return false;
    }
    //点击各个按钮时出发的事件
    @Override
    public void onClick(View v){
        switch (v.getId()){
            case R.id.continue_button:
                startGame(Game.DIFFICULTY_CONTINUE);
                break;
            case R.id.about_button:
                Intent i = new Intent(this,About.class);
                startActivity(i);
                break;
            case R.id.new_game_button:
                openNewGameDialog();
                break;
            case R.id.exit_button:
                finish();
                break;
        }
    }
    //打开一个新的对话框，新开始一个游戏的对话框
    private void openNewGameDialog(){
        new AlertDialog.Builder(this).setTitle(R.string.new_game_title)
                .setItems(R.array.difficulty,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                startGame(i);
                            }
                        }).show();
    }
    private static final String TAG="Sudoku";

    //选择了游戏难度之后开始一个新游戏
    private void startGame(int i){
        Log.d(TAG,"click on "+i);
        Intent intent =  new Intent(MainActivity.this,Game.class);
        intent.putExtra(Game.KEY_DIFFICULTY,i);
        startActivity(intent);
    }

    @Override
    protected void onResume(){
        Log.d(TAG,"on Resume");
        super.onResume();
        Music.play(this,R.raw.main);
    }

    @Override
    protected void onPause(){
        super.onPause();
        Music.stop(this);
    }

    @Override
    protected void onStart(){
        super.onStart();
        Log.d(TAG,"on Start");
    }
}

