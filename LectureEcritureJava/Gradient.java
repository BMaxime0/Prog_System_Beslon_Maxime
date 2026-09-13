public class Gradient {

    public static void main(String[] args) {
        Image img = new Image(200, 100);

        // Génération du dégradé de bleu
        System.out.print("" + img.getHeight() + img.getWidth() + "");


        for (int y = 0; y < img.getHeight() ; y++) {

            for (int x = 0; x < img.getWidth() ; x++) {

                int bleu = (int) (x * 0.78); // quel calcule ? = produit en croix ((200*100)/255 = 0.78)
                img.setPixel(x, y, 0, 0, bleu);
            }
        }

        try {
            img.save_txt("gradient.ppm");
            System.out.println("Dégradé créé avec succès !");
        } catch (Exception e) {
            System.err.println("Erreur lors de la création du dégradé : " + e.getMessage());
        }
    }
}