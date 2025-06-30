
package email;

import java.sql.*;

public class UtilisateurDAO {

    public static int recupererOuCreerUtilisateur(String nom) throws SQLException {
    	
    	
        String select = "SELECT id_utilisateur FROM utilisateur WHERE nom = ?";
        String insert = "INSERT INTO utilisateur (nom) VALUES (?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement psSelect = conn.prepareStatement(select)) {

            psSelect.setString(1, nom);
            ResultSet rs = psSelect.executeQuery();

            if (rs.next()) {
                return rs.getInt("id_utilisateur");
            } else {
                try (PreparedStatement psInsert = conn.prepareStatement(insert, Statement.RETURN_GENERATED_KEYS)) {
                    psInsert.setString(1, nom);
                    psInsert.executeUpdate();
                    ResultSet keys = psInsert.getGeneratedKeys();
                    if (keys.next()) {
                        return keys.getInt(1);
                    }
                }
            }
        }
        throw new SQLException("Erreur lors de la création de l'utilisateur.");
    }

    public static void associerUtilisateurEtAdresse(int idUtilisateur, int idAdresse) throws SQLException {
        String check = "SELECT * FROM utilisateur_adresse_email WHERE id_utilisateur = ? AND id_adresse = ?";
        String insert = "INSERT INTO utilisateur_adresse_email (id_utilisateur, id_adresse) VALUES (?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement psCheck = conn.prepareStatement(check)) {

            psCheck.setInt(1, idUtilisateur);
            psCheck.setInt(2, idAdresse);
            ResultSet rs = psCheck.executeQuery();

            if (!rs.next()) {
                try (PreparedStatement psInsert = conn.prepareStatement(insert)) {
                    psInsert.setInt(1, idUtilisateur);
                    psInsert.setInt(2, idAdresse);
                    psInsert.executeUpdate();
                }
            }
        }
    }
}
