package fr.insa.gaspardclovisemma.modele;

import fr.insa.gaspardclovisemma.materiaux.Revetement;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class GestionnaireCatalogue {

    public static List<Revetement> chargerCatalogue(String cheminFichier) {
        List<Revetement> catalogue = new ArrayList<>();
        
        try (BufferedReader br = new BufferedReader(new FileReader(cheminFichier))) {
            String ligne;
            int numeroLigne = 0;
            
            while ((ligne = br.readLine()) != null) {
                numeroLigne++;
                
                // Nettoyage des espaces et du caractère invisible (BOM)
                ligne = ligne.trim().replace("\uFEFF", "");
                
                // On ignore les lignes vides ou l'en-tête
                if (ligne.isEmpty() || ligne.toLowerCase().startsWith("id")) continue; 

                try {
                    String[] data = ligne.split(";");
                    if (data.length >= 6) {
                        int id = Integer.parseInt(data[0].trim());
                        String nom = data[1].trim();
                        boolean pourMur = data[2].trim().equals("1");
                        boolean pourSol = data[3].trim().equals("1");
                        boolean pourPlafond = data[4].trim().equals("1");
                        double prix = Double.parseDouble(data[5].trim().replace(",", "."));

                        catalogue.add(new Revetement(id, nom, pourMur, pourSol, pourPlafond, prix));
                    }
                } catch (Exception e) {
                    System.err.println("ATTENTION - Impossible de lire la ligne " + numeroLigne + " : " + ligne);
                }
            }
            System.out.println("SUCCES : " + catalogue.size() + " materiaux ont ete charges depuis le fichier !");
            
        } catch (IOException e) {
            System.err.println("ERREUR CRITIQUE : Java ne trouve pas le fichier '" + cheminFichier + "'.");
            System.err.println("INFO : Dossier ou Java cherche actuellement : " + new java.io.File(".").getAbsolutePath());
        }
        
        return catalogue;
    }
}