package application;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;

public class HeaderView {

    private static Label titleLabel;
    private static Button dashboardBtn, categoriesBtn, productsBtn, movementsBtn;
    private static Button activeButton;
    
    public static HBox create() {
        // Titre à gauche
        titleLabel = new Label("Accueil");
        titleLabel.setStyle("""
            -fx-text-fill:white;
            -fx-font-size:22;
            -fx-font-weight:bold;
        """);

        // Espace flexible pour pousser les boutons à droite
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        // Boutons de navigation
        dashboardBtn = createNavButton("Dashboard");
        categoriesBtn = createNavButton("Catégories");
        productsBtn = createNavButton("Produits");
        movementsBtn = createNavButton("Mouvements");

        // Marquer Dashboard comme actif par défaut
        setActiveButton(dashboardBtn);

        // Container pour les boutons
        HBox buttonContainer = new HBox(10, dashboardBtn, categoriesBtn, productsBtn, movementsBtn);
        buttonContainer.setAlignment(Pos.CENTER_RIGHT);

        // Configuration de l'en-tête
        HBox header = new HBox(10, titleLabel, spacer, buttonContainer);
        header.setAlignment(Pos.CENTER_LEFT);
        header.setStyle("""
            -fx-background-color:#306e80;
            -fx-padding:10;
        """);

        return header;
    }

    private static Button createNavButton(String text) {
        Button button = new Button(text);
        button.setStyle("""
            -fx-text-fill:white;
            -fx-font-size:14;
            -fx-font-weight:bold;
            -fx-background-color:transparent;
            -fx-border-color:transparent;
            -fx-cursor:hand;
            -fx-padding:5 10 5 10;
        """);
        
        // Effet hover
        button.setOnMouseEntered(e -> {
            if (button != activeButton) {
                button.setStyle("""
                    -fx-text-fill:#a6d8e7;
                    -fx-font-size:14;
                    -fx-font-weight:bold;
                    -fx-background-color:transparent;
                    -fx-border-color:transparent;
                    -fx-cursor:hand;
                    -fx-padding:5 10 5 10;
                """);
            }
        });
        
        button.setOnMouseExited(e -> {
            if (button != activeButton) {
                button.setStyle("""
                    -fx-text-fill:white;
                    -fx-font-size:14;
                    -fx-font-weight:bold;
                    -fx-background-color:transparent;
                    -fx-border-color:transparent;
                    -fx-cursor:hand;
                    -fx-padding:5 10 5 10;
                """);
            }
        });

        return button;
    }

    // Méthodes pour gérer les actions
    public static void setTitle(String title) {
        titleLabel.setText(title);
    }

    public static void setActiveButton(Button button) {
        // Réinitialiser le style du bouton précédent
        if (activeButton != null) {
            activeButton.setStyle("""
                -fx-text-fill:white;
                -fx-font-size:14;
                -fx-font-weight:bold;
                -fx-background-color:transparent;
                -fx-border-color:transparent;
                -fx-cursor:hand;
                -fx-padding:5 10 5 10;
            """);
        }
        
        // Appliquer le nouveau style au bouton actif
        activeButton = button;
        if (activeButton != null) {
            activeButton.setStyle("""
                -fx-text-fill:#ffcc00;
                -fx-font-size:14;
                -fx-font-weight:bold;
                -fx-background-color:rgba(255,255,255,0.1);
                -fx-border-radius:4;
                -fx-background-radius:4;
                -fx-cursor:hand;
                -fx-padding:5 10 5 10;
            """);
        }
    }

    public static Button getDashboardBtn() {
        return dashboardBtn;
    }

    public static Button getCategoriesBtn() {
        return categoriesBtn;
    }

    public static Button getProductsBtn() {
        return productsBtn;
    }

    public static Button getMovementsBtn() {
        return movementsBtn;
    }
}
