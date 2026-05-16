package fr.insa.gaspardclovisemma.modele;

import fr.insa.gaspardclovisemma.materiaux.Revetement;
import java.util.HashMap;
import java.util.Map;

public class CalculateurDevis {

    // La méthode prend maintenant le Bâtiment entier en paramètre !
    public static String genererFactureDetaillee(Batiment batiment) {
        Map<Revetement, Double> surfacesParRevetement = new HashMap<>();
        double devisTotal = 0.0;

        // 1. On fouille dans toute l'arborescence
        for (Niveau niveau : batiment.getNiveaux()) {
            for (Appartement appart : niveau.getAppartements()) {
                for (Piece piece : appart.getPieces()) {
                    
                    // A. Calcul pour les Murs de la pièce
                    for (Mur mur : piece.getMurs()) {
                        Revetement revMur = mur.getRevetement();
                        if (revMur != null) {
                            double surfaceMur = mur.calculerSurface(niveau.getHauteurSousPlafond());
                            surfacesParRevetement.put(revMur, surfacesParRevetement.getOrDefault(revMur, 0.0) + surfaceMur);
                        }
                    }

                    // B. Calcul pour le Sol et le Plafond
                    double surfaceSol = calculerSurfacePieceRectangulaire(piece);
                    
                    Revetement revSol = piece.getRevetementSol();
                    if (revSol != null) {
                        surfacesParRevetement.put(revSol, surfacesParRevetement.getOrDefault(revSol, 0.0) + surfaceSol);
                    }
                    
                    Revetement revPlafond = piece.getRevetementPlafond();
                    if (revPlafond != null) {
                        surfacesParRevetement.put(revPlafond, surfacesParRevetement.getOrDefault(revPlafond, 0.0) + surfaceSol);
                    }
                }
            }
        }

        // 2. Construction du texte de la facture
        StringBuilder facture = new StringBuilder();
        facture.append("=== DEVIS DU BATIMENT : ").append(batiment.getId()).append(" ===\n\n");

        for (Map.Entry<Revetement, Double> entree : surfacesParRevetement.entrySet()) {
            Revetement rev = entree.getKey();
            double surface = entree.getValue();
            double prix = surface * rev.getPrixM2();
            devisTotal += prix;

            facture.append(String.format("- %s (%.2f m²) : %.2f €\n", rev.getNom(), surface, prix));
        }

        facture.append("-----------------------------------\n");
        facture.append(String.format("TOTAL GLOBAL : %.2f €\n", devisTotal));

        return facture.toString();
    }

    // Petite méthode utilitaire pour trouver la surface au sol en regardant les coordonnées des murs
    private static double calculerSurfacePieceRectangulaire(Piece p) {
        if (p.getMurs().isEmpty()) return 0.0;
        double minX = Double.MAX_VALUE, maxX = -Double.MAX_VALUE;
        double minY = Double.MAX_VALUE, maxY = -Double.MAX_VALUE;
        
        for (Mur m : p.getMurs()) {
            minX = Math.min(minX, Math.min(m.getDebut().getX(), m.getFin().getX()));
            maxX = Math.max(maxX, Math.max(m.getDebut().getX(), m.getFin().getX()));
            minY = Math.min(minY, Math.min(m.getDebut().getY(), m.getFin().getY()));
            maxY = Math.max(maxY, Math.max(m.getDebut().getY(), m.getFin().getY()));
        }
        return (maxX - minX) * (maxY - minY);
    }
}