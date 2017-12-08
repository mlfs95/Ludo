package Client;
import java.awt.*;
import java.awt.event.*;

import javax.swing.*;

import interfacejogo.BoardFrame;

public class ClientFrame extends JFrame implements ActionListener, ObserverLobby {
	
	private static ClientFrame instance = null;
	private JLabel l1;
	private JButton b1;
	private JTextField t1;
	private Container screen;
	private String currentNickname;
	private boolean ValidNickname = true;
	
	public ClientFrame(String s){
		super(s);
		screen = getContentPane();
		screen.setLayout(null);
		
		l1 = new JLabel("Insira nickname: ");
		l1.setBounds(20,70,100,30);
		t1 = new JTextField();
		t1.setBounds(130,70,100,30);
		b1 = new JButton("Ok");
		b1.setBounds(90,130,80,80);
		
		screen.add(l1);
		screen.add(t1);
		screen.add(b1);
		
		
		t1.addActionListener(this);
		b1.addActionListener(this);
		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		Client.getInstance().addObserver(this);
	}
	
	
	public static ClientFrame getInstance(){
		if(instance == null){
			instance = new ClientFrame("LUDO");
		}
		
		return instance;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getSource() == b1){
			
			currentNickname = t1.getText();
			Client.getInstance().sendMessage("Nickname " + currentNickname);
			b1.setEnabled(false);
			
//			if(ValidNickname){
//				ClientConnection.getInstance();
//				dispose();
//			}
		}
		else{
			System.exit(1);
		}
		
	}

	@Override
	public void receivedNicknameAvaiable() {
		t1.hide();
		l1.setSize(100, 30);
		l1.setText("Bem-vindo: " + currentNickname);
	}


	@Override
	public void receivedNicknameUnavaiable() {
		b1.setEnabled(true);
	}


	@Override
	public void receivedGameStart() {
		ClientConnection.getInstance();
		dispose();
	}

}
