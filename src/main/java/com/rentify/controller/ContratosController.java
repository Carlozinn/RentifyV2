package com.rentify.controller;

import com.rentify.dao.ContratoDAO;
import com.rentify.model.ContratoTabla;
import com.rentify.util.Navegacion;
import com.rentify.util.Sesion;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

public class ContratosController {

    private static final int ROL_ARRENDADOR = 2;
    private static final int ROL_ARRENDATARIO = 3;

    @FXML
    private TableView<ContratoTabla> tablaContratos;

    @FXML
    private TableColumn<ContratoTabla, Integer> colId;

    @FXML
    private TableColumn<ContratoTabla, String> colFolio;

    @FXML
    private TableColumn<ContratoTabla, String> colInmueble;

    @FXML
    private TableColumn<ContratoTabla, String> colContraparte;

    @FXML
    private TableColumn<ContratoTabla, String> colFechaGeneracion;

    @FXML
    private TableColumn<ContratoTabla, String> colFechaFirma;

    @FXML
    private TableColumn<ContratoTabla, String> colEstado;

    @FXML
    private TableColumn<ContratoTabla, String> colArchivo;

    @FXML
    private Button btnFirmarContrato;

    @FXML
    private Button btnAbrirPdf;

    @FXML
    private Button btnCancelarContrato;

    private final ContratoDAO contratoDAO;

    public ContratosController() {
        this.contratoDAO = new ContratoDAO();
    }

    @FXML
    public void initialize() {
        configurarColumnas();
        configurarVistaSegunRol();
        cargarContratos();
    }

    private void configurarColumnas() {
        colId.setCellValueFactory(new PropertyValueFactory<>("idContrato"));
        colFolio.setCellValueFactory(new PropertyValueFactory<>("folioContrato"));
        colInmueble.setCellValueFactory(new PropertyValueFactory<>("inmueble"));
        colContraparte.setCellValueFactory(new PropertyValueFactory<>("contraparte"));
        colFechaGeneracion.setCellValueFactory(new PropertyValueFactory<>("fechaGeneracion"));
        colFechaFirma.setCellValueFactory(new PropertyValueFactory<>("fechaFirma"));
        colEstado.setCellValueFactory(new PropertyValueFactory<>("estado"));
        colArchivo.setCellValueFactory(new PropertyValueFactory<>("archivoPdf"));
    }

    private void configurarVistaSegunRol() {
        boolean esArrendador = esRol(ROL_ARRENDADOR);
        boolean esArrendatario = esRol(ROL_ARRENDATARIO);

        mostrarBoton(btnAbrirPdf, esArrendador || esArrendatario);
        mostrarBoton(btnCancelarContrato, esArrendador);
        mostrarBoton(btnFirmarContrato, esArrendatario);
    }

    @FXML
    private void cargarContratos() {
        if (Sesion.getUsuarioActual() == null) {
            mostrarError("No hay sesión activa.");
            return;
        }

        List<ContratoTabla> lista;

        if (esRol(ROL_ARRENDADOR)) {
            lista = contratoDAO.listarContratosComoArrendador(
                    Sesion.getUsuarioActual().getIdUsuario()
            );
        } else if (esRol(ROL_ARRENDATARIO)) {
            lista = contratoDAO.listarContratosComoArrendatario(
                    Sesion.getUsuarioActual().getIdUsuario()
            );
        } else {
            mostrarError("Este módulo no aplica para este rol.");
            tablaContratos.setItems(FXCollections.observableArrayList());
            return;
        }

        ObservableList<ContratoTabla> datos = FXCollections.observableArrayList(lista);
        tablaContratos.setItems(datos);
    }

    @FXML
    private void firmarContrato() {
        if (!esRol(ROL_ARRENDATARIO)) {
            mostrarError("Solo el arrendatario puede firmar contratos.");
            return;
        }

        ContratoTabla seleccionado = obtenerContratoSeleccionado();

        if (seleccionado == null) {
            return;
        }

        if (!"Generado".equalsIgnoreCase(seleccionado.getEstado())) {
            mostrarError("Solo puedes firmar contratos en estado Generado.");
            return;
        }

        boolean confirmado = confirmar(
                "Confirmar firma",
                "Firmar contrato",
                "¿Confirmas que deseas firmar este contrato?\n\n" +
                        "Folio: " + seleccionado.getFolioContrato() + "\n" +
                        "Inmueble: " + seleccionado.getInmueble() + "\n\n" +
                        "Después de firmarlo, el arrendador podrá generar los pagos correspondientes."
        );

        if (!confirmado) {
            return;
        }

        boolean actualizado = contratoDAO.firmarContrato(seleccionado.getIdContrato());

        if (actualizado) {
            cargarContratos();
            mostrarInfo("Contrato firmado correctamente.");
        } else {
            mostrarError("No se pudo firmar el contrato.");
        }
    }

    @FXML
    private void cancelarContrato() {
        if (!esRol(ROL_ARRENDADOR)) {
            mostrarError("Solo el arrendador puede cancelar contratos.");
            return;
        }

        ContratoTabla seleccionado = obtenerContratoSeleccionado();

        if (seleccionado == null) {
            return;
        }

        if ("Firmado".equalsIgnoreCase(seleccionado.getEstado())) {
            mostrarError("No puedes cancelar un contrato ya firmado.");
            return;
        }

        if ("Cancelado".equalsIgnoreCase(seleccionado.getEstado())) {
            mostrarError("El contrato ya está cancelado.");
            return;
        }

        boolean confirmado = confirmar(
                "Confirmar cancelación",
                "Cancelar contrato",
                "¿Deseas cancelar este contrato?\n\n" +
                        "Folio: " + seleccionado.getFolioContrato() + "\n" +
                        "Inmueble: " + seleccionado.getInmueble()
        );

        if (!confirmado) {
            return;
        }

        boolean actualizado = contratoDAO.cancelarContrato(seleccionado.getIdContrato());

        if (actualizado) {
            cargarContratos();
            mostrarInfo("Contrato cancelado correctamente.");
        } else {
            mostrarError("No se pudo cancelar el contrato.");
        }
    }

    @FXML
    private void abrirPdfContrato() {
        if (!esRol(ROL_ARRENDADOR) && !esRol(ROL_ARRENDATARIO)) {
            mostrarError("No tienes permiso para consultar contratos.");
            return;
        }

        ContratoTabla seleccionado = obtenerContratoSeleccionado();

        if (seleccionado == null) {
            return;
        }

        String rutaPdf = seleccionado.getArchivoPdf();

        if (rutaPdf == null || rutaPdf.isBlank()) {
            mostrarError("Este contrato no tiene archivo PDF registrado.");
            return;
        }

        File archivo = new File(rutaPdf);

        if (!archivo.exists()) {
            mostrarError("No se encontró el archivo PDF en la ruta registrada:\n" + rutaPdf);
            return;
        }

        try {
            if (!Desktop.isDesktopSupported()) {
                mostrarError("Tu sistema no permite abrir archivos automáticamente.");
                return;
            }

            Desktop.getDesktop().open(archivo);

        } catch (IOException e) {
            mostrarError("No se pudo abrir el PDF del contrato.");
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

            Stage stage = (Stage) tablaContratos.getScene().getWindow();
            Navegacion.cambiarEscena(stage, loader, titulo);

        } catch (IOException e) {
            mostrarError("No se pudo volver al panel.");
            e.printStackTrace();
        }
    }

    private ContratoTabla obtenerContratoSeleccionado() {
        ContratoTabla seleccionado = tablaContratos.getSelectionModel().getSelectedItem();

        if (seleccionado == null) {
            mostrarError("Selecciona un contrato.");
            return null;
        }

        return seleccionado;
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
        Stage stage = (Stage) tablaContratos.getScene().getWindow();
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