package fr.insa.gaspardclovisemma.modele;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class GestionnaireFichier {

    // Méthode qui crée un fichier texte et écrit l'arborescence complète du projet dedans
    public static void sauvegarderProjet(Batiment batiment, String cheminFichier) {
        // Utilisation du "try-with-resources" : Garantit que le fichier sera correctement fermé et enregistré,
        // même s'il y a un bug ou un plantage pendant l'écriture.
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(cheminFichier))) {
            
            // Écriture de l'en-tête du bâtiment
            bw.write("=== SAUVEGARDE DU BATIMENT : " + batiment.getId() + " ===\n");
            bw.write("Type : " + batiment.getType() + "\n\n");

            // On descend l'arborescence étape par étape en ajoutant des espaces (indentation) pour que le fichier texte soit lisible par un humain
            for (Niveau n : batiment.getNiveaux()) {
                bw.write(String.format("--- NIVEAU %d (Hauteur sous plafond: %.2fm) ---\n", n.getId(), n.getHauteurSousPlafond()));
                
                for (Appartement a : n.getAppartements()) {
                    bw.write("  APPARTEMENT " + a.getId() + "\n");
                    
                    for (Piece p : a.getPieces()) {
                        bw.write("    PIECE " + p.getId() + "\n");
                        
                        // Gestion de l'affichage si le revêtement n'est pas encore choisi (Brut)
                        String sol = p.getRevetementSol() != null ? p.getRevetementSol().getNom() : "Brut";
                        String plafond = p.getRevetementPlafond() != null ? p.getRevetementPlafond().getNom() : "Brut";
                        bw.write("      Revêtement Sol : " + sol + "\n");
                        bw.write("      Revêtement Plafond : " + plafond + "\n");
                        
                        int numMur = 1;
                        for (Mur m : p.getMurs()) {
                            String rev = m.getRevetement() != null ? m.getRevetement().getNom() : "Brut";
                            // Sauvegarde brute des coordonnées exactes (X1, Y1) et (X2, Y2) de chaque mur comme imposé par le sujet
                            bw.write(String.format("      Mur %d : Coordonnées (%.2f, %.2f) à (%.2f, %.2f) - Revêtement : %s\n", 
                                numMur++, m.getDebut().getX(), m.getDebut().getY(), m.getFin().getX(), m.getFin().getY(), rev));
                        }
                    }
                }
            }
            System.out.println("Sauvegarde réussie avec succès !");

        } catch (IOException e) {
            // Bloc de secours au cas où le disque dur est plein ou protégé en écriture
            System.err.println("Erreur lors de la sauvegarde : " + e.getMessage());
        }
    }
}