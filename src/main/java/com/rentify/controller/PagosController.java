package com.rentify.controller;

import com.rentify.dao.PagoDAO;
import com.rentify.model.PagoTabla;
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

public class PagosController {

    private static final int ROL_ARRENDADOR = 2;
    private static final int ROL_ARRENDATARIO = 3;

    @FXML
    private TableView<PagoTabla> tablaPagos;

    @FXML
    private TableColumn<PagoTabla, Integer> colId;

    @FXML
    private TableColumn<PagoTabla, String> colInmueble;

    @FXML
    private TableColumn<PagoTabla, String> colContraparte;

    @FXML
    private TableColumn<PagoTabla, String> colFechaVencimiento;

    @FXML
    private TableColumn<PagoTabla, String> colFechaPago;

    @FXML
    private TableColumn<PagoTabla, String> colPeriodo;

    @FXML
    private TableColumn<PagoTabla, String> colMonto;

    @FXML
    private TableColumn<PagoTabla, String> colMetodoPago;

    @FXML
    private TableColumn<PagoTabla, String> colReferencia;

    @FXML
    private TableColumn<PagoTabla, String> colComprobante;

    @FXML
    private TableColumn<PagoTabla, String> colEstado;

    @FXML
    private Button btnPagar;

    private final PagoDAO pagoDAO;

    public PagosController() {
        this.pagoDAO = new PagoDAO();
    }

    @FXML
    public void initialize() {
        configurarColumnas();
        configurarVistaSegunRol();
        cargarPagos();
    }

    private void configurarColumnas() {
        colId.setCellValueFactory(new PropertyValueFactory<>("idPago"));
        colInmueble.setCellValueFactory(new PropertyValueFactory<>("inmueble"));
        colContraparte.setCellValueFactory(new PropertyValueFactory<>("contraparte"));
        colFechaVencimiento.setCellValueFactory(new PropertyValueFactory<>("fechaVencimiento"));
        colFechaPago.setCellValueFactory(new PropertyValueFactory<>("fechaPago"));
        colPeriodo.setCellValueFactory(new PropertyValueFactory<>("periodo"));
        colMonto.setCellValueFactory(new PropertyValueFactory<>("monto"));
        colMetodoPago.setCellValueFactory(new PropertyValueFactory<>("metodoPago"));
        colReferencia.setCellValueFactory(new PropertyValueFactory<>("referenciaPago"));
        colComprobante.setCellValueFactory(new PropertyValueFactory<>("comprobante"));
        colEstado.setCellValueFactory(new PropertyValueFactory<>("estado"));
    }

    private void configurarVistaSegunRol() {
        boolean puedePagar = esRol(ROL_ARRENDATARIO);

        btnPagar.setVisible(puedePagar);
        btnPagar.setManaged(puedePagar);
    }

    @FXML
    private void cargarPagos() {
        if (Sesion.getUsuarioActual() == null) {
            mostrarError("No hay sesión activa.");
            return;
        }

        pagoDAO.actualizarPagosVencidos();

        List<PagoTabla> lista;

        if (esRol(ROL_ARRENDADOR)) {
            lista = pagoDAO.listarPagosComoArrendador(
                    Sesion.getUsuarioActual().getIdUsuario()
            );
        } else if (esRol(ROL_ARRENDATARIO)) {
            lista = pagoDAO.listarPagosComoArrendatario(
                    Sesion.getUsuarioActual().getIdUsuario()
            );
        } else {
            mostrarError("Este módulo no aplica para este rol.");
            tablaPagos.setItems(FXCollections.observableArrayList());
            return;
        }

        ObservableList<PagoTabla> datos = FXCollections.observableArrayList(lista);
        tablaPagos.setItems(datos);
    }

    @FXML
    private void pagarPago() {
        if (!esRol(ROL_ARRENDATARIO)) {
            mostrarError("Solo el arrendatario puede pagar.");
            return;
        }

        PagoTabla seleccionado = obtenerPagoSeleccionado();

        if (seleccionado == null) {
            return;
        }

        boolean esPendiente = "Pendiente".equalsIgnoreCase(seleccionado.getEstado());
        boolean esVencido = "Vencido".equalsIgnoreCase(seleccionado.getEstado());

        if (!esPendiente && !esVencido) {
            mostrarError("Solo puedes pagar pagos pendientes o vencidos.");
            return;
        }

        boolean confirmado = confirmarPago(seleccionado, esVencido);

        if (!confirmado) {
            return;
        }

        Optional<String> metodoSeleccionado = seleccionarMetodoPago();

        if (metodoSeleccionado.isEmpty()) {
            return;
        }

        int idMetodoPago = convertirMetodoPagoAId(metodoSeleccionado.get());

        boolean pagado = pagoDAO.pagarPago(
                seleccionado.getIdPago(),
                idMetodoPago
        );

        if (pagado) {
            cargarPagos();

            if (esVencido) {
                mostrarInformacion("Pago vencido liquidado correctamente.");
            } else {
                mostrarInformacion("Pago realizado correctamente.");
            }

        } else {
            mostrarError("No se pudo registrar el pago.");
        }
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

            Stage stage = (Stage) tablaPagos.getScene().getWindow();
            Navegacion.cambiarEscena(stage, loader, titulo);

        } catch (IOException e) {
            mostrarError("No se pudo volver al panel.");
            e.printStackTrace();
        }
    }

    private PagoTabla obtenerPagoSeleccionado() {
        PagoTabla seleccionado = tablaPagos.getSelectionModel().getSelectedItem();

        if (seleccionado == null) {
            mostrarError("Selecciona un pago.");
            return null;
        }

        return seleccionado;
    }

    private boolean confirmarPago(PagoTabla pago, boolean esVencido) {
        String mensaje;

        if (esVencido) {
            mensaje =
                    "Este pago está vencido.\n\n" +
                            "Periodo: " + pago.getPeriodo() + "\n" +
                            "Inmueble: " + pago.getInmueble() + "\n" +
                            "Monto: " + pago.getMonto() + "\n\n" +
                            "¿Deseas liquidarlo ahora?";
        } else {
            mensaje =
                    "Vas a registrar el pago siguiente:\n\n" +
                            "Periodo: " + pago.getPeriodo() + "\n" +
                            "Inmueble: " + pago.getInmueble() + "\n" +
                            "Monto: " + pago.getMonto() + "\n\n" +
                            "¿Deseas continuar?";
        }

        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacion.setTitle("Confirmar pago");
        confirmacion.setHeaderText(esVencido ? "Liquidar pago vencido" : "Registrar pago");
        confirmacion.setContentText(mensaje);

        Optional<ButtonType> respuesta = confirmacion.showAndWait();

        return respuesta.isPresent() && respuesta.get() == ButtonType.OK;
    }

    private Optional<String> seleccionarMetodoPago() {
        ChoiceDialog<String> dialog = new ChoiceDialog<>(
                "Transferencia",
                "Transferencia",
                "Efectivo",
                "Tarjeta"
        );

        dialog.setTitle("Método de pago");
        dialog.setHeaderText("Selecciona el método de pago");
        dialog.setContentText("Método:");

        return dialog.showAndWait();
    }

    private int convertirMetodoPagoAId(String metodo) {
        return switch (metodo) {
            case "Transferencia" -> 1;
            case "Efectivo" -> 2;
            case "Tarjeta" -> 3;
            default -> 1;
        };
    }

    private boolean esRol(int idRol) {
        return Sesion.getUsuarioActual() != null
                && Sesion.getUsuarioActual().getIdRol() == idRol;
    }

    private void volverALogin() throws IOException {
        FXMLLoader loader = Navegacion.cargarVista("/fxml/login.fxml");
        Stage stage = (Stage) tablaPagos.getScene().getWindow();
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