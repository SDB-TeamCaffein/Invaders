package engine;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

import screen.*;

/**
 * Implements core game logic.
 *
 * @author <a href="mailto:RobertoIA1987@gmail.com">Roberto Izquierdo Amo</a>
 *
 */
public final class Core {

	/** Width of current screen. */
	private static final int WIDTH = 448;
	/** Height of current screen. */
	private static final int HEIGHT = 520;
	/** Max fps of current screen. */
	private static final int FPS = 60;
	/** Max lives. */
	private static final int MAX_LIVES = 3;
	/** Levels between extra life. */
	private static final int EXTRA_LIFE_FRECUENCY = 3;
	/** Total number of levels. */
	private static final int NUM_LEVELS = 8;

	/** Difficulty settings for boss. */
	private static final GameSettings SETTINGS_LEVEL_boss =
			new GameSettings(1, 1, 2, 500, true);
	/**default level*/
	/** Difficulty default settings for level 1. */
	private static final GameSettings SETTINGS_Default_LEVEL_1 =
			new GameSettings(5, 4, 60, 2000, false);
	/** Difficulty default settings for level 2. */
	private static final GameSettings SETTINGS_Default_LEVEL_2 =
			new GameSettings(5, 5, 50, 2500, false);
	/** Difficulty default settings for level 3. */
	private static final GameSettings SETTINGS_Default_LEVEL_3 =
			new GameSettings(6, 5, 40, 1500, false);
	/** Difficulty default settings for level 4. */
	private static final GameSettings SETTINGS_Default_LEVEL_4 =
			new GameSettings(6, 6, 30, 1500, false);
	/** Difficulty default settings for level 5. */
	private static final GameSettings SETTINGS_Default_LEVEL_5 =
			new GameSettings(7, 6, 20, 1000, false);
	/** Difficulty default settings for level 6. */
	private static final GameSettings SETTINGS_Default_LEVEL_6 =
			new GameSettings(7, 7, 20, 1500, false);
	/** Difficulty default settings for level 7. */
	private static final GameSettings SETTINGS_Default_LEVEL_7 =
			new GameSettings(8, 7, 20, 1300, false);


	/** Hard level */
	/** Difficulty hard settings for level 1. */
	private static final GameSettings SETTINGS_Hard_LEVEL_1 =
			new GameSettings(5, 4, 30, 1500, false);
	/** Difficulty hard settings for level 2. */
	private static final GameSettings SETTINGS_Hard_LEVEL_2 =
			new GameSettings(5, 5, 20, 1500, false);
	/** Difficulty hard settings for level 3. */
	private static final GameSettings SETTINGS_Hard_LEVEL_3 =
			new GameSettings(6, 5, 15, 1000, false);
	/** Difficulty hard settings for level 4. */
	private static final GameSettings SETTINGS_Hard_LEVEL_4 =
			new GameSettings(6, 6, 20, 1500, false);
	/** Difficulty hard settings for level 5. */
	private static final GameSettings SETTINGS_Hard_LEVEL_5 =
			new GameSettings(7, 6, 20, 700, false);
	/** Difficulty hard settings for level 6. */
	private static final GameSettings SETTINGS_Hard_LEVEL_6 =
			new GameSettings(7, 7, 10, 1000, false);
	/** Difficulty hard settings for level 7. */
	private static final GameSettings SETTINGS_Hard_LEVEL_7 =
			new GameSettings(8, 7, 15, 1000, false);



	/**Expert level*/
	/** Difficulty expert settings for level 1. */
	private static final GameSettings SETTINGS_Expert_LEVEL_1 =
			new GameSettings(5, 4, 10, 1000, false);
	/** Difficulty expert settings for level 2. */
	private static final GameSettings SETTINGS_Expert_LEVEL_2 =
			new GameSettings(5, 5, 10, 1000, false);
	/** Difficulty expert settings for level 3. */
	private static final GameSettings SETTINGS_Expert_LEVEL_3 =
			new GameSettings(6, 5, 10, 700, false);
	/** Difficulty expert settings for level 4. */
	private static final GameSettings SETTINGS_Expert_LEVEL_4 =
			new GameSettings(6, 6, 15, 700, false);
	/** Difficulty expert settings for level 5. */
	private static final GameSettings SETTINGS_Expert_LEVEL_5 =
			new GameSettings(7, 6, 15, 800, false);
	/** Difficulty expert settings for level 6. */
	private static final GameSettings SETTINGS_Expert_LEVEL_6 =
			new GameSettings(7, 7, 10, 700, false);
	/** Difficulty expert settings for level 7. */
	private static final GameSettings SETTINGS_Expert_LEVEL_7 =
			new GameSettings(8, 7, 2, 500, false);


	/** Frame to draw the screen on. */
	private static Frame frame;
	/** Screen currently shown. */
	private static Screen currentScreen;
	/** Difficulty default settings list. */
	private static List<GameSettings> gameSettings_Default;
	/** Difficulty hard settings list. */
	private static List<GameSettings> gameSettings_Hard;
	/** Difficulty expert settings list. */
	private static List<GameSettings> gameSettings_Expert;
	/** Application logger. */
	private static final Logger LOGGER = Logger.getLogger(Core.class
			.getSimpleName());
	/** Logger handler for printing to disk. */
	private static Handler fileHandler;
	/** Logger handler for printing to console. */
	private static ConsoleHandler consoleHandler;
	private static Audio background;


	/**
	 * Test implementation.
	 *
	 * @param args
	 *            Program args, ignored.
	 */
	public static void main(final String[] args) {
		try {
			LOGGER.setUseParentHandlers(false);

			fileHandler = new FileHandler("log");
			fileHandler.setFormatter(new MinimalFormatter());

			consoleHandler = new ConsoleHandler();
			consoleHandler.setFormatter(new MinimalFormatter());

			LOGGER.addHandler(fileHandler);
			LOGGER.addHandler(consoleHandler);
			LOGGER.setLevel(Level.ALL);

		} catch (Exception e) {
			// TODO handle exception
			e.printStackTrace();
		}

		frame = new Frame(WIDTH, HEIGHT);
		DrawManager.getInstance().setFrame(frame);
		int width = frame.getWidth();
		int height = frame.getHeight();


		/**Default level setting*/
		gameSettings_Default = new ArrayList<GameSettings>();
		gameSettings_Default.add(SETTINGS_Default_LEVEL_1);
		gameSettings_Default.add(SETTINGS_Default_LEVEL_2);
		gameSettings_Default.add(SETTINGS_Default_LEVEL_3);
		gameSettings_Default.add(SETTINGS_Default_LEVEL_4);
		gameSettings_Default.add(SETTINGS_Default_LEVEL_5);
		gameSettings_Default.add(SETTINGS_Default_LEVEL_6);
		gameSettings_Default.add(SETTINGS_Default_LEVEL_7);
		gameSettings_Default.add(SETTINGS_LEVEL_boss);

		/**Hard level setting*/
		gameSettings_Hard = new ArrayList<GameSettings>();
		gameSettings_Hard.add(SETTINGS_Hard_LEVEL_1);
		gameSettings_Hard.add(SETTINGS_Hard_LEVEL_2);
		gameSettings_Hard.add(SETTINGS_Hard_LEVEL_3);
		gameSettings_Hard.add(SETTINGS_Hard_LEVEL_4);
		gameSettings_Hard.add(SETTINGS_Hard_LEVEL_5);
		gameSettings_Hard.add(SETTINGS_Hard_LEVEL_6);
		gameSettings_Hard.add(SETTINGS_Hard_LEVEL_7);
		gameSettings_Hard.add(SETTINGS_LEVEL_boss);

		/**Expert level setting*/
		gameSettings_Expert = new ArrayList<GameSettings>();
		gameSettings_Expert.add(SETTINGS_Expert_LEVEL_1);
		gameSettings_Expert.add(SETTINGS_Expert_LEVEL_2);
		gameSettings_Expert.add(SETTINGS_Expert_LEVEL_3);
		gameSettings_Expert.add(SETTINGS_Expert_LEVEL_4);
		gameSettings_Expert.add(SETTINGS_Expert_LEVEL_5);
		gameSettings_Expert.add(SETTINGS_Expert_LEVEL_6);
		gameSettings_Expert.add(SETTINGS_Expert_LEVEL_7);
		gameSettings_Expert.add(SETTINGS_LEVEL_boss);

		GameState gameState;
		background = new Audio("bgm", true);

		int returnCode = 1;
		do {
			gameState = new GameState(1, 0, MAX_LIVES, 0, 0);

			switch (returnCode) {
				case 1:
					// Main menu.
					currentScreen = new TitleScreen(width, height, FPS);
					background.start();
					LOGGER.info("Starting " + WIDTH + "x" + HEIGHT
							+ " title screen at " + FPS + " fps.");
					returnCode = frame.setScreen(currentScreen);
					LOGGER.info("Closing title screen.");
					break;
				case 2:
					//Game Summary & Manual
					currentScreen=new GameSummaryScreen(width, height,FPS);
					LOGGER.info("Starting " +WIDTH+ "x" +HEIGHT
							+ " Game Summary screen at " +FPS+ " fps.");
					returnCode=frame.setScreen(currentScreen);
					// Game & score.
					do {
						// One extra live every few levels.
						boolean bonusLife = gameState.getLevel()
								% EXTRA_LIFE_FRECUENCY == 0
								&& gameState.getLivesRemaining() < MAX_LIVES;
						boolean bossStage = gameState.getLevel() > 7;

						currentScreen = new GameScreen(gameState,
								gameSettings_Default.get(gameState.getLevel() - 1),
								bonusLife, false, width, height, FPS);
						background.stop();
						LOGGER.info("Starting " + WIDTH + "x" + HEIGHT
								+ " game screen at " + FPS + " fps.");
						frame.setScreen(currentScreen);
						LOGGER.info("Closing game screen.");

						gameState = ((GameScreen) currentScreen).getGameState();

						gameState = new GameState(gameState.getLevel() + 1,
								gameState.getScore(),
								gameState.getLivesRemaining(),
								gameState.getBulletsShot(),
								gameState.getShipsDestroyed());

					} while (gameState.getLivesRemaining() > 0
							&& gameState.getLevel() <= NUM_LEVELS);

					LOGGER.info("Starting " + WIDTH + "x" + HEIGHT
							+ " score screen at " + FPS + " fps, with a score of "
							+ gameState.getScore() + ", "
							+ gameState.getLivesRemaining() + " lives remaining, "
							+ gameState.getBulletsShot() + " bullets shot and "
							+ gameState.getShipsDestroyed() + " ships destroyed.");
					currentScreen = new ScoreScreen(width, height, FPS, gameState);
					returnCode = frame.setScreen(currentScreen);
					LOGGER.info("Closing score screen.");
					break;
				case 3:
					do {
						currentScreen = new SettingScreen(width, height, FPS);
						LOGGER.info("Starting " + WIDTH + "x" + HEIGHT +
								" setting screen at " + FPS + "fps.");
						returnCode = frame.setScreen(currentScreen);
						LOGGER.info("Closing setting screen.");
						switch (returnCode) {
							case 2:
								// window mode setting
								currentScreen = new WindowSettingScreen(width, height, FPS);
								LOGGER.info("Starting " + WIDTH + "x" + HEIGHT
										+ " window setting screen at " + FPS + " fps.");
								returnCode = frame.setScreen(currentScreen);
								LOGGER.info("Closing window mode setting screen");
								switch (returnCode) {
									case 2:
										DrawManager.getInstance().setMiniScreenFrame();
										frame.setSize(WIDTH, HEIGHT);
										frame.resizingScreen();
										frame.getGraphics().fillRect(0, 0, frame.getWidth(), frame.getHeight());
										break;
									case 3:
										DrawManager.getInstance().setMiniScreenFrame();
										frame.setSize((int)Math.round(WIDTH*1.5), (int)Math.round(HEIGHT*1.5));
										frame.resizingScreen();
										frame.getGraphics().fillRect(0, 0, frame.getWidth(), frame.getHeight());
										break;
									case 4:
										DrawManager.getInstance().setFullScreenFrame();
										frame.resizingScreen();
										frame.getGraphics().fillRect(0, 0, frame.getWidth(), frame.getHeight());
										break;
									default:
										break;
								}
								width = frame.getWidth();
								height = frame.getHeight();
								LOGGER.info("Set " + width + "x" + height
										+ " screen at " + FPS + " fps.");
								break;
							case 3:
								// difficulty setting
								currentScreen = new DifficultyScreen(width, height, FPS);
								LOGGER.info("Starting " + WIDTH + "x" + HEIGHT
										+ " difficulty screen at " + FPS + " fps.");
								returnCode = frame.setScreen(currentScreen);
								LOGGER.info("Difficulty screen.");
								switch (returnCode) {
									case 1:
										gameSettings_Default=gameSettings_Default;
										LOGGER.info("NORMAL MODE");
										break;
									case 2:
										gameSettings_Default=gameSettings_Hard;
										LOGGER.info("HARD MODE");
										break;
									case 3:
										gameSettings_Default=gameSettings_Expert;
										LOGGER.info("EXPERT MODE");
										break;
									default:
										break;
								}
								break;
//						case 4:
//							// sound volume setting
//							break;
							case 0:
								returnCode = 1;
								break;
							default:
								break;
						}

					} while (returnCode != 1);
					// Game Setting
					currentScreen = new TitleScreen(width, height, FPS);
					LOGGER.info("Starting " + WIDTH + "x" + HEIGHT +
							" Title at " + FPS + "fps.");
					returnCode = frame.setScreen(currentScreen);
					LOGGER.info("Closing Title screen.");
					break;
				case 4:
					// High scores.
					currentScreen = new HighScoreScreen(width, height, FPS);
					LOGGER.info("Starting " + WIDTH + "x" + HEIGHT
							+ " high score screen at " + FPS + " fps.");
					returnCode = frame.setScreen(currentScreen);
					LOGGER.info("Closing high score screen.");
					break;
				default:
					break;
			}

		} while (returnCode != 0);

		fileHandler.flush();
		fileHandler.close();
		System.exit(0);
	}

	/**
	 * Constructor, not called.
	 */
	private Core() {

	}

	/**
	 * Controls access to the logger.
	 *
	 * @return Application logger.
	 */
	public static Logger getLogger() {
		return LOGGER;
	}

	/**
	 * Controls access to the drawing manager.
	 *
	 * @return Application draw manager.
	 */
	public static DrawManager getDrawManager() {
		return DrawManager.getInstance();
	}

	/**
	 * Controls access to the input manager.
	 *
	 * @return Application input manager.
	 */
	public static InputManager getInputManager() {
		return InputManager.getInstance();
	}

	/**
	 * Controls access to the file manager.
	 *
	 * @return Application file manager.
	 */
	public static FileManager getFileManager() {
		return FileManager.getInstance();
	}

	/**
	 * Controls creation of new cooldowns.
	 *
	 * @param milliseconds
	 *            Duration of the cooldown.
	 * @return A new cooldown.
	 */
	public static Cooldown getCooldown(final int milliseconds) {
		return new Cooldown(milliseconds);
	}

	/**
	 * Controls creation of new cooldowns with variance.
	 *
	 * @param milliseconds
	 *            Duration of the cooldown.
	 * @param variance
	 *            Variation in the cooldown duration.
	 * @return A new cooldown with variance.
	 */
	public static Cooldown getVariableCooldown(final int milliseconds,
											   final int variance) {
		return new Cooldown(milliseconds, variance);
	}
}