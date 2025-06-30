package application;

import email.MailingList;
import email.MailingListDAO;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;

public class CreerMailingListController {

    @FXML
    private TextField nomMailingListField;

    @FXML
    private TextField adresseEmailField;

    @FXML
    private ListView<String> listeAdresses;

    private List<String> adresses = new ArrayList<>();

    @FXML
    private void ajouterAdresse() {
        String adresse = adresseEmailField.getText().trim();
        if (!adresse.isEmpty() && !adresses.contains(adresse)) {
            adresses.add(adresse);
            listeAdresses.getItems().add(adresse);
            adresseEmailField.clear();
        }
    }

    @FXML
    private void validerMailingList() {
    	
    	 String nom = nomMailingListField.getText().trim();
         if (nom.isEmpty() || adresses.isEmpty()) {
             Alert alert = new Alert(Alert.AlertType.ERROR, "Nom manquant.");
             alert.show();
             return;
         }
         MailingList ml=new MailingList(nom, adresses);
         MailingListDAO mld=new MailingListDAO();
         mld.CreateCompleteMailingList(ml);
         
      // Fermer la fenêtre après validation
         Stage stage = (Stage) nomMailingListField.getScene().getWindow();
         stage.close();

         
         
    }
    
    
    
    
}
