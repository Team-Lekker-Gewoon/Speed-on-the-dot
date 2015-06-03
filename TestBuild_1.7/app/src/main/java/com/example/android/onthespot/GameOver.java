package com.example.android.onthespot;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import com.example.android.onthespot.R;

public class GameOver extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_over);

        MenuButton();
        ScoreButton();
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    private void MenuButton() {
        Button button = (Button) findViewById(R.id.GotoMenu);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(GameOver.this, "Main Menu is not available.", Toast.LENGTH_LONG).show();
                //startActivity(new Intent(GameOver.this, MainActivity.class));
                //change to menu.class once integrated
            }
        });
    }

    private void ScoreButton() {
        Button button = (Button) findViewById(R.id.Leaderboards);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(GameOver.this, "Leaderboards are not available.", Toast.LENGTH_LONG).show();
                //startActivity(new Intent(GameOver.this, MainActivity.class));
                //change to leaderboards.class once integrated
            }
        });
    }

    @Override
    public boolean onTouchEvent(MotionEvent e){
        if (e.getAction() == MotionEvent.ACTION_DOWN) {
            //startActivity(new Intent(GameOver.this, MainActivity.class));
            finish();
        }
        return true;
    }

/*
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_game_over, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    } */
}
