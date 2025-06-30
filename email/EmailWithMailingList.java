package email;

import java.time.LocalDateTime;
import java.util.List;

public class EmailWithMailingList {
    private int id;
    private String sujet;
    private String corps;
    private String expediteur;
    private MailingList mailingList;
    private LocalDateTime dateEnvoi;
    private List<String> piecesJointes;

    // Constructeur complet
    public EmailWithMailingList( String sujet, String corps, String expediteur,
                                MailingList mailingList, LocalDateTime dateEnvoi,
                                List<String> piecesJointes) {
        
        this.sujet = sujet;
        this.corps = corps;
        this.expediteur = expediteur;
        this.mailingList = mailingList;
        this.dateEnvoi = dateEnvoi;
        this.piecesJointes = piecesJointes;
    }

    // Getters
    public int getId() {
        return id;
    }

    public String getSujet() {
        return sujet;
    }

    public String getCorps() {
        return corps;
    }

    public String getExpediteur() {
        return expediteur;
    }

    public MailingList getMailingList() {
        return mailingList;
    }

    public LocalDateTime getDateEnvoi() {
        return dateEnvoi;
    }

    public List<String> getPiecesJointes() {
        return piecesJointes;
    }

    // Setters
    public void setId(int id) {
        this.id = id;
    }

    public void setSujet(String sujet) {
        this.sujet = sujet;
    }

    public void setCorps(String corps) {
        this.corps = corps;
    }

    public void setExpediteur(String expediteur) {
        this.expediteur = expediteur;
    }

    public void setMailingList(MailingList mailingList) {
        this.mailingList = mailingList;
    }

    public void setDateEnvoi(LocalDateTime dateEnvoi) {
        this.dateEnvoi = dateEnvoi;
    }

    public void setPiecesJointes(List<String> piecesJointes) {
        this.piecesJointes = piecesJointes;
    }
}
