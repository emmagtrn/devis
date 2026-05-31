package fr.insa.gaspardclovisemma.modele;

import java.util.ArrayList;
import java.util.List;

// La classe Niveau représente un étage du bâtiment (ex: 0 pour RDC, 1 pour 1er étage)
public class Niveau {
    private int idNiveau; // Le numéro de l'étage
    
    // La hauteur des murs est définie pour tout l'étage
    private double hauteurSousPlafond; 
    
    // Un étage contient une liste d'appartements
    private List<Appartement> appartements = new ArrayList<>();

    // Constructeur
    public Niveau(int idNiveau, double hauteurSousPlafond) {
        this.idNiveau = idNiveau;
        this.hauteurSousPlafond = hauteurSousPlafond;
    }

    // Méthode pour rajouter un appartement sur ce palier
    public void ajouterAppartement(Appartement a) { appartements.add(a); }
    
    // Getters
    public List<Appartement> getAppartements() { return appartements; }
    public int getId() { return idNiveau; }
    public double getHauteurSousPlafond() { return hauteurSousPlafond; }
}