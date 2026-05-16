package fr.insa.gaspardclovisemma.modele;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class GestionnaireFichier {

    public static void sauvegarderProjet(Batiment batiment, String cheminFichier) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(cheminFichier))) {
            
            bw.write("=== SAUVEGARDE DU BATIMENT : " + batiment.getId() + " ===\n");
            bw.write("Type : " + batiment.getType() + "\n\n");

            // On parcourt toute l'arborescence pour écrire le fichier texte
            for (Niveau n : batiment.getNiveaux()) {
                bw.write(String.format("--- NIVEAU %d (Hauteur sous plafond: %.2fm) ---\n", n.getId(), n.getHauteurSousPlafond()));
                
                for (Appartement a : n.getAppartements()) {
                    bw.write("  APPARTEMENT " + a.getId() + "\n");
                    
                    for (Piece p : a.getPieces()) {
                        bw.write("    PIECE " + p.getId() + "\n");
                        
                        String sol = p.getRevetementSol() != null ? p.getRevetementSol().getNom() : "Brut";
                        String plafond = p.getRevetementPlafond() != null ? p.getRevetementPlafond().getNom() : "Brut";
                        bw.write("      Revêtement Sol : " + sol + "\n");
                        bw.write("      Revêtement Plafond : " + plafond + "\n");
                        
                        int numMur = 1;
                        for (Mur m : p.getMurs()) {
                            String rev = m.getRevetement() != null ? m.getRevetement().getNom() : "Brut";
                            // On sauvegarde bien les coordonnées X et Y comme demandé !
                            bw.write(String.format("      Mur %d : Coordonnées (%.2f, %.2f) à (%.2f, %.2f) - Revêtement : %s\n", 
                                numMur++, m.getDebut().getX(), m.getDebut().getY(), m.getFin().getX(), m.getFin().getY(), rev));
                        }
                    }
                }
            }
            System.out.println("Sauvegarde reussie avec succes !");

        } catch (IOException e) {
            System.err.println("Erreur lors de la sauvegarde : " + e.getMessage());
        }
    }
}