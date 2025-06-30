package email;

import java.util.List;

public class MailingList {
    private int id; // optionnel, si tu veux suivre la base
    private String nom;
    private List<String> adressesEmail;

    
    
    
    public MailingList(String nom, List<String> adressesEmail) {
        this.nom = nom;
        this.adressesEmail = adressesEmail;
    }

    // Getters et setters
    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public List<String> getAdressesEmail() {
        return adressesEmail;
    }

    public void setAdressesEmail(List<String> adressesEmail) {
        this.adressesEmail = adressesEmail;
    }

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
}
