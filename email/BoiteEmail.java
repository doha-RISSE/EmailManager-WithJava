package email;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class BoiteEmail {
    private String adresseEmail;
    private List<Email> emailsRecus;
    private List<Email> emailsEnvoyes;

    public BoiteEmail(String adresseEmail) {
        this.adresseEmail = adresseEmail;
        this.emailsRecus = new ArrayList<>();
        this.emailsEnvoyes = new ArrayList<>();
    }

    public void chargerEmails() {
    	 emailsRecus.clear();
    	    emailsEnvoyes.clear();
        chargerEmailsRecus();
        chargerEmailsEnvoyes();
    }

    private void chargerEmailsRecus() {
        String sql = "SELECT e.id_email, e.sujet, e.corps, e.date_envoi, ae.adresse AS expediteur, e.categorie " +
                     "FROM email e " +
                     "JOIN destinataire d ON e.id_email = d.id_email " +
                     "JOIN adresse_email ae ON e.id_expediteur = ae.id_adresse " +
                     "JOIN adresse_email adr ON d.id_destinataire = adr.id_adresse " +
                     "WHERE adr.adresse = ? " +
                     "ORDER BY e.date_envoi DESC";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, adresseEmail);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Email email = construireEmailDepuisResultSet(rs, conn, true); // pourReception = true
                emailsRecus.add(email);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void chargerEmailsEnvoyes() {
        String sql = "SELECT e.id_email, e.sujet, e.corps, e.date_envoi, ae.adresse AS expediteur, e.categorie " +
                     "FROM email e " +
                     "JOIN adresse_email ae ON e.id_expediteur = ae.id_adresse " +
                     "WHERE ae.adresse = ? " +
                     "ORDER BY e.date_envoi DESC";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, adresseEmail);
            ResultSet rs = ps.executeQuery();

            List<Integer> emailsVus = new ArrayList<>(); // pour éviter les doublons

            while (rs.next()) {
                int idEmail = rs.getInt("id_email");

                if (!emailsVus.contains(idEmail)) {
                    Email email = construireEmailDepuisResultSet(rs, conn, false); // pourReception = false
                    emailsEnvoyes.add(email);
                    emailsVus.add(idEmail);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Construit un objet Email depuis un ResultSet.
     * @param rs Résultat de la requête sur la table `email`
     * @param conn Connexion à la base de données
     * @param pourReception true si c'est un mail reçu (1 destinataire), false si c'est un mail envoyé (plusieurs destinataires possibles)
     */
    private Email construireEmailDepuisResultSet(ResultSet rs, Connection conn, boolean pourReception) throws SQLException {
        int idEmail = rs.getInt("id_email");
        String sujet = rs.getString("sujet");
        String corps = rs.getString("corps");
        Timestamp timestamp = rs.getTimestamp("date_envoi");
        String expediteur = rs.getString("expediteur");
        String categorie = rs.getString("categorie");

        // === Destinataires ===
        List<String> destinataires = new ArrayList<>();
        if (pourReception) {
            // Si reçu, le seul destinataire est cette boîte
            destinataires.add(this.adresseEmail);
        } else {
            // Si envoyé, on récupère tous les destinataires
            String sqlDest = "SELECT ae.adresse FROM destinataire d " +
                             "JOIN adresse_email ae ON d.id_destinataire = ae.id_adresse " +
                             "WHERE d.id_email = ?";
            try (PreparedStatement psDest = conn.prepareStatement(sqlDest)) {
                psDest.setInt(1, idEmail);
                ResultSet rsDest = psDest.executeQuery();
                while (rsDest.next()) {
                    destinataires.add(rsDest.getString("adresse"));
                }
            }
        }

        // === Pièces jointes ===
        List<String> piecesJointes = new ArrayList<>();
        String sqlPieces = "SELECT pj.chemin FROM email_piece_jointe epj " +
                           "JOIN piece_jointe pj ON epj.id_piece = pj.id_piece " +
                           "WHERE epj.id_email = ?";
        try (PreparedStatement psPieces = conn.prepareStatement(sqlPieces)) {
            psPieces.setInt(1, idEmail);
            ResultSet rsPieces = psPieces.executeQuery();
            while (rsPieces.next()) {
                piecesJointes.add(rsPieces.getString("chemin"));
            }
        }

        return new Email(idEmail, sujet, corps, expediteur, destinataires,
                         timestamp.toLocalDateTime(), piecesJointes, categorie);
    }

    // Surcharge optionnelle (utilisable si tu veux appeler la méthode sans indiquer le type)
    private Email construireEmailDepuisResultSet(ResultSet rs, Connection conn) throws SQLException {
        return construireEmailDepuisResultSet(rs, conn, false);
    }

    public void afficherEmails() {
        System.out.println("=== Boîte Email : " + adresseEmail + " ===");
        System.out.println("Emails reçus :");
        for (Email e : emailsRecus) {
            System.out.println(e.getDateEnvoi() + " | " + e.getExpediteur() + " | " + e.getSujet());
        }
        System.out.println("\nEmails envoyés :");
        for (Email e : emailsEnvoyes) {
            System.out.println(e.getDateEnvoi() + " | " + e.getDestinataires() + " | " + e.getSujet());
        }
    }

    public List<Email> getEmailsRecus() {
        return emailsRecus;
    }

    public List<Email> getEmailsEnvoyes() {
        return emailsEnvoyes;
    }

	public String getAdresseEmail() {
		// TODO Auto-generated method stub
		return this.adresseEmail;
	}
}