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
import javafx.scene.control.ButtonType;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

public class ArrendamientosController {

    private static final int ROL_ARRENDADOR = 2;
    private static final int ROL_ARRENDATARIO = 3;

    private static final int ESTADO_ARRENDAMIENTO_ACTIVO = 1;
    private static final int ESTADO_ARRENDAMIENTO_FINALIZADO = 2;
    private static final int ESTADO_ARRENDAMIENTO_CANCELADO = 3;

    private static final int ESTADO_INMUEBLE_DISPONIBLE = 1;
    private static final int ESTADO_INMUEBLE_OCUPADO = 2;

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
        configurarColumnas();
        configurarVistaSegunRol();
        cargarArrendamientos();
    }

    private void configurarColumnas() {
        colId.setCellValueFactory(new PropertyValueFactory<>("idArrendamiento"));
        colInmueble.setCellValueFactory(new PropertyValueFactory<>("inmueble"));
        colContraparte.setCellValueFactory(new PropertyValueFactory<>("contraparte"));
        colFechaInicio.setCellValueFactory(new PropertyValueFactory<>("fechaInicio"));
        colFechaFin.setCellValueFactory(new PropertyValueFactory<>("fechaFin"));
        colMonto.setCellValueFactory(new PropertyValueFactory<>("montoMensual"));
        colEstado.setCellValueFactory(new PropertyValueFactory<>("estado"));
    }

    private void configurarVistaSegunRol() {
        boolean esArrendador = esRol(ROL_ARRENDADOR);

        mostrarBoton(btnGenerarPago, esArrendador);
        mostrarBoton(btnGenerarContrato, esArrendador);
        mostrarBoton(btnCambiarEstado, esArrendador);
    }

    @FXML
    private void cargarArrendamientos() {
        if (Sesion.getUsuarioActual() == null) {
            mostrarError("No hay sesión activa.");
            tablaArrendamientos.setItems(FXCollections.observableArrayList());
            return;
        }

        List<ArrendamientoTabla> lista;

        if (esRol(ROL_ARRENDADOR)) {
            lista = arrendamientoDAO.listarArrendamientosComoArrendador(
                    Sesion.getUsuarioActual().getIdUsuario()
            );
        } else if (esRol(ROL_ARRENDATARIO)) {
            lista = arrendamientoDAO.listarArrendamientosComoArrendatario(
                    Sesion.getUsuarioActual().getIdUsuario()
            );
        } else {
            mostrarError("Este módulo no aplica para este rol.");
            tablaArrendamientos.setItems(FXCollections.observableArrayList());
            return;
        }

        ObservableList<ArrendamientoTabla> datos = FXCollections.observableArrayList(lista);
        tablaArrendamientos.setItems(datos);
    }

    @FXML
    private void abrirFormularioPago() {
        if (!esRol(ROL_ARRENDADOR)) {
            mostrarError("Solo el arrendador puede generar pagos.");
            return;
        }

        ArrendamientoTabla seleccionado = obtenerArrendamientoSeleccionado();

        if (seleccionado == null) {
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
        if (!esRol(ROL_ARRENDADOR)) {
            mostrarError("Solo el arrendador puede generar contratos.");
            return;
        }

        ArrendamientoTabla seleccionado = obtenerArrendamientoSeleccionado();

        if (seleccionado == null) {
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
        if (!esRol(ROL_ARRENDADOR)) {
            mostrarError("Solo el arrendador puede cambiar el estado del arrendamiento.");
            return;
        }

        ArrendamientoTabla seleccionado = obtenerArrendamientoSeleccionado();

        if (seleccionado == null) {
            return;
        }

        if (!"Activo".equalsIgnoreCase(seleccionado.getEstado())) {
            mostrarError("Solo puedes finalizar o cancelar arrendamientos activos.");
            return;
        }

        ChoiceDialog<String> dialog = new ChoiceDialog<>(
                "Finalizado",
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

        boolean confirmado = confirmar(
                "Confirmar cambio de estado",
                "Cambiar arrendamiento a " + nuevoEstado,
                "¿Deseas cambiar este arrendamiento a estado " + nuevoEstado + "?\n\n" +
                        "Inmueble: " + seleccionado.getInmueble() + "\n" +
                        "Contraparte: " + seleccionado.getContraparte() + "\n\n" +
                        "El inmueble volverá a quedar disponible."
        );

        if (!confirmado) {
            return;
        }

        int idEstadoArrendamiento = convertirEstadoArrendamientoAId(nuevoEstado);

        boolean arrendamientoActualizado = arrendamientoDAO.actualizarEstadoArrendamiento(
                seleccionado.getIdArrendamiento(),
                idEstadoArrendamiento
        );

        if (!arrendamientoActualizado) {
            mostrarError("No se pudo actualizar el estado del arrendamiento.");
            return;
        }

        int idInmueble = arrendamientoDAO.obtenerIdInmueblePorArrendamiento(
                seleccionado.getIdArrendamiento()
        );

        if (idInmueble > 0) {
            boolean inmuebleActualizado = inmuebleDAO.actualizarEstadoInmueble(
                    idInmueble,
                    ESTADO_INMUEBLE_DISPONIBLE
            );

            if (!inmuebleActualizado) {
                mostrarError("El arrendamiento se actualizó, pero no se pudo actualizar el estado del inmueble.");
                cargarArrendamientos();
                return;
            }
        }

        cargarArrendamientos();
        mostrarInfo("Estado del arrendamiento actualizado correctamente.");
    }

    @FXML
    private void volverAlPanel() {
        try {
            if (Sesion.getUsuarioActual() == null) {
                volverALogin();
                return;
            }

            FXMLLoader loader;
            String titulo;

            if (esRol(ROL_ARRENDADOR)) {
                loader = Navegacion.cargarVista("/fxml/arrendador.fxml");
                ArrendadorController controller = loader.getController();
                controller.setNombreUsuario(Sesion.getUsuarioActual().getNombre());
                titulo = "Rentify - Arrendador";
            } else if (esRol(ROL_ARRENDATARIO)) {
                loader = Navegacion.cargarVista("/fxml/arrendatario.fxml");
                ArrendatarioController controller = loader.getController();
                controller.setNombreUsuario(Sesion.getUsuarioActual().getNombre());
                titulo = "Rentify - Arrendatario";
            } else {
                mostrarError("Este módulo no aplica para este rol.");
                return;
            }

            Stage stage = (Stage) tablaArrendamientos.getScene().getWindow();
            Navegacion.cambiarEscena(stage, loader, titulo);

        } catch (IOException e) {
            mostrarError("No se pudo volver al panel.");
            e.printStackTrace();
        }
    }

    private ArrendamientoTabla obtenerArrendamientoSeleccionado() {
        ArrendamientoTabla seleccionado = tablaArrendamientos.getSelectionModel().getSelectedItem();

        if (seleccionado == null) {
            mostrarError("Selecciona un arrendamiento.");
            return null;
        }

        return seleccionado;
    }

    private int convertirEstadoArrendamientoAId(String estado) {
        return switch (estado) {
            case "Activo" -> ESTADO_ARRENDAMIENTO_ACTIVO;
            case "Finalizado" -> ESTADO_ARRENDAMIENTO_FINALIZADO;
            case "Cancelado" -> ESTADO_ARRENDAMIENTO_CANCELADO;
            default -> ESTADO_ARRENDAMIENTO_ACTIVO;
        };
    }

    private boolean esRol(int idRol) {
        return Sesion.getUsuarioActual() != null
                && Sesion.getUsuarioActual().getIdRol() == idRol;
    }

    private void mostrarBoton(Button boton, boolean visible) {
        boton.setVisible(visible);
        boton.setManaged(visible);
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
        Stage stage = (Stage) tablaArrendamientos.getScene().getWindow();
        Navegacion.cambiarEscena(stage, loader, "Rentify - Login");
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