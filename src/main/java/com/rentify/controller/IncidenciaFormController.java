package com.rentify.controller;

import com.rentify.dao.IncidenciaDAO;
import com.rentify.model.ArrendamientoComboItem;
import com.rentify.util.Navegacion;
import com.rentify.util.Sesion;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.List;

public class IncidenciaFormController {

    @FXML
    private ComboBox<ArrendamientoComboItem> cbArrendamiento;

    @FXML
    private TextField txtTitulo;

    @FXML
    private TextArea txtDescripcion;

    @FXML
    private ComboBox<String> cbPrioridad;

    @FXML
    private Label lblMensaje;

    private final IncidenciaDAO incidenciaDAO;

    public IncidenciaFormController() {
        this.incidenciaDAO = new IncidenciaDAO();
    }

    @FXML
    public void initialize() {
        cbPrioridad.getItems().addAll("Baja", "Media", "Alta");

        if (Sesion.getUsuarioActual() != null && Sesion.getUsuarioActual().getIdRol() == 3) {
            List<ArrendamientoComboItem> items =
                    incidenciaDAO.listarArrendamientosActivosDeArrendatario(Sesion.getUsuarioActual().getIdUsuario());
            cbArrendamiento.getItems().addAll(items);
        }
    }

    @FXML
    private void guardarIncidencia() {
        if (Sesion.getUsuarioActual() == null) {
            lblMensaje.setText("No hay sesión activa.");
            return;
        }

        ArrendamientoComboItem arrendamiento = cbArrendamiento.getValue();
        String titulo = txtTitulo.getText() == null ? "" : txtTitulo.getText().trim();
        String descripcion = txtDescripcion.getText() == null ? "" : txtDescripcion.getText().trim();
        String prioridad = cbPrioridad.getValue();

        if (arrendamiento == null || titulo.isEmpty() || descripcion.isEmpty() || prioridad == null) {
            lblMensaje.setText("Completa todos los campos.");
            return;
        }

        boolean guardado = incidenciaDAO.insertarIncidencia(
                titulo,
                descripcion,
                arrendamiento.getIdArrendamiento(),
                Sesion.getUsuarioActual().getIdUsuario(),
                convertirPrioridadAId(prioridad)
        );

        if (guardado) {
            volverAIncidencias();
        } else {
            lblMensaje.setText("No se pudo guardar la incidencia.");
        }
    }

    @FXML
    private void cancelar() {
        volverAIncidencias();
    }

    private int convertirPrioridadAId(String prioridad) {
        return switch (prioridad) {
            case "Baja" -> 1;
            case "Media" -> 2;
            case "Alta" -> 3;
            default -> 1;
        };
    }

    private void volverAIncidencias() {
        try {
            FXMLLoader loader = Navegacion.cargarVista("/fxml/incidencias.fxml");
            Stage stage = (Stage) txtTitulo.getScene().getWindow();
            Navegacion.cambiarEscena(stage, loader, "Rentify - Incidencias");
        } catch (IOException e) {
            lblMensaje.setText("No se pudo volver a incidencias.");
            e.printStackTrace();
        }
    }
}