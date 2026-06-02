package com.rentify.controller;

import com.rentify.dao.ImagenInmuebleDAO;
import com.rentify.model.ImagenInmueble;
import com.rentify.model.Inmueble;
import com.rentify.util.Navegacion;
import com.rentify.util.MonedaUtil;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;

public class InmuebleDetalleController {

    @FXML
    private Label lblTitulo;

    @FXML
    private Label lblDescripcion;

    @FXML
    private Label lblTipo;

    @FXML
    private Label lblEstado;

    @FXML
    private Label lblPrecioRenta;

    @FXML
    private Label lblDireccion;

    @FXML
    private Label lblCiudad;

    @FXML
    private Label lblEstadoProvincia;

    @FXML
    private Label lblCodigoPostal;

    @FXML
    private Label lblSuperficie;

    @FXML
    private Label lblHabitaciones;

    @FXML
    private Label lblBanos;

    @FXML
    private Label lblEstacionamientos;

    @FXML
    private Label lblMascotas;

    @FXML
    private ImageView imgPrincipal;

    @FXML
    private Label lblMensaje;

    private final ImagenInmuebleDAO imagenDAO = new ImagenInmuebleDAO();

    private String vistaOrigen = "inmuebles";

    public void setVistaOrigen(String vistaOrigen) {
        this.vistaOrigen = vistaOrigen;
    }

    public void cargarDatos(Inmueble inmueble) {
        if (inmueble == null) {
            lblMensaje.setText("No se recibió información del inmueble.");
            return;
        }

        lblTitulo.setText(valorTexto(inmueble.getTitulo()));
        lblDescripcion.setText(valorTexto(inmueble.getDescripcion()));

        lblTipo.setText(convertirTipoInmueble(inmueble.getIdTipoInmueble()));
        lblEstado.setText(convertirEstadoInmueble(inmueble.getIdEstadoInmueble()));

        lblPrecioRenta.setText(formatoMoneda(inmueble.getPrecioRenta()));
        lblDireccion.setText(construirDireccion(inmueble));

        lblCiudad.setText(valorTexto(inmueble.getCiudad()));
        lblEstadoProvincia.setText(valorTexto(inmueble.getEstadoProvincia()));
        lblCodigoPostal.setText(valorTexto(inmueble.getCodigoPostal()));

        lblSuperficie.setText(formatoDecimal(inmueble.getSuperficieM2(), " m²"));
        lblHabitaciones.setText(formatoEntero(inmueble.getHabitaciones()));
        lblBanos.setText(formatoDecimal(inmueble.getBanos(), ""));
        lblEstacionamientos.setText(formatoEntero(inmueble.getEstacionamientos()));

        lblMascotas.setText(
                inmueble.isMascotasPermitidas()
                        ? "Permitidas"
                        : "No permitidas"
        );

        cargarImagenPrincipal(inmueble.getIdInmueble());
    }

    private void cargarImagenPrincipal(int idInmueble) {
        try {
            ImagenInmueble imagen = imagenDAO.obtenerImagenPrincipal(idInmueble);

            if (imagen == null || imagen.getUrlImagen() == null || imagen.getUrlImagen().isBlank()) {
                imgPrincipal.setImage(null);
                lblMensaje.setText("Este inmueble todavía no tiene imagen principal.");
                return;
            }

            File archivoImagen = new File(imagen.getUrlImagen());

            if (!archivoImagen.exists()) {
                imgPrincipal.setImage(null);
                lblMensaje.setText("La imagen registrada no se encontró en la carpeta del proyecto.");
                return;
            }

            Image image = new Image(archivoImagen.toURI().toString());
            imgPrincipal.setImage(image);
            lblMensaje.setText("");

        } catch (Exception e) {
            lblMensaje.setText("Error al cargar la imagen del inmueble.");
            e.printStackTrace();
        }
    }

    private String construirDireccion(Inmueble inmueble) {
        StringBuilder direccion = new StringBuilder();

        if (inmueble.getCalle() != null && !inmueble.getCalle().isBlank()) {
            direccion.append(inmueble.getCalle());
        }

        if (inmueble.getNumeroExterior() != null && !inmueble.getNumeroExterior().isBlank()) {
            direccion.append(" #").append(inmueble.getNumeroExterior());
        }

        if (inmueble.getNumeroInterior() != null && !inmueble.getNumeroInterior().isBlank()) {
            direccion.append(" Int. ").append(inmueble.getNumeroInterior());
        }

        if (inmueble.getColonia() != null && !inmueble.getColonia().isBlank()) {
            direccion.append(", Col. ").append(inmueble.getColonia());
        }

        if (direccion.isEmpty()) {
            return "No especificada";
        }

        return direccion.toString();
    }

    @FXML
    private void volver() {
        try {
            String rutaFXML;
            String tituloVentana;

            if ("explorar".equalsIgnoreCase(vistaOrigen)) {
                rutaFXML = "/fxml/explorar_inmuebles.fxml";
                tituloVentana = "Rentify - Explorar Inmuebles";
            } else {
                rutaFXML = "/fxml/inmuebles.fxml";
                tituloVentana = "Rentify - Gestión de Inmuebles";
            }

            FXMLLoader loader = Navegacion.cargarVista(rutaFXML);

            Stage stage = (Stage) lblTitulo
                    .getScene()
                    .getWindow();

            Navegacion.cambiarEscena(stage, loader, tituloVentana);

        } catch (IOException e) {
            lblMensaje.setText("No se pudo volver a la pantalla anterior.");
            e.printStackTrace();
        }
    }

    private String valorTexto(String valor) {
        if (valor == null || valor.isBlank()) {
            return "No especificado";
        }

        return valor;
    }

    private String formatoMoneda(BigDecimal valor) {
        if (valor == null) {
            return "No especificado";
        }

        return MonedaUtil.formatear(valor);
    }

    private String formatoDecimal(BigDecimal valor, String sufijo) {
        if (valor == null) {
            return "No especificado";
        }

        return valor + sufijo;
    }

    private String formatoEntero(Integer valor) {
        if (valor == null) {
            return "No especificado";
        }

        return String.valueOf(valor);
    }

    private String convertirTipoInmueble(int idTipoInmueble) {
        return switch (idTipoInmueble) {
            case 1 -> "Casa";
            case 2 -> "Departamento";
            case 3 -> "Local";
            default -> "No especificado";
        };
    }

    private String convertirEstadoInmueble(int idEstadoInmueble) {
        return switch (idEstadoInmueble) {
            case 1 -> "Disponible";
            case 2 -> "Ocupado";
            case 3 -> "No disponible";
            default -> "No especificado";
        };
    }
}