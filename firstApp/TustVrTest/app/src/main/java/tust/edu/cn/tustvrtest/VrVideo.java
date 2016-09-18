package tust.edu.cn.tustvrtest;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.util.Pair;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.vr.sdk.widgets.pano.VrPanoramaView;
import com.google.vr.sdk.widgets.video.VrVideoEventListener;
import com.google.vr.sdk.widgets.video.VrVideoView;

import java.io.IOException;

/**
 * Created by MyPC on 2016/9/17.
 */
public class VrVideo extends Activity {
    private static final  String TAG = VrVideo.class.getSimpleName();
    /*查看视频加载状态，
    * */
    public static final int LOAD_VIDEO_STATUS_UNKNOWN = 0;
    public static final int LOAD_VIDEO_STATUS_SUCCESS = 1;
    public static final int LOAD_VIDEO_STATUS_ERROR   = 2;
    private int loadVideoStatus = LOAD_VIDEO_STATUS_UNKNOWN;
    //当屏幕旋转时保留视频的状态
    private static final String STATE_IS_PAUSED = "isPaused";
    private static final String STATE_PROGRESS_TIME = "progressTime";
    //视频的长度不需要保留，在本次试验中保留是为了可以让seekBar在onRestoreInstanceState中就可以配置。
    //而不必等着视频加载完毕后再配置
    private static final String STATE_VIDEO_DURATION = "videoDuration";
    /*
    * video的控制按钮
    * */
    private SeekBar seekBar;
    private TextView statusText;
    /*video的视图框与其UI元素
    * */
    private VrVideoView videoWidgetView;

    //默认情况下视频加载完毕后即可播放，
    private boolean isPaused = false;

    //Tracks the file to be loaded across the lifetime of this app
    private Uri fileUri;
    //视频的配置信息
    private VrVideoView.Options videoOptions = new VrVideoView.Options();
    private VideoLoaderTask backgroundVideoLoaderTask;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.video);

        seekBar = (SeekBar) findViewById(R.id.video_seek_bar);
        seekBar.setOnSeekBarChangeListener(new SeekBarListener());
        statusText = (TextView) findViewById(R.id.video_status_text);
        //使连接生效
        TextView sourceText = (TextView) findViewById(R.id.video_source);
        sourceText.setText(Html.fromHtml(getString(R.string.video_source)));
        sourceText.setMovementMethod(LinkMovementMethod.getInstance());
        //Bind input and output objects for the view
        videoWidgetView = (VrVideoView)findViewById(R.id.video_view);
        videoWidgetView.setEventListener(new ActivityEventListener());

        loadVideoStatus = LOAD_VIDEO_STATUS_UNKNOWN;
        /*
        *处理初始化app的登录或因旋转造成的recreation
         */
        handleIntent(getIntent());


    }
    //当Activity已经处于Running状态并赋予一个新的intent时使用
    @Override
    protected  void onNewIntent(Intent intent){
        Log.i(TAG,this.hashCode() + ".onNewIntent()");
        /*保存Intent，这允许在onCreate()方法中调用的getIntent()获取该新的Intent，并在以后的使用
        * */
        setIntent(intent);
        //load the new image
        handleIntent(intent);
    }
    /*
    *
    * */
    private void handleIntent(Intent intent){
        //判断Intent是否需要加载文件
        if(Intent.ACTION_VIEW.equals(intent.getAction())){
            Log.i(TAG,"ACTION_VIEW Intent received");
            fileUri = intent.getData();
            if(fileUri == null){
                Log.w(TAG,"No data uri specified. Use \" -d path/filename\" .");
            }else{
                Log.i(TAG,"Using file " + fileUri.toString());
            }
            videoOptions.inputFormat = intent.getIntExtra("inputFormat", VrVideoView.Options.FORMAT_DEFAULT);
            videoOptions.inputType = intent.getIntExtra("inputType", VrVideoView.Options.TYPE_MONO);
        }else{
            Log.i(TAG,"Intent is not ACTION_VIEW. Using the default video");
            fileUri = null;
        }
        //利用后台线程加载位图，以避免阻塞线程。
        if(backgroundVideoLoaderTask !=null){
            backgroundVideoLoaderTask.cancel(true);
        }
        backgroundVideoLoaderTask = new VideoLoaderTask();
        backgroundVideoLoaderTask.execute(Pair.create(fileUri,videoOptions));
    }

    @Override
    protected void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putLong(STATE_PROGRESS_TIME,videoWidgetView.getCurrentPosition());
        savedInstanceState.putLong(STATE_VIDEO_DURATION,videoWidgetView.getDuration());
        savedInstanceState.putBoolean(STATE_IS_PAUSED,isPaused);
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        long progressTime = savedInstanceState.getLong(STATE_PROGRESS_TIME);
        videoWidgetView.seekTo(progressTime);
        seekBar.setMax((int)savedInstanceState.getLong(STATE_VIDEO_DURATION));
        seekBar.setProgress((int)progressTime);
        isPaused = savedInstanceState.getBoolean(STATE_IS_PAUSED);
        if(isPaused)
            videoWidgetView.pauseVideo();
    }

    @Override
    protected void onPause() {
        super.onPause();
        //阻止view继续在后台渲染
        videoWidgetView.pauseRendering();;
        //如果视频正在播放时调用onPause()，默认将会暂停直到调用onResume方法
        isPaused = true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        //恢复3D渲染
        videoWidgetView.resumeRendering();

        updateStatusText();
    }

    @Override
    protected void onDestroy() {
        videoWidgetView.shutdown();
        super.onDestroy();

    }

    private void updateStatusText(){
        StringBuilder status = new StringBuilder();
        status.append(isPaused?"Paused: ":"Playing: ");
        status.append(String.format("%.2f",videoWidgetView.getCurrentPosition()/1000f));
        status.append("/");
        status.append(videoWidgetView.getDuration()/1000f);
        status.append("seconds");
        statusText.setText(status.toString());

    }
    private void togglePause(){
        if(isPaused){
            videoWidgetView.playVideo();
        }else{
            videoWidgetView.pauseVideo();
        }
        isPaused = !isPaused;
        updateStatusText();
    }
    //用户控制seekbar时，修改视频的位置
    private class SeekBarListener implements SeekBar.OnSeekBarChangeListener {

        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            if(fromUser){
                videoWidgetView.seekTo(progress);
                updateStatusText();
            }
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {

        }
    }
    //监听来自视频播放框的事件
    private class ActivityEventListener extends VrVideoEventListener{
        //视频加载完毕后，调用该方法
        @Override
        public void onLoadSuccess() {
            Log.i(TAG,"Successfully loaded video" + videoWidgetView.getDuration());
            super.onLoadSuccess();
        }
        //视频加载出错时调用
        @Override
        public void onLoadError(String errorMessage) {
            //基本上是因为无法解码视频格式的错误
            loadVideoStatus = LOAD_VIDEO_STATUS_ERROR;
            Toast.makeText(VrVideo.this,"Error loading video"+errorMessage,Toast.LENGTH_LONG).show();
            Log.e(TAG,"Error loading video: "+errorMessage);
        }

        @Override
        public void onClick() {
            //super.onClick();
            togglePause();
        }
        //每帧调用该方法更新UI
        @Override
        public void onNewFrame(){
            updateStatusText();
            seekBar.setProgress((int)videoWidgetView.getCurrentPosition());
        }
        //使视频循环播放。也可以利用该方法切换视频
        @Override
        public void onCompletion() {
            videoWidgetView.seekTo(0);
            //super.onCompletion();


        }
    }

    //Helper class to manage threading
    class VideoLoaderTask extends AsyncTask<Pair<Uri,VrVideoView.Options>,Void,Boolean>{
        private VrVideoView.Options options;
        private boolean bloadFromAssets = false;
        private Pair<Uri,VrVideoView.Options> infor;

        @Override
        protected Boolean doInBackground(Pair<Uri, VrVideoView.Options>... fileInformation) {

            if(fileInformation == null || fileInformation.length<1
                    ||fileInformation[0] == null || fileInformation[0].first == null){
                options = new VrVideoView.Options();
                options.inputType = VrVideoView.Options.TYPE_STEREO_OVER_UNDER;
                bloadFromAssets = true;
            }else{
                bloadFromAssets = false;
               infor = fileInformation[0];
            }

            return true;
        }


        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            try{
                if(bloadFromAssets) {
                    videoWidgetView.loadVideoFromAsset("video/congo.mp4", options);
                }else{
                    videoWidgetView.loadVideo(infor.first,infor.second);
                }
            }catch (IOException e){
                loadVideoStatus = LOAD_VIDEO_STATUS_ERROR;
                videoWidgetView.post(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(VrVideo.this,"Error opening file.",Toast.LENGTH_LONG).show();
                    }
                });
                Log.e(TAG,"Could not open video"+e);
            }

        }
    }

}
