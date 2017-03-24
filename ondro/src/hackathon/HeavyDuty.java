package hackathon;
import java.awt.Color;

import robocode.*;

public class HeavyDuty extends Robot
{
	public boolean rightDirection;
	boolean hitWallFlag;
	double lastBearingToWall;
	boolean fightingOtherRobotFlag;
	int fightIncrement;
	
	public void run() {
		
		// INITIALIZATION
		initRobot();
	
		turnGunRight(90);
		Movement.goToClosestWall(this);		
			
		//MAIN LOOP
		while(true) {
			//RIDING AROUND
			 if(hitWallFlag){
				hitWallFlag = false;
				if(rightDirection){
					turnRight(90 - lastBearingToWall);
					ahead(getBattleFieldWidth());
				} else {
					turnLeft(90 - lastBearingToWall);
					ahead(getBattleFieldWidth());
				}
			} 
			
			//FIGHTING
			else if(fightingOtherRobotFlag && fightIncrement < 4){
				fightIncrement++;
				if(rightDirection){
					turnGunLeft(45);
					turnGunRight(45);					
				} else{
					turnGunRight(45);
					turnGunLeft(45);
				}
				back(30);
				
				//ENDING FIGHT
				if(fightIncrement >= 3){
					fightIncrement = 0;
					fightingOtherRobotFlag = false;
					turnRight(180);
					Movement.goToClosestWall(this);
				}
			}
		}
	}
	
	@Override
	public void onHitRobot(HitRobotEvent event) {
		super.onHitRobot(event);
		stop(true);
		back(10);
		Movement.adjustGunToDefensePosition(this, rightDirection);
		
		fightingOtherRobotFlag = true;
		rightDirection = !rightDirection;
	}
	
	public void onHitWall(HitWallEvent event) {
		super.onHitWall(event);
		hitWallFlag = true;
		Movement.resetGunToAttackAngle(this, rightDirection);	
		cancelFight(event);
	}
	
	@Override
	public void onScannedRobot(ScannedRobotEvent event) {
		super.onScannedRobot(event);
		fire(Utils.getFirePower(this, event));
	}
	
	private void initRobot(){
		setColors(Color.red,Color.red,Color.red); // body,gun,radar
		
		rightDirection = true;
		hitWallFlag = false;
		lastBearingToWall = 0;
		fightingOtherRobotFlag = false;
		fightIncrement = 0;
	}
	
	private void cancelFight(HitWallEvent event){
		fightingOtherRobotFlag = false;	
		fightIncrement = 0;
		lastBearingToWall = event.getBearing();
	}
}
