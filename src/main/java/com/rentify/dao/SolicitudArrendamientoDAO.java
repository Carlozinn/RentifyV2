package com.rentify.dao;

import com.rentify.database.DatabaseConnection;
import com.rentify.database.MultiDatabaseExecutor;
import com.rentify.model.SolicitudTabla;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class SolicitudArrendamientoDAO {

    public boolean insertarSolicitud(int idUsuarioArrendatario, int idInmueble, String mensaje) {
        String sql = """
                INSERT INTO solicitud_arrendamiento (
                    mensaje,
                    id_usuario_arrendatario,
                    id_inmueble,
                    id_estado_solicitud
                )
                VALUES (?, ?, ?, ?)
                """;

        try {
            return MultiDatabaseExecutor.ejecutarActualizacion(sql, statement -> {
                if (mensaje == null || mensaje.trim().isEmpty()) {
                    statement.setNull(1, Types.VARCHAR);
                } else {
                    statement.setString(1, mensaje.trim());
                }

                statement.setInt(2, idUsuarioArrendatario);
                statement.setInt(3, idInmueble);
                statement.setInt(4, 1);
            });

        } catch (SQLException e) {
            System.out.println("Error al insertar solicitud en las bases sincronizadas: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public List<SolicitudTabla> listarSolicitudesPorArrendador(int idUsuarioArrendador) {
        List<SolicitudTabla> solicitudes = new ArrayList<>();

        String sql = """
                SELECT s.id_solicitud,
                       i.titulo AS inmueble,
                       u.nombre || ' ' || u.apellido_paterno AS arrendatario,
                       s.mensaje,
                       s.fecha_solicitud,
                       es.nombre_estado
                FROM solicitud_arrendamiento s
                INNER JOIN inmueble i ON s.id_inmueble = i.id_inmueble
                INNER JOIN usuario u ON s.id_usuario_arrendatario = u.id_usuario
                INNER JOIN estado_solicitud es ON s.id_estado_solicitud = es.id_estado_solicitud
                WHERE i.id_usuario_arrendador = ?
                ORDER BY s.id_solicitud DESC
                """;

        try (Connection conexion = DatabaseConnection.getConnection();
             PreparedStatement statement = conexion.prepareStatement(sql)) {

            statement.setInt(1, idUsuarioArrendador);

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    SolicitudTabla solicitud = new SolicitudTabla(
                            resultSet.getInt("id_solicitud"),
                            resultSet.getString("inmueble"),
                            resultSet.getString("arrendatario"),
                            resultSet.getString("mensaje"),
                            resultSet.getTimestamp("fecha_solicitud").toString(),
                            resultSet.getString("nombre_estado")
                    );

                    solicitudes.add(solicitud);
                }
            }

        } catch (SQLException e) {
            System.out.println("Error al listar solicitudes del arrendador: " + e.getMessage());
            e.printStackTrace();
        }

        return solicitudes;
    }

    public boolean actualizarEstadoSolicitud(int idSolicitud, int idEstadoSolicitud) {
        String sql = """
                UPDATE solicitud_arrendamiento
                SET id_estado_solicitud = ?,
                    fecha_respuesta = CURRENT_TIMESTAMP
                WHERE id_solicitud = ?
                """;

        try {
            return MultiDatabaseExecutor.ejecutarActualizacion(sql, statement -> {
                statement.setInt(1, idEstadoSolicitud);
                statement.setInt(2, idSolicitud);
            });

        } catch (SQLException e) {
            System.out.println("Error al actualizar estado de solicitud en las bases sincronizadas: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public int obtenerIdInmueblePorSolicitud(int idSolicitud) {
        String sql = """
                SELECT id_inmueble
                FROM solicitud_arrendamiento
                WHERE id_solicitud = ?
                """;

        try (Connection conexion = DatabaseConnection.getConnection();
             PreparedStatement statement = conexion.prepareStatement(sql)) {

            statement.setInt(1, idSolicitud);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getInt("id_inmueble");
                }
            }

        } catch (SQLException e) {
            System.out.println("Error al obtener inmueble por solicitud: " + e.getMessage());
            e.printStackTrace();
        }

        return 0;
    }

    public int obtenerIdUsuarioArrendatarioPorSolicitud(int idSolicitud) {
        String sql = """
                SELECT id_usuario_arrendatario
                FROM solicitud_arrendamiento
                WHERE id_solicitud = ?
                """;

        try (Connection conexion = DatabaseConnection.getConnection();
             PreparedStatement statement = conexion.prepareStatement(sql)) {

            statement.setInt(1, idSolicitud);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getInt("id_usuario_arrendatario");
                }
            }

        } catch (SQLException e) {
            System.out.println("Error al obtener arrendatario por solicitud: " + e.getMessage());
            e.printStackTrace();
        }

        return 0;
    }

    public List<SolicitudTabla> listarSolicitudesPorArrendatario(int idUsuarioArrendatario) {
        List<SolicitudTabla> solicitudes = new ArrayList<>();

        String sql = """
                SELECT s.id_solicitud,
                       i.titulo AS inmueble,
                       u.nombre || ' ' || u.apellido_paterno AS arrendatario,
                       s.mensaje,
                       s.fecha_solicitud,
                       es.nombre_estado
                FROM solicitud_arrendamiento s
                INNER JOIN inmueble i ON s.id_inmueble = i.id_inmueble
                INNER JOIN usuario u ON s.id_usuario_arrendatario = u.id_usuario
                INNER JOIN estado_solicitud es ON s.id_estado_solicitud = es.id_estado_solicitud
                WHERE s.id_usuario_arrendatario = ?
                ORDER BY s.id_solicitud DESC
                """;

        try (Connection conexion = DatabaseConnection.getConnection();
             PreparedStatement statement = conexion.prepareStatement(sql)) {

            statement.setInt(1, idUsuarioArrendatario);

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    SolicitudTabla solicitud = new SolicitudTabla(
                            resultSet.getInt("id_solicitud"),
                            resultSet.getString("inmueble"),
                            resultSet.getString("arrendatario"),
                            resultSet.getString("mensaje"),
                            resultSet.getTimestamp("fecha_solicitud").toString(),
                            resultSet.getString("nombre_estado")
                    );

                    solicitudes.add(solicitud);
                }
            }

        } catch (SQLException e) {
            System.out.println("Error al listar solicitudes del arrendatario: " + e.getMessage());
            e.printStackTrace();
        }

        return solicitudes;
    }

    public boolean insertarSolicitudSiDisponible(int idUsuarioArrendatario, int idInmueble, String mensaje) {
        String sql = """
                INSERT INTO solicitud_arrendamiento (
                    fecha_solicitud,
                    mensaje,
                    id_usuario_arrendatario,
                    id_inmueble,
                    id_estado_solicitud
                )
                SELECT CURRENT_TIMESTAMP, ?, ?, ?, 1
                WHERE EXISTS (
                    SELECT 1
                    FROM inmueble
                    WHERE id_inmueble = ?
                      AND id_estado_inmueble = 1
                )
                """;

        try {
            return MultiDatabaseExecutor.ejecutarActualizacion(sql, statement -> {
                if (mensaje != null && !mensaje.isBlank()) {
                    statement.setString(1, mensaje.trim());
                } else {
                    statement.setNull(1, Types.VARCHAR);
                }

                statement.setInt(2, idUsuarioArrendatario);
                statement.setInt(3, idInmueble);
                statement.setInt(4, idInmueble);
            });

        } catch (SQLException e) {
            System.out.println("Error al insertar solicitud validando disponibilidad en las bases sincronizadas: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public int actualizarSolicitudesPendientesVencidas() {
        String sql = """
                UPDATE solicitud_arrendamiento
                SET id_estado_solicitud = (
                    SELECT id_estado_solicitud
                    FROM estado_solicitud
                    WHERE nombre_estado = 'Rechazada'
                ),
                fecha_respuesta = CURRENT_TIMESTAMP
                WHERE id_estado_solicitud = (
                    SELECT id_estado_solicitud
                    FROM estado_solicitud
                    WHERE nombre_estado = 'Pendiente'
                )
                AND fecha_respuesta IS NULL
                AND fecha_solicitud < ?
                """;

        Timestamp fechaLimite = Timestamp.valueOf(LocalDateTime.now().minusDays(7));

        try {
            boolean actualizado = MultiDatabaseExecutor.ejecutarActualizacion(sql, statement -> {
                statement.setTimestamp(1, fechaLimite);
            });

            return actualizado ? 1 : 0;

        } catch (SQLException e) {
            System.out.println("Error al actualizar solicitudes vencidas en las bases sincronizadas: " + e.getMessage());
            e.printStackTrace();
            return 0;
        }
    }
}