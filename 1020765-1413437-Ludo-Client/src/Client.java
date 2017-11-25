

import java.io.IOException;
import java.io.PrintStream;
import java.net.*;
import java.util.Scanner;

public class Client{

	private static boolean hasNickname = false;
	
	public static void main(String[] args) {
		// Se conecta com o servidor passando o ip e porta
		Socket server;
		try {
			server = new Socket("localhost", 12345);
		} catch (IOException e) {
			System.out.println("Não foi possivel se conectar em 'localhost' e porta 12345");
			return;
		} 
		// Variavel para pegar coisas escritas 
		Scanner input = new Scanner(System.in);
		
		// Se conectou corretamente
		if (server.isConnected()) {
			// Pede um nome do cliente
			System.out.println("Escolha um nome:");
			
			// Chama a função para ouvir o servidor
			threadMessages(server);
			
			// Varíavel para enviar mensagens no socket 
			PrintStream output;
			try {
				output = new PrintStream(server.getOutputStream());
			} catch (IOException e) {
				System.out.println("Não foi possivel escrever no socket");
				return;
			}
			// ultima mensagem enviada
			String msg = input.nextLine();
			
			// Enquanto não recebe a string "###" o chat continua
			while (msg.compareTo("###") != 0) {
				
				// Se nao tiver setado um nickname ou não tiver sido aceito ele envia um nickname no próximo envio
				if (!hasNickname) {
					output.println("Nickname " + msg);
				} else { // Se não ele considera o string enviado uma mensagem
					output.println("Message " + msg);
				}
				
				//Pega a próxima coisa escrita
				msg = input.nextLine();
			}

			System.out.println("O cliente terminou de executar!");
			
			// Ao receber a string "###", o chat termina e fecha os sockets
			output.close();
			input.close();
			try {
				server.close();
			} catch (IOException e) {
				System.out.println("Impossível fechar socket");
			}
		}
	}
	
	static void threadMessages(Socket server) {
		
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
						
						// Se o servidor aprovar o nome
						if (msg.compareTo("Valid Nickname") == 0) {
							
							// Diz que tem um nickname
							hasNickname = true;
							System.out.println("Nome configurado corretamente");
							
						// Se já houver um nome como o que o cliente enviou espera outro resposta do servidor para um novo nome
						} else if (msg.compareTo("Invalid Nickname") == 0) {
							System.out.println("Nome já existente, digite outro:");
							
						// Se não, é uma mensagem
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
