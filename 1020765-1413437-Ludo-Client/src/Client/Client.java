package Client;
import java.io.IOException;
import java.io.PrintStream;
import java.net.*;
import java.util.ArrayList;
import java.util.ListIterator;
import java.util.Scanner;

public class Client implements ObservableLobby, ObservableGame {

	// Instância do singleton
	private static Client instance = null;
	private static ArrayList<ObserverLobby> lst1 = new ArrayList<ObserverLobby>();
	private static ArrayList<ObserverGame> lst2 = new ArrayList<ObserverGame>();
	
	// Variavel da conexào
	private Socket socket;
	// Varíavel para enviar mensagens no socket 
	private PrintStream output;
	private Scanner input;
	
	private static boolean hasNickname = false;
	
	public static Client getInstance(){
		if(instance == null){
			instance = new Client();
		}
		return instance;
	}
	
	public void startClient() {
		// Se conecta com o servidor passando o ip e porta
		try {
			socket = new Socket("localhost", 12345);
		} catch (IOException e) {
			System.out.println("Não foi possivel se conectar em 'localhost' e porta 12345");
			return;
		} 
		// Variavel para pegar coisas escritas 
		input = new Scanner(System.in);
		
		// Se conectou corretamente
		if (socket.isConnected()) {
			
			// Pede um nome do cliente
//			System.out.println("Escolha um nome:");
			
			// Chama a função para ouvir o servidor
			threadMessages(socket);
			
			// Tenta inicializar o output com o socket conectado
			try {
				output = new PrintStream(socket.getOutputStream());
			} catch (IOException e) {
				System.out.println("Não foi possivel escrever no socket");
				return;
			}
			
			
			// ultima mensagem enviada
//			String msg = input.nextLine();
//			
//			// Enquanto não recebe a string "###" o chat continua
//			while (msg.compareTo("###") != 0) {
//				
//				// Se nao tiver setado um nickname ou não tiver sido aceito ele envia um nickname no próximo envio
//				if (!hasNickname) {
//					output.println("Nickname " + msg);
//				} else { // Se não ele considera o string enviado uma mensagem
//					output.println("Message " + msg);
//				}
//				
//				//Pega a próxima coisa escrita
//				msg = input.nextLine();
//			}
		}
	}
	
	private static void threadMessages(Socket server) {
		
		// Variável para ouvir do servidor
		Scanner scanner;
		try {
			scanner = new Scanner(server.getInputStream());
		} catch (IOException e2) {
			System.out.println("impossivel ouvir servidor");
			e2.printStackTrace();
			return;
		}
		
		// Se conectou, cria uma thread para ouvir do servidor
		Thread messageThread = new Thread() {
					
			@Override
			public void run() {
				
				try{
					// Enquanto tiver algo para ouvir
					while (scanner.hasNextLine()) {
						
						// Salva a mensagem escutada pelo socket
						String msg = scanner.nextLine();
						
						ListIterator<ObserverLobby> li = lst1.listIterator();
						
						// Se o servidor aprovar o nome
						if (msg.compareTo("Valid Nickname") == 0) {
							
							// Diz que tem um nickname
							hasNickname = true;
							System.out.println("Nome configurado corretamente");
							
							// aciona o observer
							while(li.hasNext()) {
								
								li.next().receivedNicknameAvaiable();
							}
							
						// Se já houver um nome como o que o cliente enviou espera outro resposta do servidor para um novo nome
						} else if (msg.compareTo("Invalid Nickname") == 0) {
							System.out.println("Nome já existente, digite outro:");
							
							while(li.hasNext()){
								
								li.next().receivedNicknameUnavaiable();
							}
							
						
						} else if (msg.contains("Game Start")) {
							System.out.println("Começando o jogo!");
							
							while(li.hasNext()){
								
								li.next().receivedGameStart();
							}
							
						// Se não, é uma jogada
						} else {
							System.out.println("Recebi uma jogada!");
							ListIterator<ObserverGame> listIt2 = lst2.listIterator();
							
							while (listIt2.hasNext()) {
								listIt2.next().receivedPlay(msg);
							}
						}
					}
				} catch(Exception e) {
					System.out.println("Erro ao ouvir servidor, desconectando");
					e.printStackTrace();
					try {
						server.close();
					} catch (IOException e1) {
						e1.printStackTrace();
					}
				}	
			}					
		};
		
		messageThread.start();
	}
	
	public void sendMessage(String message) {
		
		try {
			output = new PrintStream(socket.getOutputStream());
		} catch (IOException e) {
			System.out.println("Não foi possivel escrever no socket");
			return;
		}
		
		output.println(message);
	}
	
	public void closeClient() {

		System.out.println("O cliente terminou de executar!");
		
		output.close();
		input.close();
		try {
			socket.close();
		} catch (IOException e) {
			System.out.println("Impossível fechar socket");
		}
	}

	@Override
	public void addObserver(ObserverLobby o) {
		
		lst1.add(o);
	}

	@Override
	public void removeObserver(ObserverLobby o) { }

	@Override
	public void notifyObserver(ObserverLobby o) { }

	@Override
	public void addObserver(ObserverGame o) {
		
		lst2.add(o);
	}

	@Override
	public void removeObserver(ObserverGame o) { }

	@Override
	public void notifyObserver(ObserverGame o) { }
}









