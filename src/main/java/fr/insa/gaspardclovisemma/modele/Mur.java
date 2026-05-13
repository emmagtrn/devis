package fr.insa.gaspardclovisemma.modele;
import fr.insa.gaspardclovisemma.materiaux.Ouverture;
import java.util.ArrayList;
import java.util.List;

public class Mur {
    private Coin debut, fin;
    private List<Ouverture> ouvertures = new ArrayList<>();

    public Mur(Coin debut, Coin fin) {
        this.debut = debut;
        this.fin = fin;
    }

    // --- LES VOICI : Les méthodes manquantes pour réparer l'erreur ! ---
    public Coin getDebut() { return debut; }
    public Coin getFin() { return fin; }
    // -------------------------------------------------------------------

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