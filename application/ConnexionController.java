package application;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import java.io.IOException;
import java.util.Properties;
import jakarta.mail.*;

public class ConnexionController {

    @FXML
    private TextField emailField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Label statusLabel;

    @FXML
    private void handleConnexion() {
        String email = emailField.getText();
        String password = passwordField.getText();

        if (email.isEmpty() || password.isEmpty()) {
            statusLabel.setText("Veuillez remplir tous les champs.");
            return;
        }

        boolean success = essayerConnexionSMTP(email, password);
        if (success) {
            // Sauvegarde dans la session
            Session.setUtilisateurConnecte(email);
            Session.setMotDePasse(password);

            statusLabel.setText("Connexion réussie !");
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("Main.fxml"));
                Stage stage = (Stage) emailField.getScene().getWindow(); // fenêtre actuelle
                Scene scene = new Scene(loader.load());

                // Ajout feuille de style
                scene.getStylesheets().add(getClass().getResource("main-style.css").toExternalForm());

                stage.setScene(scene);

                // Récupérer le contrôleur principal et passer l'email
                MainController mainController = loader.getController();
                mainController.setEmailConnecte(email);

            } catch (IOException e) {
                e.printStackTrace();
                statusLabel.setText("Erreur lors du chargement de l'interface principale.");
            }
        } else {
            statusLabel.setText("Échec de la connexion. Vérifiez vos identifiants.");
        }
    }

    private boolean essayerConnexionSMTP(String email, String password) {
        try {
            Properties props = new Properties();
            props.put("mail.smtp.auth", "true");
            props.put("mail.smtp.starttls.enable", "true");

            String domain = email.split("@")[1].toLowerCase();

            if (domain.endsWith("outlook.com") || domain.endsWith("hotmail.com") || domain.endsWith("live.com")
                    || domain.endsWith("outlook.fr") || domain.endsWith("hotmail.fr") || domain.endsWith("live.fr")) {
                props.put("mail.smtp.host", "smtp.office365.com");
            } else {
                props.put("mail.smtp.host", "smtp." + domain);
            }

            props.put("mail.smtp.port", "587");

            jakarta.mail.Session session = jakarta.mail.Session.getInstance(props, new Authenticator() {
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(email, password);
                }
            });

            Transport transport = session.getTransport("smtp");
            transport.connect();
            transport.close();

            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}