package hackathon;

import robocode.*;

public class Movement {

	public static void goToClosestWall(Robot robot){
		double battleFieldWidth = robot.getBattleFieldWidth();
		double battleFieldHeight = robot.getBattleFieldHeight();
		double xRobot = robot.getX();
		double yRobot = robot.getY();
		
		double toXClosestDistance = yRobot > (battleFieldHeight / 2) ? (battleFieldHeight - yRobot) :  yRobot;
		double toYClosestDistance = xRobot > (battleFieldWidth / 2) ? (battleFieldWidth - xRobot) :  xRobot;
		
		double closestDistance = toXClosestDistance < toYClosestDistance ? toXClosestDistance : toYClosestDistance;
		
		boolean isCloserX = toXClosestDistance < toYClosestDistance ? true : false;
		
		int kvadrant = Utils.getQuadrant(xRobot, yRobot, battleFieldWidth, battleFieldHeight);
		
		switch(kvadrant){
			case Utils.BOTTOM_LEFT : {
				if(isCloserX){
					robot.turnRight(180 - robot.getHeading());					
				} else {
					robot.turnRight(270 - robot.getHeading());
				}
				break;
			}
			case Utils.BOTTOM_RIGHT : {
				if(isCloserX){
					robot.turnRight(180 - robot.getHeading());					
				} else {
					robot.turnRight(90 - robot.getHeading());
				}
				break;
			}
			case Utils.TOP_LEFT : {
				if(isCloserX){
					robot.turnRight(0 - robot.getHeading());					
				} else {
					robot.turnRight(270 - robot.getHeading());
				}
				break;
			}
			case Utils.TOP_RIGHT : {
				if(isCloserX){
					robot.turnRight(0 - robot.getHeading());					
				} else {
					robot.turnRight(90 - robot.getHeading());
				}
				break;
			}
		}
		robot.ahead(closestDistance);
	}

}
