package com.example.dmitriy.tapkiller;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

public class StartActivity extends AppCompatActivity implements View.OnClickListener {

    MenuBackgroundSurface backgroundSurface;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        if (getSupportActionBar() != null){
            getSupportActionBar().hide();
        }
        LinearLayout backgroundView = (LinearLayout) findViewById(R.id.backgroundView);
        backgroundSurface = new MenuBackgroundSurface(this);
        backgroundView.addView(backgroundSurface);
        Button buttonStart = (Button) findViewById(R.id.start_button);
        Button buttonOptions = (Button) findViewById(R.id.options_button);
        Button buttonExit = (Button) findViewById(R.id.exit_button);
        buttonStart.setOnClickListener(this);
        buttonOptions.setOnClickListener(this);
        buttonExit.setOnClickListener(this);
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.start_button:
                Intent intent = new Intent();
                intent.setClass(this, MainActivity.class);
                startActivity(intent);
                break;
            case R.id.options_button:

                break;
            case R.id.exit_button:
                finish();
                break;
        }
    }


}