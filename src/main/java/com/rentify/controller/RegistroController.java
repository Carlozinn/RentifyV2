package com.rentify.controller;

import com.rentify.dao.UsuarioDAO;
import com.rentify.model.Usuario;
import com.rentify.util.Navegacion;
import com.rentify.util.Sesion;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.time.LocalDate;

public class RegistroController {

    @FXML
    private TextField txtNombre;

    @FXML
    private TextField txtApellidoPaterno;

    @FXML
    private TextField txtApellidoMaterno;

    @FXML
    private TextField txtUsername;

    @FXML
    private PasswordField txtPassword;

    @FXML
    private TextField txtPasswordVisible;

    @FXML
    private PasswordField txtConfirmarPassword;

    @FXML
    private TextField txtConfirmarPasswordVisible;

    @FXML
    private CheckBox chkMostrarPassword;

    @FXML
    private DatePicker dpFechaNacimiento;

    @FXML
    private ComboBox<String> cbRol;

    @FXML
    private Label lblMensaje;

    private final UsuarioDAO usuarioDAO;

    public RegistroController() {
        this.usuarioDAO = new UsuarioDAO();
    }

    @FXML
    public void initialize() {
        cbRol.setItems(FXCollections.observableArrayList("Arrendador", "Arrendatario"));
        cbRol.setValue("Arrendatario");

        if (txtPasswordVisible != null) {
            txtPasswordVisible.setManaged(false);
            txtPasswordVisible.setVisible(false);
        }

        if (txtConfirmarPasswordVisible != null) {
            txtConfirmarPasswordVisible.setManaged(false);
            txtConfirmarPasswordVisible.setVisible(false);
        }

        if (dpFechaNacimiento != null) {
            dpFechaNacimiento.setEditable(false);
        }
    }

    @FXML
    private void registrarUsuario() {
        limpiarMensaje();

        String nombre = txtNombre.getText().trim();
        String apellidoPaterno = txtApellidoPaterno.getText().trim();
        String apellidoMaterno = txtApellidoMaterno.getText().trim();
        String username = txtUsername.getText().trim();
        String password = obtenerPasswordActual().trim();
        String confirmarPassword = obtenerConfirmacionActual().trim();
        String rolSeleccionado = cbRol.getValue();
        LocalDate fechaNacimiento = dpFechaNacimiento.getValue();

        if (nombre.isEmpty() || apellidoPaterno.isEmpty() || username.isEmpty()
                || password.isEmpty() || confirmarPassword.isEmpty() || rolSeleccionado == null) {
            lblMensaje.setText("Completa todos los campos obligatorios.");
            return;
        }

        if (!password.equals(confirmarPassword)) {
            lblMensaje.setText("Las contraseñas no coinciden.");
            return;
        }

        if (!passwordValida(password)) {
            lblMensaje.setText("La contraseña debe tener al menos 8 caracteres, una mayúscula, una minúscula, un número y un carácter especial.");
            return;
        }

        if (username.contains(" ")) {
            lblMensaje.setText("El nombre de usuario no debe contener espacios.");
            return;
        }

        if (fechaNacimiento != null && fechaNacimiento.isAfter(LocalDate.now())) {
            lblMensaje.setText("La fecha de nacimiento no puede ser futura.");
            return;
        }

        if (usuarioDAO.buscarPorUsername(username) != null) {
            lblMensaje.setText("Ese nombre de usuario ya existe.");
            return;
        }

        Usuario usuario = new Usuario();
        usuario.setNombre(nombre);
        usuario.setApellidoPaterno(apellidoPaterno);
        usuario.setApellidoMaterno(apellidoMaterno.isEmpty() ? null : apellidoMaterno);
        usuario.setUsername(username);
        usuario.setPasswordHash(password); // el DAO lo hashea
        usuario.setFechaNacimiento(fechaNacimiento);
        usuario.setIdRol(convertirRolAId(rolSeleccionado));
        usuario.setIdEstadoUsuario(1); // Activo

        boolean registrado = usuarioDAO.insertarUsuario(usuario);

        if (!registrado) {
            lblMensaje.setText("No se pudo registrar el usuario.");
            return;
        }

        Usuario usuarioRegistrado = usuarioDAO.autenticarUsuario(username, password);

        if (usuarioRegistrado == null) {
            lblMensaje.setText("El usuario se registró, pero no se pudo iniciar sesión.");
            return;
        }

        Sesion.setUsuarioActual(usuarioRegistrado);
        redirigirSegunRol(usuarioRegistrado);
    }

    @FXML
    private void volverLogin() {
        try {
            FXMLLoader loader = Navegacion.cargarVista("/fxml/login.fxml");
            Stage stage = (Stage) txtNombre.getScene().getWindow();
            Navegacion.cambiarEscena(stage, loader, "Rentify - Login");
        } catch (IOException e) {
            lblMensaje.setText("No se pudo volver al login.");
            e.printStackTrace();
        }
    }

    @FXML
    private void toggleMostrarPassword() {
        if (chkMostrarPassword == null) {
            return;
        }

        if (chkMostrarPassword.isSelected()) {
            txtPasswordVisible.setText(txtPassword.getText());
            txtConfirmarPasswordVisible.setText(txtConfirmarPassword.getText());

            txtPasswordVisible.setVisible(true);
            txtPasswordVisible.setManaged(true);
            txtConfirmarPasswordVisible.setVisible(true);
            txtConfirmarPasswordVisible.setManaged(true);

            txtPassword.setVisible(false);
            txtPassword.setManaged(false);
            txtConfirmarPassword.setVisible(false);
            txtConfirmarPassword.setManaged(false);
        } else {
            txtPassword.setText(txtPasswordVisible.getText());
            txtConfirmarPassword.setText(txtConfirmarPasswordVisible.getText());

            txtPassword.setVisible(true);
            txtPassword.setManaged(true);
            txtConfirmarPassword.setVisible(true);
            txtConfirmarPassword.setManaged(true);

            txtPasswordVisible.setVisible(false);
            txtPasswordVisible.setManaged(false);
            txtConfirmarPasswordVisible.setVisible(false);
            txtConfirmarPasswordVisible.setManaged(false);
        }
    }

    private String obtenerPasswordActual() {
        if (chkMostrarPassword != null && chkMostrarPassword.isSelected()) {
            return txtPasswordVisible.getText();
        }
        return txtPassword.getText();
    }

    private String obtenerConfirmacionActual() {
        if (chkMostrarPassword != null && chkMostrarPassword.isSelected()) {
            return txtConfirmarPasswordVisible.getText();
        }
        return txtConfirmarPassword.getText();
    }

    private int convertirRolAId(String rol) {
        return switch (rol) {
            case "Arrendador" -> 2;
            case "Arrendatario" -> 3;
            default -> 3;
        };
    }

    private void redirigirSegunRol(Usuario usuario) {
        try {
            FXMLLoader loader;
            String titulo;

            switch (usuario.getIdRol()) {
                case 2 -> {
                    loader = Navegacion.cargarVista("/fxml/arrendador.fxml");
                    ArrendadorController controller = loader.getController();
                    controller.setNombreUsuario(usuario.getNombre());
                    titulo = "Rentify - Arrendador";
                }
                case 3 -> {
                    loader = Navegacion.cargarVista("/fxml/arrendatario.fxml");
                    ArrendatarioController controller = loader.getController();
                    controller.setNombreUsuario(usuario.getNombre());
                    titulo = "Rentify - Arrendatario";
                }
                default -> {
                    lblMensaje.setText("Rol inválido para registro público.");
                    return;
                }
            }

            Stage stage = (Stage) txtNombre.getScene().getWindow();
            Navegacion.cambiarEscena(stage, loader, titulo);

        } catch (IOException e) {
            lblMensaje.setText("No se pudo abrir el panel del usuario.");
            e.printStackTrace();
        }
    }

    private void limpiarMensaje() {
        lblMensaje.setText("");
    }

    private boolean passwordValida(String password) {
        if (password == null || password.length() < 8) {
            return false;
        }

        boolean tieneMayuscula = false;
        boolean tieneMinuscula = false;
        boolean tieneNumero = false;
        boolean tieneEspecial = false;

        for (char c : password.toCharArray()) {
            if (Character.isUpperCase(c)) {
                tieneMayuscula = true;
            } else if (Character.isLowerCase(c)) {
                tieneMinuscula = true;
            } else if (Character.isDigit(c)) {
                tieneNumero = true;
            } else {
                tieneEspecial = true;
            }
        }

        return tieneMayuscula && tieneMinuscula && tieneNumero && tieneEspecial;
    }
}