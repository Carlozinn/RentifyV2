package com.rentify.controller;

import com.lowagie.text.DocumentException;
import com.rentify.dao.ArrendamientoDAO;
import com.rentify.dao.ContratoDAO;
import com.rentify.model.ArrendamientoContratoData;
import com.rentify.util.Navegacion;
import com.rentify.util.PdfContratoUtil;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ContratoFormController {

    @FXML
    private TextField txtFolioContrato;

    @FXML
    private Label lblMensaje;

    private final ContratoDAO contratoDAO;
    private final ArrendamientoDAO arrendamientoDAO;

    private int idArrendamiento;

    public ContratoFormController() {
        this.contratoDAO = new ContratoDAO();
        this.arrendamientoDAO = new ArrendamientoDAO();
    }

    @FXML
    public void initialize() {
        String folio = "CTR-" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss"));
        txtFolioContrato.setText(folio);
    }

    public void setIdArrendamiento(int idArrendamiento) {
        this.idArrendamiento = idArrendamiento;
    }

    @FXML
    private void guardarContrato() {
        String folio = txtFolioContrato.getText().trim();

        if (folio.isEmpty()) {
            lblMensaje.setText("El folio es obligatorio.");
            return;
        }

        if (idArrendamiento <= 0) {
            lblMensaje.setText("No se recibió un arrendamiento válido.");
            return;
        }

        if (contratoDAO.existeContratoParaArrendamiento(idArrendamiento)) {
            lblMensaje.setText("Ese arrendamiento ya tiene contrato.");
            return;
        }

        ArrendamientoContratoData data = arrendamientoDAO.obtenerDatosContrato(idArrendamiento);
        if (data == null) {
            lblMensaje.setText("No se pudieron obtener los datos del arrendamiento.");
            return;
        }

        try {
            String rutaPdf = PdfContratoUtil.generarContratoPdf(
                    folio,
                    data.getNombreArrendador(),
                    data.getNombreArrendatario(),
                    data.getTituloInmueble(),
                    data.getDireccionInmueble(),
                    data.getFechaInicio(),
                    data.getFechaFin(),
                    data.getMontoMensual(),
                    data.getDepositoGarantia(),
                    data.getDiaPago(),
                    data.getObservaciones()
            );

            boolean guardado = contratoDAO.insertarContrato(folio, rutaPdf, idArrendamiento);

            if (!guardado) {
                lblMensaje.setText("No se pudo guardar el contrato.");
                return;
            }

            volverAContratos();

        } catch (IOException | DocumentException e) {
            lblMensaje.setText("No se pudo generar el PDF del contrato.");
            e.printStackTrace();
        }
    }

    @FXML
    private void cancelar() {
        volverAContratos();
    }

    private void volverAContratos() {
        try {
            FXMLLoader loader = Navegacion.cargarVista("/fxml/contratos.fxml");
            Stage stage = (Stage) txtFolioContrato.getScene().getWindow();
            Navegacion.cambiarEscena(stage, loader, "Rentify - Contratos");
        } catch (IOException e) {
            lblMensaje.setText("No se pudo volver a contratos.");
            e.printStackTrace();
        }
    }
}