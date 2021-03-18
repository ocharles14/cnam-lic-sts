package sockets.client;

import java.io.Serializable;


public class Personne implements Serializable {
		
		private String id;
		private String nom;
		private String prenom;
		
		public Personne(String nom, String prenom, String id ) {
			super();
			this.id = id;
			this.nom = nom;
			this.prenom = prenom;
		}
		public String getId() {
			return id;
		}
		public String getNom() {
			return nom;
		}
		public void setNom(String nom) {
			this.nom = nom;
		}
		public String getPrenom() {
			return prenom;
		}
		public void setPrenom(String prenom) {
			this.prenom = prenom;
		}
		
	}
	

