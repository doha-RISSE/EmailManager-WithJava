package email;

import java.util.Properties;

import jakarta.mail.Authenticator;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.Session;
import jakarta.mail.Transport;

public class SMTPService {
    public static boolean verifierConnexion(String email, String motDePasse) {
        try {
            Properties props = new Properties();
            props.put("mail.smtp.auth", "true");
            props.put("mail.smtp.starttls.enable", "true");
            props.put("mail.smtp.host", "smtp.gmail.com"); // ou autre
            props.put("mail.smtp.port", "587");

            Session session = Session.getInstance(props,
                new Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(email, motDePasse);
                    }
                });

            // Test de connexion
            Transport transport = session.getTransport("smtp");
            transport.connect(); // Essaie de se connecter
            transport.close();
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
