import robocode.*;
import robocode.Robot;
import robocode.util.Utils;

import java.awt.*;
import java.awt.geom.Point2D;
import java.util.Random;

public class MichalBric extends Robot {

    private static final Color BLACK_900 = Color.decode("#212121");
    private boolean roundEnded = false;

    // game stuff
    private Enemy enemy = new Enemy();
    private int scanDirection = 1;
    private int moveDirection = 1;
    private Random random = new Random();
    private int playersRemaining = 8;


    @Override
    public void run() {
        setColors(BLACK_900, BLACK_900, BLACK_900, Color.RED, Color.WHITE);
        setAdjustRadarForRobotTurn(true);
        setAdjustGunForRobotTurn(true);
        setAdjustRadarForGunTurn(true);

        long lastMoved = 0;
        while (!roundEnded) {
            turnRadarRight(360 * scanDirection);
            if (getTime() - lastMoved > 35) {
                drive();
                lastMoved = getTime();
            }
        }
    }

    private void drive() {
        // only turn enough to fit into single turn
        turnRight(enemy.bearing + 90);
        if (random.nextBoolean()) {

            moveDirection *= -1;
        }
        ahead(150 * moveDirection);
    }

    @Override
    public void onHitWall(HitWallEvent event) {
        ahead(250 * moveDirection * -1);
        turnRadarRight(360);
    }

    @Override
    public void onScannedRobot(final ScannedRobotEvent event) {
        // targeting crawler since I should hit it every time
        if (enemy.notSelected() || event.getName().equals("HeavyDuty") || event.getName().equals("sample.Walls") || event.getDistance() < (enemy.distance - 50)
                || event.getName().equals(enemy.name)) {
            enemy.update(event);
        }
        adjustRadar();
    }

    private void adjustRadar() {
        scanDirection *= -1;
        double turn = getHeading() + enemy.bearing - getRadarHeading();
        turnRadarRight(Utils.normalRelativeAngle(turn));
        aim();
    }

    private void aim() {
        // iterative linear targeting
        // the math formula from wiki: http://robowiki.net/wiki/Linear_Targeting
        double bulletPower = Math.min(enemy.distance < 100 ? 3.0 : 2.0, getEnergy());

        if (enemy.distance < 250 && enemy.velocity == 0) {
            bulletPower = 3;
        }

        double myX = getX();
        double myY = getY();
        double absoluteBearing = rads(getHeading()) + rads(enemy.bearing);
        double enemyX = getX() + enemy.distance * Math.sin(absoluteBearing);
        double enemyY = getY() + enemy.distance * Math.cos(absoluteBearing);
        double enemyHeading = rads(enemy.heading);
        double enemyVelocity = 9;
        if (enemy.velocity == 0) {
            enemyVelocity = 0.3;
        }


        double deltaTime = 0;
        double battleFieldHeight = getBattleFieldHeight(),
                battleFieldWidth = getBattleFieldWidth();
        double predictedX = enemyX, predictedY = enemyY;
        while((++deltaTime) * (20.0 - 3.0 * bulletPower) <
                Point2D.Double.distance(myX, myY, predictedX, predictedY)){
            predictedX += Math.sin(enemyHeading) * enemyVelocity;
            predictedY += Math.cos(enemyHeading) * enemyVelocity;
            if(	predictedX < 18.0
                    || predictedY < 18.0
                    || predictedX > battleFieldWidth - 18.0
                    || predictedY > battleFieldHeight - 18.0){
                predictedX = Math.min(Math.max(18.0, predictedX),
                        battleFieldWidth - 18.0);
                predictedY = Math.min(Math.max(18.0, predictedY),
                        battleFieldHeight - 18.0);
                break;
            }
        }
        double theta = Utils.normalAbsoluteAngle(Math.atan2(
                predictedX - getX(), predictedY - getY()));

        turnRadarRight(degs(Utils.normalRelativeAngle(absoluteBearing - rads(getRadarHeading()))));
        double gunTurnDegs = degs(Utils.normalRelativeAngle(theta - rads(getGunHeading())));
        turnGunRight(gunTurnDegs);
        fire(bulletPower);
        if (Math.abs(gunTurnDegs) < 10) {
            shoot(bulletPower);
        }
    }

    @Override
    public void onHitRobot(HitRobotEvent event) {
        if (!event.getName().equals(enemy.name)) {
            enemy.reset();
            enemy.name = event.getName();
            turnRadarRight(360);
        }
    }

    private void shoot(double bulletPower) {
        if (getGunHeat() == 0) {
            if ((playersRemaining > 3 && enemy.distance < 600) || enemy.distance < 400) {
                fire(bulletPower);
            }
        }
    }

    private double headingTobearing(double angle) {
        while (angle > 180) angle -= 360;
        while (angle < -180) angle += 360;
        return angle;
    }

    private double degs(double radians) {
        return Math.toDegrees(radians);
    }

    private double rads(double degrees) {
        return Math.toRadians(degrees);
    }

    @Override
    public void onRoundEnded(final RoundEndedEvent event) {
        roundEnded = true;
    }

    @Override
    public void onRobotDeath(RobotDeathEvent event) {
        if (event.getName().equals(enemy.name)) {
            enemy.reset();
        }
        playersRemaining--;
        // maybe some deicison based on how many robots are left?
    }

    // used for storing so that it does not get overwritten on every scan event
    private static class Enemy {
        private double bearing;
        private double heading;
        private double distance;
        private double velocity;
        private double energy;
        private double x;
        private double y;
        private String name;


        private void update(final ScannedRobotEvent e) {
            this.bearing = e.getBearing();
            this.heading = e.getHeading();
            this.energy = e.getEnergy();
            this.distance = e.getDistance();
            this.velocity = e.getVelocity();
            this.energy = e.getEnergy();
            this.name = e.getName();
        }

        private void reset() {
            this.name = "";
        }

        private boolean notSelected() {
            return name == null || "".equals(this.name);
        }
    }
}
