import java.io.IOException;
import java.io.PrintStream;
import java.net.*;
import java.util.Scanner;

public class Client implements Runnable {

	public static void main(String[] args) throws IOException {
		Socket cli = new Socket("127.0.0.1", 12345); 
		System.out.println("O cliente se conectou ao servidor!");
		
		Client nclient = new Client();
		Thread client_thread = new Thread(nclient);
		client_thread.start();
		
		Scanner teclado = new Scanner(System.in);
		PrintStream saida = new PrintStream(cli.getOutputStream());
		String msg = teclado.nextLine(); 

		while (msg.compareTo("###")!=0) {
			saida.println(msg);
			msg = teclado.nextLine();
		}
		
		saida.close();
		teclado.close();
		cli.close();
		System.out.println("O cliente terminou de executar!");

	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		
	}

}
