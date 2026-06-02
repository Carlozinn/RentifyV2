package com.rentify.controller;

import com.rentify.util.Navegacion;
import com.rentify.util.Sesion;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import com.rentify.dao.DashboardDAO;
import com.rentify.model.DashboardResumen;
import com.rentify.util.MonedaUtil;


import java.io.IOException;

public class ArrendatarioController {

    @FXML
    private Label lblBienvenida;

    @FXML
    private Label lblSolicitudesEnviadas;

    @FXML
    private Label lblSolicitudesPendientes;

    @FXML
    private Label lblArrendamientosActivos;

    @FXML
    private Label lblContratosPorFirmar;

    @FXML
    private Label lblPagosPendientes;

    @FXML
    private Label lblMontoPagosPendientes;

    @FXML
    private Label lblPagosVencidos;

    @FXML
    private Label lblMontoPagosVencidos;

    @FXML
    private Label lblIncidenciasAbiertas;

    private final DashboardDAO dashboardDAO = new DashboardDAO();
    public void setNombreUsuario(String nombre) {
        lblBienvenida.setText("Bienvenido, " + nombre + ".");
        cargarDashboard();
    }

    @FXML
    private void abrirExplorarInmuebles() {
        try {
            FXMLLoader loader = Navegacion.cargarVista("/fxml/explorar_inmuebles.fxml");
            Stage stage = (Stage) lblBienvenida.getScene().getWindow();
            Navegacion.cambiarEscena(stage, loader, "Rentify - Explorar Inmuebles");
        } catch (IOException e) {
            mostrarError("No se pudo abrir explorar inmuebles.\n" + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            mostrarError("Ocurrió un error inesperado.\n" + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void abrirMisSolicitudes() {
        try {
            FXMLLoader loader = Navegacion.cargarVista("/fxml/mis_solicitudes.fxml");
            Stage stage = (Stage) lblBienvenida.getScene().getWindow();
            Navegacion.cambiarEscena(stage, loader, "Rentify - Mis Solicitudes");
        } catch (IOException e) {
            mostrarError("No se pudo abrir mis solicitudes.\n" + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void abrirArrendamientos() {
        try {
            FXMLLoader loader = Navegacion.cargarVista("/fxml/arrendamientos.fxml");
            Stage stage = (Stage) lblBienvenida.getScene().getWindow();
            Navegacion.cambiarEscena(stage, loader, "Rentify - Arrendamientos");
        } catch (IOException e) {
            mostrarError("No se pudo abrir arrendamientos.\n" + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void abrirContratos() {
        try {
            FXMLLoader loader = Navegacion.cargarVista("/fxml/contratos.fxml");
            Stage stage = (Stage) lblBienvenida.getScene().getWindow();
            Navegacion.cambiarEscena(stage, loader, "Rentify - Contratos");
        } catch (IOException e) {
            mostrarError("No se pudo abrir contratos.\n" + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void abrirPagos() {
        try {
            FXMLLoader loader = Navegacion.cargarVista("/fxml/pagos.fxml");
            Stage stage = (Stage) lblBienvenida.getScene().getWindow();
            Navegacion.cambiarEscena(stage, loader, "Rentify - Pagos");
        } catch (IOException e) {
            mostrarError("No se pudo abrir pagos.\n" + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void abrirIncidencias() {
        try {
            FXMLLoader loader = Navegacion.cargarVista("/fxml/incidencias.fxml");
            Stage stage = (Stage) lblBienvenida.getScene().getWindow();
            Navegacion.cambiarEscena(stage, loader, "Rentify - Incidencias");
        } catch (IOException e) {
            mostrarError("No se pudo abrir incidencias.\n" + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void cerrarSesion() {
        try {
            Sesion.cerrarSesion();
            FXMLLoader loader = Navegacion.cargarVista("/fxml/login.fxml");
            Stage stage = (Stage) lblBienvenida.getScene().getWindow();
            Navegacion.cambiarEscena(stage, loader, "Rentify - Login");
        } catch (IOException e) {
            mostrarError("Error al cerrar sesión.\n" + e.getMessage());
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

    @FXML
    private void abrirContactos() {
        try {
            FXMLLoader loader = Navegacion.cargarVista("/fxml/contactos.fxml");

            Stage stage = (Stage) lblBienvenida
                    .getScene()
                    .getWindow();

            Navegacion.cambiarEscena(
                    stage,
                    loader,
                    "Rentify - Mis Contactos"
            );

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void cargarDashboard() {
        if (Sesion.getUsuarioActual() == null) {
            return;
        }

        DashboardResumen resumen = dashboardDAO.obtenerResumenArrendatario(
                Sesion.getUsuarioActual().getIdUsuario()
        );

        lblSolicitudesEnviadas.setText(String.valueOf(resumen.getSolicitudesEnviadas()));
        lblSolicitudesPendientes.setText(String.valueOf(resumen.getSolicitudesPendientes()));
        lblArrendamientosActivos.setText(String.valueOf(resumen.getArrendamientosActivos()));

        lblContratosPorFirmar.setText(String.valueOf(resumen.getContratosPorFirmar()));

        lblPagosPendientes.setText(String.valueOf(resumen.getPagosPendientes()));
        lblMontoPagosPendientes.setText(MonedaUtil.formatear(resumen.getMontoPagosPendientes()));


        lblPagosVencidos.setText(String.valueOf(resumen.getPagosVencidos()));
        lblMontoPagosVencidos.setText(MonedaUtil.formatear(resumen.getMontoPagosVencidos()));

        lblIncidenciasAbiertas.setText(String.valueOf(resumen.getIncidenciasAbiertas()));
    }

}