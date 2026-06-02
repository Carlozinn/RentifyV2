package com.rentify.controller;

import com.rentify.dao.ArrendamientoDAO;
import com.rentify.dao.InmuebleDAO;
import com.rentify.dao.SolicitudArrendamientoDAO;
import com.rentify.util.MonedaUtil;
import com.rentify.util.Navegacion;
import com.rentify.util.Sesion;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Date;

public class AceptarSolicitudController {

    @FXML
    private DatePicker dpFechaInicio;

    @FXML
    private DatePicker dpFechaFin;

    @FXML
    private TextField txtMontoMensual;

    @FXML
    private TextField txtDepositoGarantia;

    @FXML
    private ComboBox<Integer> cbDiaPago;

    @FXML
    private TextArea txtObservaciones;

    @FXML
    private Label lblMensaje;

    private final SolicitudArrendamientoDAO solicitudDAO;
    private final ArrendamientoDAO arrendamientoDAO;
    private final InmuebleDAO inmuebleDAO;

    private int idSolicitud;
    private int idInmueble;

    public AceptarSolicitudController() {
        this.solicitudDAO = new SolicitudArrendamientoDAO();
        this.arrendamientoDAO = new ArrendamientoDAO();
        this.inmuebleDAO = new InmuebleDAO();
    }

    @FXML
    public void initialize() {
        dpFechaInicio.setEditable(false);
        dpFechaFin.setEditable(false);

        for (int i = 1; i <= 31; i++) {
            cbDiaPago.getItems().add(i);
        }

        txtMontoMensual.setEditable(false);
    }

    public void setIdSolicitud(int idSolicitud) {
        this.idSolicitud = idSolicitud;

        this.idInmueble = solicitudDAO.obtenerIdInmueblePorSolicitud(idSolicitud);

        BigDecimal precioRenta = inmuebleDAO.obtenerPrecioRentaPorId(idInmueble);
        if (precioRenta != null) {
            txtMontoMensual.setText(MonedaUtil.formatear(precioRenta));
        } else {
            txtMontoMensual.setText(MonedaUtil.formatear(BigDecimal.ZERO));
        }

        /*
         * El depósito es opcional. Se deja en 0 por defecto para evitar valores NULL.
         * No se usa para generar pagos mensuales automáticamente.
         */
        txtDepositoGarantia.setText(MonedaUtil.formatear(BigDecimal.ZERO));
    }

    @FXML
    private void aceptarYGenerar() {
        if (Sesion.getUsuarioActual() == null) {
            lblMensaje.setText("No hay sesión activa.");
            return;
        }

        if (dpFechaInicio.getValue() == null || txtMontoMensual.getText().trim().isEmpty()) {
            lblMensaje.setText("Fecha de inicio y monto mensual son obligatorios.");
            return;
        }

        try {
            Date fechaInicio = Date.valueOf(dpFechaInicio.getValue());
            Date fechaFin = dpFechaFin.getValue() != null
                    ? Date.valueOf(dpFechaFin.getValue())
                    : null;

            BigDecimal montoMensual = MonedaUtil.parsear(txtMontoMensual.getText());

            BigDecimal depositoGarantia = txtDepositoGarantia.getText() == null
                    || txtDepositoGarantia.getText().trim().isEmpty()
                    ? BigDecimal.ZERO
                    : MonedaUtil.parsear(txtDepositoGarantia.getText());

            Integer diaPago = cbDiaPago.getValue();

            int idUsuarioArrendatario = solicitudDAO.obtenerIdUsuarioArrendatarioPorSolicitud(idSolicitud);

            boolean arrendamientoCreado = arrendamientoDAO.insertarArrendamiento(
                    fechaInicio,
                    fechaFin,
                    montoMensual,
                    depositoGarantia,
                    diaPago,
                    txtObservaciones.getText(),
                    idInmueble,
                    Sesion.getUsuarioActual().getIdUsuario(),
                    idUsuarioArrendatario,
                    idSolicitud
            );

            if (!arrendamientoCreado) {
                lblMensaje.setText("No se pudo generar el arrendamiento.");
                return;
            }

            boolean solicitudActualizada = solicitudDAO.actualizarEstadoSolicitud(idSolicitud, 2);
            boolean inmuebleActualizado = inmuebleDAO.actualizarEstadoInmueblePorSolicitud(idInmueble, 2);

            if (solicitudActualizada && inmuebleActualizado) {
                volverASolicitudes();
            } else {
                lblMensaje.setText("Se creó el arrendamiento, pero faltó actualizar otros datos.");
            }

        } catch (NumberFormatException e) {
            lblMensaje.setText("Verifica los campos de dinero.");
        }
    }

    @FXML
    private void cancelar() {
        volverASolicitudes();
    }

    private void volverASolicitudes() {
        try {
            FXMLLoader loader = Navegacion.cargarVista("/fxml/solicitudes_arrendador.fxml");
            Stage stage = (Stage) txtMontoMensual.getScene().getWindow();
            Navegacion.cambiarEscena(stage, loader, "Rentify - Solicitudes Recibidas");
        } catch (IOException e) {
            lblMensaje.setText("No se pudo volver a solicitudes.");
            e.printStackTrace();
        }
    }
}