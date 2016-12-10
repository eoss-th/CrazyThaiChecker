package com.example.thaicheckerdroid;

import android.graphics.Canvas;
import android.view.SurfaceHolder;

public class GameLogic extends Thread {
	
	private SurfaceHolder surfaceHolder;
	private GameView mGameView;
	private int game_state;
	public static final int PAUSE = 0;
	public static final int READY = 1;
	public static final int RUNNING = 2;

	public GameLogic(SurfaceHolder surfaceHolder, GameView mGameView) {
		super();
		this.surfaceHolder = surfaceHolder;
		this.mGameView = mGameView;
	}

	public void setGameState(int gamestate) {
		this.game_state = gamestate;
	}

	public int getGameState() {
		return game_state;
	}

	@Override
	public void run() {
		Canvas canvas;
		while (game_state == RUNNING) {
			canvas = null;
			try {
				canvas = this.surfaceHolder.lockCanvas();
				synchronized (surfaceHolder) {
					this.mGameView.update();
					this.mGameView.onDraw(canvas);
				}
			} finally {
				if (canvas != null) {
					surfaceHolder.unlockCanvasAndPost(canvas);
				}
			}
		}
	}
}
