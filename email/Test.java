package email;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Test {

	public static void main(String[] args) {
	
		
		/*
		
		List<String> destinaires=new ArrayList<String>();
		destinaires.add("doha_risse@um5.ac.ma");
		// il faut que le chemin a les slaches de cette facon : /
		

	
		
		Email monmail=new Email(7,"test si ca va marcher","jespere que ca arrivera","doharisse@gmail.com",destinaires,dateEnvoi,piecesJointes);
		EnvoiEmail app=new EnvoiEmail();
		app.envoyerEmail(monmail,"kvfv ytjk mqdk jcmw");
		*/
		LocalDateTime dateEnvoi = LocalDateTime.now();
        List<String> piecesJointes=new ArrayList<String>();
        piecesJointes.add("C:\\Users\\HP\\Desktop\\photo-1419833173245-f59e1b93f9ee.jpeg");

	
		
		ArrayList<String> liste_email=new ArrayList<String>();
		liste_email.add("doha_risse@um5.ac.ma");
		liste_email.add("doha.driss199021@gmail.com");
		liste_email.add("doharisse@gmail.com");
		
		MailingList ml=new MailingList("ami-ensam",liste_email);
		
		EmailWithMailingList email_=new EmailWithMailingList("rdv", "rdv demain pour voir les rapport et statistiques", "doharisse@gmail.com",ml,dateEnvoi,piecesJointes);
		
		EnvoiEmail app=new EnvoiEmail();
		//app.envoyerEmailWithMailingList(email_,"kvfv ytjk mqdk jcmw");
	
		
		/*
		BoiteEmail maboite1=new BoiteEmail("doha.driss199021@gmail.com");
		maboite1.chargerEmails();
		List<Email> mes_emails_recus=maboite1.getEmailsRecus();
		for(Email mail_recu:mes_emails_recus) {
			System.out.println(mail_recu.toString());
		}
		
		
		BoiteEmail maboite2=new BoiteEmail("doharisse@gmail.com");
		maboite2.chargerEmails();
		List<Email> mes_emails_envoyes=maboite2.getEmailsEnvoyes();
		for(Email mail_envoyes:mes_emails_envoyes) {
			System.out.println(mail_envoyes.toString());
		}
		
		*/

	}

}
