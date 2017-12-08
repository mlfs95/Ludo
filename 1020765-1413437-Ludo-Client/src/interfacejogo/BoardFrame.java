package interfacejogo;
import java.awt.*;
import javax.swing.*;

import Client.ObserverGame;
import Client.Client;

public class BoardFrame extends JFrame implements ObserverGame {
	private static final long serialVersionUID = 1L;

	private int altura;
	private int largura;
	private Graphics2D g;
	public BoardPanel boardPanel;
	public ButtonsPanel buttonsPanel;
	
	public BoardFrame(){
		
		// adicionando na lista de observados
		Client.getInstance().addObserver(this);
		
		SetPanelTabuleiro();
		SetPanelBotoes();
		SetConfigurations();
	}
	
	private void SetPanelTabuleiro() {
		boardPanel = BoardPanel.GetBoardPanel();
		boardPanel.paintComponents(g);
		this.getContentPane().add(boardPanel);
	}
	
	private void SetPanelBotoes(){
		buttonsPanel = ButtonsPanel.GetButtonsPanel();
		this.getContentPane().add(buttonsPanel);
	}
	
	private void SetConfigurations() {
		SetDefautSizes();
		SetBoundsAndLayout();		
		SetTitleAndSize();
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setVisible(true);
		
		// Enviando para todos menos a mim mesmo algo
		System.out.println("Enviando a todos que comecei o jogo");
		Client.getInstance().sendMessage("Iniciei o jogo");
	}
	
	private void SetDefautSizes(){
		this.altura = Main.altura;
		this.largura = Main.largura;
	}
	
	private void SetBoundsAndLayout(){
		Toolkit tk=Toolkit.getDefaultToolkit();
		Dimension screenSize=tk.getScreenSize();
		int sl=screenSize.width, sa=screenSize.height;
		int x=sl/2-largura/2, y=sa/2-altura/2;
		this.setBounds(x,y,largura,altura);
		this.setLayout(null);
	}
	
	private void SetTitleAndSize(){
		this.setSize(largura, altura);
		this.setTitle("Ludo");
		this.setResizable(false);
	}

	@Override
	public void receivedPlay(String play) {
		System.out.println("Recebeu uma jogada de alguem: " + play);
	}
	
}
