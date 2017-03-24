package hackathon;

import robocode.Robot;

public class Utils {

	public static final int BOTTOM_LEFT = 1;
	public static final int BOTTOM_RIGHT = 2;
	public static final int TOP_RIGHT = 3;
	public static final int TOP_LEFT = 4;
	
	public static final double ROBOT_MIN_ENERGY_BACKUP = 50;
	
	protected static int getQuadrant(double x, double y, double width, double height){
		if((x <= width / 2) && (y <= height / 2)){
			return BOTTOM_LEFT;
		} else if((x > width / 2) && (y <= height / 2)){
			return BOTTOM_RIGHT;
		} else if((x > width / 2) && (y > height / 2)){
			return TOP_RIGHT;
		} else 
			return TOP_LEFT;
	}
	
	protected static void fireOnTarget(){
		
	}
	
	protected static double getMaximumFirePower(Robot robot){
		double robotEnergy = robot.getEnergy();
		
		if(robotEnergy < ROBOT_MIN_ENERGY_BACKUP){
			return 1.0;
		} else{
			return (robotEnergy - ROBOT_MIN_ENERGY_BACKUP) / 5;
		}
	}
}
