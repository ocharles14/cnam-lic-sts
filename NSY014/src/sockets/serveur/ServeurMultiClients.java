package sockets.serveur;

/*
 *  Olivier CHARLES pour CNAM NSY014 - Mars 2021
 *  Objectif : comprehension communication pas socket
 *  
 *  DEVELOPPEMENT D'UN SERVEUR MULTI CLIENTS :
 *  	- Gestion d'une liste de threads gérant les connexions
 *        par socket avec les clients
 *  	- Reception et Envoi des ordres sous forme de tampon d'octets
 *  		- message simple
 *  		- deserialisation d'un objet
 *  	- Ferme la connection avec le client si le message reçu est Quit
 *  
 *  Nouvelles connaissances techniques :
 *  	- Lecture et ecriture d'une réponse vers le client
 *      - Pool (liste) de thread de connexions
 *      - Deserialisation   octets -> objet
 *      - Synchronisation
 */

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import sockets.client.Personne;

public class ServeurMultiClients {

	private static   Integer nbClients = 0;
	private static Boolean isActive = true;
	private static AsynchronousServerSocketChannel serverChannel;
	
	// Le serveur maintient une liste de threads de communication 
	// par socket vers les clients
	private List<ConnectionClient> connexions = new ArrayList<ConnectionClient>();

	public static void main(String[] args) throws IOException, InterruptedException, ExecutionException {
		new ServeurMultiClients().go();

	}

	private void go() throws IOException, InterruptedException, ExecutionException {

		serverChannel = AsynchronousServerSocketChannel.open();
		InetSocketAddress adresseServeur = new InetSocketAddress("localhost", 3883);
		serverChannel.bind(adresseServeur);
		System.out.println("Serveur en écoute sur le port: " + adresseServeur.getPort());
		System.out.println("Attente de connexion des clients ...");

		while ( isActive ) {
			Future<AsynchronousSocketChannel> acceptResult = serverChannel.accept();
			AsynchronousSocketChannel clientChannel=null;
			try {
			 // On attend qu'un client se connecte
			 // On récupère le canal vers ce client
			 clientChannel = acceptResult.get();
			} catch( ExecutionException asc) {
				isActive = false;
			}
			if ( clientChannel != null ) {
				// Incrementation du nombre de client
				nbClients++;
				// On crée un thread avec le canal vers client obtenu
				ConnectionClient cc = new ConnectionClient( clientChannel, nbClients);
				// On rajoute de thread de connexion dans la liste
				connexions.add( cc );
				// On démarre le thread de communication vers le client
				cc.start();
			}
		
		}
		
		// On ferme toutes les connections
		for ( ConnectionClient cc  : connexions ) {
			if ( cc.getClientChannel().isOpen() ) {
				cc.getClientChannel().close();
			}
		}
		serverChannel.close();
		System.out.println("Arrêt du serveur ...");
	}

	
	// Une classe qui hérite de Thread
	// Un thread par connection avec un client
	public class ConnectionClient extends Thread {
		
		int numClient = 0;
		AsynchronousSocketChannel clientChannel = null;
		boolean isClientActive = true;
		
		public AsynchronousSocketChannel getClientChannel() {
			return clientChannel;
		}

		public ConnectionClient( AsynchronousSocketChannel clientChannel , int numClient ) {
			this.clientChannel = clientChannel;
			this.numClient = numClient;
		}
		
		@Override
		public void run() {
			if ((clientChannel != null) && (clientChannel.isOpen())) {
				while (isClientActive) {
					ByteBuffer buffer = ByteBuffer.allocate(128);
					Future<Integer> result = clientChannel.read(buffer);
					while (!result.isDone()) {}
					if ( result.isCancelled() ) {
				    	isClientActive = false;
				    }
					buffer.flip();
					String message = new String(buffer.array()).trim();
						Personne moi = null;
					// On essaye de convertir le buffer en objet de type Personne
					// On essaye de transformer le flux d'octets reçus du client
					// en objet de type Personne. On deserialise ...
					try {
						ByteArrayInputStream bais = new ByteArrayInputStream( buffer.array());
			            ObjectInputStream ois = new ObjectInputStream(bais); 
			            moi = (Personne)ois.readObject();
						message = "Objet Personne : Nom : " + moi.getNom() + " Prénom : "
								+ moi.getPrenom();
						
					} catch ( Exception e ) {}
					
					System.out.println("Client n°"+numClient+" : "+message);
					
					String messageAuServeur = "Bien reçu : " + message;
					if ( moi != null ) {
						messageAuServeur = "Bien reçu objet personne " +
					    moi.getPrenom() + " " + moi.getNom();
					}
					
					buffer = ByteBuffer.wrap( messageAuServeur.getBytes());
					result = clientChannel.write( buffer );
					while( ! result.isDone() ) {}
					buffer.clear();
					
					if (message.equals("Quit")) {
						synchronized( ServeurMultiClients.nbClients ) {
						  isClientActive = false;
						  buffer.clear();
						  nbClients--;
						  if ( nbClients == 0 ) {
							  isActive = false;
							  try {
								serverChannel.close();
							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						  }
						}
					}
				    if ( result.isCancelled() ) {
				    	isClientActive = false;
				    }
				}
				
				System.out.println("Client n° " + numClient +" deconnecté");
				try {
					clientChannel.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

		}
	}
}
