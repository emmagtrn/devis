package fr.insa.gaspardclovisemma.materiaux;

public class Revetement {
    private String nom;
    private double prixM2;

    public Revetement(String nom, double prixM2) {
        this.nom = nom;
        this.prixM2 = prixM2;
    }

    public String getNom() { return nom; }
    public double getPrixM2() { return prixM2; }
}
