package application;


public class Session {
    private static String utilisateurConnecte;
    private static String motDePasse;

    public static void setUtilisateurConnecte(String email) {
        utilisateurConnecte = email;
    }

    public static String getUtilisateurConnecte() {
        return utilisateurConnecte;
    }

    public static void setMotDePasse(String mdp) {
        motDePasse = mdp;
    }

    public static String getMotDePasse() {
        return motDePasse;
    }
}
