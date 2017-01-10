package com.example.thaicheckerdroid;

import java.util.List;

import game.checker.thai.ThaiCheckerBoard;
import game.checker.thai.ThaiCheckerThinker;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.media.AudioManager;
import android.media.SoundPool;

public class SpriteBoard {
	
	private ThaiCheckerBoard board;
	private int x;
	private int y;
	private int pieceSize;
	private int boardSize;
	private int draggingIndex;
	private ThaiCheckerBoard.Moving lastMoving;
	private List<ThaiCheckerBoard.Moving> availableMovings;
    private int nextPlayer;
	
	private int swordSound;
	private int stepSound;
	private SoundPool soundPool;
	
	private int volume;

    private boolean thinking;

	private int numPlayers;

    private Paint textPaint;
	private Paint boardPaint1;
	private Paint boardPaint2;

	private SpriteCircle blackSprite;
	private SpriteCircle blackKingSprite;

	private SpriteCircle whiteSprite;
	private SpriteCircle whiteKingSprite;

    private boolean isFightingMode;
    private int count;

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

	public SpriteBoard(Context context, ThaiCheckerBoard board, int numPlayers, int x, int y, int boardSize) {
		this.board = board;
		this.numPlayers = numPlayers;
		this.x = x;
		this.y = y;
		
		this.boardSize = boardSize;
		pieceSize = boardSize / 8;
		draggingIndex = -1;
		
		availableMovings = ThaiCheckerThinker.think(ThaiCheckerBoard.WHITE_PLAYER, board);
        nextPlayer = ThaiCheckerBoard.WHITE_PLAYER;

		soundPool = new SoundPool(10, AudioManager.STREAM_MUSIC, 0);
		swordSound = soundPool.load(context, R.raw.steelsword, 1);
		stepSound = soundPool.load(context, R.raw.step, 1);
		
		 AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);		
		 volume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);

        textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

        textPaint.setColor(context.getResources().getColor(android.R.color.white));
        textPaint.setTextSize(40);

		boardPaint1 = new Paint(Paint.ANTI_ALIAS_FLAG);
		boardPaint1.setColor(context.getResources().getColor(android.R.color.white));
		boardPaint1.setStyle(Paint.Style.FILL_AND_STROKE);

		boardPaint2 = new Paint(Paint.ANTI_ALIAS_FLAG);
		boardPaint2.setColor(context.getResources().getColor(android.R.color.holo_blue_dark));
		boardPaint2.setStyle(Paint.Style.FILL_AND_STROKE);

		blackSprite = new SpriteCircle(x, y, pieceSize, context.getResources().getColor(android.R.color.holo_orange_dark), context.getResources().getColor(android.R.color.holo_orange_light));
		blackKingSprite = new SpriteCircle(x, y, pieceSize, context.getResources().getColor(android.R.color.holo_orange_light), context.getResources().getColor(android.R.color.white));

		whiteSprite = new SpriteCircle(x, y, pieceSize, context.getResources().getColor(android.R.color.holo_green_dark), context.getResources().getColor(android.R.color.holo_green_light));
		whiteKingSprite = new SpriteCircle(x, y, pieceSize, context.getResources().getColor(android.R.color.holo_green_light), context.getResources().getColor(android.R.color.white));

	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	public void setX(int x) {
		this.x = x;
	}

	public void setY(int y) {
		this.y = y;
	}

	public void draw(Canvas canvas) {
		
		synchronized (this) {
			
			//canvas.drawBitmap(bitmap, x, y, null);
			canvas.drawRect(new Rect(x, y, x + boardSize, y + boardSize), boardPaint1);

			int postX, postY;
			
			int which;
			Bitmap img;
			for (int i=0; i < 32; i++) {

				postX = pieceSize * 2 * (i%4) + ((i/4)%2== 0?pieceSize:0);
				postY =  pieceSize * (i/4);

                canvas.drawRect(new Rect(postX + x, postY + y, postX + x + pieceSize, postY + y + pieceSize), boardPaint2);

                if (i==draggingIndex) continue;

				which = board.get(i);
				switch (which) {
				case ThaiCheckerBoard.BS: blackSprite.set(x + postX, y + postY); blackSprite.draw(canvas); break;
				case ThaiCheckerBoard.BK: blackKingSprite.set(x + postX, y + postY); blackKingSprite.draw(canvas); break;
				case ThaiCheckerBoard.WS: whiteSprite.set(x + postX, y + postY); whiteSprite.draw(canvas); break;
				case ThaiCheckerBoard.WK: whiteKingSprite.set(x + postX, y + postY); whiteKingSprite.draw(canvas); break;
				}

			}

			if (numPlayers==1) {

                String emotional;
                if (!isFightingMode) {
                    emotional = "(ಠ_ಠ)";
                } else if (count==0) {
                    emotional = "(ʘ_ʘ)";
                } else if (count<0) {
                    emotional = "(ಥ_ಥ)";
                } else {
                    emotional = "(ಠ‿ಠ)";
                }

                canvas.drawText(emotional, boardSize/2 - 50, 50, textPaint);

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
						break;
					}
				}
				
			}
			
		}
		
		draggingIndex = -1;
	}
	
	public SpriteCircle drag(int touchX, int touchY) {
		
		if (lastMoving!=null) return null;
		
		if (contains(touchX, touchY)==false) return null;
		
		int pos = getBoardPosition(touchX, touchY);
		
		if (pos==-1) return null;
		
		int which = board.get(pos);

        draggingIndex = pos;

        if (nextPlayer==ThaiCheckerBoard.WHITE_PLAYER) {

            switch (which) {
                case ThaiCheckerBoard.WS: return whiteSprite;
                case ThaiCheckerBoard.WK: return whiteKingSprite;
            }

        } else if (nextPlayer==ThaiCheckerBoard.BLACK_PLAYER) {

            switch (which) {
                case ThaiCheckerBoard.BS: return blackSprite;
                case ThaiCheckerBoard.BK: return blackKingSprite;
            }

        }

        //Reset dragging
        draggingIndex = -1;

        return null;
	}

	public int update() {

        if (lastMoving!=null) {
            synchronized (this) {

                try {
                    board = lastMoving.next();
                    if (lastMoving instanceof ThaiCheckerBoard.Killing) {
                        soundPool.play(swordSound, volume, volume, 1, 0, 1);
                    } else {
                        soundPool.play(stepSound, volume, volume, 1, 0, 1);
                    }

                    int state = board.state();
                    if (state != ThaiCheckerBoard.GAME_PLAYING) return state;

                    count = board.count();
                    isFightingMode = board.isfightingMode();

                    availableMovings = ThaiCheckerThinker.think(lastMoving.getOpponent(), board);

                    if (availableMovings.isEmpty()) {
                        if (count > 0) return ThaiCheckerBoard.GAME_OVER_BLACK_WIN;
                        return ThaiCheckerBoard.GAME_OVER_WHITE_WIN;
                    }

                    nextPlayer = lastMoving.getOpponent();

                    if (numPlayers==1) {
						think(lastMoving);
						System.gc();
					}


                } finally {

                    lastMoving = null;
                }
            }
        }

		return ThaiCheckerBoard.GAME_PLAYING;
	}

    public void think (final ThaiCheckerBoard.Moving moving) {
        if (moving.getPlayer()==ThaiCheckerBoard.WHITE_PLAYER && thinking==false) {
            thinking = true;
            new Thread() {
                public void run() {

                    int deep = 6;
                    lastMoving = ThaiCheckerThinker.minimax(moving, deep);
                    thinking = false;
                }
            }.start();
        }
    }
	
	public boolean contains(int x, int y) {
		if (x < this.x) return false;
		if (x > this.x + this.boardSize) return false;
		if (y < this.y) return false;
		if (y > this.y + this.boardSize) return false;
		
		return true;
	}
	
}
