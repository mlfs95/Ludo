package regras;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Map.Entry;

import interfaces.ObservadoIF;
import interfaces.ObservadorIF;

public class Pieces implements ObservadoIF{

	private int[][] red;
	private int[][] blue;
	private int[][] green;
	private int[][] yellow;
	private Map<Integer, Integer[]> coordinatesDictionary;
	private List<ObservadorIF> lst = new ArrayList<ObservadorIF>();
	
	int[][] GetRedPieces(){return red;}
	int[][] GetBluePieces(){return blue;}
	int[][] GetGreenPieces(){return green;}
	int[][] GetYellowPieces(){return yellow;}
	
	void SetRedPieces(int[][] red){ 
		this.red = red;
		ListIterator<ObservadorIF> li = lst.listIterator();
		while(li.hasNext())
			li.next().notify(this);
	}
	void SetBluePieces(int[][] blue){ 
		this.blue = blue; 
		ListIterator<ObservadorIF> li = lst.listIterator();
		while(li.hasNext())
			li.next().notify(this);
	}
	void SetGreenPieces(int[][] green){ 
		this.green = green; 
		ListIterator<ObservadorIF> li = lst.listIterator();
		while(li.hasNext())
			li.next().notify(this);
	}
	void SetYellowPieces(int[][] yellow){ 
		this.yellow = yellow;
		ListIterator<ObservadorIF> li = lst.listIterator();
		while(li.hasNext())
			li.next().notify(this);
	}
	
	private static Pieces instance = null;
	public static Pieces GetPieces(){
		if(instance == null)
			instance = new Pieces();
		return instance;
	}
	
    Pieces(){
    	coordinatesDictionary = new HashMap<Integer, Integer[]>();
    	SetDictionary();
    }
	
    int [][][] GetAll(){
    	int[][][] all = new int[4][4][2];
    	all[0] = GetRedPieces();
    	all[1] = GetBluePieces();
    	all[2] = GetGreenPieces();
    	all[3] = GetYellowPieces();
    	return all;
    }
    
    public void SetAll(int[][][] allpieces) {
		SetRedPieces(allpieces[0]);
		SetBluePieces(allpieces[1]);
		SetGreenPieces(allpieces[2]);
		SetYellowPieces(allpieces[3]);		
	}
    
    int Decode(int[] place){
		 for (Entry<Integer, Integer[]> entry : coordinatesDictionary.entrySet()) 
		 {
			 Integer [] value = entry.getValue();
			 int[] toInt = new int[value.length];
			 int count = 0;
			 for(Integer v : value)
				 toInt[count++] = v.intValue();
		        if ( Arrays.equals(place,toInt)) 
		            return entry.getKey();
		 }
		 return 0;
	}
	
	int[] Encode(int place) {
		int temp []= new int[2];
		temp[0]=coordinatesDictionary.get(place)[0];
		temp[1]= coordinatesDictionary.get(place)[1];
		return temp;
	}
	
	int[] GetDecodedPiecesPlacesByColor(String color){
		Game jogo = Game.GetJogo();
		int [] decodedPieces=new int[4];
		int[][] encodedPieces = GetEncodedPiecesFromColor(color);
		int count=0;
		for(int[] piece :encodedPieces ){
			if(jogo.IsInStart(piece, color))
				decodedPieces[count] = -1;
			else if(jogo.IsInEnd(piece, color))
				decodedPieces[count] = 0;
			else
				decodedPieces[count] = Decode(piece);
			count++;
		}		
		return decodedPieces;
	}
	
	int[][] GetEncodedPiecesFromColor(String color) {
		switch(color){
		case "red":
			return GetRedPieces();
		case "blue":
			return GetBluePieces();
		case "green":
			return GetGreenPieces();
		case "yellow":
			return GetYellowPieces();
		}
		return null;
	}

	void MovePieces(int[][] newCoordinates,String color) {
		switch(color){
		case "red":
			SetRedPieces(newCoordinates);
			break;
		case "blue":
			SetBluePieces(newCoordinates);
			break;
		case "green":
			SetGreenPieces(newCoordinates);
			break;
		case "yellow":
			SetYellowPieces(newCoordinates);
			break;
		}
	}
	
	private void SetDictionary(){
		Integer temps[][] = new Integer[][]{
				new  Integer[]{6,0},new Integer[]{7,0}, new Integer[]{8,0}, new Integer[]{8,1}, new Integer[]{8,2} ,
				new  Integer[]{8,3},new Integer[]{8,4}, new Integer[]{8,5}, new Integer[]{9,6}, new Integer[]{10,6},
				new Integer[]{11,6},new Integer[]{12,6},new Integer[]{13,6},new Integer[]{14,6},new Integer[]{14,7},
				new Integer[]{14,8},new Integer[]{13,8},new Integer[]{12,8},new Integer[]{11,8},new Integer[]{10,8},
				new Integer[]{9,8},	new Integer[]{8,9},	new Integer[]{8,10},new Integer[]{8,11},new Integer[]{8,12},
				new Integer[]{8,13},new Integer[]{8,14},new Integer[]{7,14},new Integer[]{6,14},new Integer[]{6,13},
				new Integer[]{6,12},new Integer[]{6,11},new Integer[]{6,10},new Integer[]{6,9} ,new Integer[]{5,8} ,
				new Integer[]{4,8} ,new Integer[]{3,8} ,new Integer[]{2,8} ,new Integer[]{1,8} ,new Integer[]{0,8} ,
				new Integer[]{0,7} ,new Integer[]{0,6} ,new Integer[]{1,6} ,new Integer[]{2,6} ,new Integer[]{3,6} ,
				new Integer[]{4,6} ,new Integer[]{5,6} ,new Integer[]{6,5} ,new Integer[]{6,4} ,new Integer[]{6,3} ,
				new Integer[]{6,2} ,new Integer[]{6,1} 
		};
		int count = 1;
		for(Integer[] temp : temps)
			coordinatesDictionary.put(count++, temp);
		
		Integer finalroute[][] = new Integer[][]{
			new Integer[]{1,7},new Integer[]{2,7},new Integer[]{3,7},new Integer[]{4,7},new Integer[]{5,7},//red
			new Integer[]{7,13},new Integer[]{7,12},new Integer[]{7,11},new Integer[]{7,10},new Integer[]{7,9},//blue
			new Integer[]{7,1},new Integer[]{7,2},new Integer[]{7,3},new Integer[]{7,4},new Integer[]{7,5},//green
			new Integer[]{13,7},new Integer[]{12,7},new Integer[]{11,7},new Integer[]{10,7},new Integer[]{9,7},//yellow
		};
		
		count =100;
		for(Integer[] temp : finalroute)
			coordinatesDictionary.put(count++, temp);
	}
	
	//OBSERVADO POR JOGOFACADE
	@Override
	public void add(ObservadorIF observador) {
		lst.add(observador);
	}
	@Override
	public void remove(ObservadorIF observador) {
		lst.remove(observador);
	}
	@Override
	public Object get(int i) {
		// TODO Auto-generated method stub
		return null;
	}
	

	
	
	
}
