package game.checker.thai;

public class ThaiCheckerBoard {

	public static final int BLACK_PLAYER = 1;
	public static final int WHITE_PLAYER = 3;
	
	public static final byte XX = 0;
	public static final byte BS = 1;
	public static final byte BK = 2;
	public static final byte WS = 3;
	public static final byte WK = 4;
	
	public static final int GAME_PLAYING = 0;
	public static final int GAME_OVER_DRAW = 1;
	public static final int GAME_OVER_BLACK_WIN = 2;
	public static final int GAME_OVER_WHITE_WIN = 3;

	private byte [] gameState;

    private boolean fightingMode;

	public ThaiCheckerBoard() {
		this (new byte[] 
				{  BS, BS, BS, BS,
				  BS, BS, BS, BS,
				   XX, XX, XX, XX,
				  XX, XX, XX, XX,
				   XX, XX, XX, XX,
				  XX, XX, XX, XX,
				   WS, WS, WS, WS,
				  WS, WS, WS, WS }, false);
	}
	
	public ThaiCheckerBoard(byte[]gameState, boolean fightingMode) {
		this.gameState = gameState;
        this.fightingMode = fightingMode;
	}

	/**
	 *    0, 1, 2, 3
	 *  4, 5, 6, 7
	 *    8, 9, 10, 11
	 *  12, 13, 14, 15
	 *    16, 17, 18, 19
	 *  20, 21, 22, 23
	 *    24, 25, 26, 27
	 *  28, 29, 30, 31
	 */
	private static final int [][] PATH = 
		{{-1, -1, 4, 5}, {-1, -1, 5, 6}, {-1, -1, 6, 7}, {-1, -1, 7, -1},
		{-1, 0, -1, 8}, {0, 1, 8, 9}, {1, 2, 9, 10}, {2, 3, 10, 11}, 
		{4, 5, 12, 13}, {5, 6, 13, 14}, {6, 7, 14, 15}, {7, -1, 15, -1}, 
		{-1, 8, -1, 16}, {8, 9, 16, 17}, {9, 10, 17, 18}, {10, 11, 18, 19}, 
		{12, 13, 20, 21}, {13, 14, 21, 22}, {14, 15, 22, 23}, {15, -1, 23, -1}, 
		{-1, 16, -1, 24}, {16, 17, 24, 25}, {17, 18, 25, 26}, {18, 19, 26, 27}, 
		{20, 21, 28, 29}, {21, 22, 29, 30}, {22, 23, 30, 31}, {23, -1, 31, -1}, 
		{-1, 24, -1, -1}, {24, 25, -1, -1}, {25, 26, -1, -1}, {26, 27, -1, -1}, 
		};
	/*
	private static final int [] scoreBoard = {	
		4, 4, 4, 4,
	   4, 3, 3, 3,
	    3, 2, 2, 4,
	   4, 2, 1, 3,
  	    3, 1, 2, 4,
	   4, 2, 2, 3,
	    3, 3, 3, 4,
	   4, 4, 4, 4,
	};*/

	private static final int [] scoreBoard = {
			0, 1, 1, 0,
			0, 0, 1, 0,
			0, 0, 0, 0,
			0, 0, 0, 0,
			0, 0, 0, 0,
			0, 0, 0, 0,
			0, 1, 0, 0,
			0, 1, 1, 0,
	};
	
	public class Moving {
		int player, from, to;
		ThaiCheckerBoard nextBoard;
		Moving(int player, int from, int to) {
			this.player = player;
			this.from = from;
			this.to = to;
			nextBoard = ThaiCheckerBoard.this;
		}

        public int getPlayer() {return player;}
        public int getOpponent() {return player==BLACK_PLAYER?WHITE_PLAYER:BLACK_PLAYER;}
		public int getFrom() {return from;}
		public int getTo() {return to;}
		public ThaiCheckerBoard next() {
			return nextBoard;
		}
	}
		
	public class Walking extends Moving {
		
		public Walking(int player, int from, int to, boolean canPromote) {
			super (player, from, to);
			byte [] newGameState = new byte[gameState.length];
			System.arraycopy(gameState, 0, newGameState, 0, gameState.length);

			//Move
			newGameState[to] = newGameState[from];
			newGameState[from] = XX;
			
			//King Line
			if (canPromote && ( (to >=0 && to <= 3) || (to >=28 && to <= 31) )) {
				//Promote
				newGameState[to]++;
			}
			
			nextBoard = new ThaiCheckerBoard(newGameState, fightingMode);
		}
		
	}
	
	public class Killing extends Moving {
		
		public Killing(int player, int from, int to, int target, boolean canPromote) {
			super (player, from, to);
			byte [] newGameState = new byte[gameState.length];
			System.arraycopy(gameState, 0, newGameState, 0, gameState.length);

			//Eating
			newGameState[to] = newGameState[from];
			newGameState[target] = XX;
			newGameState[from] = XX;
			
			//King Line
			if (canPromote && ( (to >=0 && to <= 3) || (to >=28 && to <= 31) )) {
				//Promote to King
				newGameState[to]++;
			}
			
			nextBoard = new ThaiCheckerBoard(newGameState, fightingMode);
		}
		
	}
	
	//Get Next Location
	public static int getNextPosition(int from, int direction) {
		
		return PATH[from][direction];
	}

    public int count() {
        int value, which;

        int score = 0;
        for (int w=0; w<gameState.length; w++) {

            which = gameState[w];

            switch (which) {
                case BS: value = 1; break;
                case BK: value = 10; break;
                case WS: value = -1; break;
                case WK: value = -10; break;
                default: value = 0;
            }

            score+=value;
        }

        return score;
    }

	public int _score() {
		int value, which;
		
		int score = 0;
        int blackCount = 0;
		int whiteCount = 0;
		for (int w=0; w<gameState.length; w++) {
			
			which = gameState[w];
			
			switch (which) {
			case BS: value = (w >= 24 && w <=27)? 70: (w==0||w==1||w==2||w==6) ? 60 : 50; blackCount++; break;
			case BK: value = 100; blackCount++; break;
			case WS: value = (w >= 4 && w <=7)? -70: (w==25||w==29||w==30||w==31) ? -60 : -50; whiteCount++; break;
			case WK: value = -100; whiteCount++; break;
			default: value = 0;
			}
			
			score += value + scoreBoard[w];
		}

        //Win
        if (whiteCount==0) {
            return 100;
        }
		
		return score;
	}

    public int score() {
        int which;

        int score = 0;
        int blackCount = 0;
        int whiteCount = 0;
        for (int w=0; w<gameState.length; w++) {

            which = gameState[w];

            switch (which) {
                case BS: score += (w >= 24 && w <=27)? 12: 10 + scoreBoard[w]; blackCount++; break;
                case BK: score += 15; blackCount++; break;
                case WS: score -= (w >= 4 && w <=7)? 12: 10 + scoreBoard[w]; whiteCount++; break;
                case WK: score -= 15; whiteCount++; break;
            }

        }

        //Win
        if (whiteCount==0) {
            return 50;
        }

        return score;
    }

    public boolean isfightingMode() {
        return fightingMode;
    }
	
	public int state() {
		
		int blackSoldierCount = 0;
		int blackKingCount = 0;
		
		int whiteSoldierCount = 0;
		int whiteKingCount = 0;

        int w;
        for (int i=0; i<gameState.length; i++) {
            w = gameState[i];
            switch (w) {
                case BS: blackSoldierCount++; break;
                case BK: blackKingCount++; break;
                case WS: whiteSoldierCount++; if (!fightingMode) fightingMode=i<=19; break;
                case WK: whiteKingCount++; break;
            }
        }

		int blackTeamCount = blackSoldierCount + blackKingCount;
		int whiteTeamCount = whiteSoldierCount + whiteKingCount;
		
		if (blackTeamCount != whiteTeamCount) {
			
			if (blackTeamCount==0)
				return GAME_OVER_WHITE_WIN;
			if (whiteTeamCount==0)
				return GAME_OVER_BLACK_WIN;

		}  else if (blackTeamCount==1 && (gameState[3]==XX && gameState[28]==XX)) {

			return GAME_OVER_DRAW;
		}
		
		return GAME_PLAYING;
	}
	
	public int get(int position) {
		return gameState[position];
	}
	
	public int size() {
		return gameState.length;
	}
	
	public void print() {
		//Print Board
		System.out.print("  ");
		
		String w;
		for (int i=0, j=1; i<gameState.length; i++) {

			if (gameState[i]==1)
				w = "BS";
			else if (gameState[i]==2)
				w = "BK";
			else if (gameState[i]==3)
				w = "WS";
			else if (gameState[i]==4)
				w = "WK";
			else
				w = "__";
			
			System.out.print(w+ "[" + labelBoard[i]+ "],  ");

			//Check newline
			if ( (i+1)%4==0) {

				System.out.println();

				//Correct Indent
				if (j%2==0)
					System.out.print("  ");
				j++;
			}
		}
	}
	
	private static final String [] labelBoard = {	
		"00", "01", "02", "03",
	 "04", "05", "06", "07",
		"08", "09", "10", "11",
	 "12", "13", "14", "15",
		"16", "17", "18", "19",
	 "20", "21", "22", "23",
		"24", "25", "26", "27",
	 "28", "29", "30", "31",
	};
	
}
