package tust.edu.cn.tustvrtest;

import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.util.Pair;
import android.widget.TextView;
import android.widget.Toast;

import com.google.vr.sdk.widgets.pano.VrPanoramaEventListener;
import com.google.vr.sdk.widgets.pano.VrPanoramaView;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public class Main extends AppCompatActivity {
    private static final String TAG = Main.class.getSimpleName();
    /*
    * Arbitrary variable to track load status.
    *  In this example, this variable should only be accessed on the UI thread.
    *  In a real app, this variable would be code that performs some UI actions
    *  when the panorama is fully loaded.
    * */
    public boolean loadImageSuccessful;
    /*
    * Tracks the file to be loaded across the lifetime of this app
    * */
    private Uri fileUri;
    //Configuration information for the panorama
    private VrPanoramaView.Options panoOptions = new VrPanoramaView.Options();
    private ImageLoaderTask backgroundImageLoaderTask;
    //Actual panorama widget
    private VrPanoramaView panoWidgetView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Make the source link clickable
        TextView sourceText = (TextView)findViewById(R.id.source);
        sourceText.setText(Html.fromHtml(getString(R.string.source)));
        sourceText.setMovementMethod(LinkMovementMethod.getInstance());

        panoWidgetView = (VrPanoramaView) findViewById(R.id.pano_view);
        panoWidgetView.setEventListener(new ActivityEventListener());

        //Initial launch of the app or an activity recreation due to rotation
        handleIntent(getIntent());

    }
    /*Load custom images based on the intent or load the default image
    See the Javadoc for this class for information on generating a custom intent via adb.
    * */
    private void handleIntent(Intent intent){
        if(Intent.ACTION_VIEW.equals(intent.getAction())){
            Log.i(TAG,"ACTION_VIEW Intent recieved");

            fileUri = intent.getData();
            if(fileUri == null){
                Log.w(TAG,"No data uri specified. Use \"-d /path/fileName\"");
            }else{
                Log.i(TAG,"Using file " + fileUri.toString());
            }
            panoOptions.inputType = intent.getIntExtra("inputType", VrPanoramaView.Options.TYPE_MONO);
            Log.i(TAG,"Options.inputType = " + panoOptions.inputType);
        }else{
            Log.i(TAG,"Intent is not ACTION_VIEW. Using default pano image.");
            fileUri = null;
            panoOptions.inputType = VrPanoramaView.Options.TYPE_MONO;
        }
        //Load the bitmap in a background thread to avoid blocking the UI thread.
        // This operation can take 100s of milliseconds
        if(backgroundImageLoaderTask  != null){
            //Cancel any task from a previous intent sent to this activity
            backgroundImageLoaderTask.cancel(true);
        }
        backgroundImageLoaderTask = new ImageLoaderTask();
        backgroundImageLoaderTask.execute(Pair.create(fileUri,panoOptions));

    }
    @Override
    protected void onPause() {
        panoWidgetView.pauseRendering();
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        panoWidgetView.resumeRendering();
    }

    @Override
    protected void onDestroy() {
        // Destroy the widget and free memory.
        panoWidgetView.shutdown();

        // The background task has a 5 second timeout so it can potentially stay alive for 5 seconds
        // after the activity is destroyed unless it is explicitly cancelled.
        if (backgroundImageLoaderTask != null) {
            backgroundImageLoaderTask.cancel(true);
        }
        super.onDestroy();
    }
    /*Listen to the important events from widget
    * */
    private class ActivityEventListener extends VrPanoramaEventListener{
        /*called by pano widget on the UI thread when it is
        done loading the image
        * */
        @Override
        public void onLoadSuccess(){
            loadImageSuccessful = true;
        }
        /*
        * Called by pano widget on the UI thread on any asynchronous error
        * */

        @Override
        public void onLoadError(String errorMessage){
            loadImageSuccessful = false;
            Toast.makeText(Main.this,"Error loading pano: "+errorMessage, Toast.LENGTH_LONG).show();
            Log.e(TAG,"Error loading pano: "+errorMessage);
        }

    }

    class ImageLoaderTask extends AsyncTask<Pair<Uri,VrPanoramaView.Options>,Void,Boolean>{
        VrPanoramaView.Options panoOptions = null;  // It's safe to use null VrPanoramaView.Options.
        InputStream istr = null;
        /**
         * Reads the bitmap from disk in the background and waits until it's loaded by pano widget.
         */
        @Override
        protected Boolean doInBackground(Pair<Uri, VrPanoramaView.Options>... fileInformation) {

            if (fileInformation == null || fileInformation.length < 1
                    || fileInformation[0] == null || fileInformation[0].first == null) {
                AssetManager assetManager = getAssets();
                try {
                    String[] names = assetManager.list("");
                    istr = assetManager.open("image/2.jpg");
                    panoOptions = new VrPanoramaView.Options();
                    panoOptions.inputType = VrPanoramaView.Options.TYPE_STEREO_OVER_UNDER;
                } catch (IOException e) {
                    Log.e(TAG, "Could not decode default bitmap: " + e);
                    return false;
                }
            } else {
                try {
                    istr = new FileInputStream(new File(fileInformation[0].first.getPath()));
                    panoOptions = fileInformation[0].second;
                } catch (IOException e) {
                    Log.e(TAG, "Could not load file: " + e);
                    return false;
                }
            }

            return true;
        }
        @Override
        protected void onPostExecute(Boolean aBoolean){
            if(istr!=null&&panoOptions!=null){
                panoWidgetView.loadImageFromBitmap(BitmapFactory.decodeStream(istr), panoOptions);
                try {
                    istr.close();
                } catch (IOException e) {
                    Log.e(TAG, "Could not close input stream: " + e);
                }
            }
            super.onPostExecute(aBoolean);
        }
    }
}
