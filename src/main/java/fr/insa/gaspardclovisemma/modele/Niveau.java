package fr.insa.gaspardclovisemma.modele;

import java.util.ArrayList;
import java.util.List;

public class Niveau {
    private int idNiveau;
    private double hauteurSousPlafond;
    private List<Appartement> appartements = new ArrayList<>();

    public Niveau(int idNiveau, double hauteurSousPlafond) {
        this.idNiveau = idNiveau;
        this.hauteurSousPlafond = hauteurSousPlafond;
    }

    public void ajouterAppartement(Appartement a) { appartements.add(a); }
    public List<Appartement> getAppartements() { return appartements; }
    
    public int getId() { return idNiveau; }
    public double getHauteurSousPlafond() { return hauteurSousPlafond; }
}