package tust.edu.cn.browserintent;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by MyPC on 2016/9/17.
 */
public class LocalBrowser extends Activity {
    private static final String TAG =LocalBrowser.class.getSimpleName();
    private final Handler handler = new Handler();


    private WebView webView = null;
    private TextView textView = null;
    private Button viewBut  = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.jswebview);
        //find the Android controls on the screen
        webView = (WebView)findViewById(R.id.js_web_view);
        textView = (TextView) findViewById(R.id.js_text_view);
        viewBut = (Button) findViewById(R.id.js_web_view_but);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.addJavascriptInterface(new AndroidBridge(),"android");
        webView.setWebChromeClient(new WebChromeClient(){
            @Override
            public boolean onJsAlert(WebView view, String url, String message, JsResult result) {
                Log.d(TAG,"onJsAlert("+view+", "+url+", "+message+", "+result+")");
                Toast.makeText(LocalBrowser.this,message,Toast.LENGTH_LONG).show();
                result.confirm();
                return true;
                //return super.onJsAlert(view, url, message, result);
            }
        });
        webView.loadUrl("file:///android_asset/index.html");
        viewBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG,"onClick("+view+")");
                webView.loadUrl("javascript:callJS('Hello from Android')");
            }
        });
    }
    //Object exposed to JavaScript
    private class AndroidBridge{
        //当javaScript调用该方法时，就创建新的Runnable对象，并将其放到主线程的运行队列上
        @JavascriptInterface
        public void callAndroid(final String arg){
            handler.post(new Runnable() {
                @Override
                public void run() {
                    Log.d(TAG,"callAndroid("+arg+")");
                    textView.setText(arg);
                }
            });
        }
    }
}
