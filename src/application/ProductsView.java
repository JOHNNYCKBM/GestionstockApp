package application;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class ProductsView {

    public static BorderPane create(MainApp app) {

        BorderPane root = new BorderPane();
        root.setPadding(new Insets(15));

        /* ===================== NAVIGATION ONGLET ===================== */

        HBox nav = new HBox(30);
        nav.setPadding(new Insets(10));
        nav.setAlignment(Pos.CENTER_LEFT);

        Label allTab = createTab("Tous les produits");
        Label lowStockTab = createTab("Faible stock");
        Label expiredTab = createTab("Expirés");

        nav.getChildren().addAll(allTab, lowStockTab, expiredTab);

        VBox content = new VBox(10);
        content.setPadding(new Insets(10));

        // Par défaut : tous les produits
        content.getChildren().setAll(createAllProductsContent());
        setActive(allTab, allTab, lowStockTab, expiredTab);

        allTab.setOnMouseClicked(e -> {
            content.getChildren().setAll(createAllProductsContent());
            setActive(allTab, allTab, lowStockTab, expiredTab);
        });

        lowStockTab.setOnMouseClicked(e -> {
            content.getChildren().setAll(createLowStockContent());
            setActive(lowStockTab, allTab, lowStockTab, expiredTab);
        });

        expiredTab.setOnMouseClicked(e -> {
            content.getChildren().setAll(createExpiredContent());
            setActive(expiredTab, allTab, lowStockTab, expiredTab);
        });

        root.setTop(nav);
        root.setCenter(content);
        return root;
    }

    /* ===================== ONGLET 1 : TOUS LES PRODUITS ===================== */

    private static VBox createAllProductsContent() {

        TextField searchField = new TextField();
        searchField.setPromptText("Recherche en temps réel...");

        TableView<ProductSimple> table = createProductsTable(true);

        ObservableList<ProductSimple> data =
                FXCollections.observableArrayList(ProductDAO.getAllProducts());

        FilteredList<ProductSimple> filtered =
                new FilteredList<>(data, p -> true);

        searchField.textProperty().addListener((obs, old, val) -> {
            filtered.setPredicate(p ->
                p.getName().toLowerCase().contains(val.toLowerCase())
            );
        });

        table.setItems(filtered);

        VBox box = new VBox(10, searchField, table);
        box.setPadding(new Insets(10));
        return box;
    }

    /* ===================== ONGLET 2 : FAIBLE STOCK ===================== */

    private static VBox createLowStockContent() {

        TextField searchField = new TextField();
        searchField.setPromptText("Recherche produit faible stock...");

        TableView<ProductSimple> table = createProductsTable(false);

        ObservableList<ProductSimple> data =
                FXCollections.observableArrayList(ProductDAO.getLowStockProducts());

        FilteredList<ProductSimple> filtered =
                new FilteredList<>(data, p -> true);

        searchField.textProperty().addListener((obs, old, val) -> {
            filtered.setPredicate(p ->
                p.getName().toLowerCase().contains(val.toLowerCase())
            );
        });

        table.setItems(filtered);

        VBox box = new VBox(10, searchField, table);
        box.setPadding(new Insets(10));
        return box;
    }

    /* ===================== ONGLET 3 : EXPIRÉS ===================== */

    private static VBox createExpiredContent() {
        Label label = new Label("Module produits expirés non disponible pour le moment");
        label.setStyle("-fx-font-size:16; -fx-text-fill:gray;");
        return new VBox(20, label);
    }

    /* ===================== TABLE PRODUITS ===================== */

    private static TableView<ProductSimple> createProductsTable(boolean allowDelete) {

        TableView<ProductSimple> table = new TableView<>();

        TableColumn<ProductSimple, Integer> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(c ->
            new javafx.beans.property.SimpleIntegerProperty(
                c.getValue().getId()).asObject()
        );

        TableColumn<ProductSimple, String> nameCol = new TableColumn<>("Produit");
        nameCol.setCellValueFactory(c ->
            new javafx.beans.property.SimpleStringProperty(
                c.getValue().getName())
        );
        /*TableColumn<ProductSimple, Double> categorieCol = new TableColumn<>("Categorie");
        categorieCol.setCellValueFactory(c ->
            new javafx.beans.property.SimpleDoubleProperty(
                c.getValue().get()).asObject()
        );*/

        TableColumn<ProductSimple, Double> priceCol = new TableColumn<>("Prix");
        priceCol.setCellValueFactory(c ->
            new javafx.beans.property.SimpleDoubleProperty(
                c.getValue().getPrice()).asObject()
        );

        TableColumn<ProductSimple, Integer> stockCol = new TableColumn<>("Stock");
        stockCol.setCellValueFactory(c ->
            new javafx.beans.property.SimpleIntegerProperty(
                c.getValue().getQuantity()).asObject()
        );

       table.getColumns().addAll(idCol, nameCol,priceCol, stockCol);

        if (allowDelete) {
            TableColumn<ProductSimple, Void> actionCol = new TableColumn<>("Action");

            actionCol.setCellFactory(col -> new TableCell<>() {

                private final Button deleteBtn = new Button("Supprimer");

                {
                    deleteBtn.setOnAction(e -> {
                        ProductSimple p =
                            getTableView().getItems().get(getIndex());

                        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION,
                                "Supprimer ce produit ?", ButtonType.YES, ButtonType.NO);

                        confirm.showAndWait().ifPresent(b -> {
                            if (b == ButtonType.YES) {
                                ProductDAO.deleteProduct(p.getId());
                                table.setItems(
                                    FXCollections.observableArrayList(
                                        ProductDAO.getAllProducts()));
                            }
                        });
                    });
                }

                @Override
                protected void updateItem(Void item, boolean empty) {
                    super.updateItem(item, empty);
                    setGraphic(empty ? null : deleteBtn);
                }
            });

            table.getColumns().add(actionCol);
        }

        return table;
    }

    /* ===================== ONGLET STYLE ===================== */

    private static Label createTab(String text) {
        Label l = new Label(text);
        l.setStyle("-fx-font-size:16; -fx-cursor:hand;");
        return l;
    }

    private static void setActive(Label active, Label... tabs) {
        for (Label t : tabs) {
            t.setStyle("-fx-font-size:16;");
        }
        active.setStyle("-fx-font-size:16; -fx-font-weight:bold;");
    }
}
