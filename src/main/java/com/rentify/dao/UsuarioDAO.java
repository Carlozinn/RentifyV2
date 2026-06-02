package com.rentify.dao;

import com.rentify.database.DatabaseConnection;
import com.rentify.database.MultiDatabaseExecutor;
import com.rentify.model.Usuario;
import com.rentify.model.UsuarioTabla;
import com.rentify.util.PasswordUtil;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

public class UsuarioDAO {

    public boolean insertarUsuario(Usuario usuario) {
        String sql = """
            INSERT INTO usuario (
                nombre,
                apellido_paterno,
                apellido_materno,
                username,
                password_hash,
                fecha_nacimiento,
                id_rol,
                id_estado_usuario
            )
            VALUES (?, ?, ?, ?, ?, ?, ?, ?)
            """;

        String passwordHash = PasswordUtil.hashPassword(usuario.getPasswordHash());

        try {
            return MultiDatabaseExecutor.ejecutarActualizacion(sql, statement -> {
                statement.setString(1, usuario.getNombre());
                statement.setString(2, usuario.getApellidoPaterno());
                statement.setString(3, usuario.getApellidoMaterno());
                statement.setString(4, usuario.getUsername());
                statement.setString(5, passwordHash);

                if (usuario.getFechaNacimiento() != null) {
                    statement.setDate(6, Date.valueOf(usuario.getFechaNacimiento()));
                } else {
                    statement.setNull(6, Types.DATE);
                }

                statement.setInt(7, usuario.getIdRol());
                statement.setInt(8, usuario.getIdEstadoUsuario());
            });

        } catch (SQLException e) {
            System.out.println("Error al insertar usuario en las bases sincronizadas: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public Usuario autenticarUsuario(String username, String passwordPlano) {
        String sql = """
            SELECT id_usuario, nombre, apellido_paterno, apellido_materno,
                   username, password_hash, fecha_nacimiento, fecha_registro,
                   id_rol, id_estado_usuario
            FROM usuario
            WHERE username = ?
            """;

        try (Connection conexion = DatabaseConnection.getConnection();
             PreparedStatement statement = conexion.prepareStatement(sql)) {

            statement.setString(1, username);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    String passwordHashGuardado = resultSet.getString("password_hash");

                    if (!PasswordUtil.verificarPassword(passwordPlano, passwordHashGuardado)) {
                        return null;
                    }

                    return mapearUsuario(resultSet);
                }
            }

        } catch (SQLException e) {
            System.out.println("Error al autenticar usuario: " + e.getMessage());
            e.printStackTrace();
        }

        return null;
    }

    public Usuario buscarPorId(int idUsuario) {
        String sql = """
            SELECT id_usuario, nombre, apellido_paterno, apellido_materno,
                   username, password_hash, fecha_nacimiento, fecha_registro,
                   id_rol, id_estado_usuario
            FROM usuario
            WHERE id_usuario = ?
            """;

        try (Connection conexion = DatabaseConnection.getConnection();
             PreparedStatement statement = conexion.prepareStatement(sql)) {

            statement.setInt(1, idUsuario);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return mapearUsuario(resultSet);
                }
            }

        } catch (SQLException e) {
            System.out.println("Error al buscar usuario por id: " + e.getMessage());
            e.printStackTrace();
        }

        return null;
    }

    public boolean actualizarUsuario(Usuario usuario) {
        String sql = """
            UPDATE usuario
            SET nombre = ?,
                apellido_paterno = ?,
                apellido_materno = ?,
                username = ?,
                password_hash = ?,
                fecha_nacimiento = ?,
                id_rol = ?,
                id_estado_usuario = ?
            WHERE id_usuario = ?
            """;

        String passwordHash = PasswordUtil.hashPassword(usuario.getPasswordHash());

        try {
            return MultiDatabaseExecutor.ejecutarActualizacion(sql, statement -> {
                statement.setString(1, usuario.getNombre());
                statement.setString(2, usuario.getApellidoPaterno());
                statement.setString(3, usuario.getApellidoMaterno());
                statement.setString(4, usuario.getUsername());
                statement.setString(5, passwordHash);

                if (usuario.getFechaNacimiento() != null) {
                    statement.setDate(6, Date.valueOf(usuario.getFechaNacimiento()));
                } else {
                    statement.setNull(6, Types.DATE);
                }

                statement.setInt(7, usuario.getIdRol());
                statement.setInt(8, usuario.getIdEstadoUsuario());
                statement.setInt(9, usuario.getIdUsuario());
            });

        } catch (SQLException e) {
            System.out.println("Error al actualizar usuario en las bases sincronizadas: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public boolean actualizarEstadoUsuario(int idUsuario, int idEstadoUsuario) {
        String sql = """
            UPDATE usuario
            SET id_estado_usuario = ?
            WHERE id_usuario = ?
            """;

        try {
            return MultiDatabaseExecutor.ejecutarActualizacion(sql, statement -> {
                statement.setInt(1, idEstadoUsuario);
                statement.setInt(2, idUsuario);
            });

        } catch (SQLException e) {
            System.out.println("Error al actualizar estado del usuario en las bases sincronizadas: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public List<Usuario> listarUsuarios() {
        List<Usuario> usuarios = new ArrayList<>();

        String sql = """
                SELECT id_usuario, nombre, apellido_paterno, apellido_materno,
                       username, password_hash, fecha_nacimiento, fecha_registro,
                       id_rol, id_estado_usuario
                FROM usuario
                ORDER BY id_usuario
                """;

        try (Connection conexion = DatabaseConnection.getConnection();
             PreparedStatement statement = conexion.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                Usuario usuario = mapearUsuario(resultSet);
                usuarios.add(usuario);
            }

        } catch (SQLException e) {
            System.out.println("Error al listar usuarios: " + e.getMessage());
            e.printStackTrace();
        }

        return usuarios;
    }

    public Usuario buscarPorUsername(String username) {
        String sql = """
                SELECT id_usuario, nombre, apellido_paterno, apellido_materno,
                       username, password_hash, fecha_nacimiento, fecha_registro,
                       id_rol, id_estado_usuario
                FROM usuario
                WHERE username = ?
                """;

        try (Connection conexion = DatabaseConnection.getConnection();
             PreparedStatement statement = conexion.prepareStatement(sql)) {

            statement.setString(1, username);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return mapearUsuario(resultSet);
                }
            }

        } catch (SQLException e) {
            System.out.println("Error al buscar usuario por username: " + e.getMessage());
            e.printStackTrace();
        }

        return null;
    }

    public List<UsuarioTabla> listarUsuariosTabla() {
        List<UsuarioTabla> usuarios = new ArrayList<>();

        String sql = """
                SELECT u.id_usuario,
                       u.nombre,
                       u.apellido_paterno,
                       u.apellido_materno,
                       u.username,
                       r.nombre_rol,
                       eu.nombre_estado
                FROM usuario u
                INNER JOIN rol r
                    ON u.id_rol = r.id_rol
                INNER JOIN estado_usuario eu
                    ON u.id_estado_usuario = eu.id_estado_usuario
                ORDER BY u.id_usuario
                """;

        try (Connection conexion = DatabaseConnection.getConnection();
             PreparedStatement statement = conexion.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                UsuarioTabla usuarioTabla = new UsuarioTabla(
                        resultSet.getInt("id_usuario"),
                        resultSet.getString("nombre"),
                        resultSet.getString("apellido_paterno"),
                        resultSet.getString("apellido_materno"),
                        resultSet.getString("username"),
                        resultSet.getString("nombre_rol"),
                        resultSet.getString("nombre_estado")
                );

                usuarios.add(usuarioTabla);
            }

        } catch (SQLException e) {
            System.out.println("Error al listar usuarios para tabla: " + e.getMessage());
            e.printStackTrace();
        }

        return usuarios;
    }

    private Usuario mapearUsuario(ResultSet resultSet) throws SQLException {
        Usuario usuario = new Usuario();

        usuario.setIdUsuario(resultSet.getInt("id_usuario"));
        usuario.setNombre(resultSet.getString("nombre"));
        usuario.setApellidoPaterno(resultSet.getString("apellido_paterno"));
        usuario.setApellidoMaterno(resultSet.getString("apellido_materno"));
        usuario.setUsername(resultSet.getString("username"));
        usuario.setPasswordHash(resultSet.getString("password_hash"));

        Date fechaNacimientoSql = resultSet.getDate("fecha_nacimiento");
        if (fechaNacimientoSql != null) {
            usuario.setFechaNacimiento(fechaNacimientoSql.toLocalDate());
        }

        Timestamp fechaRegistroSql = resultSet.getTimestamp("fecha_registro");
        if (fechaRegistroSql != null) {
            usuario.setFechaRegistro(fechaRegistroSql.toLocalDateTime());
        }

        usuario.setIdRol(resultSet.getInt("id_rol"));
        usuario.setIdEstadoUsuario(resultSet.getInt("id_estado_usuario"));

        return usuario;
    }
}