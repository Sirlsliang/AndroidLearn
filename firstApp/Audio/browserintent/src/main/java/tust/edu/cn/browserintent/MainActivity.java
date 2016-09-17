package tust.edu.cn.browserintent;

import android.content.Intent;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //点击BrowserIntent
        Button browserIn = (Button) findViewById(R.id.browser_intent);
        browserIn.setOnClickListener(this);
        Button browserVi = (Button) findViewById(R.id.browser_view);
        browserVi.setOnClickListener(this);
        Button loBrowser = (Button) findViewById(R.id.localbrowser);
        loBrowser.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.browser_intent:
                startActivity(new Intent(this,BrowserIn.class));
                break;
            case R.id.browser_view:
                startActivity(new Intent(this,BrowserVi.class));
                break;
            case R.id.localbrowser:
                startActivity(new Intent(this,LocalBrowser.class));
                break;
        }
    }
}
