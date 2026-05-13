package fr.insa.gaspardclovisemma.modele;

import java.util.ArrayList;
import java.util.List;

public class Piece {
    private int idPiece;
    private List<Mur> murs;

    public Piece(int idPiece) {
        this.idPiece = idPiece;
        this.murs = new ArrayList<>();
    }

    public void ajouterMur(Mur m) {
        murs.add(m);
    }

    public List<Mur> getMurs() {
        return murs;
    }

    // Calcule la surface totale des murs de cette pièce
    public double calculerSurfacePiece(double hauteurPlafond) {
        double surfaceTotal = 0;
        for (Mur mur : murs) {
            surfaceTotal += mur.calculerSurface(hauteurPlafond);
        }
        return surfaceTotal;
    }
}