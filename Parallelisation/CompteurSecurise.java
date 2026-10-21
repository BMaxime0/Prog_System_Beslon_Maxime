public class CompteurSecurise {
    private static long compteurGlobal = 0;  // Variable partagée MODIFIABLE
    private static Object verrou = new Object();

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
                synchronized(verrou) {
                    compteurGlobal++;  // PAS RACE CONDITION ICI !
                }
            }
            System.out.println(nom + " terminé. Compteur vu : " + compteurGlobal);
        }
    }

    public static long getCompteur() {
        synchronized(verrou) {
            return compteurGlobal;
        }

    }

    public static void resetCompteur() {
        synchronized(verrou){
            compteurGlobal = 0;
        }

    }
}