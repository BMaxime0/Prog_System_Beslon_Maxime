public class CompteurDangereux {
    private static long compteurGlobal = 0;  // Variable partagée MODIFIABLE

    static class Incrementeur implements Runnable {
        private final String nom;
        private final int nombreIncrements;

        public Incrementeur(String nom, int nombreIncrements) {
            this.nom = nom;
            this.nombreIncrements = nombreIncrements;
        }

        @Override
        public void run() {
            for (int i = 0; i < nombreIncrements; i++) {
                // Cette ligne pose problème car durant les 3 instauctions exectué par compteurGlobal++
                // plusieurs threads peuvent "LOAD" la variable, l'incrémenter et ainsi renvoyer la même valeur théorique
                compteurGlobal++;  // RACE CONDITION ICI !
            }
            System.out.println(nom + " terminé. Compteur vu : " + compteurGlobal);
        }
    }

    public static long getCompteur() {
        return compteurGlobal;
    }

    public static void resetCompteur() {
        compteurGlobal = 0;
    }
}