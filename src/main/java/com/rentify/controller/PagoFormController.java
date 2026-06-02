package com.rentify.controller;

import com.rentify.dao.ArrendamientoDAO;
import com.rentify.dao.PagoDAO;
import com.rentify.util.Navegacion;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import com.rentify.util.MonedaUtil;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;

public class PagoFormController {

    @FXML
    private Label lblTotalMeses;

    @FXML
    private Label lblPagosRegistrados;

    @FXML
    private Label lblPagosRestantes;

    @FXML
    private Label lblSiguientePeriodo;

    @FXML
    private DatePicker dpFechaVencimiento;

    @FXML
    private TextField txtPeriodoMes;

    @FXML
    private TextField txtPeriodoAnio;

    @FXML
    private TextField txtMonto;

    @FXML
    private ComboBox<String> cbMetodoPago;

    @FXML
    private ComboBox<String> cbEstadoPago;

    @FXML
    private TextField txtReferenciaPago;

    @FXML
    private TextField txtComprobantePago;

    @FXML
    private Label lblMensaje;

    private final PagoDAO pagoDAO;
    private final ArrendamientoDAO arrendamientoDAO;

    private int idArrendamiento;
    private LocalDate fechaInicioArrendamiento;
    private LocalDate fechaFinArrendamiento;
    private int totalMeses;
    private int pagosRegistrados;

    public PagoFormController() {
        this.pagoDAO = new PagoDAO();
        this.arrendamientoDAO = new ArrendamientoDAO();
    }

    @FXML
    public void initialize() {
        dpFechaVencimiento.setEditable(false);

        txtPeriodoMes.setEditable(false);
        txtPeriodoAnio.setEditable(false);
        txtMonto.setEditable(false);
        txtReferenciaPago.setEditable(false);
        txtComprobantePago.setEditable(false);

        cbMetodoPago.getItems().addAll("Transferencia", "Efectivo", "Tarjeta");
        cbEstadoPago.getItems().addAll("Pendiente", "Pagado", "Vencido");

        dpFechaVencimiento.valueProperty().addListener((obs, oldValue, newValue) -> {
            if (newValue != null) {
                txtPeriodoMes.setText(String.valueOf(newValue.getMonthValue()));
                txtPeriodoAnio.setText(String.valueOf(newValue.getYear()));
                generarDatosAutomaticos();
            } else {
                txtPeriodoMes.clear();
                txtPeriodoAnio.clear();
                txtReferenciaPago.clear();
                txtComprobantePago.clear();
            }
        });
    }

    public void setIdArrendamiento(int idArrendamiento) {
        this.idArrendamiento = idArrendamiento;

        BigDecimal montoMensual = arrendamientoDAO.obtenerMontoMensualPorId(idArrendamiento);
        if (montoMensual != null) {
            txtMonto.setText(MonedaUtil.formatear(montoMensual));
        }

        LocalDate[] fechas = arrendamientoDAO.obtenerFechasArrendamiento(idArrendamiento);
        if (fechas != null) {
            fechaInicioArrendamiento = fechas[0];
            fechaFinArrendamiento = fechas[1];
        }

        pagosRegistrados = pagoDAO.contarPagosPorArrendamiento(idArrendamiento);
        totalMeses = calcularTotalMeses(fechaInicioArrendamiento, fechaFinArrendamiento);

        lblTotalMeses.setText(String.valueOf(totalMeses));
        lblPagosRegistrados.setText(String.valueOf(pagosRegistrados));
        lblPagosRestantes.setText(String.valueOf(Math.max(totalMeses - pagosRegistrados, 0)));

        LocalDate siguienteFecha = calcularSiguienteFechaSugerida();
        if (siguienteFecha != null) {
            dpFechaVencimiento.setValue(siguienteFecha);
            lblSiguientePeriodo.setText(String.format("%02d/%d",
                    siguienteFecha.getMonthValue(), siguienteFecha.getYear()));
        } else {
            lblSiguientePeriodo.setText("No disponible");
        }

        generarDatosAutomaticos();
    }

    @FXML
    private void guardarPago() {
        if (dpFechaVencimiento.getValue() == null
                || txtPeriodoMes.getText().trim().isEmpty()
                || txtPeriodoAnio.getText().trim().isEmpty()
                || txtMonto.getText().trim().isEmpty()
                || cbEstadoPago.getValue() == null) {
            lblMensaje.setText("Completa los campos obligatorios.");
            return;
        }

        if (pagosRegistrados >= totalMeses && totalMeses > 0) {
            lblMensaje.setText("Ese arrendamiento ya cubrió todos los pagos estimados.");
            return;
        }

        try {
            Date fechaVencimiento = Date.valueOf(dpFechaVencimiento.getValue());
            Integer periodoMes = Integer.parseInt(txtPeriodoMes.getText().trim());
            Integer periodoAnio = Integer.parseInt(txtPeriodoAnio.getText().trim());
            BigDecimal monto = MonedaUtil.parsear(txtMonto.getText());

            if (pagoDAO.existePagoDelPeriodo(idArrendamiento, periodoAnio, periodoMes)) {
                lblMensaje.setText("Ya existe un pago registrado para ese arrendamiento y periodo.");
                return;
            }

            Integer idMetodoPago = convertirMetodoPagoAId(cbMetodoPago.getValue());
            Integer idEstadoPago = convertirEstadoPagoAId(cbEstadoPago.getValue());

            Timestamp fechaPago = null;
            if ("Pagado".equals(cbEstadoPago.getValue())) {
                fechaPago = Timestamp.valueOf(LocalDateTime.now());
                if (idMetodoPago == null) {
                    lblMensaje.setText("Si el pago está pagado, selecciona un método de pago.");
                    return;
                }
            }

            boolean insertado = pagoDAO.insertarPago(
                    idArrendamiento,
                    fechaVencimiento,
                    periodoAnio,
                    periodoMes,
                    monto,
                    idMetodoPago,
                    idEstadoPago,
                    fechaPago,
                    txtReferenciaPago.getText().trim(),
                    txtComprobantePago.getText().trim()
            );

            if (insertado) {
                volverAPagos();
            } else {
                lblMensaje.setText("No se pudo guardar el pago.");
            }

        } catch (NumberFormatException e) {
            lblMensaje.setText("Verifica los campos numéricos.");
        }
    }

    @FXML
    private void cancelar() {
        volverAPagos();
    }

    private LocalDate calcularSiguienteFechaSugerida() {
        Date ultimaFecha = pagoDAO.obtenerUltimaFechaVencimientoPorArrendamiento(idArrendamiento);

        if (ultimaFecha != null) {
            return ultimaFecha.toLocalDate().plusMonths(1);
        }

        return fechaInicioArrendamiento;
    }

    private int calcularTotalMeses(LocalDate inicio, LocalDate fin) {
        if (inicio == null || fin == null || fin.isBefore(inicio)) {
            return 0;
        }

        Period periodo = Period.between(
                inicio.withDayOfMonth(1),
                fin.withDayOfMonth(1)
        );

        return periodo.getYears() * 12 + periodo.getMonths() + 1;
    }

    private void generarDatosAutomaticos() {
        if (idArrendamiento > 0
                && !txtPeriodoAnio.getText().trim().isEmpty()
                && !txtPeriodoMes.getText().trim().isEmpty()) {

            int anio = Integer.parseInt(txtPeriodoAnio.getText().trim());
            int mes = Integer.parseInt(txtPeriodoMes.getText().trim());

            String referencia = "PAG-A" + idArrendamiento + "-" + anio + "-" + String.format("%02d", mes);
            String comprobante = "COMP-A" + idArrendamiento + "-" + anio + "-" + String.format("%02d", mes) + ".pdf";

            txtReferenciaPago.setText(referencia);
            txtComprobantePago.setText(comprobante);
        }
    }

    private Integer convertirMetodoPagoAId(String metodo) {
        if (metodo == null) {
            return null;
        }

        return switch (metodo) {
            case "Transferencia" -> 1;
            case "Efectivo" -> 2;
            case "Tarjeta" -> 3;
            default -> null;
        };
    }

    private Integer convertirEstadoPagoAId(String estado) {
        return switch (estado) {
            case "Pendiente" -> 1;
            case "Pagado" -> 2;
            case "Vencido" -> 3;
            default -> 1;
        };
    }

    private void volverAPagos() {
        try {
            FXMLLoader loader = Navegacion.cargarVista("/fxml/pagos.fxml");
            Stage stage = (Stage) txtMonto.getScene().getWindow();
            Navegacion.cambiarEscena(stage, loader, "Rentify - Pagos");
        } catch (IOException e) {
            lblMensaje.setText("No se pudo volver a pagos.");
            e.printStackTrace();
        }
    }
}