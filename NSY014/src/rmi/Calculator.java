package rmi;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface Calculator extends Remote {
	public long add( long p1, long p2 ) throws RemoteException;
	public long sub( long p1, long p2 ) throws RemoteException;
	public long mul( long p1, long p2 ) throws RemoteException;
	public long div( long p1, long p2 ) throws RemoteException;
}
