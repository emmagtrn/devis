package fr.insa.gaspardclovisemma.materiaux;

// Représente un matériau du catalogue officiel (Carrelage, Peinture, etc.)
public class Revetement {
    // Toutes les colonnes de votre fichier texte sont représentées ici
    private int idRevetement;
    private String designation; // Le nom affiché (ex: "Parquet")
    
    // Les booléens (Vrai/Faux) pour savoir où on a le droit de poser ce matériau
    private boolean pourMur;
    private boolean pourSol;
    private boolean pourPlafond;
    
    private double prixUnitaire; // Le prix au mètre carré

    // Constructeur complet
    public Revetement(int idRevetement, String designation, boolean pourMur, boolean pourSol, boolean pourPlafond, double prixUnitaire) {
        this.idRevetement = idRevetement;
        this.designation = designation;
        this.pourMur = pourMur;
        this.pourSol = pourSol;
        this.pourPlafond = pourPlafond;
        this.prixUnitaire = prixUnitaire;
    }

    // Getters pour que le CalculateurDevis et l'Interface Graphique puissent lire ces données
    public int getId() { return idRevetement; }
    public String getNom() { return designation; }
    public double getPrixM2() { return prixUnitaire; }
    
    // Les getters pour les booléens s'appellent souvent "is..." au lieu de "get..." en Java
    public boolean isPourMur() { return pourMur; }
    public boolean isPourSol() { return pourSol; }
    public boolean isPourPlafond() { return pourPlafond; }
}