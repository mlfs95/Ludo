package regras;

public class GetByColor {

	int[][] getStartRed(){return new int [][]{ new int[]{1,4}, new int[]{4,1},	new int[]{1,1},	new int[]{4,4}};}
	int[][] getStartBlue(){return new int [][]{ new int[]{1,10}, new int[]{1,13}, new int[]{4,10}, new int[]{4,13}};}
	int[][] getStartGreen(){return new int [][]{ new int[]{10,1}, new int[]{13,1}, new int[]{10,4}, new int[]{13,4}};}
	int[][] getStartYellow(){return new int [][]{ new int[]{13,10}, new int[]{10,13}, new int[]{10,10}, new int[]{13,13}};	}	
		
	private int[] getEndRed(){return new int[]{6,7};}
	private int[] getEndBlue(){return new int[]{7,8};}
	private int[] getEndGreen(){return new int[]{7,6};}
	private int[] getEndYellow(){return new int[]{8,7};}
		
	private int LastRedPieceMoved;
	private int LastBluePieceMoved;
	private int LastGreenPieceMoved;
	private int LastYellowPieceMoved;
	
	private static GetByColor instance = null;
	public static GetByColor GetGetByColor(){
		if(instance == null)
			instance = new GetByColor();
		return instance;
	}	
	
	int[][] GetStartPlacesByColor(String color) {
		switch(color){
		case "red":
			return getStartRed();
		case "blue":
			return getStartBlue();
		case "green":
			return getStartGreen();
		case "yellow":
			return getStartYellow();
		}
		return null;
	}
	
	int[] GetCoordinatesOfEndPlaceByColor(String color) {
		switch(color){
		case "red":
			return this.getEndRed();
		case "blue":
			return this.getEndBlue();
		case "green":
			return this.getEndGreen();
		case "yellow":
		return this.getEndYellow();
		}
		return null;
	}
	
	int GetStartHouseByColor(String color) {
		 int redStartPlace = 43;
		 int blueStartPlace = 30;
		 int greenStartPlace = 4;
		 int yellowStartPlace = 17;
		switch(color){
		case "red":
			return redStartPlace;
		case "blue":
			return blueStartPlace;
		case "green":
			return greenStartPlace;
		case  "yellow":
			return yellowStartPlace;
		}
		return 0;
	}
	
	int GetFinalHouseByColor(String color) {
		 int redFinalPlace = 41;
		 int blueFinalPlace = 28;
		 int greenFinalPlace = 2;
		 int yellowFinalPlace = 15;
		switch(color){
		case "red":
			return redFinalPlace;
		case "blue":
			return blueFinalPlace;
		case "green":
			return greenFinalPlace;
		case  "yellow":
			return yellowFinalPlace;
		}
		return 0;
	}
	
	int GetFirstHouseOfFinalRouteByColor(String color){
		int redFirstPlaceofFinalRoute = 100;
		 int blueFirstPlaceofFinalRoute = 105;
		 int greenFirstPlaceofFinalRoute = 110;
		 int yellowFirstPlaceofFinalRoute = 115;
		switch(color){
		case "red":
			return redFirstPlaceofFinalRoute;
		case "blue":
			return blueFirstPlaceofFinalRoute;
		case "green":
			return greenFirstPlaceofFinalRoute;
		case  "yellow":
			return yellowFirstPlaceofFinalRoute;
		}
		return 0;
	}
	
	int GetLastPieceMovedByColor(String color){
		switch(color){
		case "red":
			return LastRedPieceMoved;
		case "blue":
			return LastBluePieceMoved;
		case "green":
			return LastGreenPieceMoved;
		case "yellow":
			return LastYellowPieceMoved;
		}
		return 0;
	}
	
	void StoreLastPieceMoved(String color, int newPlace) {
		switch(color){
		case "red":
			LastRedPieceMoved = newPlace;
			break;
		case "blue":
			LastBluePieceMoved = newPlace;
			break;
		case "green":
			LastGreenPieceMoved = newPlace;
			break;
		case "yellow":
			LastYellowPieceMoved = newPlace;
			break;
		}
	}
	
}
