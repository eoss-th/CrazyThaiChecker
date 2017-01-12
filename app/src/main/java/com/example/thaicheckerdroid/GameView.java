package com.example.thaicheckerdroid;

import game.checker.thai.ThaiCheckerBoard;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

public class GameView extends SurfaceView implements SurfaceHolder.Callback {

	private SpriteBoard spriteBoard;
	private SpriteCircle draggingSprite;
	private GameLogic mGameLogic;
	
	private ThaiCheckerBoard checkerBoard;

    private boolean sound;
	
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
	
	public GameView(Activity context, int numPlayers, boolean sound) {
		super(context);

		this.numPlayers = numPlayers;
        this.sound = sound;
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

					int x, y;
					x = (int) event.getX();
					y = (int) event.getY();
					
					SpriteCircle spriteCircle = spriteBoard.drag(x, y);
					if (spriteCircle!=null) {
						draggingSprite = new SpriteCircle(spriteCircle);
						draggingSprite.center(x, y);
						return true;
					}
				}
				
				if (event.getAction()==MotionEvent.ACTION_MOVE){
					if (draggingSprite != null) {
						draggingSprite.center((int)event.getX(), (int)event.getY());
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
		newGame(this.numPlayers, this.sound);
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

	public void newGame() {
		mGameLogic.setGameState(GameLogic.PAUSE);
		newGame(numPlayers, sound);
		mGameLogic.setGameState(GameLogic.RUNNING);
		mGameLogic.start();
	}
	
	private void newGame(int numPlayers, boolean sound) {
		DisplayMetrics m = new DisplayMetrics();
		context.getWindowManager().getDefaultDisplay().getMetrics(m);
		
		System.out.println(m.widthPixels+"," +m.heightPixels);
		int x = 0;
		int y = m.widthPixels/4;

		checkerBoard = new ThaiCheckerBoard(/*new byte[]
					{  XX, XX, XX, XX,
					  XX, BS, BS, BS,
					   BS, XX, BS, BS,
					  BS, BS, XX, XX,
					   XX, WS, WS, XX,
					  XX, WS, WS, WS,
					   WS, WS, XX, WS,
					  XX, XX, XX, XX}*/);
		
		spriteBoard = new SpriteBoard(this.context, checkerBoard, numPlayers, x, y, m.widthPixels, sound);
				
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
					case ThaiCheckerBoard.GAME_OVER_BLACK_WIN: msg = "คุณแพ้!"; break;
					case ThaiCheckerBoard.GAME_OVER_WHITE_WIN: msg = "คุณชนะ!"; break;
					case ThaiCheckerBoard.GAME_OVER_DRAW: msg = "เสมอ!"; break;
					}
					
					AlertDialog.Builder builder = new AlertDialog.Builder(context);
					builder.setMessage(msg + "\nเล่นอีกตามั้ย?")
					       .setCancelable(false)
					       .setPositiveButton("เริ่มใหม่", new DialogInterface.OnClickListener() {
					           public void onClick(DialogInterface dialog, int id) {
					        	   
					        	   newGame(numPlayers, sound);
					        	   mGameLogic.setGameState(GameLogic.RUNNING);
					        	   mGameLogic.start();
					        	   
					           }
					       })
					       .setNegativeButton("ออก", new DialogInterface.OnClickListener() {
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