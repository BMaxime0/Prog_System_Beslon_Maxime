public class TestMutex {

    static final int NB_THREADS = 90;
    static final int RES1THREAD = 1111111;
    static final int NB_ESSAIS = 1000;

    public static void main(String[] args) throws InterruptedException {
        int attendu = NB_THREADS * RES1THREAD;
        int nbCorrects = 0;
        long min = Integer.MAX_VALUE;
        long max = Integer.MIN_VALUE;

        for (int essai = 0; essai < NB_ESSAIS; essai++) {
            CompteurSecurise.resetCompteur();

            Thread[] threads = new Thread[NB_THREADS];
            for (int i = 0; i < NB_THREADS; i++) {
                threads[i] = new Thread(new CompteurSecurise.Incrementeur("T" + i, RES1THREAD));
            }

            for (int actuelleS = 0; actuelleS < threads.length; actuelleS++) {
                threads[actuelleS].start();
            }
            for (int actuelleF = 0; actuelleF < threads.length; actuelleF++) {
                threads[actuelleF].join();
            }

            long resultat = CompteurSecurise.getCompteur();
            //System.out.println("Essai " + essai + " : " + resultat + " (attendu " + attendu + ")");

            if (resultat == attendu) {
                nbCorrects++;
            }
            min = Math.min(min, resultat);
            max = Math.max(max, resultat);
        }

        System.out.println("\nCorrects : " + nbCorrects + "/" + NB_ESSAIS
                + " (" + (100.0 * nbCorrects / NB_ESSAIS) + " %)");
        System.out.println("Min : " + min + " | Max : " + max);
    }
}
