import java.io.IOException;
import java.io.PrintStream;
import java.net.*;
import java.util.Scanner;

public class Server{
	
	static int maxClients = 4;
	static Socket[] clients;
	static String[] nicknames;
	static int numberOfClients = 0;
	static boolean serverIsRunning = true;

	public static void main(String[] args) {
		ServerSocket server;
		try {
			server = new ServerSocket(12345);
		} catch (IOException e) {
			System.out.println("Não consegue abrir porta 12345");
			e.printStackTrace();
			return;
		} 
		System.out.println("Porta 12345 aberta!");
		
		clients = new Socket[maxClients];
		nicknames = new String[maxClients];
		
		threadAcceptClients(server);
	}
	
	static void threadAcceptClients(ServerSocket server){
		
		Thread acceptClients = new Thread(){
			
			@Override
			public void run() {

				while (serverIsRunning){
					
					try {
						if (numberOfClients <= 4){
							
							Socket newClient = server.accept();
							clients[numberOfClients] = newClient;
							numberOfClients += 1;
							System.out.println("Nova conexão com o cliente " + newClient.getInetAddress().getHostAddress());
							
							threadListenNewClient(newClient);
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
	
	static void threadListenNewClient(Socket newClient){
		
		Thread listenClient = new Thread(){
			
			@Override
			public void run(){
				
				Scanner scanner;
				
				while(serverIsRunning){
					String nickname = null;
					
					try {
						scanner = new Scanner(newClient.getInputStream());
						
						while (scanner.hasNextLine()) { 
							String message = scanner.nextLine();
							if (message.equals("###")){
								// terminar conexão
							} else if  (message.startsWith("Nickname ")){
								nickname = message.substring(9);
								PrintStream output = new PrintStream(newClient.getOutputStream());
								boolean nameIsAvaiable = true;
								
								if (numberOfClients-1 != 0) {
									for (int i = 0; i < numberOfClients-1; i++) {
										
										if (nicknames[i].compareTo(nickname) == 0) {
											output.println("Invalid Nickname");
											nameIsAvaiable = false;
											break;
										}
									}
								}
								
								if (nameIsAvaiable) {
									nicknames[numberOfClients-1] = nickname;
									output.println("Valid Nickname");
								}
								
							} else if (message.startsWith("Message ")) {
								sendsAllClientsMessage(message.substring(8), newClient, nickname);
							}
							
						}
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		};
		
		listenClient.start();
	}
	
	static void sendsAllClientsMessage(String message, Socket sender, String nickname) {

		System.out.println("sending to all clients");
		for (int i=0; i<numberOfClients; i++) {
			
			if (nickname.compareTo(nicknames[i]) != 0) {
				try {
					PrintStream output = new PrintStream(clients[i].getOutputStream());
					String fullMessage = nickname + " enviou: " + message;
					output.println(fullMessage);
				} catch (IOException e) {
					System.out.println("erro ao pegar outputStream");
					e.printStackTrace();
				}
			}
		}
	}
}