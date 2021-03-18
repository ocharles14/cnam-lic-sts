package rmi;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class BindingPersonneProvider {
	
	public static void main(String[] args) {
		try {
			PersonneProvider pp = new PersonneProvider();
			// Si l'objet n'herite pas de UnicastRemoteObject on peut exporter le 
			// code du stub avec : UnicastRemoteObject.exportObject(calculator, 1099);
			Registry registry = LocateRegistry.getRegistry("127.0.0.1",1099);
			registry.rebind("fournisseur-personnes",  pp );
			//Naming.rebind("rmi://127.0.0.1:1099/Calculator", calculator );
			System.out.println("Objet PersonneProvider disponible à distance par rmi ...");
		} catch (Exception e) {
			e.printStackTrace();
			System.err.println("Exception bind  : " + e.getClass().getName());
		}
	}

}
