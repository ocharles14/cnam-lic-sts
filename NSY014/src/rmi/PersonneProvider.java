package rmi;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class PersonneProvider extends UnicastRemoteObject implements IPersonneProvider {

	// Map< TypeClé , ValeurStocké >
	private Map<String,Personne> personnes;
	
	protected PersonneProvider() throws RemoteException {
		super();
		creationListePersonnes();
	}

	private void creationListePersonnes() {
		personnes = new HashMap<String,Personne>();
		personnes.put("000001" , new Personne("CHARLES","Olivier","000001"));
		personnes.put("000002" , new Personne("CHARLES","Martin","000002"));
		personnes.put("000003" , new Personne("CHARLES","Simon","000003"));
		
		
	}

	@Override
	public Personne getPersonneById(String id) {
		return personnes.get( id );
	}

	@Override
	public List<IPersonne> getAllPersonnes() {
	  return new ArrayList<IPersonne>(personnes.values());
	}

}
