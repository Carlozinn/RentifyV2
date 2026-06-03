package com.rentify.controller;

import com.rentify.dao.InmuebleDAO;
import com.rentify.dao.SolicitudArrendamientoDAO;
import com.rentify.model.Inmueble;
import com.rentify.model.InmuebleExplorarTabla;
import com.rentify.util.MonedaUtil;
import com.rentify.util.Navegacion;
import com.rentify.util.Sesion;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public class ExplorarInmueblesController {

    private static final int ROL_ARRENDATARIO = 3;

    @FXML
    private TableView<InmuebleExplorarTabla> tablaInmuebles;

    @FXML
    private TableColumn<InmuebleExplorarTabla, Integer> colId;

    @FXML
    private TableColumn<InmuebleExplorarTabla, String> colTitulo;

    @FXML
    private TableColumn<InmuebleExplorarTabla, String> colCiudad;

    @FXML
    private TableColumn<InmuebleExplorarTabla, String> colTipo;

    @FXML
    private TableColumn<InmuebleExplorarTabla, String> colPrecio;

    @FXML
    private TableColumn<InmuebleExplorarTabla, String> colArrendador;

    @FXML
    private TextField txtCiudadFiltro;

    @FXML
    private ComboBox<String> cbTipoFiltro;

    @FXML
    private TextField txtPrecioMaximoFiltro;

    private final InmuebleDAO inmuebleDAO;
    private final SolicitudArrendamientoDAO solicitudDAO;

    public ExplorarInmueblesController() {
        this.inmuebleDAO = new InmuebleDAO();
        this.solicitudDAO = new SolicitudArrendamientoDAO();
    }

    @FXML
    public void initialize() {
        configurarColumnas();
        configurarFiltros();
        cargarInmuebles();
    }

    private void configurarColumnas() {
        colId.setCellValueFactory(new PropertyValueFactory<>("idInmueble"));
        colTitulo.setCellValueFactory(new PropertyValueFactory<>("titulo"));
        colCiudad.setCellValueFactory(new PropertyValueFactory<>("ciudad"));
        colTipo.setCellValueFactory(new PropertyValueFactory<>("tipo"));
        colPrecio.setCellValueFactory(new PropertyValueFactory<>("precioRenta"));
        colArrendador.setCellValueFactory(new PropertyValueFactory<>("arrendador"));
    }

    private void configurarFiltros() {
        cbTipoFiltro.setItems(FXCollections.observableArrayList(
                "Todos",
                "Casa",
                "Departamento",
                "Local"
        ));

        cbTipoFiltro.setValue("Todos");
    }

    @FXML
    private void cargarInmuebles() {
        if (!esArrendatario()) {
            mostrarError("Solo el arrendatario puede explorar inmuebles.");
            tablaInmuebles.setItems(FXCollections.observableArrayList());
            return;
        }

        List<InmuebleExplorarTabla> lista = inmuebleDAO.listarInmueblesDisponibles();
        ObservableList<InmuebleExplorarTabla> datos = FXCollections.observableArrayList(lista);
        tablaInmuebles.setItems(datos);
    }

    @FXML
    private void filtrarInmuebles() {
        if (!esArrendatario()) {
            mostrarError("Solo el arrendatario puede explorar inmuebles.");
            return;
        }

        String ciudad = txtCiudadFiltro.getText().trim();
        Integer idTipo = convertirTipoAId(cbTipoFiltro.getValue());

        BigDecimal precioMaximo = null;
        String textoPrecio = txtPrecioMaximoFiltro.getText().trim();

        if (!textoPrecio.isEmpty()) {
            try {
                precioMaximo = MonedaUtil.parsear(textoPrecio);

                if (precioMaximo.compareTo(BigDecimal.ZERO) <= 0) {
                    mostrarError("El precio máximo debe ser mayor a 0.");
                    return;
                }

            } catch (NumberFormatException e) {
                mostrarError("El precio máximo debe ser numérico. Ejemplo: 8500 o $8,500.00.");
                return;
            }
        }

        List<InmuebleExplorarTabla> lista = inmuebleDAO.filtrarInmueblesDisponibles(
                ciudad.isEmpty() ? null : ciudad,
                idTipo,
                precioMaximo
        );

        tablaInmuebles.setItems(FXCollections.observableArrayList(lista));
    }

    @FXML
    private void limpiarFiltros() {
        if (!esArrendatario()) {
            mostrarError("Solo el arrendatario puede explorar inmuebles.");
            return;
        }

        txtCiudadFiltro.clear();
        txtPrecioMaximoFiltro.clear();
        cbTipoFiltro.setValue("Todos");
        cargarInmuebles();
    }

    @FXML
    private void verDetalleInmueble() {
        if (!esArrendatario()) {
            mostrarError("Solo el arrendatario puede consultar inmuebles disponibles.");
            return;
        }

        InmuebleExplorarTabla seleccionado = obtenerInmuebleSeleccionado(
                "Selecciona un inmueble para ver el detalle."
        );

        if (seleccionado == null) {
            return;
        }

        try {
            Inmueble inmueble = inmuebleDAO.buscarDetallePorId(seleccionado.getIdInmueble());

            if (inmueble == null) {
                mostrarError("No se encontró el inmueble seleccionado.");
                return;
            }

            FXMLLoader loader = Navegacion.cargarVista("/fxml/inmueble_detalle.fxml");
            InmuebleDetalleController controller = loader.getController();
            controller.setVistaOrigen("explorar");
            controller.cargarDatos(inmueble);

            Stage stage = (Stage) tablaInmuebles.getScene().getWindow();
            Navegacion.cambiarEscena(stage, loader, "Rentify - Detalle de Inmueble");

        } catch (IOException e) {
            mostrarError("No se pudo abrir el detalle del inmueble.");
            e.printStackTrace();
        }
    }

    @FXML
    private void solicitarArrendamiento() {
        if (!esArrendatario()) {
            mostrarError("Solo el arrendatario puede solicitar arrendamientos.");
            return;
        }

        InmuebleExplorarTabla seleccionado = obtenerInmuebleSeleccionado(
                "Selecciona un inmueble para solicitar."
        );

        if (seleccionado == null) {
            return;
        }

        if (!inmuebleDAO.inmuebleDisponible(seleccionado.getIdInmueble())) {
            mostrarError("El inmueble ya no está disponible para solicitud.");
            cargarInmuebles();
            return;
        }

        boolean confirmado = confirmar(
                "Confirmar solicitud",
                "Solicitar arrendamiento",
                "¿Deseas enviar una solicitud para este inmueble?\n\n" +
                        "Inmueble: " + seleccionado.getTitulo() + "\n" +
                        "Precio: " + seleccionado.getPrecioRenta()
        );

        if (!confirmado) {
            return;
        }

        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Solicitud de arrendamiento");
        dialog.setHeaderText("Enviar solicitud para: " + seleccionado.getTitulo());
        dialog.setContentText("Mensaje (opcional):");

        Optional<String> resultado = dialog.showAndWait();

        if (resultado.isEmpty()) {
            return;
        }

        boolean insertado = solicitudDAO.insertarSolicitudSiDisponible(
                Sesion.getUsuarioActual().getIdUsuario(),
                seleccionado.getIdInmueble(),
                resultado.get()
        );

        if (insertado) {
            mostrarInformacion("Solicitud enviada correctamente.");
            cargarInmuebles();
        } else {
            mostrarError("No se pudo enviar la solicitud. El inmueble podría ya no estar disponible.");
            cargarInmuebles();
        }
    }

    @FXML
    private void volverAlPanel() {
        try {
            if (!esArrendatario()) {
                volverALogin();
                return;
            }

            FXMLLoader loader = Navegacion.cargarVista("/fxml/arrendatario.fxml");
            ArrendatarioController controller = loader.getController();
            controller.setNombreUsuario(Sesion.getUsuarioActual().getNombre());

            Stage stage = (Stage) tablaInmuebles.getScene().getWindow();
            Navegacion.cambiarEscena(stage, loader, "Rentify - Arrendatario");

        } catch (IOException e) {
            mostrarError("No se pudo volver al panel.");
            e.printStackTrace();
        }
    }

    private InmuebleExplorarTabla obtenerInmuebleSeleccionado(String mensajeError) {
        InmuebleExplorarTabla seleccionado = tablaInmuebles.getSelectionModel().getSelectedItem();

        if (seleccionado == null) {
            mostrarError(mensajeError);
            return null;
        }

        return seleccionado;
    }

    private Integer convertirTipoAId(String tipo) {
        if (tipo == null || "Todos".equalsIgnoreCase(tipo)) {
            return null;
        }

        return switch (tipo) {
            case "Casa" -> 1;
            case "Departamento" -> 2;
            case "Local" -> 3;
            default -> null;
        };
    }

    private boolean esArrendatario() {
        return Sesion.getUsuarioActual() != null
                && Sesion.getUsuarioActual().getIdRol() == ROL_ARRENDATARIO;
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
        Stage stage = (Stage) tablaInmuebles.getScene().getWindow();
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