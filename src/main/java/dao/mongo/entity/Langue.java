package dao.mongo.entity;

public class Langue {
	
	private String libelle;
	private String codeIso;
	
	public Langue(String libelle, String codeIso) {
		super();
		this.libelle = libelle;
		this.codeIso = codeIso;
	}
	
	public Langue() {
		super();
	}

	public String getLibelle() {
		return libelle;
	}

	public void setLibelle(String libelle) {
		this.libelle = libelle;
	}

	public String getCodeIso() {
		return codeIso;
	}

	public void setCodeIso(String codeIso) {
		this.codeIso = codeIso;
	}

	@Override
	public String toString() {
		return "Langue [libelle=" + libelle + ", codeIso=" + codeIso + "]";
	}

}