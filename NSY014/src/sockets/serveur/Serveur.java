package sockets.serveur;

/*
 *  Olivier CHARLES pour CNAM NSY014 - Mars 2021
 *  Objectif : comprehension communication pas socket
 *  
 *  DEVELOPPEMENT D'UN SERVEUR MONO CLIENT :
 *  	- Connexion par socket avec un client qui se connecte
 *  	- Reception et Envoi des ordres sous forme de tampon d'octets
 *  		- message simple
 *  	- Ferme la connection avec le client si le message reçu est Quit
 *  
 *  Nouvelles connaissances techniques :
 *  	- Lecture et ecriture d'une réponse vers le client
 */

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class Serveur {
	
	public static final int PORT = 3883;
	public static final String SERVER_ADRESS = "localhost";
	
	private boolean isActive = true;
	
	public static void main(String[] args) throws IOException, InterruptedException, ExecutionException {
		new Serveur().go();
		
	}
	private void go() throws IOException, InterruptedException, ExecutionException {
		
		// On crée et un serveur asynchrone et on ouvre un canal sur une socket
		AsynchronousServerSocketChannel serverSocket =
				AsynchronousServerSocketChannel.open();
		
		// On lie une adresse avec un port au serveur asynchrone
		InetSocketAddress adresseServeur = new InetSocketAddress("localhost",3883);
		serverSocket.bind( adresseServeur );
		
		
		
		System.out.println("Serveur en écoute sur le port: "+adresseServeur.getPort());
		System.out.println("Attente de connexion de clients ...");
		
		// Un future qui donne comme resultat la socket vers le client
		Future<AsynchronousSocketChannel> acceptResult = serverSocket.accept();
		// On attend la connexion d'un client, on récupère un canal de communication
		// vers ce client
		AsynchronousSocketChannel clientSocketChannel = acceptResult.get();
		
		System.out.println("Messages du client : " );
		if (( clientSocketChannel != null ) && (clientSocketChannel.isOpen()) ) {
			while( isActive ) {
				ByteBuffer buffer = ByteBuffer.allocate(128);
				// On lit sur le canal du client en reception en utilisant 
				// une future
				Future<Integer> result = clientSocketChannel.read( buffer );
				
				// Tant qu'on a rien reçu
				while( !result.isDone()) {}
				
				buffer.flip();
				String message = new String( buffer.array()).trim();
				System.out.println( message );
				
				// On renvoie un accusé de reception au client
				String messagePourServeur = "Ok bien reçu !";
				buffer = ByteBuffer.wrap( messagePourServeur.getBytes());
				clientSocketChannel.write( buffer );
				
			
				if ( message.equals("Quit")) {
					isActive = false;
					buffer.clear();
				}
			} // Fin de boucle principal
			
			// On ferme le canal vers le client
			clientSocketChannel.close();
			// On ferme le serveur
			serverSocket.close();
			System.out.println("Serveur deconnecté");
		}
	}
}
