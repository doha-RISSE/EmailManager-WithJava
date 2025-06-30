package email;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class EmailClassifierDB {
    private Connection conn;

    public EmailClassifierDB(Connection conn) {
        this.conn = conn;
    }

    public String devinerCategorie(String sujet, String corps) throws SQLException {
        String contenu = (sujet + " " + corps).toLowerCase();
        String[] mots = contenu.split("\\W+"); // séparation en mots simples

        String sql = "SELECT categorie FROM mot_cle_categorie WHERE mot_cle = ?";
        Map<String, Integer> compteur = new HashMap<>();

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            for (String mot : mots) {
                ps.setString(1, mot);
                ResultSet rs = ps.executeQuery();
                while (rs.next()) {
                    String cat = rs.getString("categorie");
                    compteur.put(cat, compteur.getOrDefault(cat, 0) + 1);
                }
            }
        }

        // Choisir la catégorie la plus fréquente
        return compteur.entrySet().stream()
            .max(Map.Entry.comparingByValue())
            .map(Map.Entry::getKey)
            .orElse("autre");
    }
}
