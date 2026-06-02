package com.rentify.controller;

import com.rentify.util.Navegacion;
import com.rentify.util.Sesion;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.io.IOException;

public class AdminController {

    @FXML
    private Label lblBienvenida;

    public void setNombreUsuario(String nombre) {
        lblBienvenida.setText("Bienvenido, " + nombre + ".");
    }

    @FXML
    private void abrirGestionUsuarios() {
        try {
            FXMLLoader loader = Navegacion.cargarVista("/fxml/usuarios.fxml");
            Stage stage = (Stage) lblBienvenida.getScene().getWindow();
            Navegacion.cambiarEscena(stage, loader, "Rentify - Gestión de Usuarios");
        } catch (IOException e) {
            System.out.println("Error al abrir gestión de usuarios: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void abrirReportes() {
        try {
            FXMLLoader loader = Navegacion.cargarVista("/fxml/reportes.fxml");
            Stage stage = (Stage) lblBienvenida.getScene().getWindow();
            Navegacion.cambiarEscena(stage, loader, "Rentify - Reportes");
        } catch (IOException e) {
            System.out.println("Error al abrir reportes: " + e.getMessage());
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
}