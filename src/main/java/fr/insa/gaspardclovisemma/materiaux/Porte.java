package fr.insa.gaspardclovisemma.materiaux;

// "extends Ouverture" signifie que Porte est un enfant de Ouverture. 
// Elle hérite automatiquement du calcul de surface !
public class Porte extends Ouverture {
    
    // Le constructeur : quand on crée une Porte, on fixe directement ses dimensions
    public Porte() {
        this.largeur = 0.90; // Largeur imposée par le sujet (en mètres)
        this.hauteur = 2.10; // Hauteur imposée par le sujet (en mètres)
    }
}