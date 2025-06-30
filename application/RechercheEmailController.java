package application;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.*;
import java.time.LocalDate;
import java.util.*;

import email.DatabaseConnection;

public class RechercheEmailController {

    @FXML private VBox zoneRecherche;
    @FXML private ListView<String> listeEmails;

    @FXML private Label detailSujet;
    @FXML private Label detailCategorie;
    @FXML private Label detailCorps;
    @FXML private Label detailEmetteur;
    @FXML private Label detailRecepteur;
    @FXML private Label detailPiecesJointes;

    private Map<String, Map<String, String>> resultatsEmails = new HashMap<>();

    // ⚠️ Mapping des fx:id dans le FXML vers les vraies colonnes SQL
    private static final Map<String, String> champVersColonne = Map.of(
        "sujet", "e.sujet",
        "categorie", "e.categorie",
        "corps", "e.corps",
        "adresseRecepteur", "a2.adresse",
        "date_envoi", "e.date_envoi"
    );

    @FXML
    private void handleLancerRecherche() {
        StringBuilder requete = new StringBuilder(
            "SELECT e.sujet, e.categorie, e.corps, e.date_envoi, " +
            "a1.adresse AS emetteur, a2.adresse AS recepteur, pj.chemin " +
            "FROM email e " +
            "LEFT JOIN adresse_email a1 ON e.id_expediteur = a1.id_adresse " +
            "LEFT JOIN destinataire d ON d.id_email = e.id_email " +
            "LEFT JOIN adresse_email a2 ON d.id_destinataire = a2.id_adresse " +
            "LEFT JOIN email_piece_jointe ep ON e.id_email = ep.id_email " +
            "LEFT JOIN piece_jointe pj ON pj.id_piece = ep.id_piece " +
            "WHERE 1=1"
        );

        List<Object> valeurs = new ArrayList<>();

        for (Node node : zoneRecherche.getChildren()) {
            String fxid = node.getId();
            if (fxid == null || !champVersColonne.containsKey(fxid)) continue;

            String colonne = champVersColonne.get(fxid);

            if (node instanceof TextField champ) {
                String valeur = champ.getText().trim();
                if (!valeur.isEmpty()) {
                    requete.append(" AND ").append(colonne).append(" LIKE ?");
                    valeurs.add("%" + valeur + "%");
                }
            } else if (node instanceof DatePicker picker) {
                LocalDate date = picker.getValue();
                if (date != null) {
                    requete.append(" AND ").append(colonne).append(" >= ? AND ")
                           .append(colonne).append(" < ?");
                    valeurs.add(Timestamp.valueOf(date.atStartOfDay()));
                    valeurs.add(Timestamp.valueOf(date.plusDays(1).atStartOfDay()));
                }
            }
        }

        // ✅ Condition sur l'émetteur (utilisateur connecté)
        requete.append(" AND a1.adresse = ?");
        valeurs.add(Session.getUtilisateurConnecte());

        System.out.println("Requête SQL générée : " + requete);

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(requete.toString())) {

            for (int i = 1; i <= valeurs.size(); i++) {
                Object valeur = valeurs.get(i - 1);
                if (valeur instanceof String) {
                    stmt.setString(i, (String) valeur);
                } else if (valeur instanceof Timestamp) {
                    stmt.setTimestamp(i, (Timestamp) valeur);
                }
            }

            ResultSet rs = stmt.executeQuery();
            ObservableList<String> items = FXCollections.observableArrayList();
            resultatsEmails.clear();

            while (rs.next()) {
                String sujet = rs.getString("sujet");
                String date = rs.getString("date_envoi");
                String idAffichage = sujet + " (" + date + ")";
                items.add(idAffichage);

                Map<String, String> details = new HashMap<>();
                details.put("sujet", sujet);
                details.put("categorie", rs.getString("categorie"));
                details.put("corps", rs.getString("corps"));
                details.put("emetteur", rs.getString("emetteur"));
                details.put("recepteur", rs.getString("recepteur"));
                details.put("pieces", rs.getString("chemin"));

                resultatsEmails.put(idAffichage, details);
            }

            listeEmails.setItems(items);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleSelectionEmail(MouseEvent event) {
        String selection = listeEmails.getSelectionModel().getSelectedItem();
        if (selection != null && resultatsEmails.containsKey(selection)) {
            Map<String, String> details = resultatsEmails.get(selection);
            detailSujet.setText(details.getOrDefault("sujet", ""));
            detailCategorie.setText(details.getOrDefault("categorie", ""));
            detailCorps.setText(details.getOrDefault("corps", ""));
            detailEmetteur.setText(details.getOrDefault("emetteur", ""));
            detailRecepteur.setText(details.getOrDefault("recepteur", ""));
            detailPiecesJointes.setText(details.getOrDefault("pieces", ""));
        }
    }
}
