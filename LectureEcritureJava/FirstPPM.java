import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class FirstPPM {
    public static void main(String[] args) {
        try {
            FileWriter writer = new FileWriter("FirstPPM.ppm", StandardCharsets.UTF_8);

            writer.write("P3\n");
            writer.write("255\n");
            writer.write("3 2\n");
            writer.write("255 0 0  0 255 0  0  0 255 ");
            writer.write("255 255 0 255 255 255 0 0 0");
            // Écriture de la valeur maximal
            // Écriture des pixels
            // Première ligne : rouge, vert, bleu
            // Deuxième ligne : jaune, blanc, noir

            writer.close(); // Fermeture du fichier

            System.out.println("Image PPM créée avec succès !");
        } catch (IOException e) {
            System.err.println("Erreur lors de l'écriture du fichier : " + e.getMessage());
        }
    }
}
