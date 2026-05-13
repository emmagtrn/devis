package fr.insa.gaspardclovisemma.materiaux;

public abstract class Ouverture {
    protected double largeur, hauteur;
    public double getSurface() { return largeur * hauteur; }
}

class Porte extends Ouverture {
    public Porte() { this.largeur = 0.9; this.hauteur = 2.1; }
}

class Fenetre extends Ouverture {
    public Fenetre() { this.largeur = 1.2; this.hauteur = 1.2; }
}