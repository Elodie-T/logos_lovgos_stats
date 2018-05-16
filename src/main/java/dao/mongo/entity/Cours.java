package dao.mongo.entity;

import java.time.LocalDate;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "cours")
public class Cours {
	
	@Id
	private Integer _id;
	private String titre;
//	@DBRef
	private Integer auteur;
	private Niveau niveau;
	
	public Cours(Integer _id, String titre, Integer auteur, Niveau niveau) {
		super();
		this._id = _id;
		this.titre = titre;
		this.auteur = auteur;
		this.niveau = niveau;
	}
	
	public Cours() {
		super();
	}
	
	public Integer get_id() {
		return _id;
	}
	public void set_id(Integer _id) {
		this._id = _id;
	}
	public String getTitre() {
		return titre;
	}
	public void setTitre(String titre) {
		this.titre = titre;
	}
	public Integer getAuteur() {
		return auteur;
	}
	public void setAuteur(Integer auteur) {
		this.auteur = auteur;
	}
	public Niveau getNiveau() {
		return niveau;
	}
	public void setNiveau(Niveau niveau) {
		this.niveau = niveau;
	}
	
	@Override
	public String toString() {
		return "Cours [_id=" + _id + ", titre=" + titre + ", auteur=" + auteur + ", niveau=" + niveau + "]";
	}
	
}