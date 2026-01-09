package application;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

public class Card {

    public static VBox create(String title, String value, Runnable action) {

        Label titleLabel = new Label(title);
        titleLabel.setStyle("-fx-font-size:22;");

        Label valueLabel = new Label(value);
        valueLabel.setStyle("-fx-font-size:18;-fx-text-fill:#2ecc71;-fx-font-weight:bold;");

        VBox card = new VBox(10, titleLabel, valueLabel);
        card.setPadding(new Insets(20));
        card.setAlignment(Pos.CENTER_LEFT);
        card.setPrefSize(340, 117);

        card.setStyle("""
            -fx-background-color:#f5f7fa;
            -fx-background-radius:15;
            -fx-cursor:hand;
            -fx-font-weight:bold;
        """);

        card.setOnMouseClicked(e -> action.run());

        return card;
    }
}
