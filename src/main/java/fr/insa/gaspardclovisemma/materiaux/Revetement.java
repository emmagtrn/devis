package fr.insa.gaspardclovisemma.materiaux;

public class Revetement {
    private int idRevetement;
    private String designation;
    private boolean pourMur;
    private boolean pourSol;
    private boolean pourPlafond;
    private double prixUnitaire;

    public Revetement(int idRevetement, String designation, boolean pourMur, boolean pourSol, boolean pourPlafond, double prixUnitaire) {
        this.idRevetement = idRevetement;
        this.designation = designation;
        this.pourMur = pourMur;
        this.pourSol = pourSol;
        this.pourPlafond = pourPlafond;
        this.prixUnitaire = prixUnitaire;
    }

    // Getters
    public int getId() { return idRevetement; }
    public String getNom() { return designation; }
    public double getPrixM2() { return prixUnitaire; }
    public boolean isPourMur() { return pourMur; }
    public boolean isPourSol() { return pourSol; }
    public boolean isPourPlafond() { return pourPlafond; }
}
