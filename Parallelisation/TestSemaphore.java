// TestSemaphore.java
public class TestSemaphore {
    public static void main(String[] args) throws InterruptedException {
        System.out.println("--- Scénario 1 : Équilibré ---");
        // 2 prods x 15 items = 30 produits | 3 consos x 10 items = 30 consommations
        new ProducteurConsommateur().exécuter(2, 15, 3, 10);

        System.out.println("--- Scénario 2 : Déséquilibré ---");
        // 5 prods x 10 items = 50 produits | 2 consos x 10 items = 20 consommations
        // Les producteurs vont vite bloquer en attendant que le buffer se vide
        new ProducteurConsommateur().exécuter(5, 10, 2, 10);
    }
}