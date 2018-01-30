package de.tuberlin.sese.swtpp.gameserver.test.cannon;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import de.tuberlin.sese.swtpp.gameserver.control.GameController;
import de.tuberlin.sese.swtpp.gameserver.model.Player;
import de.tuberlin.sese.swtpp.gameserver.model.User;
import de.tuberlin.sese.swtpp.gameserver.model.cannon.CannonGame;

public class TryMoveTest {

	User user1 = new User("Alice", "alice");
	User user2 = new User("Bob", "bob");
	
	Player whitePlayer = null;
	Player blackPlayer = null;
	CannonGame game = null;
	GameController controller;
	
	@Before
	public void setUp() throws Exception {
		controller = GameController.getInstance();
		controller.clear();
		
		int gameID = controller.startGame(user1, "");
		
		game = (CannonGame) controller.getGame(gameID);
		whitePlayer = game.getPlayer(user1);

	}
	
	public void startGame(String initialBoard, boolean whiteNext) {
		controller.joinGame(user2);		
		blackPlayer = game.getPlayer(user2);
		
		game.setBoard(initialBoard);
		game.setNextPlayer(whiteNext? whitePlayer:blackPlayer);
	}
	
	public void assertMove(String move, boolean white, boolean expectedResult) {
		if (white)
			assertEquals(expectedResult, game.tryMove(move, whitePlayer));
		else 
			assertEquals(expectedResult,game.tryMove(move, blackPlayer));
	}
	
	public void assertGameState(String expectedBoard, boolean whiteNext, boolean finished, boolean whiteWon) {
		assertEquals(expectedBoard,game.getBoard().replaceAll("e", ""));
		assertEquals(whiteNext, game.isWhiteNext());

		assertEquals(finished, game.isFinished());
		if (!game.isFinished()) {
			assertEquals(whiteNext, game.isWhiteNext());
		} else {
			assertEquals(whiteWon, whitePlayer.isWinner());
			assertEquals(!whiteWon, blackPlayer.isWinner());
		}
	}
	

	/*******************************************
	 * !!!!!!!!! To be implemented !!!!!!!!!!!!
	 *******************************************/
	
	@Test
	public void exampleTest() {
		startGame("5W4/1w1w1w1w1w/1w1w1w1w1w/1w3w1w1w/2w7/5b4/b1b3b1b1/b1b1b1b1b1/b1b1b1b1b1/3B6",true);
		assertMove("h6-h5",true,true);
		assertGameState("5W4/1w1w1w1w1w/1w1w1w1w1w/1w3w3w/2w4w2/5b4/b1b3b1b1/b1b1b1b1b1/b1b1b1b1b1/3B6",false,false,false);		
	}
	
	@Test
	public void test0() {
		//normal starting positions, white's turn
		startGame("5W4/1w1w1w1w1w/1w1w1w1w1w/1w1w1w1w1w///b1b1b1b1b1/b1b1b1b1b1/b1b1b1b1b1/3B6",true);
		assertMove("h6-h5",false,false);		//wrong player
		assertMove("",true,false);				//empty move string
		assertMove("a1-a1-a1",true,false);		//too many coordinates (3)
		assertMove("a11-a1",true,false);		//wrong first coordinate (a11)
		assertMove("a1-a11",true,false);		//wrong second coordinate (a11)
		assertMove("1a-a1",true,false);			//wrong order of letter and number (1a)
		assertMove("a1-1a",true,false);			//wrong order of letter and number (1a)
		assertMove("z1-a1",true,false);			//wrong letter (z)
		assertMove("a1-z1",true,false);			//wrong letter (z)
		assertMove(" 1-a1",true,false);			//space
		assertMove("a1- 1",true,false);			//space
		assertMove("a1-b4",true,false);			//difference between x and y coordinates not the same
		assertMove("a1-i1",true,false);			//x difference > 5
		assertMove("a1-a9",true,false);			//y difference > 5
	}
	
	@Test
	public void testWhiteCity() {
		//no cities placed yet, white's turn
		startGame("/1w1w1w1w1w/1w1w1w1w1w/1w1w1w1w1w///b1b1b1b1b1/b1b1b1b1b1/b1b1b1b1b1/",true);
		assertMove("b0-b0",true,false);			//wrong row
		assertMove("a0-b0",true,false);			//not same x coordinates
		assertMove("a0-a1",true,false);			//not same y coordinates
		assertMove("a0-b1",true,false);			//not same x&y coordinates
		assertMove("a9-a9",true,false);			//corner
		assertMove("j9-j9",true,false);			//corner
		assertMove("b9-b9",true,true);			//placed white city
	}
	
	@Test
	public void testBlackCity() {
		//no cities placed yet, black's turn
		startGame("/1w1w1w1w1w/1w1w1w1w1w/1w1w1w1w1w///b1b1b1b1b1/b1b1b1b1b1/b1b1b1b1b1/",false);
		assertMove("a0-b0",false,false);		//not same x coordinates
		assertMove("a0-a1",false,false);		//not same y coordinates
		assertMove("a0-b1",false,false);		//not same x&y coordinates
		assertMove("a0-a0",false,false);		//corner
		assertMove("j0-j0",false,false);		//corner
		assertMove("b1-b1",false,false);		//wrong row
		assertMove("b0-b0",false,true);			//placed black city
	}
	
	@Test
	public void test3() {
		//white city not placed yet, black's turn
		startGame("/1w1w1w1w1w/1w1w1w1w1w/1w1w1w1w1w///b1b1b1b1b1/b1b1b1b1b1/b1b1b1b1b1/1B8",false);
		assertMove("b0-b0",false,false);		//black's turn although white city not placed
	}
	
	@Test
	public void test4() {
		//white city not placed yet, white's turn
		startGame("/1w1w1w1w1w/1w1w1w1w1w/1w1w1w1w1w///b1b1b1b1b1/b1b1b1b1b1/b1b1b1b1b1/1B8",true);
		assertMove("b0-b0",true,false);			//wrong row
	}
	
	@Test
	public void test7() {
		//black city not placed yet, black's turn
		startGame("1W8/1w1w1w1w1w/1w1w1w1w1w/1w1w1w1w1w///b1b1b1b1b1/b1b1b1b1b1/b1b1b1b1b1/",false);
		assertMove("b0-b0",false,true);			//corner
	}
	
	@Test
	public void test6() {
		//black city not placed yet, white's turn
		startGame("1W8/1w1w1w1w1w/1w1w1w1w1w/1w1w1w1w1w///b1b1b1b1b1/b1b1b1b1b1/b1b1b1b1b1/",true);
		assertMove("b0-b0",true,false);			//white's turn although black city not placed
	}
	
	@Test
	public void test8() {
		//normal starting positions, white's turn
		startGame("1W8/1w1w1w1w1w/1w1w1w1w1w/1w1w1w1w1w///b1b1b1b1b1/b1b1b1b1b1/b1b1b1b1b1/1B8",true);
		assertMove("b0-b0",true,false);			//placing city but city already placed
		assertMove("b9-b9",true,false);			//placing city but city already placed
		assertMove("a0-a0",true,false);			//placing city but city already placed
		assertMove("b8-b9",true,false);			//wrong direction
		assertMove("c1-c0",true,false);			//wrong soldier color
		assertMove("b8-b7",true,false);			//friendly soldier in the way
		assertMove("b6-c6",true,false);			//cannot move horizontally
		assertMove("b6-d6",true,false);			//cannot backup horizontally
		assertMove("b6-b4",true,false);			//cannot backup forward
		assertMove("b6-b5",true,true);			//correct vertical soldier move
	}
	
	@Test
	public void test10() {
		//normal starting positions, white's turn
		startGame("1W8/1w1w1w1w1w/1w1w1w1w1w/1w1w1w1w1w///b1b1b1b1b1/b1b1b1b1b1/b1b1b1b1b1/1B8",true);
		assertMove("d6-c5",true,true);			//correct diagonal soldier move
	}
	
	@Test
	public void test9() {
		//normal starting positions, black's turn
		startGame("1W8/1w1w1w1w1w/1w1w1w1w1w/1w1w1w1w1w///b1b1b1b1b1/b1b1b1b1b1/b1b1b1b1b1/1B8",false);
		assertMove("b8-b8",false,false);		//placing city but city already placed
		assertMove("c1-c1",false,false);		//placing city but city already placed
		assertMove("c1-c0",false,false);		//wrong direction
		assertMove("b8-b9",false,false);		//wrong soldier color
		assertMove("c1-c2",false,false);		//friendly soldier in the way 
		assertMove("c3-d3",false,false);		//cannot move horizontally
		assertMove("a2-c0",false,false);		//cannot backup, not threatened
		assertMove("a3-a5",false,false);		//cannot backup forward
		assertMove("a3-a1",false,false);		//target cell not empty
		assertMove("c3-c4",false,true);			//correct vertical soldier move
	}
	
	@Test
	public void test11() {
		//black soldier at b6, white's turn
		startGame("1W8/1w1w1w1w1w/1w1w1w1w1w/1b1w1w1w1w///b1b1b1b1b1/b1b1b1b1b1/b1b1b1b1b1/1B8",true);
		assertMove("b7-b6",true,true);			//correct vertical hit
	}
	
	@Test
	public void test12() {
		//black soldier at b6, black's turn
		startGame("1W8/1w1w1w1w1w/1w1w1w1w1w/1b1w1w1w1w///b1b1b1b1b1/b1b1b1b1b1/b1b1b1b1b1/1B8",false);
		assertMove("b6-b7",false,true);			//correct vertical hit
	}
	
	@Test
	public void test13() {
		//white soldier at b1, white's turn
		startGame("1W8/1w1w1w1w1w/1w1w1w1w1w/1w1w1w1w1w///b1b1b1b1b1/b1b1b1b1b1/bwb1b1b1b1/1B8",true);
		assertMove("b1-a1",true,true);			//correct horizontal hit
	}
	
	@Test
	public void test14() {
		//white soldier at b1, black's turn
		startGame("1W8/1w1w1w1w1w/1w1w1w1w1w/1w1w1w1w1w///b1b1b1b1b1/b1b1b1b1b1/bwb1b1b1b1/1B8",false);
		assertMove("c3-d3",false,false);		//cannot move horizontally
		assertMove("a1-b1",false,true);			//correct horizontal hit
	}
	
	@Test
	public void test15() {
		//white soldier at a9,c0, black soldier at c9,a0, black's turn
		startGame("wWb7/1w1w1w1w1w/1w1w1w1w1w/1w1w1w1w1w///b1b1b1b1b1/b1b1b1b1b1/bwb1b1b1b1/bBw7",false);
		assertMove("a0-b0",false,false);		//cannot move horizontally into city
		assertMove("c9-b9",false,true);			//correct horizontal city hit
	}
	
	@Test
	public void test16() {
		//white soldier at a9,c0, black soldier at c9,a0, white's turn
		startGame("wWb7/1w1w1w1w1w/1w1w1w1w1w/1w1w1w1w1w///b1b1b1b1b1/b1b1b1b1b1/bwb1b1b1b1/bBw7",true);
		assertMove("c9-b9",true,false);			//cannot move horizontally into city
		assertMove("c0-b0",true,true);			//correct horizontal city hit
	}
	
	@Test
	public void test17() {
		//white soldiers at a7,j7,c0, black soldier at c9, white's turn
		startGame("1Wb7//w8w///////1Bw7",true);
		assertMove("a7-a9",true,false);			//cannot backup, not threatened
		assertMove("j7-j9",true,false);			//cannot backup, not threatened
		assertMove("c0-c2",true,false);			//cannot backup, not threatened
		//white soldiers at a7,j7,c0, black soldier at c9, black's turn
		startGame("1Wb7//w8w///////1Bw7",false);
		assertMove("c9-c7",false,false);		//cannot backup, not threatened
		//white soldier at b7, black soldier at a8, white's turn
		startGame("8W1/b9/1w8///////1B8",true);
		assertMove("b7-b9",true,true);			//correct backup
		//white soldier at b7, black soldier at b8, white's turn
		startGame("8W1/1b8/1w8///////1B8",true);
		assertMove("b7-b9",true,false);			//backup path blocked
		//white soldier at b7, black soldier at c8, white's turn
		startGame("8W1/2b7/1w8///////1B8",true);
		assertMove("b7-b9",true,true);			//correct backup
		//white soldier at b7, black soldier at a7, white's turn
		startGame("8W1//bw8///////1B8",true);
		assertMove("b7-b9",true,true);			//correct backup
		//white soldier at b7, black soldier at c7, white's turn
		startGame("8W1//1wb7///////1B8",true);
		assertMove("b7-b9",true,true);			//correct backup
		//white soldier at b7, black soldier at a6, white's turn
		startGame("8W1//1w8/b9//////1B8",true);
		assertMove("b7-b9",true,true);			//correct backup
		//white soldier at b7, black soldier at b6, white's turn
		startGame("8W1//1w8/1b8//////1B8",true);
		assertMove("b7-b9",true,true);			//correct backup
		//white soldier at b7, black soldier at c6, white's turn
		startGame("8W1//1w8/2b7//////1B8",true);
		assertMove("b7-b9",true,true);			//correct backup
		//white soldier at b9,b7, black soldier at c6, white's turn
		startGame("1w6W1//1w8/2b7//////1B8",true);
		assertMove("b7-b9",true,false);			//target cell not empty
		//white soldier at b7,b9, black soldier at c6, black's turn
		startGame("1w6W1//1w8/2b7//////1B8",false);
		assertMove("c6-c4",false,true);			//correct backup
	}
	
	@Test
	public void test18() {
		startGame("1W8/1w1w1w1w1w/1w1w1w1w1w/1w1w1w1w1w///b1b1b1b1b1/b1b1b1b1b1/b1b1b1b1b1/1B8",true);
		assertMove("b7-b4",true,false);
		assertMove("b6-b3",true,false);
		assertMove("b6-b9",true,false);
		assertMove("b8-b5",true,true);
		assertMove("a2-a5",false,false);
		assertMove("a3-a6",false,false);
		assertMove("a1-a4",false,true);
		startGame("1W8/1www1w1w1w/1w1w1w1w1w/1w1w1w1w1w///b1b1b1b1b1/b1b1b1b1b1/b1b1b1b1b1/1B8",true);
		assertMove("b8-e8",true,true);
	}
	
	@Test
	public void testCannonShot() {
		startGame("Wbbbbbbbbb/bbbbbbbbbb//b1wwwwww1b/b1wwwwww1b/b1wwwwww1b//bbbbbbbbbb/bbbbbbbbbb/8B1",true);
		assertMove("c4-c8",true,true);
		startGame("Wbbbbbbbbb/bbbbbbbbbb//b1wwwwww1b/b1wwwwww1b/b1wwwwww1b//bbbbbbbbbb/bbbbbbbbbb/8B1",true);
		assertMove("c4-c9",true,true);
		startGame("Wbbbbbbbbb/bbbbbbbbbb//b1wwwwww1b/b1wwwwww1b/b1wwwwww1b//bbbbbbbbbb/bbbbbbbbbb/8B1",true);
		assertMove("c6-c2",true,true);
		startGame("Wbbbbbbbbb/bbbbbbbbbb//b1wwwwww1b/b1wwwwww1b/b1wwwwww1b//bbbbbbbbbb/bbbbbbbbbb/8B1",true);
		assertMove("c6-c1",true,true);
		startGame("Wbbbbbbbbb/bbbbbbbbbb//b1wwwwww1b/b1wwwwww1b/b1wwwwww1b//bbbbbbbbbb/bbbbbbbbbb/8B1",true);
		assertMove("f4-b8",true,true);
		startGame("Wbbbbbbbbb/bbbbbbbbbb//b1wwwwww1b/b1wwwwww1b/b1wwwwww1b//bbbbbbbbbb/bbbbbbbbbb/8B1",true);
		assertMove("e4-j9",true,true);
		startGame("Wbbbbbbbbb/bbbbbbbbbb//b1wwwwww1b/b1wwwwww1b/b1wwwwww1b//bbbbbbbbbb/bbbbbbbbbb/8B1",true);
		assertMove("f6-b2",true,true);
		startGame("Wbbbbbbbbb/bbbbbbbbbb//b1wwwwww1b/b1wwwwww1b/b1wwwwww1b//bbbbbbbbbb/bbbbbbbbbb/8B1",true);
		assertMove("e6-j1",true,true);
		startGame("Wbbbbbbbbb/bbbbbbbbbb//bb1wwww1bb/bb1wwww1bb/bb1wwww1bb//bbbbbbbbbb/bbbbbbbbbb/8B1",true);
		assertMove("f4-b4",true,true);
		startGame("Wbbbbbbbbb/bbbbbbbbbb//bb1wwww1bb/bb1wwww1bb/bb1wwww1bb//bbbbbbbbbb/bbbbbbbbbb/8B1",true);
		assertMove("f4-a4",true,true);
		startGame("Wbbbbbbbbb/bbbbbbbbbb//bb1wwww1bb/bb1wwww1bb/bb1wwww1bb//bbbbbbbbbb/bbbbbbbbbb/8B1",true);
		assertMove("e4-i4",true,true);
		startGame("Wbbbbbbbbb/bbbbbbbbbb//bb1wwww1bb/bb1wwww1bb/bb1wwww1bb//bbbbbbbbbb/bbbbbbbbbb/8B1",true);
		assertMove("f5-a0",true,false);
		assertMove("e4-j4",true,true);
		
		startGame("1W8//1b8/1b8/1b8/////1B8",false);
		assertMove("b5-b9",false,true);
		startGame("1W8/////1w8/1w8/1w8//1B8",true);
		assertMove("b4-b0",true,true);
		
		startGame("8W1/w9////b9////8B1",true);
		assertMove("a8-a4",true,false);
		startGame("8W1/w9/w9///b9////8B1",true);
		assertMove("a8-a4",true,false);
		startGame("8W1/w9/w9/w9/b9/b9////8B1",true);
		assertMove("a8-a4",true,false);
		startGame("8W1/b9////w9////8B1",true);
		assertMove("a4-a8",true,false);
		startGame("8W1/b9///w9/w9////8B1",true);
		assertMove("a4-a8",true,false);
		startGame("8W1/b9/b9/w9/w9/w9////8B1",true);
		assertMove("a4-a8",true,false);
		
		startGame("8W1/b3w3b1////////8B1",true);
		assertMove("e8-a8",true,false);
		assertMove("e8-i8",true,false);
		startGame("8W1/b2www2b1////////8B1",true);
		assertMove("e8-a8",true,false);
		assertMove("e8-i8",true,false);
		startGame("8W1/bbwwwwwbb1////////8B1",true);
		assertMove("e8-a8",true,false);
		assertMove("e8-i8",true,false);
		
		startGame("8W1/b7b1////4w41////bB6b1",true);
		assertMove("e4-a8",true,false);
		assertMove("e4-i8",true,false);
		assertMove("e4-a0",true,false);
		assertMove("e4-i0",true,false);
		startGame("8W1/b7b1///3w1w4/4w5/3w1w4///bB6b1",true);
		assertMove("e4-a8",true,false);
		assertMove("e4-i8",true,false);
		assertMove("e4-a0",true,false);
		assertMove("e4-i0",true,false);
		startGame("8W1/b7b1/bbbbbbbbbb/2w3w3/3w1w4/4w5/3w1w4/2w3w3/bbbbbbbbbb/bB6b1",true);
		assertMove("e4-a8",true,false);
		assertMove("e4-i8",true,false);
		assertMove("e4-a0",true,false);
		assertMove("e4-i0",true,false);
		
		startGame("1W8//1b8/1b8/1b8/////1B8",false);
		assertMove("b5-b9",false,true);
		startGame("1W8/////1w8/1w8/1w8//1B8",true);
		assertMove("b4-b0",true,true);
	}
	
	@Test public void test21() {
		startGame("1W8/1b8///////1w8/1B8",false);
		assertMove("b8-b9",false,true);
		startGame("1W8/1b8///////1w8/1B8",true);
		assertMove("b1-b0",true,true);
	}
	
	//TODO: implement test cases of same kind as example here
}
