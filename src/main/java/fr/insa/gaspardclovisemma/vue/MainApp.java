package fr.insa.gaspardclovisemma.vue;

import fr.insa.gaspardclovisemma.materiaux.*; 
import fr.insa.gaspardclovisemma.modele.*;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;

public class MainApp extends Application {
    
    // --- MODELE DE DONNEES ---
    // Racine du projet en mémoire
    private Batiment monBatiment = new Batiment("Batiment_GCE", "Immeuble");
    
    // Liste des revêtements importés via le fichier texte
    private List<Revetement> catalogue = new ArrayList<>();
    
    // Gestion du Undo (annulation du dernier mur)
    private Mur dernierMurAjoute;
    private Piece pieceDuDernierMur;

    // --- PARAMETRES IHM ---
    private final double ECHELLE = 40.0; // 1m = 40px
    
    // Composants globaux modifiables par les events
    private Canvas canvasPlan; 
    private TextArea zoneDevis; 
    private ComboBox<Integer> selecteurVueNiveau; 

    @Override
    public void start(Stage stage) {
        stage.setTitle("EstimaBat");
        
        // Chargement des données au démarrage
        initialiserCatalogue();

        // ==========================================
        // UI - ZONE DE DESSIN (DROITE)
        // ==========================================
        
        VBox zoneCentrale = new VBox(10);
        zoneCentrale.setPadding(new Insets(10));
        
        HBox enTeteVue = new HBox(15);
        Label lblPlan = new Label("Visualisation du Plan 2D :");
        lblPlan.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
        
        selecteurVueNiveau = new ComboBox<>();
        enTeteVue.getChildren().addAll(lblPlan, new Label("Étage affiché :"), selecteurVueNiveau);
        
        // Init du canvas et de son pinceau
        canvasPlan = new Canvas(600, 500);
        GraphicsContext gc = canvasPlan.getGraphicsContext2D();
        initialiserGrille(gc);
        
        zoneCentrale.getChildren().addAll(enTeteVue, canvasPlan);

        // ==========================================
        // UI - FORMULAIRE (GAUCHE)
        // ==========================================
        
        VBox formulaire = new VBox(12);
        formulaire.setPadding(new Insets(15));
        formulaire.setPrefWidth(480); 
        formulaire.setStyle("-fx-background-color: #f8f9fa; -fx-border-color: #dee2e6;");

        // 1. Identifiants
        TextField txtNiveau = new TextField("0"); txtNiveau.setPrefWidth(40);
        TextField txtAppart = new TextField("1"); txtAppart.setPrefWidth(40);
        TextField txtPiece = new TextField("1"); txtPiece.setPrefWidth(40);
        HBox boxHierarchie = new HBox(10, new Label("Niveau :"), txtNiveau, new Label("Appartement :"), txtAppart, new Label("Salle/Pièce :"), txtPiece);

        // 2. Choix des matériaux
        ComboBox<String> comboSol = new ComboBox<>();
        ComboBox<String> comboPlafond = new ComboBox<>();
        ComboBox<String> comboMurCote1 = new ComboBox<>();
        ComboBox<String> comboMurCote2 = new ComboBox<>(); 
        
        // Tri des matériaux selon leur utilisation autorisée
        for (Revetement r : catalogue) {
            String choix = r.getNom() + " (" + r.getPrixM2() + " €/m²)";
            if (r.isPourSol()) comboSol.getItems().add(choix);
            if (r.isPourPlafond()) comboPlafond.getItems().add(choix);
            if (r.isPourMur()) {
                comboMurCote1.getItems().add(choix);
                comboMurCote2.getItems().add(choix);
            }
        }
        
        // Sélections par défaut
        comboSol.getSelectionModel().selectFirst(); 
        comboPlafond.getSelectionModel().selectFirst();
        comboMurCote1.getSelectionModel().selectFirst(); 
        comboMurCote2.getSelectionModel().selectFirst();

        GridPane gridDetails = new GridPane();
        gridDetails.setHgap(10); 
        gridDetails.setVgap(10); 
        gridDetails.addRow(0, new Label("Revêtement Sol Salle :"), comboSol);
        gridDetails.addRow(1, new Label("Revêtement Plafond Salle :"), comboPlafond);
        gridDetails.addRow(2, new Label("Revêtement Mur (Côté 1) :"), comboMurCote1);
        gridDetails.addRow(3, new Label("Revêtement Mur (Côté 2) :"), comboMurCote2);

        // 3. Géométrie et Ouvertures
        TextField txtX1 = new TextField("0"); txtX1.setPrefWidth(45);
        TextField txtY1 = new TextField("0"); txtY1.setPrefWidth(45);
        TextField txtX2 = new TextField("5"); txtX2.setPrefWidth(45);
        TextField txtY2 = new TextField("0"); txtY2.setPrefWidth(45);
        HBox coord1 = new HBox(8, new Label("X1 :"), txtX1, new Label("Y1 :"), txtY1, new Label("X2 :"), txtX2, new Label("Y2 :"), txtY2);
        
        TextField txtPortes = new TextField("0"); txtPortes.setPrefWidth(45);
        TextField txtFenetres = new TextField("0"); txtFenetres.setPrefWidth(45);
        HBox boxOuvertures = new HBox(15, new Label("Nombre Portes :"), txtPortes, new Label("Nombre Fenêtres :"), txtFenetres);

        // 4. Boutons d'actions
        Button btnAjouter = new Button("Tracer ce Mur");
        btnAjouter.setStyle("-fx-background-color: #28a745; -fx-text-fill: white; -fx-font-weight: bold;");
        
        Button btnAnnuler = new Button("✖ Supprimer Dernier Mur");
        btnAnnuler.setStyle("-fx-background-color: #dc3545; -fx-text-fill: white;");
        btnAnnuler.setDisable(true); // Désactivé tant qu'il n'y a pas de mur à annuler
        HBox boxBoutonsMur = new HBox(10, btnAjouter, btnAnnuler);

        Button btnCalculerDevis = new Button("Calculer Devis");
        Button btnSauvegarder = new Button("Sauvegarder");
        HBox boxActions = new HBox(10, btnCalculerDevis, btnSauvegarder);
        
        zoneDevis = new TextArea(); 
        zoneDevis.setEditable(false); 
        zoneDevis.setPrefHeight(150);

        // Assemblage du menu de gauche
        formulaire.getChildren().addAll(
            new Label("1. Emplacement de la structure"), boxHierarchie, new Separator(),
            new Label("2. Choix des matériaux (Par surfaces)"), gridDetails, new Separator(),
            new Label("3. Paramètres géométriques du segment"), coord1, boxOuvertures,
            boxBoutonsMur, new Separator(), boxActions, zoneDevis
        );

        // ==========================================
        // EVENTS ET LOGIQUE METIER
        // ==========================================

        // Maj de l'affichage quand on change d'étage
        selecteurVueNiveau.setOnAction(e -> dessinerPlan(gc, selecteurVueNiveau.getValue()));
        
        btnAjouter.setOnAction(e -> {
            try {
                int idNiv = Integer.parseInt(txtNiveau.getText().trim());
                int idApp = Integer.parseInt(txtAppart.getText().trim());
                int idPiece = Integer.parseInt(txtPiece.getText().trim());
                
                // On récupère ou on instancie les niveaux/apparts/pièces
                Niveau niveau = trouverOuCreerNiveau(idNiv);
                
                // Maj du sélecteur si c'est un nouveau niveau
                if (!selecteurVueNiveau.getItems().contains(idNiv)) {
                    selecteurVueNiveau.getItems().add(idNiv); 
                    selecteurVueNiveau.getSelectionModel().select((Integer)idNiv);
                }
                
                Appartement appart = trouverOuCreerAppartement(niveau, idApp);
                Piece piece = trouverOuCreerPiece(appart, idPiece);

                piece.setRevetementSol(trouverRevetement(comboSol.getValue()));
                piece.setRevetementPlafond(trouverRevetement(comboPlafond.getValue()));

                // Parsing des coordonnées (gestion des virgules européennes)
                double x1 = Double.parseDouble(txtX1.getText().replace(",", "."));
                double y1 = Double.parseDouble(txtY1.getText().replace(",", "."));
                double x2 = Double.parseDouble(txtX2.getText().replace(",", "."));
                double y2 = Double.parseDouble(txtY2.getText().replace(",", "."));

                // Création et configuration du mur
                Mur nouveauMur = new Mur(new Coin(x1, y1), new Coin(x2, y2));
                nouveauMur.setRevetementCote1(trouverRevetement(comboMurCote1.getValue()));
                nouveauMur.setRevetementCote2(trouverRevetement(comboMurCote2.getValue()));
                
                // Ajout des ouvertures
                int nbPortes = Integer.parseInt(txtPortes.getText().trim());
                int nbFenetres = Integer.parseInt(txtFenetres.getText().trim());
                for(int i=0; i<nbPortes; i++) nouveauMur.ajouterOuverture(new Porte());
                for(int i=0; i<nbFenetres; i++) nouveauMur.ajouterOuverture(new Fenetre());

                piece.ajouterMur(nouveauMur);
                
                // Sauvegarde de l'état pour la fonction Undo
                dernierMurAjoute = nouveauMur;
                pieceDuDernierMur = piece;
                btnAnnuler.setDisable(false); 
                
                // Refresh IHM
                dessinerPlan(gc, selecteurVueNiveau.getValue());
                
            } catch (NumberFormatException ex) {
                // Gestion des mauvaises saisies
                new Alert(Alert.AlertType.ERROR, "Données d'entrée erronées. Vérifiez les formats.").showAndWait();
            }
        });

        // Action bouton Annuler
        btnAnnuler.setOnAction(e -> {
            if (dernierMurAjoute != null && pieceDuDernierMur != null) {
                pieceDuDernierMur.getMurs().remove(dernierMurAjoute); 
                dernierMurAjoute = null; 
                pieceDuDernierMur = null;
                btnAnnuler.setDisable(true); 
                dessinerPlan(gc, selecteurVueNiveau.getValue()); 
            }
        });

        // Actions globales (Devis / Export)
        btnCalculerDevis.setOnAction(e -> zoneDevis.setText(CalculateurDevis.genererFactureDetaillee(monBatiment)));
        btnSauvegarder.setOnAction(e -> GestionnaireFichier.sauvegarderProjet(monBatiment, "sauvegarde_estimabat.txt"));

        // ==========================================
        // ASSEMBLAGE FINAL
        // ==========================================
        
        BorderPane layout = new BorderPane();
        layout.setLeft(formulaire); 
        layout.setCenter(zoneCentrale); 
        
        stage.setScene(new Scene(layout, 1120, 650));
        stage.show();
    }
    
    // ==========================================
    // METHODES UTILITAIRES (Recherche/Creation)
    // ==========================================
    
    private Niveau trouverOuCreerNiveau(int id) {
        for (Niveau n : monBatiment.getNiveaux()) if (n.getId() == id) return n;
        Niveau nouveau = new Niveau(id, 2.50); 
        monBatiment.ajouterNiveau(nouveau); 
        return nouveau;
    }
    
    private Appartement trouverOuCreerAppartement(Niveau n, int id) {
        for (Appartement a : n.getAppartements()) if (a.getId() == id) return a;
        Appartement nouveau = new Appartement(id); 
        n.ajouterAppartement(nouveau); 
        return nouveau;
    }
    
    private Piece trouverOuCreerPiece(Appartement a, int id) {
        for (Piece p : a.getPieces()) if (p.getId() == id) return p;
        Piece nouvelle = new Piece(id); 
        a.ajouterPiece(nouvelle); 
        return nouvelle;
    }
    
    // Récupère l'objet Revetement correspondant au texte sélectionné dans la ComboBox
    private Revetement trouverRevetement(String choix) {
        if (choix == null) return null; 
        for (Revetement r : catalogue) if (choix.startsWith(r.getNom())) return r;
        return null;
    }

    // ==========================================
    // MOTEUR DE DESSIN 2D (CANVAS)
    // ==========================================

    private void initialiserGrille(GraphicsContext gc) {
        gc.setStroke(Color.LIGHTGRAY); 
        gc.setLineWidth(0.5); 
        for(int i=0; i<600; i+=ECHELLE) gc.strokeLine(i, 0, i, 500); // Lignes verticales
        for(int i=0; i<500; i+=ECHELLE) gc.strokeLine(0, i, 600, i); // Lignes horizontales
    }

    private void dessinerPlan(GraphicsContext gc, Integer niveauAAfficher) {
        // Nettoyage complet du canvas avant redraw
        gc.clearRect(0, 0, 600, 500);
        initialiserGrille(gc); 
        
        if (niveauAAfficher == null) return; 

        // Parcours du modèle
        for (Niveau n : monBatiment.getNiveaux()) {
            if (n.getId() == niveauAAfficher) {
                for (Appartement a : n.getAppartements()) {
                    for (Piece p : a.getPieces()) {
                        
                        // 1. Dessin des murs
                        gc.setLineWidth(4); 
                        for (Mur m : p.getMurs()) {
                            // Highlight visuel du dernier mur ajouté
                            if (m == dernierMurAjoute) {
                                gc.setStroke(Color.RED); 
                            } else {
                                gc.setStroke(Color.DARKSLATEGRAY); 
                            }
                            
                            // Map coordonnées logiques -> physiques (pixels)
                            double x1 = m.getDebut().getX() * ECHELLE + 50, y1 = m.getDebut().getY() * ECHELLE + 50;
                            double x2 = m.getFin().getX() * ECHELLE + 50, y2 = m.getFin().getY() * ECHELLE + 50;
                            gc.strokeLine(x1, y1, x2, y2); 
                        }

                        // 2. Affichage des identifiants des pièces (calcul du centre de gravité)
                        if (!p.getMurs().isEmpty()) {
                            double minX = Double.MAX_VALUE, maxX = -Double.MAX_VALUE;
                            double minY = Double.MAX_VALUE, maxY = -Double.MAX_VALUE;
                            
                            for (Mur m : p.getMurs()) {
                                minX = Math.min(minX, Math.min(m.getDebut().getX(), m.getFin().getX()));
                                maxX = Math.max(maxX, Math.max(m.getDebut().getX(), m.getFin().getX()));
                                minY = Math.min(minY, Math.min(m.getDebut().getY(), m.getFin().getY()));
                                maxY = Math.max(maxY, Math.max(m.getDebut().getY(), m.getFin().getY()));
                            }
                            
                            double centreX = ((minX + maxX) / 2) * ECHELLE + 50;
                            double centreY = ((minY + maxY) / 2) * ECHELLE + 50;
                            
                            gc.setFill(Color.BLUE);
                            gc.setFont(Font.font("Arial", FontWeight.BOLD, 12));
                            gc.fillText("Salle n°" + p.getId(), centreX - 22, centreY + 4);
                        }
                    }
                }
            }
        }
    }

    /**
     * Initialisation du catalogue des matériaux via le fichier externe.
     */
    private void initialiserCatalogue() {
        catalogue = GestionnaireCatalogue.chargerCatalogue("revetements"); 
        
        if (catalogue.isEmpty()) {
            System.err.println("Avertissement : Le catalogue n'a pas pu être chargé (liste vide).");
        }
    }
    
    
    public static void main(String[] args) { launch(args); }
}