

import java.io.IOException;
import java.io.PrintStream;
import java.net.*;
import java.util.Scanner;

public class Client{

	private static boolean hasNickname = false;
	
	public static void main(String[] args) throws IOException {
		Socket server = new Socket("localhost", 12345); 
		Scanner input = new Scanner(System.in);
		
		if (server.isConnected()) {
			System.out.println("Escolha um nome:");
			
			// Ouvir do servidor
			threadMessages(server);
			
			PrintStream output = new PrintStream(server.getOutputStream());
			String msg = input.nextLine();
			
			// Enquanto não recebe a string "###" o chat continua
			while (msg.compareTo("###") != 0) {
				if (!hasNickname) {
					output.println("Nickname " + msg);
				} else {
					output.println("Message " + msg);
				}
			
				msg = input.nextLine();
			}
			
//			// Ao receber a string, o chat termina
			output.close();
			input.close();
			server.close();
			System.out.println("O cliente terminou de executar!");
			
		} else {
			
			System.out.println("Não se conectou com o servidor");
//			input.close();
			server.close();
		}
		
		
	}
	
	static void threadMessages(Socket server) {
		
		
		Scanner scanner;
		try {
			scanner = new Scanner(server.getInputStream());
		} catch (IOException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
			return;
		}
		
		// Se conectou, cria uma thread para ouvir do servidor
		Thread messageThread = new Thread() {
					
			@Override
			public void run() {
							
				try{
					while (scanner.hasNextLine()) {
						String msg = scanner.nextLine();
						if (msg.compareTo("Valid Nickname") == 0) {
							hasNickname = true;
							System.out.println("Nome configurado corretamente");
						} else if (msg.compareTo("Invalid Nickname") == 0) {
							System.out.println("Nome já existente");
						} else {
							System.out.println(msg);
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
}
