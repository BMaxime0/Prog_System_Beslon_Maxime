public class CalculParallele {

    private static final int[] DONNEES = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10};
    private static Calcule[] threads = new Calcule[DONNEES.length];

    public static void main(String[] args) {

        System.out.println("=== Calcul Séquentiel ===");

        long debut = System.nanoTime();
        long sommeTotal = 0;

        for (int i = 0; i < threads.length; i++) {
            threads[i] = new Calcule(DONNEES[i], 1000, i);
            threads[i].start();
        }

        try {
            for (int i = 0; i < threads.length; i++) {
                threads[i].join();
                sommeTotal += threads[i].getResultat();
                System.out.println("Traitement de " + threads[i].getValeur() + " terminé : " + threads[i].getResultat());
            }
        } catch (InterruptedException e) {
            System.out.println("erreur dans la récupération des résultats");
        }

        long duree = (System.nanoTime() - debut) / 1_000_000;
        System.out.println("Résultat total : " + sommeTotal);
        System.out.println("Durée : " + duree + " ms");
    }
}