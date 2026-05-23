package fr.insa.gaspardclovisemma.materiaux;

// "extends Ouverture" : La fenêtre est aussi un enfant de Ouverture.
public class Fenetre extends Ouverture {
    
    // Le constructeur : quand on crée une Fenêtre, on fixe ses dimensions
    public Fenetre() {
        this.largeur = 1.20; // Largeur imposée par le sujet
        this.hauteur = 1.20; // Hauteur imposée par le sujet
    }
}