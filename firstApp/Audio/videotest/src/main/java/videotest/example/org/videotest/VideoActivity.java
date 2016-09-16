package videotest.example.org.videotest;

import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.VideoView;

public class VideoActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //Fill view from resource
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video);
        VideoView video = (VideoView)findViewById(R.id.video);
        //load and start the movie
        video.setVideoPath("/data/samplevideo.3gp");
        video.start();

    }
}
