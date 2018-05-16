package dao.mongo.entity;

public class Niveau {
	
	private String libelle;
	private Langue langue;
	
	public Niveau(String libelle, Langue langue) {
		super();
		this.libelle = libelle;
		this.langue = langue;
	}
	
	public Niveau() {
		super();
	}
	
	public String getLibelle() {
		return libelle;
	}
	
	public void setLibelle(String libelle) {
		this.libelle = libelle;
	}
	
	public Langue getLangue() {
		return langue;
	}
	
	public void setLangue(Langue langue) {
		this.langue = langue;
	}
	
	@Override
	public String toString() {
		return "Niveau [libelle=" + libelle + ", langue=" + langue + "]";
	}

}