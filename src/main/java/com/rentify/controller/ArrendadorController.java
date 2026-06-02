package com.rentify.controller;

import com.rentify.util.Navegacion;
import com.rentify.util.Sesion;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import com.rentify.dao.DashboardDAO;
import com.rentify.model.DashboardResumen;
import com.rentify.util.MonedaUtil;


import java.io.IOException;

public class ArrendadorController {

    @FXML
    private Label lblBienvenida;

    @FXML
    private Label lblInmueblesRegistrados;

    @FXML
    private Label lblInmueblesDisponibles;

    @FXML
    private Label lblInmueblesOcupados;

    @FXML
    private Label lblSolicitudesPendientes;

    @FXML
    private Label lblArrendamientosActivos;

    @FXML
    private Label lblContratosFirmados;

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
    private void abrirGestionInmuebles() {
        try {
            FXMLLoader loader = Navegacion.cargarVista("/fxml/inmuebles.fxml");
            Stage stage = (Stage) lblBienvenida.getScene().getWindow();
            Navegacion.cambiarEscena(stage, loader, "Rentify - Gestión de Inmuebles");
        } catch (IOException e) {
            System.out.println("Error al abrir gestión de inmuebles: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void abrirSolicitudes() {
        try {
            FXMLLoader loader = Navegacion.cargarVista("/fxml/solicitudes_arrendador.fxml");
            Stage stage = (Stage) lblBienvenida.getScene().getWindow();
            Navegacion.cambiarEscena(stage, loader, "Rentify - Solicitudes Recibidas");
        } catch (IOException e) {
            System.out.println("Error al abrir solicitudes: " + e.getMessage());
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
            System.out.println("Error al abrir arrendamientos: " + e.getMessage());
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
            System.out.println("Error al abrir contratos: " + e.getMessage());
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
            System.out.println("Error al abrir pagos: " + e.getMessage());
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
            System.out.println("Error al abrir incidencias: " + e.getMessage());
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
            System.out.println("Error al cerrar sesión: " + e.getMessage());
            e.printStackTrace();
        }
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

        DashboardResumen resumen = dashboardDAO.obtenerResumenArrendador(
                Sesion.getUsuarioActual().getIdUsuario()
        );

        lblInmueblesRegistrados.setText(String.valueOf(resumen.getInmueblesRegistrados()));
        lblInmueblesDisponibles.setText(String.valueOf(resumen.getInmueblesDisponibles()));
        lblInmueblesOcupados.setText(String.valueOf(resumen.getInmueblesOcupados()));

        lblSolicitudesPendientes.setText(String.valueOf(resumen.getSolicitudesPendientes()));
        lblArrendamientosActivos.setText(String.valueOf(resumen.getArrendamientosActivos()));
        lblContratosFirmados.setText(String.valueOf(resumen.getContratosFirmados()));

        lblPagosPendientes.setText(String.valueOf(resumen.getPagosPendientes()));
        lblMontoPagosPendientes.setText(MonedaUtil.formatear(resumen.getMontoPagosPendientes()));

        lblPagosVencidos.setText(String.valueOf(resumen.getPagosVencidos()));
        lblMontoPagosVencidos.setText(MonedaUtil.formatear(resumen.getMontoPagosVencidos()));

        lblIncidenciasAbiertas.setText(String.valueOf(resumen.getIncidenciasAbiertas()));
    }

}