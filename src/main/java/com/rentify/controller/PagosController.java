package com.rentify.controller;

import com.rentify.dao.PagoDAO;
import com.rentify.model.PagoTabla;
import com.rentify.util.Navegacion;
import com.rentify.util.Sesion;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

public class PagosController {

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

        configurarVistaSegunRol();
        cargarPagos();
    }

    private void configurarVistaSegunRol() {
        if (Sesion.getUsuarioActual() != null && Sesion.getUsuarioActual().getIdRol() == 2) {
            btnPagar.setVisible(false);
            btnPagar.setManaged(false);
        }
    }

    @FXML
    private void cargarPagos() {
        if (Sesion.getUsuarioActual() == null) {
            mostrarError("No hay sesión activa.");
            return;
        }

        /*
         * Antes de mostrar los pagos, actualizamos automáticamente
         * los pagos vencidos.
         */
        pagoDAO.actualizarPagosVencidos();

        List<PagoTabla> lista;

        if (Sesion.getUsuarioActual().getIdRol() == 2) {
            lista = pagoDAO.listarPagosComoArrendador(
                    Sesion.getUsuarioActual().getIdUsuario()
            );
        } else if (Sesion.getUsuarioActual().getIdRol() == 3) {
            lista = pagoDAO.listarPagosComoArrendatario(
                    Sesion.getUsuarioActual().getIdUsuario()
            );
        } else {
            mostrarError("Este módulo no aplica para este rol.");
            return;
        }

        ObservableList<PagoTabla> datos = FXCollections.observableArrayList(lista);
        tablaPagos.setItems(datos);
    }

    @FXML
    private void pagarPago() {
        if (Sesion.getUsuarioActual() == null || Sesion.getUsuarioActual().getIdRol() != 3) {
            mostrarError("Solo el arrendatario puede pagar.");
            return;
        }

        PagoTabla seleccionado = tablaPagos.getSelectionModel().getSelectedItem();

        if (seleccionado == null) {
            mostrarError("Selecciona un pago.");
            return;
        }

        if (!"Pendiente".equalsIgnoreCase(seleccionado.getEstado())) {
            mostrarError("Solo puedes pagar pagos pendientes.");
            return;
        }

        ChoiceDialog<String> dialog = new ChoiceDialog<>(
                "Transferencia",
                "Transferencia",
                "Efectivo",
                "Tarjeta"
        );
        dialog.setTitle("Pagar");
        dialog.setHeaderText("Selecciona el método de pago");
        dialog.setContentText("Método:");

        Optional<String> resultado = dialog.showAndWait();

        if (resultado.isPresent()) {
            int idMetodoPago = convertirMetodoPagoAId(resultado.get());

            boolean pagado = pagoDAO.pagarPago(seleccionado.getIdPago(), idMetodoPago);

            if (pagado) {
                cargarPagos();
                mostrarInformacion("Pago realizado correctamente.");
            } else {
                mostrarError("No se pudo registrar el pago.");
            }
        }
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

            Stage stage = (Stage) tablaPagos.getScene().getWindow();
            Navegacion.cambiarEscena(stage, loader, titulo);
        } catch (IOException e) {
            mostrarError("No se pudo volver al panel.");
            e.printStackTrace();
        }
    }

    private int convertirMetodoPagoAId(String metodo) {
        return switch (metodo) {
            case "Transferencia" -> 1;
            case "Efectivo" -> 2;
            case "Tarjeta" -> 3;
            default -> 1;
        };
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