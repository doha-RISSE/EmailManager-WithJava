package email;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Properties;

import jakarta.mail.*;
import jakarta.mail.internet.*;
public class EnvoiEmail {
	public EnvoiEmail() {}
	
	
	public int envoyerEmail(Email email, String motDePasseApp) {
		
		
	    String from = email.getExpediteur();
	    String smtpHost;
	    int smtpPort = 587;

	    if (from.endsWith("@gmail.com")) {
	        smtpHost = "smtp.gmail.com";
	    } else if (from.matches(".*@(outlook|hotmail|live)\\.(com|fr)")) {
	        smtpHost = "smtp.office365.com";
	    } else {
	        throw new RuntimeException("Fournisseur non supporté : " + from);
	    }
	    Properties props = new Properties();
	    props.put("mail.smtp.host", smtpHost);
	    props.put("mail.smtp.port", smtpPort);
	    props.put("mail.smtp.auth", "true");
	    props.put("mail.smtp.starttls.enable", "true");

	    Session session = Session.getInstance(props, new Authenticator() {
	        protected PasswordAuthentication getPasswordAuthentication() {
	            return new PasswordAuthentication(from, motDePasseApp);
	        }
	    });

	    
	    
	    
	    
	   
	    
	 // 1. Classifier l'email
	    try (Connection conn = DatabaseConnection.getConnection()) {
	        EmailClassifierDB classifier = new EmailClassifierDB(conn);
	        String categorie = classifier.devinerCategorie(email.getSujet(), email.getCorps());
	        email.setCategorie(categorie);
	    } catch (SQLException e) {
	        e.printStackTrace();
	        email.setCategorie("autre"); // Catégorie par défaut en cas d'erreur
	    }

	    
	    
	    
	    
	    // Créer l'entrée email en BDD **avant** la boucle 
	    int idEmail = creerconeoin(email);
	    if (idEmail == -1) { // si lemail nest pas inseree ,donc on nenvoi pas lemail 
	        return -1;
	    }

	    for (String dest : email.getDestinataires()) {
	        try {
	            MimeMessage message = new MimeMessage(session);
	            message.setFrom(new InternetAddress(from));
	            message.setRecipient(Message.RecipientType.TO, new InternetAddress(dest));
	            message.setSubject(email.getSujet());

	            MimeMultipart multipart = new MimeMultipart();

	            MimeBodyPart textPart = new MimeBodyPart();
	            textPart.setText(email.getCorps());
	            multipart.addBodyPart(textPart);

	            for (String pieceJointe : email.getCheminsPieceJointe()) {
	                try {
	                    MimeBodyPart attachmentPart = new MimeBodyPart();
	                    attachmentPart.attachFile(pieceJointe);
	                    multipart.addBodyPart(attachmentPart);
	                } catch (IOException e) {
	                    System.err.println("Erreur avec la pièce jointe : " + pieceJointe);
	                    e.printStackTrace();
	                }
	            }

	            message.setContent(multipart);

	            Transport.send(message);
	            System.out.println("Envoyé à " + dest);

	        } catch (MessagingException e) {
	            System.err.println("Échec pour " + dest + ": " + e.getMessage());
	            // continue à envoyer aux autres destinataires même s'il y a une erreur
	        }
	    }

	    return idEmail;  // On retourne une seule fois l'ID de l'email 
	}

	
	
	
	
	
	
	
	
	
	
	
	public int creerconeoin(Email email) {
	    try (Connection conn = DatabaseConnection.getConnection()) {
	        EmailDAO emailDAO = new EmailDAO(conn);
	        AdresseEmailDAO adresseDAO = new AdresseEmailDAO(conn);
	        int idEmail = emailDAO.insertEmail(email);
	        return idEmail;
	    } catch (SQLException e) {
	        e.printStackTrace();
	        return -1; // valeur par défaut si erreur (à adapter selon ton système)
	    }
	}


}