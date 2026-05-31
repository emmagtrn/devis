package fr.insa.gaspardclovisemma.materiaux;

// "extends Ouverture" Porte est un enfant de Ouverture. 
// Elle hérite automatiquement du calcul de surface !
public class Porte extends Ouverture {
    
    // Le constructeur : quand on crée une Porte, on fixe directement ses dimensions
    public Porte() {
        this.largeur = 0.90; 
        this.hauteur = 2.10; 
    }
}