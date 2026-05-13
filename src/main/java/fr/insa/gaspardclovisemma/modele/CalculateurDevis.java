package fr.insa.gaspardclovisemma.modele;

import fr.insa.gaspardclovisemma.materiaux.Revetement;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CalculateurDevis {

    // Regroupe les surfaces par type de revêtement
    public static String genererFactureDetaillee(List<Mur> listeMurs, double hauteurPlafond) {
        Map<Revetement, Double> surfacesParRevetement = new HashMap<>();
        double devisTotal = 0.0;

        // 1. Calcul des surfaces totales par revêtement
        for (Mur mur : listeMurs) {
            Revetement rev = mur.getRevetement();
            if (rev != null) {
                double surfaceNette = mur.calculerSurface(hauteurPlafond);
                surfacesParRevetement.put(rev, surfacesParRevetement.getOrDefault(rev, 0.0) + surfaceNette);
            }
        }

        // 2. Construction du texte du devis selon les exigences de l'Étape 1
        StringBuilder facture = new StringBuilder();
        facture.append("=== DÉTAIL DU DEVIS (Par Revêtement) ===\n\n");

        for (Map.Entry<Revetement, Double> entree : surfacesParRevetement.entrySet()) {
            Revetement rev = entree.getKey();
            double surfaceTotale = entree.getValue();
            double prixTotalRevetement = surfaceTotale * rev.getPrixM2();
            devisTotal += prixTotalRevetement;

            facture.append(String.format("Revêtement : %s\n", rev.getNom()));
            facture.append(String.format(" - Surface totale : %.2f m²\n", surfaceTotale));
            facture.append(String.format(" - Prix total : %.2f €\n\n", prixTotalRevetement));
        }

        facture.append("-----------------------------------\n");
        facture.append(String.format("DEVIS TOTAL GLOBAL : %.2f €\n", devisTotal));

        return facture.toString();
    }
}