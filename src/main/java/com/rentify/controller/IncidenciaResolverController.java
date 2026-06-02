package com.rentify.controller;

import com.rentify.dao.IncidenciaDAO;
import com.rentify.util.Navegacion;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;

import java.io.IOException;

public class IncidenciaResolverController {

    @FXML
    private TextArea txtSolucion;

    @FXML
    private Label lblMensaje;

    private final IncidenciaDAO incidenciaDAO;

    private int idIncidencia;

    public IncidenciaResolverController() {
        this.incidenciaDAO = new IncidenciaDAO();
    }

    public void setIdIncidencia(int idIncidencia) {
        this.idIncidencia = idIncidencia;
    }

    @FXML
    private void guardarSolucion() {
        String solucion = txtSolucion.getText().trim();

        if (solucion.isEmpty()) {
            lblMensaje.setText("Escribe la solución.");
            return;
        }

        boolean actualizado = incidenciaDAO.resolverIncidencia(idIncidencia, solucion);

        if (actualizado) {
            volverAIncidencias();
        } else {
            lblMensaje.setText("No se pudo resolver la incidencia.");
        }
    }

    @FXML
    private void cancelar() {
        volverAIncidencias();
    }

    private void volverAIncidencias() {
        try {
            FXMLLoader loader = Navegacion.cargarVista("/fxml/incidencias.fxml");
            Stage stage = (Stage) txtSolucion.getScene().getWindow();
            Navegacion.cambiarEscena(stage, loader, "Rentify - Incidencias");
        } catch (IOException e) {
            lblMensaje.setText("No se pudo volver a incidencias.");
            e.printStackTrace();
        }
    }
}