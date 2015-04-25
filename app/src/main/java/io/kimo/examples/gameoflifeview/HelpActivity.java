package io.kimo.examples.gameoflifeview;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;

public class HelpActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_help);
        Utils.configureToolbar(this, true);
        setTitle("Help");
    }

}
