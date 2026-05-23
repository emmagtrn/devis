package fr.insa.gaspardclovisemma.modele;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class GestionnaireFichier {

    /**
     * Exporte toute l'arborescence du projet dans un fichier texte lisible.
     */
    public static void sauvegarderProjet(Batiment batiment, String cheminFichier) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(cheminFichier))) {
            bw.write("=== SAUVEGARDE DU BATIMENT : " + batiment.getId() + " ===\n");
            bw.write("Type : " + batiment.getType() + "\n\n");

            // On parcourt chaque niveau...
            for (Niveau n : batiment.getNiveaux()) {
                bw.write(String.format("--- NIVEAU %d (Hauteur sous plafond: %.2fm) ---\n", n.getId(), n.getHauteurSousPlafond()));
                
                // ... puis chaque appartement ...
                for (Appartement a : n.getAppartements()) {
                    bw.write("  APPARTEMENT " + a.getId() + "\n");
                    
                    // ... puis chaque pièce ...
                    for (Piece p : a.getPieces()) {
                        bw.write("    PIECE " + p.getId() + "\n");
                        
                        // Récupération des matériaux de la pièce
                        String sol = p.getRevetementSol() != null ? p.getRevetementSol().getNom() : "Brut";
                        String plafond = p.getRevetementPlafond() != null ? p.getRevetementPlafond().getNom() : "Brut";
                        bw.write("      Revêtement Sol : " + sol + "\n");
                        bw.write("      Revêtement Plafond : " + plafond + "\n");
                        
                        // ... et enfin chaque mur !
                        int numMur = 1;
                        for (Mur m : p.getMurs()) {
                            
                            // On récupère proprement le Côté 1 et le Côté 2 du mur
                            String revCote1 = m.getRevetementCote1() != null ? m.getRevetementCote1().getNom() : "Brut";
                            String revCote2 = m.getRevetementCote2() != null ? m.getRevetementCote2().getNom() : "Brut";
                            
                            // On écrit la ligne avec les coordonnées, les deux faces et le nombre de trous
                            bw.write(String.format("      Mur %d : (%.2f, %.2f) à (%.2f, %.2f) - Côté 1 : %s | Côté 2 : %s - %d Ouverture(s)\n", 
                                numMur++, m.getDebut().getX(), m.getDebut().getY(), m.getFin().getX(), m.getFin().getY(), revCote1, revCote2, m.getOuvertures().size()));
                        }
                    }
                }
            }
            System.out.println("Sauvegarde réussie avec succès !");
            
        } catch (IOException e) {
            System.err.println("Erreur lors de la sauvegarde : " + e.getMessage());
        }
    }
}