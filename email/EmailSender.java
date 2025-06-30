package email;

import java.util.Properties;
import jakarta.mail.*;
import jakarta.mail.internet.*;

public class EmailSender {

    public static void main(String[] args) {

        // 1. Configuration SMTP du fournisseur (ex: Gmail)
        Properties props = new Properties();
        props.put("mail.smtp.host", "smtp.gmail.com");          // Serveur SMTP Gmail
        props.put("mail.smtp.port", "587");                     // Port TLS
        props.put("mail.smtp.auth", "true");                    // Authentification requise
        props.put("mail.smtp.starttls.enable", "true");         // Activation TLS

        // 2. Authentification avec ton email et mot de passe d'application
        // cad si le email et le mdp ne sont pas correcte (cad les vrais que tu utilise reelement avec le vrai gmail)
        // ca va pas marcher !
        Session session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication("doharisse@gmail.com", "hkqi urom njih zbip");
            }
        });

        try {
            // 3. Création du message
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress("doharisse@gmail.com"));
            message.setRecipients(
                Message.RecipientType.TO, InternetAddress.parse("doharisse@gmail.com"));
            message.setSubject("motivation  usf");
            message.setText("acknowledge pain ");

            // 4. Envoi du message
            Transport.send(message);

            System.out.println("Email envoyé avec succès !");

        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }
}
