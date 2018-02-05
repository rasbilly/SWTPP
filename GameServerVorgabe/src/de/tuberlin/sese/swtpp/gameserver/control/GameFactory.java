package de.tuberlin.sese.swtpp.gameserver.control;

import de.tuberlin.sese.swtpp.gameserver.model.Game;
import de.tuberlin.sese.swtpp.gameserver.model.User;
import de.tuberlin.sese.swtpp.gameserver.model.cannon.CannonGame;
import de.tuberlin.sese.swtpp.gameserver.model.cannon.HaskellBot;

public class GameFactory {
	
	//TODO: change path to bot executable if desired
	static String workingDir = "%\\GameServerVorgabe";
	public static final String BOT_PATH = workingDir+"\\Bot";
	public static final String BOT_COMMAND = "cmd /c main.exe";
	
	public static Game createGame() {
		return new CannonGame();
	}

	public static User createBot(String type, Game game) {
		switch(type) {
			case "haskell": return new HaskellBot((CannonGame)game, BOT_PATH, BOT_COMMAND);
			default: return null;
		}
	}
	
}
