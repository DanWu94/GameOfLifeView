package io.kimo.examples.gameoflifeview;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.Button;

import io.kimo.gameoflifeview.view.GameOfLifeView;

public class ThroughXMLActivity extends ActionBarActivity {

    private GameOfLifeView gameOfLifeView;
    private boolean ifPaused;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.with_xml);
        Utils.configureToolbar(this, true);
        setTitle("Free Mode");

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

    public void pauseLife(View view) {
        Button pauseButton = (Button) findViewById(R.id.pausebtn);
        if(ifPaused){
            gameOfLifeView.start();
            pauseButton.setText("Pause");

        }else{
            gameOfLifeView.stop();
            pauseButton.setText("Resume");
        }
        ifPaused = !ifPaused;
    }
}
