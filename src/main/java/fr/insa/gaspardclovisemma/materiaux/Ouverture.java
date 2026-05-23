package fr.insa.gaspardclovisemma.materiaux;

// La classe Parent. On la met "abstract" car on ne crée jamais une "Ouverture" dans le vide. 
// On créera toujours soit une Porte, soit une Fenêtre.
public abstract class Ouverture {
    
    // Protected signifie que seules les classes "Enfants" (Porte et Fenetre) ont le droit de toucher à ces variables
    protected double largeur;
    protected double hauteur;

    // Méthode commune à tous les enfants : le calcul de la surface du trou
    public double getSurface() {
        return largeur * hauteur;
    }
}