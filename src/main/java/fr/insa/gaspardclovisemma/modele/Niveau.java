package fr.insa.gaspardclovisemma.modele;

import java.util.ArrayList;
import java.util.List;

public class Niveau {
    private int idNiveau;
    private double hauteurSousPlafond;
    private List<Appartement> appartements;

    public Niveau(int idNiveau, double hauteurSousPlafond) {
        this.idNiveau = idNiveau;
        this.hauteurSousPlafond = hauteurSousPlafond;
        this.appartements = new ArrayList<>();
    }

    public void ajouterAppartement(Appartement a) {
        appartements.add(a);
    }

    public List<Appartement> getAppartements() {
        return appartements;
    }

    public double getHauteurSousPlafond() {
        return hauteurSousPlafond;
    }
}
