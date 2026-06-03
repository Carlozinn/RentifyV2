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
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

public class SolicitudesArrendadorController {

    private static final int ROL_ARRENDADOR = 2;

    private static final int ESTADO_SOLICITUD_RECHAZADA = 3;

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
        configurarColumnas();
        cargarSolicitudes();
    }

    private void configurarColumnas() {
        colId.setCellValueFactory(new PropertyValueFactory<>("idSolicitud"));
        colInmueble.setCellValueFactory(new PropertyValueFactory<>("inmueble"));
        colArrendatario.setCellValueFactory(new PropertyValueFactory<>("arrendatario"));
        colMensaje.setCellValueFactory(new PropertyValueFactory<>("mensaje"));
        colFecha.setCellValueFactory(new PropertyValueFactory<>("fechaSolicitud"));
        colEstado.setCellValueFactory(new PropertyValueFactory<>("estado"));
    }

    @FXML
    private void cargarSolicitudes() {
        if (!esArrendador()) {
            mostrarError("Solo el arrendador puede consultar solicitudes recibidas.");
            tablaSolicitudes.setItems(FXCollections.observableArrayList());
            return;
        }

        solicitudDAO.actualizarSolicitudesPendientesVencidas();

        List<SolicitudTabla> lista = solicitudDAO.listarSolicitudesPorArrendador(
                Sesion.getUsuarioActual().getIdUsuario()
        );

        ObservableList<SolicitudTabla> datos = FXCollections.observableArrayList(lista);
        tablaSolicitudes.setItems(datos);
    }

    @FXML
    private void aceptarSolicitud() {
        if (!esArrendador()) {
            mostrarError("Solo el arrendador puede aceptar solicitudes.");
            return;
        }

        SolicitudTabla seleccionada = obtenerSolicitudSeleccionada();

        if (seleccionada == null) {
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
        if (!esArrendador()) {
            mostrarError("Solo el arrendador puede rechazar solicitudes.");
            return;
        }

        SolicitudTabla seleccionada = obtenerSolicitudSeleccionada();

        if (seleccionada == null) {
            return;
        }

        if (!"Pendiente".equalsIgnoreCase(seleccionada.getEstado())) {
            mostrarError("Solo puedes responder solicitudes pendientes.");
            return;
        }

        boolean confirmado = confirmar(
                "Confirmar rechazo",
                "Rechazar solicitud",
                "¿Deseas rechazar esta solicitud?\n\n" +
                        "Inmueble: " + seleccionada.getInmueble() + "\n" +
                        "Arrendatario: " + seleccionada.getArrendatario()
        );

        if (!confirmado) {
            return;
        }

        cambiarEstadoSolicitud(
                seleccionada,
                ESTADO_SOLICITUD_RECHAZADA,
                "Solicitud rechazada correctamente."
        );
    }

    private void cambiarEstadoSolicitud(
            SolicitudTabla solicitud,
            int nuevoEstado,
            String mensajeExito
    ) {
        boolean actualizado = solicitudDAO.actualizarEstadoSolicitud(
                solicitud.getIdSolicitud(),
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
            if (!esArrendador()) {
                volverALogin();
                return;
            }

            FXMLLoader loader = Navegacion.cargarVista("/fxml/arrendador.fxml");
            ArrendadorController controller = loader.getController();
            controller.setNombreUsuario(Sesion.getUsuarioActual().getNombre());

            Stage stage = (Stage) tablaSolicitudes.getScene().getWindow();
            Navegacion.cambiarEscena(stage, loader, "Rentify - Arrendador");

        } catch (IOException e) {
            mostrarError("No se pudo volver al panel.");
            e.printStackTrace();
        }
    }

    private SolicitudTabla obtenerSolicitudSeleccionada() {
        SolicitudTabla seleccionada = tablaSolicitudes.getSelectionModel().getSelectedItem();

        if (seleccionada == null) {
            mostrarError("Selecciona una solicitud.");
            return null;
        }

        return seleccionada;
    }

    private boolean esArrendador() {
        return Sesion.getUsuarioActual() != null
                && Sesion.getUsuarioActual().getIdRol() == ROL_ARRENDADOR;
    }

    private boolean confirmar(String titulo, String encabezado, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(encabezado);
        alert.setContentText(mensaje);

        Optional<ButtonType> resultado = alert.showAndWait();

        return resultado.isPresent() && resultado.get() == ButtonType.OK;
    }

    private void volverALogin() throws IOException {
        FXMLLoader loader = Navegacion.cargarVista("/fxml/login.fxml");
        Stage stage = (Stage) tablaSolicitudes.getScene().getWindow();
        Navegacion.cambiarEscena(stage, loader, "Rentify - Login");
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