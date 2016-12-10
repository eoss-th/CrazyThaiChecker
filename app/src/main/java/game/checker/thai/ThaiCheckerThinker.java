package game.checker.thai;

import java.util.ArrayList;
import java.util.List;

public class ThaiCheckerThinker {

	private static List<ThaiCheckerBoard.Moving> recursiveSoldierEat(
			ThaiCheckerBoard board, int soldier, int startDir,int endDir, int soldierOpponent, int kingOpponent, int position, int firstStart) {
		List<ThaiCheckerBoard.Moving> result = new ArrayList<ThaiCheckerBoard.Moving>();
		int nextPosition, which, targetPosition;
		
		for (int dir=startDir; dir<=endDir; dir++) {

			nextPosition = ThaiCheckerBoard.getNextPosition(position, dir);
			if (nextPosition==-1) continue;

			which = board.get(nextPosition);

			//Found opponent
			if ( which == soldierOpponent || which == kingOpponent) {

				targetPosition = nextPosition;
				nextPosition = ThaiCheckerBoard.getNextPosition(nextPosition, dir);
				if (nextPosition==-1) continue;

				which = board.get(nextPosition);
				//Can Eat
				if (which==ThaiCheckerBoard.XX) {
					
					ThaiCheckerBoard.Moving killing = 
							board.new Killing(position, nextPosition, targetPosition, true);
					
					//Search available next eating
					List<ThaiCheckerBoard.Moving> nextEating = 
							recursiveSoldierEat(killing.next(), soldier, startDir, endDir, soldierOpponent, kingOpponent, nextPosition, firstStart);
					
					//Add only final eating
					if (nextEating.isEmpty()) {
						
						//More than one eating
						killing.from = firstStart;						
						result.add(killing);
						
					} else {
						
						result.addAll(nextEating);
						
					}
				}

			} 

		}
		return result;
	}
	
	private static List<ThaiCheckerBoard.Moving> soldierEat(
			ThaiCheckerBoard board, int soldier, int startDir,int endDir, int soldierOpponent, int kingOpponent) {

		List<ThaiCheckerBoard.Moving> result = new ArrayList<ThaiCheckerBoard.Moving>();
		int which;
		//Have to eat?
		for (int position=0; position<board.size(); position++) {

			which = board.get(position);
			if (which==soldier) {
				
				result.addAll(recursiveSoldierEat(board, soldier, startDir, endDir, soldierOpponent, kingOpponent, position, position));

			}
		}
		return result;
	}
	
	private static List<ThaiCheckerBoard.Moving> recursiveKingEat(ThaiCheckerBoard board, int king, int soldierOpponent, int kingOpponent, int position, int firstStart) {
		List<ThaiCheckerBoard.Moving> result = new ArrayList<ThaiCheckerBoard.Moving>();
		int nextPosition, which, targetPosition;
		
		for (int dir=0; dir<=3; dir++) {

			nextPosition = ThaiCheckerBoard.getNextPosition(position, dir);
			if (nextPosition==-1) continue;

			//Search Opponent
			while (true) {
				which = board.get(nextPosition);

				if (which==ThaiCheckerBoard.XX) {
					nextPosition = ThaiCheckerBoard.getNextPosition(nextPosition, dir);
					if (nextPosition==-1) break;
					continue;
				}

				//Found Opponent
				if ( which == soldierOpponent || which == kingOpponent) {

					targetPosition = nextPosition;
					nextPosition = ThaiCheckerBoard.getNextPosition(nextPosition, dir);
					if (nextPosition==-1) break;

					which = board.get(nextPosition);
					//Can Eat
					if (which==ThaiCheckerBoard.XX) {
						
						ThaiCheckerBoard.Moving killing = 
								board.new Killing(position, nextPosition, targetPosition, false);
						
						//Search available next eating
						List<ThaiCheckerBoard.Moving> nextEating = recursiveKingEat(killing.next(), king, soldierOpponent, kingOpponent, nextPosition, firstStart);
						
						//Add only final eating
						if (nextEating.isEmpty()) {
							
							//More than one eating
							killing.from = firstStart;							
							result.add(killing);
							
						} else {
							
							result.addAll(nextEating);
							
						}
					}
				}
				break;
			}
		}
		return result;
	}
	
	private static List<ThaiCheckerBoard.Moving> kingEat(ThaiCheckerBoard board, int king, int soldierOpponent, int kingOpponent) {

		List<ThaiCheckerBoard.Moving> result = new ArrayList<ThaiCheckerBoard.Moving>();
		int which;
		//Have to eat?
		for (int position=0; position<board.size(); position++) {

			which = board.get(position);
			if (which==king) {

				result.addAll(recursiveKingEat(board, king, soldierOpponent, kingOpponent, position, position));
				
			}
		}
		return result;
	}
	
	public static List<ThaiCheckerBoard.Moving> think(ThaiCheckerBoard board, int team) {

		List<ThaiCheckerBoard.Moving> result = new ArrayList<ThaiCheckerBoard.Moving>();
		int nextPosition, which, startDir, endDir, soldier, king, soldierOpponent, kingOpponent;
		if (team == ThaiCheckerBoard.BLACK_PLAYER) {

			soldier = ThaiCheckerBoard.BS;
			king = ThaiCheckerBoard.BK;
			startDir = 2;
			endDir = 3;
			soldierOpponent = ThaiCheckerBoard.WS;
			kingOpponent = ThaiCheckerBoard.WK;

		} else {

			soldier = ThaiCheckerBoard.WS;
			king = ThaiCheckerBoard.WK;
			startDir = 0;
			endDir = 1;
			soldierOpponent = ThaiCheckerBoard.BS;
			kingOpponent = ThaiCheckerBoard.BK;
			
		}

		result.addAll(
				soldierEat(board, soldier, startDir, endDir, soldierOpponent, kingOpponent));
		result.addAll(
				kingEat(board, king, soldierOpponent, kingOpponent));
		//Can walk?
		if (result.isEmpty()) {
			for (int position=0; position<board.size(); position++) {

				if (board.get(position)==soldier) {

					for (int dir=startDir; dir<=endDir; dir++) {

						nextPosition = ThaiCheckerBoard.getNextPosition(position, dir);
						if (nextPosition==-1) continue;

						which = board.get(nextPosition);

						if (which==ThaiCheckerBoard.XX) {
							
							ThaiCheckerBoard.Moving walking = 
									board.new Walking(position, nextPosition, true);
														
							result.add(walking);
						} 

					}

				} else if (board.get(position)==king) {
					
					for (int dir=0; dir<=3; dir++) {
						
						nextPosition = ThaiCheckerBoard.getNextPosition(position, dir);
						if (nextPosition==-1) continue;
						
						while (true) {
							which = board.get(nextPosition);
							
							if (which==ThaiCheckerBoard.XX) {
								
								ThaiCheckerBoard.Moving walking = 
										board.new Walking(position, nextPosition, false);
															
								result.add(walking);
								
								nextPosition = ThaiCheckerBoard.getNextPosition(nextPosition, dir);
								
								if (nextPosition==-1) break;
								continue;
							}
							break;
						}
					}
				}
			}
		}

		return result;
	}	
	
	private static ThaiCheckerBoard.Moving minimax(int player, ThaiCheckerBoard.Moving moving, int level, int alpha, int beta) {
		
		if (level==0) return moving;
				
		List<ThaiCheckerBoard.Moving> choices = think(moving.next(), player);
		
		if (choices.isEmpty()) return moving;
		
		int score;
		ThaiCheckerBoard.Moving test;
		ThaiCheckerBoard.Moving best = moving;
		
		if (player==ThaiCheckerBoard.BLACK_PLAYER) {
			for (ThaiCheckerBoard.Moving m:choices) {
				test = minimax(ThaiCheckerBoard.WHITE_PLAYER, m, level-1, alpha, beta);
				score = test.next().score();
				if (score > alpha) {
					alpha = score;
					best = test;
					if (level==6) best = m;
				}
				
				if (score > beta) {
					return test;
				}
				
			}			
		} else {
			
			for (ThaiCheckerBoard.Moving m:choices) {
				test = minimax(ThaiCheckerBoard.BLACK_PLAYER, m, level-1, alpha, beta);
				score = test.next().score();
				if (score < beta) {
					beta = score;
					best = test;
				}
				
				if (score < alpha) {
					return test;
				}
				
			}
			
		}
		
		return best;
	}
		
	public static ThaiCheckerBoard.Moving minimax(ThaiCheckerBoard.Moving moving) {
		return minimax(ThaiCheckerBoard.BLACK_PLAYER, moving, 6, Integer.MIN_VALUE, Integer.MAX_VALUE);
	}
}
