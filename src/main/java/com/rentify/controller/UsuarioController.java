package com.rentify.controller;

import com.rentify.dao.UsuarioDAO;
import com.rentify.model.Usuario;
import com.rentify.model.UsuarioTabla;
import com.rentify.util.Navegacion;
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

public class UsuarioController {

    @FXML
    private TableView<UsuarioTabla> tablaUsuarios;

    @FXML
    private TableColumn<UsuarioTabla, Integer> colId;

    @FXML
    private TableColumn<UsuarioTabla, String> colNombre;

    @FXML
    private TableColumn<UsuarioTabla, String> colApellidoPaterno;

    @FXML
    private TableColumn<UsuarioTabla, String> colApellidoMaterno;

    @FXML
    private TableColumn<UsuarioTabla, String> colUsername;

    @FXML
    private TableColumn<UsuarioTabla, String> colRol;

    @FXML
    private TableColumn<UsuarioTabla, String> colEstado;

    private final UsuarioDAO usuarioDAO;

    public UsuarioController() {
        this.usuarioDAO = new UsuarioDAO();
    }

    @FXML
    public void initialize() {
        colId.setCellValueFactory(new PropertyValueFactory<>("idUsuario"));
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colApellidoPaterno.setCellValueFactory(new PropertyValueFactory<>("apellidoPaterno"));
        colApellidoMaterno.setCellValueFactory(new PropertyValueFactory<>("apellidoMaterno"));
        colUsername.setCellValueFactory(new PropertyValueFactory<>("username"));
        colRol.setCellValueFactory(new PropertyValueFactory<>("rol"));
        colEstado.setCellValueFactory(new PropertyValueFactory<>("estado"));

        cargarUsuarios();
    }

    @FXML
    private void cargarUsuarios() {
        List<UsuarioTabla> lista = usuarioDAO.listarUsuariosTabla();
        ObservableList<UsuarioTabla> datos = FXCollections.observableArrayList(lista);
        tablaUsuarios.setItems(datos);
    }

    @FXML
    private void abrirFormularioNuevoUsuario() {
        try {
            FXMLLoader loader = Navegacion.cargarVista("/fxml/usuario_form.fxml");
            UsuarioFormController controller = loader.getController();
            controller.setModoNuevo();

            Stage stage = (Stage) tablaUsuarios.getScene().getWindow();
            Navegacion.cambiarEscena(stage, loader, "Rentify - Nuevo Usuario");

        } catch (IOException e) {
            mostrarError("No se pudo abrir el formulario de usuario.\n" + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void abrirFormularioEditarUsuario() {
        UsuarioTabla seleccionado = tablaUsuarios.getSelectionModel().getSelectedItem();

        if (seleccionado == null) {
            mostrarError("Selecciona un usuario para editar.");
            return;
        }

        try {
            Usuario usuario = usuarioDAO.buscarPorId(seleccionado.getIdUsuario());

            if (usuario == null) {
                mostrarError("No se encontró el usuario seleccionado.");
                return;
            }

            FXMLLoader loader = Navegacion.cargarVista("/fxml/usuario_form.fxml");
            UsuarioFormController controller = loader.getController();
            controller.setModoEdicion(usuario);

            Stage stage = (Stage) tablaUsuarios.getScene().getWindow();
            Navegacion.cambiarEscena(stage, loader, "Rentify - Editar Usuario");

        } catch (IOException e) {
            mostrarError("No se pudo abrir el formulario de edición.\n" + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void cambiarEstadoUsuario() {
        UsuarioTabla seleccionado = tablaUsuarios.getSelectionModel().getSelectedItem();

        if (seleccionado == null) {
            mostrarError("Selecciona un usuario para cambiar su estado.");
            return;
        }

        ChoiceDialog<String> dialog = new ChoiceDialog<>("Activo", "Activo", "Inactivo", "Suspendido");
        dialog.setTitle("Cambiar estado");
        dialog.setHeaderText("Cambiar estado del usuario");
        dialog.setContentText("Nuevo estado:");

        Optional<String> resultado = dialog.showAndWait();

        if (resultado.isPresent()) {
            String nuevoEstado = resultado.get();
            int idEstado = convertirEstadoAId(nuevoEstado);

            boolean actualizado = usuarioDAO.actualizarEstadoUsuario(seleccionado.getIdUsuario(), idEstado);

            if (actualizado) {
                cargarUsuarios();
                mostrarInformacion("Estado actualizado correctamente.");
            } else {
                mostrarError("No se pudo actualizar el estado del usuario.");
            }
        }
    }

    @FXML
    private void volverAlPanel() {
        try {
            FXMLLoader loader = Navegacion.cargarVista("/fxml/admin.fxml");
            Stage stage = (Stage) tablaUsuarios.getScene().getWindow();
            Navegacion.cambiarEscena(stage, loader, "Rentify - Administrador");
        } catch (IOException e) {
            mostrarError("No se pudo volver al panel.\n" + e.getMessage());
            e.printStackTrace();
        }
    }

    private int convertirEstadoAId(String estado) {
        return switch (estado) {
            case "Activo" -> 1;
            case "Inactivo" -> 2;
            case "Suspendido" -> 3;
            default -> 0;
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