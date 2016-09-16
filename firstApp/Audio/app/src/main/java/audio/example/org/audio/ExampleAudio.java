package audio.example.org.audio;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;

public class ExampleAudio extends AppCompatActivity {
    private MediaPlayer mp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_example_audio);
        //下面的方法告诉Android，在该应用程序运行时如果用户按下了音量增加或音量降低键，应该调整音乐或其他媒体的音量，而不是铃声的音量
        setVolumeControlStream(AudioManager.STREAM_MUSIC);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event){
        int resId;
        switch (keyCode){
            case KeyEvent.KEYCODE_DPAD_UP:
                resId = R.raw.up;
                break;
            case KeyEvent.KEYCODE_DPAD_DOWN:
                resId = R.raw.down;
                break;
            case KeyEvent.KEYCODE_DPAD_LEFT:
                resId = R.raw.left;
                break;
            case KeyEvent.KEYCODE_DPAD_RIGHT:
                resId = R.raw.right;
                break;
            case KeyEvent.KEYCODE_DPAD_CENTER:
            case KeyEvent.KEYCODE_ENTER:
                resId = R.raw.enter;
                break;
            case KeyEvent.KEYCODE_A:
                resId = R.raw.a;
                break;
            case KeyEvent.KEYCODE_S:
                resId = R.raw.s;
                break;
            case KeyEvent.KEYCODE_D:
                resId = R.raw.d;
                break;
            case KeyEvent.KEYCODE_F:
                resId = R.raw.f;
                break;
            default:
                return super.onKeyDown(keyCode,event);
        }
        if(mp!=null){
            mp.release();
        }
        mp = MediaPlayer.create(this,resId);
        mp.start();
        return true;
    }
}
