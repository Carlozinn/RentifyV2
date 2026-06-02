package com.rentify.util;

import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
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

        Scene scene = new Scene(root, ANCHO_MINIMO, ALTO_MINIMO);

        scene.getStylesheets().add(
                Navegacion.class.getResource("/css/styles.css").toExternalForm()
        );

        stage.setTitle(titulo);
        stage.setMinWidth(ANCHO_MINIMO);
        stage.setMinHeight(ALTO_MINIMO);
        stage.setResizable(true);
        stage.setScene(scene);

        stage.show();

        Platform.runLater(() -> {
            stage.setMaximized(true);
            stage.toFront();
        });
    }
}