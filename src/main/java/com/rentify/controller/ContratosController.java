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
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import javafx.scene.control.ButtonType;

import java.awt.Desktop;
import java.io.File;
import java.util.Optional;

import java.io.IOException;
import java.util.List;

public class ContratosController {

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
        colId.setCellValueFactory(new PropertyValueFactory<>("idContrato"));
        colFolio.setCellValueFactory(new PropertyValueFactory<>("folioContrato"));
        colInmueble.setCellValueFactory(new PropertyValueFactory<>("inmueble"));
        colContraparte.setCellValueFactory(new PropertyValueFactory<>("contraparte"));
        colFechaGeneracion.setCellValueFactory(new PropertyValueFactory<>("fechaGeneracion"));
        colFechaFirma.setCellValueFactory(new PropertyValueFactory<>("fechaFirma"));
        colEstado.setCellValueFactory(new PropertyValueFactory<>("estado"));
        colArchivo.setCellValueFactory(new PropertyValueFactory<>("archivoPdf"));

        configurarVistaSegunRol();
        cargarContratos();
    }

    private void configurarVistaSegunRol() {
        if (Sesion.getUsuarioActual() == null) {
            return;
        }

        int idRol = Sesion.getUsuarioActual().getIdRol();

        if (idRol == 2) {
            /*
             * Arrendador:
             * - puede cancelar contratos generados
             * - puede abrir PDF
             * - no firma el contrato
             */
            btnFirmarContrato.setVisible(false);
            btnFirmarContrato.setManaged(false);

            btnCancelarContrato.setVisible(true);
            btnCancelarContrato.setManaged(true);

            btnAbrirPdf.setVisible(true);
            btnAbrirPdf.setManaged(true);

        } else if (idRol == 3) {
            /*
             * Arrendatario:
             * - puede firmar contratos generados
             * - puede abrir PDF
             * - no cancela contrato
             */
            btnFirmarContrato.setVisible(true);
            btnFirmarContrato.setManaged(true);

            btnCancelarContrato.setVisible(false);
            btnCancelarContrato.setManaged(false);

            btnAbrirPdf.setVisible(true);
            btnAbrirPdf.setManaged(true);
        }
    }

    @FXML
    private void cargarContratos() {
        if (Sesion.getUsuarioActual() == null) {
            mostrarError("No hay sesión activa.");
            return;
        }

        List<ContratoTabla> lista;

        if (Sesion.getUsuarioActual().getIdRol() == 2) {
            lista = contratoDAO.listarContratosComoArrendador(
                    Sesion.getUsuarioActual().getIdUsuario()
            );
        } else if (Sesion.getUsuarioActual().getIdRol() == 3) {
            lista = contratoDAO.listarContratosComoArrendatario(
                    Sesion.getUsuarioActual().getIdUsuario()
            );
        } else {
            mostrarError("Este módulo no aplica para este rol.");
            return;
        }

        ObservableList<ContratoTabla> datos = FXCollections.observableArrayList(lista);
        tablaContratos.setItems(datos);
    }

    @FXML
    private void firmarContrato() {
        if (Sesion.getUsuarioActual() == null || Sesion.getUsuarioActual().getIdRol() != 3) {
            mostrarError("Solo el arrendatario puede firmar contratos.");
            return;
        }

        ContratoTabla seleccionado = tablaContratos.getSelectionModel().getSelectedItem();

        if (seleccionado == null) {
            mostrarError("Selecciona un contrato.");
            return;
        }

        if (!"Generado".equalsIgnoreCase(seleccionado.getEstado())) {
            mostrarError("Solo puedes firmar contratos en estado Generado.");
            return;
        }

        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacion.setTitle("Confirmar firma");
        confirmacion.setHeaderText("Firmar contrato");
        confirmacion.setContentText(
                "¿Confirmas que deseas firmar este contrato?\n\n" +
                        "Folio: " + seleccionado.getFolioContrato() + "\n" +
                        "Inmueble: " + seleccionado.getInmueble() + "\n\n" +
                        "Después de firmarlo, el arrendador podrá generar los pagos correspondientes."
        );

        Optional<ButtonType> resultado = confirmacion.showAndWait();

        if (resultado.isEmpty() || resultado.get() != ButtonType.OK) {
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
        if (Sesion.getUsuarioActual() == null || Sesion.getUsuarioActual().getIdRol() != 2) {
            mostrarError("Solo el arrendador puede cancelar contratos.");
            return;
        }

        ContratoTabla seleccionado = tablaContratos.getSelectionModel().getSelectedItem();

        if (seleccionado == null) {
            mostrarError("Selecciona un contrato.");
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
        ContratoTabla seleccionado = tablaContratos.getSelectionModel().getSelectedItem();

        if (seleccionado == null) {
            mostrarError("Selecciona un contrato.");
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

            Stage stage = (Stage) tablaContratos.getScene().getWindow();
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