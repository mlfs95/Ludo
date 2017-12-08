package Client;
import interfacejogo.BoardFrame;

public class ClientConnection {
	
	private static ClientConnection instance = null;
	
	public ClientConnection(){
		
		
		new BoardFrame();
		
	}
	
	public void loadGame(){
		
	}
	
	public static ClientConnection getInstance(){
		if(instance == null){
			instance = new ClientConnection();
		}
		
		return instance;
	}

}
