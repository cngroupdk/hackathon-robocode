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
	private static final double ROBOT_FAR_AWAY = 800;
	private int OFFSET;
	private Random rand;
	Iterator<Integer> dist;
	Iterator<Integer> angles;
	Iterator<Integer> borderAngles;
	int minDist = 70;
	int maxDist = 250;
	int borderTurn = 220;
	private ScannedRobotEvent lastScanned = null;
	private double lastScannedX = 0;
	private double lastScannedY = 0;

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
		if (lastScanned == null) {
			turnGunRight(360);
		} else {
			turnGunToLastScanned();
		}

		turn();
		moveFromWall();
	}

	private void turnGunToLastScanned() {
		double angle = getAngleBetween(getX(), getY(), lastScannedX, lastScannedY);
		double currentHeading = getHeading();

		turnGunLeft(currentHeading - angle);
	}

	private double getAngleBetween(double x, double y, double lastScannedX, double lastScannedY) {
		double xDif = x - lastScannedX;
		double yDif = y - lastScannedY;

		double angle = Math.toDegrees(1.0 / Math.tan(xDif/yDif));
		return angle;
	}

	private void turn() {
		double offset = 0;
//		if (lastScanned != null) {
//			offset = lastScanned.getBearing() + 180;
//		}

		if (rand.nextBoolean()) {
			turnRight(angles.next() + offset);
		} else {
			turnLeft(angles.next() + offset);
		}
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

	private int countFinalXPos(double dist, double x, double heading) {
		double rad = Math.toRadians(heading);
		return (int) (x + Math.sin(rad) / dist);
	}

	private int countFinalYPos(double dist, double y, double heading) {
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
		this.lastScanned = e;
		this.lastScannedX = calculateCurrentX(getX(), getGunHeading(), e.getDistance());
		this.lastScannedY = calculateCurrentY(getY(), getGunHeading(), e.getDistance());
		if (e.getDistance() < ROBOT_CLOSE) {
			//adjustGunToRobot(e.getHeading(), e.getDistance(), e.getVelocity());
			fire(2.5);
		} else if (e.getDistance() > ROBOT_FAR_AWAY ) {
			//nothing
		} else {
			//adjust gun
			fire(1);
		}
	}

	private void adjustGunToRobot(double heading, double distance, double velocity) {
		double currentX = calculateCurrentX(getX(), getHeading(), distance);
		double currentY = calculateCurrentY(getY(), getHeading(), distance);

		//todo
		double estimatedDistance = Math.abs(velocity) + 1;

		double finalX = countFinalXPos(estimatedDistance, currentX, heading);
		double finalY = countFinalYPos(estimatedDistance, currentY, heading);

		double newDistance = calculateDistanceTo(finalX, finalY);

		double gunMovement = calculateGunMovement(distance, estimatedDistance, newDistance);
		System.out.println(gunMovement);
		turnGunRight(gunMovement);
	}

	private double calculateGunMovement(double x, double y, double z) {
		System.out.println(x);
		System.out.println(y);
		System.out.println(z);

		return Math.acos((x*x+z*z-y*y)/(2*x*z));
	}

	private double calculateDistanceTo(double finalX, double finalY) {
		double protilehla = Math.abs(getX() - finalX);
		double prilehla = Math.abs(getY() - finalY);

		return Math.sqrt(protilehla*protilehla + prilehla*prilehla);
	}

	private double calculateCurrentX(double x, double heading, double distance) {
		return countFinalXPos(distance, x, heading);
	}

	private double calculateCurrentY(double y, double heading, double distance) {
		return countFinalYPos(distance, y, heading);
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
