package cn.edu.tust.myvrschool;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

public class MainActivity extends AppCompatActivity implements OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //set up listeners for all the buttons
        View continueButton = findViewById(R.id.about_button);
        continueButton.setOnClickListener(this);
        View newButton  = findViewById(R.id.new_game_button);
        newButton.setOnClickListener(this);


    }
    @Override
    public void onClick(View v){
        switch (v.getId()){
            case R.id.about_button:
                Intent i = new Intent(this,About.class);
                startActivity(i);
                break;
        }
    }
}
