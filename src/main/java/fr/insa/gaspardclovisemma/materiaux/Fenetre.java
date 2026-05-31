package fr.insa.gaspardclovisemma.materiaux;

// La fenêtre est aussi un enfant de Ouverture.
public class Fenetre extends Ouverture {
    
    // Quand on crée une Fenêtre, on fixe ses dimensions
    public Fenetre() {
        this.largeur = 1.20; 
        this.hauteur = 1.20; 
    }
}