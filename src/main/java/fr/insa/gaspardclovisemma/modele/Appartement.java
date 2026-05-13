package fr.insa.gaspardclovisemma.modele;

import java.util.ArrayList;
import java.util.List;

public class Appartement {
    private int idAppartement;
    private List<Piece> pieces;

    public Appartement(int idAppartement) {
        this.idAppartement = idAppartement;
        this.pieces = new ArrayList<>();
    }

    public void ajouterPiece(Piece p) {
        pieces.add(p);
    }

    public List<Piece> getPieces() {
        return pieces;
    }
}
