package kucera;
import robocode.*;
import java.awt.Color;
import java.util.Iterator;
import java.util.Random;
import java.util.stream.IntStream;

// API help : http://robocode.sourceforge.net/docs/robocode/robocode/Robot.html

/**
 * Kucera - a robot by (your name here)
 */
public class Kucera extends Robot
{

	private static final double ROBOT_CLOSE = 150;
	private int OFFSET;
	private Random rand;
	Iterator<Integer> dist;
	Iterator<Integer> angles;
	Iterator<Integer> borderAngles;
	int minDist = 70;
	int maxDist = 250;
	int borderTurn = 220;
	private ScannedRobotEvent lastScanned;

	/**
	 * run: Kucera's default behavior
	 */
	public void run() {
		OFFSET = (int) (getBattleFieldWidth() / 8.5);
		rand = new Random();
		dist = rand.ints(minDist, maxDist).iterator();
		angles = rand.ints(30, 60).iterator();
		borderAngles = rand.ints(140, 220).iterator();
		setColors(Color.green,Color.green,Color.green); // body,gun,radar

		// Robot main loop
		while(true) {
			basicRandomizedMovement();
		}
	}

	private void basicRandomizedMovement() {
		moveAhead();
		turnGunRight(360);

		if (rand.nextBoolean()) {
			turnRight(angles.next());
		} else {
			turnLeft(angles.next());
		}
		moveFromWall();

		//todo movement from the enemies
	}

	private void moveAhead() {
		int aheadDist = dist.next();
		if (isValidFinalPosition(aheadDist)) {
			ahead(aheadDist);
		} else {
			ahead(getMinimalMovableDistance());
		}
	}

	private void moveBack() {
		int aheadDist = dist.next();
		if (isValidFinalPosition((-1)*aheadDist)) {
			back(aheadDist);
		} else {
			back(getMinimalMovableDistance());
		}
	}

	private boolean isValidFinalPosition(int aheadDist) {
		if (countFinalXPos(aheadDist, getX(), getHeading()) > 0 && countFinalXPos(aheadDist, getX(), getHeading()) < getBattleFieldWidth()) {
			if (countFinalYPos(aheadDist, getY(), getHeading()) > 0 && countFinalYPos(aheadDist, getY(), getHeading()) < getBattleFieldHeight()) {
				return true;
			}
		}
		return false;
	}

	private int countFinalXPos(int dist, double x, double heading) {
		System.out.println(heading);
		double rad = Math.toRadians(heading);
		System.out.println(rad);
		return (int) (x + Math.sin(rad) / dist);
	}

	private int countFinalYPos(int dist, double y, double heading) {
		double rad = Math.toRadians(heading);
		return (int) (y + Math.cos(rad) / dist);
	}

	private void moveFromWall() {
		//pravy okraj
		if (getX() > getBattleFieldWidth() - OFFSET) {
			if (getHeading() < 180) {
				turnRight(borderAngles.next());
			}
		}
		//levy okraj
		if (getX() < OFFSET) {
			if (getHeading() > 180) {
				turnLeft(borderAngles.next());
			}
		}
		//horni okraj
		if (getY() > getBattleFieldHeight() - OFFSET) {
			if (getHeading() < 90 || getHeading() > 270) {
				turnRight(borderAngles.next());
			}
		}
		//dolni okraj
		if (getY() < OFFSET) {
			if (getHeading() > 90 && getHeading() < 270) {
				turnLeft(borderAngles.next());
			}
		}
	}

	/**
	 * onScannedRobot: What to do when you see another robot
	 */
	public void onScannedRobot(ScannedRobotEvent e) {
		//todo dont shoot if they are too far awyay
		//adjust shooting by velocity and heading
		this.lastScanned = e;
		if (e.getDistance() < ROBOT_CLOSE) {
			fire(2.5);
		} else {
			fire(1);
		}
	}

	/**
	 * onHitByBullet: What to do when you're hit by a bullet
	 */
	public void onHitByBullet(HitByBulletEvent e) {
		// Replace the next line with any behavior you would like
		if (rand.nextBoolean()) {
			turnRight(e.getBearing() + 50 + angles.next());
		} else {
			turnLeft(e.getBearing() + 50 + angles.next());
		}
		moveAhead();
	}
	
	/**
	 * onHitWall: What to do when you hit a wall
	 */
	public void onHitWall(HitWallEvent e) {
		// Replace the next line with any behavior you would like
		turnRight(180);
		moveAhead();
	}

	public double getMinimalMovableDistance() {
		return Math.min(getMinimalXDistance(), getMinimalYDistance());
	}

	public double getMinimalXDistance() {
		double dif;
		dif = Math.abs(getBattleFieldWidth() - getX());
		return dif / Math.sin(Math.toRadians(getHeading()));
	}

	public double getMinimalYDistance() {
		double dif;
		dif = Math.abs(getBattleFieldHeight() - getY());
		return dif / Math.cos(Math.toRadians(getHeading()));
	}
}
