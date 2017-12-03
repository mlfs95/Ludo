package regras;

import java.awt.Color;
import java.awt.event.MouseEvent;
import java.util.*;

import javax.swing.JOptionPane;

import interfaces.*;

public class GameFacade implements ObservadoIF, ObservadorIF{

	Game jogo;
	GameSaver gameSaver;
	Pieces pieces;
	private List<ObservadorIF> lst = new ArrayList<ObservadorIF>();
	boolean lancarDadoEnabled;
	
	private static GameFacade instance = null;
	public static GameFacade GetJogoFacade(){
		if(instance == null)
			instance = new GameFacade();
		return instance;
	}	
	
	GameFacade(){
		jogo = Game.GetJogo();
		gameSaver = GameSaver.GetGameSaver();
		pieces = Pieces.GetPieces();
		
		jogo.add(this);
		pieces.add(this);
	}
	
	public void StartNewRound(){
		SetLancarDadoEnabled(false);
		jogo.StartNewRound();
	}
	
	public void SetLancarDadoEnabled(boolean trueorfalse){
		lancarDadoEnabled = trueorfalse;
		ListIterator<ObservadorIF> li = lst.listIterator();
		while(li.hasNext())
		li.next().notify(this);
	}
	
	public void StartGame(){
		SetLancarDadoEnabled(true);
		jogo.StartGame();
	}
	
	public void RollDice(){
		jogo.RollDice();
	}
	
	public void MovePiece(){
		jogo.MovePiece();
	}
	
	public int GetDiceValue(){
		return jogo.GetDiceValue();
	}
	
	public void MouseClicked(MouseEvent e){
		jogo.MouseClicked(e);
	}
	
	public Color GetCurrentPlayerForeground(){
		switch (jogo.GetCurrentPlayer()){
			case 0:	return new Color(220,20,60);
			case 1:	return new Color (60,179,113);
			case 2:	return new Color(255,215,0);
			case 3:	return new Color(100,149,237);
		}return null;
	}
	
	public String GetCurrentPlayerText(){
		switch (jogo.GetCurrentPlayer()){
			case 0:	return "Vermelho";
			case 1: return "Verde";
			case 2: return "Amarelo";
			case 3:	return "Azul";
		}	return null;
	}
	
	public String GetCurrentStateText(){
		switch (jogo.GetCurrentState()){
			case 6: return "";
			case 0:	return "<html>Esperando<br> lançamento.</html>";
			case 1:	return "<html>Escolha jogada<br> clicando na<br> peça desejada.</html>";
			case 2:	return "<html>Não há<br> jogadas<br> possíveis.</html>";
			case 3:	return "<html> Essa Jogada<br> não  é<br> possível, <br>escolha outra.";
			case 4:	return "<html> Ande 7 casas<br> (Dado = 6 e<br> não existem<br> peças na casa<br> inicial).</html>";
			case 5:	return "<html> Peça comida.<br> Ande 20<br> casas.</html>";
		}return null;
	}
	
	public void SaveGame(){
		gameSaver.SaveGame();
		JOptionPane.showMessageDialog(null, "Jogo Salvo");
	}

	public void LoadGame(){
		gameSaver.LoadGame();
		JOptionPane.showMessageDialog(null, "Jogo Carregado");
	}
	
	//OBSERVADO PELO BUTTONS PANEL E BOARDPANEL
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
		if(i == 1)
			return jogo.GetDiceValue();
		if(i == 2)
			return GetCurrentPlayerForeground();
		if(i == 3)
			return GetCurrentPlayerText();
		if(i == 4)
			return GetCurrentStateText();
		if(i == 5)
			return lancarDadoEnabled;
		if(i == 6)
			return pieces.GetAll();
		return 0;
	}
	
	void EndGame(){
		JOptionPane.showMessageDialog(null, "Fim de Jogo\n Ganhador: " + GetCurrentPlayerText()+
				"\n" + jogo.GetPoints());
		System.exit(1);
	}


	//OBSERVADOR DE JOGO E PIECES
	@Override
	public void notify(ObservadoIF observado) {
		ListIterator<ObservadorIF> li = lst.listIterator();
		while(li.hasNext())
			li.next().notify(this);
	}
}
