package email;

import java.sql.*;

public class AdresseEmailDAO {
    private Connection conn;

    public AdresseEmailDAO(Connection conn) {
        this.conn = conn;
    }

    // Cherche l'ID d'une adresse email, ou la crée si elle n'existe pas
    public int getOrCreateId(String email) throws SQLException {
        String selectSql = "SELECT id_adresse FROM adresse_email WHERE adresse = ?";
        try (PreparedStatement ps = conn.prepareStatement(selectSql)) {
            ps.setString(1, email);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt("id_adresse");
            }
        }
        // Si pas trouvé, on insère
        String insertSql = "INSERT INTO adresse_email (adresse) VALUES (?)";
        try (PreparedStatement ps = conn.prepareStatement(insertSql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, email);
            ps.executeUpdate();
            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) {
                return rs.getInt(1);
            } else {
                throw new SQLException("Echec de récupération de l'ID pour " + email);
            }
        }
    }
}
