import java.io.IOException;
import java.io.PrintStream;
import java.net.*;
import java.util.Scanner;

public class Client{

	public static void main(String[] args) throws IOException {
		Scanner input = new Scanner(System.in);
		Socket client = new Socket("localhost", 12345); 
		
		if (client.isConnected()){
			System.out.println("Conectado ao servidor com sucesso");
			
			// Ouvir do servidor
			threadMessages(client);
			
			PrintStream output = new PrintStream(client.getOutputStream());
			String msg = input.nextLine(); 

			// Enquanto não recebe a string "###" o chat continua
			while (msg.compareTo("###") != 0) {
				output.println(msg);
				msg = input.nextLine();
			}
			
			// Ao receber a string, o chat termina
			output.close();
			input.close();
			client.close();
			System.out.println("O cliente terminou de executar!");
			
		} else {
			
			System.out.println("Não se conectou com o servidor");
			input.close();
			client.close();
		}
		
		
	}
	
	static void threadMessages(Socket client) {
		
		// Se conectou, cria uma thread para ouvir do servidor
		Thread messageThread = new Thread() {
					
			@Override
			public void run() {
							
				try{
					Scanner message = new Scanner(client.getInputStream());
			        
					while (message.hasNextLine()) {
						System.out.println(message.nextLine());
					}
				} catch(Exception e) { 
					System.out.println("Erro ao ouvir servidor, desconectando");
					e.printStackTrace();
					try {
						client.close();
					} catch (IOException e1) {
						e1.printStackTrace();
					}
				}	
			}					
		};
		
		messageThread.start();
	}
}
