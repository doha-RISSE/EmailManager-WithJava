package email;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Email {

    private int id;
    private String sujet;
    private String corps;
    private String expediteur;
    private List<String> cheminsPieceJointe=new ArrayList<>();
    
    private List<String> destinataires=new ArrayList<>();
    private LocalDateTime dateEnvoi;
    private String categorie; // EX : "professionnel", "personnel", "promotion", etc.

    

    // 🔹 Constructeur vide (par défaut)
    public Email() {
    	
    }
    
    

    // ce constructeur c pour lenvoie des email par moi , bien sur c pas moi qui va generer le id , c pour ca on le met pas ici dans le constructeur
    
    public Email(String sujet, String corps, String expediteur, List<String> destinataires,
            LocalDateTime dateEnvoi, List<String> chemins) {
   this.sujet = sujet;
   this.corps = corps;
   this.expediteur = expediteur;
   this.destinataires = destinataires;
   this.dateEnvoi = dateEnvoi;
   if (chemins == null) {
       this.cheminsPieceJointe = new ArrayList<>();
   } else {
       this.cheminsPieceJointe = chemins;
   }
}

    // 🔹 Constructeur avec tous les champs, pour que auand on veut lire depuis DB et on va constuirre lemail , on recupere lid
    // que la table a generee et on remplit donc les attribut
    
    public Email(int id, String sujet, String corps, String expediteur, List<String> destinataires,
            LocalDateTime dateEnvoi, List<String> cheminsPieceJointe, String categorie) {
   this.id = id;
   this.sujet = sujet;
   this.corps = corps;
   this.expediteur = expediteur;
   this.destinataires = destinataires;
   this.dateEnvoi = dateEnvoi;
   if (cheminsPieceJointe == null) {
       this.cheminsPieceJointe = new ArrayList<>();
   } else {
       this.cheminsPieceJointe = cheminsPieceJointe;
   }
   this.categorie = categorie;
}


    public String toString() {
    	return "["+getId()+" "+getSujet()+" "+getCorps()+" "+getExpediteur()+" "+getDestinataires()+" "+getDateEnvoi()+" "+getCheminsPieceJointe()+" "+getCategorie()+" ";
    }

    // 🔹 Getters et Setters

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getSujet() {
        return sujet;
    }

    public void setSujet(String sujet) {
        this.sujet = sujet;
    }

    public String getCorps() {
        return corps;
    }

    public void setCorps(String corps) {
        this.corps = corps;
    }

    public String getExpediteur() {
        return expediteur;
    }

    public void setExpediteur(String expediteur) {
        this.expediteur = expediteur;
    }

    public List<String> getDestinataires() {
        return destinataires;
    }

    public void setDestinataires(List<String> destinataires) {
        this.destinataires = destinataires;
    }

    public LocalDateTime getDateEnvoi() {
        return dateEnvoi;
    }

    public void setDateEnvoi(LocalDateTime dateEnvoi) {
        this.dateEnvoi = dateEnvoi;
    }

	public List<String> getCheminsPieceJointe() {
		return cheminsPieceJointe;
	}

	public void setCheminsPieceJointe(List<String> cheminsPieceJointe) {
		this.cheminsPieceJointe = cheminsPieceJointe;
	}
	public String getCategorie() {
	    return categorie;
	}

	public void setCategorie(String categorie) {
	    this.categorie = categorie;
	}


}