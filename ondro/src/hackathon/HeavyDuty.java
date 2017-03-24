package hackathon;
import java.awt.Color;

import robocode.*;
// API help : http://robocode.sourceforge.net/docs/robocode/robocode/Robot.html

public class HeavyDuty extends Robot
{
	boolean hitWallFlag;
	double lastBearingToWall;
	
	public void run() {
		
		// INITIALIZATION
		setColors(Color.red,Color.blue,Color.green); // body,gun,radar
		hitWallFlag = true;
		lastBearingToWall = 0;
		
		turnGunRight(90);
		Movement.goToClosestWall(this);		
		
		//MAIN LOOP
		while(true) {
			if(hitWallFlag){
				hitWallFlag = false;
				turnRight(90 - lastBearingToWall);
				ahead(getBattleFieldWidth());
			}
		}
	}
	
	@Override
	public void onHitRobot(HitRobotEvent event) {
		super.onHitRobot(event);
	}
	
	@Override
	public void onScannedRobot(ScannedRobotEvent event) {
		super.onScannedRobot(event);
		fire(1);
		
	}
	
	public void onHitWall(HitWallEvent event) {
		super.onHitWall(event);
		hitWallFlag = true;
		lastBearingToWall = event.getBearing();

	}
	
	@Override
	public void onBulletHit(BulletHitEvent event) {
		super.onBulletHit(event);
	}
	
	public void onHitByBullet(HitByBulletEvent event) {
		super.onHitByBullet(event);
	}
	
	
	
	
	
	
	
	//Provides robot's current state
	@Override
	public void onStatus(StatusEvent event) {
		super.onStatus(event);
		RobotStatus robotStatus = event.getStatus();
		robotStatus.getEnergy();
		robotStatus.getHeading();	//toto pouzi na vypocitanie pozicie v ramci okna
									//telo sa bude hybat organizovane, hlaven sa ale bude zameriavat na nepriatelov
		robotStatus.getEnergy();	//podla tohoto upravuj silu strely
		robotStatus.getGunHeat();	//podla tohoto upravuj silu strely
		
		robotStatus.getNumSentries();	//neviem co je sentry, potrebujes ale pocet robotov v arene, podla nich
										//uprav strategiu
										//menej ako 4, zameraj sa na konkretneho robota,
										//4 a viac, utekaj k stene a strielaj po najblizsom
		robotStatus.getOthers();		// -||-

		robotStatus.getRadarHeading();	//rozmysli si, ci chces otacat radar spolu s hlavnou, alebo nie
		robotStatus.getVelocity();
	}
	
	@Override
	public void onWin(WinEvent event) {
		super.onWin(event);
	}
}
