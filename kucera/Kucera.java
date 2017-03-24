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

	private int OFFSET;
	private Random rand;
	Iterator<Integer> dist;
	Iterator<Integer> angles;
	int minDist = 70;
	int maxDist = 200;
	int borderTurn = 220;

	/**
	 * run: Kucera's default behavior
	 */
	public void run() {
		OFFSET = (int) (getBattleFieldWidth() / 8.5);
		rand = new Random();
		dist = rand.ints(minDist, maxDist).iterator();
		angles = rand.ints(30, 60).iterator();
		setColors(Color.green,Color.green,Color.green); // body,gun,radar

		// Robot main loop
		while(true) {
			basicRandomizedMovement();
		}
	}

	private void basicRandomizedMovement() {
		int aheadDist = dist.next();
		if (countFinalXPos(aheadDist, getX(), getHeading()) > 0 && countFinalXPos(aheadDist, getX(), getHeading()) < getBattleFieldWidth()) {
			if (countFinalYPos(aheadDist, getY(), getHeading()) > 0 && countFinalYPos(aheadDist, getY(), getHeading()) < getBattleFieldHeight()) {
				ahead(dist.next());
			}
		}

		if (dist.next() > minDist + ((maxDist - minDist) /2)) {
			turnRight(angles.next());
		} else {
			turnLeft(angles.next());
		}

		moveFromWall();
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
				turnRight(borderTurn);
			}
		}
		//levy okraj
		if (getX() < OFFSET) {
			if (getHeading() > 180) {
				turnLeft(borderTurn);
			}
		}
		//horni okraj
		if (getY() > getBattleFieldHeight() - OFFSET) {
			if (getHeading() < 90 || getHeading() > 270) {
				turnRight(borderTurn);
			}
		}
		//dolni okraj
		if (getY() < OFFSET) {
			if (getHeading() > 90 && getHeading() < 270) {
				turnLeft(borderTurn);
			}
		}
	}

	/**
	 * onScannedRobot: What to do when you see another robot
	 */
	public void onScannedRobot(ScannedRobotEvent e) {
		// Replace the next line with any behavior you would like
		fire(1);
	}

	/**
	 * onHitByBullet: What to do when you're hit by a bullet
	 */
	public void onHitByBullet(HitByBulletEvent e) {
		// Replace the next line with any behavior you would like
		back(20);
		turnRight(angles.next());
		ahead(dist.next());
	}
	
	/**
	 * onHitWall: What to do when you hit a wall
	 */
	public void onHitWall(HitWallEvent e) {
		// Replace the next line with any behavior you would like
		back(dist.next());
	}	
}
