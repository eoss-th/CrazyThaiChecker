package com.example.thaicheckerdroid;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.ToggleButton;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class MainActivity extends Activity {

    private ToggleButton optionButton;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.activity_main);        
        
        Button playButton = (Button) findViewById(R.id.playButton);
        playButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent i = new Intent(MainActivity.this, GameActivity.class);
				i.putExtra("numPlayers", 1);
                i.putExtra("sound", optionButton.isChecked());
				startActivity(i);
			}
		});

        Button play2Button = (Button) findViewById(R.id.play2Button);
        play2Button.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, GameActivity.class);
                i.putExtra("numPlayers", 2);
                i.putExtra("sound", optionButton.isChecked());
                startActivity(i);
            }
        });

        optionButton = (ToggleButton) findViewById(R.id.optionButton);
        optionButton.setChecked(loadOption());
    }

    boolean loadOption() {

        try {
            FileInputStream fi = openFileInput("option.ser");

            ObjectInputStream oi = new ObjectInputStream(fi);
            Boolean sound = (Boolean) oi.readObject();
            return sound;

        } catch (Exception e) {

        }
        return true;
    }

    void saveOption() {

        try {

            FileOutputStream fout = openFileOutput("option.ser", Activity.MODE_PRIVATE);

            ObjectOutputStream os = new ObjectOutputStream(fout);
            os.writeObject(optionButton.isChecked());

            fout.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    protected void onDestroy() {
        saveOption();
        super.onDestroy();
    }
}
