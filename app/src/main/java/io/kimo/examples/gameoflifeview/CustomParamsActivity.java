package io.kimo.examples.gameoflifeview;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;

import io.kimo.gameoflifeview.view.GameOfLifeView;

public class CustomParamsActivity extends ActionBarActivity {

    private GameOfLifeView gameOfLifeView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.with_custom_params);
        Utils.configureToolbar(this, true);
        setTitle("Clear Mode");

        gameOfLifeView = (GameOfLifeView) findViewById(R.id.game_of_life);
        gameOfLifeView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
    }

    @Override
    protected void onResume() {
        super.onResume();
        gameOfLifeView.start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        gameOfLifeView.stop();
    }
}
