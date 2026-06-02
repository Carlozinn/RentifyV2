package com.rentify.controller;

import com.rentify.dao.ImagenInmuebleDAO;
import com.rentify.model.ImagenInmueble;
import com.rentify.util.Navegacion;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.text.Normalizer;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class ImagenesInmuebleController {

    @FXML
    private Label lblArchivoSeleccionado;

    @FXML
    private ImageView imgPreview;

    @FXML
    private TextField txtDescripcion;

    @FXML
    private TextField txtOrdenVisualizacion;

    @FXML
    private CheckBox chkEsPrincipal;

    @FXML
    private TableView<ImagenInmueble> tablaImagenes;

    @FXML
    private TableColumn<ImagenInmueble, Integer> colId;

    @FXML
    private TableColumn<ImagenInmueble, String> colUrl;

    @FXML
    private TableColumn<ImagenInmueble, String> colDescripcion;

    @FXML
    private TableColumn<ImagenInmueble, Boolean> colPrincipal;

    @FXML
    private TableColumn<ImagenInmueble, Integer> colOrdenVisualizacion;

    @FXML
    private Label lblMensaje;

    private final ImagenInmuebleDAO imagenDAO;

    private int idInmueble;
    private File archivoSeleccionado;

    public ImagenesInmuebleController() {
        this.imagenDAO = new ImagenInmuebleDAO();
    }

    @FXML
    public void initialize() {
        configurarColumnas();
        configurarFormulario();
    }

    private void configurarColumnas() {
        colId.setCellValueFactory(new PropertyValueFactory<>("idImagen"));
        colUrl.setCellValueFactory(new PropertyValueFactory<>("urlImagen"));
        colDescripcion.setCellValueFactory(new PropertyValueFactory<>("descripcion"));
        colPrincipal.setCellValueFactory(new PropertyValueFactory<>("esPrincipal"));
        colOrdenVisualizacion.setCellValueFactory(new PropertyValueFactory<>("ordenVisualizacion"));
    }

    private void configurarFormulario() {
        /*
         * Decisión final:
         * - La primera imagen será principal automáticamente.
         * - Para cambiar la principal se usa el botón "Marcar principal".
         * - No se permite marcar principal al insertar para evitar inconsistencias.
         */
        chkEsPrincipal.setSelected(false);
        chkEsPrincipal.setDisable(true);
        chkEsPrincipal.setTooltip(
                new Tooltip("La primera imagen será principal automáticamente. Para cambiarla, selecciona una imagen de la tabla y presiona 'Marcar principal'.")
        );

        lblArchivoSeleccionado.setText("Ningún archivo seleccionado");
        lblMensaje.setText("");
    }

    public void setIdInmueble(int idInmueble) {
        this.idInmueble = idInmueble;
        cargarImagenes();
    }

    @FXML
    private void seleccionarImagen() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Seleccionar imagen del inmueble");

        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Imágenes", "*.jpg", "*.jpeg", "*.png", "*.webp"),
                new FileChooser.ExtensionFilter("JPG", "*.jpg", "*.jpeg"),
                new FileChooser.ExtensionFilter("PNG", "*.png"),
                new FileChooser.ExtensionFilter("WEBP", "*.webp")
        );

        Stage stage = (Stage) tablaImagenes.getScene().getWindow();
        File archivo = fileChooser.showOpenDialog(stage);

        if (archivo == null) {
            return;
        }

        archivoSeleccionado = archivo;
        lblArchivoSeleccionado.setText(archivo.getName());

        Image imagenPreview = new Image(archivo.toURI().toString());
        imgPreview.setImage(imagenPreview);

        lblMensaje.setText("");
    }

    @FXML
    private void agregarImagen() {
        if (idInmueble <= 0) {
            lblMensaje.setText("No se puede agregar imagen: inmueble no válido.");
            return;
        }

        if (archivoSeleccionado == null) {
            lblMensaje.setText("Selecciona una imagen primero.");
            return;
        }

        String descripcion = txtDescripcion.getText() == null
                ? ""
                : txtDescripcion.getText().trim();

        String ordenTexto = txtOrdenVisualizacion.getText() == null
                ? ""
                : txtOrdenVisualizacion.getText().trim();

        Integer ordenVisualizacion = obtenerOrdenVisualizacion(ordenTexto);

        if (ordenVisualizacion == null && !ordenTexto.isEmpty()) {
            return;
        }

        if (ordenVisualizacion == null) {
            ordenVisualizacion = imagenDAO.obtenerSiguienteOrden(idInmueble);
        }

        boolean primeraImagen = imagenDAO.contarPorInmueble(idInmueble) == 0;

        try {
            String rutaGuardada = copiarImagenAProyecto(archivoSeleccionado, idInmueble);

            ImagenInmueble imagen = new ImagenInmueble();
            imagen.setUrlImagen(rutaGuardada);
            imagen.setDescripcion(descripcion.isEmpty() ? null : descripcion);
            imagen.setEsPrincipal(primeraImagen);
            imagen.setOrdenVisualizacion(ordenVisualizacion);
            imagen.setIdInmueble(idInmueble);

            boolean insertado = imagenDAO.insertarImagen(imagen);

            if (insertado) {
                limpiarFormulario();
                cargarImagenes();

                if (primeraImagen) {
                    lblMensaje.setText("Imagen agregada correctamente. Se marcó como principal por ser la primera.");
                } else {
                    lblMensaje.setText("Imagen agregada correctamente.");
                }

            } else {
                lblMensaje.setText("No se pudo guardar la imagen en la base de datos.");
            }

        } catch (IOException e) {
            lblMensaje.setText("No se pudo copiar la imagen al proyecto.");
            e.printStackTrace();
        }
    }

    private String copiarImagenAProyecto(File archivoOrigen, int idInmueble) throws IOException {
        Path carpetaDestino = Path.of(
                "documentos",
                "imagenes_inmuebles",
                "inmueble_" + idInmueble
        );

        Files.createDirectories(carpetaDestino);

        String extension = obtenerExtension(archivoOrigen.getName());
        String nombreLimpio = limpiarNombreArchivo(archivoOrigen.getName());

        String fecha = LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));

        String nombreFinal = fecha + "_" + nombreLimpio;

        if (!nombreFinal.toLowerCase().endsWith(extension.toLowerCase())) {
            nombreFinal = nombreFinal + extension;
        }

        Path destino = carpetaDestino.resolve(nombreFinal);

        Files.copy(
                archivoOrigen.toPath(),
                destino,
                StandardCopyOption.REPLACE_EXISTING
        );

        return destino.toString();
    }

    private String obtenerExtension(String nombreArchivo) {
        int ultimoPunto = nombreArchivo.lastIndexOf(".");

        if (ultimoPunto == -1) {
            return ".jpg";
        }

        return nombreArchivo.substring(ultimoPunto);
    }

    private String limpiarNombreArchivo(String nombreArchivo) {
        String nombreSinExtension = nombreArchivo;

        int ultimoPunto = nombreArchivo.lastIndexOf(".");
        if (ultimoPunto != -1) {
            nombreSinExtension = nombreArchivo.substring(0, ultimoPunto);
        }

        String normalizado = Normalizer.normalize(
                nombreSinExtension,
                Normalizer.Form.NFD
        );

        String limpio = normalizado
                .replaceAll("[\\p{InCombiningDiacriticalMarks}]", "")
                .replaceAll("[^a-zA-Z0-9-_]", "_")
                .replaceAll("_+", "_");

        if (limpio.isBlank()) {
            limpio = "imagen";
        }

        return limpio;
    }

    private Integer obtenerOrdenVisualizacion(String ordenTexto) {
        if (ordenTexto == null || ordenTexto.trim().isEmpty()) {
            return null;
        }

        try {
            int orden = Integer.parseInt(ordenTexto.trim());

            if (orden < 1) {
                lblMensaje.setText("El orden debe ser mayor o igual a 1.");
                return null;
            }

            return orden;

        } catch (NumberFormatException e) {
            lblMensaje.setText("El orden de visualización debe ser numérico.");
            return null;
        }
    }

    @FXML
    private void cargarImagenes() {
        if (idInmueble <= 0) {
            lblMensaje.setText("No se recibió un inmueble válido.");
            return;
        }

        List<ImagenInmueble> lista = imagenDAO.listarPorInmueble(idInmueble);
        tablaImagenes.setItems(FXCollections.observableArrayList(lista));
    }

    @FXML
    private void marcarPrincipal() {
        if (idInmueble <= 0) {
            lblMensaje.setText("No se puede marcar principal: inmueble no válido.");
            return;
        }

        ImagenInmueble seleccionada = tablaImagenes.getSelectionModel().getSelectedItem();

        if (seleccionada == null) {
            lblMensaje.setText("Selecciona una imagen.");
            return;
        }

        boolean actualizado = imagenDAO.marcarComoPrincipal(
                seleccionada.getIdImagen(),
                idInmueble
        );

        if (actualizado) {
            cargarImagenes();
            lblMensaje.setText("Imagen principal actualizada.");
        } else {
            lblMensaje.setText("No se pudo actualizar la imagen principal.");
        }
    }

    @FXML
    private void eliminarImagen() {
        if (idInmueble <= 0) {
            lblMensaje.setText("No se puede eliminar imagen: inmueble no válido.");
            return;
        }

        ImagenInmueble seleccionada = tablaImagenes.getSelectionModel().getSelectedItem();

        if (seleccionada == null) {
            lblMensaje.setText("Selecciona una imagen.");
            return;
        }

        boolean eraPrincipal = seleccionada.isEsPrincipal();

        boolean eliminado = imagenDAO.eliminarImagen(seleccionada.getIdImagen());

        if (eliminado) {
            if (eraPrincipal) {
                asignarNuevaPrincipalSiExiste();
            }

            cargarImagenes();
            limpiarFormulario();
            lblMensaje.setText("Imagen eliminada correctamente.");

        } else {
            lblMensaje.setText("No se pudo eliminar la imagen.");
        }
    }

    private void asignarNuevaPrincipalSiExiste() {
        List<ImagenInmueble> imagenesRestantes = imagenDAO.listarPorInmueble(idInmueble);

        if (!imagenesRestantes.isEmpty()) {
            ImagenInmueble nuevaPrincipal = imagenesRestantes.get(0);

            imagenDAO.marcarComoPrincipal(
                    nuevaPrincipal.getIdImagen(),
                    idInmueble
            );
        }
    }

    @FXML
    private void volver() {
        try {
            FXMLLoader loader = Navegacion.cargarVista("/fxml/inmuebles.fxml");

            Stage stage = (Stage) tablaImagenes
                    .getScene()
                    .getWindow();

            Navegacion.cambiarEscena(
                    stage,
                    loader,
                    "Rentify - Gestión de Inmuebles"
            );

        } catch (IOException e) {
            lblMensaje.setText("No se pudo volver.");
            e.printStackTrace();
        }
    }

    private void limpiarFormulario() {
        archivoSeleccionado = null;
        lblArchivoSeleccionado.setText("Ningún archivo seleccionado");
        imgPreview.setImage(null);
        txtDescripcion.clear();
        txtOrdenVisualizacion.clear();
        chkEsPrincipal.setSelected(false);
    }
}