package com.rentify.controller;

import com.rentify.dao.IncidenciaDAO;
import com.rentify.model.IncidenciaTabla;
import com.rentify.model.IncidenciaDetalle;
import com.rentify.util.Navegacion;
import com.rentify.util.Sesion;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.List;

public class IncidenciasController {

    @FXML
    private TableView<IncidenciaTabla> tablaIncidencias;

    @FXML
    private TableColumn<IncidenciaTabla, Integer> colId;

    @FXML
    private TableColumn<IncidenciaTabla, String> colTitulo;

    @FXML
    private TableColumn<IncidenciaTabla, String> colArrendamiento;

    @FXML
    private TableColumn<IncidenciaTabla, String> colReporta;

    @FXML
    private TableColumn<IncidenciaTabla, String> colFechaReporte;

    @FXML
    private TableColumn<IncidenciaTabla, String> colPrioridad;

    @FXML
    private TableColumn<IncidenciaTabla, String> colEstado;

    @FXML
    private Button btnNuevaIncidencia;

    @FXML
    private Button btnEnProceso;

    @FXML
    private Button btnResolver;

    private final IncidenciaDAO incidenciaDAO;

    public IncidenciasController() {
        this.incidenciaDAO = new IncidenciaDAO();
    }

    @FXML
    public void initialize() {
        colId.setCellValueFactory(new PropertyValueFactory<>("idIncidencia"));
        colTitulo.setCellValueFactory(new PropertyValueFactory<>("titulo"));
        colArrendamiento.setCellValueFactory(new PropertyValueFactory<>("arrendamiento"));
        colReporta.setCellValueFactory(new PropertyValueFactory<>("reporta"));
        colFechaReporte.setCellValueFactory(new PropertyValueFactory<>("fechaReporte"));
        colPrioridad.setCellValueFactory(new PropertyValueFactory<>("prioridad"));
        colEstado.setCellValueFactory(new PropertyValueFactory<>("estado"));

        configurarVistaSegunRol();
        cargarIncidencias();
    }

    private void configurarVistaSegunRol() {
        if (Sesion.getUsuarioActual() != null && Sesion.getUsuarioActual().getIdRol() == 2) {
            btnNuevaIncidencia.setVisible(false);
            btnNuevaIncidencia.setManaged(false);
        } else {
            btnEnProceso.setVisible(false);
            btnEnProceso.setManaged(false);
            btnResolver.setVisible(false);
            btnResolver.setManaged(false);
        }
    }

    @FXML
    private void cargarIncidencias() {
        if (Sesion.getUsuarioActual() == null) {
            mostrarError("No hay sesión activa.");
            return;
        }

        List<IncidenciaTabla> lista;

        if (Sesion.getUsuarioActual().getIdRol() == 2) {
            lista = incidenciaDAO.listarIncidenciasComoArrendador(Sesion.getUsuarioActual().getIdUsuario());
        } else if (Sesion.getUsuarioActual().getIdRol() == 3) {
            lista = incidenciaDAO.listarIncidenciasComoArrendatario(Sesion.getUsuarioActual().getIdUsuario());
        } else {
            mostrarError("Este módulo no aplica para este rol.");
            return;
        }

        ObservableList<IncidenciaTabla> datos = FXCollections.observableArrayList(lista);
        tablaIncidencias.setItems(datos);
    }

    @FXML
    private void verDetalleIncidencia() {
        IncidenciaTabla seleccionada = tablaIncidencias.getSelectionModel().getSelectedItem();

        if (seleccionada == null) {
            mostrarError("Selecciona una incidencia para ver el detalle.");
            return;
        }

        IncidenciaDetalle detalle = incidenciaDAO.buscarDetallePorId(
                seleccionada.getIdIncidencia()
        );

        if (detalle == null) {
            mostrarError("No se encontró el detalle de la incidencia.");
            return;
        }

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Detalle de incidencia");
        alert.setHeaderText(detalle.getTitulo());

        alert.setContentText(
                "ID: " + detalle.getIdIncidencia() + "\n\n" +
                        "Arrendamiento: " + detalle.getArrendamiento() + "\n" +
                        "Reporta: " + detalle.getReporta() + "\n" +
                        "Prioridad: " + detalle.getPrioridad() + "\n" +
                        "Estado: " + detalle.getEstado() + "\n" +
                        "Fecha reporte: " + detalle.getFechaReporte() + "\n" +
                        "Fecha cierre: " + detalle.getFechaCierre() + "\n\n" +
                        "Descripción:\n" + detalle.getDescripcion() + "\n\n" +
                        "Solución:\n" + detalle.getSolucion()
        );

        alert.showAndWait();
    }

    @FXML
    private void abrirNuevaIncidencia() {
        try {
            FXMLLoader loader = Navegacion.cargarVista("/fxml/incidencia_form.fxml");
            Stage stage = (Stage) tablaIncidencias.getScene().getWindow();
            Navegacion.cambiarEscena(stage, loader, "Rentify - Nueva Incidencia");
        } catch (IOException e) {
            mostrarError("No se pudo abrir el formulario de incidencia.");
            e.printStackTrace();
        }
    }

    @FXML
    private void marcarEnProceso() {
        if (Sesion.getUsuarioActual() == null || Sesion.getUsuarioActual().getIdRol() != 2) {
            mostrarError("Solo el arrendador puede cambiar el estado.");
            return;
        }

        IncidenciaTabla seleccionada = tablaIncidencias.getSelectionModel().getSelectedItem();

        if (seleccionada == null) {
            mostrarError("Selecciona una incidencia.");
            return;
        }

        if (!"Abierta".equalsIgnoreCase(seleccionada.getEstado())) {
            mostrarError("Solo puedes marcar en proceso incidencias abiertas.");
            return;
        }

        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacion.setTitle("Confirmar cambio");
        confirmacion.setHeaderText("Marcar incidencia en proceso");
        confirmacion.setContentText(
                "¿Deseas marcar esta incidencia como En proceso?\n\n" +
                        seleccionada.getTitulo()
        );

        var resultado = confirmacion.showAndWait();

        if (resultado.isEmpty() || resultado.get() != javafx.scene.control.ButtonType.OK) {
            return;
        }

        boolean actualizado = incidenciaDAO.actualizarEstadoIncidencia(
                seleccionada.getIdIncidencia(),
                2
        );

        if (actualizado) {
            cargarIncidencias();
            mostrarInfo("Incidencia marcada en proceso.");
        } else {
            mostrarError("No se pudo actualizar la incidencia.");
        }
    }
    @FXML
    private void abrirResolverIncidencia() {
        if (Sesion.getUsuarioActual() == null || Sesion.getUsuarioActual().getIdRol() != 2) {
            mostrarError("Solo el arrendador puede resolver incidencias.");
            return;
        }

        IncidenciaTabla seleccionada = tablaIncidencias.getSelectionModel().getSelectedItem();

        if (seleccionada == null) {
            mostrarError("Selecciona una incidencia.");
            return;
        }

        if ("Resuelta".equalsIgnoreCase(seleccionada.getEstado())) {
            mostrarError("La incidencia ya está resuelta.");
            return;
        }

        try {
            FXMLLoader loader = Navegacion.cargarVista("/fxml/incidencia_resolver.fxml");
            IncidenciaResolverController controller = loader.getController();
            controller.setIdIncidencia(seleccionada.getIdIncidencia());

            Stage stage = (Stage) tablaIncidencias.getScene().getWindow();
            Navegacion.cambiarEscena(stage, loader, "Rentify - Resolver Incidencia");
        } catch (IOException e) {
            mostrarError("No se pudo abrir resolver incidencia.");
            e.printStackTrace();
        }
    }

    @FXML
    private void volverAlPanel() {
        try {
            FXMLLoader loader;
            String titulo;

            if (Sesion.getUsuarioActual() != null && Sesion.getUsuarioActual().getIdRol() == 2) {
                loader = Navegacion.cargarVista("/fxml/arrendador.fxml");
                ArrendadorController controller = loader.getController();
                controller.setNombreUsuario(Sesion.getUsuarioActual().getNombre());
                titulo = "Rentify - Arrendador";
            } else {
                loader = Navegacion.cargarVista("/fxml/arrendatario.fxml");
                ArrendatarioController controller = loader.getController();
                controller.setNombreUsuario(Sesion.getUsuarioActual().getNombre());
                titulo = "Rentify - Arrendatario";
            }

            Stage stage = (Stage) tablaIncidencias.getScene().getWindow();
            Navegacion.cambiarEscena(stage, loader, titulo);
        } catch (IOException e) {
            mostrarError("No se pudo volver al panel.");
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

    private void mostrarInfo(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Información");
        alert.setHeaderText("Operación exitosa");
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}