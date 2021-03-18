package rmi;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.List;

public class RmiAccessPersonneProvider {
	public static String SERVER_ADDRESS = "127.0.0.1";
	public static void main(String[] args) {
		try {
			Registry registry = LocateRegistry.getRegistry( SERVER_ADDRESS ,1099);
			IPersonneProvider remotePersonneProvider = (IPersonneProvider)registry.lookup("fournisseur-personnes");
			
			IPersonne simon = remotePersonneProvider.getPersonneById("000003");
			if ( simon != null ) {
			  System.out.println("Personne récupérée : " + simon.getNom()+
					  " " + simon.getPrenom());
			}
			
			System.out.println("");
			System.out.println("Liste des personnes");
			List<IPersonne> personnes = remotePersonneProvider.getAllPersonnes();
			int num = 0;
			for( IPersonne p : personnes ) {
				num++;
				System.out.println("Personne n° : " + num + " " + p.getNom()+
						  " " + p.getPrenom() +" "+ p.getId());
			}
		}
		catch( Exception e ) {
			e.printStackTrace();
			System.err.println("Exception CalculatorEngine : " 
								+ e.getClass().getName());
		}
	}

}
