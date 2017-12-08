package Client;

public class main {

	public static void main(String[] args) {
		
		ClientFrame screen = ClientFrame.getInstance();
		screen.setSize(300,300);
		screen.setVisible(true);

		Client client = Client.getInstance();
		
		client.startClient();
	
	}

}
