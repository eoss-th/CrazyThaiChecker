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

public class MainActivity extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.activity_main);        
        
        Button playButton = (Button) findViewById(R.id.playButton);
        playButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				//textView.setText("Best game in the world");
				Intent i = new Intent(MainActivity.this, GameActivity.class);
				
				startActivity(i);
			}
		});
        
        Button optionButton = (Button) findViewById(R.id.optionButton);
        optionButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
			}
		});

        
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }
}
