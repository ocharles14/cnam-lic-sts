package rmi;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

// Il faut lancer le programme d'annuaire d'enregistrement 
// des objets distants rmiregistry avec le code binaire de la souche dans son classpath :
//    commande : rmiregistry <option> <port>
public class BindingCalculator {
	public static void main(String[] args) {
		try {
			Calculator calculator = new CalculatorEngine();
			// Si l'objet n'herite pas de UnicastRemoteObject on peut exporter le 
			// code du stub avec : UnicastRemoteObject.exportObject(calculator, 1099);
			Registry registry = LocateRegistry.getRegistry("127.0.0.1",1099);
			registry.rebind("calculator", calculator);
			//Naming.rebind("rmi://127.0.0.1:1099/Calculator", calculator );
			System.out.println("Objet de type CalculatorEngine disponible ...");
		} catch (Exception e) {
			System.err.println("Exception bind  : " + e.getClass().getName());
		}
	}
}
