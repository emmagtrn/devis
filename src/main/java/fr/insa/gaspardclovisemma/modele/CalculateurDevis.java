package fr.insa.gaspardclovisemma.modele;

import fr.insa.gaspardclovisemma.materiaux.Revetement;
import java.util.HashMap;
import java.util.Map;

public class CalculateurDevis {

    public static String genererFactureDetaillee(Batiment batiment) {
        // Dictionnaire pour cumuler les mètres carrés par type de matériau
        Map<Revetement, Double> surfacesParRevetement = new HashMap<>();
        double devisTotal = 0.0;

        // Descente de l'arborescence en cascade
        for (Niveau niveau : batiment.getNiveaux()) {
            for (Appartement appart : niveau.getAppartements()) {
                for (Piece piece : appart.getPieces()) {
                    
                    // PARCOURS DES MURS
                    for (Mur mur : piece.getMurs()) {
                        // La surface nette est identique pour les deux côtés du mur
                        double surfaceNette = mur.BlanketSurfaceNette(niveau.getHauteurSousPlafond());
                        
                        // Traitement du Côté 1
                        Revetement r1 = mur.getRevetementCote1();
                        if (r1 != null) {
                            surfacesParRevetement.put(r1, surfacesParRevetement.getOrDefault(r1, 0.0) + surfaceNette);
                        }
                        
                        // Traitement du Côté 2 
                        Revetement r2 = mur.getRevetementCote2();
                        if (r2 != null) {
                            surfacesParRevetement.put(r2, surfacesParRevetement.getOrDefault(r2, 0.0) + surfaceNette);
                        }
                    }

                    // Facturation des Sols et Plafonds de la pièce
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

        // Génération du livrable textuel
        StringBuilder facture = new StringBuilder();
        facture.append("=== DEVIS DU BATIMENT : ").append(batiment.getId()).append(" ===\n\n");
        
        for (Map.Entry<Revetement, Double> entree : surfacesParRevetement.entrySet()) {
            double prix = entree.getValue() * entree.getKey().getPrixM2();
            devisTotal += prix;
            facture.append(String.format("- %s (%.2f m²) : %.2f €\n", entree.getKey().getNom(), entree.getValue(), prix));
        }
        
        facture.append("-----------------------------------\n");
        facture.append(String.format("TOTAL GLOBAL : %.2f €\n", devisTotal));
        
        return facture.toString();
    }

    private static double calculerSurfacePieceRectangulaire(Piece p) {
        if (p.getMurs().isEmpty()) return 0.0;
        double minX = Double.MAX_VALUE, maxX = -Double.MAX_VALUE, minY = Double.MAX_VALUE, maxY = -Double.MAX_VALUE;
        for (Mur m : p.getMurs()) {
            minX = Math.min(minX, Math.min(m.getDebut().getX(), m.getFin().getX()));
            maxX = Math.max(maxX, Math.max(m.getDebut().getX(), m.getFin().getX()));
            minY = Math.min(minY, Math.min(m.getDebut().getY(), m.getFin().getY()));
            maxY = Math.max(maxY, Math.max(m.getDebut().getY(), m.getFin().getY()));
        }
        return (maxX - minX) * (maxY - minY);
    }
}