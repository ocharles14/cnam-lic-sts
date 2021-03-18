package rmi;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class CalculatorEngine extends UnicastRemoteObject implements Calculator {
	protected CalculatorEngine() throws RemoteException {
		super(); }

	@Override
	public long add(long p1, long p2) throws RemoteException {
		return p1 + p2; }

	@Override
	public long sub(long p1, long p2) throws RemoteException {
		return p1 - p2;}

	@Override
	public long mul(long p1, long p2) throws RemoteException {
		return p1 * p2;}

	@Override
	public long div(long p1, long p2) throws RemoteException {
		return p1 / p2;}
}
