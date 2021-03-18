package sockets.client;

/*
 *  Olivier CHARLES pour CNAM NSY014 - Mars 2021
 *  Objectif : comprehension communication pas socket
 *  
 *  DEVELOPPEMENT D'UN CLIENT :
 *  	- Connexion par socket sur un serveur en attente de connection
 *  	- Envoi des ordres sous forme de tampon d'octets
 *  		- message simple
 *  		- serialisation d'un objet
 *  	- Ferme la connection avec le serveur si l'ordre est Quit
 *  
 *  Nouvelles connaissances techniques :
 *  	- utilisation de Future dans la connection et l'écriture vers le serveur
 */

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
 

public class Client {
	
	public static final int PORT = 3883;
	public static final String SERVER_ADRESS = "localhost";
	
	public static void main(String[] args) throws IOException, InterruptedException, ExecutionException {
	     new Client().go();
	}
	
	public void go() throws IOException, InterruptedException, ExecutionException {
		// On ouvre un canal avec socket sur le client poste local
		AsynchronousSocketChannel client =
				AsynchronousSocketChannel.open();
		
		// On crée une adresse de connection du canal 
		InetSocketAddress adresseServeur = new InetSocketAddress( SERVER_ADRESS, PORT );
		
		// On tente connecter la socket client sur une socket en attente d'acceptation
		// sur le serveur. 
		
		Future<Void> future = client.connect( adresseServeur );
		// On attend que la connexion soit ok
		future.get();
 		
		// Ici la connexion est OK socket client et socket serveur sont connectees
		// Socket client <----------> Socket Serveur
		
		while(true) {
			
			
			// On lit au clavier le message sur l'entree standard
			// jusqu'a retour clavier
			System.out.print("Entrer un message (object pour transéférer un objet) :");
	        BufferedReader reader = new BufferedReader( 
	            new InputStreamReader(System.in));  
	        String messageClient = reader.readLine(); 
	  
	        ByteBuffer buffer = null;
	        if (messageClient.startsWith("object")) {
	        	Personne moi = new Personne("CHARLES","Olivier","000001");
		        ByteArrayOutputStream baos = new ByteArrayOutputStream();
	            ObjectOutputStream oos = new ObjectOutputStream(baos);
	            // On serialise l'objet personne dans un tableau d'octets
	            oos.writeObject( moi );
	            // On copie le tableau d'octets dans le buffer
	         	buffer = ByteBuffer.wrap(   baos.toByteArray() );		
	        }
	        else {
	        	buffer = ByteBuffer.wrap(  messageClient.getBytes());
	        }
            
	        // On ecrit le contenu du tampon sur la socket client qui l'envoie
			// vers la socket correspondante sur le serveur
			Future<Integer> result = client.write( buffer );
			
			// On attend que l'écriture soit ok
			// Risque de boucle infinie ?
			while( ! result.isDone() ) {}
			
			// L'écriture est ok, on vide le buffer
			buffer.clear();
			
			// On alloue &28 octets au tampon
			buffer = ByteBuffer.allocate(128);
			
			// On demande la lecture sur la socket
			// en attendant la réponse du serveur
			result = client.read(buffer);

			// On attend la réponse du serveur
			while (!result.isDone()) {
			}

			// On tronque le tampon à la taille des octets reçus
			buffer.flip();
			// On convertit le contenu du tampon en chaine
			String messageServeur = new String(buffer.array()).trim();
			// On affiche le message reçu du serveur
			System.out.println("Message du serveur : " + messageServeur);
			// Si le message est 
			if ( messageClient.equals( "Quit")) {
				// Si Quit on casse la boucle principale
				break;
			}
			
		} // Fin de boucle principale
		
		// On ferme la socket locale
		client.close();
	}
	
	
}
