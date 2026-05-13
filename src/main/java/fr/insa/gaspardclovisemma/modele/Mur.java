package fr.insa.gaspardclovisemma.modele;

import fr.insa.gaspardclovisemma.materiaux.Ouverture;
import fr.insa.gaspardclovisemma.materiaux.Revetement;
import java.util.ArrayList;
import java.util.List;

public class Mur {
    private Coin debut, fin;
    private List<Ouverture> ouvertures = new ArrayList<>();
    
    // NOUVEAU : Le mur sait maintenant quel est son revêtement !
    private Revetement revetement; 

    public Mur(Coin debut, Coin fin) {
        this.debut = debut;
        this.fin = fin;
    }

    public Coin getDebut() { return debut; }
    public Coin getFin() { return fin; }

    // NOUVEAU : Les méthodes pour ajouter et lire le revêtement
    public void setRevetement(Revetement r) { this.revetement = r; }
    public Revetement getRevetement() { return revetement; }

    public void ajouterOuverture(Ouverture o) { ouvertures.add(o); }

    public double getLongueur() {
        return Math.sqrt(Math.pow(fin.getX() - debut.getX(), 2) + Math.pow(fin.getY() - debut.getY(), 2));
    }

    public double calculerSurface(double hauteur) {
        double surfaceBrute = getLongueur() * hauteur;
        double surfaceOuvertures = ouvertures.stream().mapToDouble(Ouverture::getSurface).sum();
        return surfaceBrute - surfaceOuvertures;
    }
}