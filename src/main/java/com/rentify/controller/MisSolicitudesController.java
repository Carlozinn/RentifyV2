package com.rentify.controller;

import com.rentify.dao.SolicitudArrendamientoDAO;
import com.rentify.model.SolicitudTabla;
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

public class MisSolicitudesController {

    @FXML
    private TableView<SolicitudTabla> tablaSolicitudes;

    @FXML
    private TableColumn<SolicitudTabla, Integer> colId;

    @FXML
    private TableColumn<SolicitudTabla, String> colInmueble;

    @FXML
    private TableColumn<SolicitudTabla, String> colMensaje;

    @FXML
    private TableColumn<SolicitudTabla, String> colFecha;

    @FXML
    private TableColumn<SolicitudTabla, String> colEstado;

    private final SolicitudArrendamientoDAO solicitudDAO;

    public MisSolicitudesController() {
        this.solicitudDAO = new SolicitudArrendamientoDAO();
    }

    @FXML
    public void initialize() {
        colId.setCellValueFactory(new PropertyValueFactory<>("idSolicitud"));
        colInmueble.setCellValueFactory(new PropertyValueFactory<>("inmueble"));
        colMensaje.setCellValueFactory(new PropertyValueFactory<>("mensaje"));
        colFecha.setCellValueFactory(new PropertyValueFactory<>("fechaSolicitud"));
        colEstado.setCellValueFactory(new PropertyValueFactory<>("estado"));

        cargarSolicitudes();
    }

    @FXML
    private void cargarSolicitudes() {
        if (Sesion.getUsuarioActual() == null) {
            mostrarError("No hay sesión activa.");
            return;
        }

        /*
         * También actualizamos vencidas cuando el arrendatario consulta
         * sus solicitudes, para que vea el estado actualizado.
         */
        solicitudDAO.actualizarSolicitudesPendientesVencidas();

        List<SolicitudTabla> lista = solicitudDAO.listarSolicitudesPorArrendatario(
                Sesion.getUsuarioActual().getIdUsuario()
        );

        ObservableList<SolicitudTabla> datos = FXCollections.observableArrayList(lista);
        tablaSolicitudes.setItems(datos);
    }

    @FXML
    private void volverAlPanel() {
        try {
            FXMLLoader loader = Navegacion.cargarVista("/fxml/arrendatario.fxml");
            ArrendatarioController controller = loader.getController();

            if (Sesion.getUsuarioActual() != null) {
                controller.setNombreUsuario(Sesion.getUsuarioActual().getNombre());
            }

            Stage stage = (Stage) tablaSolicitudes.getScene().getWindow();
            Navegacion.cambiarEscena(stage, loader, "Rentify - Arrendatario");
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