package application;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.ButtonType;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

public class MovementsView {

	public static BorderPane create() {
	    return create(null);
	}
	
	public static BorderPane create(String defaultTab) {

	    BorderPane root = new BorderPane();
	    root.setPadding(new Insets(15));

	    HBox nav = new HBox(30);
	    nav.setPadding(new Insets(10));
	    nav.setAlignment(Pos.CENTER_LEFT);

	    Label venteTab = createTab("Vente");
	    Label approTab = createTab("Approvisionnement");
	    Label transferTab = createTab("Transfert");
	    Label histTab = createTab("Historique");

	    nav.getChildren().addAll(venteTab, approTab, transferTab, histTab);

	    // ✅ UNE SEULE déclaration
	    VBox content = new VBox();
	    content.setPadding(new Insets(10));

	    /* ===== Onglet par défaut ===== */
	    if ("VENTE".equals(defaultTab)) {
	        content.getChildren().setAll(createVenteContent());
	        setActive(venteTab, venteTab, approTab, transferTab, histTab);

	    } else if ("APPRO".equals(defaultTab)) {
	        content.getChildren().setAll(createApproContent());
	        setActive(approTab, venteTab, approTab, transferTab, histTab);

	    } else if ("TRANSFERT".equals(defaultTab)) {
	        content.getChildren().setAll(createTransferContent());
	        setActive(transferTab, venteTab, approTab, transferTab, histTab);

	    } else {
	        // header "Mouvements"
	        content.getChildren().setAll(createApproContent());
	        setActive(approTab, venteTab, approTab, transferTab, histTab);
	    }

	    /* ===== Clicks ===== */
	    venteTab.setOnMouseClicked(e -> {
	        content.getChildren().setAll(createVenteContent());
	        setActive(venteTab, venteTab, approTab, transferTab, histTab);
	    });

	    approTab.setOnMouseClicked(e -> {
	        content.getChildren().setAll(createApproContent());
	        setActive(approTab, venteTab, approTab, transferTab, histTab);
	    });

	    transferTab.setOnMouseClicked(e -> {
	        content.getChildren().setAll(createTransferContent());
	        setActive(transferTab, venteTab, approTab, transferTab, histTab);
	    });

	    histTab.setOnMouseClicked(e -> {
	        content.getChildren().setAll(createHistoryContent());
	        setActive(histTab, venteTab, approTab, transferTab, histTab);
	    });

	    root.setTop(nav);
	    root.setCenter(content);
	    return root;
	}
    /* ===================== APPROVISIONNEMENT ===================== */

    private static VBox createApproContent() {

        VBox root = new VBox(10);
        root.setPadding(new Insets(10));

        // Boutons
        Button addProductBtn = new Button("+ Ajouter un produit");
        Button addCategoryBtn = new Button("+ Ajouter une catégorie");

        HBox buttons = new HBox(10, addProductBtn, addCategoryBtn);

        addCategoryBtn.setOnAction(e -> showAddCategoryDialog());
        addProductBtn.setOnAction(e -> showAddProductDialog());

        TextField searchField = new TextField();
        searchField.setPromptText("Rechercher un produit...");

        TableView<ProductSimple> table = createTable("Approvisionner", true);

        FilteredList<ProductSimple> filtered =
            new FilteredList<>(loadProducts(), p -> true);

        searchField.textProperty().addListener((obs, o, n) ->
            filtered.setPredicate(p ->
                p.getName().toLowerCase().contains(n.toLowerCase())
            )
        );

        table.setItems(filtered);

        root.getChildren().addAll(buttons, searchField, table);
        return root;
    }

    /* ===================== VENTE ===================== */

    private static VBox createVenteContent() {

        VBox root = new VBox(10);
        root.setPadding(new Insets(10));

        TextField searchField = new TextField();
        searchField.setPromptText("Rechercher un produit...");

        TableView<ProductSimple> table = createTable("Vendre", false);

        FilteredList<ProductSimple> filtered =
            new FilteredList<>(loadProducts(), p -> true);

        searchField.textProperty().addListener((obs, o, n) ->
            filtered.setPredicate(p ->
                p.getName().toLowerCase().contains(n.toLowerCase())
            )
        );

        table.setItems(filtered);

        root.getChildren().addAll(searchField, table);
        return root;
    }

    /* ===================== TABLE COMMUNE ===================== */

    private static TableView<ProductSimple> createTable(
            String actionLabel, boolean isAppro) {

        TableView<ProductSimple> table = new TableView<>();

        TableColumn<ProductSimple, Integer> idCol =
                new TableColumn<>("ID");
        idCol.setCellValueFactory(c ->
            new SimpleIntegerProperty(c.getValue().getId()).asObject());

        TableColumn<ProductSimple, String> nameCol =
                new TableColumn<>("Nom");
        nameCol.setCellValueFactory(c ->
            new SimpleStringProperty(c.getValue().getName()));

        TableColumn<ProductSimple, Double> priceCol =
                new TableColumn<>("Prix (USD)");
        priceCol.setCellValueFactory(c ->
            new SimpleDoubleProperty(c.getValue().getPrice()).asObject());

        TableColumn<ProductSimple, Integer> stockCol =
                new TableColumn<>("Stock");
        stockCol.setCellValueFactory(c ->
            new SimpleIntegerProperty(c.getValue().getQuantity()).asObject());

        TableColumn<ProductSimple, Void> actionCol =
                new TableColumn<>("Action");

        actionCol.setCellFactory(col -> new TableCell<>() {

            private final TextField qtyField = new TextField();
            private final Button btn = new Button(actionLabel);

            {
                qtyField.setPrefWidth(60);

                btn.setOnAction(e -> {
                    ProductSimple p =
                        getTableView().getItems().get(getIndex());

                    try {
                        int qty = Integer.parseInt(qtyField.getText());

                        boolean ok = isAppro
                                ? StockMovementDAO.supplyProduct(p.getId(), qty)
                                : StockMovementDAO.sellProduct(p.getId(), qty);

                        if (ok) {
                            qtyField.clear();
                            table.setItems(loadProducts());
                        }

                    } catch (Exception ex) {
                        showAlert("Quantité invalide");
                    }
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : new HBox(5, qtyField, btn));
            }
        });

        table.getColumns().addAll(
            idCol, nameCol, priceCol, stockCol, actionCol
        );

        return table;
    }

    /* ===================== DIALOGS ===================== */

    private static void showAddCategoryDialog() {

        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Ajouter une catégorie");
        dialog.setHeaderText(null);
        dialog.setContentText("Nom de la catégorie :");

        dialog.showAndWait().ifPresent(name ->
            CategoryDAO.insertCategory(name)
        );
    }

    private static void showAddProductDialog() {

        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Ajouter un produit");

        TextField nameField = new TextField();
        TextField priceField = new TextField();
        TextField qtyField = new TextField();

        ComboBox<String> categoryBox = new ComboBox<>();
        categoryBox.getItems().addAll(CategoryDAO.getAllCategories());

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);

        grid.addRow(0, new Label("Nom :"), nameField);
        grid.addRow(1, new Label("Prix :"), priceField);
        grid.addRow(2, new Label("Quantité :"), qtyField);
        grid.addRow(3, new Label("Catégorie :"), categoryBox);

        dialog.getDialogPane().setContent(grid);
        dialog.getDialogPane().getButtonTypes()
              .addAll(ButtonType.OK, ButtonType.CANCEL);

        dialog.showAndWait().ifPresent(b -> {
            if (b == ButtonType.OK) {

                int catId =
                    CategoryDAO.getCategoryIdByName(categoryBox.getValue());

                ProductDAO.insertProduct(
                        nameField.getText(),
                        Double.parseDouble(priceField.getText()),
                        Integer.parseInt(qtyField.getText()),
                        catId
                );
            }
        });
    }
    /* ===================== UTILS ===================== */

    private static ObservableList<ProductSimple> loadProducts() {
        return FXCollections.observableArrayList(
            ProductDAO.getAllProducts());
    }

    private static Label createTab(String text) {
        Label l = new Label(text);
        l.setStyle("-fx-font-size:16px;-fx-font-weight:bold;");
        return l;
    }

    private static void setActive(Label active, Label... tabs) {
        for (Label t : tabs)
            t.setStyle("-fx-font-size:16px;-fx-font-weight:bold;");
        active.setStyle(
            "-fx-font-size:16px;-fx-font-weight:bold;-fx-underline:true;");
    }

    private static void showAlert(String msg) {
        Alert a = new Alert(Alert.AlertType.WARNING, msg);
        a.showAndWait();
    }
    private static VBox createHistoryContent() {

        VBox root = new VBox(10);
        root.setPadding(new Insets(10));

        TableView<String[]> table = new TableView<>();

        TableColumn<String[], String> idCol =
            new TableColumn<>("ID");
        idCol.setCellValueFactory(c ->
            new SimpleStringProperty(c.getValue()[0]));

        TableColumn<String[], String> prodCol =
            new TableColumn<>("Produit");
        prodCol.setCellValueFactory(c ->
            new SimpleStringProperty(c.getValue()[1]));

        TableColumn<String[], String> typeCol =
            new TableColumn<>("Type");
        typeCol.setCellValueFactory(c ->
            new SimpleStringProperty(c.getValue()[2]));

        TableColumn<String[], String> qtyCol =
            new TableColumn<>("Quantité");
        qtyCol.setCellValueFactory(c ->
            new SimpleStringProperty(c.getValue()[3]));

        TableColumn<String[], String> dateCol =
            new TableColumn<>("Date");
        dateCol.setCellValueFactory(c ->
            new SimpleStringProperty(c.getValue()[4]));

        table.getColumns().addAll(
            idCol, prodCol, typeCol, qtyCol, dateCol
        );

        table.setItems(FXCollections.observableArrayList(
            StockMovementDAO.getAllMovements()
        ));

        root.getChildren().add(table);
        return root;
    }
    private static VBox createTransferContent() {

        VBox root = new VBox(10);
        root.setPadding(new Insets(10));

        //  TABLE
        TableView<ProductSimple> table = new TableView<>();

        // Bouton ajouter point de vente
        Button addPointBtn = new Button("+ Ajouter un point de vente");
        addPointBtn.setOnAction(e -> showAddPointDialog());

        HBox topBar = new HBox(10, addPointBtn);
        root.getChildren().add(topBar);

        // ================== COLONNES ==================

        TableColumn<ProductSimple, Integer> idCol =
                new TableColumn<>("ID");
        idCol.setCellValueFactory(c ->
            new SimpleIntegerProperty(c.getValue().getId()).asObject());

        TableColumn<ProductSimple, String> nameCol =
                new TableColumn<>("Produit");
        nameCol.setCellValueFactory(c ->
            new SimpleStringProperty(c.getValue().getName()));

        TableColumn<ProductSimple, Integer> stockCol =
                new TableColumn<>("Stock Central");
        stockCol.setCellValueFactory(c ->
            new SimpleIntegerProperty(c.getValue().getQuantity()).asObject());

        TableColumn<ProductSimple, Void> actionCol =
                new TableColumn<>("Action");

        actionCol.setCellFactory(col -> new TableCell<>() {

            TextField qtyField = new TextField();
            ComboBox<String> pointBox = new ComboBox<>();
            Button btn = new Button("Transférer");

            {
                qtyField.setPrefWidth(60);
                pointBox.getItems().addAll(PointVenteDAO.getAllPoints());

                btn.setOnAction(e -> {
                    ProductSimple p =
                            getTableView().getItems().get(getIndex());

                    try {
                        int qty = Integer.parseInt(qtyField.getText());
                        int pointId =
                                PointVenteDAO.getIdByName(pointBox.getValue());

                        boolean ok =
                                StockMovementDAO.transferProduct(
                                        p.getId(), pointId, qty);

                        if (ok) {
                            qtyField.clear();
                            table.setItems(loadProducts());
                            showAlert("Transfert effectué");
                        } else {
                            showAlert("Stock insuffisant");
                        }

                    } catch (Exception ex) {
                        showAlert("Données invalides");
                    }
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null :
                    new HBox(5, qtyField, pointBox, btn));
            }
        });

        table.getColumns().addAll(
                idCol, nameCol, stockCol, actionCol
        );

        table.setItems(loadProducts());

        root.getChildren().add(table);
        return root;
    }   
    private static void showAddPointDialog() {

        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Ajouter un point de vente");
        dialog.setHeaderText(null);
        dialog.setContentText("Nom du point de vente :");

        dialog.showAndWait().ifPresent(name -> {
            if (!name.trim().isEmpty()) {
                PointVenteDAO.insertPoint(name);
            }
        });
    }
}
