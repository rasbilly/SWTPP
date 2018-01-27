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
	
	public void setBoardChar(int i, int k, Character c) {
		if(c.equals('w')) {
			board[i][k] = 1;
		}else if(c.equals('W')) {
			board[i][k] = 2;
		}else if(c.equals('b')) {
			board[i][k] = -1;
		}else if(c.equals('B')) {
			board[i][k] = -2;
		}
	}
	
	public int setBoardInt(int i, int k, Character c) {
		int x = Integer.parseInt(Character.toString(c));
		for(int l=0;l<x;l++) {
			board[i][k]=0;
			k++;
		}
		return k;
	}
	
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
					k = setBoardInt(i,k,c);					
				}catch(Exception e) {
					setBoardChar(i, k, c);
					k++;
				}
			}
		}
	}
	
	public String getBoardChar(int i, int j, String boardString) {
		if(board[i][j]==1) {
			boardString += "w";
		}else if(board[i][j]==2) {
			boardString += "W";
		}else if(board[i][j]==-1) {
			boardString += "b";
		}else if(board[i][j]==-2) {
			boardString += "B";
		}
		return boardString;
	}
	
	public String readRow(int i, String boardString) {
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
			boardString = getBoardChar(i, j, boardString);
			j++;
		}
		return boardString;
	}
	
	@Override
	public String getBoard() {
		String boardString = "";
		for(int i=0;i<10;i++) {
			boardString = readRow(i,boardString);
			if(i!=9) {
				boardString += "/";
			}
		}
		return boardString;
	}
	
	public boolean getPlayerBoolean(Player player) {
		return player.equals(this.blackPlayer) ? false : true;
	}
	
	public boolean correctPlayer(boolean playerIsWhite) {
		if((isWhiteNext() && !playerIsWhite) || (!isWhiteNext() && playerIsWhite)) {return false;}
		return true;
	}
	
	public void findCities() {
		for(int i=0;i<10;i++) {
			for(int j=0;j<10;j++) {
				if(board[i][j]==2) {whiteCityPlaced = true;}
				if(board[i][j]==-2) {blackCityPlaced = true;}
			}
		}
	}
	
	public int placeWhiteCity(int pos1x, int pos1y, int pos2x, int pos2y, boolean playerIsWhite) {
		findCities();
		if(!whiteCityPlaced) {
			if(blackCityPlaced && !playerIsWhite) {return -1;}
			if(playerIsWhite) {
				if(pos1x-pos2x != 0 || pos1y-pos2y !=0 || pos1y!=0|| pos1x==0 || pos1x==9) {return -1;}
				board[pos1y][pos1x] = 2;
				whiteCityPlaced = true;
				updateNext();
				return 1;
			}
		}
		return 0;
	}
	
	public int placeBlackCity(int pos1x, int pos1y, int pos2x, int pos2y, boolean playerIsWhite) {
		findCities();
		if(!blackCityPlaced) {
			if(playerIsWhite) {return -1;
			}else {
				if(pos1x-pos2x != 0 || pos1y-pos2y !=0 || pos1y!=9 || pos1x==0 || pos1x==9) {return -1;}
				board[pos1y][pos1x] = -2;
				updateNext();
				blackCityPlaced = true;
				return 1;
			}
		}
		return 0;
	}
	
	public int[] parseString(String moveString) {
		int[] posArray = new int[4];
		String[] pos = moveString.split("-");
		if(pos.length!=2 || pos[0].length()!=2 || pos[1].length()!=2) {return null;}
		posArray[0] = pos[0].charAt(0)-'a';
		try{posArray[1] = 9-Integer.parseInt(Character.toString(pos[0].charAt(1)));}catch(Exception e) {return null;}
		posArray[2] = pos[1].charAt(0)-'a';
		try{posArray[3] = 9-Integer.parseInt(Character.toString(pos[1].charAt(1)));}catch(Exception e) {return null;}
		if(!(0<=posArray[0] && posArray[0]<=9)|| !(0<=posArray[2] && posArray[2]<=9)) {return null;}
		return posArray;
	}
	
	public boolean trySoldierMove(int pos1x, int pos1y, int pos2x, int pos2y, boolean playerIsWhite) {
		int diff_y = pos2y-pos1y;
		if((diff_y<0 && playerIsWhite) || (diff_y>0 && !playerIsWhite)) {return false;}
		if(((board[pos2y][pos2x]==1 || board[pos2y][pos2x]==2) && playerIsWhite)|| ((board[pos2y][pos2x]==-1 || board[pos2y][pos2x]==-2) && !playerIsWhite)) {return false;}
		boolean hitCity = false;
		if(board[pos2y][pos2x]==2 	|| board[pos2y][pos2x]==-2){ hitCity = true;}
		board[pos2y][pos2x] = playerIsWhite? 1: -1;
		board[pos1y][pos1x] = 0;
		if(hitCity) {
			finish(playerIsWhite? this.whitePlayer : this.blackPlayer);
		}else{
			updateNext();
		}
		return true;
	}
	
	public boolean trySoldierHit(int pos1x, int pos1y, int pos2x, int pos2y, boolean playerIsWhite) {
		if((board[pos2y][pos2x]!=-1 && board[pos2y][pos2x]!=-2 && playerIsWhite) || (board[pos2y][pos2x]!=1 && board[pos2y][pos2x]!=2 && !playerIsWhite)) {return false;}
		boolean hitCity = false;
		if(board[pos2y][pos2x]==2 	|| board[pos2y][pos2x]==-2){ hitCity = true;}
		board[pos2y][pos2x] = playerIsWhite? 1: -1;
		board[pos1y][pos1x] = 0;
		if(hitCity) {
			finish(playerIsWhite? this.whitePlayer : this.blackPlayer);
		}else{
			updateNext();
		}
		return true;
	}
	
	public boolean trySoldierBackup(int pos1x, int pos1y, int pos2x, int pos2y, boolean playerIsWhite) {
		int diff_x = pos2x-pos1x;
		int diff_y = pos2y-pos1y;
		if((diff_y<0 && !playerIsWhite) || (diff_y>0 && playerIsWhite)) {return false;}
		if(!isThreatened(pos1x, pos1y, playerIsWhite?-1:1)) {return false;}
		if(board[pos2y][pos2x]!=0 
				|| board[pos1y+diff_y/2][pos1x+diff_x/2]!=0) {
			return false;}
		board[pos2y][pos2x] = playerIsWhite ? 1 : -1;
		board[pos1y][pos1x] = 0;
		updateNext();
		return true;
	}
	
	public boolean tryCannonMove(int pos1x, int pos1y, int pos2x, int pos2y, boolean playerIsWhite) {
		int diff_x = pos2x-pos1x;
		int diff_y = pos2y-pos1y;
		if((board[pos1y+diff_y/3][pos1x+diff_x/3]!=1
				&& playerIsWhite) 
				|| (board[pos1y+diff_y*2/3][pos1x+diff_x*2/3]!=1 
				&& playerIsWhite)) 
		{return false;}
		if((board[pos1y+diff_y/3][pos1x+diff_x/3]!=-1 
				&& !playerIsWhite) 
				|| (board[pos1y+diff_y*2/3][pos1x+diff_x*2/3]!=-1 
				&& !playerIsWhite)) 
		{return false;}
		if(board[pos2y][pos2x]!=0) {return false;}
		board[pos2y][pos2x] = playerIsWhite ? 1 : -1;
		board[pos1y][pos1x] = 0;
		updateNext();
		return true;
	}
	
	public boolean tryShotLeft(int pos1x, int pos1y, int pos2x, int pos2y, int soldier) {
		int diff_y = pos2y-pos1y;
		if(diff_y<0) {
			if((board[pos1y-1][pos1x-1]!=soldier) || (board[pos1y-2][pos1x-2]!=soldier)){return false;}
			if(board[pos1y-3][pos1x-3]!=0) {return false;}
		}else if(diff_y==0) {
			if((board[pos1y][pos1x-1]!=soldier) || (board[pos1y][pos1x-2]!=soldier)){return false;}
			if(board[pos1y][pos1x-3]!=0) {return false;}
		}else{
			if((board[pos1y+1][pos1x-1]!=soldier)|| (board[pos1y+2][pos1x-2]!=soldier)){return false;}
			if(board[pos1y+3][pos1x-3]!=0) {return false;}
		}
		return true;
	}
	
	public boolean tryShotVertical(int pos1x, int pos1y, int pos2x, int pos2y, int soldier) {
		int diff_y = pos2y-pos1y;
		if(diff_y<0) {
			if((board[pos1y-1][pos1x]!=soldier) || (board[pos1y-2][pos1x]!=soldier)) {return false;}
			if(board[pos1y-3][pos1x]!=0) {return false;}
		}else{
			if((board[pos1y+1][pos1x]!=soldier) || (board[pos1y+2][pos1x]!=soldier)) {return false;}
			if(board[pos1y+3][pos1x]!=0) {return false;}
		}
		return true;
	}
	
	public boolean tryShotRight(int pos1x, int pos1y, int pos2x, int pos2y, int soldier) {
		int diff_y = pos2y-pos1y;
		if(diff_y<0) {
			if((board[pos1y-1][pos1x+1]!=soldier) || (board[pos1y-2][pos1x+2]!=soldier)) {return false;}
			if(board[pos1y-3][pos1x+3]!=0) {return false;}
		}else if(diff_y==0) {
			if((board[pos1y][pos1x+1]!=soldier) || (board[pos1y][pos1x+2]!=soldier)) {return false;}
			if(board[pos1y][pos1x+3]!=0) {return false;}
		}else{
			if((board[pos1y+1][pos1x+1]!=soldier) || (board[pos1y+2][pos1x+2]!=soldier)) {return false;}
			if(board[pos1y+3][pos1x+3]!=0) {return false;}
		}
		return true;
	}
	
	public boolean tryCannonShot(int pos1x, int pos1y, int pos2x, int pos2y, boolean playerIsWhite) {
		//System.out.print(pos1x);System.out.print(pos1y);System.out.print(pos2x);System.out.print(pos2y);System.out.println();
		int diff_x = pos2x-pos1x;
		int soldier = playerIsWhite ? 1 : -1;
		if(board[pos2y][pos2x]!=-soldier && board[pos2y][pos2x]!=-soldier*2) {return false;}
		if(diff_x<0) {
			if(!tryShotLeft(pos1x, pos1y, pos2x, pos2y, soldier)) {return false;}
		}else if(diff_x==0) {
			if(!tryShotVertical(pos1x, pos1y, pos2x, pos2y, soldier)) {return false;}
		}else{
			if(!tryShotRight(pos1x, pos1y, pos2x, pos2y, soldier)) {return false;}
		}
		boolean shotCity = false;
		if(board[pos2y][pos2x]==2 || board[pos2y][pos2x]==-2) {shotCity = true;}
		board[pos2y][pos2x] = 0;
		if(shotCity) {
			finish(playerIsWhite? this.whitePlayer : this.blackPlayer);
		}else{
			updateNext();
		}
		return true;
	}
	
	@Override
	public boolean tryMove(String moveString, Player player) {
		boolean playerIsWhite = getPlayerBoolean(player);
		if(!correctPlayer(playerIsWhite)) {return false;}
		int[] posArray = parseString(moveString);
		if(posArray==null) {return false;}		
		if(posArray[2]-posArray[0]!=0 && posArray[3]-posArray[1]!=0 && Math.abs(posArray[2]-posArray[0])!=Math.abs(posArray[3]-posArray[1])) {return false;}
		if(Math.abs(posArray[2]-posArray[0]) > 5 || Math.abs(posArray[3]-posArray[1]) > 5) {return false;}
		int whiteCity = placeWhiteCity(posArray[0], posArray[1], posArray[2], posArray[3], playerIsWhite);
		if(whiteCity==-1) {return false;}
		if(whiteCity==1) {return true;}
		int blackCity = placeBlackCity(posArray[0], posArray[1], posArray[2], posArray[3], playerIsWhite);
		if(blackCity==-1) {return false;}
		if(blackCity==1) {return true;}
		return tryMove2(posArray, playerIsWhite);
	}
	
	public boolean tryMove2(int[] posArray, boolean playerIsWhite) {
		if(board[posArray[1]][posArray[0]]==0 || board[posArray[1]][posArray[0]]==2 || board[posArray[1]][posArray[0]]==-2) {return false;}
		if((board[posArray[1]][posArray[0]]==1 && !playerIsWhite) || (board[posArray[1]][posArray[0]]==-1 && playerIsWhite)) {return false;}
		if(Math.abs(posArray[3]-posArray[1])==1) {return trySoldierMove(posArray[0], posArray[1], posArray[2], posArray[3], playerIsWhite);}
		if(Math.abs(posArray[2]-posArray[0])==1) {return trySoldierHit(posArray[0], posArray[1], posArray[2], posArray[3], playerIsWhite);}
		if(Math.abs(posArray[2]-posArray[0])==2 && Math.abs(posArray[3]-posArray[1])!=2) {return false;}
		if(Math.abs(posArray[3]-posArray[1])==2) {return trySoldierBackup(posArray[0], posArray[1], posArray[2], posArray[3], playerIsWhite);}
		if(Math.abs(posArray[2]-posArray[0])==3 || Math.abs(posArray[3]-posArray[1])==3) {return tryCannonMove(posArray[0], posArray[1], posArray[2], posArray[3], playerIsWhite);}
		if(Math.abs(posArray[2]-posArray[0])==4 || Math.abs(posArray[3]-posArray[1])==4 || Math.abs(posArray[2]-posArray[0])==5 || Math.abs(posArray[3]-posArray[1])==5) {return tryCannonShot(posArray[0], posArray[1], posArray[2], posArray[3], playerIsWhite);}
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
