package fr.insa.gaspardclovisemma.modele;

import java.util.ArrayList;
import java.util.List;

// Représente un foyer/logement spécifique sur un palier (ex: Appartement 101)
public class Appartement {
    private int idAppartement;
    
    // Suite de l'emboîtement : Un appartement contient une liste de pièces
    private List<Piece> pieces = new ArrayList<>();

    // Constructeur
    public Appartement(int idAppartement) {
        this.idAppartement = idAppartement;
    }

    // Méthode pour ajouter une nouvelle pièce au logement
    public void ajouterPiece(Piece p) { pieces.add(p); }
    
    // Getters
    public List<Piece> getPieces() { return pieces; }
    public int getId() { return idAppartement; }
}