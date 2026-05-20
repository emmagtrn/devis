package fr.insa.gaspardclovisemma.modele;

import fr.insa.gaspardclovisemma.materiaux.Revetement;
import java.util.HashMap;
import java.util.Map;

public class CalculateurDevis {

    // Méthode principale qui calcule tout en cascade à travers les objets
    public static String genererFactureDetaillee(Batiment batiment) {
        // Map : Permet de regrouper et d'additionner les surfaces par matériau.
        // La clé est le Revetement, la valeur associée est la surface cumulée en m² (Double).
        Map<Revetement, Double> surfacesParRevetement = new HashMap<>();
        double devisTotal = 0.0;

        // PARCOURS EN CASCADE DU BATIMENT (Le système de poupées russes exigé par le sujet)
        for (Niveau niveau : batiment.getNiveaux()) {
            for (Appartement appart : niveau.getAppartements()) {
                for (Piece piece : appart.getPieces()) {
                    
                    // ALLOCATION A : Calcul de la surface des murs de la pièce
                    for (Mur mur : piece.getMurs()) {
                        Revetement revMur = mur.getRevetement();
                        if (revMur != null) {
                            // Calcul de la surface nette du mur (Longueur * Hauteur sous plafond du niveau)
                            double surfaceMur = mur.calculerSurface(niveau.getHauteurSousPlafond());
                            // getOrDefault(revMur, 0.0) : Récupère la surface déjà calculée pour ce matériau, ou 0 s'il n'existe pas encore
                            surfacesParRevetement.put(revMur, surfacesParRevetement.getOrDefault(revMur, 0.0) + surfaceMur);
                        }
                    }

                    // ALLOCATION B : Calcul automatique du Sol et du Plafond de la pièce
                    double surfaceSol = calculerSurfacePieceRectangulaire(piece);
                    
                    // Si un revêtement de sol est défini, on ajoute la surface au dictionnaire
                    Revetement revSol = piece.getRevetementSol();
                    if (revSol != null) {
                        surfacesParRevetement.put(revSol, surfacesParRevetement.getOrDefault(revSol, 0.0) + surfaceSol);
                    }
                    
                    // Pareil pour le revêtement de plafond
                    Revetement revPlafond = piece.getRevetementPlafond();
                    if (revPlafond != null) {
                        surfacesParRevetement.put(revPlafond, surfacesParRevetement.getOrDefault(revPlafond, 0.0) + surfaceSol);
                    }
                }
            }
        }

        // CONSTRUCTION DU TEXTE DE LA FACTURE (StringBuilder est plus performant que des concaténations avec '+')
        StringBuilder facture = new StringBuilder();
        facture.append("=== DEVIS DU BATIMENT : ").append(batiment.getId()).append(" ===\n\n");

        // On parcourt notre dictionnaire pour calculer les coûts individuels et construire les lignes du texte
        for (Map.Entry<Revetement, Double> entree : surfacesParRevetement.entrySet()) {
            Revetement rev = entree.getKey();
            double surface = entree.getValue();
            double prix = surface * rev.getPrixM2(); // Surface (m²) * Prix unitaire (€/m²)
            devisTotal += prix; // Cumul dans le prix total du bâtiment

            // String.format("%.2f") permet d'arrondir proprement à 2 chiffres après la virgule
            facture.append(String.format("- %s (%.2f m²) : %.2f €\n", rev.getNom(), surface, prix));
        }

        facture.append("-----------------------------------\n");
        facture.append(String.format("TOTAL GLOBAL : %.2f €\n", devisTotal));

        return facture.toString(); // Renvoie la facture sous forme de texte prête à être affichée
    }

    // Méthode mathématique interne qui analyse les coordonnées des murs pour trouver la surface au sol d'un rectangle
    private static double calculerSurfacePieceRectangulaire(Piece p) {
        if (p.getMurs().isEmpty()) return 0.0;
        
        // Initialisation des bornes avec les valeurs extrêmes
        double minX = Double.MAX_VALUE, maxX = -Double.MAX_VALUE;
        double minY = Double.MAX_VALUE, maxY = -Double.MAX_VALUE;
        
        // On cherche le X minimum, X maximum, Y minimum et Y maximum parmi tous les murs de la pièce
        for (Mur m : p.getMurs()) {
            minX = Math.min(minX, Math.min(m.getDebut().getX(), m.getFin().getX()));
            maxX = Math.max(maxX, Math.max(m.getDebut().getX(), m.getFin().getX()));
            minY = Math.min(minY, Math.min(m.getDebut().getY(), m.getFin().getY()));
            maxY = Math.max(maxY, Math.max(m.getDebut().getY(), m.getFin().getY()));
        }
        // Formule de la surface d'un rectangle : Largeur (Delta X) * Longueur (Delta Y)
        return (maxX - minX) * (maxY - minY);
    }
}