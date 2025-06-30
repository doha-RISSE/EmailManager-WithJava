package application;

import email.Email;
import email.EnvoiEmail;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class NouveauEmailController {

    @FXML
    private TextField destinatairesField;

    @FXML
    private TextField sujetField;

    @FXML
    private TextArea corpsArea;

    @FXML
    private Label piecesJointesLabel;

    private List<String> cheminsPiecesJointes = new ArrayList<>();

    private String expediteur;
    private String motDePasse; // à définir via méthode setUtilisateurConnecte

    public void setUtilisateurConnecte(String expediteur, String motDePasse) {
        this.expediteur = expediteur;
        this.motDePasse = motDePasse;
    }

    @FXML
    private void handleAjouterPieceJointe() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Sélectionner une pièce jointe");
        List<File> fichiers = fileChooser.showOpenMultipleDialog(new Stage());

        if (fichiers != null) {
            for (File f : fichiers) {
                cheminsPiecesJointes.add(f.getAbsolutePath());
            }
            piecesJointesLabel.setText(String.join(", ", cheminsPiecesJointes));
        }
    }

    @FXML
    private void handleEnvoyer() {
        String destinatairesText = destinatairesField.getText();
        String sujet = sujetField.getText();
        String corps = corpsArea.getText();

        if (destinatairesText.isEmpty() || sujet.isEmpty() || corps.isEmpty()) {
            showAlert("Tous les champs doivent être remplis.");
            return;
        }

        List<String> destinataires = List.of(destinatairesText.split("\\s*,\\s*"));

        Email email = new Email(
                sujet,
                corps,
                expediteur,
                destinataires,
                LocalDateTime.now(),
                cheminsPiecesJointes
        );

        EnvoiEmail envoi = new EnvoiEmail();

        int idEmail = envoi.envoyerEmail(email, motDePasse);

        if (idEmail != -1) {
            showAlert("Email envoyé avec succès !");
            ((Stage) destinatairesField.getScene().getWindow()).close();
        } else {
            showAlert("Échec de l'envoi de l'email.");
        }
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
