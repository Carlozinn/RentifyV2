package com.rentify.controller;

import com.rentify.dao.ArrendamientoDAO;
import com.rentify.dao.ContratoDAO;
import com.rentify.dao.InmuebleDAO;
import com.rentify.model.ArrendamientoTabla;
import com.rentify.util.Navegacion;
import com.rentify.util.Sesion;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

public class ArrendamientosController {

    @FXML
    private TableView<ArrendamientoTabla> tablaArrendamientos;

    @FXML
    private TableColumn<ArrendamientoTabla, Integer> colId;

    @FXML
    private TableColumn<ArrendamientoTabla, String> colInmueble;

    @FXML
    private TableColumn<ArrendamientoTabla, String> colContraparte;

    @FXML
    private TableColumn<ArrendamientoTabla, String> colFechaInicio;

    @FXML
    private TableColumn<ArrendamientoTabla, String> colFechaFin;

    @FXML
    private TableColumn<ArrendamientoTabla, String> colMonto;

    @FXML
    private TableColumn<ArrendamientoTabla, String> colEstado;

    @FXML
    private Button btnGenerarPago;

    @FXML
    private Button btnGenerarContrato;

    @FXML
    private Button btnCambiarEstado;

    private final ArrendamientoDAO arrendamientoDAO;
    private final ContratoDAO contratoDAO;
    private final InmuebleDAO inmuebleDAO;

    public ArrendamientosController() {
        this.arrendamientoDAO = new ArrendamientoDAO();
        this.contratoDAO = new ContratoDAO();
        this.inmuebleDAO = new InmuebleDAO();
    }

    @FXML
    public void initialize() {
        colId.setCellValueFactory(new PropertyValueFactory<>("idArrendamiento"));
        colInmueble.setCellValueFactory(new PropertyValueFactory<>("inmueble"));
        colContraparte.setCellValueFactory(new PropertyValueFactory<>("contraparte"));
        colFechaInicio.setCellValueFactory(new PropertyValueFactory<>("fechaInicio"));
        colFechaFin.setCellValueFactory(new PropertyValueFactory<>("fechaFin"));
        colMonto.setCellValueFactory(new PropertyValueFactory<>("montoMensual"));
        colEstado.setCellValueFactory(new PropertyValueFactory<>("estado"));

        configurarVistaSegunRol();
        cargarArrendamientos();
    }

    private void configurarVistaSegunRol() {
        if (Sesion.getUsuarioActual() != null && Sesion.getUsuarioActual().getIdRol() == 3) {
            btnGenerarPago.setVisible(false);
            btnGenerarPago.setManaged(false);

            btnGenerarContrato.setVisible(false);
            btnGenerarContrato.setManaged(false);

            btnCambiarEstado.setVisible(false);
            btnCambiarEstado.setManaged(false);
        }
    }

    @FXML
    private void cargarArrendamientos() {
        if (Sesion.getUsuarioActual() == null) {
            mostrarError("No hay sesión activa.");
            return;
        }

        List<ArrendamientoTabla> lista;

        if (Sesion.getUsuarioActual().getIdRol() == 2) {
            lista = arrendamientoDAO.listarArrendamientosComoArrendador(
                    Sesion.getUsuarioActual().getIdUsuario()
            );
        } else if (Sesion.getUsuarioActual().getIdRol() == 3) {
            lista = arrendamientoDAO.listarArrendamientosComoArrendatario(
                    Sesion.getUsuarioActual().getIdUsuario()
            );
        } else {
            mostrarError("Este módulo no aplica para este rol.");
            return;
        }

        ObservableList<ArrendamientoTabla> datos = FXCollections.observableArrayList(lista);
        tablaArrendamientos.setItems(datos);
    }

    @FXML
    private void abrirFormularioPago() {
        if (Sesion.getUsuarioActual() == null || Sesion.getUsuarioActual().getIdRol() != 2) {
            mostrarError("Solo el arrendador puede generar pagos.");
            return;
        }

        ArrendamientoTabla seleccionado = tablaArrendamientos.getSelectionModel().getSelectedItem();

        if (seleccionado == null) {
            mostrarError("Selecciona un arrendamiento para generar pago.");
            return;
        }

        if (!"Activo".equalsIgnoreCase(seleccionado.getEstado())) {
            mostrarError("Solo se pueden generar pagos para arrendamientos activos.");
            return;
        }

        if (!contratoDAO.existeContratoParaArrendamiento(seleccionado.getIdArrendamiento())) {
            mostrarError("Primero debes generar el contrato del arrendamiento.");
            return;
        }

        if (!contratoDAO.contratoFirmadoParaArrendamiento(seleccionado.getIdArrendamiento())) {
            mostrarError("Solo puedes generar pagos cuando el contrato esté firmado.");
            return;
        }

        try {
            FXMLLoader loader = Navegacion.cargarVista("/fxml/pago_form.fxml");
            PagoFormController controller = loader.getController();
            controller.setIdArrendamiento(seleccionado.getIdArrendamiento());

            Stage stage = (Stage) tablaArrendamientos.getScene().getWindow();
            Navegacion.cambiarEscena(stage, loader, "Rentify - Generar Pago");
        } catch (IOException e) {
            mostrarError("No se pudo abrir el formulario de pago.");
            e.printStackTrace();
        }
    }

    @FXML
    private void abrirFormularioContrato() {
        if (Sesion.getUsuarioActual() == null || Sesion.getUsuarioActual().getIdRol() != 2) {
            mostrarError("Solo el arrendador puede generar contratos.");
            return;
        }

        ArrendamientoTabla seleccionado = tablaArrendamientos.getSelectionModel().getSelectedItem();

        if (seleccionado == null) {
            mostrarError("Selecciona un arrendamiento para generar contrato.");
            return;
        }

        if (!"Activo".equalsIgnoreCase(seleccionado.getEstado())) {
            mostrarError("Solo se pueden generar contratos para arrendamientos activos.");
            return;
        }

        if (contratoDAO.existeContratoParaArrendamiento(seleccionado.getIdArrendamiento())) {
            mostrarError("Ese arrendamiento ya tiene contrato.");
            return;
        }

        try {
            FXMLLoader loader = Navegacion.cargarVista("/fxml/contrato_form.fxml");
            ContratoFormController controller = loader.getController();
            controller.setIdArrendamiento(seleccionado.getIdArrendamiento());

            Stage stage = (Stage) tablaArrendamientos.getScene().getWindow();
            Navegacion.cambiarEscena(stage, loader, "Rentify - Generar Contrato");
        } catch (IOException e) {
            mostrarError("No se pudo abrir el formulario de contrato.");
            e.printStackTrace();
        }
    }

    @FXML
    private void cambiarEstadoArrendamiento() {
        if (Sesion.getUsuarioActual() == null || Sesion.getUsuarioActual().getIdRol() != 2) {
            mostrarError("Solo el arrendador puede cambiar el estado del arrendamiento.");
            return;
        }

        ArrendamientoTabla seleccionado = tablaArrendamientos.getSelectionModel().getSelectedItem();

        if (seleccionado == null) {
            mostrarError("Selecciona un arrendamiento.");
            return;
        }

        ChoiceDialog<String> dialog = new ChoiceDialog<>(
                seleccionado.getEstado(),
                "Activo",
                "Finalizado",
                "Cancelado"
        );
        dialog.setTitle("Cambiar estado");
        dialog.setHeaderText("Cambiar estado del arrendamiento");
        dialog.setContentText("Nuevo estado:");

        Optional<String> resultado = dialog.showAndWait();

        if (resultado.isEmpty()) {
            return;
        }

        String nuevoEstado = resultado.get();
        int idEstadoArrendamiento = convertirEstadoArrendamientoAId(nuevoEstado);

        boolean actualizado = arrendamientoDAO.actualizarEstadoArrendamiento(
                seleccionado.getIdArrendamiento(),
                idEstadoArrendamiento
        );

        if (!actualizado) {
            mostrarError("No se pudo actualizar el estado del arrendamiento.");
            return;
        }

        int idInmueble = arrendamientoDAO.obtenerIdInmueblePorArrendamiento(seleccionado.getIdArrendamiento());

        if (idInmueble > 0) {
            int idEstadoInmueble;

            if ("Activo".equalsIgnoreCase(nuevoEstado)) {
                idEstadoInmueble = 2;
            } else {
                idEstadoInmueble = 1;
            }

            inmuebleDAO.actualizarEstadoInmueble(idInmueble, idEstadoInmueble);
        }

        cargarArrendamientos();
        mostrarInfo("Estado del arrendamiento actualizado correctamente.");
    }

    private int convertirEstadoArrendamientoAId(String estado) {
        return switch (estado) {
            case "Activo" -> 1;
            case "Finalizado" -> 2;
            case "Cancelado" -> 3;
            default -> 1;
        };
    }

    @FXML
    private void volverAlPanel() {
        try {
            FXMLLoader loader;
            String titulo;

            if (Sesion.getUsuarioActual() != null && Sesion.getUsuarioActual().getIdRol() == 2) {
                loader = Navegacion.cargarVista("/fxml/arrendador.fxml");
                ArrendadorController controller = loader.getController();
                controller.setNombreUsuario(Sesion.getUsuarioActual().getNombre());
                titulo = "Rentify - Arrendador";
            } else {
                loader = Navegacion.cargarVista("/fxml/arrendatario.fxml");
                ArrendatarioController controller = loader.getController();
                controller.setNombreUsuario(Sesion.getUsuarioActual().getNombre());
                titulo = "Rentify - Arrendatario";
            }

            Stage stage = (Stage) tablaArrendamientos.getScene().getWindow();
            Navegacion.cambiarEscena(stage, loader, titulo);
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

    private void mostrarInfo(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Información");
        alert.setHeaderText("Operación exitosa");
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}