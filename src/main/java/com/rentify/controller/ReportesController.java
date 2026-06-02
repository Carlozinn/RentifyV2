package com.rentify.controller;

import com.rentify.dao.ReporteDAO;
import com.rentify.model.ReporteResumen;
import com.rentify.util.Navegacion;
import com.rentify.util.Sesion;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Alert;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.List;

public class ReportesController {

    @FXML
    private TableView<ReporteResumen> tablaReportes;

    @FXML
    private TableColumn<ReporteResumen, String> colCategoria;

    @FXML
    private TableColumn<ReporteResumen, String> colConcepto;

    @FXML
    private TableColumn<ReporteResumen, Integer> colTotal;

    private final ReporteDAO reporteDAO;

    public ReportesController() {
        this.reporteDAO = new ReporteDAO();
    }

    @FXML
    public void initialize() {
        colCategoria.setCellValueFactory(new PropertyValueFactory<>("categoria"));
        colConcepto.setCellValueFactory(new PropertyValueFactory<>("concepto"));
        colTotal.setCellValueFactory(new PropertyValueFactory<>("total"));

        cargarReportes();
    }

    @FXML
    private void cargarReportes() {
        List<ReporteResumen> lista = reporteDAO.obtenerResumenGeneral();
        ObservableList<ReporteResumen> datos = FXCollections.observableArrayList(lista);
        tablaReportes.setItems(datos);
    }

    @FXML
    private void volverAlPanel() {
        try {
            FXMLLoader loader = Navegacion.cargarVista("/fxml/admin.fxml");
            AdminController controller = loader.getController();

            if (Sesion.getUsuarioActual() != null) {
                controller.setNombreUsuario(Sesion.getUsuarioActual().getNombre());
            }

            Stage stage = (Stage) tablaReportes.getScene().getWindow();
            Navegacion.cambiarEscena(stage, loader, "Rentify - Administrador");
        } catch (IOException e) {
            mostrarError("No se pudo volver al panel.");
            e.printStackTrace();
        }
    }

    private void mostrarError(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText("Ocurrió un problema");
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}