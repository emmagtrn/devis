package fr.insa.gaspardclovisemma.modele;

import fr.insa.gaspardclovisemma.materiaux.Revetement;

// Le Mur est l'élément géométrique de base. Il se trace d'un point A (début) à un point B (fin).
public class Mur {
    // Les coins sont des objets contenant un X et un Y
    private Coin debut, fin;
    
    // Le matériau posé sur ce mur spécifique (ex: Papier Peint)
    private Revetement revetement; 

    // Constructeur
    public Mur(Coin debut, Coin fin) {
        this.debut = debut;
        this.fin = fin;
    }

    // Getters
    public Coin getDebut() { return debut; }
    public Coin getFin() { return fin; }

    // Setter et Getter pour le revêtement
    public void setRevetement(Revetement r) { this.revetement = r; }
    public Revetement getRevetement() { return revetement; }

    // Méthode mathématique : Calcule la longueur du mur avec le théorème de Pythagore : √((x2-x1)² + (y2-y1)²)
    public double getLongueur() {
        return Math.sqrt(Math.pow(fin.getX() - debut.getX(), 2) + Math.pow(fin.getY() - debut.getY(), 2));
    }

    // Calcule la surface totale du mur (Longueur * Hauteur)
    public double calculerSurface(double hauteur) {
        return getLongueur() * hauteur;
    }
}