package application;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import java.sql.*;

import email.DatabaseConnection;
import application.Session;

public class ArchivageController {

    @FXML
    private ListView<String> listViewEmails;

    @FXML
    private Button btnArchiver;

    // Pour stocker les ID des emails affichés
    private ObservableList<Integer> emailIds = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        chargerEmails();

        btnArchiver.setOnAction(event -> archiverEmail());
    }

    private void chargerEmails() {
        try (Connection conn = DatabaseConnection.getConnection()) {
            String requete = """
                SELECT e.id_email, e.sujet, e.categorie, e.corps, e.date_envoi,
                       a2.adresse AS destinataire, pj.chemin
                FROM email e
                LEFT JOIN adresse_email a1 ON e.id_expediteur = a1.id_adresse
                LEFT JOIN destinataire d ON d.id_email = e.id_email
                LEFT JOIN adresse_email a2 ON d.id_destinataire = a2.id_adresse
                LEFT JOIN email_piece_jointe ep ON e.id_email = ep.id_email
                LEFT JOIN piece_jointe pj ON pj.id_piece = ep.id_piece
                WHERE a1.adresse = ?
            """;

            PreparedStatement stmt = conn.prepareStatement(requete);
            stmt.setString(1, Session.getUtilisateurConnecte());

            ResultSet rs = stmt.executeQuery();

            ObservableList<String> items = FXCollections.observableArrayList();
            emailIds.clear();

            while (rs.next()) {
                int id = rs.getInt("id_email");
                String sujet = rs.getString("sujet");
                String destinataire = rs.getString("destinataire");
                String date = rs.getString("date_envoi");

                String ligne = "Sujet: " + sujet + " | Destinataire: " + destinataire + " | Date: " + date;
                items.add(ligne);
                emailIds.add(id);
            }

            listViewEmails.setItems(items);

        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Erreur", "Impossible de charger les emails.");
        }
    }

    private void archiverEmail() {
        int index = listViewEmails.getSelectionModel().getSelectedIndex();
        if (index == -1) {
            showAlert("Aucune sélection", "Veuillez sélectionner un email.");
            return;
        }

        int idEmail = emailIds.get(index);

        try (Connection conn = DatabaseConnection.getConnection()) {
            conn.setAutoCommit(false);

            // 1. Copier dans la table archive (tu peux ajuster selon ta structure)
            String copySQL = "INSERT INTO archive SELECT * FROM email WHERE id_email = ?";
            PreparedStatement copyStmt = conn.prepareStatement(copySQL);
            copyStmt.setInt(1, idEmail);
            copyStmt.executeUpdate();

            // 2. Supprimer de la table email
            String deleteSQL = "DELETE FROM email WHERE id_email = ?";
            PreparedStatement deleteStmt = conn.prepareStatement(deleteSQL);
            deleteStmt.setInt(1, idEmail);
            deleteStmt.executeUpdate();

            conn.commit();

            showAlert("Succès", "Email archivé avec succès.");
            chargerEmails(); // actualiser la liste

        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Erreur", "Impossible d'archiver l'email.");
        }
    }

    private void showAlert(String titre, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titre);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
