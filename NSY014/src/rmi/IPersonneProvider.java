package rmi;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface IPersonneProvider extends Remote {	
	public IPersonne getPersonneById( String id ) throws RemoteException;
	public List<IPersonne> getAllPersonnes() throws RemoteException;
}
