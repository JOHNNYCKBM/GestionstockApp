package application;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class MainApp extends Application {

    private BorderPane root;

    @Override
    public void start(Stage stage) {
    	
    	try {
    	    DBConnection.getConnection();
    	    System.out.println("Connexion MySQL OK");
    	} catch (Exception e) {
    	    e.printStackTrace();
    	}

        root = new BorderPane();

        // Header avec boutons de navigation
        root.setTop(HeaderView.create());

        // Affichage initial : Dashboard
        showDashboard();

        // Configuration des actions des boutons
        setupNavigation();

        Scene scene = new Scene(root, 1200, 700);
        stage.getIcons().add(new Image(getClass().getResourceAsStream("/images/logo.png")));
        stage.setTitle("Gestion de Stock");
        stage.setScene(scene);
        stage.show();
    }

    private void setupNavigation() {
        // Action pour le bouton Dashboard
        HeaderView.getDashboardBtn().setOnAction(e -> showDashboard());
        
        // Action pour le bouton Catégories
        HeaderView.getCategoriesBtn().setOnAction(e -> showCategories());
        
        // Action pour le bouton Produits
        HeaderView.getProductsBtn().setOnAction(e -> showProducts());
        
        // Action pour le bouton Mouvements
        HeaderView.getMovementsBtn().setOnAction(e -> showMovements());
    }

    /* ===== Navigation ===== */

    public void showDashboard() {
        HeaderView.setTitle("Dashboard");
        HeaderView.setActiveButton(HeaderView.getDashboardBtn());
        root.setCenter(DashboardView.create(this));
    }

    public void showCategories() {
        HeaderView.setTitle("Catégories");
        HeaderView.setActiveButton(HeaderView.getCategoriesBtn());
        root.setCenter(CategoriesView.create(this));
    }

    public void showProducts() {
        HeaderView.setTitle("Produits");
        HeaderView.setActiveButton(HeaderView.getProductsBtn());
        // Créez votre vue Produits si elle n'existe pas encore
        root.setCenter(ProductsView.create(this));
    }

    public void showMovements() {
        HeaderView.setTitle("Mouvements");
        HeaderView.setActiveButton(HeaderView.getMovementsBtn());
        // Créez votre vue Mouvements si elle n'existe pas encore
        root.setCenter(MovementsView.create());
    }

    private javafx.scene.layout.VBox createProductsView() {
        javafx.scene.control.Label label = new javafx.scene.control.Label("Interface Produits");
        label.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");
        javafx.scene.layout.VBox vbox = new javafx.scene.layout.VBox(20, label);
        vbox.setAlignment(javafx.geometry.Pos.CENTER);
        return vbox;
    }

    private javafx.scene.layout.VBox createMovementsView() {
        javafx.scene.control.Label label = new javafx.scene.control.Label("Interface Mouvements");
        label.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");
        javafx.scene.layout.VBox vbox = new javafx.scene.layout.VBox(20, label);
        vbox.setAlignment(javafx.geometry.Pos.CENTER);
        return vbox;
    }

    public void showMovements(String tab) {
        HeaderView.setTitle("Mouvements");
        HeaderView.setActiveButton(HeaderView.getMovementsBtn());
        root.setCenter(MovementsView.create(tab));
    }
    
    public static void main(String[] args) {
        launch();
    }
    
}
