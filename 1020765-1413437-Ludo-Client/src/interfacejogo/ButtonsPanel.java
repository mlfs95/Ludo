package interfacejogo;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URL;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import regras.GameFacade;
import interfaces.*;

public class ButtonsPanel extends JPanel implements ObservadorIF {
	private static final long serialVersionUID = 1L;

	private int ponto;
	private int x0=15*ponto;
	private JButton novoJogo = new JButton("Novo Jogo");
	private JButton carregar = new JButton("Carregar Jogo");
	private JButton salvar = new JButton("Salvar");
	private JButton lancarDado = new JButton("Lançar Dado");
	private JLabel jogadorAtual = new JLabel();
	private JLabel dice = new JLabel();
	private JLabel estadoAtual = new JLabel();

	private GameFacade jogof;
	
	private static ButtonsPanel instance = null;
	public static ButtonsPanel GetButtonsPanel(){
		if(instance == null)
			instance = new ButtonsPanel();
		return instance;
	}
	
	private ButtonsPanel(){
		this.ponto = Main.ponto;
		jogof = GameFacade.GetJogoFacade();
		jogof.add(this);
		SetButtons();
		SetLabels();
		SetDice();
		SetCurrentPlayerLabel();
		StartListeners();
		SetConfigurations();
		
	}
		
	private URL GetDiceImageByNumber(int n){
		switch(n){
		case 1:	return getClass().getResource("/images/1.png");
		case 2:	return getClass().getResource("/images/2.png");
		case 3:	return getClass().getResource("/images/3.png");
		case 4:	return getClass().getResource("/images/4.png");
		case 5:	return getClass().getResource("/images/5.png");
		case 6:	return getClass().getResource("/images/6.png");
		case 7:	return getClass().getResource("/images/6.png");
		}
		return null;
	}
	
	private void SetButtons(){
		int i=0;
		for(JButton jb : new JButton []{ novoJogo, carregar, salvar}){
			jb.setSize(4*ponto, ponto);
			jb.setLocation(x0+ponto/2, i+ponto);
			this.add(jb);
			i+=3*ponto/2;
		}		
		lancarDado.setSize(4*ponto, ponto);
		lancarDado.setLocation(x0+ponto/2, 19*ponto/2);
		this.add(lancarDado);
		
		lancarDado.setEnabled(false);
		salvar.setEnabled(false);
	}
	
	private void SetLabels(){
		JLabel jl = new JLabel("À Jogar:", SwingConstants.CENTER);
		jl.setFont(new Font("Courier New",Font.BOLD,20) );
		jl.setSize(4*ponto, ponto);
		jl.setLocation(x0+ponto/2, 11*ponto/2);
		this.add(jl);
		
		estadoAtual.setHorizontalAlignment(JLabel.CENTER);
		estadoAtual.setVerticalAlignment(JLabel.TOP);
		estadoAtual.setFont(new Font("Courier New",Font.PLAIN,15) );
		estadoAtual.setSize(5*ponto, 3*ponto);
		estadoAtual.setLocation(x0, 22*ponto/2);
		this.add(estadoAtual);
	}

	private void SetDice(){
		dice.setLocation(x0+2*ponto,16*ponto/2);
		this.add(dice);	
	}
	
	private void SetCurrentPlayerLabel(){
		jogadorAtual = new JLabel();
		jogadorAtual.setHorizontalAlignment(JLabel.CENTER);
		jogadorAtual.setFont(new Font("Courier New",Font.BOLD,20) );
		jogadorAtual.setSize(4*ponto, ponto);
		jogadorAtual.setLocation(x0+ponto/2, 13*ponto/2);
		this.add(jogadorAtual);
	}
	
	private void StartListeners(){
		
		lancarDado.addActionListener( new ActionListener(){
			
			@Override
			public void actionPerformed(ActionEvent e)
			{
				Thread t = new Thread(new Runnable() { 
				public void run() { 			
					jogof.StartNewRound();
					jogof.RollDice();			
					jogof.MovePiece();					
				}});
				t.start();
			}
			
		});
		
		novoJogo.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				jogof.StartGame();
				salvar.setEnabled(true);
				revalidate();
				repaint();
			}
        });
		
		salvar.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				jogof.SaveGame();
			}
        });
		
		carregar.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				jogof.LoadGame();
				salvar.setEnabled(true);
				revalidate();
				repaint();
			}
        });
	}
	
	private void SetConfigurations() {
		this.setBackground(Color.lightGray);
		this.setLayout(null);
		this.setSize(5*ponto, 15*ponto);
		this.setLocation(15*ponto, 0);
		this.setVisible(true);
	}
	
	private void RefreshDice(int diceValue){
		if(diceValue<=7 && diceValue >0){
			dice.setIcon(new ImageIcon(GetDiceImageByNumber(diceValue)));
			try {
				Thread.sleep(90);
			} catch (InterruptedException e) {
			}
		}
		else
			dice.setIcon(new ImageIcon());
		dice.setSize(dice.getIcon().getIconWidth(),dice.getIcon().getIconHeight());
	}
	
	// OBSERVADOR DE JOGOFACADE
	@Override
	public void notify(ObservadoIF observado) {
		int diceValue = (int) observado.get(1);
		RefreshDice(diceValue);
		
		Color foreground = (Color) observado.get(2);
		jogadorAtual.setForeground(foreground);
		
		String currentPlayerText = (String) observado.get(3);
		jogadorAtual.setText(currentPlayerText);
		
		String currentStateText = (String) observado.get(4);
		estadoAtual.setText(currentStateText);
		
		boolean lancarDadoEnabled = (boolean )observado.get(5);
		lancarDado.setEnabled(lancarDadoEnabled);
		
		revalidate();
		repaint();
	}


}
