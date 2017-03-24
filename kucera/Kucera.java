package kucera;
import robocode.*;
import java.awt.Color;
import java.util.Iterator;
import java.util.Random;

import static robocode.util.Utils.normalRelativeAngleDegrees;

// API help : http://robocode.sourceforge.net/docs/robocode/robocode/Robot.html

/**
 * Kucera - a robot by (your name here)
 */
public class Kucera extends Robot
{

	private static final double ROBOT_CLOSE = 150;
	private static final double ROBOT_FAR_AWAY = 600;
	private static final double ROBOT_CLOSER = 100;
	private int OFFSET;
	private Random rand;
	Iterator<Integer> dist;
	Iterator<Integer> angles;
	Iterator<Integer> borderAngles;
	int minDist = 100;
	int maxDist = 200;
	int borderTurn = 220;
	private ScannedRobotEvent lastScanned = null;
	private double lastScannedX = 0;
	private double lastScannedY = 0;
	private HitByBulletEvent lastHitBy;
	private long lastHitByTime = 0;
	private long lastScannedTime = 0;

	/**
	 * run: Kucera's default behavior
	 */
	public void run() {
		lastHitBy = null;
		lastScanned = null;
		OFFSET = (int) (getBattleFieldWidth() / 8.5);
		rand = new Random();
		dist = rand.ints(minDist, maxDist).iterator();
		angles = rand.ints(30, 60).iterator();
		borderAngles = rand.ints(140, 220).iterator();
		setColors(Color.green,Color.green,Color.red); // body,gun,radar

		setAdjustGunForRobotTurn(true);

		// Robot main loop
		while(true) {
			if (lastHitByTime < getTime() - 100) {
				lastHitBy = null;
			}
			if (lastScannedTime < getTime() - 100) {
				lastScanned = null;
			}
			basicRandomizedMovement();
		}
	}

	@Override
	public void onBulletHit(BulletHitEvent event) {
//		turnGunToAngle(40, event.getBullet().getHeading());
	}

	private void basicRandomizedMovement() {
		if (lastHitBy == null) {
			moveAheadRandomized();
			performScan();
			turnRandomized();
		} else {
			//recently hit
			moveAhead();
			turnRandomized();
			performScan();
		}
		moveFromWall();
	}

	private void performScan() {
//		if (lastScanned != null) {
//			turnGunToLastScanned();
//		} else {
			turnGunRight(360);
//		}
	}

	private void turnGunToLastScanned() {
		turnGunToWithOffset(lastScannedX, lastScannedY, 30);
	}

	private void turnGunToWithOffset(double x, double y, int offset) {
		double difX = getX() - x;
		double difY = getY() - y;

		double angle = Math.toDegrees(Math.atan(difY/difX));
		turnGunToAngle(offset, angle);
	}

	private void turnGunToAngle(int offset, double angle) {
		double heading = getGunHeading();
		double res = angle - heading;

		if (res < 0) {
			turnGunLeft(Math.abs(res) + offset);
		} else {
			turnGunRight(Math.abs(res) + offset);
		}
	}

	private void moveAheadRandomized() {
		int aheadDist = dist.next();
		if (isValidFinalPosition(aheadDist)) {
			ahead(aheadDist);
		} else {
			ahead(getMinimalMovableDistance(false));
		}
	}

	private void moveAhead() {
		ahead(getMinimalMovableDistance(false) / 3);
	}

	private void moveBack() {
		int aheadDist = dist.next();
		if (isValidFinalPosition((-1)*aheadDist)) {
			back(aheadDist);
		} else {
			back(getMinimalMovableDistance(true));
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
		this.lastScannedTime = getTime();
		this.lastScannedX = calculateCurrentX(getX(), getGunHeading(), e.getDistance());
		this.lastScannedY = calculateCurrentY(getY(), getGunHeading(), e.getDistance());



		if (e.getDistance() < ROBOT_CLOSER) {
			double gunTurnAmt = normalRelativeAngleDegrees(e.getBearing() + (getHeading() - getRadarHeading()));
			turnGunRight(gunTurnAmt);
			fire(3);
		} else if (e.getDistance() < ROBOT_CLOSE) {
			//adjustGunToRobot(e.getHeading(), e.getDistance(), e.getVelocity());
			double gunTurnAmt = normalRelativeAngleDegrees(e.getBearing() + (getHeading() - getRadarHeading()));
			turnGunRight(gunTurnAmt);
			fire(2);
		} else if (e.getDistance() > ROBOT_FAR_AWAY ) {
			//nothing
			moveAheadRandomized();
			return;
		} else {
			double gunTurnAmt = normalRelativeAngleDegrees(e.getBearing() + (getHeading() - getRadarHeading()));
			turnGunRight(gunTurnAmt);
			fire(1);
		}
		moveAheadRandomized();
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
		this.lastHitBy = e;
		this.lastHitByTime = getTime();
		if (rand.nextBoolean()) {
			turnRight(e.getBearing() + 50 + angles.next());
		} else {
			turnLeft(e.getBearing() + 50 + angles.next());
		}
		moveAheadRandomized();
	}

	@Override
	public void onHitRobot(HitRobotEvent event) {
		moveBack();
	}

	/**
	 * onHitWall: What to do when you hit a wall
	 */
	public void onHitWall(HitWallEvent e) {
		// Replace the next line with any behavior you would like
		turnRight(180);
		moveAheadRandomized();
	}

	public double getMinimalMovableDistance(boolean back) {
		return Math.min(getMinimalXDistance(back), getMinimalYDistance(back));
	}

	public double getMinimalXDistance(boolean back) {
		double dif;
		dif = Math.abs(getBattleFieldWidth() - getX());
		if (back)
			return dif / Math.sin(Math.toRadians(getHeading() + 180));
		else
			return dif / Math.sin(Math.toRadians(getHeading()));

	}

	public double getMinimalYDistance(boolean back) {
		double dif;
		dif = Math.abs(getBattleFieldHeight() - getY());
		if (back)
			return dif / Math.sin(Math.toRadians(getHeading() + 180));
		else
			return dif / Math.sin(Math.toRadians(getHeading()));
	}


	private double getAngleBetween(double x, double y, double lastScannedX, double lastScannedY) {
		double xDif = x - lastScannedX;
		double yDif = y - lastScannedY;

		double angle = Math.toDegrees(1.0 / Math.tan(xDif/yDif));
		return angle;
	}

	private void turnRandomized() {
		double offset = 0;

		if (rand.nextBoolean()) {
			turnRight(angles.next() + offset);
		} else {
			turnLeft(angles.next() + offset);
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

		return Math.acos((x*x+z*z-y*y)/(2*x*z));
	}

	private double calculateDistanceTo(double finalX, double finalY) {
		double protilehla = Math.abs(getX() - finalX);
		double prilehla = Math.abs(getY() - finalY);

		return Math.sqrt(protilehla*protilehla + prilehla*prilehla);
	}
}
