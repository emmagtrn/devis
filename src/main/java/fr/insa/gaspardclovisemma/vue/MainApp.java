package fr.insa.gaspardclovisemma.vue;

// Importation des outils nécessaires pour faire fonctionner notre application.
import fr.insa.gaspardclovisemma.materiaux.*; // Importe toutes nos classes de matériaux (Revetement, Porte, Fenetre...)
import fr.insa.gaspardclovisemma.modele.*;   // Importe toutes nos classes d'architecture (Batiment, Mur, Piece...)

// Importations spécifiques à JavaFX (la bibliothèque qui permet de créer des fenêtres avec des boutons)
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

/**
 * La classe MainApp hérite de "Application". C'est obligatoire en JavaFX.
 * Cela indique à Java que ce fichier n'est pas un code classique, mais une fenêtre graphique.
 */
public class MainApp extends Application {

    // =========================================================================
    // VARIABLES GLOBALES (Accessibles partout dans ce fichier)
    // =========================================================================
    
    // Création de l'objet de base. C'est notre "disque dur" en mémoire le temps que le programme tourne.
    private Batiment monBatiment = new Batiment("Batiment_GCE", "Immeuble");
    
    // Une liste dynamique (ArrayList) qui va stocker les matériaux définis par le sujet.
    private List<Revetement> catalogue = new ArrayList<>();
    
    // Variables de mémorisation pour la fonction "Annuler". 
    // Elles stockent temporairement le dernier mur créé et la pièce où il a été mis.
    private Mur dernierMurAjoute;
    private Piece pieceDuDernierMur;

    // ECHELLE : Constante (final) qui définit que 1 mètre = 40 pixels sur notre écran.
    private final double ECHELLE = 40.0;
    
    // Composants de l'interface qu'on déclare ici car plusieurs méthodes doivent pouvoir les modifier.
    private Canvas canvasPlan; // L'objet qui permet de dessiner (notre feuille de papier)
    private TextArea zoneDevis; // La grosse zone de texte où s'affiche la facture
    private ComboBox<Integer> selecteurVueNiveau; // La liste déroulante pour choisir l'étage

    /**
     * La méthode "start" est le moteur de JavaFX. C'est elle qui s'exécute au lancement.
     * "Stage" représente la fenêtre de l'application (le cadre de la fenêtre Windows/Mac).
     */
    @Override
    public void start(Stage stage) {
        // On donne un titre à notre fenêtre
        stage.setTitle("EstimaBat - Édition Professionnelle");
        
        // On lance notre méthode pour remplir le catalogue avec les 18 matériaux du sujet
        initialiserCatalogue();

        // =========================================================================
        // PARTIE 1 : CONSTRUCTION DU CÔTÉ DROIT (LA ZONE DE DESSIN)
        // =========================================================================
        
        // VBox (Vertical Box) : Un conteneur invisible qui empile ses éléments de haut en bas.
        // Le "10" est l'espacement (spacing) en pixels entre chaque élément empilé.
        VBox zoneCentrale = new VBox(10);
        // Insets : Ajoute des marges à l'intérieur du conteneur (10 pixels sur les 4 côtés) pour aérer.
        zoneCentrale.setPadding(new Insets(10));
        
        // HBox (Horizontal Box) : Un conteneur qui aligne ses éléments de gauche à droite.
        HBox enTeteVue = new HBox(15);
        
        // Label : Un simple texte affiché à l'écran, que l'utilisateur ne peut pas modifier.
        Label lblPlan = new Label("Visualisation du Plan 2D :");
        // setStyle : Permet d'injecter du code CSS pour modifier l'apparence (taille, gras...).
        lblPlan.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
        
        // ComboBox : Une liste déroulante. Le "<Integer>" signifie qu'elle contiendra des nombres entiers (les étages).
        selecteurVueNiveau = new ComboBox<>();
        // On ajoute le titre et le menu déroulant dans notre boîte horizontale
        enTeteVue.getChildren().addAll(lblPlan, new Label("Étage affiché :"), selecteurVueNiveau);
        
        // Canvas : Zone de dessin libre. Ici, on lui donne une largeur de 600px et une hauteur de 500px.
        canvasPlan = new Canvas(600, 500);
        // GraphicsContext : C'est le "stylo" associé à notre Canvas. Il permet de tracer des lignes, des carrés...
        GraphicsContext gc = canvasPlan.getGraphicsContext2D();
        // On appelle notre méthode perso pour tracer une grille grise sur le Canvas
        initialiserGrille(gc);
        
        // On ajoute l'en-tête (le menu) et le Canvas dans notre grande boîte verticale de droite
        zoneCentrale.getChildren().addAll(enTeteVue, canvasPlan);

        // =========================================================================
        // PARTIE 2 : CONSTRUCTION DU CÔTÉ GAUCHE (LE FORMULAIRE)
        // =========================================================================
        
        // On crée la boîte verticale principale pour notre menu de gauche
        VBox formulaire = new VBox(12);
        formulaire.setPadding(new Insets(15));
        formulaire.setPrefWidth(480); // On fixe la largeur à 480 pixels pour être sûr que tout le texte rentre.
        // On met un fond gris clair (#f8f9fa) et une légère bordure grise (#dee2e6) pour séparer la zone.
        formulaire.setStyle("-fx-background-color: #f8f9fa; -fx-border-color: #dee2e6;");

        // --- SECTION A : Hiérarchie ---
        // TextField : Une case blanche où l'utilisateur peut taper du texte. On y met une valeur par défaut.
        TextField txtNiveau = new TextField("0"); txtNiveau.setPrefWidth(40);
        TextField txtAppart = new TextField("1"); txtAppart.setPrefWidth(40);
        TextField txtPiece = new TextField("1"); txtPiece.setPrefWidth(40);
        // On aligne horizontalement ces 3 cases avec leurs étiquettes (Labels)
        HBox boxHierarchie = new HBox(10, new Label("Niveau :"), txtNiveau, new Label("Appartement :"), txtAppart, new Label("Salle/Pièce :"), txtPiece);

        // --- SECTION B : Matériaux ---
        // Création des 4 listes déroulantes pour les revêtements
        ComboBox<String> comboSol = new ComboBox<>();
        ComboBox<String> comboPlafond = new ComboBox<>();
        ComboBox<String> comboMurCote1 = new ComboBox<>();
        ComboBox<String> comboMurCote2 = new ComboBox<>(); 
        
        // Boucle For-Each : Pour chaque "Revetement r" présent dans notre "catalogue"
        for (Revetement r : catalogue) {
            // On prépare le texte qui s'affichera dans la liste (Nom + Prix)
            String choix = r.getNom() + " (" + r.getPrixM2() + " €/m²)";
            // Logique de tri : Si le matériau est autorisé pour le sol, on l'ajoute à la liste du sol, etc.
            if (r.isPourSol()) comboSol.getItems().add(choix);
            if (r.isPourPlafond()) comboPlafond.getItems().add(choix);
            if (r.isPourMur()) {
                comboMurCote1.getItems().add(choix);
                comboMurCote2.getItems().add(choix);
            }
        }
        // selectFirst() : On force la liste à sélectionner le 1er élément pour ne pas avoir une case vide
        comboSol.getSelectionModel().selectFirst(); comboPlafond.getSelectionModel().selectFirst();
        comboMurCote1.getSelectionModel().selectFirst(); comboMurCote2.getSelectionModel().selectFirst();

        // GridPane : Un tableau invisible (avec des lignes et des colonnes) pour bien aligner les éléments.
        GridPane gridDetails = new GridPane();
        gridDetails.setHgap(10); // Espace horizontal entre les colonnes
        gridDetails.setVgap(10); // Espace vertical entre les lignes
        // addRow(numLigne, Colonne1, Colonne2) : Place les éléments dans le tableau
        gridDetails.addRow(0, new Label("Revêtement Sol Salle :"), comboSol);
        gridDetails.addRow(1, new Label("Revêtement Plafond Salle :"), comboPlafond);
        gridDetails.addRow(2, new Label("Revêtement Mur (Côté 1) :"), comboMurCote1);
        gridDetails.addRow(3, new Label("Revêtement Mur (Côté 2) :"), comboMurCote2);

        // --- SECTION C : Coordonnées du mur ---
        TextField txtX1 = new TextField("0"); txtX1.setPrefWidth(45);
        TextField txtY1 = new TextField("0"); txtY1.setPrefWidth(45);
        TextField txtX2 = new TextField("5"); txtX2.setPrefWidth(45);
        TextField txtY2 = new TextField("0"); txtY2.setPrefWidth(45);
        HBox coord1 = new HBox(8, new Label("X1 :"), txtX1, new Label("Y1 :"), txtY1, new Label("X2 :"), txtX2, new Label("Y2 :"), txtY2);
        
        TextField txtPortes = new TextField("0"); txtPortes.setPrefWidth(45);
        TextField txtFenetres = new TextField("0"); txtFenetres.setPrefWidth(45);
        HBox boxOuvertures = new HBox(15, new Label("Nombre Portes :"), txtPortes, new Label("Nombre Fenêtres :"), txtFenetres);

        // --- SECTION D : Boutons ---
        // Button : Un bouton cliquable standard
        Button btnAjouter = new Button("Tracer ce Mur");
        // On colore le bouton en vert avec un texte blanc gras via CSS
        btnAjouter.setStyle("-fx-background-color: #28a745; -fx-text-fill: white; -fx-font-weight: bold;");
        
        Button btnAnnuler = new Button("✖ Supprimer Dernier Mur");
        btnAnnuler.setStyle("-fx-background-color: #dc3545; -fx-text-fill: white;"); // Rouge
        // setDisable(true) : Rend le bouton gris et incliquable au lancement du programme
        btnAnnuler.setDisable(true);
        HBox boxBoutonsMur = new HBox(10, btnAjouter, btnAnnuler);

        Button btnCalculerDevis = new Button("Calculer Devis");
        Button btnSauvegarder = new Button("Sauvegarder");
        HBox boxActions = new HBox(10, btnCalculerDevis, btnSauvegarder);
        
        // TextArea : Une grosse case permettant d'afficher plusieurs lignes de texte (idéal pour le devis)
        zoneDevis = new TextArea(); 
        // setEditable(false) : Empêche l'utilisateur d'écrire dedans au clavier
        zoneDevis.setEditable(false); 
        zoneDevis.setPrefHeight(150); // Fixe la hauteur à 150 pixels

        // Assemblage final : On ajoute tout dans la grande boîte "formulaire"
        // Les "new Separator()" tracent une ligne horizontale fine pour délimiter les sections
        formulaire.getChildren().addAll(
            new Label("1. Emplacement de la structure"), boxHierarchie, new Separator(),
            new Label("2. Choix des matériaux (Par surfaces)"), gridDetails, new Separator(),
            new Label("3. Paramètres géométriques du segment"), coord1, boxOuvertures,
            boxBoutonsMur, new Separator(), boxActions, zoneDevis
        );

        // =========================================================================
        // PARTIE 3 : LOGIQUE INTERACTIVE (ÉVÉNEMENTS CLICS)
        // =========================================================================

        // setOnAction(e -> ...) : Déclenche une action quand l'utilisateur interagit avec l'élément.
        // Ici, si on change d'étage dans le menu déroulant, on appelle la méthode qui redessine le plan.
        selecteurVueNiveau.setOnAction(e -> dessinerPlan(gc, selecteurVueNiveau.getValue()));

        // Que se passe-t-il quand on clique sur "Tracer ce mur" ?
        btnAjouter.setOnAction(e -> {
            // Le bloc "try" tente d'exécuter le code. S'il y a une erreur (ex: on tape la lettre 'A' au lieu d'un chiffre), 
            // il s'arrête et saute directement au bloc "catch" en bas, évitant que le logiciel ne crash.
            try {
                // Integer.parseInt() : Convertit le texte tapé en nombre entier (int).
                // .trim() : Enlève les espaces tapés par erreur avant ou après le chiffre.
                int idNiv = Integer.parseInt(txtNiveau.getText().trim());
                int idApp = Integer.parseInt(txtAppart.getText().trim());
                int idPiece = Integer.parseInt(txtPiece.getText().trim());
                
                // Recherche ou création des éléments dans notre architecture (Poupées russes)
                Niveau niveau = trouverOuCreerNiveau(idNiv);
                
                // Si c'est un nouvel étage, on rajoute son numéro dans le menu déroulant de visualisation
                if (!selecteurVueNiveau.getItems().contains(idNiv)) {
                    selecteurVueNiveau.getItems().add(idNiv); 
                    selecteurVueNiveau.getSelectionModel().select((Integer)idNiv); // On l'affiche direct
                }
                Appartement appart = trouverOuCreerAppartement(niveau, idApp);
                Piece piece = trouverOuCreerPiece(appart, idPiece);

                // On applique les revêtements sélectionnés pour cette pièce
                piece.setRevetementSol(trouverRevetement(comboSol.getValue()));
                piece.setRevetementPlafond(trouverRevetement(comboPlafond.getValue()));

                // Double.parseDouble() : Convertit le texte en nombre à virgule (double).
                // .replace(",", ".") : Si l'utilisateur tape "2,5" on le transforme en "2.5" car Java exige des points.
                double x1 = Double.parseDouble(txtX1.getText().replace(",", "."));
                double y1 = Double.parseDouble(txtY1.getText().replace(",", "."));
                double x2 = Double.parseDouble(txtX2.getText().replace(",", "."));
                double y2 = Double.parseDouble(txtY2.getText().replace(",", "."));

                // On crée notre objet Mur avec ses 2 points (Début et Fin)
                Mur nouveauMur = new Mur(new Coin(x1, y1), new Coin(x2, y2));
                // On lui applique les revêtements de la liste déroulante
                nouveauMur.setRevetementCote1(trouverRevetement(comboMurCote1.getValue()));
                nouveauMur.setRevetementCote2(trouverRevetement(comboMurCote2.getValue()));
                
                // Traitement des ouvertures
                int nbPortes = Integer.parseInt(txtPortes.getText().trim());
                int nbFenetres = Integer.parseInt(txtFenetres.getText().trim());
                // Boucle "For" : Répète l'action "i" fois pour créer le bon nombre de portes et fenêtres
                for(int i=0; i<nbPortes; i++) nouveauMur.ajouterOuverture(new Porte());
                for(int i=0; i<nbFenetres; i++) nouveauMur.ajouterOuverture(new Fenetre());

                // On attache enfin ce mur terminé à la pièce correspondante
                piece.ajouterMur(nouveauMur);
                
                // On met en mémoire ce mur pour que le bouton d'annulation sache quoi supprimer
                dernierMurAjoute = nouveauMur;
                pieceDuDernierMur = piece;
                btnAnnuler.setDisable(false); // On réactive (dégrise) le bouton de suppression
                
                // On demande au stylo de retracer l'écran pour afficher le nouveau mur
                dessinerPlan(gc, selecteurVueNiveau.getValue());
                
            } catch (Exception ex) {
                // S'il y a eu une erreur de conversion de texte en chiffre, on affiche une "Alert" (Pop-up rouge)
                new Alert(Alert.AlertType.ERROR, "Données d'entrée erronées. Vérifiez les formats.").showAndWait();
            }
        });

        // Que se passe-t-il quand on clique sur "Supprimer Dernier Mur" ?
        btnAnnuler.setOnAction(e -> {
            // On vérifie que la mémoire n'est pas vide
            if (dernierMurAjoute != null && pieceDuDernierMur != null) {
                // On supprime physiquement l'objet mur de la liste des murs de la pièce
                pieceDuDernierMur.getMurs().remove(dernierMurAjoute); 
                dernierMurAjoute = null; // On vide la mémoire pour ne pas le supprimer deux fois
                pieceDuDernierMur = null;
                btnAnnuler.setDisable(true); // On re-grise le bouton
                dessinerPlan(gc, selecteurVueNiveau.getValue()); // On met à jour le dessin
            }
        });

        // Quand on clique sur Devis, on appelle notre classe "CalculateurDevis" et on met le résultat dans la zone de texte
        btnCalculerDevis.setOnAction(e -> zoneDevis.setText(CalculateurDevis.genererFactureDetaillee(monBatiment)));
        
        // Quand on clique sur Sauvegarder, on appelle la classe "GestionnaireFichier"
        btnSauvegarder.setOnAction(e -> GestionnaireFichier.sauvegarderProjet(monBatiment, "sauvegarde_estimabat.txt"));

        // =========================================================================
        // PARTIE 4 : AFFICHAGE FINAL DE LA FENÊTRE
        // =========================================================================
        
        // BorderPane : C'est un conteneur principal très pratique qui a des emplacements (Gauche, Droite, Centre, Haut, Bas)
        BorderPane layout = new BorderPane();
        layout.setLeft(formulaire); // On place notre menu vertical à gauche
        layout.setCenter(zoneCentrale); // On place la zone de dessin au centre/droite
        
        // Scene : Contient notre BorderPane. C'est l'intérieur de la fenêtre (taille 1120x650)
        stage.setScene(new Scene(layout, 1120, 650));
        // On affiche enfin le tout à l'écran !
        stage.show();
    }
    
    // =========================================================================
    // MÉTHODES UTILITAIRES (Pour fouiller dans les listes)
    // =========================================================================
    
    // Ces méthodes parcourent les listes avec une boucle For-Each pour trouver si l'élément (ex: l'étage 1) existe déjà.
    // S'il n'existe pas, la méthode le crée avec "new", l'ajoute dans le bâtiment, et le renvoie.
    private Niveau trouverOuCreerNiveau(int id) {
        for (Niveau n : monBatiment.getNiveaux()) if (n.getId() == id) return n;
        Niveau nouveau = new Niveau(id, 2.50); monBatiment.ajouterNiveau(nouveau); return nouveau;
    }
    
    private Appartement trouverOuCreerAppartement(Niveau n, int id) {
        for (Appartement a : n.getAppartements()) if (a.getId() == id) return a;
        Appartement nouveau = new Appartement(id); n.ajouterAppartement(nouveau); return nouveau;
    }
    
    private Piece trouverOuCreerPiece(Appartement a, int id) {
        for (Piece p : a.getPieces()) if (p.getId() == id) return p;
        Piece nouvelle = new Piece(id); a.ajouterPiece(nouvelle); return nouvelle;
    }
    
    // Permet de retrouver l'objet Revetement (avec son prix) juste à partir de son Nom affiché dans la liste déroulante
    private Revetement trouverRevetement(String choix) {
        if (choix == null) return null; // Sécurité si la liste est vide
        // startsWith : Vérifie si le choix commence par le nom du revêtement
        for (Revetement r : catalogue) if (choix.startsWith(r.getNom())) return r;
        return null;
    }

    // =========================================================================
    // MOTEUR DE DESSIN (CANVAS)
    // =========================================================================

    /**
     * Dessine une grille en arrière-plan
     * @param gc Le pinceau de dessin
     */
    private void initialiserGrille(GraphicsContext gc) {
        gc.setStroke(Color.LIGHTGRAY); // Couleur de la ligne
        gc.setLineWidth(0.5); // Épaisseur du trait
        // Boucle : i augmente de la valeur de l'ECHELLE (40) à chaque tour
        for(int i=0; i<600; i+=ECHELLE) gc.strokeLine(i, 0, i, 500); // Lignes verticales
        for(int i=0; i<500; i+=ECHELLE) gc.strokeLine(0, i, 600, i); // Lignes horizontales
    }

    /**
     * Méthode qui efface tout et redessine tous les murs du niveau actuel.
     */
    private void dessinerPlan(GraphicsContext gc, Integer niveauAAfficher) {
        // clearRect : Gomme la totalité du dessin existant
        gc.clearRect(0, 0, 600, 500);
        initialiserGrille(gc); // On retrace la grille par-dessus le vide
        
        if (niveauAAfficher == null) return; // Si aucun étage n'est choisi, on arrête ici.

        // On descend dans toute l'arborescence...
        for (Niveau n : monBatiment.getNiveaux()) {
            if (n.getId() == niveauAAfficher) {
                for (Appartement a : n.getAppartements()) {
                    for (Piece p : a.getPieces()) {
                        
                        // 1. DESSIN DES MURS DE LA PIÈCE
                        gc.setLineWidth(4); // Murs épais (4 pixels)
                        for (Mur m : p.getMurs()) {
                            // Code couleur
                            if (m == dernierMurAjoute) {
                                gc.setStroke(Color.RED); // Surligne le dernier mur en rouge
                            } else {
                                gc.setStroke(Color.DARKSLATEGRAY); // Les autres murs sont gris foncé
                            }
                            
                            // CONVERSION MATHÉMATIQUE (Valeur en Mètres * Échelle 40) + Marge 50 pixels
                            double x1 = m.getDebut().getX() * ECHELLE + 50, y1 = m.getDebut().getY() * ECHELLE + 50;
                            double x2 = m.getFin().getX() * ECHELLE + 50, y2 = m.getFin().getY() * ECHELLE + 50;
                            gc.strokeLine(x1, y1, x2, y2); // Ordre effectif de tracer la ligne
                        }

                        // 2. ÉCRITURE DU NOM DE LA PIÈCE AU CENTRE
                        if (!p.getMurs().isEmpty()) {
                            double minX = Double.MAX_VALUE, maxX = -Double.MAX_VALUE;
                            double minY = Double.MAX_VALUE, maxY = -Double.MAX_VALUE;
                            
                            // On cherche le point le plus à gauche, à droite, en haut et en bas de la pièce
                            for (Mur m : p.getMurs()) {
                                minX = Math.min(minX, Math.min(m.getDebut().getX(), m.getFin().getX()));
                                maxX = Math.max(maxX, Math.max(m.getDebut().getX(), m.getFin().getX()));
                                minY = Math.min(minY, Math.min(m.getDebut().getY(), m.getFin().getY()));
                                maxY = Math.max(maxY, Math.max(m.getDebut().getY(), m.getFin().getY()));
                            }
                            
                            // On calcule le milieu mathématique de la pièce pour placer le texte
                            double centreX = ((minX + maxX) / 2) * ECHELLE + 50;
                            double centreY = ((minY + maxY) / 2) * ECHELLE + 50;
                            
                            // Configuration de la police de texte (Bleu, Arial, Gras, taille 12)
                            gc.setFill(Color.BLUE);
                            gc.setFont(Font.font("Arial", FontWeight.BOLD, 12));
                            // On écrit "Salle n°X" à l'emplacement calculé. (Le -22 et +4 servent à centrer le texte sur le point).
                            gc.fillText("Salle n°" + p.getId(), centreX - 22, centreY + 4);
                        }
                    }
                }
            }
        }
    }

    /**
     * Remplit la base de données brute avec les 18 matériaux du cahier des charges.
     */
    private void initialiserCatalogue() {
        catalogue.clear(); // On vide pour être sûr
        // Paramètres : ID, Nom, Autorisé sur Mur?, Autorisé sur Sol?, Autorisé sur Plafond?, Prix unitaire
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
    
    // Le vrai point d'entrée du programme. "launch(args)" va automatiquement appeler notre méthode "start()".
    public static void main(String[] args) { launch(args); }
}