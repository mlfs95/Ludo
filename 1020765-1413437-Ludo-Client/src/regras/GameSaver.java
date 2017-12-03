package regras;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class GameSaver {

	private Pieces pieces;
	private Game jogo;
	
	private static GameSaver instance = null;
	public static GameSaver GetGameSaver(){
		if(instance == null)
			instance = new GameSaver();
		return instance;
	}
	
	public void SaveGame(){
		pieces = Pieces.GetPieces();
		jogo = Game.GetJogo();
		
		FileWriter outputStream = null;
		try{
			outputStream = new FileWriter("savefile.txt");
			outputStream.write(jogo.GetCurrentPlayer());
			outputStream.write(jogo.GetCurrentState());
			outputStream.write(jogo.IsWaitingForPlayer() == true ? 1:0);
			outputStream.write(jogo.GetDiceValue());
			
			for(int [][] allpieces : pieces.GetAll()){
				for(int [] pieces : allpieces)
					for(int piece : pieces)
						outputStream.write(piece);
			}
		}catch(Exception e){
			System.exit(0);
		} finally {
			if(outputStream != null)
				try {
					outputStream.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					System.exit(1);
				}
		}
	}
	
	
	public void LoadGame(){
		pieces = Pieces.GetPieces();
		jogo = Game.GetJogo();
		
		FileReader inputStream = null;
		try{
			
			inputStream = new FileReader("savefile.txt");
			jogo.SetCurrentPlayer(inputStream.read());
			jogo.SetCurrentState(inputStream.read());
			jogo.SetWaitingForPlayer(inputStream.read() == 1);
			jogo.SetDiceValue(inputStream.read());
			int [][][] allpieces = new int [4][4][2];
			for(int i = 0; i< allpieces.length;i++){
				for(int j = 0; j< allpieces[i].length;j++)
					for(int k =0; k < allpieces[i][j].length;k++)
						allpieces[i][j][k] = inputStream.read();;
			}
			pieces.SetAll(allpieces);
		}catch(Exception e){
			System.exit(1);
		}finally{
			if(inputStream != null)
				try{
					inputStream.close();
				}catch(Exception e){
					System.exit(3);
				}
		}
		
	}
	
}
