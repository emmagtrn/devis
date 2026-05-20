package fr.insa.gaspardclovisemma.modele;

import java.util.ArrayList;
import java.util.List;

// La classe Batiment est la racine de notre projet. Elle représente la structure globale.
public class Batiment {
    // Attributs exigés par le cahier des charges
    private String idBatiment;   // L'identifiant du bâtiment (ex: "Batiment_A")
    private String typeBatiment; // Le type (ex: "Maison" ou "Immeuble")
    
    // C'est ici que l'emboîtement commence : Un bâtiment contient une liste d'étages (Niveaux)
    private List<Niveau> niveaux = new ArrayList<>();

    // Le Constructeur : il sert à initialiser l'objet quand on fait un "new Batiment(...)"
    public Batiment(String idBatiment, String typeBatiment) {
        this.idBatiment = idBatiment;
        this.typeBatiment = typeBatiment;
    }

    // Méthode pour rajouter un étage au bâtiment
    public void ajouterNiveau(Niveau n) { niveaux.add(n); }
    
    // Les "Getters" : ils permettent aux autres classes (comme le CalculateurDevis) de venir lire ces informations privées
    public List<Niveau> getNiveaux() { return niveaux; }
    public String getId() { return idBatiment; }
    public String getType() { return typeBatiment; }
}