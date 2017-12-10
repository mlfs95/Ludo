import java.io.IOException;
import java.io.PrintStream;
import java.net.Socket;

public class Lobby {

	private Player players[];
	private int numberOfPlayers;
	private boolean isFull;
	private int turn = 0;
	
	public Lobby(){
		
		players = new Player[4];
		numberOfPlayers = 0;
		isFull = false;
	}
	
	public boolean getIsFull() {
		return isFull;
	}
	
	public int getNumberOfPlayers(){
		return numberOfPlayers;
	}
	
	public boolean addPlayer(String nickname, Socket socket) {
		
		if (numberOfPlayers == 3) {
			isFull = true;
		}
		
		if (numberOfPlayers != 0) {
			
			for (int i = 0; i < numberOfPlayers; i++) {
				
				if (players[i].getNickname().compareTo(nickname) == 0) {
					
					return false;
				}
			}
		}
		
		players[numberOfPlayers] = new Player(nickname, socket);
		numberOfPlayers += 1;
		
		return true;
	}
	
	public void startGame() {
		
		for (int i = 0; i<numberOfPlayers; i++){
			
			try {
				
				// Cria a variável para escrever para p cliente atual
				PrintStream output = new PrintStream(players[i].getSocket().getOutputStream());
				String message = "Game Start";
				// Cria a mensagem completa
				String fullMessage;
				
				if (turn == i) {
					fullMessage = "YourTurn " + message;
				} else {
					fullMessage = message;
				}
				
				// Envia a mensagem completa
				output.println(fullMessage);
			} catch (IOException e) {
				System.out.println("Não consegue escrever para " + players[i].getNickname());
				e.printStackTrace();
			}
		}
		
		turn += 1;
	}
	
	public void sendToAllPlayers(String nickname, String message) {

		for(int i = 0; i < numberOfPlayers; i++) {
			if(players[i].getNickname().compareTo(nickname) != 0) {
				try {
					
					// Cria a variável para escrever para p cliente atual
					PrintStream output = new PrintStream(players[i].getSocket().getOutputStream());
					// Cria a mensagem completa
					String fullMessage;
					
					if (turn == i) {
						fullMessage = "YourTurn " + message;
					} else {
						fullMessage = message;
					}
					
					// Envia a mensagem completa
					output.println(fullMessage);
				} catch (IOException e) {
					System.out.println("Não consegue escrever para " + players[i].getNickname());
					e.printStackTrace();
				}
			}
		}
		
		turn += 1;
		
		if (turn == 4) {
			turn = 0;
		}
	}
}
