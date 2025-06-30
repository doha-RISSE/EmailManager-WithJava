package email;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.List;

public class EmailDAO {
    private Connection conn;
    private AdresseEmailDAO adresseEmailDAO;

    public EmailDAO(Connection conn) {
        this.conn = conn;
        this.adresseEmailDAO = new AdresseEmailDAO(conn);
    }

    public int insertEmail(Email email) throws SQLException {
        conn.setAutoCommit(false);
        try {
            // 1. Récupérer l'ID de l'expéditeur
            int idExpediteur = adresseEmailDAO.getOrCreateId(email.getExpediteur());

            // 2. Insérer email dans table email
           String insertEmailSql = "INSERT INTO email (sujet, corps, date_envoi, id_expediteur, categorie) VALUES (?, ?, ?, ?, ?)";

            int idEmail;
            try (PreparedStatement ps = conn.prepareStatement(insertEmailSql, Statement.RETURN_GENERATED_KEYS)) {
                ps.setString(1, email.getSujet());
                ps.setString(2, email.getCorps());
                ps.setTimestamp(3, Timestamp.valueOf(email.getDateEnvoi()));
                ps.setInt(4, idExpediteur);
                ps.setString(5, email.getCategorie());
                ps.executeUpdate();

                ResultSet rs = ps.getGeneratedKeys();
                if (rs.next()) {
                    idEmail = rs.getInt(1);   // on recupere l'id de lemail que mysql a cree ,puisque on lutilisera 
                    // dans les prochaines insertion 
                } else {
                    throw new SQLException("Echec insertion email");
                }
            }

            // 3. Insérer destinataires dans table destinataire
            String insertDestSql = "INSERT INTO destinataire (id_email, id_destinataire) VALUES (?, ?)";
            try (PreparedStatement ps = conn.prepareStatement(insertDestSql)) {
                for (String dest : email.getDestinataires()) {
                    int idDest = adresseEmailDAO.getOrCreateId(dest); 
                    ps.setInt(1, idEmail);
                    ps.setInt(2, idDest);
                    ps.addBatch();
                }
                ps.executeBatch();
            }

            // 4. Insérer pièces jointes dans piece_jointe et liaison dans email_piece_jointe
            String insertPieceSql = "INSERT INTO piece_jointe (chemin) VALUES (?)";
            String insertLiaisonSql = "INSERT INTO email_piece_jointe (id_email, id_piece) VALUES (?, ?)";
            try (PreparedStatement psPiece = conn.prepareStatement(insertPieceSql, Statement.RETURN_GENERATED_KEYS);
                 PreparedStatement psLiaison = conn.prepareStatement(insertLiaisonSql)) {
                for (String chemin : email.getCheminsPieceJointe()) {
                    psPiece.setString(1, chemin);
                    psPiece.executeUpdate();
                    ResultSet rs = psPiece.getGeneratedKeys();
                    if (rs.next()) {
                        int idPiece = rs.getInt(1); // on insere dabord la piece jointe dans la table piece_jointe , on recupere l'id que 
                        //mysql a generee pour cet enregistrment ,puis on peut maintenant inserer un enregistrement dans la table qui lie
                        // chaque email et ses pieces jointes
                        psLiaison.setInt(1, idEmail);
                        psLiaison.setInt(2, idPiece);
                        psLiaison.addBatch();
                    }
                }
                psLiaison.executeBatch();
            }

            conn.commit();
            return idEmail;
        } catch (SQLException e) {
            conn.rollback();
            throw e;
        } finally {
            conn.setAutoCommit(true);
        }
    }
}
