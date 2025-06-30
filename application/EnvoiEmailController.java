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
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class EnvoiEmailController {

    @FXML private TextField champDestinataires;
    @FXML private TextField champSujet;
    @FXML private TextArea champCorps;
    
    
    // ces 2 attirbut c pour gerer les pieces jointes : un attribut dapres le fx:id de la listeView Qui va afficher le nom 
    //de fichiers et pas le chemin absolue et un autre attribut qui sera une liste contenant les chemins absolues 
    // pour garder 
    @FXML private ListView<String> listePiecesJointes;

    private List<File> fichiersJointes = new ArrayList<>();

    
    

    String expediteur = Session.getUtilisateurConnecte();
    String motDePasse = Session.getMotDePasse();


    public void setExpediteur(String expediteur) {
        this.expediteur = expediteur;
    }

    public void setMotDePasse(String motDePasse) {
        this.motDePasse = motDePasse;
    }

    @FXML
    private void ajouterPieceJointe() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choisir des pièces jointes");
        List<File> fichiers = fileChooser.showOpenMultipleDialog(null);

        if (fichiers != null) {
            fichiersJointes.addAll(fichiers);
            for (File f : fichiers) {
                listePiecesJointes.getItems().add(f.getName());
            }
        }
    }
    @FXML
    private void envoyerEmail() {
        String sujet = champSujet.getText();
        String corps = champCorps.getText();
        List<String> destinataires = Arrays.asList(champDestinataires.getText().split(",\\s*"));

        if (expediteur == null || motDePasse == null) {
            showAlert("Erreur", "Expéditeur ou mot de passe non définis.");
            return;
        }
        
        // prepare la liste des chemins qui seront utilises dans lobjet Email
        List<String> chemins = new ArrayList<>();
        if (fichiersJointes != null && !fichiersJointes.isEmpty()) {
            chemins = fichiersJointes.stream()
                                     .map(File::getAbsolutePath)
                                     .collect(Collectors.toList());
        }
        Email email = new Email(
                sujet,
                corps,
                expediteur,
                destinataires,
                LocalDateTime.now(),
                chemins
        );

        EnvoiEmail envoi = new EnvoiEmail();
        int idEmail = envoi.envoyerEmail(email, motDePasse);

        if (idEmail != -1) {
            showAlert("Succès", "Email envoyé !");
            ((Stage) champSujet.getScene().getWindow()).close();
        } else {
            showAlert("Erreur", "Échec de l'envoi de l'email.");
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