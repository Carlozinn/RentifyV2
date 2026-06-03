package com.rentify.util;

import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Screen;
import javafx.stage.Stage;

import java.io.IOException;

public class Navegacion {

    private static final double ANCHO_MINIMO = 1200;
    private static final double ALTO_MINIMO = 750;

    private Navegacion() {
    }

    public static FXMLLoader cargarVista(String rutaFxml) throws IOException {
        FXMLLoader loader = new FXMLLoader(Navegacion.class.getResource(rutaFxml));
        loader.load();
        return loader;
    }

    public static void cambiarEscena(Stage stage, FXMLLoader loader, String titulo) {
        Parent root = loader.getRoot();

        Rectangle2D pantalla = Screen.getPrimary().getVisualBounds();

        Scene scene = new Scene(
                root,
                pantalla.getWidth(),
                pantalla.getHeight()
        );

        scene.getStylesheets().add(
                Navegacion.class.getResource("/css/styles.css").toExternalForm()
        );

        stage.setTitle(titulo);
        stage.setScene(scene);
        stage.setResizable(true);
        stage.setMinWidth(ANCHO_MINIMO);
        stage.setMinHeight(ALTO_MINIMO);

        ajustarPantallaCompleta(stage);
    }

    public static void ajustarPantallaCompleta(Stage stage) {
        Rectangle2D pantalla = Screen.getPrimary().getVisualBounds();

        stage.setMaximized(false);

        stage.setX(pantalla.getMinX());
        stage.setY(pantalla.getMinY());
        stage.setWidth(pantalla.getWidth());
        stage.setHeight(pantalla.getHeight());

        stage.show();

        Platform.runLater(() -> {
            stage.setX(pantalla.getMinX());
            stage.setY(pantalla.getMinY());
            stage.setWidth(pantalla.getWidth());
            stage.setHeight(pantalla.getHeight());
            stage.setMaximized(true);
            stage.toFront();
        });
    }
}