package application;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import email.BoiteEmail;
import email.DatabaseConnection;
import email.Email;
import email.EmailClassifierDB;
import email.EmailWithMailingList;
import email.EnvoiEmail;
import email.MailingList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.application.Platform;
import javafx.collections.ListChangeListener;
import javafx.scene.control.ScrollBar;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.geometry.Bounds;
import javafx.geometry.Orientation;



import javafx.fxml.FXML;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.animation.FadeTransition;
import javafx.util.Duration;


public class MainController {
	
	@FXML
	private Label labelCategorie;

    @FXML
    private Label welcomeLabel;

    @FXML
    private ListView<String> listEmailsRecus;

    @FXML
    private ListView<String> listEmailsEnvoyes;

    @FXML
    private Label labelExpediteur;

    @FXML
    private Label labelDestinataires;

    @FXML
    private Label labelSujet;

    @FXML
    private TextArea textAreaCorps;

    private BoiteEmail boiteEmail;


    @FXML
    private BorderPane rootPane;

    @FXML
    private VBox glassPane;
    @FXML
    private TextField txtExpediteur;

    @FXML
    private TextField txtSujet;

    @FXML
    private TextArea txtCorps;

    @FXML
    private TextArea txtAdressesMailingList;

    @FXML
    private Button btnEnvoyerMailingList;

   

    @FXML
    private void handleRefresh() {
        // Vider les deux listes graphiques
        listEmailsRecus.getItems().clear();
        listEmailsEnvoyes.getItems().clear();

        // Recharger les emails depuis la source (boiteEmail)
        chargerEmails();
    }

    
    @FXML
    private void ouvrirFenetreEnvoi() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("EnvoiEmail.fxml"));
            Parent root = loader.load();

            //EnvoiEmailController controller = loader.getController();
           // controller.setExpediteur(boiteEmail.getAdresseEmail());

            Stage stage = new Stage();
            stage.setTitle("Nouveau message");
            stage.setScene(new Scene(root));
            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @FXML
    private void ouvrirFenetreCreerMailingList() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("CreerMailingList.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setTitle("Créer une Mailing List");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    
    public void initialize() {
    	
    	
        // S'assurer que la hauteur est adaptée au contenu (boutons)
        glassPane.setMaxHeight(VBox.USE_COMPUTED_SIZE);
        glassPane.setPrefHeight(VBox.USE_COMPUTED_SIZE);

        // Au départ caché
        glassPane.setVisible(false);
        glassPane.setOpacity(0);

        // Apparition/disparition au passage souris proche bord droit
        rootPane.setOnMouseMoved(event -> {
            double mouseX = event.getX();
            double width = rootPane.getWidth();

            if (mouseX > width * 0.85) {  // souris dans les 15% à droite
                showGlassPane();
            } else {
                // Ne cache pas immédiatement si la souris est sur la barre
                if (!isMouseOverGlassPane(event)) {
                    hideGlassPane();
                }
            }
        });

        // Cacher la barre quand la souris quitte la barre
        glassPane.setOnMouseExited(event -> hideGlassPane());
    }

    private void showGlassPane() {
        if (!glassPane.isVisible()) {
            glassPane.setVisible(true);
            FadeTransition ft = new FadeTransition(Duration.millis(300), glassPane);
            ft.setFromValue(0);
            ft.setToValue(1);
            ft.play();
        }
    }

    private void hideGlassPane() {
        if (glassPane.isVisible()) {
            FadeTransition ft = new FadeTransition(Duration.millis(300), glassPane);
            ft.setFromValue(1);
            ft.setToValue(0);
            ft.setOnFinished(e -> glassPane.setVisible(false));
            ft.play();
        }
    }

    private boolean isMouseOverGlassPane(MouseEvent event) {
        Bounds bounds = glassPane.localToScene(glassPane.getBoundsInLocal());
        double mouseX = event.getSceneX();
        double mouseY = event.getSceneY();
        return mouseX >= bounds.getMinX() && mouseX <= bounds.getMaxX()
                && mouseY >= bounds.getMinY() && mouseY <= bounds.getMaxY();
    }
    // Pour garder une référence aux emails réels (pour retrouver les objets Email)
    private java.util.List<Email> emailsRecusList;
    private java.util.List<Email> emailsEnvoyesList;

    public void setEmailConnecte(String email) {
        welcomeLabel.setText("Bienvenue, " + email);
        boiteEmail = new BoiteEmail(email);
        chargerEmails();
        setupListeners();
    }

    private void chargerEmails() {
        boiteEmail.chargerEmails();

        // Copier les listes pour éviter l’accumulation si la méthode getEmails... retourne la même instance
        emailsRecusList = new ArrayList<>(boiteEmail.getEmailsRecus());
        emailsEnvoyesList = new ArrayList<>(boiteEmail.getEmailsEnvoyes());

        // Vider les ListView avant de les remplir
        listEmailsRecus.getItems().clear();
        for (Email e : emailsRecusList) {
            listEmailsRecus.getItems().add(formatEmailPourAffichage(e));
        }

        listEmailsEnvoyes.getItems().clear();
        for (Email e : emailsEnvoyesList) {
            listEmailsEnvoyes.getItems().add(formatEmailPourAffichage(e));
        }
    }



    private void setupListeners() {
        listEmailsRecus.getSelectionModel().selectedIndexProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null && newSelection.intValue() >= 0) {
                afficherDetailsEmail(emailsRecusList.get(newSelection.intValue()));
                // Désélectionner l'autre liste
                listEmailsEnvoyes.getSelectionModel().clearSelection();
            }
        });

        listEmailsEnvoyes.getSelectionModel().selectedIndexProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null && newSelection.intValue() >= 0) {
                afficherDetailsEmail(emailsEnvoyesList.get(newSelection.intValue()));
                // Désélectionner l'autre liste
                listEmailsRecus.getSelectionModel().clearSelection();
            }
        });
    }

    private void afficherDetailsEmail(Email email) {
        labelExpediteur.setText("De : " + (email.getExpediteur() != null ? email.getExpediteur() : "Inconnu"));
        labelDestinataires.setText("À : " + String.join(", ", email.getDestinataires()));
        labelSujet.setText("Sujet : " + email.getSujet());
        textAreaCorps.setText(email.getCorps());

        try {
            Connection conn = DatabaseConnection.getConnection();
            EmailClassifierDB classifier = new EmailClassifierDB(conn);
            String categorie = classifier.devinerCategorie(email.getSujet(), email.getCorps());
            labelCategorie.setText("Catégorie : " + categorie);
            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
            labelCategorie.setText("Catégorie : (erreur)");
        }
    }



    private String formatEmailPourAffichage(Email e) {
        return e.getDateEnvoi().toLocalDate().toString() + " | " +
               (e.getExpediteur() != null ? e.getExpediteur() : String.join(", ", e.getDestinataires())) +
               " | " + e.getSujet();
    }
    
    
    
   
    @FXML
    private void handleAfficherRechercheEmail() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("Recherche.fxml"));
            Parent root = loader.load();

            Scene scene = new Scene(root);

            // Lier le style moderne
            scene.getStylesheets().add(getClass().getResource("recherchestyle.css").toExternalForm());

            Stage stage = new Stage();
            stage.setTitle("Recherche d'Emails");
            stage.setScene(scene);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleOuvrirArchivage() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("Archivage.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle("Archivage des emails");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


   

    @FXML
    private void handleEnvoyerML() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("envoyerMailingList.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setTitle("Envoyer à une mailing list");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("❌ Impossible d’ouvrir la fenêtre d’envoi à une mailing list.");
        }
    }
    
    
   
}