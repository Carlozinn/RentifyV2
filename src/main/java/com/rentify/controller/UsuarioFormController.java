package com.rentify.controller;

import com.rentify.dao.UsuarioDAO;
import com.rentify.model.Usuario;
import com.rentify.util.Navegacion;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;

public class UsuarioFormController {

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
    private DatePicker dpFechaNacimiento;

    @FXML
    private ComboBox<String> cbRol;

    @FXML
    private ComboBox<String> cbEstado;

    @FXML
    private Label lblMensaje;

    private final UsuarioDAO usuarioDAO;

    private Usuario usuarioEditar;
    private boolean modoEdicion;

    public UsuarioFormController() {
        this.usuarioDAO = new UsuarioDAO();
        this.modoEdicion = false;
    }

    @FXML
    public void initialize() {
        cbRol.getItems().addAll("Administrador", "Arrendador", "Arrendatario");
        cbEstado.getItems().addAll("Activo", "Inactivo", "Suspendido");
        dpFechaNacimiento.setEditable(false);
    }

    public void setModoNuevo() {
        this.modoEdicion = false;
        this.usuarioEditar = null;
    }

    public void setModoEdicion(Usuario usuario) {
        this.modoEdicion = true;
        this.usuarioEditar = usuario;

        txtNombre.setText(usuario.getNombre());
        txtApellidoPaterno.setText(usuario.getApellidoPaterno());
        txtApellidoMaterno.setText(usuario.getApellidoMaterno() != null ? usuario.getApellidoMaterno() : "");
        txtUsername.setText(usuario.getUsername());
        txtPassword.setText(usuario.getPasswordHash());
        dpFechaNacimiento.setValue(usuario.getFechaNacimiento());

        cbRol.setValue(convertirIdARol(usuario.getIdRol()));
        cbEstado.setValue(convertirIdAEstado(usuario.getIdEstadoUsuario()));
    }

    @FXML
    private void guardarUsuario() {
        String nombre = txtNombre.getText().trim();
        String apellidoPaterno = txtApellidoPaterno.getText().trim();
        String apellidoMaterno = txtApellidoMaterno.getText().trim();
        String username = txtUsername.getText().trim();
        String password = txtPassword.getText().trim();
        String rolSeleccionado = cbRol.getValue();
        String estadoSeleccionado = cbEstado.getValue();

        if (nombre.isEmpty() || apellidoPaterno.isEmpty() || username.isEmpty()
                || password.isEmpty() || rolSeleccionado == null || estadoSeleccionado == null) {
            lblMensaje.setText("Completa los campos obligatorios.");
            return;
        }

        Usuario usuarioExistente = usuarioDAO.buscarPorUsername(username);

        if (!modoEdicion) {
            if (usuarioExistente != null) {
                lblMensaje.setText("El username ya existe.");
                return;
            }
        } else {
            if (usuarioExistente != null && usuarioExistente.getIdUsuario() != usuarioEditar.getIdUsuario()) {
                lblMensaje.setText("El username ya existe.");
                return;
            }
        }

        Usuario usuario = new Usuario();

        if (modoEdicion) {
            usuario.setIdUsuario(usuarioEditar.getIdUsuario());
        }

        usuario.setNombre(nombre);
        usuario.setApellidoPaterno(apellidoPaterno);
        usuario.setApellidoMaterno(apellidoMaterno.isEmpty() ? null : apellidoMaterno);
        usuario.setUsername(username);
        usuario.setPasswordHash(password);
        usuario.setFechaNacimiento(dpFechaNacimiento.getValue());
        usuario.setIdRol(convertirRolAId(rolSeleccionado));
        usuario.setIdEstadoUsuario(convertirEstadoAId(estadoSeleccionado));

        boolean exito;

        if (modoEdicion) {
            exito = usuarioDAO.actualizarUsuario(usuario);
        } else {
            exito = usuarioDAO.insertarUsuario(usuario);
        }

        if (exito) {
            volverAGestionUsuarios();
        } else {
            lblMensaje.setText("No se pudo guardar el usuario.");
        }
    }

    @FXML
    private void cancelar() {
        volverAGestionUsuarios();
    }

    private int convertirRolAId(String rol) {
        return switch (rol) {
            case "Administrador" -> 1;
            case "Arrendador" -> 2;
            case "Arrendatario" -> 3;
            default -> 0;
        };
    }

    private int convertirEstadoAId(String estado) {
        return switch (estado) {
            case "Activo" -> 1;
            case "Inactivo" -> 2;
            case "Suspendido" -> 3;
            default -> 0;
        };
    }

    private String convertirIdARol(int idRol) {
        return switch (idRol) {
            case 1 -> "Administrador";
            case 2 -> "Arrendador";
            case 3 -> "Arrendatario";
            default -> "";
        };
    }

    private String convertirIdAEstado(int idEstado) {
        return switch (idEstado) {
            case 1 -> "Activo";
            case 2 -> "Inactivo";
            case 3 -> "Suspendido";
            default -> "";
        };
    }

    private void volverAGestionUsuarios() {
        try {
            FXMLLoader loader = Navegacion.cargarVista("/fxml/usuarios.fxml");
            Stage stage = (Stage) txtNombre.getScene().getWindow();
            Navegacion.cambiarEscena(stage, loader, "Rentify - Gestión de Usuarios");
        } catch (IOException e) {
            lblMensaje.setText("Error al volver a usuarios.");
            e.printStackTrace();
        }
    }
}