package cn.edu.tust.myvrschool;

import android.content.Context;
import android.media.MediaPlayer;

/**
 * Created by MyPC on 2016/9/15.
 */
public class Music {
    private static MediaPlayer mp = null;

    //stop old song and start new one, check before play
    public static void play(Context context, int resource){

        stop(context);
        //只有正确设置了才会播放音乐
        if(Prefs.getMusic(context)){
            mp = MediaPlayer.create(context,resource);
            mp.setLooping(true);
            mp.start();
        }
    }

    public static void stop(Context context){
        if(mp!=null){
            mp.stop();
            mp.release();
            mp = null;
        }
    }
}
