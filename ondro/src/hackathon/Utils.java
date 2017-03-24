package hackathon;

public class Utils {

	public static final int BOTTOM_LEFT = 1;
	public static final int BOTTOM_RIGHT = 2;
	public static final int TOP_RIGHT = 3;
	public static final int TOP_LEFT = 4;
	
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
}
