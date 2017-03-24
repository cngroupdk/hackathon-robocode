package hackathon;
import java.awt.Color;

import robocode.*;
// API help : http://robocode.sourceforge.net/docs/robocode/robocode/Robot.html

public class HeavyDuty extends Robot
{
	boolean hitWallFlag;
	double lastBearingToWall;
	
	boolean fightingOtherRobotFlag;
	//double lastBearingToRobotHit;
	
	boolean bulletMissedOtherRobotFlag;
	
	boolean enemySpottedThisRound;
	
	
	//todo sprav metodu launchFire podla vzdialenosti protivnika a svojej energie
	public void run() {
		
		// INITIALIZATION
		setColors(Color.red,Color.blue,Color.green); // body,gun,radar
		hitWallFlag = false;
		lastBearingToWall = 0;
		
		fightingOtherRobotFlag = false;
		//lastBearingToRobotHit = 0;

		bulletMissedOtherRobotFlag = false;
		enemySpottedThisRound = false;
		
		turnGunRight(90);
		Movement.goToClosestWall(this);		
		
		//MAIN LOOP
		while(true) {
			if(hitWallFlag && !fightingOtherRobotFlag){
				hitWallFlag = false;
				turnRight(90 - lastBearingToWall);
				ahead(getBattleFieldWidth());
			} 
			else if(fightingOtherRobotFlag){
				turnGunLeft(45);
				turnGunRight(45);
				back(30);
				//Utils.fireOnTarget();
//				fire(Utils.getMaximumFirePower(this));
				if(bulletMissedOtherRobotFlag || !enemySpottedThisRound){
					fightingOtherRobotFlag = false;
					turnGunRight(getHeading() - getGunHeading() + 90);
					Movement.goToClosestWall(this);
				}
				enemySpottedThisRound = false;
			}
		}
	}
	
	@Override
	public void onHitRobot(HitRobotEvent event) {
		super.onHitRobot(event);
		fightingOtherRobotFlag = true;
		//lastBearingToRobotHit = event.getBearing();
		stop(true);
		back(10);
		turnGunLeft(90 - event.getBearing() - 25);
	}
	
	public void onHitWall(HitWallEvent event) {
		super.onHitWall(event);
		hitWallFlag = true;
		lastBearingToWall = event.getBearing();
	}

	@Override
	public void onScannedRobot(ScannedRobotEvent event) {
		super.onScannedRobot(event);
		enemySpottedThisRound = true;
		fire(1);
		if(fightingOtherRobotFlag){
			fire(Utils.getMaximumFirePower(this));
		} else {
			fire(1.0);
		}

	}
	
	@Override
	public void onBulletHit(BulletHitEvent event) {
		super.onBulletHit(event);
	}
	
	@Override
	public void onHitByBullet(HitByBulletEvent event) {
		super.onHitByBullet(event);
	}
	
	@Override
	public void onBulletMissed(BulletMissedEvent event) {
		super.onBulletMissed(event);
		bulletMissedOtherRobotFlag = true;
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
