package threads;

import java.io.*;
import java.util.concurrent.*;

public class DemoThread {
	// Représente le solde de 10 comptes de 0 à 9
	static double[] soldes = new double[10];

	public static void transferSynchronise(int numCompteDebite, int numCompteCredite, double montant)
			throws InterruptedException {
		// Synchronisation des acces à une variable soldes
		synchronized (soldes) {
			soldes[numCompteCredite] += montant;
			Thread.sleep(20);
			soldes[numCompteDebite] -= montant;
		}
	}

	public static void transferNonSyncronise(int numCompteDebite, int numCompteCredite, double montant) throws InterruptedException {		
			soldes[numCompteCredite] += montant;
			Thread.sleep(20);
			soldes[numCompteDebite] -= montant;
	}
	

	public static double consolidation() {
		double montantTotal = 0.0;
		synchronized (soldes) {
			for (double montant : soldes) {
				montantTotal += montant;
			}
		}
		return montantTotal;
	}

	

	public static void demoSerialisation() throws IOException, ClassNotFoundException {
		MonThread t1 = new MonThread(1, 500);
		// Serialisation du thread t1
		FileOutputStream fos = new FileOutputStream("thread.ser");
		ObjectOutputStream oos = new ObjectOutputStream(fos);
		oos.writeObject(t1);
		oos.close();
		fos.close();

		// Récuperation de l'objet serialisé
		FileInputStream fis = new FileInputStream("thread.ser");
		ObjectInputStream ois = new ObjectInputStream(fis);
		MonThread tSer = (MonThread) ois.readObject();
		ois.close();
		fis.close();
		tSer.start();
	}

	public static void demoThread() {
		MonThread t1 = new MonThread(1, 500);
		MonThread t2 = new MonThread(2, 1000);
		MonThread t3 = new MonThread(3, 1000);
		// Lancement des threads avec start()
		// start est non bloquant
		// start déclenche la méthode run dans un
		// fil d'execution propre
		t1.start();
		t2.start();
		t3.start();
	}

	public static int randomBetween(int a, int b) {
		return (int) (a + Math.round(Math.random() * (b - a)));
	}

	public static void demoSynchronisationThreads(boolean synchro) {
		for (int i = 0; i < 10; i++) {
			soldes[i] = 2000.00;
		}
		// Creation et lancement d'un thread qui fait des transferts
		// de compte à compte
		new Thread(new Runnable() {
			@Override
			public void run() {
				for (int i = 1; i < 400; i++) {
					int cDebit = randomBetween(0, 9);
					int cCredit = randomBetween(0, 9);
					int montant = randomBetween(100, 500);
					System.out.println("N° iteration : " + i + " Time : " + System.currentTimeMillis()
							+ " Transfert du compte " + cDebit + " vers " + cCredit + " de " + montant + " euros");
					try {
						if (synchro) {
							transferSynchronise(cDebit, cCredit, montant);
						} else {
							transferNonSyncronise(cDebit, cCredit, montant);
						}
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}).start();

		// Creation et lancement d'un thread qui demande
		// et calcul la consolidation de tous les comptes
		Executors.newSingleThreadScheduledExecutor().schedule(new Runnable() {
			@Override
			public void run() {
				for (int i = 0; i < 400; i++) {
					System.out.println(
							"********* Time : " + System.currentTimeMillis() + " Montant consolidé " + consolidation());
					try {
						Thread.sleep(20);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}

			}
		}, 10, TimeUnit.MILLISECONDS);
	}

	public static void demoFuture() {

		// Callable avec le type du resultat
		// methode call obligatoire qui renvoie le type attendu
		Callable<String> calculateur = new Callable<String>() {
			public String call() {
				return groscalcul();
			}

			public String groscalcul() {
				for (int i = 0; i < 5000; i++) {
					System.out.println("Iteration dans l'objet future " + i);
				}
				return "success";
			}
		};

		ExecutorService interprete = Executors.newSingleThreadExecutor();

		// On lance une tache en asynchrone pour une utilisation
		// future du resultat de cette tâche
		Future<String> future = interprete.submit(calculateur);

		// On fait autre chose pendant que le callable s'exécute
		for (int i = 0; i < 500; i++) {
			System.out.println("Iteration hors future " + i);
		}
		// Quand on est prêt on demande le resultat du callable depuis la future
		try {
			System.out.println("Resultat " + future.get());
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		}

	}
	
	/********************************************************************** 
	 *  MAIN
	 * ********************************************************************/
	public static void main(String[] args) throws IOException, ClassNotFoundException {
		// demoThread();
		// demoSerialisation();
		// demoFuture();
		demoSynchronisationThreads(true);
		//demoSynchronisationThreads(false);
	}

}

// Heritage de la classe Thread
// Pour qu'un objet soit serializable il faut
// qu'il implémente l'interface Serializable
class MonThread extends Thread implements Serializable {

	private final int max;
	private final int num;

	public MonThread(int num, int max) {
		this.num = num;
		this.max = max;

	}

	// Redefinition de la méthode run
	@Override
	public void run() {
		for (int i = 1; i <= max; i++) {
			System.out.println("Thread n°" + num + " " + "compteur : " + i);
		}
	}
}