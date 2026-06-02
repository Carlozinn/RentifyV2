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

public class SolicitudesArrendadorController {

    @FXML
    private TableView<SolicitudTabla> tablaSolicitudes;

    @FXML
    private TableColumn<SolicitudTabla, Integer> colId;

    @FXML
    private TableColumn<SolicitudTabla, String> colInmueble;

    @FXML
    private TableColumn<SolicitudTabla, String> colArrendatario;

    @FXML
    private TableColumn<SolicitudTabla, String> colMensaje;

    @FXML
    private TableColumn<SolicitudTabla, String> colFecha;

    @FXML
    private TableColumn<SolicitudTabla, String> colEstado;

    private final SolicitudArrendamientoDAO solicitudDAO;

    public SolicitudesArrendadorController() {
        this.solicitudDAO = new SolicitudArrendamientoDAO();
    }

    @FXML
    public void initialize() {
        colId.setCellValueFactory(new PropertyValueFactory<>("idSolicitud"));
        colInmueble.setCellValueFactory(new PropertyValueFactory<>("inmueble"));
        colArrendatario.setCellValueFactory(new PropertyValueFactory<>("arrendatario"));
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
         * Antes de mostrar solicitudes al arrendador,
         * vencemos automáticamente las solicitudes pendientes antiguas.
         */
        solicitudDAO.actualizarSolicitudesPendientesVencidas();

        List<SolicitudTabla> lista = solicitudDAO.listarSolicitudesPorArrendador(
                Sesion.getUsuarioActual().getIdUsuario()
        );

        ObservableList<SolicitudTabla> datos = FXCollections.observableArrayList(lista);
        tablaSolicitudes.setItems(datos);
    }
    @FXML
    private void aceptarSolicitud() {
        SolicitudTabla seleccionada = tablaSolicitudes.getSelectionModel().getSelectedItem();

        if (seleccionada == null) {
            mostrarError("Selecciona una solicitud.");
            return;
        }

        if (!"Pendiente".equalsIgnoreCase(seleccionada.getEstado())) {
            mostrarError("Solo puedes responder solicitudes pendientes.");
            return;
        }

        try {
            FXMLLoader loader = Navegacion.cargarVista("/fxml/solicitud_aceptar_form.fxml");
            AceptarSolicitudController controller = loader.getController();
            controller.setIdSolicitud(seleccionada.getIdSolicitud());

            Stage stage = (Stage) tablaSolicitudes.getScene().getWindow();
            Navegacion.cambiarEscena(stage, loader, "Rentify - Generar Arrendamiento");

        } catch (IOException e) {
            mostrarError("No se pudo abrir el formulario de aceptación.");
            e.printStackTrace();
        }
    }

    @FXML
    private void rechazarSolicitud() {
        cambiarEstadoSolicitud(3, "Solicitud rechazada correctamente.");
    }

    private void cambiarEstadoSolicitud(int nuevoEstado, String mensajeExito) {
        SolicitudTabla seleccionada = tablaSolicitudes.getSelectionModel().getSelectedItem();

        if (seleccionada == null) {
            mostrarError("Selecciona una solicitud.");
            return;
        }

        if (!"Pendiente".equalsIgnoreCase(seleccionada.getEstado())) {
            mostrarError("Solo puedes responder solicitudes pendientes.");
            return;
        }

        boolean actualizado = solicitudDAO.actualizarEstadoSolicitud(
                seleccionada.getIdSolicitud(),
                nuevoEstado
        );

        if (actualizado) {
            cargarSolicitudes();
            mostrarInformacion(mensajeExito);
        } else {
            mostrarError("No se pudo actualizar la solicitud.");
        }
    }

    @FXML
    private void volverAlPanel() {
        try {
            FXMLLoader loader = Navegacion.cargarVista("/fxml/arrendador.fxml");
            ArrendadorController controller = loader.getController();

            if (Sesion.getUsuarioActual() != null) {
                controller.setNombreUsuario(Sesion.getUsuarioActual().getNombre());
            }

            Stage stage = (Stage) tablaSolicitudes.getScene().getWindow();
            Navegacion.cambiarEscena(stage, loader, "Rentify - Arrendador");
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

    private void mostrarInformacion(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Información");
        alert.setHeaderText("Operación exitosa");
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}