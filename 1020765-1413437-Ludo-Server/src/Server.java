import java.io.IOException;
import java.io.PrintStream;
import java.net.*;
import java.util.Scanner;

public class Server{
	
	// Instância do singleton
	private static Server instance = null;
	
	// numero maximo de clientes
	private int maxClients = 4;
	// Numero máximo de Salas
	private int maxLobbys = 5;
	// array de sockets
	private Socket[][] clients;
	// nickname de cada socket
	private String[][] nicknames;
	// numero atual de clientes por lobby
	private int numberOfClientsInLobby[];
	// numero atual de lobbys
	private int numberOfLobbys = 0;
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
		
		// inicializando os arrays
		clients = new Socket[maxClients][maxLobbys];
		nicknames = new String[maxClients][maxLobbys];
		numberOfClientsInLobby = new int[maxLobbys];
		
		for(int i = 0; i<maxLobbys; i++){
			numberOfClientsInLobby[i] = 0;
		}
		
		// chamando a thread do servidor para aceitar novos clientes
		threadAcceptClients(server);
	}
	
	private void threadAcceptClients(ServerSocket server){
		
		Thread acceptClients = new Thread(){
			
			@Override
			public void run() {

				// Enquanto o servidor está rodando
				while (serverIsRunning){
					
					try {
						// Se for menor que 4 aceita qualquer cliente
						if (numberOfClientsInLobby[numberOfLobbys] < maxClients){
							
							// Aceita cliente
							Socket newClient = server.accept();
							// Adiciona no vetor de clientes
							clients[numberOfClientsInLobby[numberOfLobbys]][numberOfLobbys] = newClient;
							// Diz que o numero de clientes aumentou 1
							numberOfClientsInLobby[numberOfLobbys] += 1;
							System.out.println("Nova conexão com o cliente " + newClient.getInetAddress().getHostAddress());
							
							// Cria uma thread para ouvir o novo cliente
							threadListenNewClient(newClient, numberOfClientsInLobby[numberOfLobbys], numberOfLobbys);
							
							if (numberOfClientsInLobby[numberOfLobbys] == 4) {
								if (numberOfLobbys < 5) {
									numberOfLobbys += 1;
								}
							}
						}
					} catch (IOException e) {
						System.out.println("Servidor não conseguiu aceitar novo cliente");
						e.printStackTrace();
					}
				}
			}
		};
		
		acceptClients.run();
	}
	
	private void threadListenNewClient(Socket newClient, int clientPositionInLobby, int lobbyNumber){
		
		Thread listenClient = new Thread(){
			
			@Override
			public void run(){
				
				Scanner scanner;
				
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
							
							// Se for "###" termina a conexão
							if (message.equals("###")){
								// terminar conexão
								
							// Se começar com a string 'Nickname ' significa que está enviando um nickname
							} else if  (message.startsWith("Nickname ")){
								
								// pega só o nickname
								nickname = message.substring(9);
								
								// Variável para escrever para o socket do cliente
								PrintStream output = new PrintStream(newClient.getOutputStream());
								// Variável para conferir o nomes
								boolean nameIsAvaiable = true;
								
								// se não for o primeiro cliente
								if (numberOfClientsInLobby[lobbyNumber]-1 != 0) {
									
									// Para cada cliente
									for (int i = 0; i < numberOfClientsInLobby[lobbyNumber]-1; i++) {
										
										// Se o nickname é o mesmo que o enviado
										if (nicknames[i][lobbyNumber].compareTo(nickname) == 0) {
											// Envia mensagem de nickname inválido
											output.println("Invalid Nickname");
											// E seta a booleanacomo inválida
											nameIsAvaiable = false;
											break;
										}
									}
								}
								
								// Se o nome for válido
								if (nameIsAvaiable) {
									// adiciona no array de nicnkames
									nicknames[numberOfClientsInLobby[lobbyNumber]-1][lobbyNumber] = nickname;
									output.println("Valid Nickname");
									output.println("Bem-vindo " + nickname);
								}
							
							// Se a mensagem começa com a String "Message "
							} else if (message.startsWith("Message ")) {
								// Chama a função para enviar para todos os clientes
								sendsAllClientsMessage(message.substring(8), newClient, nickname, lobbyNumber);
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
	
	private void sendsAllClientsMessage(String message, Socket sender, String nickname, int lobbyNumber) {

		System.out.println("sending to all clients");
		
		// Para cada cliente
		for (int i=0; i<numberOfClientsInLobby[lobbyNumber]; i++) {
			
			// Se não for quem enviou
			if (nickname.compareTo(nicknames[i][lobbyNumber]) != 0) {
				try {
					
					// Cria a variável para escrever para p cliente atual
					PrintStream output = new PrintStream(clients[i][lobbyNumber].getOutputStream());
					// Cria a mensagem completa
					String fullMessage = nickname + " enviou: '" + message + "' Na sala " + (lobbyNumber+1);
					// Envia a mensagem completa
					output.println(fullMessage);
				} catch (IOException e) {
					System.out.println("Não consegue escrever para " + nicknames[i]);
					e.printStackTrace();
				}
			}
		}
	}
}