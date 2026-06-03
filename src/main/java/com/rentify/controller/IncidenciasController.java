package com.rentify.controller;

import com.rentify.dao.IncidenciaDAO;
import com.rentify.model.IncidenciaDetalle;
import com.rentify.model.IncidenciaTabla;
import com.rentify.util.Navegacion;
import com.rentify.util.Sesion;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

public class IncidenciasController {

    private static final int ROL_ARRENDADOR = 2;
    private static final int ROL_ARRENDATARIO = 3;

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
        configurarColumnas();
        configurarVistaSegunRol();
        cargarIncidencias();
    }

    private void configurarColumnas() {
        colId.setCellValueFactory(new PropertyValueFactory<>("idIncidencia"));
        colTitulo.setCellValueFactory(new PropertyValueFactory<>("titulo"));
        colArrendamiento.setCellValueFactory(new PropertyValueFactory<>("arrendamiento"));
        colReporta.setCellValueFactory(new PropertyValueFactory<>("reporta"));
        colFechaReporte.setCellValueFactory(new PropertyValueFactory<>("fechaReporte"));
        colPrioridad.setCellValueFactory(new PropertyValueFactory<>("prioridad"));
        colEstado.setCellValueFactory(new PropertyValueFactory<>("estado"));
    }

    private void configurarVistaSegunRol() {
        boolean esArrendador = esRol(ROL_ARRENDADOR);
        boolean esArrendatario = esRol(ROL_ARRENDATARIO);

        mostrarBoton(btnNuevaIncidencia, esArrendatario);
        mostrarBoton(btnEnProceso, esArrendador);
        mostrarBoton(btnResolver, esArrendador);
    }

    @FXML
    private void cargarIncidencias() {
        if (Sesion.getUsuarioActual() == null) {
            mostrarError("No hay sesión activa.");
            tablaIncidencias.setItems(FXCollections.observableArrayList());
            return;
        }

        List<IncidenciaTabla> lista;

        if (esRol(ROL_ARRENDADOR)) {
            lista = incidenciaDAO.listarIncidenciasComoArrendador(
                    Sesion.getUsuarioActual().getIdUsuario()
            );
        } else if (esRol(ROL_ARRENDATARIO)) {
            lista = incidenciaDAO.listarIncidenciasComoArrendatario(
                    Sesion.getUsuarioActual().getIdUsuario()
            );
        } else {
            mostrarError("Este módulo no aplica para este rol.");
            tablaIncidencias.setItems(FXCollections.observableArrayList());
            return;
        }

        ObservableList<IncidenciaTabla> datos = FXCollections.observableArrayList(lista);
        tablaIncidencias.setItems(datos);
    }

    @FXML
    private void verDetalleIncidencia() {
        if (!esRol(ROL_ARRENDADOR) && !esRol(ROL_ARRENDATARIO)) {
            mostrarError("No tienes permiso para consultar incidencias.");
            return;
        }

        IncidenciaTabla seleccionada = obtenerIncidenciaSeleccionada();

        if (seleccionada == null) {
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

        VBox contenido = new VBox(8);
        contenido.setStyle("-fx-padding: 10;");

        Label lblId = new Label("ID: " + detalle.getIdIncidencia());
        Label lblArrendamiento = new Label("Arrendamiento: " + detalle.getArrendamiento());
        Label lblReporta = new Label("Reporta: " + detalle.getReporta());
        Label lblPrioridad = new Label("Prioridad: " + detalle.getPrioridad());
        Label lblEstado = new Label("Estado: " + detalle.getEstado());
        Label lblFechaReporte = new Label("Fecha reporte: " + detalle.getFechaReporte());
        Label lblFechaCierre = new Label("Fecha cierre: " + detalle.getFechaCierre());

        Label lblDescripcionTitulo = new Label("Descripción:");
        lblDescripcionTitulo.setStyle("-fx-font-weight: bold;");

        Label lblDescripcion = new Label(detalle.getDescripcion());
        lblDescripcion.setWrapText(true);

        Label lblSolucionTitulo = new Label("Solución:");
        lblSolucionTitulo.setStyle("-fx-font-weight: bold; -fx-font-size: 13px;");

        Label lblSolucion = new Label(detalle.getSolucion());
        lblSolucion.setWrapText(true);
        lblSolucion.setStyle("-fx-font-weight: bold;");

        contenido.getChildren().addAll(
                lblId,
                lblArrendamiento,
                lblReporta,
                lblPrioridad,
                lblEstado,
                lblFechaReporte,
                lblFechaCierre,
                lblDescripcionTitulo,
                lblDescripcion,
                lblSolucionTitulo,
                lblSolucion
        );

        ScrollPane scrollPane = new ScrollPane(contenido);
        scrollPane.setFitToWidth(true);
        scrollPane.setPrefWidth(500);
        scrollPane.setPrefHeight(350);

        alert.getDialogPane().setContent(scrollPane);
        alert.showAndWait();
    }

    @FXML
    private void abrirNuevaIncidencia() {
        if (!esRol(ROL_ARRENDATARIO)) {
            mostrarError("Solo el arrendatario puede registrar incidencias.");
            return;
        }

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
        if (!esRol(ROL_ARRENDADOR)) {
            mostrarError("Solo el arrendador puede cambiar el estado.");
            return;
        }

        IncidenciaTabla seleccionada = obtenerIncidenciaSeleccionada();

        if (seleccionada == null) {
            return;
        }

        if (!"Abierta".equalsIgnoreCase(seleccionada.getEstado())) {
            mostrarError("Solo puedes marcar en proceso incidencias abiertas.");
            return;
        }

        boolean confirmado = confirmar(
                "Confirmar cambio",
                "Marcar incidencia en proceso",
                "¿Deseas marcar esta incidencia como En proceso?\n\n" +
                        seleccionada.getTitulo()
        );

        if (!confirmado) {
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
        if (!esRol(ROL_ARRENDADOR)) {
            mostrarError("Solo el arrendador puede resolver incidencias.");
            return;
        }

        IncidenciaTabla seleccionada = obtenerIncidenciaSeleccionada();

        if (seleccionada == null) {
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
            if (Sesion.getUsuarioActual() == null) {
                volverALogin();
                return;
            }

            FXMLLoader loader;
            String titulo;

            if (esRol(ROL_ARRENDADOR)) {
                loader = Navegacion.cargarVista("/fxml/arrendador.fxml");
                ArrendadorController controller = loader.getController();
                controller.setNombreUsuario(Sesion.getUsuarioActual().getNombre());
                titulo = "Rentify - Arrendador";
            } else if (esRol(ROL_ARRENDATARIO)) {
                loader = Navegacion.cargarVista("/fxml/arrendatario.fxml");
                ArrendatarioController controller = loader.getController();
                controller.setNombreUsuario(Sesion.getUsuarioActual().getNombre());
                titulo = "Rentify - Arrendatario";
            } else {
                mostrarError("Este módulo no aplica para este rol.");
                return;
            }

            Stage stage = (Stage) tablaIncidencias.getScene().getWindow();
            Navegacion.cambiarEscena(stage, loader, titulo);

        } catch (IOException e) {
            mostrarError("No se pudo volver al panel.");
            e.printStackTrace();
        }
    }

    private IncidenciaTabla obtenerIncidenciaSeleccionada() {
        IncidenciaTabla seleccionada = tablaIncidencias.getSelectionModel().getSelectedItem();

        if (seleccionada == null) {
            mostrarError("Selecciona una incidencia.");
            return null;
        }

        return seleccionada;
    }

    private boolean esRol(int idRol) {
        return Sesion.getUsuarioActual() != null
                && Sesion.getUsuarioActual().getIdRol() == idRol;
    }

    private void mostrarBoton(Button boton, boolean visible) {
        boton.setVisible(visible);
        boton.setManaged(visible);
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
        Stage stage = (Stage) tablaIncidencias.getScene().getWindow();
        Navegacion.cambiarEscena(stage, loader, "Rentify - Login");
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