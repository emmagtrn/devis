package fr.insa.gaspardclovisemma.vue;

import fr.insa.gaspardclovisemma.materiaux.Revetement;
import fr.insa.gaspardclovisemma.modele.Coin;
import fr.insa.gaspardclovisemma.modele.Mur;
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

    private List<Mur> listeMurs = new ArrayList<>();
    private List<Revetement> catalogue = new ArrayList<>();
    private double totalDevis = 0;
    
    private final double ECHELLE = 40.0; // 1 mètre = 40 pixels
    private Canvas canvasPlan;

    @Override
    public void start(Stage stage) {
        stage.setTitle("EstimaBat - Gaspard, Clovis, Emma");

        // --- 1. CHARGEMENT DU CATALOGUE INSA ---
        catalogue.add(new Revetement("Vinyle Lino", 15.50));
        catalogue.add(new Revetement("Moquette", 12.00));
        catalogue.add(new Revetement("Papier Peint", 18.00));
        catalogue.add(new Revetement("Peinture", 20.00));
        catalogue.add(new Revetement("Peinture+", 28.00));
        catalogue.add(new Revetement("Peinture+++", 40.00));
        catalogue.add(new Revetement("Crepis", 45.00));
        catalogue.add(new Revetement("Parquet", 55.00));
        catalogue.add(new Revetement("Plaquettes de parement", 65.00));
        catalogue.add(new Revetement("Marbre", 120.00));

        // --- 2. MENU DE GAUCHE (CONTRÔLES) ---
        VBox menu = new VBox(10);
        menu.setPadding(new Insets(15));
        menu.setPrefWidth(280);
        menu.setStyle("-fx-background-color: #f8f9fa; -fx-border-color: #dee2e6;");

        Label titreSaisie = new Label("1. Tracer un Mur");
        titreSaisie.setStyle("-fx-font-weight: bold;");

        // Champs pour les coordonnées
        HBox coord1 = new HBox(5, new Label("X1:"), new TextField("0"), new Label("Y1:"), new TextField("0"));
        HBox coord2 = new HBox(5, new Label("X2:"), new TextField("5"), new Label("Y2:"), new TextField("0"));
        
        Label titreMateriau = new Label("2. Choisir le revêtement");
        titreMateriau.setStyle("-fx-font-weight: bold;");
        
        ComboBox<String> comboRevetement = new ComboBox<>();
        catalogue.forEach(r -> comboRevetement.getItems().add(r.getNom()));
        comboRevetement.getSelectionModel().selectFirst();
        comboRevetement.setMaxWidth(Double.MAX_VALUE);

        Button btnTracer = new Button("Tracer et Ajouter au Devis");
        btnTracer.setStyle("-fx-background-color: #005088; -fx-text-fill: white;");
        btnTracer.setMaxWidth(Double.MAX_VALUE);

        // Zone de reçu (Devis)
        TextArea recap = new TextArea();
        recap.setEditable(false);
        Label totalLabel = new Label("TOTAL : 0.00 €");
        totalLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 18px; -fx-text-fill: #b30000;");

        menu.getChildren().addAll(
            titreSaisie, coord1, coord2, 
            new Separator(), titreMateriau, comboRevetement, 
            btnTracer, 
            new Separator(), recap, totalLabel
        );

        // --- 3. ZONE DE DESSIN (CENTRE) ---
        canvasPlan = new Canvas(600, 500);
        GraphicsContext gc = canvasPlan.getGraphicsContext2D();
        initialiserGrille(gc);

        // --- 4. LE CERVEAU DU BOUTON ---
        btnTracer.setOnAction(e -> {
            try {
                // On récupère les champs de texte dans les HBox
                double x1 = Double.parseDouble(((TextField)coord1.getChildren().get(1)).getText().replace(",", "."));
                double y1 = Double.parseDouble(((TextField)coord1.getChildren().get(3)).getText().replace(",", "."));
                double x2 = Double.parseDouble(((TextField)coord2.getChildren().get(1)).getText().replace(",", "."));
                double y2 = Double.parseDouble(((TextField)coord2.getChildren().get(3)).getText().replace(",", "."));

                // A. On crée le mur et on le dessine
                Mur nouveauMur = new Mur(new Coin(x1, y1), new Coin(x2, y2));
                listeMurs.add(nouveauMur);
                dessinerPlan(gc);

                // B. On calcule le prix
                double hauteurSousPlafond = 2.50; // Hauteur standard pour commencer
                double surfaceMur = nouveauMur.calculerSurface(hauteurSousPlafond);
                Revetement sel = catalogue.get(comboRevetement.getSelectionModel().getSelectedIndex());
                
                double cout = surfaceMur * sel.getPrixM2();
                totalDevis += cout;

                // C. On met à jour la facture
                recap.appendText(String.format("Mur (%.1fm) - %s : %.2f €\n", nouveauMur.getLongueur(), sel.getNom(), cout));
                totalLabel.setText(String.format("TOTAL : %.2f €", totalDevis));
                
            } catch (NumberFormatException ex) {
                Alert alerte = new Alert(Alert.AlertType.ERROR, "Veuillez entrer uniquement des chiffres (ex: 5.5).");
                alerte.showAndWait();
            }
        });

        BorderPane layout = new BorderPane();
        layout.setLeft(menu);
        layout.setCenter(canvasPlan);

        stage.setScene(new Scene(layout, 900, 550));
        stage.show();
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
        gc.setStroke(Color.BLACK);
        gc.setLineWidth(4);

        for (Mur m : listeMurs) {
            double x1 = m.getDebut().getX() * ECHELLE + 50;
            double y1 = m.getDebut().getY() * ECHELLE + 50;
            double x2 = m.getFin().getX() * ECHELLE + 50;
            double y2 = m.getFin().getY() * ECHELLE + 50;
            gc.strokeLine(x1, y1, x2, y2);
        }
    }

    public static void main(String[] args) { launch(args); }
}