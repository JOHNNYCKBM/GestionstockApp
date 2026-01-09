package application;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.layout.*;

public class DashboardView {

    public static VBox create(MainApp app) {
        VBox root = new VBox(50);
        root.setPadding(new Insets(30));
        root.setAlignment(Pos.TOP_CENTER);

        // Background blanc
        BackgroundFill whiteBackground = new BackgroundFill(javafx.scene.paint.Color.WHITE, CornerRadii.EMPTY, Insets.EMPTY);

        // Image de fond
        Image backgroundImage = new Image("/images/Background.jpg");
        BackgroundSize backgroundSize = new BackgroundSize(100, 100, true, true, true, false);
        BackgroundImage bgImage = new BackgroundImage(
                backgroundImage,
                BackgroundRepeat.NO_REPEAT,
                BackgroundRepeat.NO_REPEAT,
                BackgroundPosition.CENTER,
                backgroundSize
        );

        // Appliquer les deux backgrounds
        root.setBackground(new Background(
                new BackgroundFill[]{whiteBackground},  // tableau de BackgroundFill
                new BackgroundImage[]{bgImage}          // tableau de BackgroundImage
        ));

        // Titre
        Label title = new Label("Gestion de Stock");
        title.setStyle("-fx-font-size:40; -fx-font-weight:bold; -fx-font-family:cursive;-fx-text-fill:green; -fx-margin-bottom:40px;");

        // GridPane des cards
        GridPane cards = new GridPane();
        cards.setHgap(20);
        cards.setVgap(60);
        cards.setAlignment(Pos.CENTER);

        
        cards.add(Card.create("Catégories", "10", () -> {
        }), 0, 0);
        
        // Juste cette partie :
        cards.add(Card.create("Catégories", "Module", app::showCategories), 0, 0);
        cards.add(Card.create("Produits", "Module", app::showProducts), 1, 0);

        cards.add(Card.create("Mouvements", "Module", app::showMovements), 2, 0);

        cards.add(Card.create("Vente", "Module", () -> app.showMovements("VENTE")), 0, 1);
        cards.add(Card.create("Approvisionnement", "Module", () -> app.showMovements("APPRO")), 1, 1);
        cards.add(Card.create("Transfert", "Module", () -> app.showMovements("TRANSFERT")), 2, 1);
        
        root.getChildren().addAll(title, cards);
        return root;
    }
}
