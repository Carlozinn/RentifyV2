package com.rentify;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    private static final double ANCHO_MINIMO = 1200;
    private static final double ALTO_MINIMO = 750;

    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/fxml/login.fxml")
        );

        Scene scene = new Scene(
                loader.load(),
                ANCHO_MINIMO,
                ALTO_MINIMO
        );

        scene.getStylesheets().add(
                getClass().getResource("/css/styles.css").toExternalForm()
        );

        primaryStage.setTitle("Rentify - Login");
        primaryStage.setMinWidth(ANCHO_MINIMO);
        primaryStage.setMinHeight(ALTO_MINIMO);
        primaryStage.setResizable(true);
        primaryStage.setScene(scene);

        primaryStage.show();

        Platform.runLater(() -> {
            primaryStage.setMaximized(true);
            primaryStage.toFront();
        });
    }

    public static void main(String[] args) {
        launch(args);
    }
}