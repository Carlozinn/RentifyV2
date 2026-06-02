package com.rentify.controller;

import com.rentify.dao.InmuebleDAO;
import com.rentify.model.Inmueble;
import com.rentify.util.MonedaUtil;
import com.rentify.util.Navegacion;
import com.rentify.util.Sesion;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.stage.Stage;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.function.UnaryOperator;

public class InmuebleFormController {

    @FXML
    private TextField txtTitulo;

    @FXML
    private TextArea txtDescripcion;

    @FXML
    private TextField txtCalle;

    @FXML
    private TextField txtNumeroExterior;

    @FXML
    private TextField txtNumeroInterior;

    @FXML
    private TextField txtColonia;

    @FXML
    private TextField txtCiudad;

    @FXML
    private ComboBox<String> cbEstadoProvincia;

    @FXML
    private TextField txtCodigoPostal;

    @FXML
    private TextField txtPrecioRenta;

    @FXML
    private TextField txtSuperficie;

    @FXML
    private TextField txtHabitaciones;

    @FXML
    private TextField txtBanos;

    @FXML
    private TextField txtEstacionamientos;

    @FXML
    private ComboBox<String> cbTipoInmueble;

    @FXML
    private CheckBox chkMascotas;

    @FXML
    private Label lblMensaje;

    private final InmuebleDAO inmuebleDAO;

    private Inmueble inmuebleEditar;
    private boolean modoEdicion;

    public InmuebleFormController() {
        this.inmuebleDAO = new InmuebleDAO();
        this.modoEdicion = false;
    }

    @FXML
    public void initialize() {
        cargarCatalogos();
        aplicarFiltrosEntrada();

        cbTipoInmueble.valueProperty().addListener(
                (obs, anterior, nuevo) -> configurarHabitacionesSegunTipo()
        );

        txtPrecioRenta.focusedProperty().addListener((obs, estabaEnfocado, estaEnfocado) -> {
            if (!estaEnfocado) {
                formatearCampoMoneda(txtPrecioRenta);
            }
        });
    }

    public void setModoNuevo() {
        this.modoEdicion = false;
        this.inmuebleEditar = null;
    }

    public void setModoEdicion(Inmueble inmueble) {
        this.modoEdicion = true;
        this.inmuebleEditar = inmueble;

        txtTitulo.setText(valorTexto(inmueble.getTitulo()));
        txtDescripcion.setText(valorTexto(inmueble.getDescripcion()));
        txtCalle.setText(valorTexto(inmueble.getCalle()));
        txtNumeroExterior.setText(valorTexto(inmueble.getNumeroExterior()));
        txtNumeroInterior.setText(valorTexto(inmueble.getNumeroInterior()));
        txtColonia.setText(valorTexto(inmueble.getColonia()));
        txtCiudad.setText(valorTexto(inmueble.getCiudad()));
        cbEstadoProvincia.setValue(inmueble.getEstadoProvincia());
        txtCodigoPostal.setText(valorTexto(inmueble.getCodigoPostal()));

        txtPrecioRenta.setText(
                inmueble.getPrecioRenta() != null
                        ? MonedaUtil.formatear(inmueble.getPrecioRenta())
                        : ""
        );

        txtSuperficie.setText(
                inmueble.getSuperficieM2() != null
                        ? inmueble.getSuperficieM2().toString()
                        : ""
        );

        txtHabitaciones.setText(
                inmueble.getHabitaciones() != null
                        ? inmueble.getHabitaciones().toString()
                        : ""
        );

        txtBanos.setText(
                inmueble.getBanos() != null
                        ? inmueble.getBanos().toString()
                        : ""
        );

        txtEstacionamientos.setText(
                inmueble.getEstacionamientos() != null
                        ? inmueble.getEstacionamientos().toString()
                        : ""
        );

        chkMascotas.setSelected(inmueble.isMascotasPermitidas());
        cbTipoInmueble.setValue(convertirIdATipo(inmueble.getIdTipoInmueble()));

        configurarHabitacionesSegunTipo();
    }

    @FXML
    private void guardarInmueble() {
        if (Sesion.getUsuarioActual() == null) {
            lblMensaje.setText("No hay sesión activa.");
            return;
        }

        String titulo = obtenerTexto(txtTitulo);
        String descripcion = obtenerTexto(txtDescripcion);
        String calle = obtenerTexto(txtCalle);
        String numeroExterior = obtenerTexto(txtNumeroExterior);
        String numeroInterior = obtenerTexto(txtNumeroInterior);
        String colonia = obtenerTexto(txtColonia);
        String ciudad = obtenerTexto(txtCiudad);
        String estadoProvincia = cbEstadoProvincia.getValue();
        String codigoPostal = obtenerTexto(txtCodigoPostal);
        String precioRentaTexto = obtenerTexto(txtPrecioRenta);
        String superficieTexto = obtenerTexto(txtSuperficie);
        String banosTexto = obtenerTexto(txtBanos);
        String estacionamientosTexto = obtenerTexto(txtEstacionamientos);
        String tipoSeleccionado = cbTipoInmueble.getValue();

        if (!validarTextoConLetras("Título", titulo)) {
            return;
        }

        if (!validarTextoConLetras("Descripción", descripcion)) {
            return;
        }

        if (!validarTextoConLetras("Calle", calle)) {
            return;
        }

        if (!validarNumeroDomicilio("Número exterior", numeroExterior)) {
            return;
        }

        if (!numeroInterior.isEmpty() && !validarNumeroInterior(numeroInterior)) {
            return;
        }

        if (!validarTextoConLetras("Colonia", colonia)) {
            return;
        }

        if (!validarTextoConLetras("Ciudad", ciudad)) {
            return;
        }

        if (estadoProvincia == null || estadoProvincia.isBlank()) {
            lblMensaje.setText("Selecciona un Estado/Provincia.");
            return;
        }

        if (!validarCodigoPostal(codigoPostal)) {
            return;
        }

        if (precioRentaTexto.isEmpty()) {
            lblMensaje.setText("El precio de renta es obligatorio.");
            return;
        }

        if (superficieTexto.isEmpty()) {
            lblMensaje.setText("La superficie es obligatoria.");
            return;
        }

        if (banosTexto.isEmpty()) {
            lblMensaje.setText("El número de baños es obligatorio.");
            return;
        }

        if (estacionamientosTexto.isEmpty()) {
            lblMensaje.setText("El número de estacionamientos es obligatorio.");
            return;
        }

        if (tipoSeleccionado == null || tipoSeleccionado.isBlank()) {
            lblMensaje.setText("Selecciona un tipo de inmueble.");
            return;
        }

        try {
            BigDecimal precioRenta = MonedaUtil.parsear(precioRentaTexto);

            if (precioRenta.compareTo(BigDecimal.ZERO) <= 0) {
                lblMensaje.setText("El precio de renta debe ser mayor a 0.");
                return;
            }

            BigDecimal superficie = new BigDecimal(superficieTexto);

            if (superficie.compareTo(BigDecimal.ZERO) <= 0) {
                lblMensaje.setText("La superficie debe ser mayor a 0.");
                return;
            }

            if (!esFormatoBanosValido(banosTexto)) {
                lblMensaje.setText("Los baños solo pueden ser enteros o medios baños. Ejemplo: 1, 1.5, 2, 2.5.");
                return;
            }

            BigDecimal banos = new BigDecimal(normalizarMedio(banosTexto));

            if (banos.compareTo(BigDecimal.ZERO) <= 0) {
                lblMensaje.setText("Los baños deben ser mayores a 0.");
                return;
            }

            Integer habitaciones = obtenerHabitacionesValidadas(tipoSeleccionado);

            if (habitaciones == null && !"Local".equals(tipoSeleccionado)) {
                return;
            }

            Integer estacionamientos = Integer.parseInt(estacionamientosTexto);

            if (estacionamientos < 0) {
                lblMensaje.setText("Los estacionamientos no pueden ser negativos.");
                return;
            }

            Inmueble inmueble = new Inmueble();

            if (modoEdicion) {
                inmueble.setIdInmueble(inmuebleEditar.getIdInmueble());
                inmueble.setIdEstadoInmueble(inmuebleEditar.getIdEstadoInmueble());
            } else {
                inmueble.setIdEstadoInmueble(1);
            }

            inmueble.setTitulo(titulo);
            inmueble.setDescripcion(descripcion);
            inmueble.setCalle(calle);
            inmueble.setNumeroExterior(numeroExterior);
            inmueble.setNumeroInterior(numeroInterior.isEmpty() ? null : numeroInterior);
            inmueble.setColonia(colonia);
            inmueble.setCiudad(ciudad);
            inmueble.setEstadoProvincia(estadoProvincia);
            inmueble.setCodigoPostal(codigoPostal);
            inmueble.setPrecioRenta(precioRenta);
            inmueble.setSuperficieM2(superficie);
            inmueble.setHabitaciones(habitaciones);
            inmueble.setBanos(banos);
            inmueble.setEstacionamientos(estacionamientos);
            inmueble.setMascotasPermitidas(chkMascotas.isSelected());
            inmueble.setIdUsuarioArrendador(Sesion.getUsuarioActual().getIdUsuario());
            inmueble.setIdTipoInmueble(convertirTipoAId(tipoSeleccionado));

            boolean exito = modoEdicion
                    ? inmuebleDAO.actualizarInmueble(inmueble)
                    : inmuebleDAO.insertarInmueble(inmueble);

            if (exito) {
                volverAGestionInmuebles();
            } else {
                lblMensaje.setText("No se pudo guardar el inmueble.");
            }

        } catch (NumberFormatException e) {
            lblMensaje.setText("Verifica los campos numéricos.");
        }
    }

    @FXML
    private void cancelar() {
        volverAGestionInmuebles();
    }

    private void cargarCatalogos() {
        cbTipoInmueble.getItems().setAll("Casa", "Departamento", "Local");

        cbEstadoProvincia.getItems().setAll(
                "Aguascalientes",
                "Baja California",
                "Baja California Sur",
                "Campeche",
                "Chiapas",
                "Chihuahua",
                "Ciudad de México",
                "Coahuila",
                "Colima",
                "Durango",
                "Estado de México",
                "Guanajuato",
                "Guerrero",
                "Hidalgo",
                "Jalisco",
                "Michoacán",
                "Morelos",
                "Nayarit",
                "Nuevo León",
                "Oaxaca",
                "Puebla",
                "Querétaro",
                "Quintana Roo",
                "San Luis Potosí",
                "Sinaloa",
                "Sonora",
                "Tabasco",
                "Tamaulipas",
                "Tlaxcala",
                "Veracruz",
                "Yucatán",
                "Zacatecas"
        );
    }

    private void aplicarFiltrosEntrada() {
        aplicarFiltroMoneda(txtPrecioRenta);
        aplicarFiltroDecimal(txtSuperficie);
        aplicarFiltroBanos(txtBanos);

        aplicarFiltroEntero(txtHabitaciones);
        aplicarFiltroEntero(txtEstacionamientos);
        aplicarFiltroEntero(txtCodigoPostal);
    }

    private void aplicarFiltroMoneda(TextField campo) {
        UnaryOperator<TextFormatter.Change> filtro = cambio -> {
            String nuevoTexto = cambio.getControlNewText();

            if (nuevoTexto.matches("[0-9$,.]*")) {
                return cambio;
            }

            return null;
        };

        campo.setTextFormatter(new TextFormatter<>(filtro));
    }

    private void aplicarFiltroDecimal(TextField campo) {
        UnaryOperator<TextFormatter.Change> filtro = cambio -> {
            String nuevoTexto = cambio.getControlNewText();

            if (nuevoTexto.matches("\\d*(\\.\\d*)?")) {
                return cambio;
            }

            return null;
        };

        campo.setTextFormatter(new TextFormatter<>(filtro));
    }

    private void aplicarFiltroBanos(TextField campo) {
        UnaryOperator<TextFormatter.Change> filtro = cambio -> {
            String nuevoTexto = cambio.getControlNewText();

            if (nuevoTexto.matches("\\d*(\\.5?)?")) {
                return cambio;
            }

            return null;
        };

        campo.setTextFormatter(new TextFormatter<>(filtro));
    }

    private void aplicarFiltroEntero(TextField campo) {
        UnaryOperator<TextFormatter.Change> filtro = cambio -> {
            String nuevoTexto = cambio.getControlNewText();

            if (nuevoTexto.matches("\\d*")) {
                return cambio;
            }

            return null;
        };

        campo.setTextFormatter(new TextFormatter<>(filtro));
    }

    private boolean validarTextoConLetras(String nombreCampo, String valor) {
        if (valor.isEmpty()) {
            lblMensaje.setText(nombreCampo + " es obligatorio.");
            return false;
        }

        if (!contieneLetra(valor)) {
            lblMensaje.setText(nombreCampo + " debe contener texto válido, no solo números.");
            return false;
        }

        return true;
    }

    private boolean validarNumeroDomicilio(String nombreCampo, String valor) {
        if (valor.isEmpty()) {
            lblMensaje.setText(nombreCampo + " es obligatorio.");
            return false;
        }

        if (!valor.matches("[A-Za-zÁÉÍÓÚáéíóúÑñ0-9/\\-#\\s]+")) {
            lblMensaje.setText(nombreCampo + " contiene caracteres no válidos.");
            return false;
        }

        if (valor.matches("0+")) {
            lblMensaje.setText(nombreCampo + " no puede ser solo 0.");
            return false;
        }

        return true;
    }

    private boolean validarNumeroInterior(String valor) {
        if ("S/N".equalsIgnoreCase(valor)) {
            return true;
        }

        if (!valor.matches("[A-Za-zÁÉÍÓÚáéíóúÑñ0-9/\\-#\\s]+")) {
            lblMensaje.setText("Número interior contiene caracteres no válidos.");
            return false;
        }

        if (valor.matches("0+")) {
            lblMensaje.setText("Número interior no puede ser solo 0. Si no tiene, escribe S/N o déjalo vacío.");
            return false;
        }

        return true;
    }

    private boolean validarCodigoPostal(String codigoPostal) {
        if (codigoPostal.isEmpty()) {
            lblMensaje.setText("El código postal es obligatorio.");
            return false;
        }

        if (!codigoPostal.matches("\\d{5}")) {
            lblMensaje.setText("El código postal debe tener exactamente 5 dígitos.");
            return false;
        }

        return true;
    }

    private Integer obtenerHabitacionesValidadas(String tipoSeleccionado) {
        if ("Local".equals(tipoSeleccionado)) {
            return null;
        }

        String texto = obtenerTexto(txtHabitaciones);

        if (texto.isEmpty()) {
            lblMensaje.setText("Las habitaciones son obligatorias para casa o departamento.");
            return null;
        }

        int habitaciones = Integer.parseInt(texto);

        if (habitaciones <= 0) {
            lblMensaje.setText("Las habitaciones deben ser mayores a 0.");
            return null;
        }

        return habitaciones;
    }

    private void configurarHabitacionesSegunTipo() {
        if ("Local".equals(cbTipoInmueble.getValue())) {
            txtHabitaciones.clear();
            txtHabitaciones.setDisable(true);
            txtHabitaciones.setPromptText("No aplica para local");
        } else {
            txtHabitaciones.setDisable(false);
            txtHabitaciones.setPromptText("Ejemplo: 2");
        }
    }

    private void formatearCampoMoneda(TextField campo) {
        String texto = obtenerTexto(campo);

        if (texto.isEmpty()) {
            return;
        }

        try {
            campo.setText(MonedaUtil.formatear(MonedaUtil.parsear(texto)));
            lblMensaje.setText("");
        } catch (NumberFormatException e) {
            lblMensaje.setText("Verifica el formato del precio de renta.");
        }
    }

    private boolean contieneLetra(String texto) {
        return texto.matches(".*[A-Za-zÁÉÍÓÚáéíóúÑñ].*");
    }

    private boolean esFormatoBanosValido(String texto) {
        return texto.matches("\\d+(\\.5)?") || texto.equals(".5");
    }

    private String normalizarMedio(String texto) {
        if (texto.equals(".5")) {
            return "0.5";
        }

        return texto;
    }

    private String obtenerTexto(TextField campo) {
        return campo.getText() == null ? "" : campo.getText().trim();
    }

    private String obtenerTexto(TextArea campo) {
        return campo.getText() == null ? "" : campo.getText().trim();
    }

    private String valorTexto(String texto) {
        return texto == null ? "" : texto;
    }

    private int convertirTipoAId(String tipo) {
        return switch (tipo) {
            case "Casa" -> 1;
            case "Departamento" -> 2;
            case "Local" -> 3;
            default -> 0;
        };
    }

    private String convertirIdATipo(int idTipo) {
        return switch (idTipo) {
            case 1 -> "Casa";
            case 2 -> "Departamento";
            case 3 -> "Local";
            default -> "";
        };
    }

    private void volverAGestionInmuebles() {
        try {
            FXMLLoader loader = Navegacion.cargarVista("/fxml/inmuebles.fxml");
            Stage stage = (Stage) txtTitulo.getScene().getWindow();
            Navegacion.cambiarEscena(stage, loader, "Rentify - Gestión de Inmuebles");
        } catch (IOException e) {
            lblMensaje.setText("Error al volver a inmuebles.");
            e.printStackTrace();
        }
    }
}