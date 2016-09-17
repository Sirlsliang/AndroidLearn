package tust.edu.cn.browserintent;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

/**
 * Created by MyPC on 2016/9/17.
 */
public class BrowserIn extends Activity{
    private Button goButton = null;
    private EditText urlText = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.browserintent);
        //Get a handle to all user interface elements
        urlText = (EditText)findViewById(R.id.url_field);
        goButton = (Button)findViewById(R.id.go_button);
        goButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openBrowser();
            }
        });

        urlText.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int keyCode, KeyEvent keyEvent) {
                if(keyCode == KeyEvent.KEYCODE_ENTER){
                    openBrowser();
                    return true;
                }
                return false;
            }
        });
    }
    private void openBrowser(){
        Uri uri = Uri.parse(urlText.getText().toString());
        Intent intent = new Intent(Intent.ACTION_VIEW,uri);


        startActivity(intent);
    }
}
