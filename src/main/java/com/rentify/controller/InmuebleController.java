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
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

public class InmuebleController {

    private static final int ROL_ARRENDADOR = 2;

    private static final int ESTADO_DISPONIBLE = 1;
    private static final int ESTADO_NO_DISPONIBLE = 3;

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
        configurarColumnas();
        cargarInmuebles();
    }

    private void configurarColumnas() {
        colId.setCellValueFactory(new PropertyValueFactory<>("idInmueble"));
        colTitulo.setCellValueFactory(new PropertyValueFactory<>("titulo"));
        colCiudad.setCellValueFactory(new PropertyValueFactory<>("ciudad"));
        colTipo.setCellValueFactory(new PropertyValueFactory<>("tipo"));
        colEstado.setCellValueFactory(new PropertyValueFactory<>("estado"));
        colPrecio.setCellValueFactory(new PropertyValueFactory<>("precioRenta"));
    }

    @FXML
    private void cargarInmuebles() {
        if (!esArrendador()) {
            mostrarError("Solo el arrendador puede gestionar inmuebles.");
            tablaInmuebles.setItems(FXCollections.observableArrayList());
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
        if (!esArrendador()) {
            mostrarError("Solo el arrendador puede registrar inmuebles.");
            return;
        }

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
        if (!esArrendador()) {
            mostrarError("Solo el arrendador puede editar inmuebles.");
            return;
        }

        InmuebleTabla seleccionado = obtenerInmuebleSeleccionado(
                "Selecciona un inmueble para editar."
        );

        if (seleccionado == null) {
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
        if (!esArrendador()) {
            mostrarError("Solo el arrendador puede consultar esta pantalla.");
            return;
        }

        InmuebleTabla seleccionado = obtenerInmuebleSeleccionado(
                "Selecciona un inmueble para ver el detalle."
        );

        if (seleccionado == null) {
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
        if (!esArrendador()) {
            mostrarError("Solo el arrendador puede administrar imágenes.");
            return;
        }

        InmuebleTabla seleccionado = obtenerInmuebleSeleccionado(
                "Selecciona un inmueble para administrar sus imágenes."
        );

        if (seleccionado == null) {
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
        if (!esArrendador()) {
            mostrarError("Solo el arrendador puede eliminar inmuebles.");
            return;
        }

        InmuebleTabla seleccionado = obtenerInmuebleSeleccionado(
                "Selecciona un inmueble para eliminar."
        );

        if (seleccionado == null) {
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

        boolean confirmado = confirmar(
                "Confirmar eliminación",
                "Eliminar inmueble",
                "¿Seguro que deseas eliminar el inmueble \"" +
                        seleccionado.getTitulo() +
                        "\"?\n\nTambién se eliminarán sus imágenes registradas."
        );

        if (!confirmado) {
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
        if (!esArrendador()) {
            mostrarError("Solo el arrendador puede cambiar el estado del inmueble.");
            return;
        }

        InmuebleTabla seleccionado = obtenerInmuebleSeleccionado(
                "Selecciona un inmueble para cambiar su estado."
        );

        if (seleccionado == null) {
            return;
        }

        /*
         * No se permite cambiar manualmente a Ocupado.
         * Ese estado debe generarse desde el flujo real:
         * solicitud aceptada → arrendamiento activo.
         */
        ChoiceDialog<String> dialog = new ChoiceDialog<>(
                seleccionado.getEstado(),
                "Disponible",
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

        boolean confirmado = confirmar(
                "Confirmar cambio de estado",
                "Cambiar estado del inmueble",
                "¿Deseas cambiar el inmueble \"" + seleccionado.getTitulo() +
                        "\" a estado " + nuevoEstado + "?"
        );

        if (!confirmado) {
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
            if (!esArrendador()) {
                volverALogin();
                return;
            }

            FXMLLoader loader = Navegacion.cargarVista("/fxml/arrendador.fxml");
            ArrendadorController controller = loader.getController();
            controller.setNombreUsuario(Sesion.getUsuarioActual().getNombre());

            Stage stage = (Stage) tablaInmuebles.getScene().getWindow();
            Navegacion.cambiarEscena(stage, loader, "Rentify - Arrendador");

        } catch (IOException e) {
            mostrarError("No se pudo volver al panel.");
            e.printStackTrace();
        }
    }

    private InmuebleTabla obtenerInmuebleSeleccionado(String mensajeError) {
        InmuebleTabla seleccionado = tablaInmuebles.getSelectionModel().getSelectedItem();

        if (seleccionado == null) {
            mostrarError(mensajeError);
            return null;
        }

        return seleccionado;
    }

    private int convertirEstadoAId(String estado) {
        return switch (estado) {
            case "Disponible" -> ESTADO_DISPONIBLE;
            case "No disponible" -> ESTADO_NO_DISPONIBLE;
            default -> ESTADO_NO_DISPONIBLE;
        };
    }

    private boolean esArrendador() {
        return Sesion.getUsuarioActual() != null
                && Sesion.getUsuarioActual().getIdRol() == ROL_ARRENDADOR;
    }

    private boolean confirmar(String titulo, String encabezado, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(encabezado);
        alert.setContentText(mensaje);

        Optional<ButtonType> resultado = alert.showAndWait();

        return resultado.isPresent() && resultado.get() == ButtonType.OK;
    }

    private void volverALogin() throws IOException {
        FXMLLoader loader = Navegacion.cargarVista("/fxml/login.fxml");
        Stage stage = (Stage) tablaInmuebles.getScene().getWindow();
        Navegacion.cambiarEscena(stage, loader, "Rentify - Login");
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