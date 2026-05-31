package fr.insa.gaspardclovisemma.modele;

import fr.insa.gaspardclovisemma.materiaux.Revetement;
import java.util.ArrayList;
import java.util.List;

// La Pièce est l'élément qui contient les matériaux et les murs
public class Piece {
    private int idPiece; 
    
    // Une pièce contient une liste de murs
    private List<Mur> murs = new ArrayList<>();
    
    // Une pièce possède aussi ses propres matériaux pour le sol et le plafond
    private Revetement revetementSol;
    private Revetement revetementPlafond;

    // Constructeur
    public Piece(int idPiece) {
        this.idPiece = idPiece;
    }

    // Gestion des murs
    public void ajouterMur(Mur m) { murs.add(m); }
    public List<Mur> getMurs() { return murs; }

    // Les "Setters" : permettent d'appliquer un matériau au sol ou au plafond après la création de la pièce
    public void setRevetementSol(Revetement r) { this.revetementSol = r; }
    public Revetement getRevetementSol() { return revetementSol; }

    public void setRevetementPlafond(Revetement r) { this.revetementPlafond = r; }
    public Revetement getRevetementPlafond() { return revetementPlafond; }
    
    public int getId() { return idPiece; }
}