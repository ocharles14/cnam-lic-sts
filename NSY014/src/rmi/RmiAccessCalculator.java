package rmi;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class RmiAccessCalculator {
	public static String SERVER_ADDRESS = "127.0.0.1";
	public static void main(String[] args) {
		try {
			Registry registry = LocateRegistry.getRegistry( SERVER_ADDRESS ,1099);
			Calculator remoteCalculator = (Calculator)registry.lookup("calculator");
			System.out.println("Calcul à distance de 10+10 : " 
								+ remoteCalculator.add(10,10));
			System.out.println("Calcul à distance de 5*4 : " 
					+ remoteCalculator.mul(4,5));

		}
		catch( Exception e ) {
			e.printStackTrace();
			System.err.println("Exception CalculatorEngine : " 
								+ e.getClass().getName());
		}
	}
}
