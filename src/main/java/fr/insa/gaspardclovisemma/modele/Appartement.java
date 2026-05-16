package fr.insa.gaspardclovisemma.modele;

import java.util.ArrayList;
import java.util.List;

public class Appartement {
    private int idAppartement;
    private List<Piece> pieces = new ArrayList<>();

    public Appartement(int idAppartement) {
        this.idAppartement = idAppartement;
    }

    public void ajouterPiece(Piece p) { pieces.add(p); }
    public List<Piece> getPieces() { return pieces; }
    
    public int getId() { return idAppartement; }
}