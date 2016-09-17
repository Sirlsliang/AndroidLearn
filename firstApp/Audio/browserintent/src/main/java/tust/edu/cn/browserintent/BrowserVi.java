package tust.edu.cn.browserintent;

import android.app.Activity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;

/**
 * Created by MyPC on 2016/9/17.
 */
public class BrowserVi extends Activity {
    private Button goBut = null;
    private EditText urlField = null;
    private WebView webView = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.browserview);
        goBut = (Button)findViewById(R.id.vi_go_button);
        urlField  = (EditText)findViewById(R.id.vi_url_field);
        webView = (WebView)findViewById(R.id.web_view);
        goBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openBrowserView();
            }
        });
        urlField.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int keyCode, KeyEvent keyEvent) {
                if(keyCode == KeyEvent.KEYCODE_ENTER){
                    openBrowserView();
                    return true;
                }
                return false;
            }
        });
    }
    private void openBrowserView(){
        webView.getSettings().setJavaScriptEnabled(true);
        webView.loadUrl(urlField.getText().toString());
    }
}
