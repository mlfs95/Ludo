package regras;

import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ListIterator;
import java.util.Random;

import interfacejogo.Main;
import interfaces.ObservadoIF;
import interfaces.ObservadorIF;

public class Game implements ObservadoIF{
	
	private Pieces piecesclass;
	private GetByColor getByColor;
	private List<ObservadorIF> lst = new ArrayList<ObservadorIF>();
	
	private int currentPlayer;
	int GetCurrentPlayer(){return currentPlayer;}
    void SetCurrentPlayer(int current){
		currentPlayer = current;
		ListIterator<ObservadorIF> li = lst.listIterator();
		while(li.hasNext())
			li.next().notify(this);
	}
	
	private int currentState;
	int GetCurrentState(){return currentState;}
	void SetCurrentState(int current){
		currentState = current;
		ListIterator<ObservadorIF> li = lst.listIterator();
		while(li.hasNext())
			li.next().notify(this);
	}
	
	private boolean waitingForPlayer = false;
	boolean IsWaitingForPlayer(){ return waitingForPlayer; }
	void SetWaitingForPlayer(boolean waiting){
		waitingForPlayer = waiting;
	}
	
	private int diceValue;
	int GetDiceValue(){return diceValue;}
	void SetDiceValue(int value){
		diceValue = value;
		ListIterator<ObservadorIF> li = lst.listIterator();
		while(li.hasNext())
			li.next().notify(this);
	}
	
	private Random random = new Random();
	private int changeCurrentStateTo;
	private int valueNeededToLeaveStartPlace = 5;
	private int roundsPlayed = 0;
	
	private static Game instance = null;
	public static Game GetJogo(){
		if(instance == null)
			instance = new Game();
		return instance;
	}
	
	private Game(){
		piecesclass = Pieces.GetPieces();
		getByColor = GetByColor.GetGetByColor();
	}
	
	void StartGamePieces(){
		piecesclass.SetRedPieces(getByColor.getStartRed());
		piecesclass.SetBluePieces(getByColor.getStartBlue());
		piecesclass.SetGreenPieces(getByColor.getStartGreen());
		piecesclass.SetYellowPieces(getByColor.getStartYellow());
	}
		
	void StartNewRound(){
		SetCurrentState(6);
	}
	
	void StartGame(){
		StartGamePieces();
		SetCurrentPlayer(0);
		SetCurrentState(0);
		SetDiceValue(-1);
	}
	
	void RollDice(){
		for(int i= 1; i<7;i++)
			SetDiceValue(i);
		
		SetDiceValue(random.nextInt(6)+1);
				
		if(GetDiceValue() == 6 && !this.IsThereAPieceInStart(this.GetCurrentPlayersColor()))
		{	
			SetDiceValue(7);
			changeCurrentStateTo = 4;
		}
	}
	
	void MouseClicked(MouseEvent e) {
		if(IsWaitingForPlayer()){
			int[] pieceSelected = PieceSelected(e);
			if(pieceSelected != null){
				boolean moved = TryMoving(pieceSelected);
				if(moved){
					SetWaitingForPlayer(changeCurrentStateTo != 5 ? false : true);
					if(!IsWaitingForPlayer() )
						NextPlayer();
					else
						MovePiece();
					return;
				}
			}
			SetCurrentState(3);
			return;
		}
	}
		
	void MovePiece(){
		SetWaitingForPlayer(false);
		String color = GetCurrentPlayersColor();
		if(GetDiceValue() == 6 && this.roundsPlayed ==2 )
				ReturnLastPieceMovedToStart(color);
		else
			ChooseMovementAndMovePiece(color);
	}
		
	boolean IsInStart(int[] piece,String color) {
		int[][] startpieces = getByColor.GetStartPlacesByColor(color);
		for(int [] startpiece : startpieces)
		if(Arrays.equals(piece,startpiece))
			return true;
		return false;
	}
	
	boolean IsInEnd(int[] piece, String color) {
		int[] endplace = getByColor.GetCoordinatesOfEndPlaceByColor(color);
		if(Arrays.equals(piece,endplace))
			return true;
		return false;
	}
	
	private void NextPlayer(){
		try {
			Thread.sleep(800);
		} catch (InterruptedException e) {
		}
		
		changeCurrentStateTo = 0;
		if(GetDiceValue() == 6 && GetCurrentState() != 2 && roundsPlayed < 2) 
			roundsPlayed++;
		else{
			SetCurrentPlayer(GetCurrentPlayer() < 3 ? GetCurrentPlayer()+1 : 0);
			roundsPlayed =0;
		}
		SetDiceValue(0);
		SetCurrentState(0);
		GameFacade.GetJogoFacade().SetLancarDadoEnabled(true);
	}
		
	private int[] PieceSelected(MouseEvent e){
		int x = e.getX();
		int y = e.getY();
		int p = Main.ponto;
		String color = GetCurrentPlayersColor();
		int[][] pieces = piecesclass.GetEncodedPiecesFromColor(color);
		for(int[] piece : pieces )
		{
			int[] pieceboundary = new int[] {piece[0]*p + p, piece[1]*p + p};
			if( piece[0]*p < x && x < pieceboundary[0])
				if(piece[1]*p < y && y < pieceboundary[1])
					return piece;
		}
		return null;
	}
	
	private boolean TryMoving(int[] pieceSelected) {
		String color = GetCurrentPlayersColor();
		int decodedPiece;
		if(IsInStart(pieceSelected, color))
			decodedPiece = -1;
		else
			decodedPiece = piecesclass.Decode(pieceSelected);
		
		//START
		if(decodedPiece == -1 ){
			if(!AreThereTwoPiecesInStartHouse(color)
				&& GetDiceValue() == valueNeededToLeaveStartPlace){
				MovePieceFromStartPlaceToStartHouse(color);
				return true;
			}
			else
				return false;
		}
		
		//FINAL
		if(decodedPiece >=100) {// peça na reta final
			if(GetDiceValue() + decodedPiece == getByColor.GetFirstHouseOfFinalRouteByColor(color) + 5){
				MoveToEndPlace(decodedPiece, color);
				return true;
			}
			else if(GetDiceValue() + decodedPiece < getByColor.GetFirstHouseOfFinalRouteByColor(color) + 5){
				MovePieceFromCurrentToNew(color, decodedPiece, GetDiceValue() + decodedPiece);
				return true;
			}
			else 
				return false;
		}
		
		int finalHouse = getByColor.GetFinalHouseByColor(color);
		
		if(EntersFinalRoute(decodedPiece, color))//decodedPiece <= finalHouse) //Peça na casa final 
		{
			int housesToWalkInFinalRoute = GetDiceValue() - (finalHouse - decodedPiece);
			if(housesToWalkInFinalRoute > 6)
				return false;
			else if(housesToWalkInFinalRoute == 0)
				MovePieceFromCurrentToNew(color, decodedPiece, finalHouse);			
			else if(housesToWalkInFinalRoute == 6)
				MoveToEndPlace(decodedPiece,color);
			else{
				int firstofRoute = getByColor.GetFirstHouseOfFinalRouteByColor(color);
				int newhouse = firstofRoute + housesToWalkInFinalRoute - 1;
				MovePieceFromCurrentToNew(color, decodedPiece, newhouse);
			}
			return true;
		}
		
		
		//NORMAL
		int newHouse = decodedPiece + GetDiceValue() <= 52 ? decodedPiece + GetDiceValue() : decodedPiece + GetDiceValue() - 52;
		if(!IsBlocked(decodedPiece, color)){
			MovePieceFromCurrentToNew(color, decodedPiece, newHouse);
			boolean captured = CapturePieceIfThereIsAPieceFromAnotherColorInHouse(color, newHouse );
			if(captured)
			{
				SetDiceValue(20);
				SetCurrentState(5);
				changeCurrentStateTo = 5;
			}
			else
				changeCurrentStateTo = 0;
			return true;
		}
		return false;
	}
	
	private boolean EntersFinalRoute(int decodedPiece, String color) {
		int newHouse = decodedPiece + GetDiceValue() <= 52 ? decodedPiece + GetDiceValue() : decodedPiece + GetDiceValue() - 52;
		int finalHouse = getByColor.GetFinalHouseByColor(color);
		
		if(decodedPiece + GetDiceValue() <= 52){
			for(int i = decodedPiece; i < decodedPiece + GetDiceValue(); i++)
				if(i == finalHouse)
					return true;
		}
		else{
			for(int i = decodedPiece; i <= 52; i++)
				if(i == finalHouse)
					return true;
			for(int i= 1; i <=newHouse; i++)
				if(i == finalHouse)
					return true;
		}
		
		return false;
	}
	private void MoveToEndPlace(int decodedPiece, String color) {
		MovePieceFromCurrentToNew(color, decodedPiece, 0);
		if(AllPiecesAreEnded(color)){
			GameFacade.GetJogoFacade().EndGame();
		}
			
	}
	
	private boolean CapturePieceIfThereIsAPieceFromAnotherColorInHouse(String color, int piece) {
		if(IsInShelter(piece))
			return false;
		String [] otherColors = GetAllOtherColors(color);
		for(String othercolor : otherColors){
			int[] otherpieces = piecesclass.GetDecodedPiecesPlacesByColor(othercolor);
			for(int otherpiece : otherpieces)
				if(otherpiece == piece){
					this.MovePieceFromCurrentToNew(othercolor, otherpiece, -1);
					return true;
				}
		}
		return false;
	}
	
	private void ReturnLastPieceMovedToStart(String color) {
		int lastmoved = getByColor.GetLastPieceMovedByColor(color);
		if(lastmoved <100)
			this.MovePieceFromCurrentToNew(color, lastmoved, -1);
		NextPlayer();
	}
	
	private void ChooseMovementAndMovePiece(String color) {
		if(CanMoveAutomatically(color)){
			MovePieceFromStartPlaceToStartHouse(color);
			NextPlayer();
		}
		else if(CanMove(color))
			WaitForPlayerMovement(color);
		else{
			PlayerCantMove();
			NextPlayer();
		}
	}
	
	private boolean CanMoveAutomatically(String color){
		return AreAllPiecesInStart(color) &&
				!AreThereTwoPiecesInStartHouse(color)
				&& GetDiceValue() == valueNeededToLeaveStartPlace;
	}
	
	private void MovePieceFromStartPlaceToStartHouse(String color) {
			int startHouse = getByColor.GetStartHouseByColor(color);
			MovePieceFromCurrentToNew(color, -1, startHouse);
	}
	
	private boolean CanMove(String color) {
		if(IsThereAPieceInStart(color) && 
				GetDiceValue() == valueNeededToLeaveStartPlace &&
				!AreThereTwoPiecesInStartHouse(color))
			return true; //can move a piece from start place
		else if(IsThereAPieceInFinalRouteThatCanMove(color))
			return true; //can move a piece in final route
		else if(!AreAllPiecesInStart(color) && IsThereAPieceOnNormalHousesNotBlocked( color) )
			return true;// at least one piece on the board and not blocked by barrier or two pieces in destination house 
		else if(AnyPieceEntersFinalRouteAndCanMove(color))
			return true;
		return false;
	}

	private boolean AnyPieceEntersFinalRouteAndCanMove(String color) {
		int finalHouse = getByColor.GetFinalHouseByColor(color);
		int[] piecesPlaces = piecesclass.GetDecodedPiecesPlacesByColor(color);
		for(int piece : piecesPlaces){
			if(EntersFinalRoute(piece, color)){
				int housesToWalkInFinalRoute = GetDiceValue() - (finalHouse - piece);
				if(housesToWalkInFinalRoute <= 6)
					return true;
			}
		}
		return false;
		
	}
	private boolean IsThereAPieceInFinalRouteThatCanMove(String color) {
		int[] piecesPlaces = piecesclass.GetDecodedPiecesPlacesByColor(color);
		for(int piece : piecesPlaces){
			if(piece > 100)
				if(GetDiceValue() + piece <= getByColor.GetFirstHouseOfFinalRouteByColor(color) + 5)
					return true;
		}
		return false;
	}
	private boolean IsThereAPieceOnNormalHousesNotBlocked( String color) {
		int[] piecesPlaces = piecesclass.GetDecodedPiecesPlacesByColor(color);
		for(int piece : piecesPlaces){
			if(piece < 100 && piece != -1)
				if(!IsBlocked(piece, color))
					return true;
		}
		return false;
	}
	
    private boolean IsBlocked(int piece, String color) {
		int newHouse = piece + GetDiceValue() <= 52 ? piece + GetDiceValue() : piece + GetDiceValue() - 52;
		if(AlreadyTwoPiecesInDestinationHouse(color, newHouse))
			return true;
		else if(IsThereABarrier(color, piece, newHouse))
			return true;
		return false;
	}
	
    private boolean IsThereABarrier(String color, int piece, int newHouse){
    	int otherPieces[][] = GetAllOtherPieces(color);
		ArrayList<Integer> PlacesWherePiecesFormBarrier = new ArrayList<Integer>();
			for(int[] otherpiece : otherPieces){
				for (int j=0;j<otherpiece.length;j++)
				  for (int k=j+1;k<otherpiece.length;k++)
				    if (k!=j && otherpiece[k] == otherpiece[j] && otherpiece[j] != -1)
				    	PlacesWherePiecesFormBarrier.add(otherpiece[j]);
			}
			
			if(piece + GetDiceValue() < 100 ){ 
				for(int barrier : PlacesWherePiecesFormBarrier)
					if(piece + GetDiceValue() <= 52 ){
						if(piece < barrier && barrier <= piece+ GetDiceValue())
							return true;
					}
					else
						if((piece < barrier && barrier <= 52) || (52< barrier && barrier < newHouse))
							return true;
			}
			return false;
    }
    
	private boolean IsInShelter(int piece){
		return piece == 52 || piece == 13 || piece == 26 || piece == 39
				||  piece == 4|| piece == 17|| piece == 30|| piece == 43;
	}
	
	private int[][] GetAllOtherPieces(String color){
		String  otherColors[] = GetAllOtherColors(color);
		int count = 0;
		int  otherPieces[][] = new int[3][4];
		for(String coulor : otherColors){
			int pieces[] = piecesclass.GetDecodedPiecesPlacesByColor(coulor);	
			otherPieces[count++] = pieces;
		}
		return otherPieces;
	}
	
	private String[] GetAllOtherColors(String color){
		String  allcolors [] = new String[]{ "red", "blue", "green", "yellow"};
		String  otherColors[] = new String [3];
		
		int count = 0;
		for(String coulor : allcolors)
			if(!coulor.equals(color))
				otherColors[count++] = coulor;
		return otherColors;
	}
	
	String GetPoints(){
		String  allcolors [] = new String[]{ "red", "blue", "green", "yellow"};
	    int[] distssum = new int [4];
		int count2 =0;
	    for(String color : allcolors)
		{ 	
			int[] dists = new int[4];
			int [] pieces = piecesclass.GetDecodedPiecesPlacesByColor(color);
			int count = 0;
			for(int piece : pieces)
			{
				if(piece == 0)
					dists[count]= 0;
				else if(piece == -1)
					dists[count] = 52;
				else if(piece >100)
					dists[count] = 3;
				else
				{
					int walked = GetWalkedDistance(color, piece);
					dists[count] = 52 - walked;
				}

				count++;
			}
			int distsum=0;
			for(int dist : dists)
				distsum+=dist;
			distssum[count2++] = distsum;
		}
		String newstr =  "\n"+ "vermelho" + " pontuacao: " + distssum[0]+ "\n"+
				"azul"  + " pontuacao: " + distssum[1]+ "\n"+
				"verde"  + " pontuacao: " + distssum[2]+ "\n"+
				"amarelo"  + " pontuacao: " + distssum[3];
		
		return newstr;
	}
	
	private int GetWalkedDistance(String color, int piece) {
		int finalhouse = getByColor.GetFinalHouseByColor(color);
		int starthouse = getByColor.GetStartHouseByColor(color);
		switch(color )
		{
		case "red":
			return piece <= finalhouse ? 9 + piece : piece - starthouse;
		case "blue":
			return piece <= finalhouse ? 22 +piece : piece - starthouse;
		case "green":
			return piece <= finalhouse ? 52 + piece : piece -  starthouse;
		case "yellow":
			return piece <= finalhouse ? 13 + piece : piece - starthouse;
		}
		return 0;
	}
	private boolean AlreadyTwoPiecesInDestinationHouse(String color, int destination){
		if(IsInShelter(destination)){
			if(AlreadyTwoPiecesOfAnyColorInDestinationHouse(destination))
				return true;
		}
		else if(AlreadyTwoPiecesOfPlayerInDestinationHouse(color, destination))
			return true;
		return false;
	}
	
	private boolean AlreadyTwoPiecesOfAnyColorInDestinationHouse(int destination){
		int count =0;
		int[] allpieces = GetAllPieces();
		for(int piece : allpieces)
			if(piece == destination)
				count++;
		return count >= 2;
	}
	
	private boolean AlreadyTwoPiecesOfPlayerInDestinationHouse(String color, int destination){
		int [] pieces = piecesclass.GetDecodedPiecesPlacesByColor(color);
		int count =0;
		for(int piece : pieces)
			if(piece == destination)
				count ++;
		return count >= 2;
	}
	
	private void WaitForPlayerMovement(String color) {
		SetCurrentState(changeCurrentStateTo !=  0 ? changeCurrentStateTo : 1);
		SetWaitingForPlayer(true);
	}
	
	private void PlayerCantMove() {
		SetCurrentState(2);
	}
	
	private boolean AreThereTwoPiecesInStartHouse(String color) {
		int startHouse = getByColor.GetStartHouseByColor(color);
		int[] piecesPlaces = this.GetAllPieces();
		int count=0;
		for(int piece : piecesPlaces)
			if(piece == startHouse)
				count++;
		return count >= 2 ? true: false;
	}
	
	private boolean IsThereAPieceInStart(String color) {
		int[] piecesPlaces = piecesclass.GetDecodedPiecesPlacesByColor(color);
		for(int piece : piecesPlaces)
			if(piece == -1)
				return true;
		return false;
	}
	
	//-1 : start, 0 : final, 100 a 118 final route
	private void MovePieceFromCurrentToNew(String color, int currentPlace, int newPlace) {
		int[][] pieces = piecesclass.GetEncodedPiecesFromColor(color);
		int[][] newCoordinates = GetNewCoordinates(color, currentPlace, newPlace, pieces);
		getByColor.StoreLastPieceMoved(color, newPlace);
		piecesclass.MovePieces(newCoordinates, color);
	}
		
	private int[] GetAllPieces(){
		int [] red = piecesclass.GetDecodedPiecesPlacesByColor("red");
		int [] blue = piecesclass.GetDecodedPiecesPlacesByColor("blue");
		int [] yellow = piecesclass.GetDecodedPiecesPlacesByColor("yellow");
		int [] green = piecesclass.GetDecodedPiecesPlacesByColor("green");
		int [] all = new int [red.length + blue.length + yellow.length + green.length];
		int count =0;
		for(int[] pieces : new int [][]{red, blue, green, yellow }){
			for(int piece : pieces)
			{
				all[count++] = piece;
			}
		}
		return all;
	}
		
	private int[][] GetNewCoordinates(String color, int currentPlace, int newPlace, int[][] pieces) {
		int[][] newCoordinates;
		if(currentPlace == -1)
			newCoordinates = SetNewCoordinatesForPieceInStart(piecesclass.Encode(newPlace), pieces,color);
		else if(newPlace == -1)
			newCoordinates = SetNewCoordinatesForPieceMovingToStart(piecesclass.Encode(currentPlace), pieces, color);
		else if(newPlace == 0)
			newCoordinates = SetNewCoordinatesForPieceInEnd(piecesclass.Encode(currentPlace), pieces, color);
		else
			newCoordinates = SetNewCoordinates(piecesclass.Encode(currentPlace), piecesclass.Encode(newPlace),pieces);
		return newCoordinates;
	}
	
	private int[][] SetNewCoordinatesForPieceInEnd(int[] currentPlace, int[][] pieces, String color) {
		for(int i=0;i <pieces.length; i++)
			if(Arrays.equals(pieces[i],currentPlace)){
				pieces[i]= getByColor.GetCoordinatesOfEndPlaceByColor(color);
				break;
			}
		return pieces;
	}
			
	private int[][] SetNewCoordinatesForPieceInStart(int[] newPlace, int[][] pieces, String color) {
		for(int i=0;i <pieces.length; i++)
			if(IsInStart(pieces[i], color)){
				pieces[i]=newPlace;
				break;
			}
		return pieces;
	}

	private int[][] SetNewCoordinatesForPieceMovingToStart(int[] currentPlace, int[][] pieces, String color) {
		for(int i=0;i <pieces.length; i++)
			if(Arrays.equals(pieces[i], currentPlace)){
				pieces[i]=GetCoordinatesOfFirstStartPlaceWithoutAPiece(color,pieces);
				break;
			}
		return pieces;
	}
	
	private int[] GetCoordinatesOfFirstStartPlaceWithoutAPiece(String color, int[][] pieces) {
		int[][] start = getByColor.GetStartPlacesByColor(color);
		boolean flag;
		for(int j=0;j<start.length;j++){
			flag = true;
			for(int i=0;i <pieces.length; i++)
				if(Arrays.equals(start[j], pieces[i]))
					flag=false;
			if(flag)
				return start[j];
		}
		return null;
	}

	private int[][] SetNewCoordinates(int[] currentPlace, int[] newPlace,int[][] pieces) {
		for(int i=0;i <pieces.length; i++)
			if(Arrays.equals(pieces[i], currentPlace)){
				pieces[i]=newPlace;
				break;
			}
		return pieces;
	}
	
	private String GetCurrentPlayersColor() {
		switch(GetCurrentPlayer()){
		case 0:
			return "red";
		case 1:
			return "green";
		case 2:
			return "yellow";
		case 3:
			return "blue";
		}
		return null;
	}

	private boolean AreAllPiecesInStart(String color) {
		int [] piecesPlaces = piecesclass.GetDecodedPiecesPlacesByColor(color);
		for(int piece : piecesPlaces)
			if(piece != -1)
				return false;
		return true;
	}
	
	private boolean AllPiecesAreEnded(String color) {
		int [] piecesPlaces = piecesclass.GetDecodedPiecesPlacesByColor(color);
		for(int piece : piecesPlaces)
			if(piece != 0)
				return false;
		return true;
	}
	
	//OBSERVADO POR JOGOFACEDE
	//OBSERVADO PELO JOGOFACADE
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
		return 0;
	}
	
	
}