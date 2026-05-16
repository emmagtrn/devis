package fr.insa.gaspardclovisemma.vue;

import fr.insa.gaspardclovisemma.materiaux.Revetement;
import fr.insa.gaspardclovisemma.modele.*;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;

public class MainApp extends Application {

    // Notre base de données principale : LE Bâtiment !
    private Batiment monBatiment = new Batiment("Batiment_GCE", "Immeuble");
    private List<Revetement> catalogue = new ArrayList<>();
    
    private final double ECHELLE = 40.0;
    private Canvas canvasPlan;
    private TextArea zoneDevis;
    private ComboBox<Integer> selecteurVueNiveau;

    @Override
    public void start(Stage stage) {
        stage.setTitle("EstimaBat - Version Finale (Étapes 1 & 2 validées)");

        initialiserCatalogue();

        // ==========================================
        // PARTIE DROITE : VISUALISATION PAR NIVEAU
        // ==========================================
        VBox zoneCentrale = new VBox(10);
        zoneCentrale.setPadding(new Insets(10));
        
        HBox enTeteVue = new HBox(15);
        Label lblPlan = new Label("Visualisation du Plan 2D :");
        lblPlan.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
        
        selecteurVueNiveau = new ComboBox<>();
        enTeteVue.getChildren().addAll(lblPlan, new Label("Étage affiché :"), selecteurVueNiveau);
        
        canvasPlan = new Canvas(600, 500);
        GraphicsContext gc = canvasPlan.getGraphicsContext2D();
        initialiserGrille(gc);
        
        zoneCentrale.getChildren().addAll(enTeteVue, canvasPlan);

        // ==========================================
        // PARTIE GAUCHE : SAISIE DES INFORMATIONS
        // ==========================================
        VBox formulaire = new VBox(12);
        formulaire.setPadding(new Insets(15));
        formulaire.setPrefWidth(350);
        formulaire.setStyle("-fx-background-color: #f8f9fa; -fx-border-color: #dee2e6;");

        // 1. Hiérarchie
        Label lblHierarchie = new Label("1. Emplacement");
        lblHierarchie.setStyle("-fx-font-weight: bold;");
        TextField txtNiveau = new TextField("0"); txtNiveau.setPrefWidth(40);
        TextField txtAppart = new TextField("1"); txtAppart.setPrefWidth(40);
        TextField txtPiece = new TextField("1"); txtPiece.setPrefWidth(40);
        HBox boxHierarchie = new HBox(10, new Label("Niv:"), txtNiveau, new Label("App:"), txtAppart, new Label("Pièce:"), txtPiece);

        // 2. Revêtements de la Pièce (Sol et Plafond)
        ComboBox<String> comboSol = new ComboBox<>();
        ComboBox<String> comboPlafond = new ComboBox<>();
        ComboBox<String> comboMur = new ComboBox<>();
        
        // On trie le catalogue intelligemment !
        for (Revetement r : catalogue) {
            String choix = r.getNom() + " (" + r.getPrixM2() + "€)";
            if (r.isPourSol()) comboSol.getItems().add(choix);
            if (r.isPourPlafond()) comboPlafond.getItems().add(choix);
            if (r.isPourMur()) comboMur.getItems().add(choix);
        }
        comboSol.getSelectionModel().selectFirst();
        comboPlafond.getSelectionModel().selectFirst();
        comboMur.getSelectionModel().selectFirst();

        GridPane gridRevPiece = new GridPane();
        gridRevPiece.setHgap(10); gridRevPiece.setVgap(5);
        gridRevPiece.addRow(0, new Label("Sol Pièce:"), comboSol);
        gridRevPiece.addRow(1, new Label("Plafond Pièce:"), comboPlafond);

        // 3. Coordonnées du Mur
        Label lblMur = new Label("2. Tracer un mur pour cette pièce");
        lblMur.setStyle("-fx-font-weight: bold;");
        TextField txtX1 = new TextField("0"); txtX1.setPrefWidth(40);
        TextField txtY1 = new TextField("0"); txtY1.setPrefWidth(40);
        TextField txtX2 = new TextField("5"); txtX2.setPrefWidth(40);
        TextField txtY2 = new TextField("0"); txtY2.setPrefWidth(40);
        HBox coord1 = new HBox(5, new Label("X1:"), txtX1, new Label("Y1:"), txtY1);
        HBox coord2 = new HBox(5, new Label("X2:"), txtX2, new Label("Y2:"), txtY2);

        HBox boxRevMur = new HBox(10, new Label("Rev. Mur:"), comboMur);

        Button btnAjouter = new Button("Ajouter ce Mur à la Pièce");
        btnAjouter.setStyle("-fx-background-color: #28a745; -fx-text-fill: white; -fx-font-weight: bold;");
        btnAjouter.setMaxWidth(Double.MAX_VALUE);

        // 4. Actions Finales
        Button btnCalculerDevis = new Button("Calculer le Devis");
        Button btnSauvegarder = new Button("Sauvegarder Projet");
        HBox boxActions = new HBox(10, btnCalculerDevis, btnSauvegarder);
        
        zoneDevis = new TextArea();
        zoneDevis.setEditable(false);
        zoneDevis.setPrefHeight(150);

        formulaire.getChildren().addAll(
            lblHierarchie, boxHierarchie, gridRevPiece,
            new Separator(),
            lblMur, coord1, coord2, boxRevMur, btnAjouter,
            new Separator(),
            boxActions, zoneDevis
        );

        // ==========================================
        // LOGIQUE ET INTERACTIONS
        // ==========================================
        selecteurVueNiveau.setOnAction(e -> dessinerPlan(gc, selecteurVueNiveau.getValue()));

        btnAjouter.setOnAction(e -> {
            try {
                int idNiv = Integer.parseInt(txtNiveau.getText());
                int idApp = Integer.parseInt(txtAppart.getText());
                int idPiece = Integer.parseInt(txtPiece.getText());
                
                double x1 = Double.parseDouble(txtX1.getText().replace(",", "."));
                double y1 = Double.parseDouble(txtY1.getText().replace(",", "."));
                double x2 = Double.parseDouble(txtX2.getText().replace(",", "."));
                double y2 = Double.parseDouble(txtY2.getText().replace(",", "."));

                // 1. On trouve ou on crée le Niveau
                Niveau niveau = trouverOuCreerNiveau(idNiv);
                if (!selecteurVueNiveau.getItems().contains(idNiv)) {
                    selecteurVueNiveau.getItems().add(idNiv);
                    selecteurVueNiveau.getSelectionModel().select((Integer)idNiv);
                }

                // 2. On trouve ou on crée l'Appartement
                Appartement appart = trouverOuCreerAppartement(niveau, idApp);

                // 3. On trouve ou on crée la Pièce
                Piece piece = trouverOuCreerPiece(appart, idPiece);

                // 4. On assigne les revêtements à la pièce (Sol et Plafond)
                piece.setRevetementSol(trouverRevetement(comboSol.getValue()));
                piece.setRevetementPlafond(trouverRevetement(comboPlafond.getValue()));

                // 5. On crée le mur et on l'ajoute à la pièce
                Mur nouveauMur = new Mur(new Coin(x1, y1), new Coin(x2, y2));
                nouveauMur.setRevetement(trouverRevetement(comboMur.getValue()));
                piece.ajouterMur(nouveauMur);
                
                dessinerPlan(gc, selecteurVueNiveau.getValue());

            } catch (Exception ex) {
                new Alert(Alert.AlertType.ERROR, "Veuillez vérifier vos saisies.").showAndWait();
            }
        });

        btnCalculerDevis.setOnAction(e -> {
            String facture = CalculateurDevis.genererFactureDetaillee(monBatiment);
            zoneDevis.setText(facture);
        });

        btnSauvegarder.setOnAction(e -> {
            GestionnaireFichier.sauvegarderProjet(monBatiment, "sauvegarde_estimabat.txt");
            Alert ok = new Alert(Alert.AlertType.INFORMATION, "Projet sauvegardé dans le dossier de l'application !");
            ok.setHeaderText(null);
            ok.showAndWait();
        });

        BorderPane layout = new BorderPane();
        layout.setLeft(formulaire);
        layout.setCenter(zoneCentrale);

        stage.setScene(new Scene(layout, 1050, 650));
        stage.show();
    }

    // --- MÉTHODES UTILITAIRES POUR GÉRER L'ARBORESCENCE ---
    
    private Niveau trouverOuCreerNiveau(int id) {
        for (Niveau n : monBatiment.getNiveaux()) {
            if (n.getId() == id) return n;
        }
        Niveau nouveau = new Niveau(id, 2.50); // Hauteur sous plafond de 2.50m par défaut
        monBatiment.ajouterNiveau(nouveau);
        return nouveau;
    }

    private Appartement trouverOuCreerAppartement(Niveau n, int id) {
        for (Appartement a : n.getAppartements()) {
            if (a.getId() == id) return a;
        }
        Appartement nouveau = new Appartement(id);
        n.ajouterAppartement(nouveau);
        return nouveau;
    }

    private Piece trouverOuCreerPiece(Appartement a, int id) {
        for (Piece p : a.getPieces()) {
            if (p.getId() == id) return p;
        }
        Piece nouvelle = new Piece(id);
        a.ajouterPiece(nouvelle);
        return nouvelle;
    }

    private Revetement trouverRevetement(String choixCombo) {
        if (choixCombo == null) return null;
        for (Revetement r : catalogue) {
            if (choixCombo.startsWith(r.getNom())) return r;
        }
        return null;
    }

    // --- INITIALISATION ET DESSIN ---

    private void initialiserCatalogue() {
        catalogue.clear();
        catalogue.add(new Revetement(1, "Peinture", true, false, true, 10.95));
        catalogue.add(new Revetement(2, "Carrelage", true, true, false, 49.75));
        catalogue.add(new Revetement(3, "Lambris", true, true, true, 50.60));
        catalogue.add(new Revetement(4, "Marbre", true, true, false, 97.85));
        catalogue.add(new Revetement(5, "Crepi", true, false, false, 67.80));
        catalogue.add(new Revetement(6, "Papier peint", true, false, false, 32.90));
        catalogue.add(new Revetement(7, "Plaquettes de parement", true, false, false, 15.20));
        catalogue.add(new Revetement(8, "Peinture", true, false, true, 77.30));
        catalogue.add(new Revetement(9, "Peinture", true, false, true, 29.90));
        catalogue.add(new Revetement(10, "Carrelage", true, true, false, 89.45));
        catalogue.add(new Revetement(11, "Lambris", true, true, false, 42.50));
        catalogue.add(new Revetement(12, "Liege", true, false, false, 25.40));
        catalogue.add(new Revetement(13, "Parquet", false, true, false, 46.36));
        catalogue.add(new Revetement(14, "Vinyle Lino", false, true, false, 23.55));
        catalogue.add(new Revetement(15, "Moquette", false, true, false, 48.10));
        catalogue.add(new Revetement(16, "Stratifie", false, true, false, 31.99));
        catalogue.add(new Revetement(17, "Gazon", false, true, false, 17.95));
        catalogue.add(new Revetement(18, "Liege", false, true, false, 33.90));
    }

    private void initialiserGrille(GraphicsContext gc) {
        gc.setStroke(Color.LIGHTGRAY);
        gc.setLineWidth(0.5);
        for(int i=0; i<600; i+=ECHELLE) gc.strokeLine(i, 0, i, 500);
        for(int i=0; i<500; i+=ECHELLE) gc.strokeLine(0, i, 600, i);
    }

    private void dessinerPlan(GraphicsContext gc, Integer niveauAAfficher) {
        gc.clearRect(0, 0, 600, 500);
        initialiserGrille(gc);
        
        if (niveauAAfficher == null) return;

        gc.setStroke(Color.DARKSLATEGRAY);
        gc.setLineWidth(4);

        // On fouille dans le bâtiment pour trouver le bon niveau à dessiner
        for (Niveau n : monBatiment.getNiveaux()) {
            if (n.getId() == niveauAAfficher) {
                for (Appartement a : n.getAppartements()) {
                    for (Piece p : a.getPieces()) {
                        for (Mur m : p.getMurs()) {
                            double x1 = m.getDebut().getX() * ECHELLE + 50;
                            double y1 = m.getDebut().getY() * ECHELLE + 50;
                            double x2 = m.getFin().getX() * ECHELLE + 50;
                            double y2 = m.getFin().getY() * ECHELLE + 50;
                            gc.strokeLine(x1, y1, x2, y2);
                        }
                    }
                }
            }
        }
    }

    public static void main(String[] args) { launch(args); }
}