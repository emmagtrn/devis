package fr.insa.gaspardclovisemma.modele;

import java.util.ArrayList;
import java.util.List;

public class Batiment {
    private String nom;
    private List<Niveau> niveaux;

    public Batiment(String nom) {
        this.nom = nom;
        this.niveaux = new ArrayList<>();
    }

    public void ajouterNiveau(Niveau n) {
        niveaux.add(n);
    }

    public List<Niveau> getNiveaux() {
        return niveaux;
    }
}