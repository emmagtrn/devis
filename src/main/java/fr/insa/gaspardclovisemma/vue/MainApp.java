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

    // Notre base de données en mémoire
    private List<Mur> tousLesMurs = new ArrayList<>();
    private List<Revetement> catalogue = new ArrayList<>();
    
    // Éléments visuels
    private final double ECHELLE = 40.0;
    private Canvas canvasPlan;
    private TextArea zoneDevis;

    @Override
    public void start(Stage stage) {
        stage.setTitle("EstimaBat - Gestionnaire de Bâtiment (Étape 2)");

        // 1. CHARGEMENT DU CATALOGUE
        initialiserCatalogue();

        // 2. FORMULAIRE DE SAISIE (GAUCHE)
        VBox formulaire = new VBox(10);
        formulaire.setPadding(new Insets(15));
        formulaire.setPrefWidth(320);
        formulaire.setStyle("-fx-background-color: #f8f9fa; -fx-border-color: #dee2e6;");

        Label lblTitre = new Label("Saisie des éléments du bâtiment");
        lblTitre.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");

        // Champs de hiérarchie (Bâtiment > Niveau > Appartement > Pièce)
        TextField txtNiveau = new TextField("0"); txtNiveau.setPromptText("Ex: 0 (RDC)");
        TextField txtAppart = new TextField("1"); txtAppart.setPromptText("ID Appartement");
        TextField txtPiece = new TextField("1"); txtPiece.setPromptText("ID Pièce");
        
        GridPane gridHierarchie = new GridPane();
        gridHierarchie.setHgap(10); gridHierarchie.setVgap(10);
        gridHierarchie.addRow(0, new Label("Niveau :"), txtNiveau, new Label("Appart. :"), txtAppart);
        gridHierarchie.addRow(1, new Label("Pièce :"), txtPiece);

        // Champs du Mur
        Label lblMur = new Label("Coordonnées du mur (en mètres)");
        lblMur.setStyle("-fx-font-weight: bold;");
        
        HBox coord1 = new HBox(5, new Label("X1:"), new TextField("0"), new Label("Y1:"), new TextField("0"));
        HBox coord2 = new HBox(5, new Label("X2:"), new TextField("5"), new Label("Y2:"), new TextField("0"));

        ComboBox<String> comboRevetement = new ComboBox<>();
        catalogue.forEach(r -> comboRevetement.getItems().add(r.getNom()));
        comboRevetement.getSelectionModel().selectFirst();
        comboRevetement.setMaxWidth(Double.MAX_VALUE);

        Button btnAjouter = new Button("Ajouter cet élément au projet");
        btnAjouter.setStyle("-fx-background-color: #28a745; -fx-text-fill: white; -fx-font-weight: bold;");
        btnAjouter.setMaxWidth(Double.MAX_VALUE);

        // Zone d'action (Devis)
        Button btnCalculerDevis = new Button("Générer le Devis Total");
        btnCalculerDevis.setMaxWidth(Double.MAX_VALUE);
        
        zoneDevis = new TextArea();
        zoneDevis.setEditable(false);
        zoneDevis.setPrefHeight(200);

        formulaire.getChildren().addAll(
            lblTitre, new Separator(),
            new Label("1. Emplacement :"), gridHierarchie,
            new Separator(),
            new Label("2. Structure :"), lblMur, coord1, coord2,
            new Label("Revêtement :"), comboRevetement,
            btnAjouter,
            new Separator(),
            btnCalculerDevis, zoneDevis
        );

        // 3. ZONE DE VISUALISATION (CENTRE)
        VBox zoneCentrale = new VBox(10);
        zoneCentrale.setPadding(new Insets(10));
        Label lblPlan = new Label("Visualisation du Plan 2D (Vue passive)");
        lblPlan.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
        
        canvasPlan = new Canvas(600, 500);
        GraphicsContext gc = canvasPlan.getGraphicsContext2D();
        initialiserGrille(gc);
        zoneCentrale.getChildren().addAll(lblPlan, canvasPlan);

        // 4. LOGIQUE DES BOUTONS
        btnAjouter.setOnAction(e -> {
            try {
                double x1 = Double.parseDouble(((TextField)coord1.getChildren().get(1)).getText().replace(",", "."));
                double y1 = Double.parseDouble(((TextField)coord1.getChildren().get(3)).getText().replace(",", "."));
                double x2 = Double.parseDouble(((TextField)coord2.getChildren().get(1)).getText().replace(",", "."));
                double y2 = Double.parseDouble(((TextField)coord2.getChildren().get(3)).getText().replace(",", "."));

                Mur nouveauMur = new Mur(new Coin(x1, y1), new Coin(x2, y2));
                Revetement rev = catalogue.get(comboRevetement.getSelectionModel().getSelectedIndex());
                nouveauMur.setRevetement(rev); // Assure-toi d'avoir un "setRevetement" dans ta classe Mur
                
                tousLesMurs.add(nouveauMur);
                
                // On met à jour l'affichage passif
                dessinerPlan(gc);
                
                // Petit retour visuel
                Alert ok = new Alert(Alert.AlertType.INFORMATION, "Mur ajouté avec succès au Niveau " + txtNiveau.getText());
                ok.setHeaderText(null);
                ok.show();

            } catch (Exception ex) {
                new Alert(Alert.AlertType.ERROR, "Erreur dans la saisie des coordonnées.").showAndWait();
            }
        });

        btnCalculerDevis.setOnAction(e -> {
            // Utilise la classe CalculateurDevis que nous avons créée précédemment
            String facture = CalculateurDevis.genererFactureDetaillee(tousLesMurs, 2.50);
            zoneDevis.setText(facture);
        });

        BorderPane layout = new BorderPane();
        layout.setLeft(formulaire);
        layout.setCenter(zoneCentrale);

        stage.setScene(new Scene(layout, 1000, 650));
        stage.show();
    }

    private void initialiserCatalogue() {
        catalogue.add(new Revetement("Vinyle Lino", 15.50));
        catalogue.add(new Revetement("Moquette", 12.00));
        catalogue.add(new Revetement("Papier Peint", 18.00));
        catalogue.add(new Revetement("Peinture", 20.00));
        catalogue.add(new Revetement("Crepis", 45.00));
        catalogue.add(new Revetement("Parquet", 55.00));
    }

    private void initialiserGrille(GraphicsContext gc) {
        gc.setStroke(Color.LIGHTGRAY);
        gc.setLineWidth(0.5);
        for(int i=0; i<600; i+=ECHELLE) gc.strokeLine(i, 0, i, 500);
        for(int i=0; i<500; i+=ECHELLE) gc.strokeLine(0, i, 600, i);
    }

    private void dessinerPlan(GraphicsContext gc) {
        gc.clearRect(0, 0, 600, 500);
        initialiserGrille(gc);
        gc.setStroke(Color.DARKSLATEGRAY);
        gc.setLineWidth(4);

        for (Mur m : tousLesMurs) {
            double x1 = m.getDebut().getX() * ECHELLE + 50;
            double y1 = m.getDebut().getY() * ECHELLE + 50;
            double x2 = m.getFin().getX() * ECHELLE + 50;
            double y2 = m.getFin().getY() * ECHELLE + 50;
            gc.strokeLine(x1, y1, x2, y2);
        }
    }

    public static void main(String[] args) { launch(args); }
}