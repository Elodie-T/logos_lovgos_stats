package dao.mongo.entity;

import java.time.LocalDate;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "suiviCours")
public class SuiviCours {

	@Id
	private Integer _id;
	private LocalDate dateInscription;
	private Integer noteCours;
	private String commentaire;
	private LocalDate dateFinSuivi;
	private Boolean acquis;
//	@DBRef
	private Integer eleve;
//	@DBRef
	private Integer cours;
	
	public SuiviCours(Integer _id, LocalDate dateInscription, Integer noteCours, String commentaire,
			LocalDate dateFinSuivi, Boolean acquis, Integer eleve, Integer cours) {
		super();
		this._id = _id;
		this.dateInscription = dateInscription;
		this.noteCours = noteCours;
		this.commentaire = commentaire;
		this.dateFinSuivi = dateFinSuivi;
		this.acquis = acquis;
		this.eleve = eleve;
		this.cours = cours;
	}
	
	public SuiviCours() {
		super();
	}
	
	public Integer get_id() {
		return _id;
	}
	public void set_id(Integer _id) {
		this._id = _id;
	}
	public LocalDate getDateInscription() {
		return dateInscription;
	}
	public void setDateInscription(LocalDate dateInscription) {
		this.dateInscription = dateInscription;
	}
	public Integer getNoteCours() {
		return noteCours;
	}
	public void setNoteCours(Integer noteCours) {
		this.noteCours = noteCours;
	}
	public String getCommentaire() {
		return commentaire;
	}
	public void setCommentaire(String commentaire) {
		this.commentaire = commentaire;
	}
	public LocalDate getDateFinSuivi() {
		return dateFinSuivi;
	}
	public void setDateFinSuivi(LocalDate dateFinSuivi) {
		this.dateFinSuivi = dateFinSuivi;
	}
	public Boolean getAcquis() {
		return acquis;
	}
	public void setAcquis(Boolean acquis) {
		this.acquis = acquis;
	}
	public Integer getEleve() {
		return eleve;
	}
	public void setEleve(Integer eleve) {
		this.eleve = eleve;
	}
	public Integer getCours() {
		return cours;
	}
	public void setCours(Integer cours) {
		this.cours = cours;
	}
	
	@Override
	public String toString() {
		return "SuiviCours [_id=" + _id + ", dateInscription=" + dateInscription + ", noteCours=" + noteCours
				+ ", commentaire=" + commentaire + ", dateFinSuivi=" + dateFinSuivi + ", acquis=" + acquis + ", eleve="
				+ eleve + ", cours=" + cours + "]";
	}
}