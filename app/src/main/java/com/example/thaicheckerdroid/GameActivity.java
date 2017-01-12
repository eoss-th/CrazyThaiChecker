package com.example.thaicheckerdroid;

import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.view.Menu;

public class GameActivity extends Activity {
	
	private GameView gameView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

		int numPlayers = getIntent().getIntExtra("numPlayers", 1);
        boolean sound = getIntent().getBooleanExtra("sound", false);
        System.out.println(sound);
        gameView = new GameView(this, numPlayers, sound);
        setContentView(gameView);
    }
    
    public void finishWithoutConfirm2() {
    	super.finish();
    }

	@Override
	public void finish() {
		
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage("ไม่เล่นแล้วเหรอ?")
		       .setCancelable(true)
		       .setPositiveButton("ออก", new DialogInterface.OnClickListener() {
		           public void onClick(DialogInterface dialog, int id) {
		        	   
		        	   gameView.release();
		        	   GameActivity.super.finish();
		        	   
		           }
		       })
		       .setNegativeButton("เริ่มใหม่", new DialogInterface.OnClickListener() {
		           public void onClick(DialogInterface dialog, int id) {

					   gameView.newGame();
					   dialog.cancel();
		           }
		       });
		AlertDialog alert = builder.create();
		alert.show();
		
	}

}
