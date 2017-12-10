import java.io.IOException;
import java.io.PrintStream;
import java.net.*;
import java.util.Scanner;

public class Server{
	
	// Instância do singleton
	private static Server instance = null;
	
	// lobby atual incompleto
	private Lobby currentLobby;
	// variável que diz se o cliente deve continuar rodando ou não
	private boolean serverIsRunning = true;
	
	public static Server getInstance(){
		if(instance == null){
			instance = new Server();
		}
		return instance;
	}
	
	public void runServer() {
		// Tenta abrir um servidor na porta 12345
		ServerSocket server;
		try {
			server = new ServerSocket(12345);
		} catch (IOException e) {
			System.out.println("Não consegue abrir porta 12345");
			e.printStackTrace();
			return;
		} 
		System.out.println("Porta 12345 aberta!");
		
		// inicializando um lobby 
		currentLobby = new Lobby();
		
		// chamando a thread do servidor para aceitar novos clientes
		threadAcceptClients(server);
	}
	
	private void threadAcceptClients(ServerSocket server) {
		
		Thread acceptClients = new Thread(){
			
			@Override
			public void run() {

				// Enquanto o servidor está rodando
				while (serverIsRunning){
					
					try {

							
						// Aceita cliente
						Socket newClient = server.accept();

						System.out.println("Nova conexão com o cliente " + newClient.getInetAddress().getHostAddress());
						
						// Cria uma thread para ouvir o novo cliente
						threadListenNewClient(newClient);
							
					} catch (IOException e) {
						System.out.println("Servidor não conseguiu aceitar novo cliente");
						e.printStackTrace();
					}
				}
			}
		};
		
		acceptClients.run();
	}
	
	private void threadListenNewClient(Socket newClient){
		
		Thread listenClient = new Thread(){
			
			@Override
			public void run(){
				
				Scanner scanner;
				
				if (currentLobby.getIsFull()) {
					System.out.println("criando lobby novo");
					currentLobby = new Lobby();
				}
				
				Lobby lobby = currentLobby;
				
				// Enquanto o servidor roda
				while(serverIsRunning){
					String nickname = null;
					
					try {
						// Variável para ouvir o novo cliente
						scanner = new Scanner(newClient.getInputStream());
						
						// Enquanto tem algo para ouvir
						while (scanner.hasNextLine()) { 
							// Pega a próxima mensagem
							String message = scanner.nextLine();
							
							System.out.println("recebi: " + message);
							
							// Se for "###" termina a conexão
							if (message.equals("###")){
								// terminar conexão
								
							// Se começar com a string 'Nickname ' significa que está enviando um nickname
							} else if  (message.startsWith("Nickname ")){
								
								// pega só o nickname
								nickname = message.substring(9);
								
								// Variável para escrever para o socket do cliente
								PrintStream output = new PrintStream(newClient.getOutputStream());
								
								if (lobby.addPlayer(nickname, newClient)) {
									output.println("Valid Nickname");
									
									if (lobby.getIsFull()) {
										System.out.println("Começando o jogo");
										lobby.startGame();
									}
								} else {
									// Envia mensagem de nickname inválido
									output.println("Invalid Nickname");
								}
							
							// Então ;e uma mensagem que deve ser repassada para todos os outros
							} else {
								// Chama a função para enviar para todos os clientes
								lobby.sendToAllPlayers(nickname, message);
							}
						}
					} catch (IOException e) {
						System.out.println("Servidor não conseguiu ouvir cliente");
						e.printStackTrace();
					}
				}
			}
		};
		
		listenClient.start();
	}
}