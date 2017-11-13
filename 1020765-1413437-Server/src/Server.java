import java.io.IOException;
import java.net.*;
import java.util.Scanner;

public class Server implements Runnable{

	public static void main(String[] args) throws IOException {
		ServerSocket servidor = new ServerSocket(12345); 
		System.out.println("Porta 12345 aberta!");
		
		
		while(true){
		Socket cliente = servidor.accept(); 
		System.out.println("Nova conexão com o cliente " + cliente.getInetAddress().getHostAddress());
		
		Scanner in = new Scanner(cliente.getInputStream());
		while (in.hasNextLine()) { 

			System.out.println(in.nextLine());

		}
		in.close();
		servidor.close();
		cliente.close();
		System.out.println("O servidor terminou de executar!");
		}

	}

	@Override
	public void run() {
		
		
	}

}
