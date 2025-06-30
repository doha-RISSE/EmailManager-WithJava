package application;

import email.DatabaseConnection;
import email.Email;
import email.EnvoiEmail;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;

import java.io.File;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class EnvoyerMLController implements Initializable {

    @FXML private TextField fieldSujet;
    @FXML private TextArea areaCorps;

    private List<String> cheminsPiecesJointes = new ArrayList<>();

    @FXML private ComboBox<String> comboMailingList;
    @FXML private ListView<File> listViewPiecesJointes;

    private List<File> piecesJointes = new ArrayList<>();

    @FXML
    private void handleAjouterPieceJointe(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        File selectedFile = fileChooser.showOpenDialog(null);
        if (selectedFile != null) {
            piecesJointes.add(selectedFile);
            listViewPiecesJointes.getItems().add(selectedFile);
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = "SELECT nom FROM mailing_list";
            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                comboMailingList.getItems().add(rs.getString("nom"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleEnvoyerEmail() {
        String nomML = comboMailingList.getValue();
        if (nomML == null || nomML.isEmpty()) {
            showAlert("Erreur", "Veuillez choisir une mailing list.");
            return;
        }

        List<String> adresses = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = "SELECT a.adresse FROM mailing_list_adresse m " +
                         "JOIN adresse_email a ON m.id_adresse = a.id_adresse " +
                         "JOIN mailing_list ml ON m.id_liste = ml.id_liste " +
                         "WHERE ml.nom = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, nomML);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                adresses.add(rs.getString("adresse"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Erreur", "Une erreur s'est produite lors de la récupération des adresses.");
            return;
        }

        if (adresses.isEmpty()) {
            showAlert("Information", "La mailing list sélectionnée ne contient aucune adresse.");
            return;
        }

        // Créer l'email avec toutes les adresses récupérées
        String sujet = fieldSujet.getText();
        String corps = areaCorps.getText();
        String expediteur = Session.getUtilisateurConnecte();
        String motDePasse = Session.getMotDePasse();

        Email email = new Email(sujet, corps, expediteur, adresses, LocalDateTime.now(), cheminsPiecesJointes);
        EnvoiEmail serviceEnvoi = new EnvoiEmail();
        int result = serviceEnvoi.envoyerEmail(email, motDePasse);

        if (result >1) {
            showAlert("Succès", "Email envoyé avec succès !");
        } else {
            showAlert("Échec", "L'envoi de l'email a échoué.");
        }
    }

    // 👉 Méthode utilitaire pour afficher une alerte
    private void showAlert(String titre, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titre);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
