import javax.swing.JPanel;

public class ClientPanel extends JPanel {
	
	private static ClientPanel instance = null;
	
	public ClientPanel(){
		
	}
	
	public static ClientPanel getInstance(){
		if(instance == null){
			instance = new ClientPanel();
		}
		
		return instance;
	}

}
