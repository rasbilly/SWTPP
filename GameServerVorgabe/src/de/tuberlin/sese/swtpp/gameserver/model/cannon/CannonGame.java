package de.tuberlin.sese.swtpp.gameserver.model.cannon;

import java.io.Serializable;

import de.tuberlin.sese.swtpp.gameserver.model.Game;
import de.tuberlin.sese.swtpp.gameserver.model.Move;
import de.tuberlin.sese.swtpp.gameserver.model.Player;

/**
 * Class LascaGame extends the abstract class Game as a concrete game instance that allows to play 
 * Lasca (http://www.lasca.org/).
 *
 */
public class CannonGame extends Game implements Serializable{


	/**
	 * 
	 */
	private static final long serialVersionUID = 5424778147226994452L;
	
	
	/************************
	 * member
	 ***********************/
	
	// just for better comprehensibility of the code: assign white and black player
	private Player blackPlayer;
	private Player whitePlayer;

	// internal representation of the game state
	private int[][] board = new int[10][10];
	private static int[] nullReihe = {0,0,0,0,0,0,0,0,0,0};
	private boolean whiteCityPlaced = false;
	private boolean blackCityPlaced = false;
	
	/************************
	 * constructors
	 ***********************/
	
	public CannonGame() {
		super();

		setBoard("/1w1w1w1w1w/1w1w1w1w1w/1w1w1w1w1w///b1b1b1b1b1/b1b1b1b1b1/b1b1b1b1b1/");
	}
	
	/*******************************************
	 * Game class functions already implemented
	 ******************************************/
	
	@Override
	public boolean addPlayer(Player player) {
		if (!started) {
			players.add(player);
			
			if (players.size() == 2) {
				started = true;
				this.whitePlayer = players.get(0);
				this.blackPlayer = players.get(1);
				nextPlayer = this.whitePlayer;
			}
			return true;
		}
		
		return false;
	}

	@Override
	public String getStatus() {
		if (error) return "Error";
		if (!started) return "Wait";
		if (!finished) return "Started";
		if (surrendered) return "Surrendered";
		if (draw) return "Draw";
		
		return "Finished";
	}
	
	@Override
	public String gameInfo() {
		String gameInfo = "";
		
		if(started) {
			if(blackGaveUp()) gameInfo = "black gave up";
			else if(whiteGaveUp()) gameInfo = "white gave up";
			else if(didWhiteDraw() && !didBlackDraw()) gameInfo = "white called draw";
			else if(!didWhiteDraw() && didBlackDraw()) gameInfo = "black called draw";
			else if(draw) gameInfo = "draw game";
			else if(finished)  gameInfo = blackPlayer.isWinner()? "black won" : "white won";
		}
			
		return gameInfo;
	}	

	@Override
	public int getMinPlayers() {
		return 2;
	}

	@Override
	public int getMaxPlayers() {
		return 2;
	}
	
	@Override
	public boolean callDraw(Player player) {
		
		// save to status: player wants to call draw 
		if (this.started && ! this.finished) {
			player.requestDraw();
		} else {
			return false; 
		}
	
		// if both agreed on draw:
		// game is over
		if(players.stream().allMatch(p -> p.requestedDraw())) {
			this.finished = true;
			this.draw = true;
			whitePlayer.finishGame();
			blackPlayer.finishGame();
		}	
		return true;
	}
	
	@Override
	public boolean giveUp(Player player) {
		if (started && !finished) {
			if (this.whitePlayer == player) { 
				whitePlayer.surrender();
				blackPlayer.setWinner();
			}
			if (this.blackPlayer == player) {
				blackPlayer.surrender();
				whitePlayer.setWinner();
			}
			finished = true;
			surrendered = true;
			whitePlayer.finishGame();
			blackPlayer.finishGame();
			
			return true;
		}
		
		return false;
	}

	/*******************************************
	 * Helpful stuff
	 ******************************************/
	
	/**
	 * 
	 * @return True if it's white player's turn
	 */
	public boolean isWhiteNext() {
		return nextPlayer == whitePlayer;
	}
	
	/**
	 * Switch next player
	 */
	private void updateNext() {
		if (nextPlayer == whitePlayer) nextPlayer = blackPlayer;
		else nextPlayer = whitePlayer;
	}
	
	/**
	 * Finish game after regular move (save winner, move game to history etc.)
	 * 
	 * @param player
	 * @return
	 */
	public boolean finish(Player player) {
		// public for tests
		if (started && !finished) {
			player.setWinner();
			finished = true;
			whitePlayer.finishGame();
			blackPlayer.finishGame();
			
			return true;
		}
		return false;
	}

	public boolean didWhiteDraw() {
		return whitePlayer.requestedDraw();
	}

	public boolean didBlackDraw() {
		return blackPlayer.requestedDraw();
	}

	public boolean whiteGaveUp() {
		return whitePlayer.surrendered();
	}

	public boolean blackGaveUp() {
		return blackPlayer.surrendered();
	}

	/*******************************************
	 * !!!!!!!!! To be implemented !!!!!!!!!!!!
	 ******************************************/
	
	@Override
	public void setBoard(String state) {

		String[] splitString = state.split("/");
		String[] boardString = {"","","","","","","","","",""};
		for(int i=0;i<splitString.length;i++) {
			boardString[i] = splitString[i];
		}
		for(int i=0;i<10;i++) {
			String s = boardString[i];
			if(s.isEmpty()) {
				board[i] = nullReihe.clone();
				continue;
			}
			int k=0;
			for(int j=0;j<s.length();j++) {
				Character c = s.charAt(j);
				try {
					int x = Integer.parseInt(Character.toString(c));
					for(int l=0;l<x;l++) {
						board[i][k]=0;
						k++;
					}
				}catch(Exception e) {
					if(c.equals('w')) {
						board[i][k] = 1;
					}else if(c.equals('W')) {
						board[i][k] = 2;
					}else if(c.equals('b')) {
						board[i][k] = -1;
					}else if(c.equals('B')) {
						board[i][k] = -2;
					}
					k++;
				}
			}
		}
	}
	
	@Override
	public String getBoard() {
		
		String boardString = "";
		for(int i=0;i<10;i++) {
			int j=0;
			while(j<10) {
				int count = 0;
				while(j<10&& board[i][j]==0) {
					count++;
					j++;
				}
				if(count!=0) {
					if(count==10) {
						break;
					}
					boardString += Integer.toString(count);
				}
				if(j==10) {
					break;
				}
				if(board[i][j]==1) {
					boardString += "w";
				}else if(board[i][j]==2) {
					boardString += "W";
				}else if(board[i][j]==-1) {
					boardString += "b";
				}else if(board[i][j]==-2) {
					boardString += "B";
				}
				j++;
			}
			if(i!=9) {
				boardString += "/";
			}
		}
		return boardString;
	}
	
	@Override
	public boolean tryMove(String moveString, Player player) {
		System.out.println(getBoard());
		if(isWhiteNext()) {
			if(player.equals(this.blackPlayer)) {System.out.println("wrong player");return false;}
		}else {
			if(player.equals(this.whitePlayer)) {System.out.println("wrong player");return false;}
		}
		boolean playerIsWhite = player.equals(this.blackPlayer) ? false : true;
		int soldier = playerIsWhite ? 1 : -1;
		int pos1x,pos1y,pos2x,pos2y;
		String[] pos = moveString.split("-");
		if(pos.length!=2 || pos[0].length()!=2 || pos[1].length()!=2) {System.out.println("length");return false;}
		pos1x = pos[0].charAt(0)-'a';
		try{pos1y = 9-Integer.parseInt(Character.toString(pos[0].charAt(1)));}catch(Exception e) {System.out.println("parseInt");return false;}
		pos2x = pos[1].charAt(0)-'a';
		try{pos2y = 9-Integer.parseInt(Character.toString(pos[1].charAt(1)));}catch(Exception e) {System.out.println("parseInt");return false;}
		if(!(0<=pos1x && pos1x<=9) || !(0<=pos1y && pos1y<=9) || !(0<=pos2x && pos2x<=9) || !(0<=pos2y && pos2y<=9)) {System.out.println("range");return false;}
		int diff_x = pos2x-pos1x;
		int diff_y = pos2y-pos1y;
		int diff_x_abs = Math.abs(diff_x);
		int diff_y_abs = Math.abs(diff_y);
//		System.out.println("Coordinates correct");
//		System.out.println(Integer.toString(pos1x)+Integer.toString(pos1y)+Integer.toString(pos2x)+Integer.toString(pos2y));
		
		if(diff_x!=0 && diff_y!=0 && diff_x_abs!=diff_y_abs) {return false;}
		if(!whiteCityPlaced) {
			if(blackCityPlaced && !playerIsWhite) {return false;}
			if(playerIsWhite) {
				if(diff_x != 0 || diff_y !=0 || pos1y!=0 || pos1x==0 || pos1x==9) {return false;}
				board[pos1y][pos1x] = 2;
				whiteCityPlaced = true;
				updateNext();
				return true;
			}
		}
		if(!blackCityPlaced) {
			if(whiteCityPlaced && playerIsWhite) {return false;}
			if(!playerIsWhite) {
				if(diff_x != 0 || diff_y !=0 || pos1y!=9 || pos1x==0 || pos1x==9) {return false;}
				board[pos1y][pos1x] = -2;
				updateNext();
				blackCityPlaced = true;
				return true;
			}
		}
		if(diff_x_abs > 5 || diff_y_abs > 5) {return false;}
		if(board[pos1y][pos1x]==0 || board[pos1y][pos1x]==2 || board[pos1y][pos1x]==-2) {return false;}
		if((board[pos1y][pos1x]==1 && !playerIsWhite) || (board[pos1y][pos1x]==-1 && playerIsWhite)) {return false;}
		if(diff_y_abs==1) {
			if(diff_x_abs!=0 && diff_x_abs!=1) {return false;}
			if((diff_y<0 && playerIsWhite) || (diff_y>0 && !playerIsWhite)) {return false;}
			if((board[pos2y][pos2x]==1 && playerIsWhite) || (board[pos2y][pos2x]==-1 && !playerIsWhite)) {return false;}
			board[pos2y][pos2x] = soldier;
			board[pos1y][pos1x] = 0;
			updateNext();
			return true;
		}
		if(diff_x_abs==1) {
			if(diff_y!=0) {return false;}
			if((diff_y<0 && playerIsWhite) || (diff_y>0 && !playerIsWhite)) {return false;}
			if((board[pos2y][pos2x]!=-1 && board[pos2y][pos2x]!=-2 && playerIsWhite) || (board[pos2y][pos2x]!=1 && board[pos2y][pos2x]!=2 && !playerIsWhite)) {return false;}
			board[pos2y][pos2x] = soldier;
			board[pos1y][pos1x] = 0;
			updateNext();
			return true;
		}
		if(diff_x_abs==2 && diff_y_abs!=2) {return false;}
		if(diff_y_abs==2) {
			if((diff_y<0 && !playerIsWhite) || (diff_y>0 && playerIsWhite)) {return false;}
			if(!isThreatened(pos1x, pos1y, playerIsWhite?-1:1)) {return false;}
			if(board[pos2y][pos2x]!=0 || board[pos2y+diff_y/2][pos2x+diff_x/2]!=0) {return false;}
			board[pos2y][pos2x] = playerIsWhite ? 1 : -1;
			board[pos1y][pos1x] = 0;
			updateNext();
			return true;
		}
		if(diff_x_abs==3 || diff_y_abs==3) {
			if((board[pos1y+diff_y/3][pos1x+diff_x/3]!=1 && playerIsWhite) || (board[pos1y+diff_y*2/3][pos1x+diff_x*2/3]!=1 && playerIsWhite)) {return false;}
			if((board[pos1y+diff_y/3][pos1x+diff_x/3]!=-1 && !playerIsWhite) || (board[pos1y+diff_y*2/3][pos1x+diff_x*2/3]!=-1 && !playerIsWhite)) {return false;}
			if(board[pos2y][pos2x]!=0) {return false;}
			board[pos2y][pos2x] = soldier;
			board[pos1y][pos1x] = 0;
			updateNext();
			return true;
		}
		if(diff_x_abs==4 || diff_y_abs==4 || diff_x_abs==5 || diff_y_abs==5) {
			if(diff_x<0) {
				if(diff_y<0) {
					if((board[pos1y-1][pos1x-1]!=soldier) || (board[pos1y-2][pos1x-2]!=soldier)) {return false;}
					if(board[pos1y-3][pos1x-3]!=0) {return false;}
				}else if(diff_y==0) {
					if((board[pos1y][pos1x-1]!=soldier) || (board[pos1y][pos1x-2]!=soldier)) {return false;}
					if(board[pos1y][pos1x-3]!=0) {return false;}
				}else if(diff_y>0){
					if((board[pos1y+1][pos1x-1]!=soldier) || (board[pos1y+2][pos1x-2]!=soldier)) {return false;}
					if(board[pos1y+3][pos1x-3]!=0) {return false;}
				}
			}else if(diff_x==0) {
				if(diff_y<0) {
					if((board[pos1y-1][pos1x]!=soldier) || (board[pos1y-2][pos1x]!=soldier)) {return false;}
					if(board[pos1y-3][pos1x]!=0) {return false;}
				}else if(diff_y>0){
					if((board[pos1y+1][pos1x]!=soldier) || (board[pos1y+2][pos1x]!=soldier)) {return false;}
					if(board[pos1y+3][pos1x]!=0) {return false;}
				}
			}else if(diff_x>0){
				if(diff_y<0) {
					if((board[pos1y-1][pos1x+1]!=soldier) || (board[pos1y-2][pos1x+2]!=soldier)) {return false;}
					if(board[pos1y-3][pos1x+3]!=0) {return false;}
				}else if(diff_y==0) {
					if((board[pos1y][pos1x+1]!=soldier) || (board[pos1y][pos1x+2]!=soldier)) {return false;}
					if(board[pos1y][pos1x+3]!=0) {return false;}
				}else if(diff_y>0){
					if((board[pos1y+1][pos1x+1]!=soldier) || (board[pos1y+2][pos1x+2]!=soldier)) {return false;}
					if(board[pos1y+3][pos1x+3]!=0) {return false;}
				}
			}
			if(board[pos2y][pos2x]!=-soldier) {return false;}
			board[pos2y][pos2x] = 0;
			updateNext();
			return true;
		}
		return false;
	}
	
	private boolean isThreatened(int x,int y, int opponent) {
		if(y>=1 && x>= 1 && board[y-1][x-1]==opponent) {return true;}
		if(y>=1 && board[y-1][x]==opponent) {return true;}
		if(y>=1 && x<= 8 && board[y-1][x+1]==opponent) {return true;}
		if(x>= 1 && board[y][x-1]==opponent) {return true;}
		if(x<= 8 && board[y][x+1]==opponent) {return true;}
		if(y<=8 && x>= 1 && board[y+1][x-1]==opponent) {return true;}
		if(y<=8 && board[y+1][x]==opponent) {return true;}
		if(y<=8 && x<= 8 && board[y+1][x+1]==opponent) {return true;}
		return false;
	}
}
