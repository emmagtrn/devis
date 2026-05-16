package fr.insa.gaspardclovisemma.modele;

import java.util.ArrayList;
import java.util.List;

public class Batiment {
    private String idBatiment;
    private String typeBatiment; // Ex: "Maison" ou "Immeuble"
    private List<Niveau> niveaux = new ArrayList<>();

    public Batiment(String idBatiment, String typeBatiment) {
        this.idBatiment = idBatiment;
        this.typeBatiment = typeBatiment;
    }

    public void ajouterNiveau(Niveau n) { niveaux.add(n); }
    public List<Niveau> getNiveaux() { return niveaux; }
    
    public String getId() { return idBatiment; }
    public String getType() { return typeBatiment; }
}