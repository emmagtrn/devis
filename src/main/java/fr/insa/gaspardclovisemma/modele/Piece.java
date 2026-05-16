package fr.insa.gaspardclovisemma.modele;

import fr.insa.gaspardclovisemma.materiaux.Revetement;
import java.util.ArrayList;
import java.util.List;

public class Piece {
    private int idPiece;
    private List<Mur> murs = new ArrayList<>();
    private Revetement revetementSol;
    private Revetement revetementPlafond;

    public Piece(int idPiece) {
        this.idPiece = idPiece;
    }

    public void ajouterMur(Mur m) { murs.add(m); }
    public List<Mur> getMurs() { return murs; }

    public void setRevetementSol(Revetement r) { this.revetementSol = r; }
    public Revetement getRevetementSol() { return revetementSol; }

    public void setRevetementPlafond(Revetement r) { this.revetementPlafond = r; }
    public Revetement getRevetementPlafond() { return revetementPlafond; }
    
    public int getId() { return idPiece; }
}