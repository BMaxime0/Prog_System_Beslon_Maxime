public class Calcule extends Thread {
    // TODO: Déclarer les attributs nécessaires

    int valeur, multiplicateur, index;
    long resultat;

    public Calcule(int valeur, int multiplicateur, int index) {
        this.valeur = valeur;
        this.multiplicateur = multiplicateur;
        this.index = index;
        this.resultat = 0;
    }

    @Override
    public void run() {
        // TODO: Implémenter le calcul intensif
        // Même logique que dans la boucle interne de CalculSequentiel

        resultat = 0;
        for (int j = 0; j < multiplicateur; j++) {
            resultat += valeur * valeur + valeur;
        }
    }

    public long getResultat() {
        return this.resultat;
    }

    public int getValeur() {
        return this.valeur;
    }
}