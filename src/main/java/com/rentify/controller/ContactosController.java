package com.rentify.controller;

import com.rentify.dao.ContactoDAO;
import com.rentify.model.Contacto;
import com.rentify.model.ContactoTabla;
import com.rentify.util.Navegacion;
import com.rentify.util.Sesion;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import java.util.regex.Pattern;

import java.io.IOException;
import java.util.Optional;

public class ContactosController {

    @FXML
    private ComboBox<String> cmbTipoContacto;

    @FXML
    private TextField txtValorContacto;

    @FXML
    private CheckBox chkPrincipal;

    @FXML
    private TableView<ContactoTabla> tablaContactos;

    @FXML
    private TableColumn<ContactoTabla, Integer> colId;

    @FXML
    private TableColumn<ContactoTabla, String> colTipo;

    @FXML
    private TableColumn<ContactoTabla, String> colValor;

    @FXML
    private TableColumn<ContactoTabla, Boolean> colPrincipal;

    @FXML
    private TableColumn<ContactoTabla, Boolean> colVerificado;

    @FXML
    private Label lblMensaje;

    private final ContactoDAO contactoDAO;

    private static final Pattern PATRON_CORREO = Pattern.compile(
            "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$"
    );

    private static final Pattern PATRON_TELEFONO = Pattern.compile(
            "^[0-9]{10,15}$"
    );

    public ContactosController() {
        this.contactoDAO = new ContactoDAO();
    }

    @FXML
    public void initialize() {
        configurarColumnas();
        cargarTiposContacto();
        cargarContactos();
    }

    private void configurarColumnas() {
        colId.setCellValueFactory(new PropertyValueFactory<>("idContacto"));
        colTipo.setCellValueFactory(new PropertyValueFactory<>("tipoContacto"));
        colValor.setCellValueFactory(new PropertyValueFactory<>("valorContacto"));
        colPrincipal.setCellValueFactory(new PropertyValueFactory<>("principal"));
        colVerificado.setCellValueFactory(new PropertyValueFactory<>("verificado"));
    }

    private void cargarTiposContacto() {
        cmbTipoContacto.setItems(FXCollections.observableArrayList(
                "Correo",
                "Telefono",
                "WhatsApp"
        ));

        cmbTipoContacto.getSelectionModel().selectFirst();
    }

    @FXML
    private void cargarContactos() {
        if (Sesion.getUsuarioActual() == null) {
            lblMensaje.setText("No hay sesión activa.");
            return;
        }

        ObservableList<ContactoTabla> datos = FXCollections.observableArrayList(
                contactoDAO.listarContactosPorUsuario(
                        Sesion.getUsuarioActual().getIdUsuario()
                )
        );

        tablaContactos.setItems(datos);
        lblMensaje.setText("");
    }

    @FXML
    private void agregarContacto() {
        if (Sesion.getUsuarioActual() == null) {
            lblMensaje.setText("No hay sesión activa.");
            return;
        }

        String tipo = cmbTipoContacto.getValue();

        String valor = txtValorContacto.getText() == null
                ? ""
                : txtValorContacto.getText().trim();

        if (tipo == null || tipo.isBlank()) {
            lblMensaje.setText("Selecciona un tipo de contacto.");
            return;
        }

        if (valor.isEmpty()) {
            lblMensaje.setText("El contacto es obligatorio.");
            return;
        }

        if (!validarFormatoContacto(tipo, valor)) {
            return;
        }

        int idUsuario = Sesion.getUsuarioActual().getIdUsuario();

        if (contactoDAO.existeContactoPorUsuarioYValor(idUsuario, valor)) {
            lblMensaje.setText("Ese contacto ya está registrado.");
            return;
        }

        int idTipoContacto = convertirTipoContactoAId(tipo);

        if (idTipoContacto == 0) {
            lblMensaje.setText("Tipo de contacto inválido.");
            return;
        }

        boolean primerContacto = contactoDAO.contarContactosPorUsuario(idUsuario) == 0;
        boolean seraPrincipal = primerContacto || chkPrincipal.isSelected();

        Contacto contacto = new Contacto();
        contacto.setValorContacto(valor);
        contacto.setPrincipal(seraPrincipal);
        contacto.setVerificado(false);
        contacto.setIdTipoContacto(idTipoContacto);
        contacto.setIdUsuario(idUsuario);

        boolean insertado = contactoDAO.insertarContacto(contacto);

        if (insertado) {
            if (!primerContacto && chkPrincipal.isSelected()) {
                marcarPrincipalPorValor(valor);
            }

            limpiarFormulario();
            cargarContactos();

            if (primerContacto) {
                lblMensaje.setText("Contacto agregado correctamente. Se marcó como principal por ser el primero.");
            } else {
                lblMensaje.setText("Contacto agregado correctamente.");
            }

        } else {
            lblMensaje.setText("No se pudo agregar el contacto.");
        }
    }

    private boolean validarFormatoContacto(String tipo, String valor) {
        if ("Correo".equalsIgnoreCase(tipo)) {
            if (!PATRON_CORREO.matcher(valor).matches()) {
                lblMensaje.setText("Ingresa un correo válido. Ejemplo: usuario@dominio.com");
                return false;
            }

            return true;
        }

        if ("Telefono".equalsIgnoreCase(tipo) || "WhatsApp".equalsIgnoreCase(tipo)) {
            String valorNormalizado = valor.replaceAll("[\\s\\-()]", "");

            if (!PATRON_TELEFONO.matcher(valorNormalizado).matches()) {
                lblMensaje.setText("Ingresa un número válido de 10 a 15 dígitos.");
                return false;
            }

            txtValorContacto.setText(valorNormalizado);
            return true;
        }

        lblMensaje.setText("Tipo de contacto inválido.");
        return false;
    }
    private void marcarPrincipalPorValor(String valorContacto) {
        int idUsuario = Sesion.getUsuarioActual().getIdUsuario();

        for (ContactoTabla contacto : contactoDAO.listarContactosPorUsuario(idUsuario)) {
            if (contacto.getValorContacto().equalsIgnoreCase(valorContacto)) {
                contactoDAO.marcarComoPrincipal(
                        contacto.getIdContacto(),
                        idUsuario
                );
                return;
            }
        }
    }

    @FXML
    private void marcarPrincipal() {
        if (Sesion.getUsuarioActual() == null) {
            lblMensaje.setText("No hay sesión activa.");
            return;
        }

        ContactoTabla seleccionado = tablaContactos.getSelectionModel().getSelectedItem();

        if (seleccionado == null) {
            lblMensaje.setText("Selecciona un contacto.");
            return;
        }

        boolean actualizado = contactoDAO.marcarComoPrincipal(
                seleccionado.getIdContacto(),
                Sesion.getUsuarioActual().getIdUsuario()
        );

        if (actualizado) {
            cargarContactos();
            lblMensaje.setText("Contacto principal actualizado.");
        } else {
            lblMensaje.setText("No se pudo actualizar el contacto principal.");
        }
    }

    @FXML
    private void eliminarContacto() {
        if (Sesion.getUsuarioActual() == null) {
            lblMensaje.setText("No hay sesión activa.");
            return;
        }

        ContactoTabla seleccionado = tablaContactos.getSelectionModel().getSelectedItem();

        if (seleccionado == null) {
            lblMensaje.setText("Selecciona un contacto.");
            return;
        }

        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacion.setTitle("Confirmar eliminación");

        if (seleccionado.isPrincipal()) {
            confirmacion.setHeaderText("Estás eliminando tu contacto principal");
            confirmacion.setContentText(
                    "El contacto seleccionado está marcado como principal:\n\n" +
                            seleccionado.getTipoContacto() + ": " +
                            seleccionado.getValorContacto() +
                            "\n\nSi lo eliminas, otro contacto será marcado como principal automáticamente si existe.\n\n" +
                            "¿Realmente deseas eliminarlo?"
            );
        } else {
            confirmacion.setHeaderText("Eliminar contacto");
            confirmacion.setContentText(
                    "¿Seguro que deseas eliminar este contacto?\n\n" +
                            seleccionado.getTipoContacto() + ": " +
                            seleccionado.getValorContacto()
            );
        }

        Optional<ButtonType> resultado = confirmacion.showAndWait();

        if (resultado.isEmpty() || resultado.get() != ButtonType.OK) {
            return;
        }

        boolean eraPrincipal = seleccionado.isPrincipal();

        boolean eliminado = contactoDAO.eliminarContacto(
                seleccionado.getIdContacto(),
                Sesion.getUsuarioActual().getIdUsuario()
        );

        if (eliminado) {
            if (eraPrincipal) {
                asignarNuevoPrincipalSiExiste();
            }

            cargarContactos();
            lblMensaje.setText("Contacto eliminado correctamente.");

        } else {
            lblMensaje.setText("No se pudo eliminar el contacto.");
        }
    }
    private void asignarNuevoPrincipalSiExiste() {
        int idUsuario = Sesion.getUsuarioActual().getIdUsuario();

        var contactos = contactoDAO.listarContactosPorUsuario(idUsuario);

        if (!contactos.isEmpty()) {
            contactoDAO.marcarComoPrincipal(
                    contactos.get(0).getIdContacto(),
                    idUsuario
            );
        }
    }

    @FXML
    private void volver() {
        if (Sesion.getUsuarioActual() == null) {
            lblMensaje.setText("No hay sesión activa.");
            return;
        }

        try {
            FXMLLoader loader;
            String titulo;

            if (Sesion.getUsuarioActual().getIdRol() == 2) {
                loader = Navegacion.cargarVista("/fxml/arrendador.fxml");
                ArrendadorController controller = loader.getController();
                controller.setNombreUsuario(Sesion.getUsuarioActual().getNombre());
                titulo = "Rentify - Arrendador";

            } else if (Sesion.getUsuarioActual().getIdRol() == 3) {
                loader = Navegacion.cargarVista("/fxml/arrendatario.fxml");
                ArrendatarioController controller = loader.getController();
                controller.setNombreUsuario(Sesion.getUsuarioActual().getNombre());
                titulo = "Rentify - Arrendatario";

            } else {
                loader = Navegacion.cargarVista("/fxml/login.fxml");
                titulo = "Rentify - Login";
            }

            Stage stage = (Stage) tablaContactos
                    .getScene()
                    .getWindow();

            Navegacion.cambiarEscena(stage, loader, titulo);

        } catch (IOException e) {
            lblMensaje.setText("No se pudo volver.");
            e.printStackTrace();
        }
    }

    private int convertirTipoContactoAId(String tipo) {
        return switch (tipo) {
            case "Correo" -> 1;
            case "Telefono" -> 2;
            case "WhatsApp" -> 3;
            default -> 0;
        };
    }

    private void limpiarFormulario() {
        txtValorContacto.clear();
        chkPrincipal.setSelected(false);
        cmbTipoContacto.getSelectionModel().selectFirst();
    }
}