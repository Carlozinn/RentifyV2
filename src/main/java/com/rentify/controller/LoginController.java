package com.rentify.controller;

import com.rentify.dao.UsuarioDAO;
import com.rentify.model.Usuario;
import com.rentify.util.Navegacion;
import com.rentify.util.Sesion;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;

public class LoginController {

    @FXML
    private TextField txtUsername;

    @FXML
    private PasswordField txtPassword;

    @FXML
    private TextField txtPasswordVisible;

    @FXML
    private CheckBox chkMostrarPassword;

    @FXML
    private Label lblMensaje;

    private final UsuarioDAO usuarioDAO;

    public LoginController() {
        this.usuarioDAO = new UsuarioDAO();
    }

    @FXML
    public void initialize() {
        txtPasswordVisible.textProperty().bindBidirectional(txtPassword.textProperty());
        txtPasswordVisible.setVisible(false);
        txtPasswordVisible.setManaged(false);
    }

    @FXML
    private void toggleMostrarPassword() {
        boolean mostrar = chkMostrarPassword.isSelected();
        txtPasswordVisible.setVisible(mostrar);
        txtPasswordVisible.setManaged(mostrar);
        txtPassword.setVisible(!mostrar);
        txtPassword.setManaged(!mostrar);
    }

    @FXML
    private void iniciarSesion() {
        limpiarMensaje();

        String username = txtUsername.getText().trim();
        String password = obtenerPasswordActual().trim();

        if (username.isEmpty() || password.isEmpty()) {
            lblMensaje.setText("Completa todos los campos.");
            return;
        }

        Usuario usuario = usuarioDAO.autenticarUsuario(username, password);

        if (usuario == null) {
            lblMensaje.setText("Username o contraseña incorrectos.");
            return;
        }

        if (usuario.getIdEstadoUsuario() != 1) {
            lblMensaje.setText("El usuario no está activo.");
            return;
        }

        Sesion.setUsuarioActual(usuario);
        redirigirSegunRol(usuario);
    }

    @FXML
    private void abrirRegistro() {
        try {
            FXMLLoader loader = Navegacion.cargarVista("/fxml/registro.fxml");
            Stage stage = (Stage) txtUsername.getScene().getWindow();
            Navegacion.cambiarEscena(stage, loader, "Rentify - Registro");
        } catch (IOException e) {
            lblMensaje.setText("No se pudo abrir el registro.");
            e.printStackTrace();
        }
    }

    private String obtenerPasswordActual() {
        if (chkMostrarPassword.isSelected()) {
            return txtPasswordVisible.getText();
        }
        return txtPassword.getText();
    }

    private void redirigirSegunRol(Usuario usuario) {
        try {
            FXMLLoader loader;
            String titulo;

            switch (usuario.getIdRol()) {
                case 1 -> {
                    loader = Navegacion.cargarVista("/fxml/admin.fxml");
                    titulo = "Rentify - Administrador";
                    AdminController controller = loader.getController();
                    controller.setNombreUsuario(usuario.getNombre());
                }
                case 2 -> {
                    loader = Navegacion.cargarVista("/fxml/arrendador.fxml");
                    titulo = "Rentify - Arrendador";
                    ArrendadorController controller = loader.getController();
                    controller.setNombreUsuario(usuario.getNombre());
                }
                case 3 -> {
                    loader = Navegacion.cargarVista("/fxml/arrendatario.fxml");
                    titulo = "Rentify - Arrendatario";
                    ArrendatarioController controller = loader.getController();
                    controller.setNombreUsuario(usuario.getNombre());
                }
                default -> {
                    lblMensaje.setText("Rol no reconocido.");
                    return;
                }
            }

            Stage stage = (Stage) txtUsername.getScene().getWindow();
            Navegacion.cambiarEscena(stage, loader, titulo);

        } catch (IOException e) {
            lblMensaje.setText("Error al abrir la siguiente ventana.");
            e.printStackTrace();
        }
    }

    private void limpiarMensaje() {
        lblMensaje.setText("");
    }
}