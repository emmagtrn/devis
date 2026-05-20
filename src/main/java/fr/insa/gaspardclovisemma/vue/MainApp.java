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

// La classe hérite d'Application (indispensable pour créer une interface JavaFX)
public class MainApp extends Application {

    // monBatiment : C'est la racine de notre base de données en mémoire (Poupée russe principale)
    private Batiment monBatiment = new Batiment("Batiment_GCE", "Immeuble");
    
    // catalogue : Liste qui stocke les 18 revêtements officiels du sujet
    private List<Revetement> catalogue = new ArrayList<>();
    
    // ECHELLE : 1 mètre dans la réalité = 40 pixels sur notre écran
    private final double ECHELLE = 40.0;
    private Canvas canvasPlan;       // La zone de dessin blanche
    private TextArea zoneDevis;      // La zone de texte pour afficher la facture
    private ComboBox<Integer> selecteurVueNiveau; // Le menu déroulant pour changer d'étage

    @Override
    public void start(Stage stage) {
        // Définition du titre de la fenêtre principale
        stage.setTitle("EstimaBat - Version Finale (Étapes 1 & 2 validées)");

        // Appel de la méthode pour charger les 18 matériaux en mémoire
        initialiserCatalogue();

        // ==========================================
        // PARTIE DROITE : VISUALISATION DU PLAN 2D
        // ==========================================
        VBox zoneCentrale = new VBox(10); // Conteneur vertical avec un espace de 10 pixels entre les éléments
        zoneCentrale.setPadding(new Insets(10)); // Marge intérieure tout autour de la zone
        
        HBox enTeteVue = new HBox(15); // Conteneur horizontal pour aligner le titre et le sélecteur d'étage
        Label lblPlan = new Label("Visualisation du Plan 2D :");
        lblPlan.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;"); // Style CSS pour agrandir le titre
        
        selecteurVueNiveau = new ComboBox<>(); // Menu déroulant des étages disponibles
        enTeteVue.getChildren().addAll(lblPlan, new Label("Étage affiché :"), selecteurVueNiveau);
        
        // Création de la zone de dessin (largeur: 600px, hauteur: 500px)
        canvasPlan = new Canvas(600, 500);
        GraphicsContext gc = canvasPlan.getGraphicsContext2D(); // gc est le "pinceau" virtuel pour dessiner
        initialiserGrille(gc); // On trace la grille de fond grise
        
        zoneCentrale.getChildren().addAll(enTeteVue, canvasPlan);

        // ==========================================
        // PARTIE GAUCHE : FORMULAIRE DE SAISIE
        // ==========================================
        VBox formulaire = new VBox(12);
        formulaire.setPadding(new Insets(15));
        formulaire.setPrefWidth(350); // Largeur fixe du menu pour éviter qu'il ne se déforme
        formulaire.setStyle("-fx-background-color: #f8f9fa; -fx-border-color: #dee2e6;"); // Fond gris clair et bordure

        // Bloc 1 : Emplacement dans la hiérarchie
        Label lblHierarchie = new Label("1. Emplacement");
        lblHierarchie.setStyle("-fx-font-weight: bold;");
        TextField txtNiveau = new TextField("0"); txtNiveau.setPrefWidth(40);
        TextField txtAppart = new TextField("1"); txtAppart.setPrefWidth(40);
        TextField txtPiece = new TextField("1"); txtPiece.setPrefWidth(40);
        HBox boxHierarchie = new HBox(10, new Label("Niv:"), txtNiveau, new Label("App:"), txtAppart, new Label("Pièce:"), txtPiece);

        // Bloc 2 : Choix des revêtements (Menus déroulants)
        ComboBox<String> comboSol = new ComboBox<>();
        ComboBox<String> comboPlafond = new ComboBox<>();
        ComboBox<String> comboMur = new ComboBox<>();
        
        // FILTRAGE INTELLIGENT : On parcourt le catalogue et on trie selon les autorisations du sujet
        for (Revetement r : catalogue) {
            String choix = r.getNom() + " (" + r.getPrixM2() + "€)";
            if (r.isPourSol()) comboSol.getItems().add(choix);
            if (r.isPourPlafond()) comboPlafond.getItems().add(choix);
            if (r.isPourMur()) comboMur.getItems().add(choix);
        }
        // Sélection par défaut du premier élément de chaque liste
        comboSol.getSelectionModel().selectFirst();
        comboPlafond.getSelectionModel().selectFirst();
        comboMur.getSelectionModel().selectFirst();

        // Organisation visuelle des choix de la pièce sous forme de grille
        GridPane gridRevPiece = new GridPane();
        gridRevPiece.setHgap(10); gridRevPiece.setVgap(5);
        gridRevPiece.addRow(0, new Label("Sol Pièce:"), comboSol);
        gridRevPiece.addRow(1, new Label("Plafond Pièce:"), comboPlafond);

        // Bloc 3 : Saisie des coordonnées mathématiques du mur
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
        btnAjouter.setMaxWidth(Double.MAX_VALUE); // Permet au bouton de prendre toute la largeur disponible

        // Bloc 4 : Boutons d'actions et affichage du devis
        Button btnCalculerDevis = new Button("Calculer le Devis");
        Button btnSauvegarder = new Button("Sauvegarder Projet");
        HBox boxActions = new HBox(10, btnCalculerDevis, btnSauvegarder);
        
        zoneDevis = new TextArea();
        zoneDevis.setEditable(false); // L'utilisateur ne peut pas taper de texte dedans, c'est juste de l'affichage
        zoneDevis.setPrefHeight(150);

        // On ajoute tous nos composants dans le formulaire de gauche
        formulaire.getChildren().addAll(
            lblHierarchie, boxHierarchie, gridRevPiece,
            new Separator(), // Ligne de séparation horizontale
            lblMur, coord1, coord2, boxRevMur, btnAjouter,
            new Separator(),
            boxActions, zoneDevis
        );

        // ==========================================
        // LOGIQUE DES ACTIONS (EVENEMENTS)
        // ==========================================
        
        // Événement du sélecteur d'étage : Quand on change de valeur, on redessine le plan de cet étage
        selecteurVueNiveau.setOnAction(e -> dessinerPlan(gc, selecteurVueNiveau.getValue()));

        // Événement du bouton "Ajouter ce Mur"
        btnAjouter.setOnAction(e -> {
            try {
                // 1. Récupération et conversion des textes du formulaire en nombres
                int idNiv = Integer.parseInt(txtNiveau.getText().trim());
                int idApp = Integer.parseInt(txtAppart.getText().trim());
                int idPiece = Integer.parseInt(txtPiece.getText().trim());
                
                // Remplacement des virgules par des points au cas où l'utilisateur se trompe
                double x1 = Double.parseDouble(txtX1.getText().replace(",", "."));
                double y1 = Double.parseDouble(txtY1.getText().replace(",", "."));
                double x2 = Double.parseDouble(txtX2.getText().replace(",", "."));
                double y2 = Double.parseDouble(txtY2.getText().replace(",", "."));

                // 2. Construction automatique de l'arborescence (Gestion des poupées russes)
                Niveau niveau = trouverOuCreerNiveau(idNiv);
                // Si le niveau est nouveau, on l'ajoute dans le menu déroulant du plan
                if (!selecteurVueNiveau.getItems().contains(idNiv)) {
                    selecteurVueNiveau.getItems().add(idNiv);
                    selecteurVueNiveau.getSelectionModel().select((Integer)idNiv);
                }

                Appartement appart = trouverOuCreerAppartement(niveau, idApp);
                Piece piece = trouverOuCreerPiece(appart, idPiece);

                // 3. Attribution des revêtements sélectionnés à la pièce
                piece.setRevetementSol(trouverRevetement(comboSol.getValue()));
                piece.setRevetementPlafond(trouverRevetement(comboPlafond.getValue()));

                // 4. Création du Mur et ajout dans la liste de la pièce
                Mur nouveauMur = new Mur(new Coin(x1, y1), new Coin(x2, y2));
                nouveauMur.setRevetement(trouverRevetement(comboMur.getValue()));
                piece.ajouterMur(nouveauMur);
                
                // 5. Rafraîchissement automatique de la vue 2D
                dessinerPlan(gc, selecteurVueNiveau.getValue());

            } catch (Exception ex) {
                // Si l'utilisateur a écrit du texte dans les cases de nombres, le programme ne plante pas : il affiche une alerte
                new Alert(Alert.AlertType.ERROR, "Veuillez vérifier vos saisies (Nombres valides requis).").showAndWait();
            }
        });

        // Événement du bouton "Calculer le Devis"
        btnCalculerDevis.setOnAction(e -> {
            // On envoie le bâtiment complet au calculateur, et on affiche la chaîne de caractères renvoyée
            String facture = CalculateurDevis.genererFactureDetaillee(monBatiment);
            zoneDevis.setText(facture);
        });

        // Événement du bouton "Sauvegarder"
        btnSauvegarder.setOnAction(e -> {
            // On appelle notre classe utilitaire pour écrire le fichier texte sur le disque dur
            GestionnaireFichier.sauvegarderProjet(monBatiment, "sauvegarde_estimabat.txt");
            Alert ok = new Alert(Alert.AlertType.INFORMATION, "Projet sauvegardé dans le fichier 'sauvegarde_estimabat.txt' !");
            ok.setHeaderText(null);
            ok.showAndWait();
        });

        // Agencement général : Le formulaire à gauche, la zone de dessin au centre
        BorderPane layout = new BorderPane();
        layout.setLeft(formulaire);
        layout.setCenter(zoneCentrale);

        // Création de la scène et affichage de la fenêtre (taille 1050x650 pixels)
        stage.setScene(new Scene(layout, 1050, 650));
        stage.show();
    }

    // --- MÉTHODES RECHERCHE ET CRÉATION AUTOMATIQUE (Évite les doublons) ---
    
    private Niveau trouverOuCreerNiveau(int id) {
        for (Niveau n : monBatiment.getNiveaux()) {
            if (n.getId() == id) return n; // Si le niveau existe déjà, on le renvoie
        }
        // Sinon, on le crée (hauteur sous plafond par défaut de 2.50m) et on l'ajoute au bâtiment
        Niveau nouveau = new Niveau(id, 2.50);
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

    // Méthode pour retrouver l'objet Revetement complet à partir du texte du ComboBox
    private Revetement trouverRevetement(String choixCombo) {
        if (choixCombo == null) return null;
        for (Revetement r : catalogue) {
            if (choixCombo.startsWith(r.getNom())) return r;
        }
        return null;
    }

    // --- MÉTHODES D'AFFICHAGE DU CANVAS ---

    // Dessine le quadrillage en arrière-plan
    private void initialiserGrille(GraphicsContext gc) {
        gc.setStroke(Color.LIGHTGRAY); // Couleur des lignes de la grille
        gc.setLineWidth(0.5); // Épaisseur des lignes
        for(int i=0; i<600; i+=ECHELLE) gc.strokeLine(i, 0, i, 500); // Lignes verticales
        for(int i=0; i<500; i+=ECHELLE) gc.strokeLine(0, i, 600, i); // Lignes horizontales
    }

    // Efface le dessin précédent et retrace uniquement les murs de l'étage sélectionné
    private void dessinerPlan(GraphicsContext gc, Integer niveauAAfficher) {
        gc.clearRect(0, 0, 600, 500); // Nettoyage complet du Canvas (Ardoise magique)
        initialiserGrille(gc); // On remet la grille propre
        
        if (niveauAAfficher == null) return;

        gc.setStroke(Color.DARKSLATEGRAY); // Couleur des murs (gris foncé)
        gc.setLineWidth(4); // Épaisseur des murs pour donner un aspect architectural

        // On parcourt l'arborescence complète pour ne dessiner QUE le niveau demandé
        for (Niveau n : monBatiment.getNiveaux()) {
            if (n.getId() == niveauAAfficher) {
                for (Appartement a : n.getAppartements()) {
                    for (Piece p : a.getPieces()) {
                        for (Mur m : p.getMurs()) {
                            // Conversion mathématique : Coordonnées réelles (mètres) -> Pixels écran
                            // Le "+ 50" sert de marge de sécurité pour que le dessin ne colle pas aux bords
                            double x1 = m.getDebut().getX() * ECHELLE + 50;
                            double y1 = m.getDebut().getY() * ECHELLE + 50;
                            double x2 = m.getFin().getX() * ECHELLE + 50;
                            double y2 = m.getFin().getY() * ECHELLE + 50;
                            gc.strokeLine(x1, y1, x2, y2); // Commande JavaFX qui trace la ligne physique
                        }
                    }
                }
            }
        }
    }

    // Remplissage rigoureux des 18 revêtements officiels du sujet (Id, Nom, Mur?, Sol?, Plafond?, Prix)
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

    public static void main(String[] args) { launch(args); }
}