package com.example.thaicheckerdroid;

import java.util.List;

import game.checker.thai.ThaiCheckerBoard;
import game.checker.thai.ThaiCheckerThinker;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.media.AudioManager;
import android.media.SoundPool;

public class SpriteBoard {
	
	private ThaiCheckerBoard board;
	private Bitmap bitmap;
	private int x;
	private int y;
	private Bitmap blackSoldier;
	private Bitmap blackKing;
	private Bitmap whiteSoldier;
	private Bitmap whiteKing;
	private int pieceSize;
	private int boardSize;
	private int draggingIndex;
	private ThaiCheckerBoard.Moving lastMoving;
	private List<ThaiCheckerBoard.Moving> availableMovings;
	
	private int swordSound;
	private int stepSound;
	private SoundPool soundPool;
	
	private int volume;
	
	private static final int [] TABLE = {
		-1, 0, -1, 1, -1, 2, -1, 3, 
		4, -1, 5, -1, 6, -1, 7, -1,
		-1, 8, -1, 9, -1, 10, -1, 11, 
		12, -1, 13, -1, 14, -1, 15, -1,
		-1, 16, -1, 17, -1, 18, -1, 19, 
		20, -1, 21, -1, 22, -1, 23, -1,
		-1, 24, -1, 25, -1, 26, -1, 27, 
		28, -1, 29, -1, 30, -1, 31, -1,		
	};
	
	public SpriteBoard(Context context, ThaiCheckerBoard board, 
			Bitmap bitmap, Bitmap blackSoldier, Bitmap blackKing, Bitmap whiteSoldier, Bitmap whiteKing, int x, int y) {
		this.board = board;
		this.bitmap = bitmap;
		this.x = x;
		this.y = y;
		
		this.blackSoldier = blackSoldier;
		this.blackKing = blackKing;
		this.whiteSoldier = whiteSoldier;
		this.whiteKing = whiteKing;
		
		boardSize = this.bitmap.getWidth();
		pieceSize = boardSize / 8;
		draggingIndex = -1;
		
		availableMovings = ThaiCheckerThinker.think(board, ThaiCheckerBoard.WHITE_PLAYER);
		
		soundPool = new SoundPool(10, AudioManager.STREAM_MUSIC, 0);
		swordSound = soundPool.load(context, R.raw.steelsword, 1);
		stepSound = soundPool.load(context, R.raw.step, 1);
		
		 AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);		
		 volume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	public Bitmap getBitmap() {
		return bitmap;
	}

	public void setX(int x) {
		this.x = x;
	}

	public void setY(int y) {
		this.y = y;
	}

	public void setBitmap(Bitmap bitmap) {
		this.bitmap = bitmap;
	}

	public void draw(Canvas canvas) {
		
		synchronized (this) {
			
			canvas.drawBitmap(bitmap, x, y, null);
			int postX, postY;
			
			int which;
			Bitmap img;
			for (int i=0; i < 32; i++) {

				if (i==draggingIndex) continue;
				
				postX = pieceSize * 2 * (i%4) + ((i/4)%2== 0?pieceSize:0);
				postY =  pieceSize * (i/4);
						
				img = null;
				which = board.get(i);
				switch (which) {
				case ThaiCheckerBoard.BS: img = blackSoldier; break;
				case ThaiCheckerBoard.BK: img = blackKing; break;
				case ThaiCheckerBoard.WS: img = whiteSoldier; break;
				case ThaiCheckerBoard.WK: img = whiteKing; break;
				}
				if (img!=null)
					canvas.drawBitmap(img, postX + x , postY + y, null);				
			}
			
		}
		
	}
	
	private int getBoardPosition(int x, int y) {
		x -= this.x;
		y -= this.y;
		return TABLE[ x / pieceSize + 8 * ( y / pieceSize)];
	}
	
	public void drop (int dropX, int dropY) {
		
		if (contains(dropX, dropY)) {
			
			int pos = getBoardPosition(dropX, dropY);
					
			for (ThaiCheckerBoard.Moving m:availableMovings) {
				if (m.getFrom()==draggingIndex && m.getTo()==pos) {
					
					synchronized (this) {
						lastMoving = m;
						board = lastMoving.next();
						if (m instanceof ThaiCheckerBoard.Killing) {
							soundPool.play(swordSound, volume, volume, 1, 0, 1);
						} else {
							soundPool.play(stepSound, volume, volume, 1, 0, 1);							
						}
						break;
					}
				}
				
			}
			
		}
		
		draggingIndex = -1;
	}
	
	public Bitmap drag(int touchX, int touchY) {
		
		//Opponent''s turn
		if (lastMoving!=null) return null;
		
		if (contains(touchX, touchY)==false) return null;
		
		int pos = getBoardPosition(touchX, touchY);
		
		if (pos==-1) return null;
		
		int which = board.get(pos);
		if (which == ThaiCheckerBoard.WS || which == ThaiCheckerBoard.WK) {
			draggingIndex = pos;		
			return whiteSoldier;
		}
		
		return null;
	}

	public int update() {
		
			if (lastMoving!=null) {
				synchronized (this) {
					
					try {
						
						int state;
						state = board.state();
						if (state!=ThaiCheckerBoard.GAME_PLAYING) return state;
						
						availableMovings = ThaiCheckerThinker.think(board, ThaiCheckerBoard.BLACK_PLAYER);
						
						if (availableMovings.isEmpty()) {
							if (board.score()>0) return ThaiCheckerBoard.GAME_OVER_BLACK_WIN;
							return ThaiCheckerBoard.GAME_OVER_WHITE_WIN;
						}						
					
						ThaiCheckerBoard.Moving comMoved = ThaiCheckerThinker.minimax(lastMoving);
						if (comMoved instanceof ThaiCheckerBoard.Killing) {
							soundPool.play(swordSound, volume, volume, 1, 0, 1);
						} else {
							soundPool.play(stepSound, volume, volume, 1, 0, 1);							
						}
						board = comMoved.next();						
					
						state = board.state();
						if (state!=ThaiCheckerBoard.GAME_PLAYING) return state;
						
						availableMovings = ThaiCheckerThinker.think(board, ThaiCheckerBoard.WHITE_PLAYER);
						
						if (availableMovings.isEmpty()) {
							if (board.score()>0) return ThaiCheckerBoard.GAME_OVER_BLACK_WIN;
							return ThaiCheckerBoard.GAME_OVER_WHITE_WIN;
						}

						
					} finally {
						lastMoving = null;						
						System.gc();
					}
				}
			}
			return ThaiCheckerBoard.GAME_PLAYING;
	}
	
	public boolean contains(int x, int y) {
		if (x < this.x) return false;
		if (x > this.x + this.boardSize) return false;
		if (y < this.y) return false;
		if (y > this.y + this.boardSize) return false;
		
		return true;
	}
	
}
