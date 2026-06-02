package com.rentify.dao;

import com.rentify.database.DatabaseConnection;
import com.rentify.database.MultiDatabaseExecutor;
import com.rentify.model.Contacto;
import com.rentify.model.ContactoTabla;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ContactoDAO {

    public List<ContactoTabla> listarContactosPorUsuario(int idUsuario) {
        List<ContactoTabla> contactos = new ArrayList<>();

        String sql = """
                SELECT c.id_contacto,
                       tc.nombre_tipo,
                       c.valor_contacto,
                       c.principal,
                       c.verificado
                FROM contacto c
                INNER JOIN tipo_contacto tc
                    ON c.id_tipo_contacto = tc.id_tipo_contacto
                WHERE c.id_usuario = ?
                ORDER BY c.principal DESC, c.id_contacto ASC
                """;

        try (Connection conexion = DatabaseConnection.getConnection();
             PreparedStatement statement = conexion.prepareStatement(sql)) {

            statement.setInt(1, idUsuario);

            try (ResultSet rs = statement.executeQuery()) {
                while (rs.next()) {
                    ContactoTabla contacto = new ContactoTabla(
                            rs.getInt("id_contacto"),
                            rs.getString("nombre_tipo"),
                            rs.getString("valor_contacto"),
                            rs.getBoolean("principal"),
                            rs.getBoolean("verificado")
                    );

                    contactos.add(contacto);
                }
            }

        } catch (SQLException e) {
            System.out.println("Error al listar contactos: " + e.getMessage());
            e.printStackTrace();
        }

        return contactos;
    }

    public boolean insertarContacto(Contacto contacto) {
        String sql = """
                INSERT INTO contacto (
                    valor_contacto,
                    principal,
                    verificado,
                    id_tipo_contacto,
                    id_usuario
                )
                VALUES (?, ?, ?, ?, ?)
                """;

        try {
            return MultiDatabaseExecutor.ejecutarActualizacion(sql, statement -> {
                statement.setString(1, contacto.getValorContacto());
                statement.setBoolean(2, contacto.isPrincipal());
                statement.setBoolean(3, contacto.isVerificado());
                statement.setInt(4, contacto.getIdTipoContacto());
                statement.setInt(5, contacto.getIdUsuario());
            });

        } catch (SQLException e) {
            System.out.println("Error al insertar contacto en las bases sincronizadas: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public int contarContactosPorUsuario(int idUsuario) {
        String sql = """
                SELECT COUNT(*) AS total
                FROM contacto
                WHERE id_usuario = ?
                """;

        try (Connection conexion = DatabaseConnection.getConnection();
             PreparedStatement statement = conexion.prepareStatement(sql)) {

            statement.setInt(1, idUsuario);

            try (ResultSet rs = statement.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("total");
                }
            }

        } catch (SQLException e) {
            System.out.println("Error al contar contactos: " + e.getMessage());
            e.printStackTrace();
        }

        return 0;
    }

    public boolean marcarComoPrincipal(int idContacto, int idUsuario) {
        String sqlQuitarPrincipal = """
                UPDATE contacto
                SET principal = FALSE
                WHERE id_usuario = ?
                """;

        String sqlMarcarPrincipal = """
                UPDATE contacto
                SET principal = TRUE
                WHERE id_contacto = ?
                AND id_usuario = ?
                """;

        try {
            return MultiDatabaseExecutor.ejecutarTransaccion(conexion -> {
                try (PreparedStatement quitarPrincipal = conexion.prepareStatement(sqlQuitarPrincipal);
                     PreparedStatement marcarPrincipal = conexion.prepareStatement(sqlMarcarPrincipal)) {

                    quitarPrincipal.setInt(1, idUsuario);
                    quitarPrincipal.executeUpdate();

                    marcarPrincipal.setInt(1, idContacto);
                    marcarPrincipal.setInt(2, idUsuario);

                    int filasActualizadas = marcarPrincipal.executeUpdate();

                    if (filasActualizadas == 0) {
                        throw new SQLException("No se encontró el contacto para marcarlo como principal.");
                    }
                }
            });

        } catch (SQLException e) {
            System.out.println("Error al marcar contacto principal en las bases sincronizadas: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public boolean eliminarContacto(int idContacto, int idUsuario) {
        String sql = """
                DELETE FROM contacto
                WHERE id_contacto = ?
                AND id_usuario = ?
                """;

        try {
            return MultiDatabaseExecutor.ejecutarActualizacion(sql, statement -> {
                statement.setInt(1, idContacto);
                statement.setInt(2, idUsuario);
            });

        } catch (SQLException e) {
            System.out.println("Error al eliminar contacto en las bases sincronizadas: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public boolean existeContactoPorUsuarioYValor(int idUsuario, String valorContacto) {
        String sql = """
                SELECT 1
                FROM contacto
                WHERE id_usuario = ?
                AND LOWER(valor_contacto) = LOWER(?)
                """;

        try (Connection conexion = DatabaseConnection.getConnection();
             PreparedStatement statement = conexion.prepareStatement(sql)) {

            statement.setInt(1, idUsuario);
            statement.setString(2, valorContacto);

            try (ResultSet rs = statement.executeQuery()) {
                return rs.next();
            }

        } catch (SQLException e) {
            System.out.println("Error al validar contacto duplicado: " + e.getMessage());
            e.printStackTrace();
            return true;
        }
    }
}