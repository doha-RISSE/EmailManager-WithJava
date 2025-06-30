package email;



import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MailingListDAO {

	public void CreateCompleteMailingList(MailingList list) {
	    int id = getMailingListIdByName(list.getNom());

	    if (id == -1) {
	        try (Connection conn = DatabaseConnection.getConnection()) {
	            System.out.println("📧 Création d'une nouvelle mailing list : " + list.getNom());
	            AdresseEmailDAO a = new AdresseEmailDAO(conn);
	            id = insertNomMailing_list(list.getNom(), conn);

	            for (String adress : list.getAdressesEmail()) {
	                int id_adress = a.getOrCreateId(adress);
	                mappingMlAddress(conn, id, id_adress);
	                System.out.println(" Adresse ajoutée à la ML : " + adress);
	            }

	        } catch (SQLException e) {
	            System.err.println("❌ Erreur lors de la création de la nouvelle mailing list : " + e.getMessage());
	        }

	    } else {
	        try (Connection conn = DatabaseConnection.getConnection()) {
	            System.out.println("La mailing list existe déjà, ajout des nouvelles adresses si besoin.");
	            AdresseEmailDAO a = new AdresseEmailDAO(conn);
	            List<String> ls = new ArrayList<>();

	            String add_ml = "SELECT a.adresse FROM mailing_list_adresse m JOIN adresse_email a ON m.id_adresse = a.id_adresse WHERE m.id_liste = ?";
	            PreparedStatement ps = conn.prepareStatement(add_ml);
	            ps.setInt(1, id);
	            ResultSet rs = ps.executeQuery();
	            while (rs.next()) {
	                ls.add(rs.getString("adresse"));
	            }

	            for (String address : list.getAdressesEmail()) {
	                if (!ls.contains(address)) {
	                    int id_ADD = a.getOrCreateId(address);
	                    mappingMlAddress(conn, id, id_ADD);
	                    System.out.println(" Nouvelle adresse ajoutée à la ML : " + address);
	                } else {
	                    System.out.println("Adresse déjà présente, ignorée : " + address);
	                }
	            }

	        } catch (SQLException e) {
	            System.err.println("❌ Erreur lors de la mise à jour d'une ML existante : " + e.getMessage());
	        }
	    }
	}


    public int getMailingListIdByName(String nom) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = "SELECT id_liste FROM mailing_list WHERE nom = ?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, nom);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getInt("id_liste");
        } catch (SQLException e) {
 	        System.err.println("❌ Erreur de la recuperation de l'ID de la MailingList par son nom'");

        }
        return -1; // pas trouvé
    }
    															//mailing_list
    public int insertNomMailing_list(String nom, Connection conn) {
        try { // prc ce que on a passage par adress ,donc si on ferme depuis ici conn ,on peut plus lutilier dans les autre
        	// methode car elle a ete fermee
            String sql = "INSERT INTO mailing_list (nom) VALUES (?)";
            PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, nom);
            ps.executeUpdate();
            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) return rs.getInt(1);
            else return -1;
        } catch (SQLException e) {
            System.err.println("❌ Erreur SQL de lajout du nom de la mailing liste: " + e.getMessage());
            return -1;
        }
        
    }
    
    public void mappingMlAddress(Connection conn,int id_ml,int id_add) {
    	try {
			String add_ml_adress="INSERT INTO mailing_list_adresse(id_liste,id_adresse) VALUES (?,?)";
			PreparedStatement ps2=conn.prepareStatement(add_ml_adress);
    		ps2.setInt(1,id_ml);
    		ps2.setInt(2, id_add);
            ps2.executeUpdate(); 


    	}
    	catch (SQLException e) {
            System.err.println("❌ Erreur SQL lors du mapping id_liste=" + id_ml + ", id_adresse=" + id_add + " : " + e.getMessage());
 	        return;
 	    }
    }
    
    
    

}