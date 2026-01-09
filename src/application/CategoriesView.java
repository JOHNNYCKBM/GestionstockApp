package application;

import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.*;

import java.util.List;

public class CategoriesView {

    public static BorderPane create(MainApp app) {

        BorderPane root = new BorderPane();
        root.setPadding(new Insets(15));
        root.setStyle("-fx-background-color: #f5f7fa;");

        /* ===== MENU GAUCHE ===== */
        VBox menu = new VBox(12);
        menu.setPadding(new Insets(15));
        menu.setStyle("""
            -fx-background-color: white;
            -fx-background-radius: 12;
            -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 10, 0, 0, 2);
            -fx-border-color: #e0e0e0;
            -fx-border-radius: 12;
            -fx-border-width: 1;
            -fx-font-weight:bold;
        """);

        Label menuTitle = new Label("CATÉGORIES");
        menuTitle.setStyle("""
            -fx-font-size: 16px;
            -fx-font-weight: bold;
            -fx-text-fill: #306e80;
            -fx-padding: 0 0 10 0;
        """);

        VBox productsArea = new VBox(10);
        productsArea.setPadding(new Insets(20));
        productsArea.setStyle("""
            -fx-background-color: white;
            -fx-background-radius: 12;
            -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 10, 0, 0, 2);
        """);

        menu.getChildren().add(menuTitle);

        // 🔥 CATÉGORIES DEPUIS LA BASE
        List<String> categories = CategoryDAO.getAllCategories();

        for (String cat : categories) {

            Button btn = new Button(cat);
            btn.setMaxWidth(Double.MAX_VALUE);
            btn.setPrefHeight(40);

            btn.setStyle("""
                -fx-background-color: #f8f9fa;
                -fx-text-fill: #333;
                -fx-font-size: 14px;
                -fx-border-color: #e9ecef;
                -fx-border-radius: 6;
                -fx-background-radius: 6;
                -fx-cursor: hand;
            """);

            btn.setOnMouseEntered(e -> btn.setStyle("""
                -fx-background-color: #306e80;
                -fx-text-fill: white;
                -fx-font-size: 14px;
                -fx-border-color: #306e80;
                -fx-border-radius: 6;
                -fx-background-radius: 6;
            """));

            btn.setOnMouseExited(e -> btn.setStyle("""
                -fx-background-color: #f8f9fa;
                -fx-text-fill: #333;
                -fx-font-size: 14px;
                -fx-border-color: #e9ecef;
                -fx-border-radius: 6;
                -fx-background-radius: 6;
            """));

            btn.setOnAction(e -> showProducts(productsArea, cat));

            menu.getChildren().add(btn);
        }

        Label welcome = new Label("👈 Sélectionnez une catégorie");
        welcome.setStyle("""
            -fx-font-size: 18px;
            -fx-text-fill: #888;
            -fx-font-style: italic;
        """);

        productsArea.getChildren().add(welcome);

        root.setLeft(menu);
        root.setCenter(productsArea);
        BorderPane.setMargin(menu, new Insets(0, 12, 0, 0));

        return root;
    }

    /* ===== PRODUITS PAR CATÉGORIE ===== */

    private static void showProducts(VBox area, String category) {

        area.getChildren().clear();

        Label title = new Label("📦 Produits - " + category);
        title.setStyle("""
            -fx-font-size: 24px;
            -fx-font-weight: bold;
            -fx-text-fill: #306e80;
            -fx-padding: 0 0 20 0;
        """);

        VBox productsList = new VBox(8);
        productsList.setPadding(new Insets(10));

        List<ProductSimple> products =
                CategoryDAO.getProductsByCategory(category);

        if (products.isEmpty()) {
            productsList.getChildren().add(
                new Label("Aucun produit dans cette catégorie.")
            );
        }

        for (ProductSimple p : products) {

            HBox item = new HBox(10);
            item.setPadding(new Insets(8, 12, 8, 12));
            item.setStyle("""
                -fx-background-color: #f8f9fa;
                -fx-background-radius: 8;
            """);

            Label name = new Label(p.getName());
            Label stock = new Label("Stock : " + p.getQuantity());

            stock.setStyle(
                p.getQuantity() < 5
                ? "-fx-text-fill:red; -fx-font-weight:bold;"
                : "-fx-text-fill:#2ecc71; -fx-font-weight:bold;"
            );

            HBox spacer = new HBox();
            HBox.setHgrow(spacer, Priority.ALWAYS);

            item.getChildren().addAll(name, spacer, stock);
            productsList.getChildren().add(item);
        }

        area.getChildren().addAll(title, productsList);
    }
}
