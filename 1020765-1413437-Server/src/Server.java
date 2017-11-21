import java.io.IOException;
import java.io.PrintStream;
import java.net.*;
import java.util.Scanner;

public class Server{
	
	static int maxClients = 4;
	static Socket[] clients;
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
							int id = numberOfClients;
							System.out.println("Nova conexão com o cliente " + newClient.getInetAddress().getHostAddress());
							
							threadListenNewClient(newClient, id);
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
	
	static void threadListenNewClient(Socket newClient, int id){
		
		Thread listenClient = new Thread(){
			
			@Override
			public void run(){
				
				Scanner scanner;
				
				while(serverIsRunning){
					
					try {
						scanner = new Scanner(newClient.getInputStream());
						
						while (scanner.hasNextLine()) { 
							String message = scanner.nextLine();
							
							if (message.equals("###")){
								// terminar conexão
							} else {
								System.out.println("Mensagem recebida enviando a todos");
								sendsAllClientsMessage(message, newClient, id);
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
	
	static void sendsAllClientsMessage(String message, Socket sender, int id) {
		
		for (int i=0; i<numberOfClients; i++) {
			
			if (!sender.equals(clients[i])) {
				
				try {
					PrintStream output = new PrintStream(clients[i].getOutputStream());
					String fullMessage = "Cliente "+ id + " enviou: " + message;
					output.println(fullMessage);
				} catch (IOException e) {
					System.out.println("erro ao pegar outputStream");
					e.printStackTrace();
				}
			}
		}
	}
}
