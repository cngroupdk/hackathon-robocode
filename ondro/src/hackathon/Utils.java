package hackathon;

import robocode.Robot;
import robocode.ScannedRobotEvent;

public class Utils {

	public static final int BOTTOM_LEFT = 1;
	public static final int BOTTOM_RIGHT = 2;
	public static final int TOP_RIGHT = 3;
	public static final int TOP_LEFT = 4;
	
	public static final double ROBOT_MINIMUM_ENERGY_BACKUP = 20;
	
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
	
	protected static double getFirePower(Robot robot, ScannedRobotEvent event){
		double robotEnergy = robot.getEnergy();
		double result;
		
		double eventDistance = event.getDistance();
		
		if(robotEnergy < ROBOT_MINIMUM_ENERGY_BACKUP){	//0 - 9
			return 1;
		} 
		if(event.getDistance()<30)
			return 10;
		if(event.getDistance()<100)
			return 3;
		if(event.getDistance()<500)
			return 2;
		return 1;
	}
}