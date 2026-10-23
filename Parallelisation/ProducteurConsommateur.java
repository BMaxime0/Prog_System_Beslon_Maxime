import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicInteger;

public class ProducteurConsommateur {

    private static final int TAILLE_BUFFER = 5;
    private final Queue<Integer> buffer = new LinkedList<>();

    // Étape 1 : Déclaration des 3 sémaphores
    private final Semaphore placesLibres = new Semaphore(TAILLE_BUFFER); // 5 places initiales
    private final Semaphore elementsDisponibles = new Semaphore(0);       // 0 élément au départ
    private final Semaphore mutexBuffer = new Semaphore(1);              // Exclusion mutuelle (1 seul accès)

    // Compteurs atomiques pour le monitoring
    private final AtomicInteger totalProduit = new AtomicInteger(0);
    private final AtomicInteger totalConsomme = new AtomicInteger(0);

    // Étape 2 : Classe Producteur
    class Producteur implements Runnable {
        private final int id;
        private final int nbArticles;

        public Producteur(int id, int nbArticles) {
            this.id = id;
            this.nbArticles = nbArticles;
        }

        @Override
        public void run() {
            for (int i = 0; i < nbArticles; i++) {
                int produit = id * 100 + i;
                try {
                    // 1. Réserver une place libre (bloque si plein)
                    placesLibres.acquire();

                    // 2 & 4. Acquérir / Libérer l'accès exclusif au buffer
                    mutexBuffer.acquire();
                    try {
                        // 3. Ajout réel
                        buffer.add(produit);
                        int count = totalProduit.incrementAndGet();
                        System.out.printf("[Prod %d] Produit : %-3d | Buffer : %d/%d | Total produit : %d%n",
                                id, produit, buffer.size(), TAILLE_BUFFER, count);
                    } finally {
                        mutexBuffer.release();
                    }

                    // 5. Signaler qu'un élément est disponible
                    elementsDisponibles.release();

                    Thread.sleep((long) (Math.random() * 50));
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        }
    }

    // Étape 3 : Classe Consommateur
    class Consommateur implements Runnable {
        private final int id;
        private final int nbArticles;

        public Consommateur(int id, int nbArticles) {
            this.id = id;
            this.nbArticles = nbArticles;
        }

        @Override
        public void run() {
            for (int i = 0; i < nbArticles; i++) {
                try {
                    // 1. Attendre qu'un élément soit disponible (bloque si vide)
                    elementsDisponibles.acquire();

                    // 2 & 4. Acquérir / Libérer l'accès exclusif au buffer
                    mutexBuffer.acquire();
                    Integer produit;
                    try {
                        // 3. Retrait réel
                        produit = buffer.poll();
                        int count = totalConsomme.incrementAndGet();
                        System.out.printf("  [Conso %d] Consommé : %-3d | Buffer : %d/%d | Total consommé : %d%n",
                                id, produit, buffer.size(), TAILLE_BUFFER, count);
                    } finally {
                        mutexBuffer.release();
                    }

                    // 5. Signaler qu'une place est libre
                    placesLibres.release();

                    Thread.sleep((long) (Math.random() * 100));
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        }
    }

    public void exécuter(int nbProds, int itemsParProd, int nbConsos, int itemsParConso) throws InterruptedException {
        Thread[] prods = new Thread[nbProds];
        Thread[] consos = new Thread[nbConsos];

        for (int i = 0; i < nbProds; i++) prods[i] = new Thread(new Producteur(i + 1, itemsParProd));
        for (int i = 0; i < nbConsos; i++) consos[i] = new Thread(new Consommateur(i + 1, itemsParConso));

        for (Thread t : prods) t.start();
        for (Thread t : consos) t.start();

        for (Thread t : prods) t.join();
        for (Thread t : consos) t.join();

        System.out.printf("%n=== Bilan : Produit = %d | Consommé = %d | En buffer = %d ===%n%n",
                totalProduit.get(), totalConsomme.get(), buffer.size());
    }
}