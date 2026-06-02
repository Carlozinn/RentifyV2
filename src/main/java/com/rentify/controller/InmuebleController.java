package com.rentify.controller;

import com.rentify.dao.ArrendamientoDAO;
import com.rentify.dao.InmuebleDAO;
import com.rentify.model.Inmueble;
import com.rentify.model.InmuebleTabla;
import com.rentify.util.Navegacion;
import com.rentify.util.Sesion;
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

public class InmuebleController {

    @FXML
    private TableView<InmuebleTabla> tablaInmuebles;

    @FXML
    private TableColumn<InmuebleTabla, Integer> colId;

    @FXML
    private TableColumn<InmuebleTabla, String> colTitulo;

    @FXML
    private TableColumn<InmuebleTabla, String> colCiudad;

    @FXML
    private TableColumn<InmuebleTabla, String> colTipo;

    @FXML
    private TableColumn<InmuebleTabla, String> colEstado;

    @FXML
    private TableColumn<InmuebleTabla, String> colPrecio;

    private final InmuebleDAO inmuebleDAO;
    private final ArrendamientoDAO arrendamientoDAO;

    public InmuebleController() {
        this.inmuebleDAO = new InmuebleDAO();
        this.arrendamientoDAO = new ArrendamientoDAO();
    }

    @FXML
    public void initialize() {
        colId.setCellValueFactory(new PropertyValueFactory<>("idInmueble"));
        colTitulo.setCellValueFactory(new PropertyValueFactory<>("titulo"));
        colCiudad.setCellValueFactory(new PropertyValueFactory<>("ciudad"));
        colTipo.setCellValueFactory(new PropertyValueFactory<>("tipo"));
        colEstado.setCellValueFactory(new PropertyValueFactory<>("estado"));
        colPrecio.setCellValueFactory(new PropertyValueFactory<>("precioRenta"));

        cargarInmuebles();
    }

    @FXML
    private void cargarInmuebles() {
        if (Sesion.getUsuarioActual() == null) {
            mostrarError("No hay sesión activa.");
            return;
        }

        List<InmuebleTabla> lista = inmuebleDAO.listarInmueblesPorArrendador(
                Sesion.getUsuarioActual().getIdUsuario()
        );
        ObservableList<InmuebleTabla> datos = FXCollections.observableArrayList(lista);
        tablaInmuebles.setItems(datos);
    }

    @FXML
    private void abrirFormularioNuevoInmueble() {
        try {
            FXMLLoader loader = Navegacion.cargarVista("/fxml/inmueble_form.fxml");
            InmuebleFormController controller = loader.getController();
            controller.setModoNuevo();

            Stage stage = (Stage) tablaInmuebles.getScene().getWindow();
            Navegacion.cambiarEscena(stage, loader, "Rentify - Nuevo Inmueble");
        } catch (IOException e) {
            mostrarError("No se pudo abrir el formulario de inmueble.");
            e.printStackTrace();
        }
    }

    @FXML
    private void abrirFormularioEditarInmueble() {
        InmuebleTabla seleccionado = tablaInmuebles.getSelectionModel().getSelectedItem();

        if (seleccionado == null) {
            mostrarError("Selecciona un inmueble para editar.");
            return;
        }

        try {
            Inmueble inmueble = inmuebleDAO.buscarPorId(seleccionado.getIdInmueble());

            if (inmueble == null) {
                mostrarError("No se encontró el inmueble seleccionado.");
                return;
            }

            FXMLLoader loader = Navegacion.cargarVista("/fxml/inmueble_form.fxml");
            InmuebleFormController controller = loader.getController();
            controller.setModoEdicion(inmueble);

            Stage stage = (Stage) tablaInmuebles.getScene().getWindow();
            Navegacion.cambiarEscena(stage, loader, "Rentify - Editar Inmueble");

        } catch (IOException e) {
            mostrarError("No se pudo abrir el formulario de edición.");
            e.printStackTrace();
        }
    }

    @FXML
    private void verDetalleInmueble() {
        InmuebleTabla seleccionado = tablaInmuebles.getSelectionModel().getSelectedItem();

        if (seleccionado == null) {
            mostrarError("Selecciona un inmueble para ver el detalle.");
            return;
        }

        try {
            Inmueble inmueble = inmuebleDAO.buscarDetallePorId(seleccionado.getIdInmueble());

            if (inmueble == null) {
                mostrarError("No se encontró el inmueble seleccionado.");
                return;
            }

            FXMLLoader loader = Navegacion.cargarVista("/fxml/inmueble_detalle.fxml");
            InmuebleDetalleController controller = loader.getController();
            controller.setVistaOrigen("inmuebles");
            controller.cargarDatos(inmueble);

            Stage stage = (Stage) tablaInmuebles.getScene().getWindow();
            Navegacion.cambiarEscena(stage, loader, "Rentify - Detalle de Inmueble");
        } catch (IOException e) {
            mostrarError("No se pudo abrir el detalle del inmueble.");
            e.printStackTrace();
        }
    }

    @FXML
    private void administrarImagenes() {
        InmuebleTabla seleccionado = tablaInmuebles.getSelectionModel().getSelectedItem();

        if (seleccionado == null) {
            mostrarError("Selecciona un inmueble para administrar sus imágenes.");
            return;
        }

        try {
            FXMLLoader loader = Navegacion.cargarVista("/fxml/imagenes_inmueble.fxml");

            ImagenesInmuebleController controller = loader.getController();
            controller.setIdInmueble(seleccionado.getIdInmueble());

            Stage stage = (Stage) tablaInmuebles.getScene().getWindow();

            Navegacion.cambiarEscena(
                    stage,
                    loader,
                    "Rentify - Imágenes del Inmueble"
            );

        } catch (IOException e) {
            mostrarError("No se pudo abrir la pantalla de imágenes.");
            e.printStackTrace();
        }
    }

    @FXML
    private void eliminarInmueble() {
        InmuebleTabla seleccionado = tablaInmuebles.getSelectionModel().getSelectedItem();

        if (seleccionado == null) {
            mostrarError("Selecciona un inmueble para eliminar.");
            return;
        }

        boolean tieneHistorial = inmuebleDAO.tieneHistorial(seleccionado.getIdInmueble());

        if (tieneHistorial) {
            mostrarError(
                    "No se puede eliminar este inmueble porque tiene solicitudes o arrendamientos asociados.\n\n" +
                            "Para conservar la integridad del historial, cambia su estado a 'No disponible'."
            );
            return;
        }

        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacion.setTitle("Confirmar eliminación");
        confirmacion.setHeaderText("Eliminar inmueble");
        confirmacion.setContentText(
                "¿Seguro que deseas eliminar el inmueble \"" +
                        seleccionado.getTitulo() +
                        "\"?\n\nTambién se eliminarán sus imágenes registradas."
        );

        Optional<ButtonType> resultado = confirmacion.showAndWait();

        if (resultado.isEmpty()
                || resultado.get() != javafx.scene.control.ButtonType.OK) {
            return;
        }

        boolean eliminado = inmuebleDAO.eliminarInmuebleSinHistorial(
                seleccionado.getIdInmueble()
        );

        if (eliminado) {
            cargarInmuebles();
            mostrarInformacion("Inmueble eliminado correctamente.");
        } else {
            mostrarError("No se pudo eliminar el inmueble.");
        }
    }

    @FXML
    private void cambiarEstadoInmueble() {
        InmuebleTabla seleccionado = tablaInmuebles.getSelectionModel().getSelectedItem();

        if (seleccionado == null) {
            mostrarError("Selecciona un inmueble para cambiar su estado.");
            return;
        }

        ChoiceDialog<String> dialog = new ChoiceDialog<>(
                seleccionado.getEstado(),
                "Disponible",
                "Ocupado",
                "No disponible"
        );
        dialog.setTitle("Cambiar estado");
        dialog.setHeaderText("Cambiar estado del inmueble");
        dialog.setContentText("Nuevo estado:");

        Optional<String> resultado = dialog.showAndWait();

        if (resultado.isEmpty()) {
            return;
        }

        String nuevoEstado = resultado.get();

        if ("Disponible".equalsIgnoreCase(nuevoEstado)
                && arrendamientoDAO.existeArrendamientoActivoPorInmueble(seleccionado.getIdInmueble())) {
            mostrarError("No puedes poner el inmueble como Disponible porque tiene un arrendamiento activo.");
            return;
        }

        int idEstado = convertirEstadoAId(nuevoEstado);

        boolean actualizado = inmuebleDAO.actualizarEstadoInmueble(
                seleccionado.getIdInmueble(),
                idEstado
        );

        if (actualizado) {
            cargarInmuebles();
            mostrarInformacion("Estado actualizado correctamente.");
        } else {
            mostrarError("No se pudo actualizar el estado del inmueble.");
        }
    }

    @FXML
    private void volverAlPanel() {
        try {
            FXMLLoader loader = Navegacion.cargarVista("/fxml/arrendador.fxml");
            ArrendadorController controller = loader.getController();

            if (Sesion.getUsuarioActual() != null) {
                controller.setNombreUsuario(Sesion.getUsuarioActual().getNombre());
            }

            Stage stage = (Stage) tablaInmuebles.getScene().getWindow();
            Navegacion.cambiarEscena(stage, loader, "Rentify - Arrendador");
        } catch (IOException e) {
            mostrarError("No se pudo volver al panel.");
            e.printStackTrace();
        }
    }

    private int convertirEstadoAId(String estado) {
        return switch (estado) {
            case "Disponible" -> 1;
            case "Ocupado" -> 2;
            case "No disponible" -> 3;
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