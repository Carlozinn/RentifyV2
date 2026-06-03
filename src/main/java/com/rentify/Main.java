package com.rentify;

import com.rentify.util.Navegacion;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.stage.Screen;
import javafx.stage.Stage;

public class Main extends Application {

    private static final double ANCHO_MINIMO = 1200;
    private static final double ALTO_MINIMO = 750;

    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/fxml/login.fxml")
        );

        Rectangle2D pantalla = Screen.getPrimary().getVisualBounds();

        Scene scene = new Scene(
                loader.load(),
                pantalla.getWidth(),
                pantalla.getHeight()
        );

        scene.getStylesheets().add(
                getClass().getResource("/css/styles.css").toExternalForm()
        );

        primaryStage.setTitle("Rentify - Login");
        primaryStage.setScene(scene);
        primaryStage.setResizable(true);
        primaryStage.setMinWidth(ANCHO_MINIMO);
        primaryStage.setMinHeight(ALTO_MINIMO);

        Navegacion.ajustarPantallaCompleta(primaryStage);
    }

    public static void main(String[] args) {
        launch(args);
    }
}