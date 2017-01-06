package com.example.thaicheckerdroid;

import game.checker.thai.ThaiCheckerBoard;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

public class GameView extends SurfaceView implements SurfaceHolder.Callback {

	private SpriteBoard spriteBoard;
	private SpriteObject draggingSprite;
	private GameLogic mGameLogic;
	
	private ThaiCheckerBoard checkerBoard;
	
	public static final int XX = 0;
	public static final int BS = 1;
	public static final int BK = 2;
	public static final int WS = 3;
	public static final int WK = 4;
	
	private Handler handler;
	
	private Activity context;
	
	private int bgColor;
	
    //private SpriteButton resetButton;

	private int numPlayers;
	
	public GameView(Activity context, int numPlayers) {
		super(context);

		this.numPlayers = numPlayers;
		setFocusable(true);	
		
		getHolder().addCallback(this);
		
		setOnTouchListener(new OnTouchListener(){
			
			public boolean onTouch(View v, MotionEvent event){
				
				if (event.getAction()==MotionEvent.ACTION_DOWN) {

                    /*
					if (resetButton.contains((int)event.getX(), (int)event.getY())) {
						resetButton.onPressed();
						return true;
					}*/
					
					Bitmap img = spriteBoard.drag((int)event.getX(), (int)event.getY());
					if (img!=null) {
						draggingSprite = new SpriteObject(img, (int)event.getX(), (int)event.getY());
						return true;
					}
				}
				
				if (event.getAction()==MotionEvent.ACTION_MOVE){
					if (draggingSprite != null) {
						draggingSprite.setX((int)event.getX());
						draggingSprite.setY((int)event.getY());
						return true;
					}
				}
				
				if (event.getAction()==MotionEvent.ACTION_UP) {

                    /*
					if (resetButton.isPressing()) {
						resetButton.onReleased();
						return true;
					}*/
					
					//Drop
					if (draggingSprite != null) {
						spriteBoard.drop((int)event.getX(), (int)event.getY());
						draggingSprite = null;
						return true;
					}
				}
				return false;
		}
			
		});
		
		handler = new Handler();
		this.context = context;
		newGame(this.numPlayers);
		bgColor = context.getResources().getColor(android.R.color.holo_blue_dark);

        /*
		resetButton = new SpriteButton(
				BitmapFactory.decodeResource(getResources(),R.drawable.reset),
				BitmapFactory.decodeResource(getResources(),R.drawable.reset_blur),
				BitmapFactory.decodeResource(getResources(),R.drawable.reset).getWidth()/2, 
				BitmapFactory.decodeResource(getResources(),R.drawable.reset).getHeight()/2);
		
		resetButton.setOnClickListener(new ButtonClickListener() {

			@Override
			public void onClicked(SpriteButton b) {
	        	   mGameLogic.setGameState(GameLogic.PAUSE);
	        	   newGame(GameView.this.numPlayers);
	        	   mGameLogic.setGameState(GameLogic.RUNNING);
	        	   mGameLogic.start();
			}
			
		});
		*/
	}
	
	private void newGame(int numPlayers) {
		DisplayMetrics m = new DisplayMetrics();
		context.getWindowManager().getDefaultDisplay().getMetrics(m);
		
		System.out.println(m.widthPixels+"," +m.heightPixels);
		int x = m.widthPixels/2 - BitmapFactory.decodeResource(getResources(),R.drawable.board).getWidth()/2;
		int y = m.heightPixels/2 - BitmapFactory.decodeResource(getResources(),R.drawable.board).getHeight()/2;
		y = m.widthPixels/4;
		checkerBoard = new ThaiCheckerBoard(/*new byte[] 
					{  XX, XX, XX, XX,
					  XX, BS, BS, BS,
					   BS, XX, BS, BS,
					  BS, BS, XX, XX,
					   XX, WS, WS, XX,
					  XX, WS, WS, WS,
					   WS, WS, XX, WS,
					  XX, XX, XX, XX}*/);
		
		spriteBoard = new SpriteBoard(this.context, checkerBoard, numPlayers,
				BitmapFactory.decodeResource(getResources(),R.drawable.board),
				BitmapFactory.decodeResource(getResources(),R.drawable.red),
				BitmapFactory.decodeResource(getResources(),R.drawable.red_king),
				BitmapFactory.decodeResource(getResources(),R.drawable.orange),
				BitmapFactory.decodeResource(getResources(),R.drawable.orange_king),
				x, y
				);
				
		mGameLogic = new GameLogic(getHolder(), this);
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		mGameLogic.setGameState(GameLogic.RUNNING);
		mGameLogic.start();
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		mGameLogic.setGameState(GameLogic.PAUSE);
	}

	public void onDraw(Canvas canvas) {
		
		canvas.drawColor(bgColor); //bg
		spriteBoard.draw(canvas);
		
		//resetButton.draw(canvas);
		
		if (draggingSprite!=null)
			draggingSprite.draw(canvas);
	}

	public void update() {
		final int gameState = spriteBoard.update();
		if (gameState != ThaiCheckerBoard.GAME_PLAYING) {
			mGameLogic.setGameState(GameLogic.PAUSE);
			handler.post(new Runnable() {

				@Override
				public void run() {
										
					String msg="";
					switch(gameState) {
					case ThaiCheckerBoard.GAME_OVER_BLACK_WIN: msg = "You Lose!"; break;
					case ThaiCheckerBoard.GAME_OVER_WHITE_WIN: msg = "You Win!"; break;
					case ThaiCheckerBoard.GAME_OVER_DRAW: msg = "Draw!"; break;
					}
					
					AlertDialog.Builder builder = new AlertDialog.Builder(context);
					builder.setMessage(msg + "\nDo you want to continue?")
					       .setCancelable(false)
					       .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
					           public void onClick(DialogInterface dialog, int id) {
					        	   
					        	   newGame(numPlayers);
					        	   mGameLogic.setGameState(GameLogic.RUNNING);
					        	   mGameLogic.start();
					        	   
					           }
					       })
					       .setNegativeButton("No", new DialogInterface.OnClickListener() {
					           public void onClick(DialogInterface dialog, int id) {
					                dialog.cancel();
					        	    mGameLogic.setGameState(GameLogic.PAUSE);
					                context.finish();
					           }
					       });
					AlertDialog alert = builder.create();
					alert.show();
				}
				
			});
		}
	}
	
	public void release() {
 	   mGameLogic.setGameState(GameLogic.PAUSE);		
	}
}