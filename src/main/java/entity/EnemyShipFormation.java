package entity;


import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.logging.Logger;

import engine.*;
import engine.DrawManager.SpriteType;
import screen.Screen;

/**
 * Groups enemy ships into a formation that moves together.
 *
 * @author <a href="mailto:RobertoIA1987@gmail.com">Roberto Izquierdo Amo</a>
 *
 */
public class EnemyShipFormation implements Iterable<EnemyShip> {

	/** Initial position in the x-axis. */
	private static final int INIT_POS_X = 20;
	/** Initial position in the y-axis. */
	private static final int INIT_POS_Y = 100;
	/** Distance between ships. */
	private static final int SEPARATION_DISTANCE = 40;
	/** Proportion of C-type ships. */
	private static final double PROPORTION_C = 0.2;
	/** Proportion of B-type ships. */
	private static final double PROPORTION_B = 0.4;
	/** Lateral speed of the formation. */
	private static final int X_SPEED = 8;
	/** Downwards speed of the formation. */
	private static final int Y_SPEED = 4;
	/** Speed of the bullets shot by the members. */
	private static final int BULLET_SPEED = 4;
	/** Proportion of differences between shooting times. */
	private static final double SHOOTING_VARIANCE = .2;
	/** Margin on the sides of the screen. */
	private static final int SIDE_MARGIN = 20;
	/** Margin on the bottom of the screen. */
	private static final int BOTTOM_MARGIN = 80;
	/** Distance to go down each pass. */
	private static final int DESCENT_DISTANCE = 20;
	/** Minimum speed allowed. */
	private static final int MINIMUM_SPEED = 10;

	/** DrawManager instance. */
	private DrawManager drawManager;
	/** Application logger. */
	private Logger logger;
	/** Screen to draw ships on. */
	private Screen screen;

	/** List of enemy ships forming the formation. */
	private List<List<EnemyShip>> enemyShips;
	/** Minimum time between shots. */
	private Cooldown shootingCooldown;

	/** UFO 출현 후 쿨다운 */
	private Cooldown specialCooldown;

	/** boss stage check */
	private boolean isBoss;

	/** Number of ships in the formation - horizontally. */
	private int nShipsWide;
	/** Number of ships in the formation - vertically. */
	private int nShipsHigh;
	/** Time between shots. */
	private int shootingInterval;
	/** Variance in the time between shots. */
	private int shootingVariance;
	/** Initial ship speed. */
	private int baseSpeed;
	/** Speed of the ships. */
	private int movementSpeed;
	/** Current direction the formation is moving on. */
	private Direction currentDirection;
	/** Direction the formation was moving previously. */
	private Direction previousDirection;
	/** Interval between movements, in frames. */
	private int movementInterval;
	/** Total width of the formation. */
	private int width;
	/** Total height of the formation. */
	private int height;
	/** Position in the x-axis of the upper left corner of the formation. */
	private int positionX;
	/** Position in the y-axis of the upper left corner of the formation. */
	private int positionY;
	/** Width of one ship. */
	private int shipWidth;
	/** Height of one ship. */
	private int shipHeight;
	/** List of ships that are able to shoot. */
	private List<EnemyShip> shooters;
	/** Number of not destroyed ships. */
	private int shipCount;

	private Audio fastInvader1;
	private Audio fastInvader2;
	private Audio fastInvader3;
	private Audio fastInvader4;
	private int moveCnt;

	private Map<SpriteType, boolean[][]> spriteMap;


	/** Directions the formation can move. */
	private enum Direction {
		/** Movement to the right side of the screen. */
		RIGHT,
		/** Movement to the left side of the screen. */
		LEFT,
		/** Movement to the bottom of the screen. */
		DOWN
	};

	/**
	 * Constructor, sets the initial conditions.
	 *
	 * @param gameSettings
	 *            Current game settings.
	 */
	public EnemyShipFormation(final GameSettings gameSettings) {
		this.drawManager = Core.getDrawManager();
		this.logger = Core.getLogger();
		this.enemyShips = new ArrayList<List<EnemyShip>>();
		this.currentDirection = Direction.RIGHT;
		this.movementInterval = 0;
		this.isBoss = gameSettings.getIsBoss();
		this.nShipsWide = gameSettings.getFormationWidth();
		this.nShipsHigh = gameSettings.getFormationHeight();
		this.shootingInterval = gameSettings.getShootingFrecuency();
		this.shootingVariance = (int) (gameSettings.getShootingFrecuency()
				* SHOOTING_VARIANCE);

		this.specialCooldown = Core.getVariableCooldown(22000 + (int)(Math.random() * 1000 +100), 1);

		this.baseSpeed = gameSettings.getBaseSpeed();
		this.movementSpeed = this.baseSpeed;
		this.positionX = INIT_POS_X;
		this.positionY = INIT_POS_Y;
		this.shooters = new ArrayList<EnemyShip>();
		SpriteType spriteType;

		fastInvader1 = new Audio("fastInvader1", false);
		fastInvader2 = new Audio("fastInvader2", false);
		fastInvader3 = new Audio("fastInvader3", false);
		fastInvader4 = new Audio("fastInvader4", false);
		moveCnt = 1;

		this.logger.info("Initializing " + nShipsWide + "x" + nShipsHigh
				+ " ship formation in (" + positionX + "," + positionY + ")");

		// Each sub-list is a column on the formation.
		for (int i = 0; i < this.nShipsWide; i++)
			this.enemyShips.add(new ArrayList<EnemyShip>());

		for (List<EnemyShip> column : this.enemyShips) {
			for (int i = 0; i < this.nShipsHigh; i++) {
				if (isBoss) {
					spriteType = SpriteType.EnemyBoss;
					column.add(new EnemyShip(positionX, positionY, spriteType, 10,true));
				} else {
					if (i / (float) this.nShipsHigh < PROPORTION_C) {
						spriteType = SpriteType.EnemyShipC1;
						column.add(new EnemyShip((SEPARATION_DISTANCE
								* this.enemyShips.indexOf(column))
								+ positionX, (SEPARATION_DISTANCE * i)
								+ positionY, spriteType, 4, false));
					}
					else if (i / (float) this.nShipsHigh < PROPORTION_B
							+ PROPORTION_C) {
						spriteType = SpriteType.EnemyShipB1;
						column.add(new EnemyShip((SEPARATION_DISTANCE
								* this.enemyShips.indexOf(column))
								+ positionX, (SEPARATION_DISTANCE * i)
								+ positionY, spriteType, 3, false));
					}
					else {
						spriteType = SpriteType.EnemyShipA1;
						column.add(new EnemyShip((SEPARATION_DISTANCE
								* this.enemyShips.indexOf(column))
								+ positionX, (SEPARATION_DISTANCE * i)
								+ positionY, spriteType, 2, false));
					}

				}
				this.shipCount++;
			}
		}

		this.shipWidth = this.enemyShips.get(0).get(0).getWidth() ;
		this.shipHeight = this.enemyShips.get(0).get(0).getHeight();

		this.width = (this.nShipsWide - 1) * SEPARATION_DISTANCE
				+ this.shipWidth;
		this.height = (this.nShipsHigh - 1) * SEPARATION_DISTANCE
				+ this.shipHeight;
		System.out.println(this.width + " , " + this.height);
		for (List<EnemyShip> column : this.enemyShips)
			this.shooters.add(column.get(column.size() - 1));
	}



	/**
	 * Associates the formation to a given screen.
	 *
	 * @param newScreen
	 *            Screen to attach.
	 */
	public final void attach(final Screen newScreen) {
		screen = newScreen;
	}

	/**
	 * Draws every individual component of the formation.
	 */
	public final void draw() {
		for (List<EnemyShip> column : this.enemyShips)
			for (EnemyShip enemyShip : column)
				drawManager.drawEntity(enemyShip, enemyShip.getPositionX(),
						enemyShip.getPositionY());
	}

	/**
	 * Updates the position of the ships.
	 */
	public final void update() {
		if(this.shootingCooldown == null) {
			this.shootingCooldown = Core.getVariableCooldown(shootingInterval,
					shootingVariance);
			this.shootingCooldown.reset();
		}

		if(this.specialCooldown == null) {
			this.specialCooldown = Core.getVariableCooldown(2000, 1);
			this.specialCooldown.reset();
		}

		cleanUp();

		int movementX = 0;
		int movementY = 0;
		double remainingProportion = (double) this.shipCount
				/ (this.nShipsHigh * this.nShipsWide);
		this.movementSpeed = (int) (Math.pow(remainingProportion, 2)
				* this.baseSpeed);
		this.movementSpeed += MINIMUM_SPEED;

		movementInterval++;
		if (movementInterval >= this.movementSpeed) {
			movementInterval = 0;

			boolean isAtBottom = positionY
					+ this.height > 497 - BOTTOM_MARGIN;
			boolean isAtRightSide = positionX
					+ this.width >= 434 - SIDE_MARGIN;

			boolean isAtLeftSide = positionX <= SIDE_MARGIN;
			boolean isAtHorizontalAltitude = positionY % DESCENT_DISTANCE == 0;

			if (currentDirection == Direction.DOWN) {
				if (isAtHorizontalAltitude)
					if (previousDirection == Direction.RIGHT) {
						currentDirection = Direction.LEFT;
						this.logger.info("Formation now moving left 1");
					} else {
						currentDirection = Direction.RIGHT;
						this.logger.info("Formation now moving right 2");
					}
			} else if (currentDirection == Direction.LEFT) {
				if (isAtLeftSide)
					if (!isAtBottom) {
						previousDirection = currentDirection;
						currentDirection = Direction.DOWN;
						this.logger.info("Formation now moving down 3");
					} else {
						currentDirection = Direction.RIGHT;
						this.logger.info("Formation now moving right 4");
					}
			} else {
				if (isAtRightSide)
					if (!isAtBottom) {
						previousDirection = currentDirection;
						currentDirection = Direction.DOWN;
						this.logger.info("Formation now moving down 5");
					} else {
						currentDirection = Direction.LEFT;
						this.logger.info("Formation now moving left 6");
					}
			}

			if (currentDirection == Direction.RIGHT)
				movementX = X_SPEED;
			else if (currentDirection == Direction.LEFT)
				movementX = -X_SPEED;
			else
				movementY = Y_SPEED;

			positionX += movementX;
			positionY += movementY;
			switch (moveCnt) {
				case 1:
					fastInvader4.start();
					moveCnt++;
					break;
				case 2:
					fastInvader1.start();
					moveCnt++;
					break;
				case 3:
					fastInvader2.start();
					moveCnt++;
					break;
				case 4:
					fastInvader3.start();
					moveCnt = 1;
					break;
			}

			// Cleans explosions.
			List<EnemyShip> destroyed;
			for (List<EnemyShip> column : this.enemyShips) {
				destroyed = new ArrayList<EnemyShip>();
				for (EnemyShip ship : column) {
					if (ship != null && ship.isDestroyed()) {
						destroyed.add(ship);
						this.logger.info("Removed enemy "
								+ column.indexOf(ship) + " from column "
								+ this.enemyShips.indexOf(column));
					}
				}
				column.removeAll(destroyed);
			}

			for (List<EnemyShip> column : this.enemyShips)
				for (EnemyShip enemyShip : column) {
					enemyShip.move(movementX, movementY);
					enemyShip.update();
				}
		}


	}

	/**
	 * Cleans empty columns, adjusts the width and height of the formation.
	 */
	private void cleanUp() {
		Set<Integer> emptyColumns = new HashSet<Integer>();
		int maxColumn = 0;
		int minPositionY = Integer.MAX_VALUE;
		for (List<EnemyShip> column : this.enemyShips) {
			if (!column.isEmpty()) {
				// Height of this column
				int columnSize = column.get(column.size() - 1).positionY
						- this.positionY + this.shipHeight;
				maxColumn = Math.max(maxColumn, columnSize);
				minPositionY = Math.min(minPositionY, column.get(0)
						.getPositionY());
			} else {
				// Empty column, we remove it.
				emptyColumns.add(this.enemyShips.indexOf(column));
			}
		}
		for (int index : emptyColumns) {
			this.enemyShips.remove(index);
			logger.info("Removed column " + index);
		}

		int leftMostPoint = 0;
		int rightMostPoint = 0;

		for (List<EnemyShip> column : this.enemyShips) {
			if (!column.isEmpty()) {
				if (leftMostPoint == 0)
					leftMostPoint = column.get(0).getPositionX();
				rightMostPoint = column.get(0).getPositionX();
			}
		}

		this.width = rightMostPoint - leftMostPoint + this.shipWidth;
		this.height = maxColumn;

		this.positionX = leftMostPoint;
		this.positionY = minPositionY;
	}

	/**
	 * Shoots a bullet downwards.
	 *
	 * @param bullets
	 *            Bullets set to add the bullet being shot.
	 */
	public final void shoot(final Set<Bullet> bullets) {
		// For now, only ships in the bottom row are able to shoot.
		int index = (int) (Math.random() * this.shooters.size());
		EnemyShip shooter = this.shooters.get(index);

		if (this.shootingCooldown.checkFinished()) {
			this.shootingCooldown.reset();
			if (shooter.getSpriteType().toString() == "EnemyBoss") {
				bullets.add(BulletPool.getBullet(shooter.getPositionX()
						+ shooter.width / 2, shooter.getPositionY() + shooter.height / 2, BULLET_SPEED * 2, Color.red));
				bullets.add(BulletPool.getBullet(shooter.getPositionX()
						+ shooter.width / 3, shooter.getPositionY() + shooter.height / 2, BULLET_SPEED * 2, Color.red));
				bullets.add(BulletPool.getBullet(shooter.getPositionX()
						+ shooter.width * 2 / 3, shooter.getPositionY() + shooter.height / 2, BULLET_SPEED * 2, Color.red));
			}
			else if (shooter.getSpriteType().toString() == "EnemyShipA1" || shooter.getSpriteType().toString() == "EnemyShipA2") {
				bullets.add(BulletPool.getBullet(shooter.getPositionX()
						+ shooter.width / 2, shooter.getPositionY(), BULLET_SPEED, Color.white));
			}
			else if (shooter.getSpriteType().toString() == "EnemyShipB1" || shooter.getSpriteType().toString() == "EnemyShipB2") {
				bullets.add(BulletPool.getBullet(shooter.getPositionX()
						+ shooter.width / 2, shooter.getPositionY(), BULLET_SPEED, Color.white));
				bullets.add(BulletPool.getBullet(shooter.getPositionX()
						+ shooter.width / 2, shooter.getPositionY() - 80, BULLET_SPEED, Color.white));
			}
			else {
				bullets.add(BulletPool.getBullet(shooter.getPositionX()
						+ shooter.width / 2, shooter.getPositionY(), BULLET_SPEED * 2, Color.white));
			}

		}
	}


	/**
	 * Destroys a ship.
	 *
	 * @param destroyedShip
	 *            Ship to be destroyed.
	 */
	public final void destroy(final EnemyShip destroyedShip) {
		for (List<EnemyShip> column : this.enemyShips)
			for (int i = 0; i < column.size(); i++)
				if (column.get(i).equals(destroyedShip)) {
					column.get(i).destroy();
					this.logger.info("Destroyed ship in ("
							+ this.enemyShips.indexOf(column) + "," + i + ")");
				}

		// Updates the list of ships that can shoot the player.
		if (this.shooters.contains(destroyedShip)) {
			int destroyedShipIndex = this.shooters.indexOf(destroyedShip);
			int destroyedShipColumnIndex = -1;

			for (List<EnemyShip> column : this.enemyShips)
				if (column.contains(destroyedShip)) {
					destroyedShipColumnIndex = this.enemyShips.indexOf(column);
					break;
				}

			EnemyShip nextShooter = getNextShooter(this.enemyShips
					.get(destroyedShipColumnIndex));

			if (nextShooter != null)
				this.shooters.set(destroyedShipIndex, nextShooter);
			else {
				this.shooters.remove(destroyedShipIndex);
				this.logger.info("Shooters list reduced to "
						+ this.shooters.size() + " members.");
			}
		}

		this.shipCount--;
	}

	/**
	 * Gets the ship on a given column that will be in charge of shooting.
	 *
	 * @param column
	 *            Column to search.
	 * @return New shooter ship.
	 */
	public final EnemyShip getNextShooter(final List<EnemyShip> column) {
		Iterator<EnemyShip> iterator = column.iterator();
		EnemyShip nextShooter = null;
		while (iterator.hasNext()) {
			EnemyShip checkShip = iterator.next();
			if (checkShip != null && !checkShip.isDestroyed())
				nextShooter = checkShip;
		}

		return nextShooter;
	}

	/**
	 * Returns an iterator over the ships in the formation.
	 *
	 * @return Iterator over the enemy ships.
	 */
	@Override
	public final Iterator<EnemyShip> iterator() {
		Set<EnemyShip> enemyShipsList = new HashSet<EnemyShip>();

		for (List<EnemyShip> column : this.enemyShips)
			for (EnemyShip enemyShip : column)
				enemyShipsList.add(enemyShip);

		return enemyShipsList.iterator();
	}

	/**
	 * Checks if there are any ships remaining.
	 *
	 * @return True when all ships have been destroyed.
	 */
	public final boolean isEmpty() {
		return this.shipCount <= 0;
	}
}
