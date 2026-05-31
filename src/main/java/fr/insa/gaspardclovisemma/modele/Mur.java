package fr.insa.gaspardclovisemma.modele;

import fr.insa.gaspardclovisemma.materiaux.Ouverture;
import fr.insa.gaspardclovisemma.materiaux.Revetement;
import java.util.ArrayList;
import java.util.List;

/**
 * La classe Mur gère deux revêtements distincts pour ses deux faces,
 * peu importe sa position dans le bâtiment (cloison interne ou façade).
 */
public class Mur {
    // Les deux points qui forment le segment de droite du mur
    private Coin debut, fin;
    
    // Les deux faces du mur possèdent chacune leur propre matériau
    private Revetement revetementCote1; // Face A (ex: intérieur de la pièce)
    private Revetement revetementCote2; // Face B (ex: côté couloir ou extérieur)
    
    // Liste dynamique pour stocker les portes et fenêtres traversant ce mur
    private List<Ouverture> ouvertures = new ArrayList<>();

    // Constructeur pour instancier un mur avec ses coordonnées de base
    public Mur(Coin debut, Coin fin) {
        this.debut = debut;
        this.fin = fin;
    }

    // --- GETTERS ET SETTERS  ---
    public Coin getDebut() { return debut; }
    public Coin getFin() { return fin; }

    public void setRevetementCote1(Revetement r) { this.revetementCote1 = r; }
    public Revetement getRevetementCote1() { return revetementCote1; }

    public void setRevetementCote2(Revetement r) { this.revetementCote2 = r; }
    public Revetement getRevetementCote2() { return revetementCote2; }

    public void ajouterOuverture(Ouverture o) { ouvertures.add(o); }
    public List<Ouverture> getOuvertures() { return ouvertures; }

    /**
     * Calcule la longueur physique du mur via le théorème de Pythagore.
     */
    public double getLongueur() {
        return Math.sqrt(Math.pow(fin.getX() - debut.getX(), 2) + Math.pow(fin.getY() - debut.getY(), 2));
    }

    /**
     * Calcule la surface nette d'UNE face du mur (Surface totale moins les ouvertures).
     * Cette valeur sera utilisée deux fois (une fois par côté) dans le devis.
     */
    public double BlanketSurfaceNette(double hauteurNiveau) {
        double surfaceBrute = getLongueur() * hauteurNiveau;
        double surfaceTrous = 0.0;
        
        // On cumule la surface de toutes les ouvertures présentes
        for (Ouverture o : ouvertures) {
            surfaceTrous += o.getSurface(); 
        }
        
        // Renvoie la surface restante (sécurisée pour ne jamais être négative)
        return Math.max(0, surfaceBrute - surfaceTrous);
    }
}